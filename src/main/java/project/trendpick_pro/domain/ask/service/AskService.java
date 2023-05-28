package project.trendpick_pro.domain.ask.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.trendpick_pro.domain.ask.entity.Ask;
import project.trendpick_pro.domain.ask.entity.dto.request.AskRequest;
import project.trendpick_pro.domain.ask.entity.dto.response.AskResponse;
import project.trendpick_pro.domain.ask.repository.AskRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AskService {

    private final AskRepository askRepository;

    public List<AskResponse> showAll() {
        List<Ask> askList = askRepository.findAll();
        List<AskResponse> responses = new ArrayList<>();

        for(Ask ask : askList){
            responses.add(AskResponse.of(ask));
        }

        return responses;
    }

    public AskResponse show(Long askId) {
        Ask ask = askRepository.findById(askId).orElseThrow();

        return AskResponse.of(ask);
    }

    @Transactional
    public void delete(Long askId) {
        askRepository.deleteById(askId);
    }

    @Transactional
    public AskResponse modify(Long askId, AskRequest askRequest) {
        Ask ask = askRepository.findById(askId).orElseThrow();

        ask.update(askRequest);
        return AskResponse.of(ask);
    }

    @Transactional
    public AskResponse register(AskRequest askRequest) {
        String member = "member"; //Member
        String brand = "brand"; //Brand

        Ask ask = Ask.of(member, brand, askRequest);

        askRepository.save(ask);
        return AskResponse.of(ask);
    }
}
