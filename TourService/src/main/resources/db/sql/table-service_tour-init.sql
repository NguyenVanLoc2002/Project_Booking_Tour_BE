ALTER TABLE tour_service
    MODIFY included_service TEXT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
    MODIFY excluded_service TEXT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
INSERT INTO `tour_service` (`service_id`, `tour_id`, `included_service`, `excluded_service`)
VALUES (1, 1, 'Hướng dẫn viên, bữa sáng, xe đưa đón', 'Bảo hiểm, chi phí cá nhân'),
       (2, 2, 'Hướng dẫn viên, bữa sáng, xe đưa đón', 'Bảo hiểm, chi phí cá nhân'),
       (3, 3, 'Hướng dẫn viên, bữa sáng, xe đưa đón', 'Bảo hiểm, chi phí cá nhân'),
       (4, 4, 'Hướng dẫn viên, bữa sáng, xe đưa đón', 'Bảo hiểm, chi phí cá nhân'),
       (5, 5, 'Hướng dẫn viên, bữa sáng, xe đưa đón', 'Bảo hiểm, chi phí cá nhân'),
       (6, 6, 'Hướng dẫn viên, bữa sáng, xe đưa đón', 'Bảo hiểm, chi phí cá nhân'),
       (7, 7, 'Hướng dẫn viên, bữa sáng, xe đưa đón', 'Bảo hiểm, chi phí cá nhân'),
       (8, 8, 'Hướng dẫn viên, bữa sáng, xe đưa đón', 'Bảo hiểm, chi phí cá nhân'),
       (9, 9, 'Hướng dẫn viên, bữa sáng, xe đưa đón', 'Bảo hiểm, chi phí cá nhân'),
       (10, 10, 'Hướng dẫn viên, bữa sáng, xe đưa đón', 'Bảo hiểm, chi phí cá nhân'),
       (11, 11, 'Hướng dẫn viên, bữa sáng, xe đưa đón', 'Bảo hiểm, chi phí cá nhân'),
       (12, 12, 'Hướng dẫn viên, bữa sáng, xe đưa đón', 'Bảo hiểm, chi phí cá nhân'),
       (13, 13, 'Hướng dẫn viên, bữa sáng, xe đưa đón', 'Bảo hiểm, chi phí cá nhân'),
       (14, 14, 'Hướng dẫn viên, bữa sáng, xe đưa đón', 'Bảo hiểm, chi phí cá nhân'),
       (15, 15, 'Hướng dẫn viên, bữa sáng, xe đưa đón', 'Bảo hiểm, chi phí cá nhân'),
       (16, 16, 'Hướng dẫn viên, bữa sáng, xe đưa đón', 'Bảo hiểm, chi phí cá nhân'),
       (17, 17, 'Hướng dẫn viên, bữa sáng, xe đưa đón', 'Bảo hiểm, chi phí cá nhân'),
       (18, 18, 'Hướng dẫn viên, bữa sáng, xe đưa đón', 'Bảo hiểm, chi phí cá nhân'),
       (19, 19, 'Hướng dẫn viên, bữa sáng, xe đưa đón', 'Bảo hiểm, chi phí cá nhân'),
       (20, 20, 'Hướng dẫn viên, bữa sáng, xe đưa đón', 'Bảo hiểm, chi phí cá nhân'),
       (21, 21, 'Hướng dẫn viên, bữa sáng, xe đưa đón', 'Bảo hiểm, chi phí cá nhân'),
       (22, 22, 'Hướng dẫn viên, bữa sáng, xe đưa đón', 'Bảo hiểm, chi phí cá nhân'),
       (23, 23, 'Hướng dẫn viên, bữa sáng, xe đưa đón', 'Bảo hiểm, chi phí cá nhân'),
       (24, 24, 'Hướng dẫn viên, bữa sáng, xe đưa đón', 'Bảo hiểm, chi phí cá nhân'),
       (25, 25, 'Hướng dẫn viên, bữa trưa, vé tham quan', 'Bảo hiểm, chi phí cá nhân'),
       (26, 26, 'Hướng dẫn viên, bữa sáng, xe đưa đón', 'Bảo hiểm, chi phí cá nhân'),
       (27, 27, 'Hướng dẫn viên, bữa trưa, xe đưa đón', 'Bảo hiểm, chi phí cá nhân'),
       (28, 28, 'Hướng dẫn viên, bữa sáng, vé tham quan', 'Bảo hiểm, chi phí cá nhân'),
       (29, 29, 'Hướng dẫn viên, bữa tối, xe đưa đón', 'Bảo hiểm, chi phí cá nhân'),
       (30, 30, 'Hướng dẫn viên, bữa sáng, xe đưa đón', 'Bảo hiểm, chi phí cá nhân'),
       (31, 31, 'Hướng dẫn viên, bữa sáng, vé tham quan', 'Bảo hiểm, chi phí cá nhân'),
       (32, 32, 'Hướng dẫn viên, bữa trưa, xe đưa đón', 'Bảo hiểm, chi phí cá nhân'),
       (33, 33, 'Hướng dẫn viên, bữa sáng, vé tham quan', 'Bảo hiểm, chi phí cá nhân'),
       (34, 34, 'Hướng dẫn viên, bữa tối, xe đưa đón', 'Bảo hiểm, chi phí cá nhân'),
       (35, 35, 'Hướng dẫn viên, bữa sáng, xe đưa đón', 'Bảo hiểm, chi phí cá nhân'),
       (36, 36, 'Hướng dẫn viên, bữa sáng, vé tham quan', 'Bảo hiểm, chi phí cá nhân'),
       (37, 37, 'Hướng dẫn viên, bữa trưa, xe đưa đón', 'Bảo hiểm, chi phí cá nhân'),
       (38, 38, 'Hướng dẫn viên, bữa sáng, xe đưa đón', 'Bảo hiểm, chi phí cá nhân'),
       (39, 39, 'Hướng dẫn viên, bữa tối, vé tham quan', 'Bảo hiểm, chi phí cá nhân'),
       (40, 40, 'Hướng dẫn viên, bữa sáng, xe đưa đón', 'Bảo hiểm, chi phí cá nhân'),
       (41, 41, 'Hướng dẫn viên, bữa sáng, vé tham quan', 'Bảo hiểm, chi phí cá nhân'),
       (42, 42, 'Hướng dẫn viên, bữa trưa, xe đưa đón', 'Bảo hiểm, chi phí cá nhân'),
       (43, 43, 'Hướng dẫn viên, bữa tối, vé tham quan', 'Bảo hiểm, chi phí cá nhân'),
       (44, 44, 'Hướng dẫn viên, bữa sáng, xe đưa đón', 'Bảo hiểm, chi phí cá nhân'),
       (45, 45, 'Hướng dẫn viên, bữa trưa, vé tham quan', 'Bảo hiểm, chi phí cá nhân'),
       (46, 46, 'Hướng dẫn viên, bữa sáng, xe đưa đón', 'Bảo hiểm, chi phí cá nhân'),
       (47, 47, 'Hướng dẫn viên, bữa tối, vé tham quan', 'Bảo hiểm, chi phí cá nhân'),
       (48, 48, 'Hướng dẫn viên, bữa sáng, vé tham quan', 'Bảo hiểm, chi phí cá nhân'),
       (49, 49, 'Hướng dẫn viên, bữa trưa, xe đưa đón', 'Bảo hiểm, chi phí cá nhân'),
       (50, 50, 'Hướng dẫn viên, bữa tối, vé tham quan', 'Bảo hiểm, chi phí cá nhân'),
       (51, 51, 'Hướng dẫn viên, bữa sáng, xe đưa đón', 'Bảo hiểm, chi phí cá nhân'),
       (52, 52, 'Hướng dẫn viên, bữa trưa, vé tham quan', 'Bảo hiểm, chi phí cá nhân'),
       (53, 53, 'Hướng dẫn viên, bữa sáng, xe đưa đón', 'Bảo hiểm, chi phí cá nhân'),
       (54, 54, 'Hướng dẫn viên, bữa tối, vé tham quan', 'Bảo hiểm, chi phí cá nhân'),
       (55, 55, 'Hướng dẫn viên, bữa sáng, vé tham quan', 'Bảo hiểm, chi phí cá nhân'),
       (56, 56, 'Hướng dẫn viên, bữa trưa, xe đưa đón', 'Bảo hiểm, chi phí cá nhân'),
       (57, 57, 'Hướng dẫn viên, bữa sáng, xe đưa đón', 'Bảo hiểm, chi phí cá nhân'),
       (58, 58, 'Hướng dẫn viên, bữa tối, vé tham quan', 'Bảo hiểm, chi phí cá nhân'),
       (59, 59, 'Hướng dẫn viên, bữa sáng, vé tham quan', 'Bảo hiểm, chi phí cá nhân'),
       (60, 60, 'Hướng dẫn viên, bữa trưa, xe đưa đón', 'Bảo hiểm, chi phí cá nhân'),
       (61, 61, 'Hướng dẫn viên, bữa sáng, xe đưa đón', 'Bảo hiểm, chi phí cá nhân'),
       (62, 62, 'Hướng dẫn viên, bữa tối, vé tham quan', 'Bảo hiểm, chi phí cá nhân'),
       (63, 63, 'Hướng dẫn viên, bữa sáng, vé tham quan', 'Bảo hiểm, chi phí cá nhân'),
       (64, 64, 'Hướng dẫn viên, bữa trưa, xe đưa đón', 'Bảo hiểm, chi phí cá nhân'),
       (65, 65, 'Hướng dẫn viên, bữa sáng, xe đưa đón', 'Bảo hiểm, chi phí cá nhân'),
       (66, 66, 'Hướng dẫn viên, bữa tối, vé tham quan', 'Bảo hiểm, chi phí cá nhân'),
       (67, 67, 'Hướng dẫn viên, bữa sáng, vé tham quan', 'Bảo hiểm, chi phí cá nhân'),
       (68, 68, 'Hướng dẫn viên, bữa trưa, xe đưa đón', 'Bảo hiểm, chi phí cá nhân'),
       (69, 69, 'Hướng dẫn viên, bữa sáng, xe đưa đón', 'Bảo hiểm, chi phí cá nhân'),
       (70, 70, 'Hướng dẫn viên, bữa tối, vé tham quan', 'Bảo hiểm, chi phí cá nhân'),
       (71, 71, 'Hướng dẫn viên, bữa sáng, vé tham quan', 'Bảo hiểm, chi phí cá nhân'),
       (72, 72, 'Hướng dẫn viên, bữa trưa, xe đưa đón', 'Bảo hiểm, chi phí cá nhân'),
       (73, 73, 'Hướng dẫn viên, bữa sáng, xe đưa đón', 'Bảo hiểm, chi phí cá nhân'),
       (74, 74, 'Hướng dẫn viên, bữa tối, vé tham quan', 'Bảo hiểm, chi phí cá nhân'),
       (75, 75, 'Hướng dẫn viên, bữa sáng, vé tham quan', 'Bảo hiểm, chi phí cá nhân'),
       (76, 76, 'Hướng dẫn viên, bữa trưa, xe đưa đón', 'Bảo hiểm, chi phí cá nhân'),
       (77, 77, 'Hướng dẫn viên, bữa sáng, xe đưa đón', 'Bảo hiểm, chi phí cá nhân'),
       (78, 78, 'Hướng dẫn viên, bữa tối, vé tham quan', 'Bảo hiểm, chi phí cá nhân'),
       (79, 79, 'Hướng dẫn viên, bữa sáng, vé tham quan', 'Bảo hiểm, chi phí cá nhân'),
       (80, 80, 'Hướng dẫn viên, bữa trưa, xe đưa đón', 'Bảo hiểm, chi phí cá nhân'),
       (81, 81, 'Hướng dẫn viên, bữa sáng, xe đưa đón', 'Bảo hiểm, chi phí cá nhân'),
       (82, 82, 'Hướng dẫn viên, bữa tối, vé tham quan', 'Bảo hiểm, chi phí cá nhân'),
       (83, 83, 'Hướng dẫn viên, bữa sáng, vé tham quan', 'Bảo hiểm, chi phí cá nhân'),
       (84, 84, 'Hướng dẫn viên, bữa trưa, xe đưa đón', 'Bảo hiểm, chi phí cá nhân'),
       (85, 85, 'Hướng dẫn viên, bữa sáng, xe đưa đón', 'Bảo hiểm, chi phí cá nhân'),
       (86, 86, 'Hướng dẫn viên, bữa tối, vé tham quan', 'Bảo hiểm, chi phí cá nhân'),
       (87, 87, 'Hướng dẫn viên, bữa sáng, vé tham quan', 'Bảo hiểm, chi phí cá nhân'),
       (88, 88, 'Hướng dẫn viên, bữa trưa, xe đưa đón', 'Bảo hiểm, chi phí cá nhân'),
       (89, 89, 'Hướng dẫn viên, bữa sáng, xe đưa đón', 'Bảo hiểm, chi phí cá nhân'),
       (90, 90, 'Hướng dẫn viên, bữa tối, vé tham quan', 'Bảo hiểm, chi phí cá nhân'),
       (91, 91, 'Hướng dẫn viên, bữa sáng, vé tham quan', 'Bảo hiểm, chi phí cá nhân'),
       (92, 92, 'Hướng dẫn viên, bữa trưa, xe đưa đón', 'Bảo hiểm, chi phí cá nhân'),
       (93, 93, 'Hướng dẫn viên, bữa sáng, xe đưa đón', 'Bảo hiểm, chi phí cá nhân'),
       (94, 94, 'Hướng dẫn viên, bữa tối, vé tham quan', 'Bảo hiểm, chi phí cá nhân'),
       (95, 95, 'Hướng dẫn viên, bữa sáng, vé tham quan', 'Bảo hiểm, chi phí cá nhân'),
       (96, 96, 'Hướng dẫn viên, bữa trưa, xe đưa đón', 'Bảo hiểm, chi phí cá nhân'),
       (97, 97, 'Hướng dẫn viên, bữa sáng, xe đưa đón', 'Bảo hiểm, chi phí cá nhân'),
       (98, 98, 'Hướng dẫn viên, bữa tối, vé tham quan', 'Bảo hiểm, chi phí cá nhân'),
       (99, 99, 'Hướng dẫn viên, bữa sáng, vé tham quan', 'Bảo hiểm, chi phí cá nhân'),
       (100, 100, 'Hướng dẫn viên, bữa trưa, xe đưa đón', 'Bảo hiểm, chi phí cá nhân'),
       (101, 101, 'Hướng dẫn viên, bữa sáng, xe đưa đón', 'Bảo hiểm, chi phí cá nhân'),
       (102, 102, 'Hướng dẫn viên, bữa tối, vé tham quan', 'Bảo hiểm, chi phí cá nhân'),
       (103, 103, 'Hướng dẫn viên, bữa sáng, vé tham quan', 'Bảo hiểm, chi phí cá nhân'),
       (104, 104, 'Hướng dẫn viên, bữa trưa, xe đưa đón', 'Bảo hiểm, chi phí cá nhân'),
       (105, 105, 'Hướng dẫn viên, bữa sáng, xe đưa đón', 'Bảo hiểm, chi phí cá nhân'),
       (106, 106, 'Hướng dẫn viên, bữa tối, vé tham quan', 'Bảo hiểm, chi phí cá nhân'),
       (107, 107, 'Hướng dẫn viên, bữa sáng, vé tham quan', 'Bảo hiểm, chi phí cá nhân'),
       (108, 108, 'Hướng dẫn viên, bữa trưa, xe đưa đón', 'Bảo hiểm, chi phí cá nhân'),
       (109, 109, 'Hướng dẫn viên, bữa sáng, xe đưa đón', 'Bảo hiểm, chi phí cá nhân'),
       (110, 110, 'Hướng dẫn viên, bữa tối, vé tham quan', 'Bảo hiểm, chi phí cá nhân'),
       (111, 111, 'Hướng dẫn viên, bữa sáng, vé tham quan', 'Bảo hiểm, chi phí cá nhân'),
       (112, 112, 'Hướng dẫn viên, bữa trưa, xe đưa đón', 'Bảo hiểm, chi phí cá nhân'),
       (113, 113, 'Hướng dẫn viên, bữa sáng, xe đưa đón', 'Bảo hiểm, chi phí cá nhân'),
       (114, 114, 'Hướng dẫn viên, bữa tối, vé tham quan', 'Bảo hiểm, chi phí cá nhân'),
       (115, 115, 'Hướng dẫn viên, bữa sáng, vé tham quan', 'Bảo hiểm, chi phí cá nhân'),
       (116, 116, 'Hướng dẫn viên, bữa trưa, xe đưa đón', 'Bảo hiểm, chi phí cá nhân'),
       (117, 117, 'Hướng dẫn viên, bữa sáng, xe đưa đón', 'Bảo hiểm, chi phí cá nhân'),
       (118, 118, 'Hướng dẫn viên, bữa tối, vé tham quan', 'Bảo hiểm, chi phí cá nhân'),
       (119, 119, 'Hướng dẫn viên, bữa sáng, vé tham quan', 'Bảo hiểm, chi phí cá nhân'),
       (120, 120, 'Hướng dẫn viên, bữa trưa, xe đưa đón', 'Bảo hiểm, chi phí cá nhân'),
       (121, 121, 'Hướng dẫn viên, bữa sáng, xe đưa đón', 'Bảo hiểm, chi phí cá nhân'),
       (122, 122, 'Hướng dẫn viên, bữa tối, vé tham quan', 'Bảo hiểm, chi phí cá nhân'),
       (123, 123, 'Hướng dẫn viên, bữa sáng, vé tham quan', 'Bảo hiểm, chi phí cá nhân'),
       (124, 124, 'Hướng dẫn viên, bữa trưa, xe đưa đón', 'Bảo hiểm, chi phí cá nhân'),
       (125, 125, 'Hướng dẫn viên, bữa sáng, xe đưa đón', 'Bảo hiểm, chi phí cá nhân'),
       (126, 126, 'Hướng dẫn viên, bữa tối, vé tham quan', 'Bảo hiểm, chi phí cá nhân'),
       (127, 127, 'Hướng dẫn viên, bữa sáng, vé tham quan', 'Bảo hiểm, chi phí cá nhân'),
       (128, 128, 'Hướng dẫn viên, bữa trưa, xe đưa đón', 'Bảo hiểm, chi phí cá nhân'),
       (129, 129, 'Hướng dẫn viên, bữa sáng, xe đưa đón', 'Bảo hiểm, chi phí cá nhân'),
       (130, 130, 'Hướng dẫn viên, bữa tối, vé tham quan', 'Bảo hiểm, chi phí cá nhân'),
       (131, 131, 'Hướng dẫn viên, bữa sáng, vé tham quan', 'Bảo hiểm, chi phí cá nhân'),
       (132, 132, 'Hướng dẫn viên, bữa trưa, xe đưa đón', 'Bảo hiểm, chi phí cá nhân'),
       (133, 133, 'Hướng dẫn viên, bữa sáng, xe đưa đón', 'Bảo hiểm, chi phí cá nhân'),
       (134, 134, 'Hướng dẫn viên, bữa tối, vé tham quan', 'Bảo hiểm, chi phí cá nhân'),
       (135, 135, 'Hướng dẫn viên, bữa sáng, vé tham quan', 'Bảo hiểm, chi phí cá nhân'),
       (136, 136, 'Hướng dẫn viên, bữa trưa, xe đưa đón', 'Bảo hiểm, chi phí cá nhân'),
       (137, 137, 'Hướng dẫn viên, bữa sáng, xe đưa đón', 'Bảo hiểm, chi phí cá nhân'),
       (138, 138, 'Hướng dẫn viên, bữa tối, vé tham quan', 'Bảo hiểm, chi phí cá nhân'),
       (139, 139, 'Hướng dẫn viên, bữa sáng, vé tham quan', 'Bảo hiểm, chi phí cá nhân'),
       (140, 140, 'Hướng dẫn viên, bữa trưa, xe đưa đón', 'Bảo hiểm, chi phí cá nhân'),
       (141, 141, 'Hướng dẫn viên, bữa sáng, xe đưa đón', 'Bảo hiểm, chi phí cá nhân'),
       (142, 142, 'Hướng dẫn viên, bữa tối, vé tham quan', 'Bảo hiểm, chi phí cá nhân'),
       (143, 143, 'Hướng dẫn viên, bữa sáng, vé tham quan', 'Bảo hiểm, chi phí cá nhân'),
       (144, 144, 'Hướng dẫn viên, bữa trưa, xe đưa đón', 'Bảo hiểm, chi phí cá nhân'),
       (145, 145, 'Hướng dẫn viên, bữa sáng, xe đưa đón', 'Bảo hiểm, chi phí cá nhân'),
       (146, 146, 'Hướng dẫn viên, bữa tối, vé tham quan', 'Bảo hiểm, chi phí cá nhân'),
       (147, 147, 'Hướng dẫn viên, bữa sáng, vé tham quan', 'Bảo hiểm, chi phí cá nhân'),
       (148, 148, 'Hướng dẫn viên, bữa trưa, xe đưa đón', 'Bảo hiểm, chi phí cá nhân'),
       (149, 149, 'Hướng dẫn viên, bữa sáng, xe đưa đón', 'Bảo hiểm, chi phí cá nhân'),
       (150, 150, 'Hướng dẫn viên, bữa tối, vé tham quan', 'Bảo hiểm, chi phí cá nhân'),
       (151, 151, 'Hướng dẫn viên, bữa sáng, vé tham quan', 'Bảo hiểm, chi phí cá nhân'),
       (152, 152, 'Hướng dẫn viên, bữa trưa, xe đưa đón', 'Bảo hiểm, chi phí cá nhân'),
       (153, 153, 'Hướng dẫn viên, bữa sáng, xe đưa đón', 'Bảo hiểm, chi phí cá nhân'),
       (154, 154, 'Hướng dẫn viên, bữa tối, vé tham quan', 'Bảo hiểm, chi phí cá nhân'),
       (155, 155, 'Hướng dẫn viên, bữa sáng, vé tham quan', 'Bảo hiểm, chi phí cá nhân'),
       (156, 156, 'Hướng dẫn viên, bữa trưa, xe đưa đón', 'Bảo hiểm, chi phí cá nhân'),
       (157, 157, 'Hướng dẫn viên, bữa sáng, xe đưa đón', 'Bảo hiểm, chi phí cá nhân'),
       (158, 158, 'Hướng dẫn viên, bữa tối, vé tham quan', 'Bảo hiểm, chi phí cá nhân'),
       (159, 159, 'Hướng dẫn viên, bữa sáng, vé tham quan', 'Bảo hiểm, chi phí cá nhân'),
       (160, 160, 'Hướng dẫn viên, bữa trưa, xe đưa đón', 'Bảo hiểm, chi phí cá nhân');
