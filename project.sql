-- 사용자
CREATE TABLE users(
  id VARCHAR(36) PRIMARY KEY,
  email VARCHAR(255) UNIQUE NOT NULL,
  password VARCHAR(255) NOT NULL,
  nickname VARCHAR(12) UNIQUE NOT NULL,
  is_privacy_agree BOOLEAN NOT NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL ON UPDATE CURRENT_TIMESTAMP,
  deleted_at TIMESTAMP NULL
)

-- rf 토큰
CREATE TABLE refresh_tokens(
  user_id VARCHAR(36) PRIMARY KEY,
  token VARCHAR(255) NOT NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL ON UPDATE CURRENT_TIMESTAMP,
  FOREIGN KEY (user_id) REFERENCES users(id)
)

-- 게시글
CREATE TABLE posts(
  id VARCHAR(36) PRIMARY KEY,
  title VARCHAR (80),
  content TEXT NOT NULL,
  view_count INT DEFAULT 0,
  user_id VARCHAR(36) NOT NULL,
  FOREIGN KEY (user_id) REFERENCES users(id),
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL ON UPDATE CURRENT_TIMESTAMP,
  deleted_at TIMESTAMP NULL
);

-- 댓글
CREATE TABLE comments(
  id VARCHAR(36) PRIMARY KEY,
  post_id VARCHAR(36) NOT NULL,
  user_id VARCHAR(36) NOT NULL,
  parent_id VARCHAR(36) NOT NULL,
  content TEXT NOT NULL,
  FOREIGN KEY (post_id) REFERENCES posts(id),
  FOREIGN KEY (user_id) REFERENCES users(id),
  FOREIGN KEY (parent_id) REFERENCES comments(id),

  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL ON UPDATE CURRENT_TIMESTAMP,
  deleted_at TIMESTAMP NULL
);

-- 좋아요
CREATE TABLE likes(
  id VARCHAR(36) PRIMARY KEY,
  user_id VARCHAR(36) NOT NULL,
  target_type VARCHAR(36) NOT NULL,
  target_id VARCHAR(36) NOT NULL,
  created_at TIMESTAMP NOT NULL,
  FOREIGN KEY (user_id) REFERENCES users(id)
)

-- 이미지 (폴리모픽 연관)
CREATE TABLE images(
  id VARCHAR(36) PRIMARY KEY,
  object_key VARCHAR(255) NOT NULL,
  target_type VARCHAR(20) NOT NULL,
  target_id VARCHAR(36) NOT NULL,
  sort_order INT DEFAULT 0,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL ON UPDATE CURRENT_TIMESTAMP,
  deleted_at TIMESTAMP NULL
)

-- user_profiles 테이블
CREATE TABLE user_profiles (
    user_id VARCHAR(36) NOT NULL,
    age_group VARCHAR(50) NOT NULL,
    gender VARCHAR(10) NOT NULL,
    height DOUBLE PRECISION NOT NULL,
    weight DOUBLE PRECISION NOT NULL,
    sleep_minutes INT,
    exercise_minutes INT,
    CONSTRAINT pk_user_profiles PRIMARY KEY (user_id),
    CONSTRAINT fk_user_profiles_user FOREIGN KEY (user_id)
        REFERENCES users (id)
        ON DELETE CASCADE
);

-- user_profile_forbidden_food 테이블
CREATE TABLE user_profile_forbidden_food (
    user_id VARCHAR(36) NOT NULL,
    food VARCHAR(100),
    CONSTRAINT fk_upf_user FOREIGN KEY (user_id)
        REFERENCES user_profiles (user_id)
        ON DELETE CASCADE
);
