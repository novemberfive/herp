-- 资产管理系统数据库初始化脚本
-- 适用于 MySQL 8.0+

CREATE DATABASE IF NOT EXISTS asset_db DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE asset_db;

-- 用户表
DROP TABLE IF EXISTS sys_user;
CREATE TABLE sys_user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键 ID',
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(100) NOT NULL COMMENT '密码（BCrypt 加密）',
    real_name VARCHAR(50) COMMENT '真实姓名',
    phone VARCHAR(20) COMMENT '手机号',
    email VARCHAR(100) COMMENT '邮箱',
    department_id BIGINT COMMENT '部门 ID',
    department_name VARCHAR(100) COMMENT '部门名称',
    role VARCHAR(20) NOT NULL DEFAULT 'operator' COMMENT '角色：admin/operator/viewer',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态：0-禁用，1-正常',
    last_login_time DATETIME COMMENT '最后登录时间',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
    INDEX idx_username (username),
    INDEX idx_phone (phone),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统用户表';

-- 资产分类表
DROP TABLE IF EXISTS asset_category;
CREATE TABLE asset_category (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键 ID',
    parent_id BIGINT DEFAULT 0 COMMENT '父级 ID',
    category_name VARCHAR(100) NOT NULL COMMENT '分类名称',
    category_code VARCHAR(50) COMMENT '分类编码',
    level INT DEFAULT 1 COMMENT '层级',
    sort_order INT DEFAULT 0 COMMENT '排序',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除',
    INDEX idx_parent_id (parent_id),
    INDEX idx_level (level)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='资产分类表';

-- 存放位置表
DROP TABLE IF EXISTS asset_location;
CREATE TABLE asset_location (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键 ID',
    parent_id BIGINT DEFAULT 0 COMMENT '父级 ID',
    location_name VARCHAR(100) NOT NULL COMMENT '位置名称',
    location_code VARCHAR(50) COMMENT '位置编码',
    province VARCHAR(50) COMMENT '省',
    city VARCHAR(50) COMMENT '市',
    district VARCHAR(50) COMMENT '区',
    detail_address VARCHAR(200) COMMENT '详细地址',
    level INT DEFAULT 1 COMMENT '层级',
    sort_order INT DEFAULT 0 COMMENT '排序',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除',
    INDEX idx_parent_id (parent_id),
    INDEX idx_level (level)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='资产存放位置表';

-- 资产卡片表
DROP TABLE IF EXISTS asset_card;
CREATE TABLE asset_card (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键 ID',
    asset_code VARCHAR(50) NOT NULL UNIQUE COMMENT '资产编码',
    asset_name VARCHAR(100) NOT NULL COMMENT '资产名称',
    specification VARCHAR(100) COMMENT '规格型号',
    category_id BIGINT COMMENT '分类 ID',
    unit VARCHAR(20) COMMENT '单位',
    quantity INT DEFAULT 1 COMMENT '数量',
    unit_price DECIMAL(10,2) COMMENT '单价',
    total_amount DECIMAL(10,2) COMMENT '总金额',
    location_id BIGINT COMMENT '存放位置 ID',
    department_id BIGINT COMMENT '使用部门 ID',
    user_id BIGINT COMMENT '使用人 ID',
    status TINYINT DEFAULT 0 COMMENT '状态：0-闲置，1-在用，2-维修中，3-待处置，4-已处置，5-丢失，6-借出',
    purchase_date DATE COMMENT '购置日期',
    enable_date DATE COMMENT '启用日期',
    expected_use_years INT COMMENT '预计使用年限（月）',
    depreciation_method TINYINT DEFAULT 1 COMMENT '折旧方法：1-平均年限法，2-工作量法，3-双倍余额递减法',
    accumulated_depreciation DECIMAL(10,2) DEFAULT 0 COMMENT '已计提折旧金额',
    net_value DECIMAL(10,2) COMMENT '净值',
    supplier_name VARCHAR(100) COMMENT '供应商名称',
    contact_person VARCHAR(50) COMMENT '联系人',
    contact_phone VARCHAR(20) COMMENT '联系电话',
    warranty_period INT COMMENT '保修期限（月）',
    warranty_end_date DATE COMMENT '保修截止日期',
    images VARCHAR(500) COMMENT '图片路径（多张逗号分隔）',
    remark VARCHAR(500) COMMENT '备注',
    create_by BIGINT COMMENT '创建人 ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by BIGINT COMMENT '更新人 ID',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除',
    INDEX idx_asset_code (asset_code),
    INDEX idx_category_id (category_id),
    INDEX idx_location_id (location_id),
    INDEX idx_status (status),
    INDEX idx_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='资产卡片表';

-- 采购申请表
DROP TABLE IF EXISTS purchase_apply;
CREATE TABLE purchase_apply (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键 ID',
    apply_no VARCHAR(50) NOT NULL UNIQUE COMMENT '申请单号',
    apply_user_id BIGINT NOT NULL COMMENT '申请人 ID',
    apply_department_id BIGINT COMMENT '申请部门 ID',
    apply_reason VARCHAR(500) COMMENT '申请理由',
    total_amount DECIMAL(10,2) COMMENT '总金额',
    status TINYINT DEFAULT 0 COMMENT '状态：0-草稿，1-待审批，2-已通过，3-已拒绝',
    approve_user_id BIGINT COMMENT '审批人 ID',
    approve_time DATETIME COMMENT '审批时间',
    approve_remark VARCHAR(500) COMMENT '审批意见',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除',
    INDEX idx_apply_no (apply_no),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='采购申请表';

-- 盘点任务表
DROP TABLE IF EXISTS inventory_task;
CREATE TABLE inventory_task (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键 ID',
    task_no VARCHAR(50) NOT NULL UNIQUE COMMENT '任务编号',
    task_name VARCHAR(100) NOT NULL COMMENT '任务名称',
    task_type TINYINT NOT NULL COMMENT '类型：1-全盘，2-抽盘，3-循环盘点',
    category_ids VARCHAR(500) COMMENT '盘点分类 IDs',
    location_ids VARCHAR(500) COMMENT '盘点位置 IDs',
    start_date DATE NOT NULL COMMENT '开始日期',
    end_date DATE NOT NULL COMMENT '结束日期',
    status TINYINT DEFAULT 0 COMMENT '状态：0-未开始，1-进行中，2-已完成，3-已终止',
    create_user_id BIGINT NOT NULL COMMENT '创建人 ID',
    complete_time DATETIME COMMENT '完成时间',
    remark VARCHAR(500) COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除',
    INDEX idx_task_no (task_no),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='盘点任务表';

-- 插入默认管理员账户（密码：admin123）
INSERT INTO sys_user (username, password, real_name, role, status) 
VALUES ('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '系统管理员', 'admin', 1);

-- 插入示例分类数据
INSERT INTO asset_category (parent_id, category_name, category_code, level, sort_order) VALUES
(0, '电子设备', 'ELECTRONIC', 1, 1),
(0, '办公家具', 'FURNITURE', 1, 2),
(0, '医疗设备', 'MEDICAL', 1, 3),
(1, '电脑', 'COMPUTER', 2, 1),
(1, '打印机', 'PRINTER', 2, 2),
(3, '诊断设备', 'DIAGNOSIS', 2, 1),
(3, '治疗设备', 'TREATMENT', 2, 2);

-- 插入示例位置数据
INSERT INTO asset_location (parent_id, location_name, location_code, level, sort_order) VALUES
(0, '一楼', 'F1', 1, 1),
(0, '二楼', 'F2', 1, 2),
(1, '诊室', 'F1-ROOM1', 2, 1),
(1, '药房', 'F1-ROOM2', 2, 2),
(2, '办公室', 'F2-OFFICE', 2, 1),
(2, '会议室', 'F2-MEETING', 2, 2);

