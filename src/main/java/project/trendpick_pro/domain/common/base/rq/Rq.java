package project.trendpick_pro.domain.common.base.rq;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.context.MessageSource;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.context.annotation.RequestScope;
import org.springframework.web.servlet.LocaleResolver;
import project.trendpick_pro.domain.member.entity.Member;
import project.trendpick_pro.domain.member.entity.RoleType;
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

        String username = SecurityContextHolder.getContext().getAuthentication().getName(); // 둘다 테스트 해보기
        Optional<Member> member = memberService.findByEmail(username);
        if(member.isPresent())
            memberService.updateRecentlyAccessDate(member.get());

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

    public String historyBack(RsData rsData) {
        return historyBack(rsData.getMsg());
    }
    public String redirectWithMsg(String url, RsData rsData) {
        return redirectWithMsg(url, rsData.getMsg());
    }
    public String redirectWithMsg(String url, String msg) {
        return "redirect:" + urlWithMsg(url, msg);
    }
    private String urlWithMsg(String url, String msg) {
        // 기존 URL에 혹시 msg 파라미터가 있다면 그것을 지우고 새로 넣는다.
        return Ut.url.modifyQueryParam(url, "msg", msgWithTtl(msg));
    }

    private String getCurrentUrl() {
        String url = req.getRequestURI();
        String queryStr = req.getQueryString();

        if (StringUtils.hasText(queryStr)) {
            url += "?" + queryStr;
        }

        return url;
    }
    public String modifyQueryParam(String paramName, String paramValue) {
        return Ut.url.modifyQueryParam(getCurrentUrl(), paramName, paramValue);
    }

    private String msgWithTtl(String msg) {
        return Ut.url.encode(msg) + ";ttl=" + new Date().getTime();
    }

    public boolean isLogin() {
        return user != null;
    }
    public boolean isLogout() {
        return !isLogin();
    }
    public Member GetMember() {
        if (isLogout()) return null;

        // 데이터가 없는지 체크
        if (member == null) {
            member = memberService.findByUsername(user.getUsername()).orElseThrow(() -> new MemberNotFoundException("존재 하지 않는 회원입니다."));
        }
        return member;
    }

    public Boolean checkLogin() {

        String username = SecurityContextHolder.getContext().getAuthentication().getName(); // 둘다 테스트 해보기
        Optional<Member> member = memberService.findByEmail(username);

        return member.isPresent();
    }
    public Member getLogin() {

        String username = SecurityContextHolder.getContext().getAuthentication().getName(); // 둘다 테스트 해보기
        Optional<Member> member = memberService.findByEmail(username);

        if (member.isPresent()) {
            return member.get();
        }
        else {
            throw new MemberNotFoundException("존재하지 않는 회원입니다.");
        }
    }

    public Boolean checkMember() {
        return getLogin().getRole().equals(RoleType.MEMBER);
    }
    public Member getMember() {
        Member member = getLogin();
        if (member.getRole().equals(RoleType.MEMBER)) {
            return member;
        }
        throw new MemberNotMatchException("허용된 권한이 아닙니다.");
    }

    public Member getBrandMember() {
        Member member = getLogin();
        if (member.getRole().equals(RoleType.BRAND_ADMIN)) {
            return member;
        }
        throw new MemberNotMatchException("허용된 권한이 아닙니다.");
    }
    public Boolean checkAdmin() {
        return !checkMember();
    }

    public Member getAdmin() {
        Member checkMember = getLogin();
        if (checkMember.getRole().equals(RoleType.MEMBER)) {
            throw new MemberNotMatchException("허용된 권한이 아닙니다.");
        }
        return checkMember;
    }

    public Boolean checkAdminOrBrand(){
        if(!checkLogin()) {
            return false;
        } else {
            return !getLogin().getRole().equals(RoleType.MEMBER);
        }
    }

    public Boolean checkBrand() {
        return getLogin().getRole().equals(RoleType.BRAND_ADMIN);
    }

    public String getBrandName(){
        return getLogin().getBrand();
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