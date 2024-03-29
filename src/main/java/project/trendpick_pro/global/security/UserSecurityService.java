package project.trendpick_pro.global.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import project.trendpick_pro.domain.member.entity.Member;
import project.trendpick_pro.domain.member.entity.MemberRoleType;
import project.trendpick_pro.domain.member.exception.MemberNotFoundException;
import project.trendpick_pro.domain.member.repository.MemberRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service("userDetailsService")
@RequiredArgsConstructor
public class UserSecurityService implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws MemberNotFoundException {
        Optional<Member> member = this.memberRepository.findByEmail(username);

        if (member.isEmpty()) {
            throw new MemberNotFoundException("사용자를 찾을수 없습니다.");
        }
        Member siteUser = member.get();
        MemberRoleType role = member.get().getRole();
        List<GrantedAuthority> authorities = new ArrayList<>();
        if (role.equals(MemberRoleType.ADMIN)) {
            authorities.add(new SimpleGrantedAuthority(MemberRoleType.ADMIN.getValue()));
        } else if (role.equals(MemberRoleType.BRAND_ADMIN)) {
            authorities.add(new SimpleGrantedAuthority(MemberRoleType.BRAND_ADMIN.getValue()));
        } else {
            authorities.add(new SimpleGrantedAuthority(MemberRoleType.MEMBER.getValue()));
        }
        return new User(siteUser.getEmail(), siteUser.getPassword(), authorities);
    }
}