-- Users
CREATE TABLE users (
    id CHAR(36) NOT NULL PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    nickname VARCHAR(12) NOT NULL,
    is_privacy_agree BOOLEAN NOT NULL,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,
    deleted_at DATETIME(6) NULL
);

-- User Profiles
CREATE TABLE user_profiles (
    user_id CHAR(36) NOT NULL PRIMARY KEY,
    age_group VARCHAR(20) NOT NULL,
    gender VARCHAR(20) NOT NULL,
    height DECIMAL(5,2) NOT NULL,
    weight DECIMAL(5,2) NOT NULL,
    sleep_minutes INT NULL,
    exercise_minutes INT NULL,
    CONSTRAINT fk_user_profile_user FOREIGN KEY (user_id)
        REFERENCES users(id)
        ON DELETE CASCADE
);

-- User Profile Forbidden Foods
CREATE TABLE user_profile_forbidden_food (
    user_id CHAR(36) NOT NULL,
    food VARCHAR(100) NOT NULL,
    CONSTRAINT fk_upf_user FOREIGN KEY (user_id)
        REFERENCES user_profiles(user_id)
        ON DELETE CASCADE
);

-- Refresh Tokens
CREATE TABLE refresh_tokens (
    user_id CHAR(36) NOT NULL PRIMARY KEY,
    token VARCHAR(255) NOT NULL,
    CONSTRAINT fk_refresh_token_user FOREIGN KEY (user_id)
        REFERENCES users(id)
        ON DELETE CASCADE
);

-- Posts
CREATE TABLE posts (
    id CHAR(36) NOT NULL PRIMARY KEY,
    title VARCHAR(80) NOT NULL,
    content TEXT NOT NULL,
    view_count INT NOT NULL DEFAULT 0,
    user_id CHAR(36) NOT NULL,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,
    deleted_at DATETIME(6) NULL,
    CONSTRAINT fk_post_user FOREIGN KEY (user_id)
        REFERENCES users(id)
        ON DELETE CASCADE
);

-- Comments
CREATE TABLE comments (
    id CHAR(36) NOT NULL PRIMARY KEY,
    post_id CHAR(36) NOT NULL,
    user_id CHAR(36) NOT NULL,
    parent_id CHAR(36) NULL,
    target_id CHAR(36) NULL,
    content TEXT NOT NULL,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,
    deleted_at DATETIME(6) NULL,
    CONSTRAINT fk_comment_post FOREIGN KEY (post_id)
        REFERENCES posts(id)
        ON DELETE CASCADE,
    CONSTRAINT fk_comment_user FOREIGN KEY (user_id)
        REFERENCES users(id)
        ON DELETE CASCADE,
    CONSTRAINT fk_comment_parent FOREIGN KEY (parent_id)
        REFERENCES comments(id)
        ON DELETE CASCADE,
    CONSTRAINT fk_comment_target FOREIGN KEY (target_id)
        REFERENCES comments(id)
        ON DELETE CASCADE
);

-- Likes
CREATE TABLE likes (
    id CHAR(36) NOT NULL PRIMARY KEY,
    user_id CHAR(36) NOT NULL,
    target_type VARCHAR(20) NOT NULL,
    target_id CHAR(36) NOT NULL,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,
    UNIQUE (user_id, target_type, target_id),
    CONSTRAINT fk_like_user FOREIGN KEY (user_id)
        REFERENCES users(id)
        ON DELETE CASCADE
);

-- Images
CREATE TABLE images (
    id CHAR(36) NOT NULL PRIMARY KEY,
    object_key VARCHAR(255) NOT NULL,
    target_type VARCHAR(20) NOT NULL,
    target_id CHAR(36) NOT NULL,
    sort_order INT NULL,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,
    deleted_at DATETIME(6) NULL
);

-- Diet Recommendations (추천 헤더)
CREATE TABLE diet_recommendations (
    id CHAR(36) NOT NULL PRIMARY KEY,
    user_id CHAR(36) NOT NULL,
    meal_type VARCHAR(20) NOT NULL, -- 아침/점심/저녁
    recommended_date DATE NOT NULL,
    source VARCHAR(20) NOT NULL, -- AI / USER
    created_at DATETIME(6) NOT NULL,
    CONSTRAINT fk_diet_recommend_user FOREIGN KEY (user_id)
        REFERENCES users(id)
        ON DELETE CASCADE
);

