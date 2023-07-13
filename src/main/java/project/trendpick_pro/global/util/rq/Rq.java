package project.trendpick_pro.global.util.rq;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.context.MessageSource;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.context.annotation.RequestScope;
import org.springframework.web.servlet.LocaleResolver;
import project.trendpick_pro.domain.member.entity.Member;
import project.trendpick_pro.domain.member.entity.RoleType;
import project.trendpick_pro.domain.member.exception.MemberNotFoundException;
import project.trendpick_pro.domain.member.exception.MemberNotMatchException;
import project.trendpick_pro.domain.member.service.MemberService;
import project.trendpick_pro.global.util.rsData.RsData;
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

    public Rq(MemberService memberService, MessageSource messageSource, LocaleResolver localeResolver, HttpServletRequest req,
              HttpServletResponse resp, HttpSession session) {
        this.memberService = memberService;
        this.messageSource = messageSource;
        this.localeResolver = localeResolver;
        this.req = req;
        this.resp = resp;
        this.session = session;

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<Member> member = memberService.findByEmail(username);
        member.ifPresent(memberService::updateRecentlyAccessDate);
    }

    public Boolean checkLogin() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<Member> member = memberService.findByEmail(username);
        return member.isPresent();
    }
    public Member getLogin() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<Member> member = memberService.findByEmail(username);
        if (member.isPresent()) {
            return member.get();
        } else {
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

    public Boolean admin() {
        return getLogin().getRole().equals(RoleType.ADMIN);
    }
    public Member getRollMember(){
        Member member=getLogin();
        if(member.getRole().equals(RoleType.MEMBER)){
            return member;
        } else if(member.getRole().equals(RoleType.BRAND_ADMIN)){
            return member;
        } else if(member.getRole().equals(RoleType.ADMIN)){
            return member;
        }
        throw new MemberNotMatchException("허용된 권한이 아닙니다.");
    }
    public Member getBrandMember() {
        Member member = getLogin();
        if (member.getRole().equals(RoleType.BRAND_ADMIN)) {
            return member;
        } throw new MemberNotMatchException("허용된 권한이 아닙니다.");
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
        } catch (Exception ignored) {}
        return defaultValue;
    }
    public void removeSessionAttr(String name) {
        session.removeAttribute(name);
    }
    public String getCText(String code, String... args) {
        return messageSource.getMessage(code, args, getLocale());
    }
    private Locale getLocale() {
        if (locale == null) {
            locale = localeResolver.resolveLocale(req);
        }
        return locale;
    }
    public String getParamsJsonStr() {
        Map<String, String[]> parameterMap = req.getParameterMap();
        return Ut.json.toStr(parameterMap);
    }

    public String historyBack(String msg) {
        String referer = req.getHeader("referer");
        String key = "historyBackErrorMsg___" + referer;
        req.setAttribute("localStorageKeyAboutHistoryBackErrorMsg", key);
        req.setAttribute("historyBackErrorMsg", msg);
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
}