package com.weiller.api.client.test;

import com.weiller.api.client.dto.UserDto;
import com.weiller.api.client.http.UserApi;
import com.weiller.sdk.core.client.OpenApiFactory;
import com.weiller.sdk.core.client.OpenApiFactoryHelper;
import com.weiller.utils.api.ApiResponse;
import org.junit.Test;

import java.util.List;

public class SdkTest {

    OpenApiFactory openApiFactory = OpenApiFactoryHelper.getOpenApiFactory("http://localhost:8080/api", "100001", "weiller");

    @Test
    public void addUser(){
        UserApi userApi = openApiFactory.create(UserApi.class);
        UserDto userDto = new UserDto();
        userDto.setId("105");
        userDto.setName("测试用户");
        userDto.setAge(10);
        ApiResponse<UserDto> add = userApi.add(userDto);
        System.out.println(add);
    }


    @Test
    public void UserList(){
        UserApi userApi = openApiFactory.create(UserApi.class);
        ApiResponse<List<UserDto>> list = userApi.list();
        System.out.println(list);
    }


    @Test
    public void getUser(){
        UserApi userApi = openApiFactory.create(UserApi.class);
        ApiResponse<UserDto> one = userApi.one("105");
        System.out.println(one);
    }

}
