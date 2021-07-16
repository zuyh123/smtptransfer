package com.haier.autotest.utils;


import lombok.extern.slf4j.Slf4j;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Date;
import java.util.Properties;

@Slf4j
public class SimpleMailSender {
    /**
     * 以文本格式发送邮件
     *
     * @param mailInfo 待发送的邮件的信息
     */
    public boolean sendTextMail(MailSenderInfo mailInfo) {
        // 判断是否需要身份认证
        MyAuthenticator authenticator = null;
        Properties pro = mailInfo.getProperties();
        if (null == pro) {
            return false;
        }

        if (mailInfo.isValidate()) {
            // 如果需要身份认证，则创建一个密码验证器
            authenticator = new MyAuthenticator(mailInfo.getUserName(), mailInfo.getPassword());
        }
        // 根据邮件会话属性和密码验证器构造一个发送邮件的session
        Session sendMailSession = Session.getDefaultInstance(pro, authenticator);
        try {
            // 根据session创建一个邮件消息
            Message mailMessage = new MimeMessage(sendMailSession);
            // 创建邮件发送者地址
            Address from = new InternetAddress(mailInfo.getFromAddress());
            // 设置邮件消息的发送者
            mailMessage.setFrom(from);
            // 创建邮件的接收者地址，并设置到邮件消息中
            Address to = new InternetAddress(mailInfo.getToAddress());
            mailMessage.setRecipient(Message.RecipientType.TO, to);
            // 设置邮件消息的主题
            mailMessage.setSubject(mailInfo.getSubject());
            // 设置邮件消息发送的时间
            mailMessage.setSentDate(new Date());
            // 设置邮件消息的主要内容
            String mailContent = mailInfo.getContent();
            mailMessage.setText(mailContent);
            // 发送邮件
            Transport.send(mailMessage);
            return true;
        } catch (MessagingException ex) {
            log.error("邮件发送异常", ex);
        }
        return false;
    }

    /**
     * 以HTML格式发送邮件
     *
     * @param mailInfo 待发送的邮件信息
     */
    public boolean sendHtmlMail(MailSenderInfo mailInfo) {
        // 判断是否需要身份认证
        MyAuthenticator authenticator = null;
        Properties pro = mailInfo.getProperties();
        if (null == pro) {
            return false;
        }
        pro.put("mail.smtp.connectiontimeout", 10000);
        pro.put("mail.smtp.timeout", 10000);
        pro.setProperty("mail.smtp.auth", "true");
        pro.setProperty("mail.debug", "true");
//        pro.setProperty("mail.smtp.socketFactory.port", "8025");
//        pro.setProperty("mail.smtp.socketFactory.fallback", "false");
//        pro.setProperty("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
//        pro.setProperty("mail.transport.protocol", PROTOCOL);
//        pro.setProperty("mail.smtp.auth", IS_AUTH);
//        pro.setProperty("mail.debug",IS_ENABLED_DEBUG_MOD);
        //如果需要身份认证，则创建一个密码验证器
        if (mailInfo.isValidate()) {
            authenticator = new MyAuthenticator(mailInfo.getUserName(), mailInfo.getPassword());
        }
        // 根据邮件会话属性和密码验证器构造一个发送邮件的session
        Session sendMailSession = Session.getDefaultInstance(pro, authenticator);
        sendMailSession.setDebug(true);
        try {
            // 根据session创建一个邮件消息
            Message mailMessage = new MimeMessage(sendMailSession);
            // 创建邮件发送者地址
            Address from = new InternetAddress(mailInfo.getFromAddress());
            // 设置邮件消息的发送者
            mailMessage.setFrom(from);
            // 创建邮件的接收者地址，并设置到邮件消息中
            Address[] to = new InternetAddress[mailInfo.getToAddresses().length];
            // 为每个邮件接收者创建一个地址
            for (int i = 0; i < mailInfo.getToAddresses().length; i++) {
                to[i] = new InternetAddress(mailInfo.getToAddresses()[i]);
            }

            // Message.RecipientType.TO属性表示接收者的类型为TO
            //单个收件人    mailMessage.setRecipient(Message.RecipientType.TO,to);
            mailMessage.setRecipients(Message.RecipientType.TO, to);
            // 设置邮件消息的主题
            mailMessage.setSubject(mailInfo.getSubject());
            // 设置邮件消息发送的时间
            mailMessage.setSentDate(new Date());
            // MiniMultipart类是一个容器类，包含MimeBodyPart类型的对象
//            Multipart mainPart = new MimeMultipart();
//            // 创建一个包含HTML内容的MimeBodyPart
//            MimeBodyPart html = new MimeBodyPart();
//            // 设置HTML内容
//            html.setContent(mailInfo.getContent(), "text/html; charset=utf-8");
//            mainPart.addBodyPart(html);
//            // 将MiniMultipart对象设置为邮件内容
//            mailMessage.setContent(mainPart);
            mailMessage.setText(mailInfo.getContent());
            // 发送邮件
            Transport.send(mailMessage);
            return true;
        } catch (MessagingException ex) {
            ex.printStackTrace();
            log.error("邮件发送异常", ex);
            return false;
        }
    }
}   