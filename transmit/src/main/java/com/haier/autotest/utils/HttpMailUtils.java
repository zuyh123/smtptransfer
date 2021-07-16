package com.haier.autotest.utils;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class HttpMailUtils {
    private static final String mailUrl = "http://10.138.87.167:8188/email/sendMessage";
    /**
     * 主题
     */
    private String subject;
    /**
     * 内容
     */
    private String content;
    /**
     * 收件人(多个用;隔开)
     */
    private String mailAddress;

    public HttpMailUtils(String subject, String content, String mailAddress) {
        this.subject = subject;
        this.content = content;
        this.mailAddress = mailAddress;
    }

    public String httpClientPostJson() {
        StringBuffer resultBuffer;
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(mailUrl);
        httpPost.setHeader("Content-Type", "application/json");
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(60 * 1000)
                .setConnectionRequestTimeout(60 * 1000)
                //设置请求和传输超时时间
                .setSocketTimeout(60 * 1000).build();
        httpPost.setConfig(requestConfig);
        // 构建请求参数
        BufferedReader br = null;
        try {
            StringEntity entity = new StringEntity(getParams(), ContentType.create("application/json", "utf-8"));
            httpPost.setEntity(entity);

            CloseableHttpResponse response = httpclient.execute(httpPost);

            // 读取服务器响应数据
            resultBuffer = new StringBuffer();
            if (null != response.getEntity()) {
                br = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), StandardCharsets.UTF_8));
                String temp;
                while ((temp = br.readLine()) != null) {
                    resultBuffer.append(temp);
                }
            }
        } catch (Exception e) {
            log.error("使用HttpClient以JSON格式发送post请求出现异常，请检查！", e);
            throw new RuntimeException(e);
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    log.error("使用HttpClient以JSON格式发送post请求后关闭br流出现异常，请检查！", e);
                }
            }
        }
        return resultBuffer.toString();
    }

    private String getParams() {
        Map map = new HashMap();
        map.put("subject", subject);
        map.put("content", content);
        map.put("mailAddress", mailAddress);
        return JSONObject.toJSONString(map);
    }
}
