package com.haier.autotest.constant;

import lombok.Data;

import java.io.Serializable;

@Data
public class MailTestBmo implements Serializable {
    private String subject;
    private String content;
    private String mailAddress;
}
