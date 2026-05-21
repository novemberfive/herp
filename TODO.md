# 资产管理系统 - 开发进度清单

## 项目概述
基于 Spring Boot 3.2 + Vue 3 + Element Plus 的资产管理系统

**技术栈**:
- **后端**: Spring Boot 3.2, MyBatis Plus, MySQL 8.0, Spring Security, JWT
- **前端**: Vue 3, Element Plus, Vite, Vue Router, Axios

---

## 已完成功能 ✅

### 1. 核心基础功能
- ✅ 用户登录认证 (JWT)
- ✅ 工作台 Dashboard
- ✅ 通用响应封装 (Result, PageResult)
- ✅ 路由配置 (8 大模块，24 个子功能)

### 2. 基础信息模块
- ✅ 资产分类管理 (后端完整 CRUD + 树形查询)
  - Entity: AssetCategory
  - Mapper: AssetCategoryMapper
  - Service: AssetCategoryService
  - Controller: AssetCategoryController
- ✅ 存放位置管理 (后端完整 CRUD + 树形查询)
  - Entity: AssetLocation
  - Mapper: AssetLocationMapper
  - Service: AssetLocationService
  - Controller: AssetLocationController

### 3. 资产取得模块
- ✅ 采购申请 (完整 CRUD + 审批流程)
  - Entity: PurchaseRequest
  - Repository: PurchaseRequestRepository
  - Service: PurchaseRequestService + PurchaseRequestServiceImpl
  - Controller: PurchaseRequestController
  - 前端：PurchaseList.vue (完整实现)
  
- ✅ 验收登记 (完整 CRUD + 验收流程)
  - Entity: AssetAcceptance
  - Repository: AssetAcceptanceRepository
  - Service: AssetAcceptanceService + AssetAcceptanceServiceImpl
  - Controller: AssetAcceptanceController
  - 前端：AcceptanceList.vue (完整实现)

- ✅ 资产入库 (完整 CRUD + 确认入库)
  - Entity: AssetStorage
  - Repository: AssetStorageRepository
  - Service: AssetStorageService + AssetStorageServiceImpl
  - Controller: AssetStorageController
  - 前端：StorageList.vue (完整实现)

### 4. 资产管理模块
- ✅ 领用退库 (完整 CRUD + 审批流程)
  - Entity: AssetRequisition
  - Repository: AssetRequisitionRepository
  - Service: AssetRequisitionService + AssetRequisitionServiceImpl
  - Controller: AssetRequisitionController
  - 前端：RequisitionList.vue (完整实现)

### 5. 资产档案模块
- ✅ 资产卡片管理
  - Entity: AssetCard
  - Mapper: AssetCardMapper
  - 前端：AssetList.vue (列表展示)
- ✅ 维修记录管理 (完整 CRUD + 维修流程) 🔴 P0
  - Entity: AssetMaintenance
  - Mapper: AssetMaintenanceMapper
  - Service: AssetMaintenanceService + AssetMaintenanceServiceImpl
  - Controller: AssetMaintenanceController
  - 前端：MaintenanceList.vue (完整实现)

### 6. 数据库表
- ✅ sys_user - 用户表
- ✅ asset_category - 资产分类表
- ✅ asset_location - 存放位置表
- ✅ asset_card - 资产卡片表
- ✅ purchase_request - 采购申请表
- ✅ asset_acceptance - 验收登记表
- ✅ asset_storage - 资产入库表
- ✅ asset_requisition - 资产领用退库表
- ✅ asset_transfer - 资产调拨表
- ✅ asset_maintenance - 资产维修记录表

---

## 待开发功能 📋

### 一、档案扩展模块 (Archive Extension)
- [ ] **附件管理** - 资产相关附件上传下载
  - 前端：AttachmentList.vue (placeholder)
- [ ] **图片管理** - 资产图片管理
- [ ] **文档管理** - 资产相关文档管理
- [ ] **变更记录** - 资产变更历史记录
  - 前端：ChangeList.vue (placeholder)
- [ ] **维修记录** - 资产维修历史
  - 前端：MaintenanceList.vue (placeholder)

### 二、资产管理模块 (Management) - 部分完成
- [x] **领用退库** - 已实现
- [ ] **资产调拨** - 部门间资产调拨 🔴 P0
  - 后端：需创建 AssetTransfer Entity/Repository/Service/Controller
  - 前端：TransferList.vue (placeholder)
- [ ] **资产借用/归还** - 借用管理
  - 前端：BorrowList.vue (placeholder)
