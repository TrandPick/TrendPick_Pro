package project.trendpick_pro.domain.common.base.rq;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;
import project.trendpick_pro.domain.member.entity.Member;
import project.trendpick_pro.domain.member.entity.RoleType;
import project.trendpick_pro.domain.member.exception.MemberNotFoundException;
import project.trendpick_pro.domain.member.exception.MemberNotMatchException;
import project.trendpick_pro.domain.member.service.MemberService;

import java.util.Optional;

@Component
@RequestScope
public class Rq {
    private final MemberService memberService;
    private final HttpServletRequest req;
    private final HttpServletResponse resp;
    private final HttpSession session;
    private final User user;
    private Member member = null; // 레이지 로딩, 처음부터 넣지 않고, 요청이 들어올 때 넣는다.

    public Rq(MemberService memberService, HttpServletRequest req, HttpServletResponse resp, HttpSession session) {
        this.memberService = memberService;
        this.req = req;
        this.resp = resp;
        this.session = session;

        // 현재 로그인한 회원의 인증정보를 가져옴
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication.getPrincipal() instanceof User) {
            this.user = (User) authentication.getPrincipal();
        } else {
            this.user = null;
        }
    }

    // 로그인 된 회원의 객체
    public Member getMember() {

        // 데이터가 없는지 체크
        if (member == null) {
            member = memberService.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName())
                            .orElseThrow(() -> new MemberNotFoundException("존재하지 않는 회원입니다."));
        }
        return member;
    }

    public Optional<Member> CheckAdmin() {
        Optional<Member> member = CheckLogin();
        Member checkMember = member.get();
        if (checkMember.getRole().equals(RoleType.MEMBER)) {
            throw new MemberNotMatchException("허용된 권한이 아닙니다.");
        }
        return member;
    }

    public Optional<Member> CheckMember() {
        Optional<Member> member = CheckLogin();
        Member checkMember = member.get();
        if (checkMember.getRole().equals(RoleType.MEMBER)) {
            return member;

        }
        throw new MemberNotMatchException("허용된 권한이 아닙니다.");
    }

    public Optional<Member> CheckLogin() {

        String username = SecurityContextHolder.getContext().getAuthentication().getName(); // 둘다 테스트 해보기
        Optional<Member> member = memberService.findByEmail(username);

        if (member.isPresent()) {
            return member;
        }
        else {
            throw new MemberNotFoundException("존재하지 않는 회원입니다.");
        }
    }
}