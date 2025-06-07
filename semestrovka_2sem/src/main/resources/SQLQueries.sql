-- 1. Таблица users (Пользователи)
CREATE TABLE users (
                       id BIGSERIAL PRIMARY KEY,
                       name VARCHAR(255) NOT NULL,
                       surname VARCHAR(255) NOT NULL,
                       email VARCHAR(255) NOT NULL UNIQUE,
                       password_hash VARCHAR(255) NOT NULL,
                       phone_number VARCHAR(12) UNIQUE,
                       role VARCHAR(10) NOT NULL CHECK (role IN ('STUDENT','TUTOR', 'ADMIN'))
);

-- 2. Таблица tutors (Репетиторы)
--    Связь One-to-One: один User → один Tutor
CREATE TABLE tutors (
                        id BIGSERIAL PRIMARY KEY,
                        user_id BIGINT NOT NULL UNIQUE,
                        experience INT DEFAULT 0 CHECK (experience >= 0),
                        description TEXT,
                        FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- 3. Таблица subjects (Предметы)
CREATE TABLE subjects (
                          id BIGSERIAL PRIMARY KEY,
                          name VARCHAR(255) NOT NULL UNIQUE
);

-- 4. Таблица-связка tutors_subjects (Many-to-Many Rep-Subject)
CREATE TABLE tutors_subjects (
                                 tutor_id BIGINT NOT NULL,
                                 subject_id BIGINT NOT NULL,
                                 PRIMARY KEY (tutor_id, subject_id),
                                 FOREIGN KEY (tutor_id) REFERENCES tutors(id) ON DELETE CASCADE,
                                 FOREIGN KEY (subject_id) REFERENCES subjects(id) ON DELETE CASCADE
);

-- 5. Таблица tutor_requests (Заявки/Объявления репетитора)
--    Связь One-to-Many: один Tutor → много TutorRequest
CREATE TABLE tutor_requests (
                                id BIGSERIAL PRIMARY KEY,
                                tutor_id BIGINT NOT NULL,
                                subject_id BIGINT NOT NULL,
                                price INT NOT NULL CHECK (price > 0),
                                duration INT NOT NULL CHECK (duration IN (45, 60, 90)),
                                description TEXT,
                                created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                FOREIGN KEY (tutor_id) REFERENCES tutors(id) ON DELETE CASCADE,
                                FOREIGN KEY (subject_id) REFERENCES subjects(id)
);

-- 6. Таблица student_responses (Отклики студентов)
--    Связь Many-to-One: много StudentResponse → один User (student)
--    Связь Many-to-One: много StudentResponse → одна TutorRequest
CREATE TABLE student_responses (
                                   id BIGSERIAL PRIMARY KEY,
                                   student_id BIGINT NOT NULL,
                                   request_id BIGINT NOT NULL,
                                   message TEXT NOT NULL,
                                   created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                   FOREIGN KEY (student_id) REFERENCES users(id) ON DELETE CASCADE,
                                   FOREIGN KEY (request_id) REFERENCES tutor_requests(id) ON DELETE CASCADE
);

-- 7. Дополнительные индексы для производительности
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_subjects_name ON subjects(name);
CREATE INDEX idx_tutor_requests_subject_id ON tutor_requests(subject_id);
CREATE INDEX idx_tutor_requests_tutor_id ON tutor_requests(tutor_id);
CREATE INDEX idx_student_responses_request_id ON student_responses(request_id);
CREATE INDEX idx_student_responses_student_id ON student_responses(student_id);