- [ ] **资产变更** - 资产信息变更

### 三、资产处置模块 (Disposal)
- [ ] **报废申请** - 资产报废申请
  - 前端：ScrapList.vue (placeholder)
- [ ] **处置审批** - 处置审批流程
  - 前端：ApprovalList.vue (placeholder)
- [ ] **资产出售/捐赠** - 处置方式
  - 前端：SaleList.vue (placeholder)
- [ ] **残值回收** - 处置收益管理

### 四、资产定位模块 (Location)
- [ ] **位置监控** - 资产实时位置查看
- [ ] **轨迹追踪** - 资产移动历史记录

### 五、资产盘点模块 (Inventory)
- [ ] **盘点计划** - 盘点计划创建
  - 前端：PlanList.vue (placeholder)
- [ ] **盘点任务** - 盘点任务执行
  - 前端：TaskList.vue (placeholder)
- [ ] **盘点结果** - 盘盈盘亏处理
  - 前端：ResultList.vue (placeholder)

### 六、资产门户模块 (Portal)
- [ ] **我的资产** - 个人名下资产查看
  - 前端：MyAssets.vue (placeholder)
- [ ] **部门资产** - 部门资产视图
  - 前端：DeptAssets.vue (placeholder)
- [ ] **资产公告** - 资产管理相关通知

### 七、资产报表模块 (Report)
- [ ] **资产台账** - 资产总览报表
- [ ] **折旧报表** - 资产折旧明细
  - 前端：DepreciationReport.vue (placeholder)
- [ ] **统计报表** - 多维度统计分析
  - 前端：StatisticsReport.vue (placeholder)
- [ ] **处置报表** - 资产处置统计
  - 前端：DisposalReport.vue (placeholder)

---

## 优先级队列 (按开发顺序)

### 🔴 P0 - 核心功能 (当前迭代)
- [x] **资产卡片完善** - 建立完整的资产档案管理 (CRUD)
  - 状态：已完成 ✅
  - 说明：AssetCard 的增删改查功能已完整实现
  - 前端：AssetList.vue, AssetForm.vue, AssetDetail.vue 完整实现
  
- [x] **资产调拨** - 部门间资产调拨
  - 状态：已完成 ✅
  - 说明：实现资产在不同部门间的调拨流程
  - 后端：AssetTransfer Entity/Mapper/Service/Controller 完整实现
  - 前端：TransferList.vue, TransferForm.vue, TransferDetail.vue 完整实现
  - 数据库：asset_transfer 表已创建
  
- [x] **资产维修** - 维护资产状态记录 🔴 P0 ✅
  - 状态：已完成 ✅
  - 说明：记录资产维修历史，维护资产状态
  - 后端：AssetMaintenance Entity/Mapper/Service/Controller 完整实现
  - 前端：MaintenanceList.vue 完整实现
  - 数据库：asset_maintenance 表已创建

### 🟡 P1 - 重要功能 (下一迭代)
- [ ] **资产盘点** - 定期资产清查功能
- [ ] **资产处置** - 资产生命周期结束管理
- [ ] **附件管理** - 资产相关文件管理

### 🟢 P2 - 辅助功能 (后续迭代)
- [ ] **报表统计** - 数据分析决策支持
- [ ] **资产门户** - 用户体验提升
- [ ] **资产定位** - 高级追踪功能

---

## 开发规范

### 后端规范
- Entity: 实体类，对应数据库表
- Repository/Mapper: 数据访问层，继承 BaseMapper
- Service: 业务逻辑层接口
- ServiceImpl: 业务逻辑实现
- Controller: RESTful API 控制器
- DTO: 数据传输对象 (Result, PageResult)

### 前端规范
- views/: 页面组件
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
- **已完成**: 11 个 (采购申请、验收登记、资产入库、领用退库、基础信息管理、资产卡片列表、**资产调拨**)
- **完成率**: 约 46%

## 最近更新
- 2024-xx-xx: 完成资产调拨功能开发 (前后端) - P0
  - 后端：Entity, Mapper, Service, Controller 完整实现
  - 前端：TransferList, TransferForm, TransferDetail 完整实现
  - 数据库：asset_transfer 表已添加到 schema.sql
- 2024-xx-xx: 完成领用退库功能开发 (前后端)
- 2024-xx-xx: 完成资产入库功能开发 (前后端)
- 2024-xx-xx: 完成验收登记功能开发 (前后端)
- 2024-xx-xx: 完成采购申请功能开发 (前后端)
- 2024-xx-xx: 完成基础框架搭建
