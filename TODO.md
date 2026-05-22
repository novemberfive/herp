# 资产管理系统 - 开发进度清单

## 项目概述
基于 Spring Boot 3.2 + Vue 3 + Element Plus 的资产管理系统

**技术栈**:
- **后端**: Spring Boot 3.2, MyBatis Plus, MySQL 8.0, Spring Security, JWT
- **前端**: Vue 3, Element Plus, Vite, Vue Router, Axios

---

## 已完成功能 ✅

### 1. 核心基础功能
- ✅ 用户登录认证 (JWT) - AuthController
- ✅ 工作台 Dashboard
- ✅ 通用响应封装 (Result, PageResult)
- ✅ 路由配置 (8 大模块，24 个子功能)

### 2. 基础信息模块
- ✅ 资产分类管理 (后端完整 CRUD + 树形查询)
  - Entity: AssetCategory
  - Mapper: AssetCategoryMapper
  - Service: AssetCategoryService + AssetCategoryServiceImpl
  - Controller: AssetCategoryController
  - 前端：CategoryList.vue
  
- ✅ 存放位置管理 (后端完整 CRUD + 树形查询)
  - Entity: AssetLocation
  - Mapper: AssetLocationMapper
  - Service: AssetLocationService + AssetLocationServiceImpl
  - Controller: AssetLocationController
  - 前端：LocationList.vue

### 3. 资产取得模块 (Acquisition)
- ✅ 采购申请 (完整 CRUD + 审批流程)
  - Entity: PurchaseRequest
  - Repository: PurchaseRequestRepository
  - Service: PurchaseRequestService + PurchaseRequestServiceImpl
  - Controller: PurchaseRequestController
  - 前端：PurchaseList.vue
  
- ✅ 验收登记 (完整 CRUD + 验收流程)
  - Entity: AssetAcceptance
  - Repository: AssetAcceptanceRepository
  - Service: AssetAcceptanceService + AssetAcceptanceServiceImpl
  - Controller: AssetAcceptanceController
  - 前端：AcceptanceList.vue

- ✅ 资产入库 (完整 CRUD + 确认入库)
  - Entity: AssetStorage
  - Repository: AssetStorageRepository
  - Service: AssetStorageService + AssetStorageServiceImpl
  - Controller: AssetStorageController
  - 前端：StorageList.vue

### 4. 资产管理模块 (Management)
- ✅ 领用退库 (完整 CRUD + 审批流程)
  - Entity: AssetRequisition
  - Repository: AssetRequisitionRepository
  - Service: AssetRequisitionService + AssetRequisitionServiceImpl
  - Controller: AssetRequisitionController
  - 前端：RequisitionList.vue

- ✅ 资产调拨 (完整 CRUD + 审批流程)
  - Entity: AssetTransfer
  - Mapper: AssetTransferMapper
  - Service: AssetTransferService + AssetTransferServiceImpl
  - Controller: AssetTransferController
  - 前端：TransferList.vue, TransferForm.vue, TransferDetail.vue

- ✅ 资产维修 (完整 CRUD + 维修流程)
  - Entity: AssetMaintenance
  - Mapper: AssetMaintenanceMapper
  - Service: AssetMaintenanceService + AssetMaintenanceServiceImpl
  - Controller: AssetMaintenanceController
  - 前端：MaintenanceList.vue

### 5. 资产档案模块 (Archive)
- ✅ 资产卡片管理 (完整 CRUD)
  - Entity: AssetCard
  - Mapper: AssetCardMapper
  - Service: AssetService
  - Controller: AssetController
  - 前端：AssetList.vue, AssetForm.vue, AssetDetail.vue

### 6. 数据库表 (共 11 张表)
- ✅ sys_user - 系统用户表
- ✅ asset_category - 资产分类表
- ✅ asset_location - 资产存放位置表
- ✅ asset_card - 资产卡片表
- ✅ purchase_request - 采购申请表
- ✅ asset_acceptance - 验收登记表
- ✅ asset_storage - 资产入库表
- ✅ asset_requisition - 资产领用退库表
- ✅ asset_transfer - 资产调拨表
- ✅ asset_maintenance - 资产维修记录表
- ✅ inventory_task - 盘点任务表 (表结构已创建)

