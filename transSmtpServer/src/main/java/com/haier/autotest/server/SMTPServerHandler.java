package com.haier.autotest.server;

import com.haier.autotest.smtp.exception.BindPortException;
import com.haier.autotest.smtp.exception.OutOfRangePortException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.subethamail.smtp.helper.SimpleMessageListenerAdapter;
import org.subethamail.smtp.server.SMTPServer;

import java.net.InetAddress;

/**
 * 启动和停止 SMTP 服务器。
 */
public enum SMTPServerHandler {
    /**
     * 实例
     */
    INSTANCE;

    private static final Logger LOGGER = LoggerFactory.getLogger(SMTPServerHandler.class);
    private final MailSaver mailSaver = new MailSaver();
    private final MailListener myListener = new MailListener(mailSaver);
    private final SMTPServer smtpServer = new SMTPServer(new SimpleMessageListenerAdapter(myListener), new SMTPAuthHandlerFactory());

    SMTPServerHandler() {
    }

    /**
     * 在参数中指定的端口和地址上启动服务器
     *
     * @param port        要打开的 SMTP 端口
     * @param bindAddress 要绑定的地址。 null 表示绑定到所有
     * @throws BindPortException        当端口无法打开时
     * @throws OutOfRangePortException  当端口超出范围时
     * @throws IllegalArgumentException 当端口超出范围时
     */
    public void startServer(int port, InetAddress bindAddress) throws BindPortException, OutOfRangePortException {
        LOGGER.debug("在端口{}上启动服务器", port);
        try {
            smtpServer.setBindAddress(bindAddress);
            smtpServer.setPort(port);
            smtpServer.start();
        } catch (RuntimeException exception) {
            if (exception.getMessage().contains("BindException")) {
                LOGGER.error("{}. Port {}", exception.getMessage(), port);
                throw new BindPortException(exception, port);
            } else if (exception.getMessage().contains("out of range")) {
                LOGGER.error("Port {} out of range.", port);
                throw new OutOfRangePortException(exception, port);
            } else {
                LOGGER.error("", exception);
                throw exception;
            }
        }
    }

    /**
     * 停止服务器
     */
    public void stopServer() {
        if (smtpServer.isRunning()) {
            LOGGER.debug("Stopping server");
            smtpServer.stop();
        }
    }

    /**
     * Returns the {@code MailSaver} object.
     *
     * @return the {@code MailSaver} object.
     */
    public MailSaver getMailSaver() {
        return mailSaver;
    }

    /**
     * Returns the {@code SMTPServer} object.
     *
     * @return the {@code SMTPServer} object.
     */
    public SMTPServer getSmtpServer() {
        return smtpServer;
    }
}
