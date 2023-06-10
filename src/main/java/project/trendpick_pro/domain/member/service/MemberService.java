package project.trendpick_pro.domain.member.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.trendpick_pro.domain.brand.service.BrandService;
import project.trendpick_pro.domain.member.entity.Member;
import project.trendpick_pro.domain.member.entity.RoleType;
import project.trendpick_pro.domain.member.entity.form.JoinForm;
import project.trendpick_pro.domain.member.exception.MemberAlreadyExistException;
import project.trendpick_pro.domain.member.exception.MemberNotFoundException;
import project.trendpick_pro.domain.member.repository.MemberRepository;
import project.trendpick_pro.domain.tags.favoritetag.entity.FavoriteTag;
import project.trendpick_pro.domain.tags.tag.entity.dto.request.TagRequest;
import project.trendpick_pro.global.rsData.RsData;

import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final PasswordEncoder passwordEncoder;
    private final MemberRepository memberRepository;

    private final BrandService brandService;

    public Optional<Member> findByUsername(String username) {
        return memberRepository.findByUsername(username);
    }

    public Optional<Member> findByEmail(String username){
        return memberRepository.findByEmail(username);
    }

    public Member findByMember(Long id){
        return memberRepository.findById(id).get();
    }

    @Transactional
    public RsData<Member> register(JoinForm joinForm) {

        if (memberRepository.findByEmail(joinForm.email()).isPresent()) {
            return RsData.of("F-1", "해당 아이디(%s)는 이미 사용중입니다.".formatted(joinForm.email()));
        }

        String brand = "";
        RoleType roleType;
        if (Objects.equals(joinForm.state(), RoleType.ADMIN.getValue())) {
            roleType = RoleType.ADMIN;
        } else if (joinForm.state().equals(RoleType.BRAND_ADMIN.getValue())) {
            roleType = RoleType.BRAND_ADMIN;
            brand = joinForm.brand();
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
        member.connectBrand(brand);

        if(brand.length() != 0)
            brandService.save(brand);

        if (joinForm.tags() != null) {
            Set<FavoriteTag> favoriteTags = new LinkedHashSet<>();
            for (String tag : joinForm.tags()) {
                FavoriteTag favoriteTag = new FavoriteTag(tag);
                favoriteTags.add(favoriteTag);
            }
            member.changeTags(favoriteTags);
        }

        memberRepository.save(member);

        return RsData.of("S-1", "회원가입이 완료되었습니다.", member);
    }

    @Transactional
    public void manageTag(String username, TagRequest tagRequest){

        Member member = memberRepository.findByUsername(username).orElseThrow(() -> new MemberNotFoundException("존재하지 않는 회원입니다."));

        Set<FavoriteTag> tags = new LinkedHashSet<>();
        for (String tag : tagRequest.getTags()) {
            FavoriteTag favoriteTag = new FavoriteTag(tag);
            tags.add(favoriteTag);
        }
        member.changeTags(tags);
    }

    @Transactional
    public RsData<Member> manageAddress(Member member, String address){
        member.connectAddress(address);
        return RsData.of("S-1", "주소 수정이 완료되었습니다.", member);
    }

    @Transactional
    public RsData<Member> manageAccount(Member member, String bank_name, String account){
        member.connectBank(bank_name, account);
        return RsData.of("S-1", "계좌 수정이 완료되었습니다.", member);
    }
}