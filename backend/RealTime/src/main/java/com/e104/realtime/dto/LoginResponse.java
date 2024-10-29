package com.e104.realtime.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginResponse {

    int userSeq;

    boolean isNotFirstLogin;
}
