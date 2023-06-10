package project.trendpick_pro.global.tosspayment;

import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import project.trendpick_pro.domain.orders.entity.dto.request.OrderForm;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Controller
public class PaymentController {

    @GetMapping(value = "/payment/success")
    public String paymentResult(
            Model model,
            @ModelAttribute OrderForm orderForm,
            @RequestParam(value = "orderId") String orderId,
            @RequestParam(value = "amount") Integer amount,
            @RequestParam(value = "paymentKey") String paymentKey) throws Exception {

        String secretKey = "test_sk_4vZnjEJeQVxqjRyWaKOrPmOoBN0k";

        System.out.println("orderForm = " + orderForm);

        Base64.Encoder encoder = Base64.getEncoder();
        byte[] encodedBytes = encoder.encode(secretKey.getBytes("UTF-8"));
        String authorizations = "Basic " + new String(encodedBytes, 0, encodedBytes.length);

        URL url = new URL("https://api.tosspayments.com/v1/payments/" + paymentKey);

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestProperty("Authorization", authorizations);
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        JSONObject obj = new JSONObject();
        obj.put("orderId", orderId);
        obj.put("amount", amount);

        OutputStream outputStream = connection.getOutputStream();
        outputStream.write(obj.toString().getBytes("UTF-8"));

        int code = connection.getResponseCode();
        boolean isSuccess = code == 200;
        model.addAttribute("isSuccess", isSuccess);

        InputStream responseStream = isSuccess ? connection.getInputStream() : connection.getErrorStream();

        Reader reader = new InputStreamReader(responseStream, StandardCharsets.UTF_8);
        JSONParser parser = new JSONParser();
        JSONObject jsonObject = (JSONObject) parser.parse(reader);
        responseStream.close();
        model.addAttribute("responseStr", jsonObject.toJSONString());
        System.out.println(jsonObject.toJSONString());

        model.addAttribute("method", (String) jsonObject.get("method"));
        model.addAttribute("orderName", (String) jsonObject.get("orderName"));

        if (((String) jsonObject.get("method")) != null) {
            if (((String) jsonObject.get("method")).equals("카드")) {
                model.addAttribute("cardNumber", (String) ((JSONObject) jsonObject.get("card")).get("number"));
            } else if (((String) jsonObject.get("method")).equals("가상계좌")) {
                model.addAttribute("accountNumber", (String) ((JSONObject) jsonObject.get("virtualAccount")).get("accountNumber"));
            } else if (((String) jsonObject.get("method")).equals("계좌이체")) {
                model.addAttribute("bank", (String) ((JSONObject) jsonObject.get("transfer")).get("bank"));
            } else if (((String) jsonObject.get("method")).equals("휴대폰")) {
                model.addAttribute("customerMobilePhone", (String) ((JSONObject) jsonObject.get("mobilePhone")).get("customerMobilePhone"));
            }
        } else {
            model.addAttribute("code", (String) jsonObject.get("code"));
            model.addAttribute("message", (String) jsonObject.get("message"));
        }

        return "redirect:/trendpick/member/orders/list";
    }

    @GetMapping(value = "/payment/fail")
    public String paymentResult(
            Model model,
            @RequestParam(value = "message") String message,
            @RequestParam(value = "code") Integer code
    ) throws Exception {

        model.addAttribute("code", code);
        model.addAttribute("message", message);

        return "trendpick/orders/order-form";
    }
}
