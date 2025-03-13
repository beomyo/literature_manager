package com.example.entity;

import lombok.Data;

@Data
public class User extends Account {
    // 使用Lombok @Data注解自动生成getter/setter
    // 继承Account类的所有属性
}