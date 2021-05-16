/*
 Navicat Premium Data Transfer

 Source Server         : root
 Source Server Type    : MySQL
 Source Server Version : 80019
 Source Host           : localhost:3306
 Source Schema         : saya

 Target Server Type    : MySQL
 Target Server Version : 80019
 File Encoding         : 65001

 Date: 16/05/2021 20:41:21
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for cld_file
-- ----------------------------
DROP TABLE IF EXISTS `cld_file`;
CREATE TABLE `cld_file`  (
  `id` bigint(0) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '自增id',
  `gmt_create` datetime(0) NOT NULL COMMENT '记录创建时间',
  `gmt_modified` datetime(0) NOT NULL COMMENT '记录修改时间',
  `del_flag` char(1) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '删除标志（0存在 1删除）',
  `drive_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '用户id（外键）',
  `folder_hash` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '文件所在文件夹哈希（外键）',
  `upload_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '文件上传id',
  `name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '文件名',
  `md5` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '文件md5',
  `hash` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '文件哈希',
  `size` bigint(0) NULL DEFAULT NULL COMMENT '文件大小（字节）',
  `extension` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '文件扩展名',
  `save_type` char(1) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '文件存储类型（1本地 2oss）',
  `status` char(1) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '文件状态（0正常 1停用）',
  `starred_flag` char(1) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '加星标志（0未加 1已加）',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 113 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for cld_folder
-- ----------------------------
DROP TABLE IF EXISTS `cld_folder`;
CREATE TABLE `cld_folder`  (
  `id` bigint(0) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '自增id',
  `gmt_create` datetime(0) NOT NULL COMMENT '记录创建时间',
  `gmt_modified` datetime(0) NOT NULL COMMENT '记录修改时间',
  `del_flag` char(1) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '删除标志（0存在 1删除）',
  `drive_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '文件所属用户id（外键）',
  `is_root` char(1) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '是否为根文件夹（0否 1是）',
  `name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '文件夹名',
  `hash` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '文件夹哈希',
  `parent_hash` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '文件夹父节点哈希',
  `status` char(1) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '文件夹状态（0正常 1停用）',
  `starred_flag` char(1) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '加星标志（0未加 1已加）',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 13 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for cld_recyclebin
-- ----------------------------
DROP TABLE IF EXISTS `cld_recyclebin`;
CREATE TABLE `cld_recyclebin`  (
  `id` bigint(0) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '自增id',
  `gmt_create` datetime(0) NOT NULL COMMENT '记录创建时间',
  `gmt_modified` datetime(0) NOT NULL COMMENT '记录修改时间',
  `del_flag` char(1) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '删除标志（0存在 1删除）',
  `drive_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '文件所属用户（外键）',
  `hash_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '文件或文件夹哈希（外键）',
  `hash_type` char(1) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '类型（1文件 2文件夹）',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '回收站' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for cld_share
-- ----------------------------
DROP TABLE IF EXISTS `cld_share`;
CREATE TABLE `cld_share`  (
  `id` bigint(0) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '自增id',
  `gmt_create` datetime(0) NOT NULL COMMENT '记录创建时间',
  `gmt_modified` datetime(0) NOT NULL COMMENT '记录修改时间',
  `del_flag` char(1) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '删除标志（0存在 1删除）',
  `drive_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '文件所有者id（外键）',
  `hash_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '文件或文件夹哈希（外键）',
  `hash_type` char(1) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '链接文件类型（1文件 2文件夹）',
  `url` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '生成的共享链接',
  `password` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '共享密码',
  `expiry` datetime(0) NULL DEFAULT NULL COMMENT '链接过期时间',
  `access_total` int(0) NULL DEFAULT NULL COMMENT '链接总访问次数',
  `access_used` int(0) NULL DEFAULT NULL COMMENT '链接已访问次数',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for sys_code
-- ----------------------------
DROP TABLE IF EXISTS `sys_code`;
CREATE TABLE `sys_code`  (
  `id` bigint(0) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '自增id',
  `gmt_create` datetime(0) NOT NULL COMMENT '记录创建时间',
  `gmt_modified` datetime(0) NOT NULL COMMENT '记录修改时间',
  `del_flag` char(1) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '删除标志（0存在 1删除）',
  `key` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '激活码',
  `expiry` datetime(0) NULL DEFAULT NULL COMMENT '过期时间',
  `access_total` int(0) NULL DEFAULT NULL COMMENT '激活码使用总次数',
  `access_used` int(0) NULL DEFAULT NULL COMMENT '激活码已使用次数',
  `incr_cloud` bigint(0) NULL DEFAULT NULL COMMENT '增加云盘空间字节数',
  `incr_cdn` bigint(0) NULL DEFAULT NULL COMMENT '增加下载用量字节数',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for sys_menu
-- ----------------------------
DROP TABLE IF EXISTS `sys_menu`;
CREATE TABLE `sys_menu`  (
  `id` bigint(0) UNSIGNED NOT NULL COMMENT '菜单id',
  `gmt_create` datetime(0) NOT NULL COMMENT '记录创建时间',
  `gmt_modified` datetime(0) NOT NULL COMMENT '记录修改时间',
  `name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '首级菜单项名',
  `type` char(1) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '菜单类型（m目录 f按钮）',
  `perms` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '权限标识',
  `url` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '请求地址',
  `icon` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '菜单图标',
  `parent_id` int(0) NULL DEFAULT NULL COMMENT '父菜单id',
  `order_num` int(0) NULL DEFAULT NULL COMMENT '显示顺序',
  `remake` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for sys_user
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user`  (
  `id` bigint(0) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '自增id',
  `gmt_create` datetime(0) NOT NULL COMMENT '记录创建时间',
  `gmt_modified` datetime(0) NOT NULL COMMENT '记录修改时间',
  `del_flag` char(1) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '删除标志（0存在 1删除）',
  `drive_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '用户uuid',
  `type` char(2) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '用户类型（00系统用户 01注册用户）',
  `name` varchar(30) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '用户昵称',
  `email` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '用户邮箱',
  `phone` varchar(11) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '用户手机号码（唯一）',
  `sex` char(1) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '用户性别（0男 1女 2未知）',
  `avatar` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '用户头像路径',
  `password` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '用户密码',
  `status` char(1) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '账号状态（0正常 1停用）',
  `login_ip` varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '用户最后登录ip',
  `login_date` datetime(0) NULL DEFAULT NULL COMMENT '用户最后登录时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '注册用户实体' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for sys_user_log
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_log`;
CREATE TABLE `sys_user_log`  (
  `id` bigint(0) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '自增id',
  `gmt_create` datetime(0) NOT NULL COMMENT '记录创建时间',
  `gmt_modified` datetime(0) NOT NULL COMMENT '记录修改时间',
  `del_flag` char(1) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '删除标志（0存在 1删除）',
  `drive_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '所属用户id（外键）',
  `upload_file_hash` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '上传文件哈希',
  `download_file_hash` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '下载文件哈希',
  `delete_file_hash` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '删除文件哈希',
  `create_folder_hash` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '创建文件夹哈希',
  `delete_folder_hash` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '删除文件夹哈希',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for sys_volume
-- ----------------------------
DROP TABLE IF EXISTS `sys_volume`;
CREATE TABLE `sys_volume`  (
  `id` bigint(0) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '自增id',
  `gmt_create` datetime(0) NOT NULL COMMENT '记录创建时间',
  `gmt_modified` datetime(0) NOT NULL COMMENT '记录修改时间',
  `del_flag` char(1) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '删除标志（0存在 1删除）',
  `drive_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '所属用户id（外键）',
  `cloud_total` bigint(0) NULL DEFAULT NULL COMMENT '云盘总容量',
  `cloud_used` bigint(0) NULL DEFAULT NULL COMMENT '云盘已使用的容量',
  `cdn_total` bigint(0) NULL DEFAULT NULL COMMENT '云盘下载总量（字节）',
  `cdn_used` bigint(0) NULL DEFAULT NULL COMMENT '云盘已下载总量（字节）',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tb_template
-- ----------------------------
DROP TABLE IF EXISTS `tb_template`;
CREATE TABLE `tb_template`  (
  `id` bigint(0) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '自增id',
  `gmt_create` datetime(0) NOT NULL COMMENT '记录创建时间',
  `gmt_modified` datetime(0) NOT NULL COMMENT '记录修改时间',
  `del_flag` char(1) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '删除标志（0存在 1删除）',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 16 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;