---

## 待开发功能 📋

### 一、档案扩展模块 (Archive Extension)
- [ ] **附件管理** - 资产相关附件上传下载
  - 前端：AttachmentList.vue (已创建 placeholder)
  - 后端：需创建 Entity/Service/Controller
- [ ] **图片管理** - 资产图片管理
- [ ] **文档管理** - 资产相关文档管理
- [ ] **变更记录** - 资产变更历史记录
  - 前端：ChangeList.vue (已创建 placeholder)

### 二、资产管理模块 (Management) - 待扩展
- [ ] **资产借用/归还** - 借用管理
  - 前端：BorrowList.vue (已创建 placeholder)
  - 后端：需创建完整功能
- [ ] **资产变更** - 资产信息变更
  - 需创建完整流程

### 三、资产处置模块 (Disposal)
- [ ] **报废申请** - 资产报废申请
  - 前端：ScrapList.vue (已创建 placeholder)
  - 后端：需创建 Entity/Service/Controller
- [ ] **处置审批** - 处置审批流程
  - 前端：ApprovalList.vue (已创建 placeholder)
- [ ] **资产出售/捐赠** - 处置方式
  - 前端：SaleList.vue (已创建 placeholder)
- [ ] **残值回收** - 处置收益管理

### 四、资产定位模块 (Location Tracking)
- [ ] **位置监控** - 资产实时位置查看
- [ ] **轨迹追踪** - 资产移动历史记录

### 五、资产盘点模块 (Inventory)
- [ ] **盘点计划** - 盘点计划创建
  - 前端：PlanList.vue (已创建 placeholder)
  - 后端：需创建完整功能
- [ ] **盘点任务** - 盘点任务执行
  - 前端：TaskList.vue (已创建 placeholder)
  - 后端：inventory_task 表已创建，需完善功能
- [ ] **盘点结果** - 盘盈盘亏处理
  - 前端：ResultList.vue (已创建 placeholder)

### 六、资产门户模块 (Portal)
- [ ] **我的资产** - 个人名下资产查看
  - 前端：MyAssets.vue (已创建 placeholder)
- [ ] **部门资产** - 部门资产视图
  - 前端：DeptAssets.vue (已创建 placeholder)
- [ ] **资产公告** - 资产管理相关通知

### 七、资产报表模块 (Report)
- [ ] **资产台账** - 资产总览报表
- [ ] **折旧报表** - 资产折旧明细
  - 前端：DepreciationReport.vue (已创建 placeholder)
- [ ] **统计报表** - 多维度统计分析
  - 前端：StatisticsReport.vue (已创建 placeholder)
- [ ] **处置报表** - 资产处置统计
  - 前端：DisposalReport.vue (已创建 placeholder)

---

## 优先级队列 (按开发顺序)

### 🔴 P0 - 核心功能 (当前迭代) ✅ 全部完成
- [x] **资产卡片管理** - 建立完整的资产档案管理 (CRUD)
  - 状态：已完成 ✅
  - 后端：AssetController, AssetService, AssetCardMapper, AssetCard Entity
  - 前端：AssetList.vue, AssetForm.vue, AssetDetail.vue
  
- [x] **资产调拨** - 部门间资产调拨
  - 状态：已完成 ✅
  - 后端：AssetTransferController, AssetTransferService, AssetTransferMapper, AssetTransfer Entity
  - 前端：TransferList.vue, TransferForm.vue, TransferDetail.vue
  - 数据库：asset_transfer 表已创建
  
- [x] **资产维修** - 维护资产状态记录
  - 状态：已完成 ✅
  - 后端：AssetMaintenanceController, AssetMaintenanceService, AssetMaintenanceMapper, AssetMaintenance Entity
  - 前端：MaintenanceList.vue
  - 数据库：asset_maintenance 表已创建

