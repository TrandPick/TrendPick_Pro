package project.trendpick_pro.domain.member.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.trendpick_pro.domain.brand.entity.Brand;
import project.trendpick_pro.domain.brand.service.BrandService;
import project.trendpick_pro.domain.cash.entity.CashLog;
import project.trendpick_pro.domain.cash.entity.dto.CashResponse;
import project.trendpick_pro.domain.cash.service.CashService;
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
    private final CashService cashService;
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
    public Member findByBrandMember(String name){
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

    @Transactional
    @Override
    public RsData<CashResponse> addCash(String brand, long price, Brand relEntity, CashLog.EvenType eventType) {
        Member member=findByBrandMember(brand);
        if(!member.getBrand().equals(brand)){
            return RsData.of("F-1","해당 브랜드와 관리자가 일치하지 않습니다.");
        }
        CashLog cashLog = cashService.addCash(member, price, relEntity.getName(),relEntity.getId(), eventType);

        long newRestCash = getRestCash(member) + cashLog.getPrice();
        member.connectCash(newRestCash);

        return RsData.of("S-1", "성공", new CashResponse(cashLog, newRestCash));
    }

    @Override
    public long getRestCash(Member member) {
        return memberRepository.findById(member.getId()).get().getRestCash();
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
}