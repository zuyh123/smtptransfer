package com.haier.autotest.common;

public enum RspEnum {
    SUCESS("0000", "成功"),
    FAILURE("9999", "失败"),
    PARAMS_LOSE("8888", "参数缺失"),
    ADDRESS_LOSE("8888", "邮箱地址缺失"),
    SEVER_ERROR("7777", "邮件发送异常"),
    IPAUTH_ERROR("9999", "未经过授权");

    private String message;
    private String code;

    RspEnum(String code,String message){
        this.code = code;
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public String getCode() {
        return code;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("{");
        sb.append("message='").append(message).append('\'');
        sb.append(", code='").append(code).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
