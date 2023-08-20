package project.trendpick_pro.domain.member.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.trendpick_pro.domain.brand.service.BrandService;
import project.trendpick_pro.domain.member.entity.Member;
import project.trendpick_pro.domain.member.entity.MemberRoleType;
import project.trendpick_pro.domain.member.entity.dto.response.MemberInfoResponse;
import project.trendpick_pro.domain.member.entity.form.JoinForm;
import project.trendpick_pro.domain.member.exception.MemberNotFoundException;
import project.trendpick_pro.domain.member.repository.MemberRepository;
import project.trendpick_pro.domain.member.service.MemberService;
import project.trendpick_pro.domain.recommend.service.RecommendService;
import project.trendpick_pro.domain.store.entity.Store;
import project.trendpick_pro.domain.store.service.StoreService;
import project.trendpick_pro.domain.tags.favoritetag.entity.FavoriteTag;
import project.trendpick_pro.domain.tags.tag.entity.dto.request.TagRequest;
import project.trendpick_pro.domain.withdraw.entity.WithdrawApply;
import project.trendpick_pro.global.util.rsData.RsData;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;

    private final PasswordEncoder passwordEncoder;
    private final RecommendService recommendService;
    private final StoreService storeService;
    private final BrandService brandService;

    @Transactional
    @Override
    public RsData<Long> join(JoinForm joinForm) {
        if (isEmailSave(joinForm.email())) {
            return RsData.of("F-1", "해당 아이디(%s)는 이미 사용중 입니다.".formatted(joinForm.email()));
        }
        Member member = settingMember(joinForm);
        Member savedMember = memberRepository.save(member);
        checkingMemberType(member);
        return RsData.of("S-1", "회원가입이 완료 되었습니다.", member.getId());
    }

    @Transactional
    @Override
    public RsData<List<Member>> joinAll(List<JoinForm> joinForms) {
        List<Member> members = new ArrayList<>();
        for (JoinForm joinForm : joinForms) {
            if (isEmailSave(joinForm.email())) {
                return RsData.of("F-1", "해당 아이디(%s)는 이미 사용중 입니다.".formatted(joinForm.email()));
            }
            members.add(settingMember(joinForm));
        }
        memberRepository.saveAll(members);
        return RsData.of("S-1", "회원가입이 완료 되었습니다.", members);
    }

    @Transactional
    @Override
    public void modifyTag(Member member, TagRequest tagRequest){
        Set<FavoriteTag> tags = tagRequest.getTags().stream()
                .map(FavoriteTag::new)
                .collect(Collectors.toSet());
        member.changeTags(tags);
    }

    @Transactional
    @Override
    public RsData<MemberInfoResponse> modifyAddress(Member member, String address){
        member.connectAddress(address);
        return RsData.of("S-1", "주소 수정이 완료되었습니다.", MemberInfoResponse.of(member));
    }

    @Override
    public Member findById(Long id) {
        return memberRepository.findById(id).orElseThrow(() -> new MemberNotFoundException("존재하지 않는 회원입니다."));
    }

    @Override
    public Member findBrandMember(String name){
        return memberRepository.findByBrand(name);
    }

    @Override
    public Optional<Member> findByEmail(String username){
        return memberRepository.findByEmail(username);
    }

    @Transactional
    @Override
    public void updateRecentlyAccessDate(Member member, LocalDateTime dateTime){
        member.updateRecentlyAccessDate(dateTime);
    }

    @Override
    public long getRestCash(Member member) {
        return member.getRestCash();
    }

    private void checkingMemberType(Member member) {
        if (member.getRole() == MemberRoleType.MEMBER) {
            recommendService.rankRecommend(member);
        }
    }

    private Member settingMember(JoinForm joinForm) {
        MemberRoleType memberRoleType = MemberRoleType.getRoleType(joinForm.state());
        Member member = Member.of(joinForm, passwordEncoder.encode(joinForm.password()), memberRoleType);
        if (memberRoleType == MemberRoleType.BRAND_ADMIN) {
            member.connectBrand(joinForm.brand());
            saveBrandAndStoreIfNotExists(joinForm.brand());
        }
        if (joinForm.tags() != null) {
            member.changeTags(createFavoriteTags(joinForm.tags()));
        }
        return member;
    }

    private boolean isEmailSave(String email) {
        return memberRepository.findByEmail(email).isPresent();
    }

    private void saveBrandAndStoreIfNotExists(String brand) {
        if (!brandService.isPresent(brand)) {
            brandService.save(brand);
            storeService.save(new Store(brand));
        }
    }

    private Set<FavoriteTag> createFavoriteTags(List<String> tagList) {
        return tagList.stream()
                .map(FavoriteTag::new)
                .collect(Collectors.toSet());
    }

    @Transactional
    @Override
    public void completeWithdraw(WithdrawApply withdrawApply) {
        Member member = withdrawApply.getApplicant();
        long newRestCash = member.getRestCash() - withdrawApply.getPrice();
        member.connectCash(newRestCash);
    }
}