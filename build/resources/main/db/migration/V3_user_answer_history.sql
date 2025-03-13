CREATE TABLE answer_history (
    answer_id SERIAL PRIMARY KEY,
    user_id INT REFERENCES user_table(id),
    question_id INT REFERENCES question_table(id),
    user_answer TEXT,
    is_correct BOOLEAN NOT NULL,
    created_at TIMESTAMP NOT NULL
);