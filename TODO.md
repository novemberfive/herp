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
  - 前端：CategoryList.vue ✅ (已完成：树形表格、搜索、新增/编辑/删除、子分类管理)
  
- ✅ 存放位置管理 (后端完整 CRUD + 树形查询)
  - Entity: AssetLocation
  - Mapper: AssetLocationMapper
  - Service: AssetLocationService + AssetLocationServiceImpl
  - Controller: AssetLocationController
  - 前端：LocationList.vue ✅ (已完成：树形表格、搜索、新增/编辑/删除、子位置管理)

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
  - 前端：TransferList.vue, TransferForm.vue, TransferDetail.vue (位于 transfer 目录)

- ✅ 资产维修 (完整 CRUD + 维修流程)
  - Entity: AssetMaintenance
  - Mapper: AssetMaintenanceMapper
  - Service: AssetMaintenanceService + AssetMaintenanceServiceImpl
  - Controller: AssetMaintenanceController
  - 前端：MaintenanceList.vue ✅ (已完成：列表查询、新增/编辑申请、审批、开始维修、完成维修、删除)

### 5. 资产档案模块 (Archive)
- ✅ 资产卡片管理 (完整 CRUD)
  - Entity: AssetCard
  - Mapper: AssetCardMapper
  - Service: AssetService
  - Controller: AssetController
  - 前端：AssetList.vue, AssetForm.vue, AssetDetail.vue

### 6. 资产盘点模块 (Inventory)
- ✅ 盘点任务 (完整 CRUD + 任务执行)
  - Entity: InventoryTask
  - Mapper: InventoryTaskMapper
  - Service: InventoryTaskService + InventoryTaskServiceImpl
  - Controller: InventoryTaskController
  - 前端：TaskList.vue
  - 数据库：inventory_task 表已创建

### 7. 数据库表 (共 11 张表)
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
- [x] **附件管理** - 资产相关附件上传下载 ✅ (前端完成，模拟 API)
  - 前端：AttachmentList.vue (已完整实现：附件列表、上传、下载、预览、删除、批量操作)
- [ ] **图片管理** - 资产图片管理
- [ ] **文档管理** - 资产相关文档管理
- [x] **变更记录** - 资产变更历史记录 ✅ (前端完成，模拟 API)
  - 前端：ChangeList.vue (已完整实现：变更记录列表、新增、详情、导出)

