package project.trendpick_pro.domain.member.service;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.trendpick_pro.domain.brand.entity.Brand;
import project.trendpick_pro.domain.brand.service.BrandService;
import project.trendpick_pro.domain.cash.entity.CashLog;
import project.trendpick_pro.domain.cash.service.CashService;
import project.trendpick_pro.domain.common.base.rq.Rq;
import project.trendpick_pro.domain.member.entity.Member;
import project.trendpick_pro.domain.member.entity.RoleType;
import project.trendpick_pro.domain.member.entity.form.JoinForm;
import project.trendpick_pro.domain.member.exception.MemberNotFoundException;
import project.trendpick_pro.domain.member.repository.MemberRepository;
import project.trendpick_pro.domain.recommend.service.RecommendService;
import project.trendpick_pro.domain.store.entity.Store;
import project.trendpick_pro.domain.store.service.StoreService;
import project.trendpick_pro.domain.tags.favoritetag.entity.FavoriteTag;
import project.trendpick_pro.domain.tags.tag.entity.dto.request.TagRequest;
import project.trendpick_pro.global.rsData.RsData;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final PasswordEncoder passwordEncoder;
    private final MemberRepository memberRepository;
    private final RecommendService recommendService;
    private final StoreService storeService;

    private final CashService cashService;
    private final BrandService brandService;
    private final Rq rq;
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

        if(brand.length() != 0 && !brandService.isPresent(brand)){
                brandService.save(brand);
                storeService.save(new Store(brand));
        }

        if (joinForm.tags() != null) {
            Set<FavoriteTag> favoriteTags = new LinkedHashSet<>();
            for (String tag : joinForm.tags()) {
                FavoriteTag favoriteTag = new FavoriteTag(tag);
                favoriteTags.add(favoriteTag);
            }
            member.changeTags(favoriteTags);
        }

        Member savedMember = memberRepository.save(member);
        recommendService.select(savedMember.getEmail());

        return RsData.of("S-1", "회원가입이 완료되었습니다.", member);
    }

    @Transactional
    public List<Member> saveAll(List<JoinForm> List) {
        List<Member> list = new ArrayList<>();
        for (JoinForm joinForm : List) {
            if (memberRepository.findByEmail(joinForm.email()).isPresent()) {
                return null;
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

            if(brand.length() != 0 && !brandService.isPresent(brand)){
                brandService.save(brand);
                storeService.save(new Store(brand));
            }

            if (joinForm.tags() != null) {
                Set<FavoriteTag> favoriteTags = new LinkedHashSet<>();
                for (String tag : joinForm.tags()) {
                    FavoriteTag favoriteTag = new FavoriteTag(tag);
                    favoriteTags.add(favoriteTag);
                }
                member.changeTags(favoriteTags);
            }
            list.add(member);
        }
        memberRepository.saveAll(list);
        return list;
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

    public Member findById(Long id) {
        return memberRepository.findById(id).orElseThrow(() -> new MemberNotFoundException("존재하지 않는 회원입니다."));
    }

    public Optional<Member> findByUsername(String username) {
        return memberRepository.findByUsername(username);
    }

    public Optional<Member> findByEmail(String username){
        return memberRepository.findByEmail(username);
    }

    @Transactional
    public void updateRecentlyAccessDate(Member member){
        member.updateRecentlyAccessDate();
    }

    @Data
    @AllArgsConstructor
    public static class AddCashRsDataBody {
        CashLog cashLog;
        long newRestCash;
    }

    @Transactional
    public RsData<AddCashRsDataBody> addCash(String brand, long price, Brand relEntity, CashLog.EvenType eventType) {
        Member member=rq.getBrandMember();
        if(!member.getBrand().equals(brand)){
            return RsData.of("F-1","해당 브랜드와 관리자가 일치하지 않습니다.");
        }

        CashLog cashLog = cashService.addCash(member, price, relEntity.getName(),relEntity.getId(), eventType);

        long newRestCash = getRestCash(member) + cashLog.getPrice();
        member.setRestCash(newRestCash);
        memberRepository.save(member);

        return RsData.of(
                "S-1",
                "성공",
                new AddCashRsDataBody(cashLog, newRestCash)
        );
    }
    public long getRestCash(Member member) {
        System.out.println( memberRepository.findById(member.getId()).get().getRestCash());
        return memberRepository.findById(member.getId()).get().getRestCash();
    }
}