/*
 Navicat Premium Data Transfer

 Source Server         : localhost_3306
 Source Server Type    : MySQL
 Source Server Version : 80039
 Source Host           : localhost:3306
 Source Schema         : feedback

 Target Server Type    : MySQL
 Target Server Version : 80039
 File Encoding         : 65001

 Date: 25/02/2025 16:08:03
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for sys_course_questionnaire
-- ----------------------------
DROP TABLE IF EXISTS `sys_course_questionnaire`;
CREATE TABLE `sys_course_questionnaire`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `course_id` bigint NOT NULL COMMENT '课程ID',
  `questionnaire_id` bigint NOT NULL COMMENT '问卷ID',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '关联时间',
  `status` int NULL DEFAULT 0 COMMENT '问卷状态：0-待发布，1-进行中，2-已完成',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `unique_course_questionnaire`(`course_id` ASC, `questionnaire_id` ASC) USING BTREE,
  INDEX `questionnaire_id`(`questionnaire_id` ASC) USING BTREE,
  CONSTRAINT `sys_course_questionnaire_ibfk_1` FOREIGN KEY (`course_id`) REFERENCES `sys_courses` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `sys_course_questionnaire_ibfk_2` FOREIGN KEY (`questionnaire_id`) REFERENCES `sys_questionnaires` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 9 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_course_questionnaire
-- ----------------------------
INSERT INTO `sys_course_questionnaire` VALUES (7, 2, 1, '2025-02-25 14:59:53', 0);
INSERT INTO `sys_course_questionnaire` VALUES (8, 2, 2, '2025-02-25 14:59:53', 0);

-- ----------------------------
-- Table structure for sys_course_students
-- ----------------------------
DROP TABLE IF EXISTS `sys_course_students`;
CREATE TABLE `sys_course_students`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `course_id` bigint NOT NULL COMMENT '课程ID',
  `student_id` bigint NOT NULL COMMENT '学生ID',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '选课时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `unique_course_student`(`course_id` ASC, `student_id` ASC) USING BTREE,
  INDEX `student_id`(`student_id` ASC) USING BTREE,
  CONSTRAINT `sys_course_students_ibfk_1` FOREIGN KEY (`course_id`) REFERENCES `sys_courses` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `sys_course_students_ibfk_2` FOREIGN KEY (`student_id`) REFERENCES `sys_user` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_course_students
-- ----------------------------
INSERT INTO `sys_course_students` VALUES (3, 2, 2, '2025-02-25 15:31:04');

-- ----------------------------
-- Table structure for sys_courses
-- ----------------------------
DROP TABLE IF EXISTS `sys_courses`;
CREATE TABLE `sys_courses`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '课程名称',
  `code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '课程代码',
  `teacher_id` bigint NOT NULL COMMENT '授课教师ID',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `code`(`code` ASC) USING BTREE,
  INDEX `teacher_id`(`teacher_id` ASC) USING BTREE,
  CONSTRAINT `sys_courses_ibfk_1` FOREIGN KEY (`teacher_id`) REFERENCES `sys_user` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_courses
-- ----------------------------
INSERT INTO `sys_courses` VALUES (2, '数学', 'MATH101', 21, '2025-02-24 11:21:04', '2025-02-24 11:21:04');
INSERT INTO `sys_courses` VALUES (3, '英语', 'ENG102', 21, '2025-02-24 11:21:15', '2025-02-24 11:21:15');

-- ----------------------------
-- Table structure for sys_file
-- ----------------------------
DROP TABLE IF EXISTS `sys_file`;
CREATE TABLE `sys_file`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` varchar(256) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '文件名称',
  `url` varchar(256) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '下载链接',
  `type` varchar(256) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '文件类型',
  `md5` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '文件md5',
  `size` bigint NULL DEFAULT NULL COMMENT '文件大小',
  `enable` tinyint NULL DEFAULT 1 COMMENT '是否禁用(1-启用, 1-禁用)',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint NOT NULL DEFAULT 0 COMMENT '是否删除(0-未删, 1-已删)',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 150 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci COMMENT = '文件上传的列表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_file
-- ----------------------------

-- ----------------------------
-- Table structure for sys_questionnaires
-- ----------------------------
DROP TABLE IF EXISTS `sys_questionnaires`;
CREATE TABLE `sys_questionnaires`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '问卷标题',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '问卷描述',
  `created_by` bigint NOT NULL COMMENT '创建者ID（通常是教师）',
  `questions` json NULL COMMENT '问题列表（JSON数组）',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `created_by`(`created_by` ASC) USING BTREE,
  CONSTRAINT `sys_questionnaires_ibfk_1` FOREIGN KEY (`created_by`) REFERENCES `sys_user` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_questionnaires
-- ----------------------------
INSERT INTO `sys_questionnaires` VALUES (1, '测试问卷', '测试问卷', 21, '[{\"id\": 1740451938362, \"max\": 100, \"min\": 0, \"step\": 1, \"type\": \"single\", \"index\": 1, \"title\": \"测试\", \"options\": [\"12\", \"123\"], \"required\": true, \"dateFormat\": \"YYYY-MM-DD\", \"timeFormat\": \"HH:mm:ss\", \"placeholder\": \"\"}, {\"id\": 1740452251865, \"max\": 100, \"min\": 0, \"step\": 1, \"type\": \"radio\", \"index\": 2, \"label\": \"\", \"title\": \"测试萨达\", \"options\": [\"4567\", \"45647\", \"4564\"], \"required\": false, \"dateFormat\": \"YYYY-MM-DD\", \"timeFormat\": \"HH:mm:ss\", \"placeholder\": \"\"}, {\"id\": 1740452674400, \"max\": 100, \"min\": 0, \"step\": 1, \"type\": \"multiple\", \"index\": 3, \"label\": \"\", \"title\": \"测试阿萨德\", \"options\": [\"123\", \"12\", \"445\"], \"required\": false, \"dateFormat\": \"YYYY-MM-DD\", \"timeFormat\": \"HH:mm:ss\", \"placeholder\": \"\"}, {\"id\": 1740452696578, \"max\": 100, \"min\": 0, \"step\": 1, \"type\": \"checkbox\", \"index\": 4, \"label\": \"\", \"title\": \"测试多选框\", \"options\": [\"1\", \"2\", \"6\", \"4\"], \"required\": true, \"dateFormat\": \"YYYY-MM-DD\", \"timeFormat\": \"HH:mm:ss\", \"placeholder\": \"\"}, {\"id\": 1740453079670, \"max\": 100, \"min\": 0, \"step\": 1, \"type\": \"checkbox\", \"index\": 5, \"label\": \"\", \"title\": \"测试多选题\", \"options\": [\"123\", \"12\"], \"required\": false, \"dateFormat\": \"YYYY-MM-DD\", \"timeFormat\": \"HH:mm:ss\", \"placeholder\": \"\"}, {\"id\": 1740453107627, \"max\": 100, \"min\": 0, \"step\": 1, \"type\": \"text\", \"index\": 6, \"label\": \"\", \"title\": \"测试填空题\", \"options\": [\"\", \"\"], \"required\": false, \"dateFormat\": \"YYYY-MM-DD\", \"timeFormat\": \"HH:mm:ss\", \"placeholder\": \"请输入\"}, {\"id\": 1740453118485, \"max\": 100, \"min\": 0, \"step\": 1, \"type\": \"textarea\", \"index\": 7, \"label\": \"\", \"title\": \"测试文本域\", \"options\": [\"\", \"\"], \"required\": false, \"dateFormat\": \"YYYY-MM-DD\", \"timeFormat\": \"HH:mm:ss\", \"placeholder\": \"123\"}, {\"id\": 1740453133408, \"max\": 100, \"min\": 0, \"step\": 1, \"type\": \"select\", \"index\": 8, \"label\": \"\", \"title\": \"测试下拉选择\", \"options\": [\"1\", \"123\"], \"required\": false, \"dateFormat\": \"YYYY-MM-DD\", \"timeFormat\": \"HH:mm:ss\", \"placeholder\": \"\"}, {\"id\": 1740453147118, \"max\": 100, \"min\": 0, \"step\": 1, \"type\": \"date\", \"index\": 9, \"label\": \"\", \"title\": \"测试日期选择\", \"options\": [\"\", \"\"], \"required\": false, \"dateFormat\": \"YYYY-MM-DD\", \"timeFormat\": \"HH:mm:ss\", \"placeholder\": \"\"}, {\"id\": 1740453158724, \"max\": 100, \"min\": 0, \"step\": 1, \"type\": \"time\", \"index\": 10, \"label\": \"\", \"title\": \"测试时间选择\", \"options\": [\"\", \"\"], \"required\": false, \"dateFormat\": \"YYYY-MM-DD\", \"timeFormat\": \"HH:mm:ss\", \"placeholder\": \"\"}, {\"id\": 1740453169569, \"max\": 100, \"min\": 0, \"step\": 1, \"type\": \"number\", \"index\": 11, \"label\": \"\", \"title\": \"测试数字输入\", \"options\": [\"\", \"\"], \"required\": false, \"dateFormat\": \"YYYY-MM-DD\", \"timeFormat\": \"HH:mm:ss\", \"placeholder\": \"\"}, {\"id\": 1740463523979, \"max\": 100, \"min\": 0, \"step\": 1, \"type\": \"single\", \"index\": 12, \"label\": \"\", \"title\": \"123\", \"options\": [\"123\", \"1231\"], \"required\": false, \"dateFormat\": \"YYYY-MM-DD\", \"timeFormat\": \"HH:mm:ss\", \"placeholder\": \"\"}]', '2025-02-24 11:53:00', '2025-02-24 11:53:00');
INSERT INTO `sys_questionnaires` VALUES (2, '问卷2', '22', 1, NULL, '2025-02-25 11:10:31', '2025-02-25 11:10:31');

-- ----------------------------
-- Table structure for sys_responses
-- ----------------------------
DROP TABLE IF EXISTS `sys_responses`;
CREATE TABLE `sys_responses`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `questionnaire_id` bigint NOT NULL COMMENT '问卷ID',
  `student_id` bigint NOT NULL COMMENT '提交学生ID',
  `answers` json NOT NULL COMMENT '学生回答（JSON数组）',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '回答时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `questionnaire_id`(`questionnaire_id` ASC) USING BTREE,
  INDEX `student_id`(`student_id` ASC) USING BTREE,
  CONSTRAINT `sys_responses_ibfk_1` FOREIGN KEY (`questionnaire_id`) REFERENCES `sys_questionnaires` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `sys_responses_ibfk_2` FOREIGN KEY (`student_id`) REFERENCES `sys_user` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_responses
-- ----------------------------

-- ----------------------------
-- Table structure for sys_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'id',
  `name` varchar(50) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '名称',
  `description` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '描述',
  `flag` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '唯一标识',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_role
-- ----------------------------
INSERT INTO `sys_role` VALUES (1, '管理员', '管理员', 'admin');
INSERT INTO `sys_role` VALUES (2, '学生', '查看问卷和提交反馈', 'student');
INSERT INTO `sys_role` VALUES (3, '教师', '发布问卷和查看结果分析\r\n', 'teacher');

-- ----------------------------
-- Table structure for sys_user
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'id',
  `username` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '用户名',
  `password` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '密码',
  `nickname` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '昵称',
  `avatar_url` longtext CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL COMMENT '头像',
  `email` varchar(20) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '邮箱',
  `role_id` int NULL DEFAULT NULL COMMENT '角色  0超级管理员  1管理员 2普通账号',
  `status` tinyint NULL DEFAULT 1 COMMENT '是否有效 1有效 0无效',
  `role` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '角色',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `username`(`username` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 22 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_user
-- ----------------------------
INSERT INTO `sys_user` VALUES (1, 'admin', 'admin', '管理员', NULL, '', 1, 1, 'admin');
INSERT INTO `sys_user` VALUES (2, 'user', 'user', '学生', NULL, '', 2, 1, 'student');
INSERT INTO `sys_user` VALUES (21, '123', '123', '李老师', NULL, NULL, 3, 1, 'teacher');

SET FOREIGN_KEY_CHECKS = 1;