### 🟡 P1 - 重要功能 (下一迭代)
- [ ] **资产盘点** - 定期资产清查功能
  - 优先级：盘点任务 > 盘点计划 > 盘点结果
  - 数据库：inventory_task 表结构已存在
- [ ] **资产处置** - 资产生命周期结束管理
  - 优先级：报废申请 > 处置审批 > 残值回收
- [ ] **资产借用/归还** - 日常借用管理

### 🟢 P2 - 辅助功能 (后续迭代)
- [ ] **报表统计** - 数据分析决策支持
  - 资产台账、折旧报表、统计报表、处置报表
- [ ] **资产门户** - 用户体验提升
  - 我的资产、部门资产、资产公告
- [ ] **附件管理** - 资产相关文件管理
- [ ] **资产定位** - 高级追踪功能

---

## 开发规范

### 后端规范
- Entity: 实体类，对应数据库表 (@Data, @TableName)
- Repository/Mapper: 数据访问层，继承 BaseMapper
- Service: 业务逻辑层接口
- ServiceImpl: 业务逻辑实现
- Controller: RESTful API 控制器
- DTO: 数据传输对象 (Result, PageResult)

### 前端规范
- views/: 页面组件
  - acquisition/: 资产取得 (PurchaseList, AcceptanceList, StorageList)
  - management/: 资产管理 (RequisitionList, TransferList, BorrowList)
  - asset/: 资产档案 (AssetList, AssetForm, AssetDetail)
  - archive/: 档案扩展 (AttachmentList, ChangeList, MaintenanceList)
  - disposal/: 资产处置 (ScrapList, SaleList, ApprovalList)
  - inventory/: 资产盘点 (PlanList, TaskList, ResultList)
  - portal/: 资产门户 (MyAssets, DeptAssets)
  - report/: 资产报表 (DepreciationReport, StatisticsReport, DisposalReport)
  - basic/: 基础信息 (CategoryList, LocationList)
- components/: 通用组件
- api/: API 调用封装
- utils/: 工具函数
- router/: 路由配置
- store/: 状态管理

### 命名规范
- 数据库表：snake_case (如：asset_card)
- Java 类：PascalCase (如：AssetCard)
- 变量方法：camelCase (如：assetName)
- 前端组件：PascalCase (如：AssetList.vue)

---

## 当前进度
- **总功能点**: 24 个
- **已完成**: 10 个
  - 基础信息：资产分类、存放位置 (2)
  - 资产取得：采购申请、验收登记、资产入库 (3)
  - 资产管理：领用退库、资产调拨、资产维修 (3)
  - 资产档案：资产卡片 (1)
  - 核心基础：用户认证 (1)
- **待开发**: 14 个
- **完成率**: 约 42%

## 最近更新
- 2024-xx-xx: 完成资产维修功能开发 (前后端)
  - 后端：Entity, Mapper, Service, Controller 完整实现
  - 前端：MaintenanceList.vue 完整实现
  - 数据库：asset_maintenance 表已添加到 schema.sql
  
- 2024-xx-xx: 完成资产调拨功能开发 (前后端)
  - 后端：Entity, Mapper, Service, Controller 完整实现
  - 前端：TransferList, TransferForm, TransferDetail 完整实现
  - 数据库：asset_transfer 表已添加到 schema.sql
  
- 2024-xx-xx: 完成资产卡片功能开发 (前后端)
  - 后端：AssetController, AssetService, AssetCardMapper 完整实现
  - 前端：AssetList, AssetForm, AssetDetail 完整实现
  
- 2024-xx-xx: 完成领用退库功能开发 (前后端)
- 2024-xx-xx: 完成资产入库功能开发 (前后端)
- 2024-xx-xx: 完成验收登记功能开发 (前后端)
- 2024-xx-xx: 完成采购申请功能开发 (前后端)
- 2024-xx-xx: 完成基础框架搭建
