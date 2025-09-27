package com.hellofit.hellofit_server.diet.recommendation.scheduler;

import com.hellofit.hellofit_server.diet.recommendation.service.DietRecommendationGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DietRecommendationScheduler {

    private final DietRecommendationGenerator generator;

    /**
     * 매일 00시에 전체 유저 식단 생성
     */
    @Scheduled(cron = "0 55 12 * * *")
//    @Scheduled(cron = "*/10 * * * * *")
    public void runDailyJob() {
        log.info("매일 자정 식단 생성 스케줄러 실행");
        generator.generateDailyDiets();
    }
}
