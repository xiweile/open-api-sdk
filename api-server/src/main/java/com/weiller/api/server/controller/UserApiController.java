package com.weiller.api.server.controller;

import com.weiller.api.server.entity.User;
import com.weiller.utils.api.ApiRequestBody;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserApiController {

    User user1= new User("101","张三",20);
    User user2= new User("102","李四",23);
    User user3= new User("103","王五",26);

    @PostMapping("add")
    public Object add(@RequestBody ApiRequestBody<User> req) {
        return req.getReqData();
    }

	@PostMapping("list")
    public Object list(@RequestBody ApiRequestBody req) {
        List<User> list = new ArrayList<>();
        list.add(user1);
        list.add(user2);
        list.add(user3);
		return list;
    }

    @PostMapping("one")
    public Object one(@RequestBody ApiRequestBody<Map<String,String>> req) {
        String id = req.getReqData().get("id");
        switch (id){
            case "101":
                return user1;
            case "102":
                return user2;
            case "103":
                return user3;
            default:
                throw new RuntimeException("该用户不存在");
        }

    }

}
