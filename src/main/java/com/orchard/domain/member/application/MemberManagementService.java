package com.orchard.domain.member.application;

import com.orchard.domain.auth.domain.persist.Message;
import com.orchard.domain.auth.domain.persist.MessageRepository;
import com.orchard.domain.member.domain.vo.UserPassword;
import com.orchard.domain.member.dto.*;
import com.orchard.domain.order.application.OrderService;
import com.orchard.global.common.ResultResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.orchard.domain.member.domain.persist.Member;
import com.orchard.domain.member.domain.persist.MemberRepository;
import com.orchard.domain.member.domain.vo.UserEmail;
import com.orchard.domain.member.domain.vo.UserName;
import com.orchard.domain.member.error.exception.DuplicateEmailException;
import com.orchard.domain.member.error.exception.MemberNotFoundException;
import com.orchard.global.common.TokenDTO;
import com.orchard.global.common.TokenProvider;
import com.orchard.global.error.exception.ErrorCode;
import org.springframework.util.function.SupplierUtils;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class MemberManagementService {

    private final MemberRepository memberRepository;
    private final MessageRepository messageRepository;
    private final OrderService orderService;
    private final PasswordEncoder encoder;
    private final TokenProvider tokenProvider;
    private final AuthenticationManagerBuilder managerBuilder;

    // 회원 가입
    public JoinResponseDTO create(final Member member) {
        member.encode(encoder);

        if (memberRepository.existsByEmail(member.getEmail())) {
            throw new DuplicateEmailException(ErrorCode.EMAIL_DUPLICATION);
        }

        return JoinResponseDTO.from(memberRepository.save(member));
    }

    // 업데이트
    public TokenDTO update(final Member member, final UserEmail email) {
        Member findMember = memberRepository.findByEmail(email).orElseThrow(
                () -> new MemberNotFoundException(ErrorCode.MEMBER_NOT_FOUND));

        Member updatedMember = findMember.update(member, encoder);

        // 새로운 email을 context 안에 저장
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken
                (updatedMember.getEmail().userEmail(), member.getPassword().userPassword());

        Authentication authenticate = managerBuilder.getObject().authenticate(token);
        SecurityContextHolder.getContext().setAuthentication(authenticate);

        return tokenProvider.createToken(updatedMember.getEmail().userEmail(), authenticate);
    }

    // 회원 삭제
    public void delete(final UserEmail email) {
        memberRepository.deleteByEmail(email);
    }

    // 회원 조회
    @Transactional(readOnly = true)
    public MemberResponseDTO findOne(final UserEmail email) {
        log.debug("userEmail : {}", email.userEmail());

        Member findMember = memberRepository.findByEmail(email).orElseThrow(() -> new MemberNotFoundException(ErrorCode.MEMBER_NOT_FOUND));

        return MemberResponseDTO.create(findMember);
    }

    // 전체 회원 조회
    @Transactional(readOnly = true)
    public List<FindAllResponse> findAll(final UserEmail email, final Pageable pageable) {
        Member member = memberRepository.findByEmail(email).orElseThrow(() ->
            new MemberNotFoundException(ErrorCode.MEMBER_NOT_FOUND));

        log.info(member.toString());
        return memberRepository.findAll(pageable).stream()
                .map(FindAllResponse::of)
                .collect(Collectors.toList());
    }

    // 회원 검색
    public List<FindAllResponse> searchMember(final UserName name, final Pageable pageable) {
        List<Member> members = memberRepository.findByName(name, pageable);

        return members.stream()
                .map(FindAllResponse::of)
                .collect(Collectors.toList());
    }

    public Member updateName(JoinRequestDTO joinRequestDTO) {
        Member member = getMember();
        member.changeName(joinRequestDTO.toEntity().getName());
        return memberRepository.save(member);
    }

    public Member updateAddress(JoinRequestDTO joinRequestDTO) {
        Member member = getMember();
        Member request = joinRequestDTO.toEntity();
        member.changePostCode(request.getPostCode());
        member.changeAddress(request.getAddress());
        return memberRepository.save(member);
    }

    private Member getMember() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        return memberRepository.findByEmail(UserEmail.from(email)).orElseThrow(() ->
                new MemberNotFoundException(ErrorCode.MEMBER_NOT_FOUND));
    }

    @Transactional(rollbackFor = Exception.class)
    public ResultResponseDto checkPw(JoinRequestDTO joinRequestDTO) {
        Member request = joinRequestDTO.toEntity();
        Member member = memberRepository.findByEmail(request.getEmail()).orElse(null);
        ResultResponseDto resultResponseDto = new ResultResponseDto();
        if(member == null) {
            resultResponseDto.setCode("400");
            resultResponseDto.setMsg("존재하지 않는 회원입니다.");
        } else if (!member.getPhoneNumber().userPhoneNumber().equals(request.getPhoneNumber().userPhoneNumber())) {
            resultResponseDto.setCode("400");
            resultResponseDto.setMsg("존재하지 않는 회원입니다.");
        } else {
            Optional<Message> messageOptional = messageRepository.findByPhoneNumber(member.getPhoneNumber());
            if(messageOptional.isPresent()) {
                Message message = messageOptional.get();
                if(message.getCount() > 5) {
                    resultResponseDto.setMsg("발송 횟수 5회 초과하였습니다. 관리자에게 문의하세요.");
                    resultResponseDto.setCode("401");
                    return resultResponseDto;
                }
                message.increaseCount();
                messageRepository.save(message);
            } else {
                messageRepository.save(Message.of(request.getPhoneNumber(), 1, ""));
            }

            SecureRandom secureRandom = new SecureRandom();
            String charNSpecialChar = IntStream.concat(
                            IntStream.rangeClosed(33, 47),
                            IntStream.rangeClosed(58, 126))
                    .mapToObj(i -> String.valueOf((char) i))
                    .collect(Collectors.joining());

            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < 10; i++) {
                builder.append(charNSpecialChar.charAt(secureRandom.nextInt(charNSpecialChar.length())));
            }

            member.changePassword(UserPassword.from(builder.toString()));
            member.encode(encoder);
            memberRepository.save(member);

            String message = "[킹체리] 임시 비밀번호 입니다.\n" + builder;
            orderService.sendMessage(message, member.getPhoneNumber());
            resultResponseDto.setCode("200");
            resultResponseDto.setMsg("휴대폰으로 임시 비밀번호 발송 되었습니다.");
        }

        return resultResponseDto;
    }

    public ResultResponseDto checkId(JoinRequestDTO joinRequestDTO) {
        Member request = joinRequestDTO.toEntity();
        List<Member> members = memberRepository.findAllByPhoneNumber(request.getPhoneNumber())
                .orElseGet(ArrayList::new);
        ResultResponseDto resultResponseDto = new ResultResponseDto();
        if(members.isEmpty()) {
            resultResponseDto.setCode("400");
            resultResponseDto.setMsg("존재하지 않는 회원입니다.");
        } else {
            Optional<Message> messageOptional = messageRepository.findByPhoneNumber(members.get(0).getPhoneNumber());
            if(messageOptional.isPresent()) {
                Message message = messageOptional.get();
                if(message.getCount() > 5) {
                    resultResponseDto.setMsg("발송 횟수 5회 초과하였습니다. 관리자에게 문의하세요.");
                    resultResponseDto.setCode("401");
                    return resultResponseDto;
                }
                message.increaseCount();
                messageRepository.save(message);
            } else {
                messageRepository.save(Message.of(request.getPhoneNumber(), 1, ""));
            }

            StringBuilder message = new StringBuilder("[킹체리] 아이디 목록입니다.");
            for(Member member : members) {
                message.append("\n").append(member.getEmail().userEmail());
            }
            orderService.sendMessage(message.toString(), members.get(0).getPhoneNumber());
            resultResponseDto.setCode("200");
            resultResponseDto.setMsg("휴대폰으로 아이디가 발송 되었습니다.");
        }

        return resultResponseDto;
    }

    public ResultResponseDto updatePassword(PwUpdateRequestDto pwUpdateRequestDto) {
        Member member = getMember();
        ResultResponseDto resultResponseDto = new ResultResponseDto();

        if(!encoder.matches(pwUpdateRequestDto.getCurrent(), member.getPassword().userPassword())) {
            resultResponseDto.setCode("401");
            resultResponseDto.setMsg("현재 비밀번호가 일치하지 않습니다.");
        } else {
            member.changePassword(UserPassword.from(pwUpdateRequestDto.getAfter()));
            member.encode(encoder);
            memberRepository.save(member);
            resultResponseDto.setCode("200");
            resultResponseDto.setMsg("비밀번호가 변경되었습니다. 다시 로그인해 주세요.");
        }

        return resultResponseDto;
    }
}
