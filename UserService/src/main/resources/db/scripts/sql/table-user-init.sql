ALTER TABLE users
    MODIFY full_name VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

INSERT IGNORE  INTO users(user_id, full_name ,email, registration_date)
VALUES
    ('1', 'Nguyễn Văn A', 'nguyenvana@gmail.com', CURRENT_DATE),
    ('2', 'Nguyễn Văn B', 'nguyenvanb@gmail.com', CURRENT_DATE),
    ('3', 'Nguyễn Văn C', 'nguyenvanc@gmail.com', CURRENT_DATE);