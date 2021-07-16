package com.haier.autotest.server;

import com.haier.autotest.smtp.utils.Configuration;
import com.haier.autotest.smtp.utils.MyAuthenticator;

import javax.mail.Message;
import javax.mail.Message.RecipientType;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Calendar;
import java.util.Date;
import java.util.Observable;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 转发邮件
 */
public final class MailSaver extends Observable {
    /**
     * 简单的缓存
     */
    private static ConcurrentHashMap<String, Date> cache = new ConcurrentHashMap<>();

    public void notifyEmail(SimpleMessage message, String recipient) {
        try {
            MimeMessage mimeMessage = message.getMimeMessage();
            if (!cache.isEmpty() && cache.containsKey(mimeMessage.getMessageID())) {
                return;
            }
            cache.put(mimeMessage.getMessageID(), new Date());
            Properties pro = Configuration.INSTANCE.getConfig();
//            pro.put("mail.smtp.host", "10.135.1.238");
//            pro.put("mail.smtp.port", "25");
//            pro.put("mail.smtp.auth", "true");
//            pro.put("mail.smtp.username", "system@haier.net");
//            pro.put("mail.smtp.password", "");
//            pro.put("mail.smtp.connectiontimeout", 10000);
//            pro.put("mail.smtp.timeout", 10000);
//            pro.setProperty("mail.debug", "true");
            MyAuthenticator authenticator = new MyAuthenticator(
                    Configuration.INSTANCE.get("mail.smtp.username"), Configuration.INSTANCE.get("mail.smtp.password"));
            Session sendMailSession = Session.getInstance(pro, authenticator);
            sendMailSession.setDebug(true);
            Message mailMessage = new MimeMessage(sendMailSession);
            mailMessage.setFrom(new InternetAddress("system@haier.net"));
            mailMessage.setRecipients(RecipientType.TO, mimeMessage.getRecipients(Message.RecipientType.TO));
            mailMessage.setSubject(mimeMessage.getSubject());
            mailMessage.setSentDate(mimeMessage.getSentDate());
            mailMessage.setReplyTo(mimeMessage.getReplyTo());
            mailMessage.setContent(mimeMessage.getContent(), mimeMessage.getContentType());
            // 发送邮件
            Transport.send(mailMessage);
            // 清缓存
            this.clearCache();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 超过3分钟的缓存销毁
     */
    private void clearCache() {
        Calendar nowTime = Calendar.getInstance();
        nowTime.add(Calendar.MINUTE, -3);
        Date time = nowTime.getTime();
        for (String key : cache.keySet()) {
            Date date = cache.get(key);
            if (date == null || time.after(date)) {
                cache.remove(key);
            }
        }
    }
}
