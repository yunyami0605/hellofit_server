CREATE TABLE foods (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    food_code VARCHAR(50) NOT NULL,           -- 식품코드
    food_name VARCHAR(255) NOT NULL,          -- 식품명
    category VARCHAR(100),                    -- 식품대분류명
    rep_food_name VARCHAR(255),               -- 대표식품명
    kcal DECIMAL(8,2),                        -- 에너지(kcal)
    protein DECIMAL(8,2),                     -- 단백질(g)
    fat DECIMAL(8,2),                         -- 지방(g)
    carbs DECIMAL(8,2),                       -- 탄수화물(g)
    sugar DECIMAL(8,2),                       -- 당류(g)
    calcium DECIMAL(8,2),                     -- 칼슘(mg)
    sodium DECIMAL(8,2),                      -- 나트륨(mg)
    cholesterol DECIMAL(8,2),                 -- 콜레스테롤(mg)
    weight DECIMAL(8,2),                      -- 식품중량
    data_date DATE,                           -- 데이터기준일자
    UNIQUE INDEX idx_food_code (food_code),
    INDEX idx_category (category)
);
