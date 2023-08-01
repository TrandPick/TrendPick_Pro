package project.trendpick_pro;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.thymeleaf.ThymeleafAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import project.trendpick_pro.domain.answer.service.AnswerService;
import project.trendpick_pro.domain.ask.controller.AskController;
import project.trendpick_pro.domain.member.service.MemberService;
import project.trendpick_pro.domain.product.service.ProductService;
import project.trendpick_pro.global.security.SecurityConfig;
import project.trendpick_pro.global.util.rq.Rq;

@Import(SecurityConfig.class)
@ActiveProfiles("test")
@WebMvcTest(
    controllers = {
        AskController.class
    }
)
public abstract class ControllerTestSupport {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @MockBean
    protected Rq rq;

    @MockBean
    protected ProductService productService;

    @MockBean
    protected AnswerService answerService;

    @MockBean
    protected MemberService memberService;
}
