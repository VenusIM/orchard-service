package com.orchard.domain.member;

import com.orchard.domain.member.application.MemberManagementService;
import com.orchard.domain.member.dto.JoinRequestDTO;
import com.orchard.domain.member.dto.JoinResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Controller
@RequestMapping("/member")
public class MemberController {

    private final MemberManagementService memberManagementService;
    public MemberController(MemberManagementService memberManagementService) {
        this.memberManagementService = memberManagementService;
    }

    @GetMapping("/register")
    public String register() {
        return "member/register";
    }

    @PostMapping("/register")
    public String register(JoinRequestDTO joinRequestDTO, Model model) {
        log.info(joinRequestDTO.toString());
        JoinResponseDTO joinResponseDTO = memberManagementService.create(joinRequestDTO.toEntity());
        model.addAttribute("user", joinResponseDTO);
        return "/member/register-complete";
    }

    @GetMapping("/find/{id}")
    public String find(@PathVariable String id) {
        return "member/find";
    }

}
