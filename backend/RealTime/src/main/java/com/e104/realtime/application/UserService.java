package com.e104.realtime.application;

import com.e104.realtime.common.exception.RestApiException;
import com.e104.realtime.common.status.StatusCode;
import com.e104.realtime.domain.entity.*;
import com.e104.realtime.dto.*;
import com.e104.realtime.mqtt.ChatMqttToWebSocketHandler;
import com.e104.realtime.redis.hash.Conversation;
import com.e104.realtime.redis.mapper.ConversationMapper;
import com.e104.realtime.redis.repository.ConversationRedisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.List;
import java.util.Locale;


@Service
@RequiredArgsConstructor
public class UserService {

    private final RepoUtil repoUtil;
    private final BuilderUtil builderUtil;
    private final ChatService chatService;
    private final ConversationRedisRepository conversationRedisRepository;
    private final ChatMqttToWebSocketHandler chatMqttToWebSocketHandler;

    private final ConversationMapper conversationMapper;

    // 로그인
    public LoginResponse login(String userId) {
        int userSeq = repoUtil.login(userId);
        User user = repoUtil.findUser(userSeq);
        return new LoginResponse(user.getUserSeq(), user.isNotFirstLogin());
    }

    // 유저정보 조회
    public UserResponse getUser(int userSeq) {
        User user = repoUtil.findUser(userSeq);
        return new UserResponse(user);
    }

    // 유저 정보 수정
    @Transactional
    public void updateUser(UserUpdateRequest request) {
        User user = repoUtil.findUser(request.getUserSeq());
        user.updateUserInfo(request.getName(), request.getAge(), request.getGender(), request.getFavorite());
        chatMqttToWebSocketHandler.sendSessionUpdate(user);
    }

    // 질문 등록
    @Transactional
    public void createQuestion(QuestionCreateRequest request) {
        User user = repoUtil.findUser(request.getUserSeq());
        Question question = builderUtil.buildQuestion(request.getContent());
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
        List<ConversationContent> contents = user.getConversationContents();
        ConversationAnalytics analytics = user.getConversationAnalytics(conversationSeq);
        return new ConversationDetailResponse(analytics, contents);
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
        List<WeekAnalytics> weekAnalyticses = user.getWeekAnalytics();

        // 오늘이 몇년, 몇월, 몇주차 인지 찾기
        LocalDate today = LocalDate.now();
        int year = today.getYear();
        int month = today.getMonthValue();
        WeekFields weekFields = WeekFields.of(Locale.KOREA);
        int week = today.get(weekFields.weekOfMonth());

        // 현재 연도, 월, 주차에 맞는 주간 대화 통계 조회
        WeekAnalytics weekAnalytics = weekAnalyticses.stream()
                .filter(w -> w.getYear() == year && w.getMonth() == month && w.getWeek() == week)
                .findFirst()
                .orElse(null);
        List<DayAnalytics> filteredDayAnalytics = user.getDayAnalytics().stream()
                .filter(d -> d.getCreatedAt().getYear() == year && d.getCreatedAt().getMonthValue() == month && d.getCreatedAt().get(weekFields.weekOfMonth()) == week)
                .toList();

        if (weekAnalytics == null) {
            throw new RestApiException(StatusCode.NO_CONTENT, "주간 대화 통계가 존재하지 않습니다.");
        }
        return new WeeklyConversationResponse(weekAnalytics, filteredDayAnalytics);
    }


    public void bufferConversation(Conversation conversation) {
        conversationRedisRepository.save(conversation);
    }

    @Transactional
    public void saveConversation(int userSeq) {
        List<Conversation> conversations = conversationRedisRepository.findAllByUserSeq(userSeq);
        List<ConversationContent> conversationContents = conversations.stream().map(conversationMapper::toConversationContent).toList();

        // 대화 내용 저장할때 부모의 질문 활성화 되어있고, 아이의 대답이 완료되었다면 응답에도 저장해야함.
        User user = repoUtil.findUser(userSeq);
        List<Question> questions = user.getQuestions();
        Question question = questions.get(questions.size() - 1);
        boolean isActive = question.isActive();
        boolean isAnswered = question.isAnswered();
        if(isActive && isAnswered) {
            String content = conversations.get(1).getContent(); // 아이의 제일 첫번째 대답을 뽑아내야함
            Answer answer = builderUtil.buildAnswer(content);
            question.addAnswer(answer);
        }
        user.addConversationContents(conversationContents);

        // TODO: FAST API에서 대화 제목, 대화 내용요약, 감정분석, 워드클라우드, 어휘력 가져오기 (WebClient, WebFlux?)
        // GPT에서 감정분석, 워드클라우드, 어휘력 설명 가져오기
        ConversationAnalytics conversationAnalytics = null;
        user.addConversationAnalytics(conversationAnalytics);

        // Redis 대화 삭제
        conversationRedisRepository.deleteAllByUserSeq(userSeq);
    }
}
