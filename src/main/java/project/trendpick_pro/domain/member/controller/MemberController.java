package project.trendpick_pro.domain.member.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import project.trendpick_pro.global.basedata.tagname.service.impl.TagNameServiceImpl;
import project.trendpick_pro.global.util.rq.Rq;
import project.trendpick_pro.domain.member.entity.Member;
import project.trendpick_pro.domain.member.entity.dto.response.MemberInfoResponse;
import project.trendpick_pro.domain.member.entity.form.AddressForm;
import project.trendpick_pro.domain.member.entity.form.JoinForm;
import project.trendpick_pro.domain.member.service.MemberService;
import project.trendpick_pro.domain.recommend.service.RecommendService;
import project.trendpick_pro.global.util.rsData.RsData;

@Controller
@RequiredArgsConstructor
@RequestMapping("/trendpick/member")
public class MemberController {

    private final MemberService memberService;
    private final TagNameServiceImpl tagNameServiceImpl;
    private final RecommendService recommendService;

    private final Rq rq;

    @PreAuthorize("isAnonymous()")
    @GetMapping("/register")
    public String join(JoinForm joinForm, Model model) {
        model.addAttribute("joinForm", joinForm);
        model.addAttribute("allTags", tagNameServiceImpl.findAll());
        return "trendpick/usr/member/join";
    }

    @PreAuthorize("isAnonymous()")
    @PostMapping("/register")
    public String join(@ModelAttribute @Valid JoinForm joinForm) {
        RsData<Member> member = memberService.join(joinForm);
        if (member.isFail())
            return rq.historyBack(member);
        if(member.getData().getRole().getValue().equals("MEMBER"))
            recommendService.rankRecommend(member.getData());
        return rq.redirectWithMsg("/trendpick/member/login", member);
    }

    @PreAuthorize("isAnonymous()")
    @GetMapping("/login")
    public String login() {
        return "trendpick/usr/member/login";
    }

    @PreAuthorize("hasAuthority({'MEMBER'})")
    @GetMapping("/info")
    public String myInfo(Model model) {
        model.addAttribute("MemberInfo", MemberInfoResponse.of(rq.getLogin()));
        model.addAttribute("myTags", rq.getMember().getTags());
        return "trendpick/usr/member/info";
    }

    @PreAuthorize("hasAuthority({'MEMBER'})")
    @GetMapping("/edit/address")
    public String modifyAddress(Model model) {
        model.addAttribute("originalAdrress", rq.getMember().getAddress());
        return "trendpick/usr/member/address";
    }

    @PreAuthorize("hasAuthority({'MEMBER'})")
    @PostMapping("/edit/address")
    public String modifyAddress(@ModelAttribute @Valid AddressForm addressForm, Model model) {
        RsData<Member> member = memberService.modifyAddress(rq.getLogin(), addressForm.address());
        model.addAttribute("MemberInfo", MemberInfoResponse.of(member.getData()));
        return rq.redirectWithMsg("/trendpick/member/info", member);
    }
}
