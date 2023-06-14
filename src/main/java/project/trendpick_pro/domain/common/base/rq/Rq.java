package project.trendpick_pro.domain.common.base.rq;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;
import org.springframework.web.servlet.LocaleResolver;
import project.trendpick_pro.domain.member.entity.Member;
import project.trendpick_pro.domain.member.entity.RoleType;
import project.trendpick_pro.domain.member.entity.form.JoinForm;
import project.trendpick_pro.domain.member.exception.MemberNotFoundException;
import project.trendpick_pro.domain.member.exception.MemberNotMatchException;
import project.trendpick_pro.domain.member.service.MemberService;
import project.trendpick_pro.global.rsData.RsData;
import project.trendpick_pro.global.util.Ut;

import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

@Component
@RequestScope
public class Rq {
    private final MemberService memberService;
    private final MessageSource messageSource;
    private final LocaleResolver localeResolver;
    private Locale locale;
    private final HttpServletRequest req;
    private final HttpServletResponse resp;
    private final HttpSession session;
    private final User user;
    private Member member = null; // 레이지 로딩, 처음부터 넣지 않고, 요청이 들어올 때 넣는다.

    public Rq(MemberService memberService, MessageSource messageSource, LocaleResolver localeResolver, HttpServletRequest req,
              HttpServletResponse resp, HttpSession session) {
        this.memberService = memberService;
        this.messageSource = messageSource;
        this.localeResolver = localeResolver;
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

    public String historyBack(String msg) {
        String referer = req.getHeader("referer");
        String key = "historyBackErrorMsg___" + referer;
        req.setAttribute("localStorageKeyAboutHistoryBackErrorMsg", key);
        req.setAttribute("historyBackErrorMsg", msg);
        // 200 이 아니라 400 으로 응답코드가 지정되도록
        resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        return "trendpick/common/js";
    }

    // 뒤로가기 + 메세지
    public String historyBack(RsData rsData) {
        return historyBack(rsData.getMsg());
    }

    // 302 + 메세지
    public String redirectWithMsg(String url, RsData rsData) {
        return redirectWithMsg(url, rsData.getMsg());
    }

    // 302 + 메세지
    public String redirectWithMsg(String url, String msg) {
        return "redirect:" + urlWithMsg(url, msg);
    }

    private String urlWithMsg(String url, String msg) {
        // 기존 URL에 혹시 msg 파라미터가 있다면 그것을 지우고 새로 넣는다.
        return Ut.url.modifyQueryParam(url, "msg", msgWithTtl(msg));
    }

    private String msgWithTtl(String msg) {
        return Ut.url.encode(msg) + ";ttl=" + new Date().getTime();
    }

    public boolean isLogin() {
        return user != null;
    }

    // 로그아웃 되어 있는지 체크
    public boolean isLogout() {
        return !isLogin();
    }

    public Member getMember() {
        if (isLogout()) return null;

        // 데이터가 없는지 체크
        if (member == null) {
            member = memberService.findByUsername(user.getUsername()).orElseThrow();
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

    public Boolean CheckAdminHtml() {
        return !CheckMemberHtml();
    }

    public Boolean checkAdminOrBrandAdminHtml(){
        if(!checkLogin()) //로그인 안되있으면 false
            return false;
        if(CheckLogin().get().getRole().equals(RoleType.MEMBER)) //Member면 false
            return false;
        return true;
    }

    public Boolean CheckBrandAdminHtml() {
        Member checkMember = CheckLogin().get();
        return checkMember.getRole().equals(RoleType.BRAND_ADMIN);
    }

    public String getBrandName(){
        return CheckLogin().get().getBrand();
    }

    public Boolean CheckMemberHtml() {
        Member checkMember = CheckLogin().get();
        return checkMember.getRole().equals(RoleType.MEMBER);
    }

    public Optional<Member> CheckMember() {
        Optional<Member> member = CheckLogin();
        Member checkMember = member.get();
        if (checkMember.getRole().equals(RoleType.MEMBER)) {
            return member;
        }
        throw new MemberNotMatchException("허용된 권한이 아닙니다.");
    }

    public Member GetMember() {
        return  CheckLogin().orElse(null);
    }

    public Boolean CheckLoginHtml() {

        String username = SecurityContextHolder.getContext().getAuthentication().getName(); // 둘다 테스트 해보기
        Optional<Member> member = memberService.findByEmail(username);

        return member.isPresent();
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

    public boolean checkLogin(){
        String username = SecurityContextHolder.getContext().getAuthentication().getName(); // 둘다 테스트 해보기
        Optional<Member> member = memberService.findByEmail(username);

        if (member.isPresent()) {
            return true;
        }
        return false;
    }

    public void setSessionAttr(String name, String value) {
        session.setAttribute(name, value);
    }

    public <T> T getSessionAttr(String name, T defaultValue) {
        try {
            return (T) session.getAttribute(name);
        } catch (Exception ignored) {
        }

        return defaultValue;
    }

    public void removeSessionAttr(String name) {
        session.removeAttribute(name);
    }

    public String getCText(String code, String... args) {
        return messageSource.getMessage(code, args, getLocale());
    }

    private Locale getLocale() {
        if (locale == null) locale = localeResolver.resolveLocale(req);

        return locale;
    }

    public String getParamsJsonStr() {
        Map<String, String[]> parameterMap = req.getParameterMap();

        return Ut.json.toStr(parameterMap);
    }
}