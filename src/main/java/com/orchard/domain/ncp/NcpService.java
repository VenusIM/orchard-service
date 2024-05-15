package com.orchard.domain.ncp;

import com.orchard.domain.auth.domain.persist.Message;
import com.orchard.domain.auth.domain.persist.MessageRepository;
import com.orchard.domain.member.domain.vo.UserPhoneNumber;
import com.orchard.domain.ncp.core.NcpApiOption;
import com.orchard.domain.ncp.core.SmsResponse;
import com.orchard.domain.ncp.core.WebClientNcpSender;
import io.micrometer.common.util.StringUtils;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class NcpService {
    private final String serviceId;
    private final String from;
    private final WebClientNcpSender webClientNcpSender;
    private final MessageRepository messageRepository;
    public NcpService(@Value("${ncp.from}") String from,
                      @Value("${ncp.serviceId}") String serviceId,
                      WebClientNcpSender webClientNcpSender,
                      MessageRepository messageRepository) {
        this.from = from;
        this.serviceId = serviceId;
        this.webClientNcpSender = webClientNcpSender;
        this.messageRepository = messageRepository;
    }

    public Map<String, String> sendMessage(String number) {

        Map<String, String> result = new HashMap<>();

        if(StringUtils.isEmpty(number)) {
            result.put("code", "200");
            result.put("msg", "번호를 입력하세요.");
            return result;
        }

        int random = ThreadLocalRandom.current().nextInt(100000, 1000000);
        Optional<Message> messageOptional = messageRepository.findByPhoneNumber(UserPhoneNumber.from(number));

        Message message;
        if(messageOptional.isEmpty()) {
           message = Message.of(UserPhoneNumber.from(number), 1, String.valueOf(random));
        } else {
            message = messageOptional.get();
            if(message.getCount() >= 5) {
                result.put("code", "400");
                result.put("msg", "발송 횟수 5회 초과하였습니다. 관리자에게 문의하세요.");
                return result;
            }
            message.updateCode(String.valueOf(random));
            message.increaseCount();
        }
        messageRepository.save(message);


        JSONObject jsonObject = new JSONObject();
        jsonObject.put("type", "SMS");
        jsonObject.put("from", from);
        jsonObject.put("content", "킹체리 인증번호 ["+random+"]");
        Map<String, String> messages = new HashMap<>();
        messages.put("to",number.replaceAll("-", ""));
        jsonObject.put("messages", List.of(messages));


        NcpApiOption ncpApiOption = NcpApiOption.builder()
                .method(HttpMethod.POST)
                .path("/sms/v2/services/"+serviceId+"/messages")
                .request(jsonObject)
                .build();

        SmsResponse smsResponse
                = webClientNcpSender.sendWithBlock(ncpApiOption, new ParameterizedTypeReference<SmsResponse>() {})
                .getBody();

        if(smsResponse != null && "202".equals(smsResponse.getStatusCode())) {
            result.put("code", "200");
            result.put("msg", "성공");
        } else {
            result.put("code", "500");
            result.put("msg", "전송 실패하였습니다. 관리자에게 문의하세요.");
        }

        return result;

    }
}
