package project.trendpick_pro.global.jmeter;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import project.trendpick_pro.domain.member.entity.Member;
import project.trendpick_pro.domain.member.entity.dto.MemberInfoDto;
import project.trendpick_pro.domain.member.repository.MemberRepository;

@RestController
@RequiredArgsConstructor
@RequestMapping("/jmeter")
public class JmeterController {

    private final MemberRepository memberRepository;

    @GetMapping("/member/info")
    public MemberInfoDto getMemberInfo(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("memberId") == null) {
            throw new RuntimeException("세션이 없거나 로그인되어 있지 않습니다.");
        }

        Long memberId = (Long) session.getAttribute("memberId");
        // memberId를 사용하여 회원 정보를 조회하고 MemberInfoDto 객체를 생성하여 반환합니다.
        Member member = memberRepository.findById(memberId).get();
        return MemberInfoDto.of(member);
    }
}
