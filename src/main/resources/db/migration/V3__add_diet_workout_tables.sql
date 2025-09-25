-- 추천 식단
CREATE TABLE diet_recommendations (
    id CHAR(36) NOT NULL PRIMARY KEY,
    user_id CHAR(36) NOT NULL,
    meal_type VARCHAR(20) NOT NULL, -- 아침/점심/저녁
    recommended_date DATE NOT NULL,
    source VARCHAR(20) NOT NULL, -- AI / USER
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,
    CONSTRAINT fk_diet_recommend_user FOREIGN KEY (user_id)
        REFERENCES users(id)
        ON DELETE CASCADE
);

-- 추천 식단 항목
CREATE TABLE diet_recommendation_items (
    id CHAR(36) NOT NULL PRIMARY KEY,
    recommendation_id CHAR(36) NOT NULL,
    food_name VARCHAR(100) NOT NULL,
    calories INT NULL,
    protein DECIMAL(5,2) NULL,
    fat DECIMAL(5,2) NULL,
    carbs DECIMAL(5,2) NULL,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,
    CONSTRAINT fk_diet_recommend_item FOREIGN KEY (recommendation_id)
        REFERENCES diet_recommendations(id)
        ON DELETE CASCADE
);

-- 실제 식단 기록
CREATE TABLE diet_logs (
    id CHAR(36) NOT NULL PRIMARY KEY,
    user_id CHAR(36) NOT NULL,
    meal_type VARCHAR(20) NOT NULL,
    log_date DATE NOT NULL,
    source VARCHAR(20) NOT NULL, -- AI / USER
    recommendation_id CHAR(36) NULL, -- 선택한 추천 ID (수정 가능)
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,
    CONSTRAINT fk_diet_log_user FOREIGN KEY (user_id)
        REFERENCES users(id)
        ON DELETE CASCADE,
    CONSTRAINT fk_diet_log_recommend FOREIGN KEY (recommendation_id)
        REFERENCES diet_recommendations(id)
        ON DELETE SET NULL,
    UNIQUE (user_id, log_date, meal_type) -- 하루 끼니당 중복 방지
);

-- 실제 식단 기록 항목(음식)
CREATE TABLE diet_log_items (
    id CHAR(36) NOT NULL PRIMARY KEY,
    diet_log_id CHAR(36) NOT NULL,
    food_name VARCHAR(100) NOT NULL,
    calories INT NULL,
    protein DECIMAL(5,2) NULL,
    fat DECIMAL(5,2) NULL,
    carbs DECIMAL(5,2) NULL,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,
    CONSTRAINT fk_diet_log_item FOREIGN KEY (diet_log_id)
        REFERENCES diet_logs(id)
        ON DELETE CASCADE
);

-- 추천 운동
CREATE TABLE workout_recommendations (
    id CHAR(36) NOT NULL PRIMARY KEY,
    user_id CHAR(36) NOT NULL,
    recommended_date DATE NOT NULL,
    source VARCHAR(20) NOT NULL, -- AI / USER
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,
    CONSTRAINT fk_workout_recommend_user FOREIGN KEY (user_id)
        REFERENCES users(id)
        ON DELETE CASCADE
);

-- 추천 상세 운동 항목
CREATE TABLE workout_recommendation_exercises (
    id CHAR(36) NOT NULL PRIMARY KEY,
    recommendation_id CHAR(36) NOT NULL,
    exercise_name VARCHAR(100) NOT NULL,
    sets INT NULL,
    repetitions INT NULL,
    duration_minutes INT NULL,
    calories_burned INT NULL,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,
    CONSTRAINT fk_workout_recommend_ex FOREIGN KEY (recommendation_id)
        REFERENCES workout_recommendations(id)
        ON DELETE CASCADE
);

-- 운동 기록
CREATE TABLE workout_logs (
    id CHAR(36) NOT NULL PRIMARY KEY,
    user_id CHAR(36) NOT NULL,
    log_date DATE NOT NULL,
    source VARCHAR(20) NOT NULL, -- AI / USER
    recommendation_id CHAR(36) NULL,
    total_minutes INT NULL,
    total_calories_burned INT NULL,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,
    CONSTRAINT fk_workout_log_user FOREIGN KEY (user_id)
        REFERENCES users(id)
        ON DELETE CASCADE,
    CONSTRAINT fk_workout_log_recommend FOREIGN KEY (recommendation_id)
        REFERENCES workout_recommendations(id)
        ON DELETE SET NULL,
    UNIQUE (user_id, log_date) -- 하루당 한 기록
);

-- 운동 기록 항목
CREATE TABLE workout_log_exercises (
    id CHAR(36) NOT NULL PRIMARY KEY,
    workout_log_id CHAR(36) NOT NULL,
    exercise_name VARCHAR(100) NOT NULL,
    sets INT NULL,
    repetitions INT NULL, -- reps 대신 repetitions
    duration_minutes INT NULL,
    calories_burned INT NULL,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,
    CONSTRAINT fk_workout_log_ex FOREIGN KEY (workout_log_id)
        REFERENCES workout_logs(id)
        ON DELETE CASCADE
);
