-- 资产管理系统数据库初始化脚本
-- 适用于 MySQL 8.0+

CREATE DATABASE IF NOT EXISTS asset_db DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE asset_db;

-- 系统角色表
DROP TABLE IF EXISTS sys_role;
CREATE TABLE sys_role (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键 ID',
    role_name VARCHAR(100) NOT NULL COMMENT '角色名称',
    role_code VARCHAR(20) NOT NULL COMMENT '角色编码',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    permissions TEXT COMMENT '角色权限列表，逗号分隔',
    sort_order INT DEFAULT 0 COMMENT '排序',
    remark VARCHAR(500) COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除',
    UNIQUE KEY uk_role_name (role_name),
    UNIQUE KEY uk_role_code (role_code),
    INDEX idx_role_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统角色表';

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
    role VARCHAR(20) NOT NULL DEFAULT 'operator' COMMENT '角色编码，关联 sys_role.role_code',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态：0-禁用，1-正常',
    last_login_time DATETIME COMMENT '最后登录时间',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
    INDEX idx_username (username),
    INDEX idx_phone (phone),
    INDEX idx_status (status),
    INDEX idx_role (role)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统用户表';

-- 系统部门表
DROP TABLE IF EXISTS sys_department;
CREATE TABLE sys_department (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键 ID',
    parent_id BIGINT DEFAULT 0 COMMENT '父级 ID',
    dept_name VARCHAR(100) NOT NULL COMMENT '部门名称',
    dept_code VARCHAR(50) COMMENT '部门编码',
    leader_name VARCHAR(50) COMMENT '负责人',
    phone VARCHAR(20) COMMENT '联系电话',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态：0-禁用，1-正常',
    level INT DEFAULT 1 COMMENT '层级',
    sort_order INT DEFAULT 0 COMMENT '排序',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除',
    INDEX idx_parent_id (parent_id),
    INDEX idx_level (level),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统部门表';

-- 操作日志表
DROP TABLE IF EXISTS operation_log;
CREATE TABLE operation_log (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键 ID',
    module VARCHAR(50) NOT NULL COMMENT '所属模块',
    action VARCHAR(50) NOT NULL COMMENT '操作动作',
    biz_type VARCHAR(50) COMMENT '业务类型',
    biz_id VARCHAR(100) COMMENT '业务主键',
    description VARCHAR(500) COMMENT '操作说明',
    operator_id BIGINT COMMENT '操作人 ID',
    operator_username VARCHAR(50) COMMENT '操作人用户名',
    operator_name VARCHAR(50) COMMENT '操作人姓名',
    operator_role VARCHAR(20) COMMENT '操作人角色',
    request_method VARCHAR(10) COMMENT '请求方法',
    request_uri VARCHAR(255) COMMENT '请求路径',
    ip_address VARCHAR(64) COMMENT 'IP 地址',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态：0-失败，1-成功',
    result_code INT COMMENT '返回码',
    result_message VARCHAR(255) COMMENT '返回消息',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除',
    INDEX idx_operation_log_module (module),
    INDEX idx_operation_log_operator (operator_id),
    INDEX idx_operation_log_status (status),
    INDEX idx_operation_log_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='操作日志表';

-- 系统供应商表
DROP TABLE IF EXISTS sys_supplier;
CREATE TABLE sys_supplier (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键 ID',
    supplier_name VARCHAR(100) NOT NULL COMMENT '供应商名称',
    supplier_code VARCHAR(50) NOT NULL COMMENT '供应商编码',
    contact_person VARCHAR(50) COMMENT '联系人',
    contact_phone VARCHAR(20) COMMENT '联系电话',
    address VARCHAR(255) COMMENT '联系地址',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态：0-禁用，1-正常',
    sort_order INT DEFAULT 0 COMMENT '排序',
    remark VARCHAR(500) COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除',
    UNIQUE KEY uk_supplier_name (supplier_name),
    UNIQUE KEY uk_supplier_code (supplier_code),
    INDEX idx_supplier_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统供应商表';

-- 资产主数据表
DROP TABLE IF EXISTS asset_master;
CREATE TABLE asset_master (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键 ID',
    asset_code VARCHAR(50) NOT NULL COMMENT '资产编码',
    asset_name VARCHAR(100) NOT NULL COMMENT '资产名称',
    category_id BIGINT NOT NULL COMMENT '分类 ID',
    specification VARCHAR(100) COMMENT '规格型号',
    brand VARCHAR(50) COMMENT '品牌',
    unit VARCHAR(20) NOT NULL DEFAULT '台' COMMENT '计量单位',
    default_price DECIMAL(10,2) COMMENT '默认单价',
    warranty_months INT COMMENT '保修期（月）',
    maintenance_cycle_days INT COMMENT '保养周期（天）',
    use_life_months INT COMMENT '使用寿命（月）',
    stock_quantity INT NOT NULL DEFAULT 0 COMMENT '当前库存数量',
    min_stock INT NOT NULL DEFAULT 0 COMMENT '最低库存预警值',
    max_stock INT COMMENT '最高库存预警值',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态：0-停用，1-启用',
    remark VARCHAR(500) COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除',
    UNIQUE KEY uk_asset_master_code (asset_code),
    INDEX idx_asset_master_category (category_id),
    INDEX idx_asset_master_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='资产主数据表';

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

-- 插入示例角色数据
INSERT INTO sys_role (role_name, role_code, status, permissions, sort_order, remark) VALUES
('管理员', 'admin', 1, 'dashboard:view,asset:list,asset:create,asset:edit,asset:delete,acquisition:purchase,acquisition:purchase:create,acquisition:purchase:edit,acquisition:purchase:delete,acquisition:purchase:submit,acquisition:purchase:approve,acquisition:acceptance,acquisition:acceptance:create,acquisition:acceptance:edit,acquisition:acceptance:delete,acquisition:acceptance:submit,acquisition:acceptance:approve,acquisition:storage,acquisition:storage:create,acquisition:storage:edit,acquisition:storage:delete,acquisition:storage:confirm,archive:attachment,archive:change,archive:maintenance,archive:maintenance:create,archive:maintenance:edit,archive:maintenance:delete,archive:maintenance:approve,archive:maintenance:start,archive:maintenance:complete,archive:maintenance:cancel,management:requisition,management:requisition:create,management:requisition:edit,management:requisition:delete,management:requisition:submit,management:requisition:approve,management:transfer,management:transfer:create,management:transfer:edit,management:transfer:delete,management:transfer:approve,management:transfer:complete,management:transfer:cancel,management:borrow,management:borrow:create,management:borrow:edit,management:borrow:delete,management:borrow:approve,management:borrow:return,management:borrow:cancel,management:borrow:remind,disposal:scrap,disposal:scrap:create,disposal:scrap:edit,disposal:scrap:delete,disposal:scrap:approve,disposal:approval,disposal:approval:approve,disposal:approval:execute,disposal:approval:complete,disposal:approval:cancel,disposal:sale,disposal:sale:create,disposal:sale:edit,disposal:sale:delete,disposal:sale:approve,disposal:sale:execute,disposal:sale:complete,disposal:sale:cancel,inventory:plan,inventory:plan:create,inventory:plan:edit,inventory:plan:delete,inventory:plan:enable,inventory:plan:disable,inventory:plan:execute,inventory:task,inventory:task:create,inventory:task:edit,inventory:task:delete,inventory:task:start,inventory:task:complete,inventory:task:cancel,inventory:result,inventory:result:create,inventory:result:edit,inventory:result:delete,inventory:result:submit,inventory:result:review,inventory:result:process,inventory:result:import,portal:my-assets,portal:dept-assets,report:statistics,report:statistics:export,report:depreciation,report:depreciation:export,report:disposal,report:disposal:export,basic:category,basic:category:create,basic:category:edit,basic:category:delete,basic:location,basic:location:create,basic:location:edit,basic:location:delete,basic:department,basic:department:create,basic:department:edit,basic:department:delete,basic:supplier,basic:supplier:create,basic:supplier:edit,basic:supplier:delete,basic:asset-master,basic:asset-master:create,basic:asset-master:edit,basic:asset-master:delete,basic:asset-master:status,system:user,system:role', 1, '系统超级管理员，负责基础维护'),
('操作员', 'operator', 1, 'dashboard:view,asset:list,asset:create,asset:edit,acquisition:purchase,acquisition:purchase:create,acquisition:purchase:edit,acquisition:purchase:submit,acquisition:acceptance,acquisition:acceptance:create,acquisition:acceptance:edit,acquisition:acceptance:submit,acquisition:storage,acquisition:storage:create,acquisition:storage:edit,acquisition:storage:confirm,archive:attachment,archive:change,archive:maintenance,archive:maintenance:create,archive:maintenance:edit,archive:maintenance:approve,archive:maintenance:start,archive:maintenance:complete,management:requisition,management:requisition:create,management:requisition:edit,management:requisition:submit,management:transfer,management:transfer:create,management:transfer:edit,management:transfer:approve,management:transfer:complete,management:transfer:cancel,management:borrow,management:borrow:create,management:borrow:edit,management:borrow:approve,management:borrow:return,management:borrow:remind,disposal:scrap,disposal:scrap:create,disposal:scrap:edit,disposal:scrap:approve,disposal:approval,disposal:approval:approve,disposal:approval:execute,disposal:approval:complete,disposal:approval:cancel,disposal:sale,disposal:sale:create,disposal:sale:edit,disposal:sale:approve,disposal:sale:execute,disposal:sale:complete,disposal:sale:cancel,inventory:plan,inventory:plan:create,inventory:plan:edit,inventory:plan:enable,inventory:plan:disable,inventory:plan:execute,inventory:task,inventory:task:create,inventory:task:edit,inventory:task:start,inventory:task:complete,inventory:task:cancel,inventory:result,inventory:result:create,inventory:result:edit,inventory:result:submit,inventory:result:review,inventory:result:process,inventory:result:import,portal:my-assets,portal:dept-assets,report:statistics,report:statistics:export,report:depreciation,report:depreciation:export,report:disposal,report:disposal:export,basic:category,basic:category:create,basic:category:edit,basic:location,basic:location:create,basic:location:edit,basic:department,basic:department:create,basic:department:edit,basic:supplier,basic:supplier:create,basic:supplier:edit,basic:asset-master,basic:asset-master:create,basic:asset-master:edit,basic:asset-master:status', 2, '日常业务录入和处理角色'),
('查看者', 'viewer', 1, 'dashboard:view,asset:list,portal:my-assets,portal:dept-assets,report:statistics,report:depreciation,report:disposal', 3, '只读查询角色');

-- 插入默认管理员账户（密码：admin123）
INSERT INTO sys_user (username, password, real_name, role, status) 
VALUES ('admin', '$2a$10$d4rkeMBlNYfKTvxdIXg5d.nRyEEvnc6x.QJsQZCjhL5EqO92Vkeri', '系统管理员', 'admin', 1);

-- 插入示例部门数据
INSERT INTO sys_department (parent_id, dept_name, dept_code, leader_name, phone, status, level, sort_order) VALUES
(0, '院办公室', 'OFFICE', '张主任', '010-10000001', 1, 1, 1),
(0, '信息科', 'IT', '李主任', '010-10000002', 1, 1, 2),
(0, '财务科', 'FINANCE', '王主任', '010-10000003', 1, 1, 3),
(0, '设备科', 'EQUIPMENT', '赵主任', '010-10000004', 1, 1, 4),
(1, '行政组', 'OFFICE-ADMIN', '陈主管', '010-10000011', 1, 2, 1),
(2, '运维组', 'IT-OPS', '周主管', '010-10000021', 1, 2, 1);

-- 插入示例供应商数据
INSERT INTO sys_supplier (supplier_name, supplier_code, contact_person, contact_phone, address, status, sort_order, remark) VALUES
('华东医疗设备有限公司', 'SUP-MED-001', '孙经理', '021-20000001', '上海市浦东新区张江高科技园区 88 号', 1, 1, '医疗设备核心供应商'),
('星海办公科技有限公司', 'SUP-OFF-001', '郑女士', '021-20000002', '上海市徐汇区漕河泾开发区 66 号', 1, 2, '办公设备和耗材供应商'),
('北辰信息系统集成有限公司', 'SUP-IT-001', '何工', '010-20000003', '北京市海淀区知春路 18 号', 1, 3, '信息化设备供应商');

-- 插入示例分类数据
INSERT INTO asset_category (parent_id, category_name, category_code, level, sort_order) VALUES
(0, '电子设备', 'ELECTRONIC', 1, 1),
(0, '办公家具', 'FURNITURE', 1, 2),
(0, '医疗设备', 'MEDICAL', 1, 3),
(1, '电脑', 'COMPUTER', 2, 1),
(1, '打印机', 'PRINTER', 2, 2),
(3, '诊断设备', 'DIAGNOSIS', 2, 1),
(3, '治疗设备', 'TREATMENT', 2, 2);

-- 插入示例资产主数据
INSERT INTO asset_master (asset_code, asset_name, category_id, specification, brand, unit, default_price, warranty_months, maintenance_cycle_days, use_life_months, stock_quantity, min_stock, max_stock, status, remark) VALUES
('MASTER-COMPUTER-001', '办公笔记本电脑', 4, 'ThinkBook 14', 'Lenovo', '台', 6200.00, 36, 180, 60, 12, 2, 20, 1, '标准办公电脑模板'),
('MASTER-PRINTER-001', '激光打印机', 5, 'LaserJet Pro 400', 'HP', '台', 2800.00, 24, 365, 72, 4, 1, 8, 1, '行政办公常用设备'),
('MASTER-MEDICAL-001', '便携式监护仪', 7, 'PM-9000', 'Mindray', '台', 18500.00, 24, 90, 96, 2, 1, 5, 1, '治疗设备模板');

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

-- 资产领用退库表
DROP TABLE IF EXISTS asset_requisition;
CREATE TABLE asset_requisition (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键 ID',
    requisition_no VARCHAR(50) NOT NULL UNIQUE COMMENT '领用退库单号',
    business_type TINYINT NOT NULL COMMENT '业务类型：1-领用，2-退库',
    asset_id BIGINT COMMENT '资产 ID',
    asset_code VARCHAR(50) COMMENT '资产编码',
    asset_name VARCHAR(100) COMMENT '资产名称',
    specification VARCHAR(100) COMMENT '规格型号',
    category_id BIGINT COMMENT '分类 ID',
    category_name VARCHAR(100) COMMENT '分类名称',
    quantity INT DEFAULT 1 COMMENT '数量',
    unit_price DECIMAL(10,2) COMMENT '单价',
    total_amount DECIMAL(10,2) COMMENT '总金额',
    original_department_id BIGINT COMMENT '原部门 ID',
    original_department_name VARCHAR(100) COMMENT '原部门名称',
    original_keeper_id BIGINT COMMENT '原保管人 ID',
    original_keeper_name VARCHAR(50) COMMENT '原保管人姓名',
    new_department_id BIGINT COMMENT '新部门 ID',
    new_department_name VARCHAR(100) COMMENT '新部门名称',
    new_keeper_id BIGINT COMMENT '新保管人 ID',
    new_keeper_name VARCHAR(50) COMMENT '新保管人姓名',
    requisition_date DATETIME COMMENT '领用退库日期',
    expected_return_date DATETIME COMMENT '预计归还日期',
    actual_return_date DATETIME COMMENT '实际归还日期',
    reason VARCHAR(500) COMMENT '原因',
    remark VARCHAR(500) COMMENT '备注',
    status TINYINT DEFAULT 0 COMMENT '审批状态：0-草稿，1-待审批，2-审批通过，3-审批拒绝，4-已完成',
    approver_id BIGINT COMMENT '审批人 ID',
    approver_name VARCHAR(50) COMMENT '审批人姓名',
    approve_time DATETIME COMMENT '审批时间',
    approve_opinion VARCHAR(500) COMMENT '审批意见',
    create_by BIGINT COMMENT '创建人 ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by BIGINT COMMENT '更新人 ID',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除',
    INDEX idx_requisition_no (requisition_no),
    INDEX idx_asset_code (asset_code),
    INDEX idx_business_type (business_type),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='资产领用退库表';

-- 资产附件表
DROP TABLE IF EXISTS asset_attachment;
CREATE TABLE asset_attachment (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键 ID',
    asset_id BIGINT COMMENT '资产 ID',
    asset_code VARCHAR(50) COMMENT '资产编码',
    asset_name VARCHAR(100) COMMENT '资产名称',
    attachment_name VARCHAR(200) NOT NULL COMMENT '附件名称',
    attachment_type TINYINT DEFAULT 7 COMMENT '附件类型：1-采购合同，2-验收报告，3-维修记录，4-保养记录，5-转移单，6-报废单，7-其他',
    file_name VARCHAR(200) COMMENT '原始文件名',
    file_path VARCHAR(500) COMMENT '服务端文件路径',
    file_url VARCHAR(500) COMMENT '访问地址',
    file_size BIGINT COMMENT '文件大小',
    upload_user VARCHAR(50) COMMENT '上传人',
    upload_time DATETIME COMMENT '上传时间',
    remark VARCHAR(500) COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除',
    INDEX idx_asset_id (asset_id),
    INDEX idx_asset_code (asset_code),
    INDEX idx_attachment_type (attachment_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='资产附件表';

-- 资产变更记录表
DROP TABLE IF EXISTS asset_change_record;
CREATE TABLE asset_change_record (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键 ID',
    asset_id BIGINT COMMENT '资产 ID',
    asset_code VARCHAR(50) COMMENT '资产编码',
    asset_name VARCHAR(100) COMMENT '资产名称',
    change_type TINYINT NOT NULL COMMENT '变更类型：1-信息变更，2-部门调拨，3-位置变更，4-使用人变更，5-状态变更，6-其他',
    change_field VARCHAR(100) COMMENT '变更字段',
    before_value VARCHAR(500) COMMENT '变更前',
    after_value VARCHAR(500) COMMENT '变更后',
    change_reason VARCHAR(500) COMMENT '变更原因',
    operator_name VARCHAR(50) COMMENT '操作人',
    operate_time DATETIME COMMENT '操作时间',
    remark VARCHAR(500) COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除',
    INDEX idx_asset_id (asset_id),
    INDEX idx_asset_code (asset_code),
    INDEX idx_change_type (change_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='资产变更记录表';
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


-- 插入演示资产卡片数据
INSERT INTO asset_card (asset_code, asset_name, specification, category_id, unit, quantity, unit_price, total_amount, location_id, department_id, user_id, status, purchase_date, enable_date, expected_use_years, depreciation_method, accumulated_depreciation, net_value, supplier_name, contact_person, contact_phone, warranty_period, warranty_end_date, remark, create_by, create_time, deleted) VALUES
('AST-DEMO-001', '便携式彩超仪', 'SonoBook Pro', 1, '台', 1, 128000.00, 128000.00, 3, 4, 1, 1, '2025-01-15', '2025-02-01', 60, 1, 12800.00, 115200.00, '华东医疗设备有限公司', '孙经理', '021-20000001', 36, '2028-01-15', '演示资产：设备科在用医疗设备', 1, DATE_SUB(NOW(), INTERVAL 5 MONTH), 0),
('AST-DEMO-002', '门诊自助终端', 'Kiosk-M8', 1, '台', 2, 26000.00, 52000.00, 1, 2, 1, 0, '2025-04-10', '2025-04-20', 48, 1, 2600.00, 49400.00, '北辰信息系统集成有限公司', '何工', '010-20000003', 24, '2027-04-10', '演示资产：待分配自助设备', 1, DATE_SUB(NOW(), INTERVAL 2 MONTH), 0),
('AST-DEMO-003', '会议室投影系统', 'Epson CB-L260', 1, '套', 1, 18500.00, 18500.00, 6, 1, 1, 6, '2024-11-08', '2024-11-20', 48, 1, 3083.33, 15416.67, '星海办公科技有限公司', '郑女士', '021-20000002', 24, '2026-11-08', '演示资产：借出给培训会议使用', 1, DATE_SUB(NOW(), INTERVAL 7 MONTH), 0),
('AST-DEMO-004', '护士站办公椅', 'Ergo-Care A2', 2, '把', 12, 680.00, 8160.00, 5, 3, 1, 1, '2024-09-01', '2024-09-10', 36, 1, 1813.33, 6346.67, '星海办公科技有限公司', '郑女士', '021-20000002', 12, '2025-09-01', '演示资产：财务科批量办公家具', 1, DATE_SUB(NOW(), INTERVAL 9 MONTH), 0),
('AST-DEMO-005', '药房冷藏柜', 'YC-395L', 3, '台', 1, 9200.00, 9200.00, 4, 4, 1, 2, '2024-06-18', '2024-07-01', 60, 1, 2913.33, 6286.67, '华东医疗设备有限公司', '孙经理', '021-20000001', 24, '2026-06-18', '演示资产：维修中冷链设备', 1, DATE_SUB(NOW(), INTERVAL 12 MONTH), 0);

-- 插入演示领用退库数据
INSERT INTO asset_requisition (requisition_no, business_type, asset_id, asset_code, asset_name, specification, category_id, category_name, quantity, unit_price, total_amount, original_department_id, original_department_name, original_keeper_id, original_keeper_name, new_department_id, new_department_name, new_keeper_id, new_keeper_name, requisition_date, expected_return_date, actual_return_date, reason, remark, status, approver_id, approver_name, approve_time, approve_opinion, create_by, create_time, deleted) VALUES
('RQ-DEMO-001', 1, (SELECT id FROM asset_card WHERE asset_code = 'AST-DEMO-003'), 'AST-DEMO-003', '会议室投影系统', 'Epson CB-L260', 1, '电子设备', 1, 18500.00, 18500.00, 1, '院办公室', 1, '系统管理员', 2, '信息科', 1, '系统管理员', DATE_SUB(NOW(), INTERVAL 10 DAY), DATE_ADD(NOW(), INTERVAL 20 DAY), NULL, '培训会议临时领用投影设备', '演示数据：已审批借用', 2, 1, 'admin', DATE_SUB(NOW(), INTERVAL 9 DAY), '同意领用，请按期归还', 1, DATE_SUB(NOW(), INTERVAL 10 DAY), 0),
('RQ-DEMO-002', 2, (SELECT id FROM asset_card WHERE asset_code = 'AST-DEMO-002'), 'AST-DEMO-002', '门诊自助终端', 'Kiosk-M8', 1, '电子设备', 1, 26000.00, 26000.00, 2, '信息科', 1, '系统管理员', 4, '设备科', 1, '系统管理员', DATE_SUB(NOW(), INTERVAL 4 DAY), NULL, DATE_SUB(NOW(), INTERVAL 3 DAY), '测试完成后退回设备科库房', '演示数据：退库草稿', 0, NULL, NULL, NULL, NULL, 1, DATE_SUB(NOW(), INTERVAL 4 DAY), 0);

-- 插入演示附件数据
INSERT INTO asset_attachment (asset_id, asset_code, asset_name, attachment_name, attachment_type, file_name, file_path, file_url, file_size, upload_user, upload_time, remark, deleted) VALUES
((SELECT id FROM asset_card WHERE asset_code = 'AST-DEMO-001'), 'AST-DEMO-001', '便携式彩超仪', '便携式彩超仪采购合同.pdf', 1, '便携式彩超仪采购合同.pdf', NULL, NULL, 245760, 'admin', DATE_SUB(NOW(), INTERVAL 4 MONTH), '演示附件：采购合同元数据', 0),
((SELECT id FROM asset_card WHERE asset_code = 'AST-DEMO-005'), 'AST-DEMO-005', '药房冷藏柜', '药房冷藏柜维修记录.docx', 3, '药房冷藏柜维修记录.docx', NULL, NULL, 98304, 'admin', DATE_SUB(NOW(), INTERVAL 12 DAY), '演示附件：维修记录元数据', 0);

-- 插入演示变更记录数据
INSERT INTO asset_change_record (asset_id, asset_code, asset_name, change_type, change_field, before_value, after_value, change_reason, operator_name, operate_time, remark, deleted) VALUES
((SELECT id FROM asset_card WHERE asset_code = 'AST-DEMO-001'), 'AST-DEMO-001', '便携式彩超仪', 2, 'departmentName', '信息科', '设备科', '设备统一归口管理', 'admin', DATE_SUB(NOW(), INTERVAL 3 MONTH), '演示变更：部门调拨', 0),
((SELECT id FROM asset_card WHERE asset_code = 'AST-DEMO-003'), 'AST-DEMO-003', '会议室投影系统', 5, 'status', '在用', '借出', '培训会议临时借用', 'admin', DATE_SUB(NOW(), INTERVAL 9 DAY), '演示变更：状态变化', 0),
((SELECT id FROM asset_card WHERE asset_code = 'AST-DEMO-005'), 'AST-DEMO-005', '药房冷藏柜', 5, 'status', '在用', '维修中', '温控异常送修', 'admin', DATE_SUB(NOW(), INTERVAL 12 DAY), '演示变更：维修状态', 0);

-- 插入演示处置数据
INSERT INTO asset_disposal (disposal_no, asset_id, asset_code, asset_name, specification, category_id, category_name, original_value, accumulated_depreciation, net_value, purchase_date, enable_date, expected_use_years, used_years, disposal_type, disposal_reason, disposal_method, estimated_value, actual_value, buyer_name, buyer_contact, applicant_id, applicant_name, department_id, department_name, apply_time, approver_id, approver_name, approve_time, approve_status, approve_remark, disposal_status, complete_time, remark, create_by, create_time, deleted) VALUES
('DP-DEMO-001', (SELECT id FROM asset_card WHERE asset_code = 'AST-DEMO-004'), 'AST-DEMO-004', '护士站办公椅', 'Ergo-Care A2', 2, '办公家具', 8160.00, 1813.33, 6346.67, '2024-09-01', '2024-09-10', 36, 9, 1, '部分椅面破损，达到报废标准', 3, 1200.00, 980.00, '本地回收单位', '周经理', 1, '系统管理员', 3, '财务科', DATE_SUB(NOW(), INTERVAL 20 DAY), 1, 'admin', DATE_SUB(NOW(), INTERVAL 18 DAY), 1, '同意按流程报废', 2, DATE_SUB(NOW(), INTERVAL 15 DAY), '演示数据：已完成报废处置', 1, DATE_SUB(NOW(), INTERVAL 20 DAY), 0);
