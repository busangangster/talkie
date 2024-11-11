package com.e104.realtime.application;

import com.e104.realtime.common.exception.RestApiException;
import com.e104.realtime.common.status.StatusCode;
import com.e104.realtime.domain.ConversationAnalytics.*;
import com.e104.realtime.domain.DayAnalytics.DayAnalytics;
import com.e104.realtime.domain.User.Answer;
import com.e104.realtime.domain.User.Question;
import com.e104.realtime.domain.User.User;
import com.e104.realtime.domain.WeekAnalytics.WeekAnalytics;
import com.e104.realtime.dto.*;
import com.e104.realtime.redis.hash.Conversation;
import com.e104.realtime.redis.mapper.ConversationMapper;
import com.e104.realtime.redis.repository.ConversationRedisRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


@Service
@RequiredArgsConstructor
public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);
    private final RepoUtil repoUtil;
    private final BuilderUtil builderUtil;
    private final ChatService chatService;
    private final ConversationRedisRepository conversationRedisRepository;

    private final ConversationMapper conversationMapper;

    private final RestTemplate restTemplate;

    @Value("${fastapi.url}")
    private String fastApiUrl;

    // 유저정보 조회
    public UserResponse getUser(int userSeq) {
        User user = repoUtil.findUser(userSeq);
        return new UserResponse(user);
    }

    public User getUserEntity(int userSeq) {
        return repoUtil.findUser(userSeq);
    }

    // 유저 정보 수정
    @Transactional
    public void updateUser(UserUpdateRequest request) {
        User user = repoUtil.findUser(request.getUserSeq());
        user.updateUserInfo(request.getName(), request.getBirth(), request.getGender(), request.getFavorite(), request.getRemark());
    }

    // 질문 등록
    @Transactional
    public void createQuestion(QuestionCreateRequest request) {
        User user = repoUtil.findUser(request.getUserSeq());
        Question question = builderUtil.buildQuestion(request.getContent());
        user.addQuestion(question);
    }


    // 질문 등록 가능한지 여부
    public boolean isQuestionAvailable(int userSeq) {
        User user = repoUtil.findUser(userSeq);
        return user.isQuestionAvailable();
    }

    // 질문 삭제
    @Transactional
    public void deleteQuestion(int userSeq) {
        User user = repoUtil.findUser(userSeq);
        user.removeQuestion();
    }

    // 질문 수정
    @Transactional
    public void updateQuestion(QuestionUpdateRequest request) {
        User user = repoUtil.findUser(request.getUserSeq());
        List<Question> questions = user.getQuestions();
        Question question = questions.get(questions.size() - 1);
        question.updateQuestion(request.getContent());
    }

    // 질문 및 응답 조회
    public List<QuestionAndResponse> getQuestionAndAnswerList(int userSeq) {
        User user = repoUtil.findUser(userSeq);
        List<Question> questions = user.getQuestions();

        return questions.stream()
                .map(QuestionAndResponse::new)
                .toList();
    }

    // 일자별 대화 목록 조회
    public ConversationListResponse getConversationList(int userSeq, LocalDate day) {
        User user = repoUtil.findUser(userSeq);
        return new ConversationListResponse(user, day);
    }

    // 대화 상세 조회
    public ConversationDetailResponse getConversationDetail(int userSeq, int conversationSeq) {
        User user = repoUtil.findUser(userSeq);
        ConversationAnalytics analytics = user.getConversationAnalytics(conversationSeq);
        return new ConversationDetailResponse(analytics);
    }

    // 대화 내용 요약 조회
    public ConversationSummaryResponse getConversationSummary(int userSeq, int conversationSeq) {
        User user = repoUtil.findUser(userSeq);
        ConversationAnalytics conversationAnalytics = user.getConversationAnalytics(conversationSeq);
        return new ConversationSummaryResponse(conversationAnalytics.getConversationSummary());
    }

    // 주별 대화 통계 조회
    public WeeklyConversationResponse getWeeklyConversation(int userSeq, LocalDate startDay, LocalDate endDay) {
        User user = repoUtil.findUser(userSeq);
        List<WeekAnalytics> weekAnalyticses = user.getWeekAnalytics();

        // 오늘이 몇년, 몇월, 몇주차 인지 찾기
        int startYear = startDay.getYear();
        int endYear = endDay.getYear();
        int startMonth = startDay.getMonthValue();
        int endMonth = endDay.getMonthValue();
        WeekFields weekFields = WeekFields.of(Locale.KOREA);
        int startWeek = startDay.get(weekFields.weekOfMonth());
        int endWeek = endDay.get(weekFields.weekOfMonth());
        if (startYear != endYear || startMonth != endMonth || startWeek != endWeek) {
            throw new RestApiException(StatusCode.BAD_REQUEST, "시작 날짜와 끝 날짜가 같은 년도,월,주차에 있어야 합니다.");
        }

        // 현재 연도, 월, 주차에 맞는 주간 대화 통계 조회
        WeekAnalytics weekAnalytics = weekAnalyticses.stream()
                .filter(w -> w.getYear() == endYear && w.getMonth() == endMonth && w.getWeek() == endWeek)
                .findFirst()
                .orElse(null);
        List<DayAnalytics> filteredDayAnalytics = user.getDayAnalytics().stream()
                .filter(d -> d.getCreatedAt().getYear() == endYear && d.getCreatedAt().getMonthValue() == endMonth && d.getCreatedAt().get(weekFields.weekOfMonth()) == endWeek)
                .toList();

        if (weekAnalytics == null) {
            throw new RestApiException(StatusCode.NO_CONTENT, "주간 대화 통계가 존재하지 않습니다.");
        }
        return new WeeklyConversationResponse(weekAnalytics, filteredDayAnalytics);
    }

    @Transactional
    public void bufferConversation(Conversation conversation) {
        log.info("레디스로 대화 기록: {}", conversation);
        conversationRedisRepository.save(conversation);
    }

    public boolean isTalkingNow(int userSeq) {
        return !conversationRedisRepository.findAllByUserSeq(userSeq).isEmpty();
    }

    @Transactional
    public void saveConversation(int userSeq) {
        List<Conversation> conversations = conversationRedisRepository.findAllByUserSeq(userSeq);
        log.info("레디스로부터 가져온 데이터: {}", conversations);
        // 아이가 한번도 대답하지 않음
        List<ConversationContent> conversationContents = conversations.stream().map(conversationMapper::toConversationContent).toList();
        log.info("대화 데이터: {}", conversationContents);

        // 대화 내용 저장할때 부모의 질문 활성화 되어있고, 아이의 대답이 완료되었다면 응답에도 저장해야함.
        User user = repoUtil.findUser(userSeq);
        List<Question> questions = user.getQuestions();
        Question question = questions.get(questions.size() - 1);
        boolean isActive = question.isActive();
        boolean isAnswered = question.isAnswered();
        if (isActive && isAnswered) {
            if (conversations.size() <= 1) {
                question.updateAnswerd(false);
                return;
            }
            String content = conversations.get(1).getContent(); // 아이의 제일 첫번째 대답을 뽑아내야함
            Answer answer = builderUtil.buildAnswer(content);
            question.addAnswer(answer);
        }
        if (conversations.size() <= 1) {
            log.info("대화 기록이 1 이하입니다. 저장을 건너뜁니다.");
            return;
        }

        List<String> conversationOfKid = conversationContents.stream().filter(ConversationContent::isAnswer).map(ConversationContent::getContent).toList();
        FastApiWordCloudResponse fastApiWordCloudResponse = fetchPostRequest(conversationOfKid, FastApiWordCloudResponse.class, "/wordcloud");
        FastApiSentimentResponse fastApiSentimentResponse = fetchPostRequest(conversationOfKid, FastApiSentimentResponse.class, "/emotion");
        FastApiVocabularyResponse fastApiVocabularyResponse = fetchPostRequest(conversationOfKid, FastApiVocabularyResponse.class, "/vocabulary");
        List<WordCloud> wordClouds = new ArrayList<>();
        for (FastApiWordCloudResponse.wordCloud wordCloud : fastApiWordCloudResponse.getWordCloud()) {
            wordClouds.add(WordCloud.builder().word(wordCloud.getWord()).count(wordCloud.getCount()).build());
        }
        Vocabulary vocabulary = Vocabulary.builder().vocabularyScore(fastApiVocabularyResponse.getMorph_analyze()).build();
        Sentiment sentiment = Sentiment.builder()
                .happyScore(fastApiSentimentResponse.getPredictions().getHappyScore())
                .loveScore(fastApiSentimentResponse.getPredictions().getLoveScore())
                .sadScore(fastApiSentimentResponse.getPredictions().getSadScore())
                .scaryScore(fastApiSentimentResponse.getPredictions().getScaryScore())
                .angryScore(fastApiSentimentResponse.getPredictions().getAngryScore())
                .amazingScore(fastApiSentimentResponse.getPredictions().getAmazingScore())
                .build();

        // 대화제목, 대화 내용 요약 가져오기
        String TitleAndContentSummary = chatService.getConversationSummary(conversationContents);
        String conversationContentSummary = null;
        String title = null;
        // "요약:"과 "제목:"을 기준으로 분리
        String[] parts = TitleAndContentSummary.split("제목:");
        if (parts.length == 2) {
            String[] summaryParts = parts[0].split("요약:");
            if (summaryParts.length == 2) {
                conversationContentSummary = summaryParts[1].trim(); // 요약 부분
            }
            title = parts[1].trim(); // 제목 부분
        }

        // 감정분석, 워드클라우드, 어휘력 설명 가져오기
        String wordCloudSummary = chatService.summarizeConversationWordCloud(wordClouds);
        String emotionSummary = chatService.summarizeConversationEmotion(sentiment);
        String vocabularySummary = chatService.summarizeConversationVocabulary(vocabulary, user.getBirth());

        // 대화 통계 생성 및 저장
        ConversationAnalytics conversationAnalytics = builderUtil.buildConversationAnalytics(title, emotionSummary, vocabularySummary, wordCloudSummary);
        ConversationSummary conversationSummary = builderUtil.buildConversationSummary(conversationContentSummary);
        conversationAnalytics.addConversationSummary(conversationSummary);
        conversationAnalytics.addSentiment(sentiment);
        conversationAnalytics.addVocabulary(vocabulary);
        conversationAnalytics.addWordCloud(wordClouds);
        conversationAnalytics.addConversationContent(conversationContents);
        user.addConversationAnalytics(conversationAnalytics);

        log.info("로그 저장! 로그 데이터: {}", conversationAnalytics);

        // Redis 대화 삭제
        conversationRedisRepository.deleteAllByUserSeq(userSeq);
    }

    private <T> T fetchPostRequest(List<String> conversationOfKid, Class<T> responseType, String path) {
        try {
            FastApiRequest request = new FastApiRequest(conversationOfKid);

            // HTTP 헤더 설정
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            // HTTP 엔티티 생성 (헤더와 요청 데이터 설정)
            HttpEntity<FastApiRequest> requestEntity = new HttpEntity<>(request, headers);

            // HTTP POST 요청 보내기
            String url = fastApiUrl + path;
            ResponseEntity<T> responseEntity = restTemplate.postForEntity(url, requestEntity, responseType);

            // 응답 값
            return responseEntity.getBody();
        } catch (Exception e) {
            throw new RestApiException(StatusCode.INTERNAL_SERVER_ERROR, "FastAPI 연결 실패");
        }
    }
}
