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

-- 采购申请表（已实现）
DROP TABLE IF EXISTS purchase_request;
CREATE TABLE purchase_request (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键 ID',
    request_no VARCHAR(50) NOT NULL UNIQUE COMMENT '申请单号',
    asset_name VARCHAR(100) NOT NULL COMMENT '资产名称',
    specification VARCHAR(100) COMMENT '规格型号',
    category_id BIGINT COMMENT '分类 ID',
    category_name VARCHAR(100) COMMENT '分类名称',
    quantity INT DEFAULT 1 COMMENT '申请数量',
    estimated_price DECIMAL(10,2) COMMENT '预估单价',
    estimated_total DECIMAL(10,2) COMMENT '预估总价',
    department_id BIGINT COMMENT '申请部门 ID',
    department_name VARCHAR(100) COMMENT '申请部门名称',
    applicant_id BIGINT COMMENT '申请人 ID',
    applicant_name VARCHAR(50) COMMENT '申请人姓名',
    apply_date DATETIME COMMENT '申请日期',
    expected_delivery_date DATETIME COMMENT '期望到货日期',
    purchase_reason VARCHAR(500) COMMENT '采购原因',
    remark VARCHAR(500) COMMENT '备注',
    status TINYINT DEFAULT 0 COMMENT '审批状态：0-草稿，1-待审批，2-审批通过，3-审批拒绝，4-已采购',
    approver_id BIGINT COMMENT '审批人 ID',
    approver_name VARCHAR(50) COMMENT '审批人姓名',
    approve_time DATETIME COMMENT '审批时间',
    approve_opinion VARCHAR(500) COMMENT '审批意见',
    create_by BIGINT COMMENT '创建人 ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by BIGINT COMMENT '更新人 ID',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除',
    INDEX idx_request_no (request_no),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='采购申请表';

-- 验收登记表
DROP TABLE IF EXISTS asset_acceptance;
CREATE TABLE asset_acceptance (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键 ID',
    acceptance_no VARCHAR(50) NOT NULL UNIQUE COMMENT '验收单号',
    purchase_request_id BIGINT COMMENT '关联采购申请 ID',
    purchase_request_no VARCHAR(50) COMMENT '采购申请单号',
    asset_name VARCHAR(100) NOT NULL COMMENT '资产名称',
    specification VARCHAR(100) COMMENT '规格型号',
    category_id BIGINT COMMENT '分类 ID',
    category_name VARCHAR(100) COMMENT '分类名称',
    quantity INT DEFAULT 1 COMMENT '验收数量',
    qualified_quantity INT DEFAULT 0 COMMENT '合格数量',
    unqualified_quantity INT DEFAULT 0 COMMENT '不合格数量',
    actual_price DECIMAL(10,2) COMMENT '实际单价',
    actual_total DECIMAL(10,2) COMMENT '实际总价',
    supplier_name VARCHAR(100) COMMENT '供应商名称',
    supplier_contact VARCHAR(50) COMMENT '供应商联系人',
    supplier_phone VARCHAR(20) COMMENT '供应商电话',
    delivery_date DATETIME COMMENT '到货日期',
    acceptance_date DATETIME COMMENT '验收日期',
    acceptor_id BIGINT COMMENT '验收人 ID',
    acceptor_name VARCHAR(50) COMMENT '验收人姓名',
    department_id BIGINT COMMENT '使用部门 ID',
    department_name VARCHAR(100) COMMENT '使用部门名称',
    location_id BIGINT COMMENT '存放位置 ID',
    location_name VARCHAR(100) COMMENT '存放位置名称',
    acceptance_result TINYINT DEFAULT 0 COMMENT '验收结果：0-待验收，1-验收合格，2-验收不合格，3-部分合格',
    unqualified_reason VARCHAR(500) COMMENT '不合格原因',
    handling_opinion VARCHAR(500) COMMENT '处理意见',
    remark VARCHAR(500) COMMENT '备注',
    storage_status TINYINT DEFAULT 0 COMMENT '入库状态：0-未入库，1-已入库',
    create_by BIGINT COMMENT '创建人 ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by BIGINT COMMENT '更新人 ID',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除',
    INDEX idx_acceptance_no (acceptance_no),
    INDEX idx_purchase_request_id (purchase_request_id),
    INDEX idx_acceptance_result (acceptance_result),
    INDEX idx_storage_status (storage_status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='验收登记表';

-- 资产入库表
DROP TABLE IF EXISTS asset_storage;
CREATE TABLE asset_storage (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键 ID',
    storage_no VARCHAR(50) NOT NULL UNIQUE COMMENT '入库单号',
    acceptance_id BIGINT COMMENT '关联验收 ID',
    acceptance_no VARCHAR(50) COMMENT '验收单号',
    purchase_request_id BIGINT COMMENT '关联采购申请 ID',
    purchase_request_no VARCHAR(50) COMMENT '采购申请单号',
    asset_code VARCHAR(100) COMMENT '资产编码',
    asset_name VARCHAR(100) NOT NULL COMMENT '资产名称',
    specification VARCHAR(100) COMMENT '规格型号',
    category_id BIGINT COMMENT '分类 ID',
    category_name VARCHAR(100) COMMENT '分类名称',
    quantity INT DEFAULT 1 COMMENT '入库数量',
    unit_price DECIMAL(10,2) COMMENT '单价',
    total_amount DECIMAL(10,2) COMMENT '总金额',
    supplier_name VARCHAR(100) COMMENT '供应商名称',
    department_id BIGINT COMMENT '使用部门 ID',
    department_name VARCHAR(100) COMMENT '使用部门名称',
    location_id BIGINT COMMENT '存放位置 ID',
    location_name VARCHAR(100) COMMENT '存放位置名称',
    keeper_id BIGINT COMMENT '保管人 ID',
    keeper_name VARCHAR(50) COMMENT '保管人姓名',
    storage_date DATETIME COMMENT '入库日期',
    storage_type TINYINT DEFAULT 1 COMMENT '入库类型：1-采购入库，2-退库入库，3-调拨入库，4-其他入库',
    status TINYINT DEFAULT 0 COMMENT '状态：0-待入库，1-已入库',
    remark VARCHAR(500) COMMENT '备注',
    create_by BIGINT COMMENT '创建人 ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by BIGINT COMMENT '更新人 ID',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除',
    INDEX idx_storage_no (storage_no),
    INDEX idx_acceptance_id (acceptance_id),
    INDEX idx_purchase_request_id (purchase_request_id),
    INDEX idx_status (status),
    INDEX idx_storage_type (storage_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='资产入库表';

-- 资产调拨表
DROP TABLE IF EXISTS asset_transfer;
CREATE TABLE asset_transfer (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键 ID',
    transfer_code VARCHAR(50) NOT NULL UNIQUE COMMENT '调拨单号',
    asset_id BIGINT NOT NULL COMMENT '资产 ID',
    asset_code VARCHAR(50) COMMENT '资产编码（冗余）',
    asset_name VARCHAR(100) COMMENT '资产名称（冗余）',
    from_department_id BIGINT COMMENT '调出部门 ID',
    from_department_name VARCHAR(100) COMMENT '调出部门名称（冗余）',
    to_department_id BIGINT COMMENT '调入部门 ID',
    to_department_name VARCHAR(100) COMMENT '调入部门名称（冗余）',
    transfer_reason VARCHAR(500) COMMENT '调拨原因',
    transfer_type TINYINT DEFAULT 1 COMMENT '调拨类型：1-部门间调拨，2-人员变更，3-位置变更',
    applicant_id BIGINT COMMENT '申请人 ID',
    applicant_name VARCHAR(50) COMMENT '申请人姓名（冗余）',
    apply_time DATETIME COMMENT '申请时间',
    approver_id BIGINT COMMENT '审批人 ID',
    approver_name VARCHAR(50) COMMENT '审批人姓名（冗余）',
    approve_time DATETIME COMMENT '审批时间',
    approve_status TINYINT DEFAULT 0 COMMENT '审批状态：0-待审批，1-已通过，2-已拒绝',
    approve_remark VARCHAR(500) COMMENT '审批意见',
    transfer_status TINYINT DEFAULT 0 COMMENT '调拨状态：0-待调拨，1-调拨中，2-已完成，3-已取消',
    complete_time DATETIME COMMENT '完成时间',
    remark VARCHAR(500) COMMENT '备注',
    create_by BIGINT COMMENT '创建人 ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by BIGINT COMMENT '更新人 ID',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除',
    INDEX idx_transfer_code (transfer_code),
    INDEX idx_asset_id (asset_id),
    INDEX idx_approve_status (approve_status),
    INDEX idx_transfer_status (transfer_status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='资产调拨表';

-- 资产维修记录表
DROP TABLE IF EXISTS asset_maintenance;
CREATE TABLE asset_maintenance (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键 ID',
    maintenance_no VARCHAR(50) NOT NULL UNIQUE COMMENT '维修单号',
    asset_id BIGINT NOT NULL COMMENT '资产 ID',
    asset_code VARCHAR(50) COMMENT '资产编码（冗余）',
    asset_name VARCHAR(100) COMMENT '资产名称（冗余）',
    category_id BIGINT COMMENT '分类 ID',
    category_name VARCHAR(100) COMMENT '分类名称',
    department_id BIGINT COMMENT '使用部门 ID',
    department_name VARCHAR(100) COMMENT '使用部门名称',
    location_id BIGINT COMMENT '存放位置 ID',
    location_name VARCHAR(100) COMMENT '存放位置名称',
    maintenance_type TINYINT DEFAULT 1 COMMENT '维修类型：1-日常维修，2-大修，3-保养，4-巡检',
    fault_description VARCHAR(500) COMMENT '故障描述',
    fault_type TINYINT DEFAULT 0 COMMENT '故障类型：0-其他，1-硬件故障，2-软件故障，3-人为损坏，4-自然老化',
    maintenance_method VARCHAR(500) COMMENT '维修方式',
    maintenance_result TINYINT DEFAULT 0 COMMENT '维修结果：0-待维修，1-维修中，2-维修完成，3-无法修复',
    maintenance_cost DECIMAL(10,2) COMMENT '维修费用',
    start_date DATETIME COMMENT '维修开始日期',
    end_date DATETIME COMMENT '维修结束日期',
    maintainer_id BIGINT COMMENT '维修人 ID',
    maintainer_name VARCHAR(50) COMMENT '维修人姓名',
    maintenance_unit VARCHAR(100) COMMENT '维修单位（外部维修时填写）',
    contact_person VARCHAR(50) COMMENT '联系人',
    contact_phone VARCHAR(20) COMMENT '联系电话',
    warranty_claim TINYINT DEFAULT 0 COMMENT '是否保修：0-否，1-是',
    applicant_id BIGINT COMMENT '申请人 ID',
    applicant_name VARCHAR(50) COMMENT '申请人姓名',
    apply_time DATETIME COMMENT '申请时间',
    approver_id BIGINT COMMENT '审批人 ID',
    approver_name VARCHAR(50) COMMENT '审批人姓名',
    approve_time DATETIME COMMENT '审批时间',
    approve_status TINYINT DEFAULT 0 COMMENT '审批状态：0-待审批，1-已通过，2-已拒绝',
    approve_remark VARCHAR(500) COMMENT '审批意见',
    remark VARCHAR(500) COMMENT '备注',
    create_by BIGINT COMMENT '创建人 ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by BIGINT COMMENT '更新人 ID',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除',
    INDEX idx_maintenance_no (maintenance_no),
    INDEX idx_asset_id (asset_id),
    INDEX idx_maintenance_result (maintenance_result),
    INDEX idx_approve_status (approve_status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='资产维修记录表';

-- 盘点计划表
DROP TABLE IF EXISTS inventory_plan;
CREATE TABLE inventory_plan (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键 ID',
    plan_no VARCHAR(50) NOT NULL UNIQUE COMMENT '计划编号',
    plan_name VARCHAR(100) NOT NULL COMMENT '计划名称',
    plan_type TINYINT NOT NULL COMMENT '计划类型：1-定期盘点，2-临时盘点',
    cycle_type TINYINT COMMENT '周期类型：1-每日，2-每周，3-每月，4-每季，5-每年（定期盘点时有效）',
    cycle_day INT COMMENT '周期执行日（如每月第几天，每周第几天）',
    category_ids VARCHAR(500) COMMENT '盘点分类 IDs（逗号分隔）',
    location_ids VARCHAR(500) COMMENT '盘点位置 IDs（逗号分隔）',
    start_date DATE COMMENT '计划开始日期',
    end_date DATE COMMENT '计划结束日期',
    status TINYINT DEFAULT 1 COMMENT '状态：0-停用，1-启用',
    create_user_id BIGINT NOT NULL COMMENT '创建人 ID',
    create_user_name VARCHAR(50) COMMENT '创建人姓名',
    remark VARCHAR(500) COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除',
    INDEX idx_plan_no (plan_no),
    INDEX idx_status (status),
    INDEX idx_plan_type (plan_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='盘点计划表';

-- 盘点结果表
DROP TABLE IF EXISTS inventory_result;
CREATE TABLE inventory_result (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键 ID',
    task_id BIGINT NOT NULL COMMENT '盘点任务 ID',
    task_no VARCHAR(50) NOT NULL COMMENT '任务编号',
    asset_id BIGINT NOT NULL COMMENT '资产 ID',
    asset_code VARCHAR(50) NOT NULL COMMENT '资产编码',
    asset_name VARCHAR(100) NOT NULL COMMENT '资产名称',
    category_id BIGINT COMMENT '分类 ID',
    category_name VARCHAR(100) COMMENT '分类名称',
    location_id BIGINT COMMENT '存放位置 ID',
    location_name VARCHAR(100) COMMENT '存放位置名称',
    department_id BIGINT COMMENT '使用部门 ID',
    department_name VARCHAR(100) COMMENT '使用部门名称',
    book_quantity INT DEFAULT 1 COMMENT '账面数量',
    actual_quantity INT COMMENT '实盘数量',
    difference_quantity INT COMMENT '差异数量（正数为盘盈，负数为盘亏）',
    result_type TINYINT DEFAULT 0 COMMENT '结果类型：0-正常，1-盘盈，2-盘亏，3-位置不符，4-信息不符',
    discrepancy_reason VARCHAR(500) COMMENT '差异原因',
    handling_suggestion VARCHAR(500) COMMENT '处理建议',
    inventory_user_id BIGINT COMMENT '盘点人 ID',
    inventory_user_name VARCHAR(50) COMMENT '盘点人姓名',
    inventory_time DATETIME COMMENT '盘点时间',
    reviewer_id BIGINT COMMENT '复核人 ID',
    reviewer_name VARCHAR(50) COMMENT '复核人姓名',
    review_time DATETIME COMMENT '复核时间',
    review_status TINYINT DEFAULT 0 COMMENT '复核状态：0-待复核，1-已复核，2-复核不通过',
    review_remark VARCHAR(500) COMMENT '复核意见',
    process_status TINYINT DEFAULT 0 COMMENT '处理状态：0-待处理，1-已处理，2-无需处理',
    process_time DATETIME COMMENT '处理时间',
    process_user_id BIGINT COMMENT '处理人 ID',
    process_user_name VARCHAR(50) COMMENT '处理人姓名',
    process_remark VARCHAR(500) COMMENT '处理备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除',
    INDEX idx_task_id (task_id),
    INDEX idx_task_no (task_no),
    INDEX idx_asset_id (asset_id),
    INDEX idx_result_type (result_type),
    INDEX idx_review_status (review_status),
    INDEX idx_process_status (process_status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='盘点结果表';

-- 资产处置申请表
DROP TABLE IF EXISTS asset_disposal;
CREATE TABLE asset_disposal (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键 ID',
    disposal_no VARCHAR(50) NOT NULL UNIQUE COMMENT '处置单号',
    asset_id BIGINT NOT NULL COMMENT '资产 ID',
    asset_code VARCHAR(50) NOT NULL COMMENT '资产编码',
    asset_name VARCHAR(100) NOT NULL COMMENT '资产名称',
    specification VARCHAR(100) COMMENT '规格型号',
    category_id BIGINT COMMENT '分类 ID',
    category_name VARCHAR(100) COMMENT '分类名称',
    original_value DECIMAL(10,2) COMMENT '原值',
    accumulated_depreciation DECIMAL(10,2) COMMENT '已提折旧',
    net_value DECIMAL(10,2) COMMENT '净值',
    purchase_date DATE COMMENT '购置日期',
    enable_date DATE COMMENT '启用日期',
    expected_use_years INT COMMENT '预计使用年限（月）',
    used_years INT COMMENT '已使用年限（月）',
    disposal_type TINYINT NOT NULL COMMENT '处置类型：1-报废，2-出售，3-捐赠，4-调出',
    disposal_reason VARCHAR(500) NOT NULL COMMENT '处置原因',
    disposal_method TINYINT COMMENT '处置方式：1-公开拍卖，2-协议转让，3-废品回收，4-其他',
    estimated_value DECIMAL(10,2) COMMENT '评估价值',
    actual_value DECIMAL(10,2) COMMENT '实际成交金额',
    buyer_name VARCHAR(100) COMMENT '受让方名称（出售时填写）',
    buyer_contact VARCHAR(50) COMMENT '受让方联系人',
    applicant_id BIGINT NOT NULL COMMENT '申请人 ID',
    applicant_name VARCHAR(50) NOT NULL COMMENT '申请人姓名',
    department_id BIGINT COMMENT '申请部门 ID',
    department_name VARCHAR(100) COMMENT '申请部门名称',
    apply_time DATETIME COMMENT '申请时间',
    approver_id BIGINT COMMENT '审批人 ID',
    approver_name VARCHAR(50) COMMENT '审批人姓名',
    approve_time DATETIME COMMENT '审批时间',
    approve_status TINYINT DEFAULT 0 COMMENT '审批状态：0-待审批，1-已通过，2-已拒绝',
    approve_remark VARCHAR(500) COMMENT '审批意见',
    disposal_status TINYINT DEFAULT 0 COMMENT '处置状态：0-待处置，1-处置中，2-已完成，3-已取消',
    complete_time DATETIME COMMENT '完成时间',
    attachment_urls VARCHAR(1000) COMMENT '附件 URLs（逗号分隔）',
    remark VARCHAR(500) COMMENT '备注',
    create_by BIGINT COMMENT '创建人 ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by BIGINT COMMENT '更新人 ID',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除',
    INDEX idx_disposal_no (disposal_no),
    INDEX idx_asset_id (asset_id),
    INDEX idx_disposal_type (disposal_type),
    INDEX idx_approve_status (approve_status),
    INDEX idx_disposal_status (disposal_status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='资产处置申请表';

-- 资产借用登记表
DROP TABLE IF EXISTS asset_borrow;
CREATE TABLE asset_borrow (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键 ID',
    borrow_no VARCHAR(50) NOT NULL UNIQUE COMMENT '借用单号',
    asset_id BIGINT NOT NULL COMMENT '资产 ID',
    asset_code VARCHAR(50) NOT NULL COMMENT '资产编码',
    asset_name VARCHAR(100) NOT NULL COMMENT '资产名称',
    specification VARCHAR(100) COMMENT '规格型号',
    category_id BIGINT COMMENT '分类 ID',
    category_name VARCHAR(100) COMMENT '分类名称',
    borrower_id BIGINT NOT NULL COMMENT '借用人 ID',
    borrower_name VARCHAR(50) NOT NULL COMMENT '借用人姓名',
    borrower_department_id BIGINT COMMENT '借用人部门 ID',
    borrower_department_name VARCHAR(100) COMMENT '借用人部门名称',
    borrower_phone VARCHAR(20) COMMENT '借用人联系电话',
    borrow_purpose VARCHAR(500) COMMENT '借用用途',
    borrow_date DATE NOT NULL COMMENT '借用日期',
    expected_return_date DATE NOT NULL COMMENT '预计归还日期',
    actual_return_date DATE COMMENT '实际归还日期',
    borrow_status TINYINT DEFAULT 0 COMMENT '借用状态：0-待审批，1-借用中，2-已归还，3-已逾期，4-已取消',
    approver_id BIGINT COMMENT '审批人 ID',
    approver_name VARCHAR(50) COMMENT '审批人姓名',
    approve_time DATETIME COMMENT '审批时间',
    approve_status TINYINT DEFAULT 0 COMMENT '审批状态：0-待审批，1-已通过，2-已拒绝',
    approve_remark VARCHAR(500) COMMENT '审批意见',
    return_condition VARCHAR(500) COMMENT '归还时资产状况',
    return_remark VARCHAR(500) COMMENT '归还备注',
    reminder_sent TINYINT DEFAULT 0 COMMENT '是否已发送提醒：0-否，1-是',
    last_reminder_time DATETIME COMMENT '最后提醒时间',
    remark VARCHAR(500) COMMENT '备注',
    create_by BIGINT COMMENT '创建人 ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by BIGINT COMMENT '更新人 ID',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除',
    INDEX idx_borrow_no (borrow_no),
    INDEX idx_asset_id (asset_id),
    INDEX idx_borrower_id (borrower_id),
    INDEX idx_borrow_status (borrow_status),
    INDEX idx_expected_return_date (expected_return_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='资产借用登记表';

