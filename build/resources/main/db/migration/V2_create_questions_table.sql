CREATE TABLE collection_sets (
    id SERIAL PRIMARY KEY,
    user_id INT REFERENCES user_table(id),
    name VARCHAR(100) NOT NULL UNIQUE
);

CREATE TABLE collections (
    id SERIAL PRIMARY KEY,
    collection_set_id INT REFERENCES collection_sets(id),
    generated_by_ai BOOLEAN,
    name VARCHAR(100) NOT NULL
);

CREATE TABLE questions (
    id SERIAL PRIMARY KEY,
    collection_id INT REFERENCES collections(id),
    question_text TEXT NOT NULL,
    correct_answer TEXT NOT NULL,
    description_text TEXT NULL
);