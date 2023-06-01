package project.trendpick_pro.domain.member.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.trendpick_pro.domain.member.entity.Member;
import project.trendpick_pro.domain.member.entity.RoleType;
import project.trendpick_pro.domain.member.entity.form.JoinForm;
import project.trendpick_pro.domain.member.exception.MemberAlreadyExistException;
import project.trendpick_pro.domain.member.exception.MemberNotFoundException;
import project.trendpick_pro.domain.member.repository.MemberRepository;
import project.trendpick_pro.domain.tag.entity.Tag;
import project.trendpick_pro.domain.tag.entity.dto.request.TagRequest;
import project.trendpick_pro.domain.tag.repository.TagRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final PasswordEncoder passwordEncoder;

    private final MemberRepository memberRepository;
    private final TagRepository tagRepository;

    public Optional<Member> findByUsername(String username) {
        return memberRepository.findByUsername(username);
    }

    @Transactional
    public void register(JoinForm joinForm) {

        if (memberRepository.findByUsername(joinForm.emailtext()).isPresent()) {
            throw new MemberAlreadyExistException("이미 존재하는 이름입니다.");
        }

        RoleType roleType;
        if (Objects.equals(joinForm.state(), RoleType.ADMIN.getValue())) {
            roleType = RoleType.ADMIN;
        } else if (joinForm.state().equals(RoleType.BRAND_ADMIN.getValue())) {
            roleType = RoleType.BRAND_ADMIN;
        } else {
            roleType = RoleType.MEMBER;
        }

        List<Tag> tags = new ArrayList<>();
        for (String tag : joinForm.tags()) {
            tagRepository.findByName(tag).ifPresent(tags::add);
        }

        Member member = Member
                .builder()
                .email(joinForm.emailtext())
                .password(passwordEncoder.encode(joinForm.password()))
                .username(joinForm.username())
                .phoneNumber(joinForm.phoneNumber())
                .role(roleType)
                .tags(tags)
                .build();

        memberRepository.save(member);
    }

    @Transactional
    public void manageTag(String username, TagRequest tagRequest){
        Member member = memberRepository.findByUsername(username).orElseThrow(() -> new MemberNotFoundException("존재하지 않는 회원입니다."));

        List<Tag> tags = new ArrayList<>();
        for (String s : tagRequest.getTags()) {
            tagRepository.findByName(s).ifPresent(tags::add);
        }
        member.changeTags(tags);
    }

    @Transactional
    public void manageAddress(Member actor, String address){
        Member member = memberRepository.findByUsername(actor.getUsername()).orElseThrow();
        member.connectAddress(address);
    }

    public void manageAccount(Member actor, String bank_name, String account){
        Member member = memberRepository.findByUsername(actor.getUsername()).orElseThrow();
        member.connectBank(bank_name, account);
    }
}