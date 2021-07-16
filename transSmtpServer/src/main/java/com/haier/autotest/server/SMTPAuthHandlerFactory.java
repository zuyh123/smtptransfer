package com.haier.autotest.server;

import org.subethamail.smtp.AuthenticationHandler;
import org.subethamail.smtp.AuthenticationHandlerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * 用于创建身份验证处理程序的工厂接口。
 */
/*package*/ final class SMTPAuthHandlerFactory implements AuthenticationHandlerFactory {
    private static final String LOGIN_MECHANISM = "LOGIN";

    @Override
    public AuthenticationHandler create() {
        return new SMTPAuthHandler();
    }

    @Override
    public List<String> getAuthenticationMechanisms() {
        List<String> result = new ArrayList<String>();
        result.add(SMTPAuthHandlerFactory.LOGIN_MECHANISM);
        return result;
    }
}
