/*
 Navicat Premium Data Transfer

 Source Server         : root
 Source Server Type    : MySQL
 Source Server Version : 80022
 Source Host           : localhost:3306
 Source Schema         : horqian

 Target Server Type    : MySQL
 Target Server Version : 80022
 File Encoding         : 65001

 Date: 09/02/2022 17:18:57
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for sys_logs
-- ----------------------------
DROP TABLE IF EXISTS `sys_logs`;
CREATE TABLE `sys_logs`  (
  `id` int(0) NOT NULL AUTO_INCREMENT,
  `request_ip` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'ip地址',
  `user_id` int(0) NULL DEFAULT NULL COMMENT '操作人id',
  `user_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '操作人名称',
  `area_code` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '区域编码',
  `area_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '区域名称',
  `exception_detail` text CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL COMMENT '错误信息',
  `is_error` tinyint(0) UNSIGNED NULL DEFAULT 0 COMMENT '是否错误 0否 1是',
  `method` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '方法名',
  `params` text CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL COMMENT '参数',
  `consuming_time` bigint(0) NULL DEFAULT NULL COMMENT '操作耗时',
  `time` datetime(0) NULL DEFAULT NULL COMMENT '请求时间',
  `system_type` tinyint(0) UNSIGNED NULL DEFAULT 10 COMMENT '系统类型 10后台 20小程序 30消息队列',
  `is_handle` tinyint(0) UNSIGNED NULL DEFAULT 0 COMMENT '是否需要处理 0否 1是',
  `is_delete` tinyint(0) UNSIGNED NULL DEFAULT 0,
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 623 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '系统-日志表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_logs
-- ----------------------------

-- ----------------------------
-- Table structure for sys_menu
-- ----------------------------
DROP TABLE IF EXISTS `sys_menu`;
CREATE TABLE `sys_menu`  (
  `id` int(0) UNSIGNED NOT NULL AUTO_INCREMENT,
  `menu_id` int(0) UNSIGNED NULL DEFAULT NULL,
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '',
  `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '',
  `jump` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '',
  `perm` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '',
  `menu_order` int(0) UNSIGNED NULL DEFAULT NULL,
  `pid` int(0) UNSIGNED NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_menu
-- ----------------------------
INSERT INTO `sys_menu` VALUES (155, NULL, 'PermissionControl', '权限控制', '', 'PermissionControl_show', 100, 0);
INSERT INTO `sys_menu` VALUES (159, NULL, 'user', '用户管理', '', 'user_show', 1, 155);
INSERT INTO `sys_menu` VALUES (163, NULL, 'role', '角色管理', '', 'role_show', 2, 155);
INSERT INTO `sys_menu` VALUES (165, NULL, '新增用户', '新增用户', '', 'user_add', 1, 159);
INSERT INTO `sys_menu` VALUES (166, NULL, '删除用户', '删除用户', '', 'user_delete', 2, 159);
INSERT INTO `sys_menu` VALUES (167, NULL, 'menu', '菜单管理', '', 'menu_show', 3, 155);
INSERT INTO `sys_menu` VALUES (168, NULL, '修改用户', '修改用户', '', 'user_save', 3, 159);
INSERT INTO `sys_menu` VALUES (169, NULL, 'swagger', 'swagger', '', 'swagger_show', 101, 0);

-- ----------------------------
-- Table structure for sys_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role`  (
  `id` int(0) UNSIGNED NOT NULL AUTO_INCREMENT,
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '',
  `info` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '',
  `keywords` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 10 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_role
-- ----------------------------
INSERT INTO `sys_role` VALUES (1, '超级管理员', '超级权限', 'PermissionControl_show,user_show,user_add,user_delete,user_save,role_show,menu_show,swagger_show');
INSERT INTO `sys_role` VALUES (10, '测试角色', '测试角色啊', 'PermissionControl_show,user_show,role_show,menu_show,swagger_show');

-- ----------------------------
-- Table structure for sys_user
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user`  (
  `id` int(0) UNSIGNED NOT NULL AUTO_INCREMENT,
  `username` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '用户名',
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '密码',
  `nickname` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '昵称',
  `sex` varchar(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '性别',
  `phone` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '电话',
  `email` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '邮件',
  `role_id` int(0) UNSIGNED NULL DEFAULT NULL,
  `role_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '',
  `is_delete` tinyint(0) UNSIGNED NULL DEFAULT 0 COMMENT '删除',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '系统管理员表\r\n' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_user
-- ----------------------------
INSERT INTO `sys_user` VALUES (1, 'admin', '$2a$10$U2NPwyUuilFJkw8qdavwcOk03P8pBpBtjwanPqK2PuXOg.6TazONe', '管理员', '男', '13200002222', 'admin@qq.com', 1, '超级管理员', 0, '2020-10-23 15:09:26', '2021-06-29 13:56:16');
INSERT INTO `sys_user` VALUES (7, 'test', '$2a$10$FDHvCWujHRtOxk4pFjpsUeqS7WhpQiuUOL.FZ6eDWDTq2eDAaQtj2', '测试用户', '男', '17664091092', '977908281@qq.com', 10, '测试角色', 0, '2021-06-29 12:28:55', '2021-06-29 13:35:37');

SET FOREIGN_KEY_CHECKS = 1;
