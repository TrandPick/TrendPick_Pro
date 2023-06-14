package project.trendpick_pro.global.jmeter;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import project.trendpick_pro.domain.common.base.rq.Rq;
import project.trendpick_pro.domain.member.entity.Member;
import project.trendpick_pro.domain.member.entity.dto.MemberInfoDto;
import project.trendpick_pro.domain.member.repository.MemberRepository;

@RestController
@RequiredArgsConstructor
@RequestMapping("/jmeter")
public class JmeterController {
    private final Rq rq;

    private final MemberRepository memberRepository;

    @GetMapping("/member/login")
    public MemberInfoDto getMemberInfo(HttpServletRequest request) {
        Member member = rq.CheckMember().get();
        return MemberInfoDto.of(member);
    }


}
