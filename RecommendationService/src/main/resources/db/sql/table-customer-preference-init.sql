ALTER TABLE customer_preferences
    MODIFY departure_location VARCHAR (100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
-- Dumping data for table recommendation.customer_preferences: ~12 rows (approximately)
INSERT INTO `customer_preferences` (`pre_id`, `cus_id`, `price`, `max_duration`, `start_date`, `type_tour`, `region`,
                                    `accommodation_quality`, `transportation_mode`, `departure_location`)
VALUES (1, 1, 6000000, 7, '2024-09-19', 'RESORT', 'NORTH', 'FIVE_STAR_HOTEL', 'AIRPLANE', 'Hà Nội'),
       (2, 1, 850, 5, '2024-03-15', 'CULTURE', 'NORTH', 'FIVE_STAR_HOTEL', 'BUS', 'Hồ Chí Minh'),
       (3, 1, 600, 4, '2024-02-20', 'RESORT', 'CENTRAL', 'FOUR_STAR_HOTEL', 'TRAIN', 'Đà Nẵng'),
       (4, 2, 6000000, 10, '2024-05-01', 'ECOLOGY', 'SOUTH', 'RESORT', 'PRIVATE_CAR', 'Hải Phòng'),
       (5, 2, 750, 6, '2024-06-18', 'RESORT', 'CENTRAL', 'THREE_STAR_HOTEL', 'BUS', 'Hồ Chí Minh'),
       (6, 2, 1100, 8, '2024-12-22', 'DISCOVER', 'WEST', 'FIVE_STAR_HOTEL', 'PRIVATE_CAR', 'Nha Trang'),
       (7, 3, 950, 5, '2024-04-10', 'SPORT', 'NORTH', 'RESORT', 'AIRPLANE', 'Bình Dương'),
       (8, 3, 6000000, 9, '2024-08-05', 'RESORT', 'SOUTH', 'FOUR_STAR_HOTEL', 'TRAIN', 'Hạ Long'),
       (9, 3, 650, 4, '2024-09-12', 'VENTURE', 'CENTRAL', 'THREE_STAR_HOTEL', 'PRIVATE_CAR', 'Cần Thơ'),
       (10, 3, 6000000, 7, '2024-12-22', 'RESORT', 'NORTH', 'FIVE_STAR_HOTEL', 'BUS', 'Huế'),
       (11, 7, 6000000, 7, '2024-11-29', 'RESORT', 'CENTRAL', 'FIVE_STAR_HOTEL', 'TRAIN', 'Hồ Chí Minh'),
       (12, 7, 6000000, 20, '2024-11-29', 'RESORT', 'CENTRAL', 'FIVE_STAR_HOTEL', 'TRAIN', 'Hồ Chí Minh'),
       (13, 7, 6000000, 6, '2024-11-29', 'DISCOVER', 'CENTRAL', 'FIVE_STAR_HOTEL', 'TRAIN', 'Hồ Chí Minh'),
       (14, 7, 6000000, 10, '2024-11-30', NULL, NULL, NULL, NULL, NULL);