INSERT INTO `tour_service` (`service_id`, `tour_id`, `included_service`, `excluded_service`)
VALUES (161, 161, 'Hướng dẫn viên, bữa sáng, xe đưa đón', 'Bảo hiểm, chi phí cá nhân'),
       (162, 162, 'Hướng dẫn viên, bữa tối, vé tham quan', 'Bảo hiểm, chi phí cá nhân'),
       (163, 163, 'Hướng dẫn viên, bữa sáng, vé tham quan', 'Bảo hiểm, chi phí cá nhân'),
       (164, 164, 'Hướng dẫn viên, bữa trưa, xe đưa đón', 'Bảo hiểm, chi phí cá nhân'),
       (165, 165, 'Hướng dẫn viên, bữa sáng, xe đưa đón', 'Bảo hiểm, chi phí cá nhân'),
       (166, 166, 'Hướng dẫn viên, bữa tối, vé tham quan', 'Bảo hiểm, chi phí cá nhân'),
       (167, 167, 'Hướng dẫn viên, bữa sáng, vé tham quan', 'Bảo hiểm, chi phí cá nhân'),
       (168, 168, 'Hướng dẫn viên, bữa trưa, xe đưa đón', 'Bảo hiểm, chi phí cá nhân'),
       (169, 169, 'Hướng dẫn viên, bữa sáng, xe đưa đón', 'Bảo hiểm, chi phí cá nhân'),
       (170, 170, 'Hướng dẫn viên, bữa tối, vé tham quan', 'Bảo hiểm, chi phí cá nhân'),
       (171, 171, 'Hướng dẫn viên, bữa sáng, vé tham quan', 'Bảo hiểm, chi phí cá nhân'),
       (172, 172, 'Hướng dẫn viên, bữa trưa, xe đưa đón', 'Bảo hiểm, chi phí cá nhân'),
       (173, 173, 'Hướng dẫn viên, bữa sáng, xe đưa đón', 'Bảo hiểm, chi phí cá nhân'),
       (174, 174, 'Hướng dẫn viên, bữa tối, vé tham quan', 'Bảo hiểm, chi phí cá nhân');