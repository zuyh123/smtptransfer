package com.haier.autotest.server;

import org.subethamail.smtp.AuthenticationHandler;

/**
 * 模拟身份验证
 */
/*package*/ final class SMTPAuthHandler implements AuthenticationHandler {
    private static final String USER_IDENTITY = "User";
    private static final String PROMPT_USERNAME = "334 VXNlcm5hbWU6";
    private static final String PROMPT_PASSWORD = "334 UGFzc3dvcmQ6";

    private int pass = 0;

    @Override
    public String auth(String clientInput) {
        String prompt;

        if (++pass == 1) {
            prompt = SMTPAuthHandler.PROMPT_USERNAME;
        } else if (pass == 2) {
            prompt = SMTPAuthHandler.PROMPT_PASSWORD;
        } else {
            pass = 0;
            prompt = null;
        }
        return prompt;
    }

    /**
     * 如果身份验证过程成功，则返回用户的身份。
     * 定义身份的类型可能因使用的身份验证机制而异，但通常会返回字符串用户名。
     * 如果身份验证不成功，则返回值未定义。
     */
    @Override
    public Object getIdentity() {
        return SMTPAuthHandler.USER_IDENTITY;
    }
}
