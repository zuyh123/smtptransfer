package com.haier.autotest.server;

import lombok.extern.slf4j.Slf4j;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.ByteArrayInputStream;

@Slf4j
public class SimpleMessage {
    byte[] messageData;
    MailListener wiser;
    String envelopeSender;
    String envelopeReceiver;

    SimpleMessage(MailListener wiser, String envelopeSender, String envelopeReceiver, byte[] messageData) {
        this.wiser = wiser;
        this.envelopeSender = envelopeSender;
        this.envelopeReceiver = envelopeReceiver;
        this.messageData = messageData;
    }

    public MimeMessage getMimeMessage() throws MessagingException {
        return new MimeMessage(this.wiser.getSession(), new ByteArrayInputStream(this.messageData));
    }

    public byte[] getData() {
        return this.messageData;
    }

    public String getEnvelopeReceiver() {
        return this.envelopeReceiver;
    }

    public String getEnvelopeSender() {
        return this.envelopeSender;
    }

    public String dumpMessage() {
        log.info("===== Dumping message =====");
        log.info("Envelope sender: " + this.getEnvelopeSender());
        log.info("Envelope recipient: " + this.getEnvelopeReceiver());
        String content = new String(this.getData());
        log.info(content);
        log.info("===== End message dump =====");
        try {
            return content.substring(content.indexOf("([") + 2, content.indexOf("])"));
        } catch (Exception e) {
            return "";
        }
    }

    @Override
    public String toString() {
        return this.getData() == null ? "" : new String(this.getData());
    }
}