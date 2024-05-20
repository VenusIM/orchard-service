package com.orchard.domain.member;


import com.orchard.global.common.ResultResponseDto;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import com.orchard.domain.member.application.MemberManagementService;
import com.orchard.domain.member.domain.persist.Member;
import com.orchard.domain.member.domain.vo.UserEmail;
import com.orchard.domain.member.domain.vo.UserName;
import com.orchard.domain.member.dto.*;
import com.orchard.global.common.TokenDTO;

import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;

@RestController
@Api("회원 관리 API")
@RequestMapping("/api/v1/member")
@RequiredArgsConstructor
public class MemberManagementController {

    private final MemberManagementService memberManagementService;

    @GetMapping("/check/{email}")
    public ResponseEntity<MemberResponseDTO> findByEmail(@PathVariable String email) {
        MemberResponseDTO member = memberManagementService.findOne(UserEmail.from(email));
        return ResponseEntity.ok(member);
    }

    @PostMapping("/join")
    @ApiOperation(value = "회원 가입", notes = "회원 정보를 입력받아 저장한다.")
    @ApiParam(name = "회원 가입 데이터 전달 DTO")
    public ResponseEntity<JoinResponseDTO> create(@Valid @RequestBody JoinRequestDTO request) {
        Member member = request.toEntity();

        URI createdMemberURI = URI.create(String.format("/api/v1/member/%d", member.getId()));
        return ResponseEntity.created(createdMemberURI).body(memberManagementService.create(member));
    }

    @PatchMapping
    @ApiOperation(value = "회원 수정", notes = "회원 수정 정보를 입력 받아 변경한다.")
    public ResponseEntity<TokenDTO> update(
            @ApiParam(name = "변경된 회원 데이터")
            @Valid @RequestBody MemberUpdateDTO updateDTO) {
        Member member = updateDTO.toEntity();

        return ResponseEntity.ok(memberManagementService.update(member, UserEmail.from(getEmail())));
    }

    @GetMapping("/findByEmail")
    @ApiOperation(value = "회원 조회", notes = "회원 정보를 보여주는 API")
    public ResponseEntity<MemberResponseDTO> findByEmail() {
        return ResponseEntity.ok(memberManagementService.findOne(UserEmail.from(getEmail())));
    }

    @DeleteMapping
    @ApiOperation(value = "회원 삭제", notes = "회원 정보를 삭제한다.")
    public ResponseEntity<Void> delete() {
        memberManagementService.delete(UserEmail.from(getEmail()));
        return ResponseEntity.noContent().build();
    }

    // 회원 검색
    @GetMapping("/search/{name}")
    public ResponseEntity<List<FindAllResponse>> searchMember(
            @PathVariable String name,
            @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {
        return ResponseEntity.ok(memberManagementService.searchMember(UserName.from(name), pageable));
    }

    @PatchMapping("/update/name")
    public ResponseEntity<String> updateName(@RequestBody JoinRequestDTO joinRequestDTO) {
        memberManagementService.updateName(joinRequestDTO);
        return ResponseEntity.ok("이름이 수정 되었습니다.");
    }

    @PatchMapping("/update/pw")
    public ResponseEntity<ResultResponseDto> updatePassword(@RequestBody PwUpdateRequestDto pwUpdateRequestDto) {

        return ResponseEntity.ok(memberManagementService.updatePassword(pwUpdateRequestDto));
    }

    @PatchMapping("/update/address")
    public ResponseEntity<String> updateAddress(@RequestBody JoinRequestDTO joinRequestDTO) {
        memberManagementService.updateAddress(joinRequestDTO);
        return ResponseEntity.ok("주소가 수정 되었습니다.");
    }

    @PostMapping("/findId")
    public ResponseEntity<ResultResponseDto> findId(@RequestBody JoinRequestDTO joinRequestDTO) {
        return ResponseEntity.ok(memberManagementService.checkId(joinRequestDTO));
    }

    @PostMapping("/findPw")
    public ResponseEntity<ResultResponseDto> findPw(@RequestBody JoinRequestDTO joinRequestDTO) {
        return ResponseEntity.ok(memberManagementService.checkPw(joinRequestDTO));
    }


    private String getEmail() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }
}