### 二、资产管理模块 (Management) - 待扩展
- [x] **资产借用/归还** - 借用管理 ✅ (前后端完成)
  - 前端：BorrowList.vue (已完整实现：借用列表查询、审批、归还操作)
  - 后端：AssetBorrowController/Service/Mapper/Entity 完整实现
    - 功能：CRUD + 审批/归还/取消/逾期提醒
    - API: /api/asset/borrow/* (8 个接口)
  - 数据库：asset_borrow 表已创建
- [ ] **资产变更** - 资产信息变更
  - 需创建完整流程
- [x] **资产调拨** - 部门间资产调拨 ✅ (前后端完成)
  - 前端：transfer/TransferList.vue, TransferForm.vue, TransferDetail.vue (已完整实现)
  - 注意：management/TransferList.vue 是占位符，应使用 transfer 目录下的实现

### 三、资产处置模块 (Disposal)
- [x] **报废申请/处置审批/出售捐赠** - 资产处置全流程 ✅ (前后端完成)
  - 前端：ScrapList.vue, ApprovalList.vue, SaleList.vue (已完整实现)
  - 后端：AssetDisposalController/Service/Mapper/Entity 完整实现
    - 功能：CRUD + 审批/执行/完成/取消
    - API: /api/asset/disposal/* (8 个接口)
  - 数据库：asset_disposal 表已创建
- [ ] **残值回收** - 处置收益管理

### 四、资产定位模块 (Location Tracking)
- [ ] **位置监控** - 资产实时位置查看
- [ ] **轨迹追踪** - 资产移动历史记录

### 五、资产盘点模块 (Inventory)
- [x] **盘点任务** - 盘点任务执行 ✅ (前后端完成)
  - 前端：TaskList.vue (已完整实现)
  - 后端：InventoryTaskController/Service/Mapper/Entity 完整实现
  - 数据库：inventory_task 表已创建
- [x] **盘点计划** - 盘点计划创建 ✅ (前后端完成)
  - 前端：PlanList.vue (已完整实现：列表查询、新增/编辑、启用/停用、执行计划)
  - 后端：InventoryPlanController/Service/Mapper/Entity 完整实现
    - 功能：CRUD + 启用/停用/执行计划
    - API: /api/inventory/plan/* (7 个接口)
  - 数据库：inventory_plan 表已创建
- [x] **盘点结果** - 盘盈盘亏处理 ✅ (前后端完成)
  - 前端：ResultList.vue (已完整实现：列表查询、提交/复核/处理、批量导入)
  - 后端：InventoryResultController/Service/Mapper/Entity 完整实现
    - 功能：CRUD + 提交/复核/处理 + 批量导入
    - API: /api/inventory/result/* (9 个接口)
  - 数据库：inventory_result 表已创建

### 六、资产门户模块 (Portal)
- [ ] **我的资产** - 个人名下资产查看
  - 前端：MyAssets.vue (占位符，待开发)
- [ ] **部门资产** - 部门资产视图
  - 前端：DeptAssets.vue (占位符，待开发)
- [ ] **资产公告** - 资产管理相关通知

### 七、资产报表模块 (Report)
- [ ] **资产台账** - 资产总览报表
- [ ] **折旧报表** - 资产折旧明细
  - 前端：DepreciationReport.vue (占位符，待开发)
- [ ] **统计报表** - 多维度统计分析
  - 前端：StatisticsReport.vue (占位符，待开发)
- [ ] **处置报表** - 资产处置统计
  - 前端：DisposalReport.vue (占位符，待开发)

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
  - 前端：TransferList.vue, TransferForm.vue, TransferDetail.vue (位于 transfer 目录)
  - 数据库：asset_transfer 表已创建
  
- [x] **资产维修** - 维护资产状态记录
  - 状态：已完成 ✅
  - 后端：AssetMaintenanceController, AssetMaintenanceService, AssetMaintenanceMapper, AssetMaintenance Entity
  - 前端：MaintenanceList.vue (完整实现：列表查询、新增/编辑申请、审批、开始维修、完成维修、删除)
  - 数据库：asset_maintenance 表已创建

### 🟡 P1 - 重要功能 (下一迭代)
- [x] **资产盘点任务** - 盘点任务执行功能 ✅
  - 状态：已完成 ✅
  - 后端：InventoryTaskController, InventoryTaskService, InventoryTaskMapper, InventoryTask Entity
  - 前端：TaskList.vue 完整实现
  - 数据库：inventory_task 表已创建
- [x] **资产盘点计划/结果** - 盘点全流程 ✅
  - 状态：已完成 ✅
  - 后端：InventoryPlanController/Service, InventoryResultController/Service 完整实现
  - 前端：PlanList.vue, ResultList.vue (已完整实现)
  - 数据库：inventory_plan, inventory_result 表已创建
- [x] **资产处置** - 资产生命周期结束管理 ✅
  - 状态：前后端均已完成
  - 后端：AssetDisposalController/Service 完整实现 (报废/出售/捐赠)
  - 前端：ScrapList.vue, ApprovalList.vue, SaleList.vue (已完整实现)
  - 数据库：asset_disposal 表已创建
- [x] **资产借用/归还** - 日常借用管理 ✅
  - 状态：已完成 ✅
  - 后端：AssetBorrowController/Service 完整实现
  - 前端：BorrowList.vue (已完整实现)
  - 数据库：asset_borrow 表已创建

### 🟢 P2 - 辅助功能 (后续迭代)
- [ ] **报表统计** - 数据分析决策支持
  - 资产台账、折旧报表、统计报表、处置报表
- [ ] **资产门户** - 用户体验提升
  - 我的资产、部门资产、资产公告
- [x] **附件管理** - 资产相关文件管理 ✅ (前端完成，模拟 API)
- [x] **变更记录** - 资产变更历史记录 ✅ (前端完成，模拟 API)
- [ ] **资产定位** - 高级追踪功能 (前后端均需开发)
- [ ] **资产变更** - 资产信息变更流程 (前后端均需开发)
- [ ] **残值回收** - 处置收益管理 (前后端均需开发)

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

## 下一步开发建议

基于当前项目进度 (89% 完成率)，建议按以下优先级继续开发:

### 🔴 P1 - 高优先级 (建议下一迭代完成)

1. **修复资产管理模块占位符页面** (1 个页面)
   - `management/TransferList.vue` - 替换为实际调拨功能页面
     - 现状：该文件是占位符，但完整的调拨功能已在 `transfer/` 目录下实现
     - 建议：删除或重定向到 `transfer/TransferList.vue`

### 🟡 P2 - 中优先级 (后续迭代)

2. **资产门户模块** (前端开发为主，2 个页面)
   - `portal/MyAssets.vue` - 我的资产
     - 功能：查看个人名下资产列表、资产详情
     - 可复用现有 AssetCard 查询接口，增加按用户筛选
     - 技术要点：使用当前登录用户 ID 过滤资产列表
   - `portal/DeptAssets.vue` - 部门资产
     - 功能：查看本部门资产列表、统计信息
     - 可复用现有 AssetCard 查询接口，增加按部门筛选
     - 技术要点：使用当前用户所属部门 ID 过滤资产列表

3. **资产报表模块** (需要复杂查询和图表展示，4 个页面)
   - `report/DepreciationReport.vue` - 折旧报表
     - 功能：展示资产折旧明细、累计折旧
     - 建议：使用 ECharts 展示折旧趋势图
   - `report/StatisticsReport.vue` - 统计报表
     - 功能：多维度统计分析 (按分类、部门、位置等)
     - 建议：使用饼图、柱状图展示分布情况
   - `report/DisposalReport.vue` - 处置报表
     - 功能：资产处置统计、残值回收统计
     - 建议：展示处置方式分布、处置金额统计

### 🟢 P3 - 低优先级 (长期规划)

4. **高级功能模块**
   - 资产定位模块 (位置监控、轨迹追踪) - 需硬件支持
   - 资产变更流程 - 完整的变更审批流程
   - 残值回收管理 - 处置收益记录
   - 资产公告 - 系统通知功能

---

## 技术债务与优化建议

### 代码层面
1. **统一 API 调用规范**: 确保所有前端页面使用统一的错误处理和 loading 状态
2. **组件复用**: 提取通用表单组件、表格组件，减少重复代码
3. **类型安全**: 考虑引入 TypeScript 提升代码质量

### 功能层面
1. **权限控制细化**: 当前基于角色的权限控制可进一步细化到按钮级别
2. **数据导出功能**: 为所有列表页面添加 Excel 导出功能
3. **批量操作优化**: 完善批量导入、批量删除等操作的体验

### 性能层面
1. **数据库索引优化**: 针对常用查询条件添加索引
2. **前端懒加载**: 对大型列表实施虚拟滚动
3. **缓存策略**: 对基础数据 (如分类、位置) 实施前端缓存

---

## 当前进度 (2025-06-01 更新)

- **总功能点**: 27 个核心模块

- **已完成**: 24 个核心功能模块 (89%)

  - 核心基础：用户认证、Dashboard ✅ (2)

  - 基础信息：资产分类、存放位置 (后端 + 前端完整实现) ✅ (2)

  - 资产取得：采购申请、验收登记、资产入库 ✅ (3)

  - 资产管理：领用退库、资产调拨、资产维修、资产借用 ✅ (4)

  - 资产档案：资产卡片 ✅ (1)

  - 资产盘点：盘点任务、盘点计划、盘点结果 ✅ (3)

  - 资产处置：报废申请/处置审批/出售捐赠 (后端 + 前端完整实现) ✅ (3)

  - 档案扩展：附件管理、变更记录、维保记录 (前端完整实现) ✅ (3)

  - 前端页面：CategoryList, LocationList, PurchaseList, AcceptanceList, StorageList, RequisitionList, BorrowList, TransferList/Form/Detail, MaintenanceList, AssetList/Form/Detail, TaskList, PlanList, ResultList, ScrapList, ApprovalList, SaleList, AttachmentList, ChangeList ✅ (24)

- **待开发**: 5 个功能模块 (11%)

  - 资产门户：我的资产、部门资产、资产公告 ❌ (3)

  - 资产报表：资产台账、折旧报表、统计报表、处置报表 ❌ (4)

- **完成率**: 约 89% (24/27 核心模块)



**代码统计**:

- 后端 Java 文件：78 个 (Controller: 15, Entity: 15, Service: 15, ServiceImpl: 15, Mapper: 15, 其他：3)

- 前端 Vue 文件：30 个 (views 页面)

- 数据库表：11 张



**前端页面状态**:

- ✅ 已完整实现：24 个页面

- ❌ 占位符页面：6 个页面 (MyAssets, DeptAssets, 报表类 3 个，management/TransferList)


## 前端页面状态明细

### ✅ 已完整实现的页面 (24 个)
| 模块 | 页面文件 | 状态 |
|------|---------|------|
| 基础信息 | CategoryList.vue | ✅ 树形表格、CRUD、子分类管理 |
| 基础信息 | LocationList.vue | ✅ 树形表格、CRUD、子位置管理 |
| 资产取得 | PurchaseList.vue | ✅ 采购申请完整流程 |
| 资产取得 | AcceptanceList.vue | ✅ 验收登记完整流程 |
| 资产取得 | StorageList.vue | ✅ 资产入库完整流程 |
| 资产管理 | RequisitionList.vue | ✅ 领用退库完整流程 |
| 资产管理 | transfer/TransferList.vue | ✅ 调拨列表 |
| 资产管理 | transfer/TransferForm.vue | ✅ 调拨申请表单 |
| 资产管理 | transfer/TransferDetail.vue | ✅ 调拨详情 |
| 资产管理 | archive/MaintenanceList.vue | ✅ 维修申请、审批、执行完整流程 |
| 资产管理 | management/BorrowList.vue | ✅ 借用归还列表、审批、归还操作 |
| 资产处置 | disposal/ScrapList.vue | ✅ 报废申请列表、审批、删除操作 |
| 资产处置 | disposal/ApprovalList.vue | ✅ 处置审批列表、审批通过/拒绝、执行处置、完成/取消、查看详情 |
| 资产处置 | disposal/SaleList.vue | ✅ 出售捐赠列表、审批通过/拒绝、执行处置、完成/取消、查看详情 |
| 资产档案 | asset/AssetList.vue | ✅ 资产卡片列表 |
| 资产档案 | asset/AssetForm.vue | ✅ 资产卡片表单 |
| 资产档案 | asset/AssetDetail.vue | ✅ 资产卡片详情 |
| 资产盘点 | inventory/TaskList.vue | ✅ 盘点任务完整流程 |
| 资产盘点 | inventory/PlanList.vue | ✅ 盘点计划列表、新增/编辑、启用/停用、执行 |
| 资产盘点 | inventory/ResultList.vue | ✅ 盘点结果列表、提交/复核/处理、批量导入 |
| 核心基础 | Dashboard.vue | ✅ 工作台 |
| 核心基础 | Login.vue | ✅ 登录页 |
| 档案扩展 | archive/AttachmentList.vue | ✅ 附件列表、上传、下载、预览、删除、批量操作 (模拟 API) |
| 档案扩展 | archive/ChangeList.vue | ✅ 变更记录列表、新增、详情、导出 (模拟 API) |

### ❌ 待开发的占位符页面 (6 个)
| 模块 | 页面文件 | 状态 |
|------|---------|------|
| 资产管理 | management/TransferList.vue | ❌ 占位符 - 需使用 transfer 目录下的完整实现 |
| 资产门户 | portal/MyAssets.vue | ❌ 占位符 - 待开发 |
| 资产门户 | portal/DeptAssets.vue | ❌ 占位符 - 待开发 |
| 资产报表 | report/DepreciationReport.vue | ❌ 占位符 - 待开发 |
| 资产报表 | report/StatisticsReport.vue | ❌ 占位符 - 待开发 |
| 资产报表 | report/DisposalReport.vue | ❌ 占位符 - 待开发 |

## 最近更新

### 2025-06-01: 项目进度分析与 TODO 文档更新
- **项目整体进度**: 89% (24/27 核心模块完成)
- **代码统计**: 
  - 后端 Java 文件：78 个 (Controller: 15, Entity: 15, Service: 15, ServiceImpl: 15, Mapper: 15)
  - 前端 Vue 文件：30 个 (views 页面，已完整实现：24 个，占位符：6 个)
  - 数据库表：11 张
- **更新内容**:
  - 分析项目完成情况，发现 `management/TransferList.vue` 是占位符，但完整的调拨功能已在 `transfer/` 目录下实现
  - 确认档案扩展模块 (AttachmentList, ChangeList) 已完整实现 (模拟 API)
  - 更新 TODO.md 中当前进度部分 (完成率从 96% 修正为 89%)
  - 更新前端页面状态明细表 (已完整实现 24 个页面，待开发 6 个页面)
  - 新增"技术债务与优化建议"章节，涵盖代码、功能、性能三个层面
  - 更新下一步开发建议，明确优先级和具体实施要点
- **下一步开发建议**:
  1. **P1 优先级**：修复 `management/TransferList.vue` 占位符问题
  2. **P2 优先级**：资产门户模块 (MyAssets, DeptAssets) - 可复用现有 AssetCard 查询接口
  3. **P3 优先级**：资产报表模块 (折旧报表、统计报表、处置报表) - 需图表库支持

- 2025-05-29: 项目进度分析与文档更新
  - 前端：inventory/PlanList.vue 完整实现 (盘点计划列表查询、新增/编辑、启用/停用、执行计划)
  - 前端：inventory/ResultList.vue 完整实现 (盘点结果列表查询、提交/复核/处理、批量导入)
  - API：inventory.js 新增盘点计划和盘点结果相关接口 (getPlanList, getPlanById, createPlan, updatePlan, deletePlan, executePlan, enablePlan, disablePlan, getResultList, getResultById, createResult, updateResult, deleteResult, submitResult, reviewResult, handleResult, importResult)
  - 更新 TODO.md 前端页面状态表

- 2025-05-29: 完成资产借用归还、报废申请前端页面开发
  - 前端：management/BorrowList.vue 完整实现 (借用列表查询、审批、归还操作)
  - 前端：disposal/ScrapList.vue 完整实现 (处置列表查询、审批、删除操作)
  - 更新 TODO.md 前端页面状态表

- 2025-05-29: 完成资产盘点计划、资产处置、资产借用功能开发 (后端)
  - 新增后端服务实现：
    - InventoryPlanServiceImpl: 盘点计划 CRUD + 启用/停用/执行
    - InventoryResultServiceImpl: 盘点结果 CRUD + 提交/复核/处理 + 批量导入
    - AssetDisposalServiceImpl: 资产处置 CRUD + 审批/执行/完成/取消
    - AssetBorrowServiceImpl: 资产借用 CRUD + 审批/归还/取消/逾期提醒
  - 新增后端控制器：
    - InventoryPlanController: /api/inventory/plan/* (7 个接口)
    - InventoryResultController: /api/inventory/result/* (9 个接口)
    - AssetDisposalController: /api/asset/disposal/* (8 个接口)
    - AssetBorrowController: /api/asset/borrow/* (8 个接口)
  - 数据库表已存在：inventory_plan, inventory_result, asset_disposal, asset_borrow

- 2025-05-29: 更新项目进度分析
  - 确认前端完整实现页面共 16 个
  - 确认占位符页面共 13 个
  - 后端 Controller 共 15 个，Entity 共 15 个
  
- 2025-05-25: 完成维保记录管理功能开发 (前端)
  - 新增 API：basic.js 中增加维保记录相关接口 (getMaintenanceList, getMaintenanceById, createMaintenance, updateMaintenance, deleteMaintenance, approveMaintenance, startMaintenance, completeMaintenance, cancelMaintenance)
  - 前端：MaintenanceList.vue 完整实现
    - 列表查询：支持按资产编码、资产名称、维修结果、审批状态筛选
    - 新增/编辑维保申请：包含资产信息、维修类型、故障类型、故障描述等字段
    - 审批流程：支持通过/拒绝审批，填写审批意见
    - 维修执行：开始维修 (指定维修人)、完成维修 (填写维修方式、费用、结果)
    - 详情查看：完整的维保记录信息展示
    - 删除功能：带二次确认的删除操作
  - 后端：AssetMaintenanceController/Service/Mapper 已存在
  
- 2024-xx-xx: 完成资产盘点任务功能开发 (前后端)
  - 后端：InventoryTaskController, InventoryTaskService, InventoryTaskServiceImpl, InventoryTaskMapper, InventoryTask Entity
  - 前端：TaskList.vue 完整实现
  - 数据库：inventory_task 表已创建
  
- 2025-05-25: 完成资产分类和存放位置管理前端开发
  - 新增 API：basic.js (包含分类和位置的 CRUD 接口)
  - 前端：CategoryList.vue 完整实现 (树形表格、搜索、新增/编辑/删除、子分类管理)
  - 前端：LocationList.vue 完整实现 (树形表格、搜索、新增/编辑/删除、子位置管理)
  - 后端：AssetCategoryController/Service/Mapper 已存在
  - 后端：AssetLocationController/Service/Mapper 已存在
  
- 2024-xx-xx: 完成资产维修功能开发 (前后端)
  - 后端：Entity, Mapper, Service, Controller 完整实现
  - 前端：archive/MaintenanceList.vue 完整实现 (维修申请、审批、执行全流程)
  - 数据库：asset_maintenance 表已创建
  
- 2024-xx-xx: 完成资产调拨功能开发 (前后端)
  - 后端：Entity, Mapper, Service, Controller 完整实现
  - 前端：TransferList, TransferForm, TransferDetail 完整实现 (位于 transfer 目录)
  - 数据库：asset_transfer 表已添加到 schema.sql
  
- 2024-xx-xx: 完成资产卡片功能开发 (前后端)
  - 后端：AssetController, AssetService, AssetCardMapper 完整实现
  - 前端：AssetList, AssetForm, AssetDetail 完整实现
  
- 2024-xx-xx: 完成领用退库功能开发 (前后端)
- 2024-xx-xx: 完成资产入库功能开发 (前后端)
- 2024-xx-xx: 完成验收登记功能开发 (前后端)
- 2024-xx-xx: 完成采购申请功能开发 (前后端)
- 2024-xx-xx: 完成基础框架搭建
