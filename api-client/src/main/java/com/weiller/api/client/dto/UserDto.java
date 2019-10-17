package com.weiller.api.client.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class UserDto {

    @NotNull
    private String id;

    @NotNull
    private String name;

    private Integer age;

    public UserDto(){}

    public UserDto(String id, String name, Integer age){
        this.id = id;
        this.name = name;
        this.age =  age;
    }

}
