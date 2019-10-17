//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.weiller.sdk.core.api;

import com.weiller.utils.api.ApiRequestBody;
import com.weiller.utils.api.ApiRequestHead;
import com.weiller.utils.common.IDUtil;
import com.weiller.utils.encrypt.Sm3Util;
import com.weiller.utils.json.JsonKit;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.Header;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.security.authentication.encoding.Md5PasswordEncoder;
import org.springframework.security.authentication.encoding.MessageDigestPasswordEncoder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class FsRestClient {
    private static final String APPLICATION_JSON = "application/json;charset=UTF-8";
    private static final String CONTENT_TYPE_TEXT_JSON = "text/json;charset=UTF-8";
    String appId;
    private String appDesKey;
    private String appSignKey;
    private String tokenId;
    private String username;
    private String password;
    private String contentType;
    private int responseCode;
    private String responseContent;
    private ApiRequestBody requestBody = new ApiRequestBody();

    public FsRestClient(String appId) {
        this(appId, (String)null, (String)null, (String)null);
    }

    public FsRestClient(String appId, String tokenId) {
        this(appId, tokenId, (String)null, (String)null);
    }

    public FsRestClient(String appId, String tokenId, String appSignKey) {
        this(appId, tokenId, null, appSignKey);
    }
 /*   public FsRestClient(String appId, String appDesKey, String appSignKey) {
        this(appId, (String)null, appDesKey, appSignKey);
    }*/

    public FsRestClient(String appId, String tokenId, String appDesKey, String appSignKey) {
        this.contentType = "application/json;charset=UTF-8";
        this.appId = appId;
        this.appDesKey = appDesKey;
        this.appSignKey = appSignKey;
        this.tokenId = tokenId;
        if (this.tokenId == null) {
            this.tokenId = IDUtil.getUUID();
        }

    }

    public boolean post(String url, String data, String requestId) {
        return this.request(true, url, data, requestId);
    }

    public boolean post(String url, String data) {
        return this.request(true, url, data, (String)null);
    }

    public boolean get(String url) {
        return this.get(url, (String)null);
    }

    public boolean get(String url, String requestId) {
        return this.request(false, url, (String)null, requestId);
    }

    private boolean request(boolean isPost, String url, String data, String requestId) {
        this.responseContent = null;
        if (log.isDebugEnabled()) {
            log.debug("Post: " + url + ", Data: " + data);
        }

        if (requestId == null) {
            requestId = IDUtil.getUUID();
        }

        long timestamp = System.currentTimeMillis();
        if (url.indexOf("?") < 0) {
            url = url + "?";
        } else {
            url = url + "&";
        }

        //url = url + String.format("appId=%s&sessionId=%s&requestId=%s&timestamp=%s", this.appId, this.tokenId, requestId, String.valueOf(timestamp));
        if (data == null) {
            data = "";
        }else{
            Map map = JsonKit.fromJson(data, Map.class);
            this.requestBody.setReqData(map);
        }
        ApiRequestHead head = new ApiRequestHead();
        head.setAppId(this.appId);
        head.setTokenId(this.tokenId);
        head.setNonce(IDUtil.getUUID());
        head.setTimestamp( timestamp );
        head.setRequestId(IDUtil.getUUID());
        this.requestBody.setHead(head);

        String signedText = null;
        boolean isEncypted = false;
        if (this.appSignKey != null) {

            signedText = createSign(requestBody);
            requestBody.getHead().setSignature(signedText);
            url = url + String.format("&sign=%s", signedText);
            isEncypted = true;
        }

        Object httpUriRequest;
        if (isPost) {
            httpUriRequest = new HttpPost(url);
        } else {
            httpUriRequest = new HttpGet(url);
        }

        ((HttpUriRequest)httpUriRequest).addHeader("Content-Type", this.contentType);
        ((HttpUriRequest)httpUriRequest).addHeader("Accept", this.contentType);
        ((HttpUriRequest)httpUriRequest).addHeader("Accept-Charset", "UTF-8");
        CloseableHttpClient httpclient;
        if (this.username != null) {
            BasicCredentialsProvider credsProvider = new BasicCredentialsProvider();
            credsProvider.setCredentials(new AuthScope(((HttpUriRequest)httpUriRequest).getURI().getHost(), ((HttpUriRequest)httpUriRequest).getURI().getPort()), new UsernamePasswordCredentials(this.username, this.password));
            httpclient = HttpClients.custom().setDefaultCredentialsProvider(credsProvider).build();
        } else {
            httpclient = HttpClients.custom().build();
        }

        if (httpUriRequest instanceof HttpPost && data != null) {
            StringEntity stringEntity = new StringEntity(JsonKit.toString(requestBody), "UTF-8");
            stringEntity.setContentType("text/json;charset=UTF-8");
            ((HttpPost)httpUriRequest).setEntity(stringEntity);
        }

        boolean result = this.requestData(httpclient, (HttpUriRequest)httpUriRequest);
        if (result && isEncypted && this.responseContent != null) {
            try {
                //this.responseContent = DESUtil.decrypt(this.responseContent, this.appDesKey, true);
            } catch (Exception var14) {
                RuntimeException e = new RuntimeException(var14);
                log.error("远程请求异常",e);
                throw e;
            }
        }

        if (log.isDebugEnabled()) {
            log.debug("Response Content: " + this.responseContent);
        }

        return result;
    }

    private boolean requestData(CloseableHttpClient httpclient, HttpUriRequest httpUriRequest) {
        RuntimeException e;
        try {
            if (log.isDebugEnabled()) {
                log.debug("Request Headers: " + this.headers2Map(httpUriRequest.getAllHeaders()));
            }

            CloseableHttpResponse response = httpclient.execute(httpUriRequest);
            StringBuffer sb = new StringBuffer();

            try {
                if (log.isDebugEnabled()) {
                    log.debug("Response Headers: " + this.headers2Map(response.getAllHeaders()));
                }

                InputStream in = response.getEntity().getContent();
                InputStreamReader rd = new InputStreamReader(in);
                BufferedReader br = new BufferedReader(rd);

                try {
                    for(String line = br.readLine(); line != null; line = br.readLine()) {
                        sb.append(line);
                    }
                } finally {
                    br.close();
                    rd.close();
                }
            } finally {
                response.close();
            }

            this.responseCode = response.getStatusLine().getStatusCode();
            this.responseContent = sb.toString();
            if (this.responseCode == 200) {
                return true;
            } else {
                log.error("Http request fail, uri: " + httpUriRequest.getURI().toString() + ", status code: " + this.responseCode + ", response data: " + this.responseContent);
                return false;
            }
        } catch (ClientProtocolException var21) {
            e = new RuntimeException(var21);
            log.error("远程请求异常",e);
            throw e;
        } catch (IOException var22) {
            e = new RuntimeException(var22);
            log.error("远程请求异常",e);
            throw e;
        }
    }

    private Map<String, String> headers2Map(Header[] headers) {
        Map<String, String> map = new HashMap();
        Header[] var6 = headers;
        int var5 = headers.length;

        for(int var4 = 0; var4 < var5; ++var4) {
            Header header = var6[var4];
            map.put(header.getName(), header.getValue());
        }

        return map;
    }


    private String createSign(String requestId,String data,Long timestamp){
        String messageText = MessageFormat.format("{0}{1}{2},{3},{4},{5},{6}{7}", data, "{", this.appId, this.appSignKey, this.tokenId, requestId, String.valueOf(timestamp), "}");
        MessageDigestPasswordEncoder mde = new MessageDigestPasswordEncoder("SHA-256", true);
        String signedText = (new Md5PasswordEncoder()).encodePassword(mde.encodePassword(messageText, timestamp), (Object)null).toUpperCase();

        if(  this.appDesKey != null){
            try {
                //data = DESUtil.encrypt(data, this.appDesKey);//暂不用数据加密
            } catch (Exception var15) {
                RuntimeException e = new RuntimeException(var15);
                log.error("远程请求异常",e);
                throw e;
            }
        }
        return signedText;
    }

    private String createSign( ApiRequestBody apiRequestBody ){
        ApiRequestHead header = apiRequestBody.getHead();
        String bizJson = JsonKit.toString( apiRequestBody.getReqData());
        String messageText = header.getAppId().concat(this.appSignKey).concat(header.getRequestId())
                .concat(header.getNonce()).concat(String.valueOf(header.getTimestamp())).concat(bizJson);
        log.info("生成签名参数:{}",messageText);
        return Sm3Util.encodePassword(messageText, null);
    }

    public int getResponseCode() {
        return this.responseCode;
    }

    public void setResponseCode(int responseCode) {
        this.responseCode = responseCode;
    }

    public String getResponseContent() {
        return this.responseContent;
    }

    public void setResponseContent(String responseContent) {
        this.responseContent = responseContent;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
