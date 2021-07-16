package com.haier.autotest.server;

import lombok.extern.slf4j.Slf4j;
import org.subethamail.smtp.TooMuchDataException;
import org.subethamail.smtp.helper.SimpleMessageListener;
import org.subethamail.wiser.WiserMessage;

import javax.mail.MessagingException;
import javax.mail.Session;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

/**
 * 侦听传入的电子邮件并将它们重定向到 {@code MailSaver} object.
 */
@Slf4j
public final class MailListener implements SimpleMessageListener {
    private final MailSaver saver;
    protected List<SimpleMessage> messages;

    /**
     * 创建侦听器。
     *
     * @param saver a {@code MailServer} 用于保存电子邮件和通知组件的对象。
     */
    public MailListener(MailSaver saver) {
        this.messages = Collections.synchronizedList(new ArrayList());
        this.saver = saver;
    }

    @Override
    public boolean accept(String from, String recipient) {
        // TODO 过滤功能待添加
        return true;
    }

    @Override
    public void deliver(String from, String recipient, InputStream is) throws TooMuchDataException, IOException {
        log.debug("Delivering mail from " + from + " to " + recipient);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        BufferedInputStream data = new BufferedInputStream(is);
        int current;
        while ((current = data.read()) >= 0) {
            out.write(current);
        }
        byte[] bytes = out.toByteArray();
        SimpleMessage simpleMessage = new SimpleMessage(this, from, recipient, bytes);
        // TODO 过滤IP 暂不开启
        // String IPAddress = simpleMessage.dumpMessage();
        saver.notifyEmail(simpleMessage, recipient);
    }

    protected Session getSession() {
        return Session.getDefaultInstance(new Properties());
    }

    public List<SimpleMessage> getMessages() {
        return this.messages;
    }

    public void dumpMessages(PrintStream out) throws MessagingException {
        log.debug("----- Start printing messages -----");
        Iterator it = this.getMessages().iterator();

        while (it.hasNext()) {
            WiserMessage wmsg = (WiserMessage) it.next();
            wmsg.dumpMessage(out);
        }
        log.debug("----- End printing messages -----");
    }
}
