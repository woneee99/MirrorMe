package com.mirror.backend.api.scheduler;


import com.mirror.backend.api.dto.Event;
import com.mirror.backend.api.entity.TextSummarySchedule;
import com.mirror.backend.api.entity.GoogleOAuthToken;
import com.mirror.backend.api.repository.TextSummaryScheduleRepository;
import com.mirror.backend.api.repository.GoogleOAuthTokenRepository;
import com.mirror.backend.api.service.CalendarService;
import com.mirror.backend.api.service.OAuthService;
import com.mirror.backend.common.utils.ChatGptUtil;
import com.mirror.backend.common.utils.EtcUtil;
import com.mirror.backend.common.utils.TokenUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;

@Component
@RequiredArgsConstructor
public class TextSummaryCalendarScheduler {

    public final GoogleOAuthTokenRepository googleOAuthTokenRepository;
    public final TextSummaryScheduleRepository textSummaryScheduleRepository;

    public final CalendarService calendarService;
    public final OAuthService oAuthService;

    public final ChatGptUtil chatGptUtil;
    public final TokenUtil tokenUtil;

//    @Scheduled(cron = "30 * * * * ?") // Develop
    @Scheduled(cron = "0 0 0 * * ?") // deploy
    public void fetchRedisData() {

        System.out.println("------------Scheduler: Summery Calendar----------");

        Iterable<GoogleOAuthToken> googleOAuthToken= googleOAuthTokenRepository.findAll();
        Iterator<GoogleOAuthToken> iterator = googleOAuthToken.iterator();

        while (iterator.hasNext()) {
            GoogleOAuthToken userTokenInfo = iterator.next();

            String accessToken = userTokenInfo.getAccessToken();
            String refreshToken = userTokenInfo.getRefreshToken();

            // AccessToken의 유효성 검사, 만약 불일치시 재발급
            accessToken = tokenUtil.confirmAccessToken(accessToken, refreshToken);

            // 해당 유저의 Email을 조회
            String userEmail = oAuthService.getUserEmailFromAccessToken(accessToken);
            System.out.println("타겟유저: " + userEmail);
            // 2. 해당 UserToken으로 Calendar내역을 각각 가져온다
            Event event = calendarService.getMyCalendar(accessToken, "primary");
            String eventInTodayList = getUserEventInToday(event);
            if ( eventInTodayList.equals("")){
                System.out.println("오늘 일정이 없음");
                continue;
            }

            // 3. Gpt에게 해당 일정을 요약해달라는 요청을 보낸다
            String answer = getSummeryCalendarFromGPT(eventInTodayList);
            saveRedisSummeryCalendar(answer, userEmail);
        }

        System.out.println("------------ Finish Scheduler ----------");
    }

    public String getUserEventInToday(Event event){
        String userEventList;

        StringBuilder sb = new StringBuilder();
        LocalDate now = LocalDate.now();

        for( Event.Item item: event.getItems()) {
            String startTime = item.getStart().getDateTime();
            String endTime = item.getEnd().getDateTime();

            startTime = startTime == null ? item.getStart().getDate() : startTime.substring(0, 10);
            endTime = endTime == null ? item.getEnd().getDate() : endTime.substring(0, 10);
            DateTimeFormatter parser = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate localStartDate = LocalDate.parse(startTime, parser);
            LocalDate localEndDate = LocalDate.parse(endTime, parser);

            boolean chk = !now.isBefore(localStartDate) && !now.isAfter(localEndDate);
            if (chk) sb.append(item.getSummary() +", ");
        }
        userEventList = sb.toString();

        return userEventList;
    }

    // 3. 해당 Calendar내역을 3줄 요약하도록 GPT한테 요청한다
    public String  getSummeryCalendarFromGPT(String eventInTodayList){

        System.out.println("가져온 모든 이벤트: " + eventInTodayList);
        StringBuilder sb = new StringBuilder();
        sb.append("다음과 같은 일정들이 있습니다. 가장 중요한 것 3가지를 뽑아 요약해주세요.");
        sb.append("만약 3가지보다 적다면, 있는 만큼만 나열해주세요. ");
        sb.append("각각에 대하여 1. {요약내용}, 2. {요약내용}, 3. {요약내용} 형태로 정리해주세요.");
        sb.append(" 최대한 간략하게 정리해주세요. (한글 기준 각각 10자가 넘지않도록) ");


        sb.append(" // " + eventInTodayList + " // ");

        String answer = chatGptUtil.createMessage(sb.toString());

        return answer;
    }

    public void saveRedisSummeryCalendar(String summeryText, String userEmail){

        StringBuilder sb = new StringBuilder();
        sb.append("안녕하세요!, 오늘 일정이 있어요. " );
        sb.append(summeryText);
        sb.append(", 나머지는 App에서 확인해요!");

        TextSummarySchedule textSummarySchedule = TextSummarySchedule.builder()
                .userEmail(userEmail)
                .textSummarySchedule(sb.toString())
                .targetDay(EtcUtil.getTodayYYYYMMDD())
                .build();

        textSummaryScheduleRepository.save(textSummarySchedule);
    }
}
