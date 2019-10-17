package com.weiller.utils.encrypt;

import com.weiller.utils.api.ApiRequestBody;
import com.weiller.utils.api.ApiRequestHead;
import com.weiller.utils.json.JsonKit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.encoding.Md5PasswordEncoder;
import org.springframework.security.authentication.encoding.MessageDigestPasswordEncoder;

import java.text.MessageFormat;

@Slf4j
public class SignUtils {


    /**
     * 开放sdk验签方式
     * @param openApiRequest 请求体
     * @param sign 签名
     */
    public static boolean checkSign(ApiRequestBody openApiRequest,String signKey, String sign){
        String bizJson = JsonKit.toString(openApiRequest.getReqData() );
        ApiRequestHead header = openApiRequest.getHead();
        String messageText = header.getAppId().concat(signKey).concat(header.getRequestId())
                .concat(header.getNonce()).concat(String.valueOf(header.getTimestamp())).concat(bizJson);
        log.info("生成签名参数:{}",messageText);
        String signature = Sm3Util.encodePassword(messageText, null);
        if(signature!=null) {
            return signature.equals(sign);
        }else{
            return false;
        }

    }

    /**
     * 签名验证
     * @return signedText
     */
    public static boolean checkSign(String signKey,String appId,String sessionId,String requestId,String timestamp,String sign,String data){
        if (data == null) {
            data = "";
        }
        String signedText = null;
        if ( signKey !=null && sign!=null) {
            String messageText = MessageFormat.format("{0}{1}{2},{3},{4},{5},{6}{7}", data, "{",  appId,signKey,  sessionId, requestId,  timestamp , "}");
            MessageDigestPasswordEncoder mde = new MessageDigestPasswordEncoder("SHA-256", true);
            signedText = (new Md5PasswordEncoder()).encodePassword(mde.encodePassword(messageText, timestamp), (Object)null).toUpperCase();
            log.info("生成签名参数:",messageText);
        }
        if(signedText!=null) {
            return signedText.equals(sign);
        }else{
            return false;
        }
    }
}
