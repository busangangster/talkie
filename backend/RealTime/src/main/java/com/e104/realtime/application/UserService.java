package com.e104.realtime.application;

import com.e104.realtime.domain.entity.User;
import com.e104.realtime.domain.vo.ConversationAnalytics;
import com.e104.realtime.domain.vo.DayAnalytics;
import com.e104.realtime.domain.vo.Question;
import com.e104.realtime.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final RepoUtil repoUtil;
    private final BuilderUtil builderUtil;

    // 로그인
    public int login(String userId) {
        return repoUtil.login(userId);
    }

    // 유저 정보 수정
    @Transactional
    public void updateUser(UserUpdateRequest request) {
        User user = repoUtil.findUser(request.getUserSeq());
        user.updateUserInfo(request.getName(), request.getAge(), request.getGender(), request.getFavorite());
        // TODO: 웹소켓 세션 업데이트
    }

    // 질문 등록
    @Transactional
    public void createQuestion(QuestionCreateRequest request) {
        User user = repoUtil.findUser(request.getUserSeq());
        Question question = builderUtil.buildQuestion(request);
        user.addQuestion(question);
    }

    // 질문 삭제
    @Transactional
    public void deleteQuestion(QuestionDeleteRequest request) {
        User user = repoUtil.findUser(request.getUserSeq());
        user.removeQuestion(request.getQuestionSeq());
    }

    // 질문 및 응답 조회
    public List<QuestionAndResponseDto> getQuestionAndAnswerList(int userSeq) {
        User user = repoUtil.findUser(userSeq);
        List<Question> questions = user.getQuestions();

        return questions.stream()
                .map(QuestionAndResponseDto::new)
                .toList();
    }

    // 일자별 대화 목록 조회
    public ConversationListResponse getConversationList(ConversationListRequest request) {
        User user = repoUtil.findUser(request.getUserSeq());
        return new ConversationListResponse(user, request.getDay());
    }

    // 대화 상세 조회
    public ConversationDetailResponse getConversationDetail(int userSeq, int conversationSeq) {
        User user = repoUtil.findUser(userSeq);
        ConversationAnalytics conversationAnalytics = user.getConversationAnalytics(conversationSeq);
        return new ConversationDetailResponse(conversationAnalytics);
    }

    // 대화 내용 요약 조회
    public ConversationSummaryResponse getConversationSummary(int userSeq, int conversationSeq) {
        User user = repoUtil.findUser(userSeq);
        ConversationAnalytics conversationAnalytics = user.getConversationAnalytics(conversationSeq);
        return new ConversationSummaryResponse(conversationAnalytics.getConversationSummary());
    }

    // 주별 대화 통계 조회
    public WeeklyConversationResponse getWeeklyConversation(WeeklyConversationRequest request) {
        User user = repoUtil.findUser(request.getUserSeq());
        List<DayAnalytics> dayAnalytics = user.getDayAnalytics();

        // startDate와 endDate 사이의 대화 통계만 필터링
        List<DayAnalytics> filteredAnalytics = dayAnalytics.stream()
                .filter(analytics -> analytics.getCreatedAt().isAfter(request.getStartTime()) && analytics.getCreatedAt().isBefore(request.getEndTime()))
                .toList();

        // TODO: GPT 호출 주간 요약 새엇ㅇ
        String emotionSummary = "감정 요약";
        String vocabularySummary = "어휘 요약";
        String wordCloudSummary = "워드클라우드 요약";
        return new WeeklyConversationResponse(filteredAnalytics, emotionSummary, vocabularySummary, wordCloudSummary);
    }


    // TODO: Kafka 구독, 대화 저장 , FAST API 호출,  GPT 호출 ( 대화 제목, 요약, 감정분석/워드클라우드/어휘력 설명 )
}
