package com.e104.realtime.dto;

import lombok.Data;

@Data
public class UserUpdateRequest {
    private int userSeq;
    private String name;
    private int age;
    private String gender;
    private String favorite;
    private String remark;
}
