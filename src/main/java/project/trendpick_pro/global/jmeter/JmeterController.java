package project.trendpick_pro.global.jmeter;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<MemberInfoDto> getMemberInfo(HttpServletRequest request) {
        Member member = rq.CheckMember().get();
        MemberInfoDto memberInfoDto = MemberInfoDto.of(member);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);

        return new ResponseEntity<>(memberInfoDto, headers, HttpStatus.OK);
    }
}
