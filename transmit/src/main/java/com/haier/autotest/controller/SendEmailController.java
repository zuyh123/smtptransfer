package com.haier.autotest.controller;

import com.alibaba.fastjson.JSON;
import com.haier.autotest.common.RspEnum;
import com.haier.autotest.constant.MailTestBmo;
import com.haier.autotest.utils.HttpMailUtils;
import com.haier.autotest.utils.HttpRequest;
import com.haier.autotest.utils.MailSenderInfo;
import com.haier.autotest.utils.SimpleMailSender;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

@CrossOrigin
@RestController
@Slf4j
@RequestMapping("/email")
public class SendEmailController {

    @Value("${mail.smtp.ip}")
    private String ip;
    @Value("${mail.smtp.port}")
    private String port;
    @Value("${mail.smtp.ssl.enable}")
    private String sslEnable;
    @Value("${mail.smtp.username}")
    private String username;
    @Value("${mail.smtp.password}")
    private String password;
    @Value("${filter.switch}")
    private boolean filterSwitch;
    @Value("${filter.iplist}")
    private String ipList;


//    @RequestMapping(value = "/sendToHaierEmail", method = RequestMethod.POST)
//    public String sendToHaierEmail(@RequestBody MailTestBmo bmo, HttpServletRequest req, HttpServletResponse rsp) {
//        return new HttpMailUtils(bmo.getSubject(), bmo.getContent(), bmo.getMailAddress()).httpClientPostJson();
//    }

    @RequestMapping(value = "/sendMessage", method = RequestMethod.POST)
    public String sendEmailMessage(@RequestBody MailTestBmo bmo, HttpServletRequest req) {
        if (!HttpRequest.ipFilter(req, filterSwitch, ipList)) {
            return RspEnum.IPAUTH_ERROR.toString();
        }
        log.info("入参={}", bmo.toString());

        // TODO 2.jenkins等来源适配问题

        String subject = bmo.getSubject();
        String content = bmo.getContent();
        String emailAddress = bmo.getMailAddress();
        if (StringUtils.isBlank(subject) || StringUtils.isBlank(content)) {
            return RspEnum.PARAMS_LOSE.toString();
        }
        String[] addresses = this.getEmailAddress(emailAddress);
        if (addresses == null) {
            return RspEnum.ADDRESS_LOSE.toString();
        }
        log.info("准备发送邮件！请稍等...");
        //这个类主要是设置邮件
        MailSenderInfo mailInfo = new MailSenderInfo();
        //这个类主要来发送邮件
        SimpleMailSender sms = new SimpleMailSender();
        mailInfo.setMailServerHost(ip);
        mailInfo.setMailServerPort(port);
        mailInfo.setSslenable("true".equals(sslEnable));
        mailInfo.setValidate(true);
        mailInfo.setUserName(username);
        //您的邮箱密码
        mailInfo.setPassword(password);
        mailInfo.setFromAddress(username);
        //标题
        mailInfo.setSubject(subject);
        //内容
        mailInfo.setContent(content);
        mailInfo.setToAddresses(addresses);

        StringBuilder stringBuilder = new StringBuilder();
        for (String address : addresses) {
            stringBuilder.append(address).append(";");
        }
        String addressEmail = stringBuilder.toString();
        log.info("邮箱信息为={}", mailInfo.toString());
        if (sms.sendHtmlMail(mailInfo)) {
            log.info("发送邮件给{}完成！", addressEmail);
        } else {
            log.warn("发送邮件给{}失败！", addressEmail);
            return RspEnum.SEVER_ERROR.toString();
        }
        return RspEnum.SUCESS.toString();
    }

    private String getParms(HttpServletRequest request) {
        Enumeration<String> a = request.getParameterNames();
        String parm = null;
        String val = "";
        Map<String, String> parmMap = new HashMap<String, String>();
        while (a.hasMoreElements()) {
            //参数名
            parm = a.nextElement();
            //值
            val = request.getParameter(parm);
            parmMap.put(parm, val);
        }
        return JSON.toJSONString(parmMap);
    }

    public String[] getEmailAddress(String temp) {
        String[] address = null;
        try {
            if (StringUtils.isNotBlank(temp)) {
                // 清除最后一个;
                if (temp.contains(";") && temp.substring(temp.length() - 1).contains(";")) {
                    temp = temp.substring(0, temp.length() - 1);
                }
                // 多个地址
                if (!temp.contains("null") && temp.contains(";")) {
                    address = temp.split(";", -1);
                    // 一个地址
                } else if (!temp.contains("null") && !temp.contains(";")) {
                    address = new String[1];
                    address[0] = temp;
                }
            } else {
                log.warn("邮件地址配置为空，取消邮件发送...");
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            log.error("获取邮件收件人地址出现异常，请检查！", e);
            return null;
        }
        return address;
    }
}
