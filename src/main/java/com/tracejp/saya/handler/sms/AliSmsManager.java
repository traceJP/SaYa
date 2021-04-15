package com.tracejp.saya.handler.sms;


import cn.hutool.core.util.RandomUtil;
import com.aliyun.dysmsapi20170525.Client;
import com.aliyun.dysmsapi20170525.models.SendSmsRequest;
import com.aliyun.dysmsapi20170525.models.SendSmsResponse;
import com.aliyun.dysmsapi20170525.models.SendSmsResponseBody;
import com.aliyun.teaopenapi.models.Config;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tracejp.saya.frame.properties.AliSmsProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * @author traceJP
 * @date 2021/4/11 10:05
 */
@Component
@Slf4j
public class AliSmsManager {

    @Autowired
    AliSmsProperties aliSmsProperties;

    /**
     * 阿里发送短信后响应状态码
     */
    private static final String RESPONSE_CODE_OK = "OK";


    /**
     * 发送6位随机数字的短信验证码
     * @param phoneNumber 手机号
     * @param templateName 短信模板
     * @return 验证码字符串
     * @throws Exception 发送失败时抛出异常
     */
    public String sendVerificationCode(String phoneNumber, String templateName) throws Exception {
        String code = String.valueOf(RandomUtil.randomInt(100000, 1000000));
        Map<String, String> templateMap = new HashMap<>();
        templateMap.put("code", code);
        if (!sendSms(phoneNumber, templateName, templateMap)) {
            throw new Exception();
        }
        return code;
    }

    /**
     * 发送短信（无参数模板短信）
     * @param phoneNumber 目标手机号
     * @param templateName 短信模板名
     * @return 发送成功返回true，否则返回false
     */
    public boolean sendSms(String phoneNumber, String templateName) {
        return sendSms(phoneNumber, templateName, null);
    }

    /**
     * 发送短信（有参数模板短信）
     * @param phoneNumber 目标手机号
     * @param templateName 短信模板名
     * @param templateParam 短信参数
     * @return 发送成功返回true，否则返回false
     */
    public boolean sendSms(String phoneNumber, String templateName, Map<String, String> templateParam) {
        SendSmsResponseBody body;
        try {
            Client client = createClient();
            SendSmsRequest sendSmsRequest = createSmsRequest(phoneNumber, templateName, templateParam);
            SendSmsResponse sendSmsResponse = client.sendSms(sendSmsRequest);
            body = sendSmsResponse.getBody();
        } catch (Exception e) {
            log.error("初始化账号或模板创建出现异常");
            return false;
        }

        // 判断记录发送状态
        if (StringUtils.equals(RESPONSE_CODE_OK, body.getCode())) {
            log.info("短信发送成功" + body);
            return true;
        } else {
            log.warn("短信发送失败" + body);
            return false;
        }
    }

    /**
     * 创建短信模板
     * @return SendSmsRequest
     */
    private SendSmsRequest createSmsRequest(String phoneNumber, String templateName, Map<String, String> templateParam)
            throws Exception {

        SendSmsRequest sendSmsRequest = new SendSmsRequest();

        sendSmsRequest.setPhoneNumbers(phoneNumber);

        sendSmsRequest.setSignName(aliSmsProperties.getSignName());

        // 遍历查找配置文件中是否存在对应模板名
        aliSmsProperties.getTemplateCode().forEach((k, v) -> {
            if (StringUtils.equals(templateName, k)) {
                sendSmsRequest.setTemplateCode(v);
            }
        });
        if (sendSmsRequest.getTemplateCode() == null) {
            log.error("未找到配置文件对应的模板名");
            throw new Exception("未找到对应的模板名");
        }

        // 如果存在模板参数则转换为json并添加
        if (templateParam != null) {
            String json = new ObjectMapper().writeValueAsString(templateParam);
            sendSmsRequest.setTemplateParam(json);
        }

        return sendSmsRequest;
    }

    /**
     * 使用AK&SK初始化账号Client
     * @return Client
     */
    private Client createClient() throws Exception {
        Config config = new Config()
                .setAccessKeyId(aliSmsProperties.getAccessKeyId())
                .setAccessKeySecret(aliSmsProperties.getAccessKeySecret())
                .setEndpoint(aliSmsProperties.getEndpoint());
        return new Client(config);
    }

}
