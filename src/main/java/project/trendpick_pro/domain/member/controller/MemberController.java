package project.trendpick_pro.domain.member.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import project.trendpick_pro.domain.member.service.MemberService;

@Controller
@RequestMapping("/trendpick/usr")
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;

    @PreAuthorize("isAnonymous()")
    @GetMapping("/register")
    public String showJoin() {
        return "trendpick/usr/member/join";
    }

    @AllArgsConstructor
    @Getter
    public static class JoinForm {
        @NotBlank
        @Size(min = 4, max = 30)
        private final String username;
        @NotBlank
        @Size(min = 4, max = 30)
        private final String password;
    }

    @PreAuthorize("isAnonymous()")
    @PostMapping("/register")
    public String register(@Valid JoinForm joinForm) {
        if ( memberService.findByUsername(joinForm.getUsername()).isPresent() ) {
            return "해당 아이디는 이미 사용중입니다.";
        }

        memberService.register(joinForm.getUsername(), joinForm.getPassword());

        return "회원가입이 완료되었습니다";
    }

    @PreAuthorize("isAnonymous()")
    @GetMapping("/login")
    public String showLogin() {
        return "/trendpick/usr/member/login";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/info")
    public String showMe() {
        return "trendpick/usr/member/info";
    }
}
