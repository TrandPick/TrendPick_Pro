package project.trendpick_pro.domain.member.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.trendpick_pro.domain.member.entity.Member;
import project.trendpick_pro.domain.member.repository.MemberRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {
    private final PasswordEncoder passwordEncoder;

    private final MemberRepository memberRepository;

    public Optional<Member> findByUsername(String username) {
        return memberRepository.findByUsername(username);
    }

    @Transactional
    public Member register(String username, String password) {

        Member member = Member
                .builder()
                .username(username)
                .password(passwordEncoder.encode(password))
                .build();

        return memberRepository.save(member);
    }
    @Transactional

    public void manageAddress(Member actor, String address){
        Member member = memberRepository.findByUsername(actor.getUsername()).orElseThrow();
        member.setAddress(address);
    }

    public void manageAccount(Member actor, String bank_name, Long account){
        Member member = memberRepository.findByUsername(actor.getUsername()).orElseThrow();
        member.setBank_name(bank_name);
        member.setAccount(account);
    }
}