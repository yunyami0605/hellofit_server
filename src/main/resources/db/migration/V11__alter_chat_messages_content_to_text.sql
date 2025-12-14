-- 확장: 어시스턴트 답변이 1000자 초과 가능하므로 TEXT로 변경
ALTER TABLE chat_messages
    MODIFY COLUMN content TEXT NOT NULL;


