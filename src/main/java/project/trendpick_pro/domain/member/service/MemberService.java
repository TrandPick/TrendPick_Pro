package project.trendpick_pro.domain.member.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.trendpick_pro.domain.tags.favoritetag.entity.FavoriteTag;
import project.trendpick_pro.domain.member.entity.Member;
import project.trendpick_pro.domain.member.entity.RoleType;
import project.trendpick_pro.domain.member.entity.form.JoinForm;
import project.trendpick_pro.domain.member.exception.MemberAlreadyExistException;
import project.trendpick_pro.domain.member.exception.MemberNotFoundException;
import project.trendpick_pro.domain.member.repository.MemberRepository;
import project.trendpick_pro.domain.tags.tag.entity.dto.request.TagRequest;
import project.trendpick_pro.domain.tags.tag.repository.TagRepository;

import java.util.*;

@Slf4j
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

    public Member findByEmail(String username){
        return memberRepository.findByEmail(username)
                .orElseThrow(() -> new MemberNotFoundException("존재하지 않는 회원입니다."));
    }

    @Transactional
    public void register(JoinForm joinForm) {

        if (memberRepository.findByEmail(joinForm.email()).isPresent()) {
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

        Member member = Member
                .builder()
                .email(joinForm.email())
                .password(passwordEncoder.encode(joinForm.password()))
                .username(joinForm.username())
                .phoneNumber(joinForm.phoneNumber())
                .role(roleType)
                .build();

        if (!joinForm.tags().isEmpty()) {
            Set<FavoriteTag> favoriteTags = new LinkedHashSet<>();
            for (String tag : joinForm.tags()) {
//            Tag findTag = tagRepository.findByName(tag).orElseThrow();
//            favoriteTag.connectMember(member);
                FavoriteTag favoriteTag = new FavoriteTag(tag);
                favoriteTags.add(favoriteTag);
            }
            member.changeTags(favoriteTags);
        }

        memberRepository.save(member);
    }

    @Transactional
    public void manageTag(String username, TagRequest tagRequest){

        Member member = memberRepository.findByUsername(username).orElseThrow(() -> new MemberNotFoundException("존재하지 않는 회원입니다."));

        Set<FavoriteTag> tags = new LinkedHashSet<>();
        for (String tag : tagRequest.getTags()) {
            //기존에 선택했던 태그들에 대한 가중치를 마이너스 (만약 0점 이하로 떨어지면 삭제)
//            Tag tag = tagRepository.findByName(s).orElseThrow();
//            favoriteTag.connectMember(member);
            FavoriteTag favoriteTag = new FavoriteTag(tag);
            tags.add(favoriteTag);
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