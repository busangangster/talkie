package com.e104.realtime.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class UserUpdateRequest {
    private int userSeq;
    private String name;
    private LocalDate birth;
    private String gender;
    private String favorite;
    private String remark;
}
