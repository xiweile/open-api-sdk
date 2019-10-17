package com.weiller.sdk.core.client;

import com.weiller.sdk.core.exception.OpenApiInvokeException;
import com.weiller.utils.encrypt.SSLUtil;
import com.weiller.utils.json.JsonKit;
import okhttp3.*;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;
import java.util.concurrent.TimeUnit;

/**
 * OpenHttpClient	@version 1.0
 */
public class OpenHttpClient {

    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private static final String ua = "GJSWZJSMBS OPEN-SDK v1.0.0";
    private OkHttpClient client = new OkHttpClient().newBuilder().connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(1, TimeUnit.MINUTES).writeTimeout(1, TimeUnit.MINUTES)
            .connectionPool(new ConnectionPool(20, 1, TimeUnit.MINUTES)).retryOnConnectionFailure(true)
            .hostnameVerifier(new HostnameVerifier() {

                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            })
            .sslSocketFactory(SSLUtil.getSSLSocketFactory(), SSLUtil.getX509TrustManager())
            .build();

    /**
     * Create a new instance of OpenHttpClient.
     */
    public OpenHttpClient() {
        super();
    }


    public String post(String url) {
        Request request = new Request.Builder().url(url).addHeader("User-Agent", ua)
                .build();
        try {
            Response response = client.newCall(request).execute();
            if (!response.isSuccessful()) {
                throw new OpenApiInvokeException(url + " 调用接口错误, http错误码" + response.code());
            }
            return response.body().string();
        } catch (Exception e) {
            throw new OpenApiInvokeException("调用接口异常:" + e.getMessage(), e);
        }
    }

    public String post(String url, String json) {
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder().url(url).addHeader("User-Agent", ua).post(body).build();
        try {
            Response response = client.newCall(request).execute();
            if (!response.isSuccessful()) {
                throw new OpenApiInvokeException(url + " 调用接口错误, http错误码" + response.code());
            }
            return response.body().string();
        } catch (Exception e) {
            throw new OpenApiInvokeException("调用接口异常:" + e.getMessage(), e);
        }
    }

    public String post(OpenApiRequest openApiRequest) {
        String apiUrl = openApiRequest.getPath();
        String url = openApiRequest.getHost() + apiUrl;

        RequestBody body = RequestBody.create(JSON, JsonKit.toString(openApiRequest.getBody()));
        Request request = new Request.Builder().url(url).addHeader("User-Agent", ua).post(body).build();
        try {
            Response response = client.newCall(request).execute();
            if (!response.isSuccessful()) {
                throw new OpenApiInvokeException(url + " 调用接口错误, http错误码" + response.code());
            }
            return response.body().string();
        } catch (Exception e) {
            throw new OpenApiInvokeException("调用接口异常:" + e.getMessage(), e);
        }
        //jdk7以上支持
        /*try (Response response = client.newCall(request).execute()) {
            if(!response.isSuccessful()){
        		throw new OpenApiInvokeException(url + " 调用接口错误, http错误码" + response.code());
        	}
            if (response.code() != 200) {
                throw new OpenApiInvokeException(url + " 调用接口错误, http错误码" + response.code());
            }
            return response.body().string();
        } catch (IOException e) {
            throw new OpenApiInvokeException("调用接口异常", e);
        }*/
    }
}
