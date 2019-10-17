package com.weiller.api.client.http;

import com.weiller.api.client.dto.UserDto;
import com.weiller.sdk.core.api.*;
import com.weiller.utils.api.ApiResponse;

import java.util.List;

@OpenApiService(protocol= OpenApiProtocol.HTTP, name = "用户访问接口")
public interface UserApi extends BaseOpenApi {

    @OpenApi(path = "/user/add", name = "添加用户")
    ApiResponse<UserDto> add(@ApiParam("userDto") UserDto userDto);

    @OpenApi(path = "/user/list", name = "用户列表")
    ApiResponse<List<UserDto>> list();

    @OpenApi(path = "/user/one", name = "是否登录状态")
    ApiResponse<UserDto> one(@ApiParam("id") String id);
}
