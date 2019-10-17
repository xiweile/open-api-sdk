package com.weiller.api.server.web.interceptor;

import com.weiller.utils.api.ApiRequestBody;
import com.weiller.utils.api.ApiResponse;
import com.weiller.utils.api.ApiResponseHead;
import com.weiller.utils.encrypt.SignUtils;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Aspect
@Order(2)
public class ApiValidateInterceptor {


    @Pointcut("execution(* com.weiller.api.server.controller.*Api*.*(..))")
    public void validate(){

    }

    @Around("validate()")
    public ApiResponse around(ProceedingJoinPoint pjp) throws Throwable {
        Long startTime = System.currentTimeMillis();

        ApiResponse result = null;
        Object resultBody = null;
   //     ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes)RequestContextHolder.getRequestAttributes();
   //     HttpServletRequest request = servletRequestAttributes.getRequest();

        String appId = "";
        String tokenId = "";
        String requestId ="";
        Long timestamp =0L;
        String sign = "";
        String signKey = ""; // String desKey = "";
        String data = "";
        ApiRequestBody apiRequest = null;

        try {
            // 1.捕获请求数据
            Object[] args = pjp.getArgs();
            if(args.length>0){
                Object arg  =  args[0];
                if(arg instanceof ApiRequestBody){
                    apiRequest = (ApiRequestBody) args[0];
                    appId = apiRequest.getHead().getAppId();
                    tokenId = apiRequest.getHead().getTokenId();
                    requestId =apiRequest.getHead().getRequestId();
                    timestamp =apiRequest.getHead().getTimestamp();
                    sign = apiRequest.getHead().getSignature();
                }else{
                    throw new RuntimeException("请求体格式错误！");
                }
            }else{
                throw new RuntimeException("无请求体！");
            }

            if(appId==null ||   requestId==null || sign==null || timestamp==null||timestamp==0 ){
                throw new RuntimeException("请求参数不完整！");
            }

            // 2.验签
            // 2.1 有效性校验 时间差最大允许1分钟
            if (System.currentTimeMillis() - timestamp > 10 * 60 * 1000) {
                throw new RuntimeException("请求过期");
            }

            // 2.2 判断是否授权 根据appId查询
            if(!"100001".equals(appId)){
                throw new RuntimeException("无效的appId");
            }

            signKey = "weiller";
            log.info("存在该app[{}]注册,秘钥:{}",appId,signKey);

            // 2.3 data参数解密 省略
            //args[8] = DESUtil.decrypt((String) args[8], desKey);

            // 2.4 合法性校验 验签
            boolean checkreult = SignUtils.checkSign(apiRequest,signKey,sign);

            if (!checkreult) {
                throw new RuntimeException("签名不合法");
            }

            resultBody = pjp.proceed(args);//执行方法

            result = ApiResponse.success(new ApiResponseHead(requestId, "0", "", System.currentTimeMillis(), System.currentTimeMillis() - startTime), resultBody);
        }catch (Throwable t){
            result = ApiResponse.fail(new ApiResponseHead(requestId, "500", t.getMessage(), System.currentTimeMillis(), System.currentTimeMillis() - startTime));
            log.error(t.getMessage(),t);
        }

        return result;
    }
}
