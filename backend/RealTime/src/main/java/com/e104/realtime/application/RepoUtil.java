package com.e104.realtime.application;

import com.e104.realtime.common.exception.RestApiException;
import com.e104.realtime.common.status.StatusCode;
import com.e104.realtime.domain.User.User;
import com.e104.realtime.infrastructure.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
@RequiredArgsConstructor
public class RepoUtil {

    private final UserRepository userRepository;

    // 로그인
    public int login(String userId) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new RestApiException(StatusCode.NO_CONTENT, "User not found"));
        return user.getUserSeq();
    }

    // 해당 유저 찾기
    public User findUser(int userSeq) {
        return userRepository.findById(userSeq)
                .orElseThrow(() -> new RestApiException(StatusCode.NO_CONTENT, "User not found"));
    }

    public List<User> findAllUsers() {
        return userRepository.findAll();
    }
}
