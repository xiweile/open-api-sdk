package com.weiller.api.server.entity;

import lombok.Data;

@Data
public class User {

    private String id;

    private String name;

    private Integer age;

    public User(){}

    public User(String id,String name,Integer age){
        this.id = id;
        this.name = name;
        this.age =  age;
    }

}