-- Diet Recommendation Items (추천 상세)
CREATE TABLE diet_recommendation_items (
    id CHAR(36) NOT NULL PRIMARY KEY,
    recommendation_id CHAR(36) NOT NULL,
    food_name VARCHAR(100) NOT NULL,
    calories INT NULL,
    protein DECIMAL(5,2) NULL,
    fat DECIMAL(5,2) NULL,
    carbs DECIMAL(5,2) NULL,
    CONSTRAINT fk_diet_recommend_item FOREIGN KEY (recommendation_id)
        REFERENCES diet_recommendations(id)
        ON DELETE CASCADE
);

-- Diet Logs (실제 기록)
CREATE TABLE diet_logs (
    id CHAR(36) NOT NULL PRIMARY KEY,
    user_id CHAR(36) NOT NULL,
    meal_type VARCHAR(20) NOT NULL,
    log_date DATE NOT NULL,
    source VARCHAR(20) NOT NULL, -- AI / USER
    recommendation_id CHAR(36) NULL, -- 선택한 추천 ID (수정 가능)
    created_at DATETIME(6) NOT NULL,
    CONSTRAINT fk_diet_log_user FOREIGN KEY (user_id)
        REFERENCES users(id)
        ON DELETE CASCADE,
    CONSTRAINT fk_diet_log_recommend FOREIGN KEY (recommendation_id)
        REFERENCES diet_recommendations(id)
        ON DELETE SET NULL,
    UNIQUE (user_id, log_date, meal_type) -- 하루에 같은 끼니 중복 기록 방지
);

-- Diet Log Items (기록 상세)
CREATE TABLE diet_log_items (
    id CHAR(36) NOT NULL PRIMARY KEY,
    diet_log_id CHAR(36) NOT NULL,
    food_name VARCHAR(100) NOT NULL,
    calories INT NULL,
    protein DECIMAL(5,2) NULL,
    fat DECIMAL(5,2) NULL,
    carbs DECIMAL(5,2) NULL,
    CONSTRAINT fk_diet_log_item FOREIGN KEY (diet_log_id)
        REFERENCES diet_logs(id)
        ON DELETE CASCADE
);

-- Workout Recommendations (추천 루틴)
CREATE TABLE workout_recommendations (
    id CHAR(36) NOT NULL PRIMARY KEY,
    user_id CHAR(36) NOT NULL,
    recommended_date DATE NOT NULL,
    source VARCHAR(20) NOT NULL, -- AI / USER
    created_at DATETIME(6) NOT NULL,
    CONSTRAINT fk_workout_recommend_user FOREIGN KEY (user_id)
        REFERENCES users(id)
        ON DELETE CASCADE
);

-- Workout Recommendation Exercises (추천 상세)
CREATE TABLE workout_recommendation_exercises (
    id CHAR(36) NOT NULL PRIMARY KEY,
    recommendation_id CHAR(36) NOT NULL,
    exercise_name VARCHAR(100) NOT NULL,
    sets INT NULL,
    repetitions INT NULL,
    duration_minutes INT NULL,
    calories_burned INT NULL,
    CONSTRAINT fk_workout_recommend_ex FOREIGN KEY (recommendation_id)
        REFERENCES workout_recommendations(id)
        ON DELETE CASCADE
);

-- Workout Logs (실제 기록)
CREATE TABLE workout_logs (
    id CHAR(36) NOT NULL PRIMARY KEY,
    user_id CHAR(36) NOT NULL,
    log_date DATE NOT NULL,
    source VARCHAR(20) NOT NULL, -- AI / USER
    recommendation_id CHAR(36) NULL, -- 선택한 추천 ID (수정 가능)
    total_minutes INT NULL,
    total_calories_burned INT NULL,
    created_at DATETIME(6) NOT NULL,
    CONSTRAINT fk_workout_log_user FOREIGN KEY (user_id)
        REFERENCES users(id)
        ON DELETE CASCADE,
    CONSTRAINT fk_workout_log_recommend FOREIGN KEY (recommendation_id)
        REFERENCES workout_recommendations(id)
        ON DELETE SET NULL,
    UNIQUE (user_id, log_date) -- 하루 운동 중복 기록 방지
);

-- Workout Log Exercises (실제 기록 운동 상세)
CREATE TABLE workout_log_exercises (
    id CHAR(36) NOT NULL PRIMARY KEY,
    workout_log_id CHAR(36) NOT NULL,
    exercise_name VARCHAR(100) NOT NULL,
    sets INT NULL,
    repetitions INT NULL,
    duration_minutes INT NULL,
    calories_burned INT NULL,
    CONSTRAINT fk_workout_log_ex FOREIGN KEY (workout_log_id)
        REFERENCES workout_logs(id)
        ON DELETE CASCADE
);
