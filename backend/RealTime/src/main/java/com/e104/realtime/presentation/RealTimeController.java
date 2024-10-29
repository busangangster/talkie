package com.e104.realtime.presentation;

import com.e104.realtime.application.UserService;
import com.e104.realtime.common.response.ResponseDto;
import com.e104.realtime.common.status.StatusCode;
import com.e104.realtime.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class RealTimeController {

    private final UserService userService;

    // 로그인
    @GetMapping("/login/{userId}")
    public ResponseEntity<ResponseDto> login(@PathVariable String userId) {
        LoginResponse loginResponse = userService.login(userId);
        return ResponseDto.response(StatusCode.SUCCESS, loginResponse);
    }

    // 유저 정보 조회
    @GetMapping("/user/{userSeq}")
    public ResponseEntity<ResponseDto> getUser(@PathVariable int userSeq) {
        UserResponse userResponse = userService.getUser(userSeq);
        return ResponseDto.response(StatusCode.SUCCESS, userResponse);
    }

    // 유저 정보 등록 및 수정
    @PutMapping("/user/update")
    public ResponseEntity<ResponseDto> updateUser(@RequestBody UserUpdateRequest request) {
        userService.updateUser(request);
        return ResponseDto.response(StatusCode.SUCCESS, "유저 정보 수정 성공");
    }

    // 부모 질문 등록
    @PostMapping("/question/create")
    public ResponseEntity<ResponseDto> createQuestion(@RequestBody QuestionCreateRequest request) {
        userService.createQuestion(request);
        return ResponseDto.response(StatusCode.SUCCESS, "부모 질문 등록 성공");
    }

    // 부모 질문 삭제
    @DeleteMapping("/question/delete")
    public ResponseEntity<ResponseDto> deleteQuestion(@RequestBody QuestionDeleteRequest request) {
        userService.deleteQuestion(request);
        return ResponseDto.response(StatusCode.SUCCESS, "부모 질문 삭제 성공");
    }


    // 질문 및 응답 조회
    @GetMapping("/question/{userSeq}")
    public ResponseEntity<ResponseDto> getQuestionAndAnswerList(@PathVariable int userSeq) {
        List<QuestionAndResponseDto> questionAndResponseDtos = userService.getQuestionAndAnswerList(userSeq);
        return ResponseDto.response(StatusCode.SUCCESS, questionAndResponseDtos);
    }

    // 일자별 대화 목록 조회
    @PostMapping("/conversation/list")
    public ResponseEntity<ResponseDto> getConversationList(@RequestBody ConversationListRequest request) {
        ConversationListResponse conversationList = userService.getConversationList(request);
        return ResponseDto.response(StatusCode.SUCCESS, conversationList);
    }

    // 대화 상세 조회
    @GetMapping("/conversation/{userSeq}/{conversationSeq}")
    public ResponseEntity<ResponseDto> getConversationDetail(@PathVariable int userSeq, @PathVariable int conversationSeq) {
        ConversationDetailResponse conversationDetail = userService.getConversationDetail(userSeq, conversationSeq);
        return ResponseDto.response(StatusCode.SUCCESS, conversationDetail);
    }

    // 대화 내용 요약 조회
    @GetMapping("/conversation/summary/{userSeq}/{conversationSeq}")
    public ResponseEntity<ResponseDto> getConversationSummary(@PathVariable int userSeq, @PathVariable int conversationSeq) {
        ConversationSummaryResponse conversationSummary = userService.getConversationSummary(userSeq, conversationSeq);
        return ResponseDto.response(StatusCode.SUCCESS, conversationSummary);
    }

    // 주별 대화 통계 조회
    @PostMapping("/conversation/weekly")
    public ResponseEntity<ResponseDto> getWeeklyConversation(@RequestBody WeeklyConversationRequest request) {
        WeeklyConversationResponse weeklyConversation = userService.getWeeklyConversation(request);
        return ResponseDto.response(StatusCode.SUCCESS, weeklyConversation);
    }

}
