package com.e104.realtime.application;

import com.e104.realtime.common.exception.RestApiException;
import com.e104.realtime.common.status.StatusCode;
import com.e104.realtime.domain.ConversationAnalytics.ConversationAnalytics;
import com.e104.realtime.domain.ConversationAnalytics.WordCloud;
import com.e104.realtime.domain.DayAnalytics.DayAnalytics;
import com.e104.realtime.domain.DayAnalytics.DayWordCloud;
import com.e104.realtime.domain.User.User;
import com.e104.realtime.domain.WeekAnalytics.WeekAnalytics;
import com.e104.realtime.domain.WeekAnalytics.WeekWordCloud;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

@EnableScheduling  // 스케줄링 활성화
@Component
@RequiredArgsConstructor
public class ScheduledTask {

    private final RepoUtil repoUtil;
    private final BuilderUtil builderUtil;
    private final ChatService chatService;

    // 매일 밤 11시에 실행되도록 설정 (24시간 기준)
    @Transactional
    @Scheduled(cron = "0 0 23 * * ?", zone = "Asia/Seoul")
    public void runTask() {
        try {
            List<User> users = repoUtil.findAllUsers();
            LocalDate now = LocalDate.now();
            List<DayWordCloud> dayWordClouds = new ArrayList<>();
            HashMap<String, Integer> wordCloudMap = new HashMap<>();

            for (User user : users) {
                List<ConversationAnalytics> conversationAnalytics = user.getConversationAnalytics();
                // 현재 날짜의 해당하는 대화별 대화통계만 가져오기
                List<ConversationAnalytics> filteredAnalytics = conversationAnalytics.stream()
                        // LocalDataTime을 LocalDate로 변환
                        .filter(ca -> ca.getCreatedAt().toLocalDate().equals(now))
                        .toList();

                double vocabularyScore = 0.0;
                int happyScore = 0, sadScore = 0, angryScore = 0, amazingScore = 0, scaryScore = 0, loveScore = 0 ,conversationCount = 0;
                for (ConversationAnalytics ca : filteredAnalytics) {
                    vocabularyScore += ca.getVocabulary().getVocabularyScore();
                    happyScore += ca.getSentiment().getHappyScore();
                    sadScore += ca.getSentiment().getSadScore();
                    angryScore += ca.getSentiment().getAngryScore();
                    amazingScore += ca.getSentiment().getAmazingScore();
                    scaryScore += ca.getSentiment().getScaryScore();
                    loveScore += ca.getSentiment().getLoveScore();
                    conversationCount++;

                    List<WordCloud> wordClouds = ca.getWordClouds();
                    for (WordCloud wordCloud : wordClouds) {
                        // 워드 클라우드 저장
                        String word = wordCloud.getWord();
                        int count = wordCloud.getCount();
                        if (wordCloudMap.containsKey(word)) {
                            wordCloudMap.put(word, wordCloudMap.get(word) + count);
                        } else {
                            wordCloudMap.put(word, count);
                        }
                    }
                }
                happyScore /= conversationCount;
                loveScore /= conversationCount;
                sadScore /= conversationCount;
                angryScore /= conversationCount;
                amazingScore /= conversationCount;
                scaryScore /= conversationCount;
                vocabularyScore /= conversationCount;
                DayAnalytics dayAnalytics = builderUtil.buildDayAnalytics(vocabularyScore, happyScore, loveScore, sadScore, angryScore, amazingScore, scaryScore, conversationCount);
                // 해시맵을 이용하여 Day 워드 클라우드 리스트 생성
                for (String word : wordCloudMap.keySet()) {
                    DayWordCloud dayWordCloud = builderUtil.buildDayWordCloud(word, wordCloudMap.get(word));
                    dayWordClouds.add(dayWordCloud);
                }
                dayAnalytics.addDayWordClouds(dayWordClouds);
                user.addDayAnalytics(dayAnalytics);
                updateWeekAnalytics(user, dayAnalytics);
            }
        } catch (Exception e) {
            throw new RestApiException(StatusCode.INTERNAL_SERVER_ERROR, "스케줄링 작업 중 오류가 발생했습니다.");
        }
    }

    private void updateWeekAnalytics(User user, DayAnalytics dayAnalytics) {
        try {
            // 오늘이 몇년, 몇월, 몇주차 인지 찾기 (타겟주)
            LocalDate today = LocalDate.now();
            int year = today.getYear();
            int month = today.getMonthValue();
            WeekFields weekFields = WeekFields.of(Locale.KOREA);
            int week = today.get(weekFields.weekOfMonth());

            WeekAnalytics weekAnalytics = user.getWeekAnalytics().stream()
                    .filter(w -> w.getYear() == year && w.getMonth() == month && w.getWeek() == week)
                    .findFirst()
                    .orElse(null);

            List<DayAnalytics> filteredDayAnalytics = user.getDayAnalytics().stream()
                    .filter(d -> d.getCreatedAt().getYear() == year && d.getCreatedAt().getMonthValue() == month && d.getCreatedAt().get(weekFields.weekOfMonth()) == week)
                    .toList();


            HashMap<String, Integer> hashMap = new HashMap<>();
            if (weekAnalytics != null) {
                List<WeekWordCloud> weekWordClouds = weekAnalytics.getWeekWordClouds();
                for (WeekWordCloud weekWordCloud : weekWordClouds) {
                    hashMap.put(weekWordCloud.getWord(), weekWordCloud.getCount());
                }
            }
            List<DayWordCloud> dayWordClouds = dayAnalytics.getDayWordClouds();
            for (DayWordCloud dayWordCloud : dayWordClouds) {
                String word = dayWordCloud.getWord();
                int count = dayWordCloud.getCount();
                if (hashMap.containsKey(word)) {
                    hashMap.put(word, hashMap.get(word) + count);
                } else {
                    hashMap.put(word, count);
                }
            }
            List<WeekWordCloud> newWeekWordClouds = new ArrayList<>();
            for (String word : hashMap.keySet()) {
                WeekWordCloud weekWordCloud = builderUtil.buildWeekWordCloud(word, hashMap.get(word));
                newWeekWordClouds.add(weekWordCloud);
            }
            // 감정 요약
            String emotionSummary = chatService.summarizeEmotions(filteredDayAnalytics);
            // 어휘 요약
            String vocabularySummary = chatService.summarizeVocabulary(filteredDayAnalytics, user.getAge());
            // 워드 클라우드 요약
            String wordCloudSummary = chatService.summarizeWeekWordCloud(newWeekWordClouds);
            // 대화 횟수 요약
            String countSummary = chatService.summarizeConversationCount(filteredDayAnalytics);
            // 타겟주의 주간 통계가 없을 경우 생성, 있으면 업데이트
            if (weekAnalytics == null) {
                weekAnalytics = builderUtil.buildWeekAnalytics(emotionSummary, vocabularySummary, wordCloudSummary, countSummary, year, month, week);
                user.addWeekAnalytics(weekAnalytics);
            } else {
                weekAnalytics.updateSummary(emotionSummary, vocabularySummary, wordCloudSummary, countSummary);
                weekAnalytics.clearWordClouds();
            }
            weekAnalytics.addWordClouds(newWeekWordClouds);
        } catch (Exception e) {
            throw new RestApiException(StatusCode.INTERNAL_SERVER_ERROR, "주간 통계 업데이트 중 오류가 발생했습니다.");
        }
    }
}
