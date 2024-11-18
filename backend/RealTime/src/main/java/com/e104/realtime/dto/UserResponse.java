package com.e104.realtime.dto;

import com.e104.realtime.domain.User.User;
import lombok.Data;

import java.time.LocalDate;

@Data
public class UserResponse {
    private int userSeq;  // 사용자의 고유 식별자, 자동으로 생성됨

    private String userId;  // 사용자 ID

    private String name; // 사용자 이름

    private LocalDate birth; // 사용자 나이

    private String gender; // 사용자 성별 ( M / F )

    private String favorite; // 사용자의 관심사

    private String remark; // 사용자의 비고

    private boolean isNotFirstLogin;

    public UserResponse(User user) {
        this.userSeq = user.getUserSeq();
        this.userId = user.getUserId();
        this.name = user.getName();
        this.birth = user.getBirth();
        this.gender = user.getGender();
        this.favorite = user.getFavorite();
        this.remark = user.getRemark();
        this.isNotFirstLogin = user.isNotFirstLogin();

    }
}
