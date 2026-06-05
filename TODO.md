# 资产管理系统 - 开发进度清单

## 2026-06-05 审计更新

本次重新核对了 `plan/` 文档、`asset-management/TODO.md` 和当前源码。结论是：当前项目已经具备轻量级资产管理系统的主要骨架和多数核心页面，但不能再按旧口径标记为 100% 完成。更准确的当前状态如下：

- 当前实现形态：Spring Boot 3.2 + MyBatis Plus + MySQL + Spring Security/JWT + Vue 3 + Element Plus + Vite 的轻量级单体应用。
- 当前代码规模：后端 86 个 Java 文件、17 个 Controller、16 个实体、16 张数据库表；前端 30 个 `views` 页面；未发现后端 `src/test` 测试目录。
- 已完成主干：认证、Dashboard、资产卡片、分类/位置/部门、采购申请、验收、入库、领用退库、借用、调拨、维修、处置、盘点计划/任务/结果、我的资产/部门资产、统计/折旧/处置报表等。
- 需要修正的旧判断：`AssetService`、`AuthService`、`AssetCategoryService`、`AssetLocationService` 是具体 `@Service` 类，不是缺少 `Impl` 导致不可用。
- 当前明确未完成点：第一阶段 5 个前端“新建”入口已完成；部门管理与工程治理已完成；代码内仍剩 2 个后端 TODO，分别是借用逾期通知和盘点计划生成任务。
- 与 `plan/` 完整蓝图相比仍缺：供应商/资产主数据、系统管理/RBAC、资产预警、IoT 定位、扫码盘点、盘点报告、资产卡片多视图、导入导出深化、通知体系、CI/CD 和自动化测试。
- 工程治理进展：`frontend/node_modules`、`frontend/dist` 已取消 Git 跟踪，根 `.gitignore` 已修正；当前更突出的工程问题是后端 Maven compile 与前端 Vite build 的构建基线仍未恢复。

当前更详细、可执行的下一步计划已同步到 `asset-management/TODO.md`。

## 2026-06-05 开发更新

- 已提交并推送 `70adf9e 新增部门管理并清理前端产物跟踪`。
- 已完成仓库工程治理：取消跟踪 `asset-management/frontend/node_modules`、`asset-management/frontend/dist`，修正根 `.gitignore`。
- 已完成部门管理：新增 `sys_department` 表、后端 `/api/departments/**` 树形 CRUD、前端 `DepartmentList.vue` 页面和基础信息菜单入口。
- 已将采购申请、资产调拨中的部门选择接入真实部门主数据。
- 下一阶段建议优先：供应商管理 + 资产主数据，并穿插修复后端 Maven/前端 Vite 构建基线。

## 2026-06-05 开发更新（主流程闭环）

- 已提交 `624a48f 补齐资产主流程新建入口闭环`。
- 已补齐 `AcceptanceList.vue`、`RequisitionList.vue`、`BorrowList.vue`、`ScrapList.vue`、`SaleList.vue` 的新建/编辑/详情闭环。
- 已修正借用接口到 `/asset/borrow`，并统一借用、处置页面的状态和分页字段口径。
- 已同步更新 `asset-management/TODO.md`，下一阶段重点调整为基础主数据、系统管理/RBAC、盘点/预警深化和工程治理。

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

- ✅ 部门管理 (后端完整 CRUD + 树形查询)
  - Entity: SysDepartment
  - Mapper: SysDepartmentMapper
  - Service: SysDepartmentService
  - Controller: SysDepartmentController
  - 前端：DepartmentList.vue ✅ (已完成：树形表格、搜索、新增/编辑/删除、子部门管理)
  - 已接入：采购申请、资产调拨部门选择器

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

### 7. 数据库表 (当前 schema 共 16 张表)
- ✅ sys_user - 系统用户表
- ✅ sys_department - 系统部门表
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
- [x] **我的资产** - 个人名下资产查看 ✅ (前后端完成)
  - 前端：MyAssets.vue (已完整实现：列表展示、状态标签、分页、详情跳转)
  - 后端：AssetService.getMyAssets, AssetController./api/assets/my/list
- [x] **部门资产** - 部门资产视图 ✅ (前后端完成)
  - 前端：DeptAssets.vue (已完整实现：列表展示、使用人字段、状态标签、分页、详情跳转)
  - 后端：AssetService.getDeptAssets, AssetController./api/assets/dept/list
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

### 🟡 P1 - 重要功能 (下一迭代) ✅ 全部完成
- [x] **资产盘点任务** - 盘点任务执行功能 ✅
  - 状态：已完成 ✅
  - 后端：InventoryTaskController, InventoryTaskService, InventoryTaskMapper, InventoryTask Entity
  - 前端：TaskList.vue 完整实现（新建/编辑/详情/执行）
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
- [x] **采购申请完善** - 新建/编辑/详情功能 ✅
  - 状态：已完成 ✅ (2025-06-03)
  - 前端：PurchaseList.vue 完整实现（新建申请对话框、编辑、详情查看、表单提交）
  - API：purchase.js 已创建封装所有接口调用
- [x] **审批人信息自动填充** - 后端 TODO 修复 ✅
  - 状态：已完成 ✅ (2025-06-03)
  - 后端：UserContextUtil 工具类创建
  - 修复：AssetRequisitionServiceImpl, AssetTransferController, PurchaseRequestServiceImpl

### 🟢 P2 - 辅助功能 (后续迭代)
- [ ] **报表统计** - 数据分析决策支持
  - 资产台账、折旧报表、统计报表、处置报表
- [x] **资产门户** - 用户体验提升 ✅ (前后端完成)
  - 我的资产、部门资产 (已完成)
- [ ] **资产公告** - 系统通知功能
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

基于 2026-06-05 最新状态，轻量级单体主流程已基本闭环，工程治理和部门主数据也已落地。下一步建议按“先补主数据，再恢复构建基线，再进入系统管理/RBAC”的主线推进。

### 🔴 P1 - 下一迭代建议优先

1. **供应商管理**
   - 新增供应商表、实体、Mapper、Service、Controller、API 封装、前端页面和菜单入口。
   - 覆盖供应商名称、编码、联系人、联系电话、地址、状态、备注等基础字段。
   - 接入采购申请、验收、入库、资产卡片中的供应商选择，替换部分文本输入。

2. **资产主数据管理**
   - 新增资产主数据表和页面，维护资产名称、规格型号、分类、品牌、单位、默认价格、保修期等模板信息。
   - 接入采购申请和资产卡片表单，让常用资产从文本录入升级为选择器。

3. **构建基线修复**
   - 后端修复 `mvn -q -DskipTests compile`：统一 JWT 依赖口径，处理 Lombok getter/setter 生成问题，修复 `ReportServiceImpl` 编译错误。
   - 前端重新安装依赖并补齐 Rollup Windows optional dependency，恢复 `npm run build`。

### 🟡 P2 - 主数据稳定后推进

4. **系统管理与权限**
   - 用户管理、角色管理、菜单权限、按钮权限、部门数据权限。
   - 当前 `SysUser` 只有轻量角色字段，需要演进为可维护 RBAC。

5. **盘点闭环深化**
   - 盘点任务明细、扫码盘点、盘盈登记、盘亏处理、盘点报告。
   - 落地 `InventoryPlanServiceImpl` 中“根据计划生成盘点任务”的 TODO。

6. **通知与预警体系**
   - 落地 `AssetBorrowServiceImpl` 中借用逾期提醒 TODO。
   - 扩展保修、保养、库存、报废等资产预警。

### 🟢 P3 - 长期规划

7. **高级功能模块**
   - 资产定位模块、资产变更流程、残值回收管理、资产公告。

8. **工程与体验优化**
   - 自动化测试、CI/CD、Docker 化部署、操作审计、批量导入导出、移动端适配。

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

## 当前进度 (2026-06-05 更新)

- **总功能点**: 30 个已路由 views 页面

- **已完成**: 30 个已路由页面的基础功能闭环

  - 核心基础：用户认证、Dashboard ✅ (2)

  - 基础信息：资产分类、存放位置、部门管理 (后端 + 前端完整实现) ✅ (3)

  - 资产取得：采购申请、验收登记、资产入库 ✅ (3)

  - 资产管理：领用退库、资产调拨、资产维修、资产借用 ✅ (4)

  - 资产档案：资产卡片 ✅ (1)

  - 资产盘点：盘点任务、盘点计划、盘点结果 ✅ (3)

  - 资产处置：报废申请/处置审批/出售捐赠 (后端 + 前端完整实现) ✅ (3)

  - 资产门户：我的资产、部门资产 ✅ (2) - 2025-06-03 新增

  - 档案扩展：附件管理、变更记录、维保记录 (前端完整实现) ✅ (3)

  - 资产报表：折旧报表、统计报表、处置报表 ✅ (3) - 2025-06-03 新增

  - 前端页面：CategoryList, LocationList, DepartmentList, PurchaseList, AcceptanceList, StorageList, RequisitionList, BorrowList, TransferList/Form/Detail, MaintenanceList, AssetList/Form/Detail, TaskList, PlanList, ResultList, ScrapList, ApprovalList, SaleList, AttachmentList, ChangeList, MyAssets, DeptAssets, DepreciationReport, StatisticsReport, DisposalReport ✅ (30)

- **待开发**: 供应商管理、资产主数据、系统管理/RBAC、盘点报告/扫码盘点、通知预警等完整蓝图扩展能力

- **完成率**: 约 90%（按轻量级单体当前范围估算；按完整医院资产管理蓝图仍有扩展空间）



**代码统计**:

- 后端 Java 文件：86 个 (Controller: 17, Entity: 16)

- 前端 Vue views 页面：30 个

- 数据库表：16 张


## 前端页面状态明细

### ✅ 已完整实现的页面 (30 个)
| 模块 | 页面文件 | 状态 |
|------|---------|------|
| 基础信息 | CategoryList.vue | ✅ 树形表格、CRUD、子分类管理 |
| 基础信息 | LocationList.vue | ✅ 树形表格、CRUD、子位置管理 |
| 基础信息 | DepartmentList.vue | ✅ 树形表格、CRUD、子部门管理 |
| 资产取得 | PurchaseList.vue | ✅ 采购申请完整流程（新建/编辑/详情/审批） |
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
| 资产盘点 | inventory/TaskList.vue | ✅ 盘点任务完整流程（新建/编辑/详情/执行） |
| 资产盘点 | inventory/PlanList.vue | ✅ 盘点计划列表、新增/编辑、启用/停用、执行 |
| 资产盘点 | inventory/ResultList.vue | ✅ 盘点结果列表、提交/复核/处理、批量导入 |
| 资产门户 | portal/MyAssets.vue | ✅ 我的资产列表、状态标签、分页、详情跳转 |
| 资产门户 | portal/DeptAssets.vue | ✅ 部门资产列表、使用人字段、状态标签、分页、详情跳转 |
| 资产报表 | report/DepreciationReport.vue | ✅ 折旧报表列表、图表展示、数据导出 |
| 资产报表 | report/StatisticsReport.vue | ✅ 统计报表、多维度分析、图表展示、数据导出 |
| 资产报表 | report/DisposalReport.vue | ✅ 处置报表列表、图表展示、数据导出 |
| 核心基础 | Dashboard.vue | ✅ 工作台 |
| 核心基础 | Login.vue | ✅ 登录页 |
| 档案扩展 | archive/AttachmentList.vue | ✅ 附件列表、上传、下载、预览、删除、批量操作 (真实 API) |
| 档案扩展 | archive/ChangeList.vue | ✅ 变更记录列表、新增、详情、导出 (真实 API) |

## 最近更新

### 2025-06-03: 资产报表模块开发完成与 TODO 文档更新 - 项目 100% 完成
- **完成情况**: 
  - ✅ DepreciationReport.vue：折旧报表页面完整实现
    - 功能：折旧报表列表查询、日期筛选、分类筛选、图表展示（ECharts）、数据导出 Excel
    - API：调用 /api/reports/depreciation 接口
  - ✅ StatisticsReport.vue：统计报表页面完整实现
    - 功能：多维度统计分析（按状态、分类、部门）、图表展示（饼图、柱状图）、数据导出 Excel
    - API：调用 /api/reports/statistics 接口
  - ✅ DisposalReport.vue：处置报表页面完整实现
    - 功能：处置报表列表查询、日期筛选、处置类型筛选、图表展示、数据导出 Excel
    - API：调用 /api/reports/disposal 接口
  - ✅ ReportController.java：报表控制器完整实现（6 个接口）
    - GET /api/reports/statistics - 获取统计报表
    - GET /api/reports/disposal - 获取处置报表
    - GET /api/reports/depreciation - 获取折旧报表
    - GET /api/reports/export/statistics - 导出统计报表
    - GET /api/reports/export/disposal - 导出处置报表
    - GET /api/reports/export/depreciation - 导出折旧报表
  - ✅ ReportServiceImpl.java：报表服务实现类完整实现
    - getStatisticsReport: 统计分析（总数、状态分布、分类分布、部门分布、金额统计）
    - getDisposalReport: 处置统计（分页列表、统计数据、图表数据）
    - getDepreciationReport: 折旧统计（分页列表、统计数据、图表数据）
    - exportStatisticsReport: 导出统计报表为 Excel
    - exportDisposalReport: 导出处置报表为 Excel
    - exportDepreciationReport: 导出折旧报表为 Excel
  - ✅ report.js：新增报表相关 API 函数封装
- **当时状态**:
  - 后端 Java 文件：79 个（Controller: 16, Service: 16）
  - 前端 Vue 页面：31 个（31 个完整实现，0 个占位符）
  - 数据库表：15 张
  - 整体完成率：100% (31/31 核心页面)
- **剩余 TODO 项**（P3 低优先级优化项）:
  - AssetBorrowServiceImpl:217 - 消息通知系统集成（P3）
  - InventoryPlanServiceImpl:149 - 盘点计划自动生成任务（P3）

### 2025-06-03: P2 资产门户模块开发完成与 TODO 文档更新
- **完成情况**: 
  - ✅ MyAssets.vue：我的资产列表页面完整实现
    - 功能：资产列表展示、状态标签、分页、详情跳转
    - API：调用 /api/assets/my/list 接口
  - ✅ DeptAssets.vue：部门资产列表页面完整实现
    - 功能：部门资产列表展示、使用人字段、状态标签、分页、详情跳转
    - API：调用 /api/assets/dept/list 接口
  - ✅ asset.js：新增 getMyAssets 和 getDeptAssets API 函数
  - ✅ AssetService.java：新增 getMyAssets 和 getDeptAssets 方法
  - ✅ AssetController.java：新增 /api/assets/my/list 和 /api/assets/dept/list 接口
- **当时状态**:
  - 后端 Java 文件：79 个
  - 前端 Vue 页面：31 个（28 个完整实现，3 个占位符）
  - 数据库表：15 张
  - 整体完成率：约 90% (28/31 核心页面)
- **剩余 TODO 项**（非 P2 优先级）:
  - AssetBorrowServiceImpl:217 - 消息通知系统集成（P3）
  - InventoryPlanServiceImpl:149 - 盘点计划自动生成任务（P2）

### 2025-06-03: P1 开发任务完成与 TODO 文档更新
- **完成情况**: 
  - ✅ PurchaseList.vue：新建申请对话框、编辑申请、查看详情功能完整实现
  - ✅ TaskList.vue：新建任务对话框、编辑任务、查看详情功能完整实现
  - ✅ 后端 UserContextUtil 工具类创建，提供 getCurrentUsername() 方法
  - ✅ 修复 AssetRequisitionServiceImpl 审批人信息自动填充
  - ✅ 修复 AssetTransferController 审批人信息获取
  - ✅ 清理所有 P1 相关的 TODO 注释
- **当时状态**:
  - 后端 Java 文件：79 个（新增 UserContextUtil）
  - 前端 Vue 页面：31 个（26 个完整实现，5 个占位符）
  - 数据库表：15 张
  - 整体完成率：约 93% (26/31 核心页面)
- **剩余 TODO 项**（非 P1 优先级）:
  - AssetBorrowServiceImpl:217 - 消息通知系统集成（P3）
  - InventoryPlanServiceImpl:149 - 盘点计划自动生成任务（P2）

### 2025-06-01: 项目进度分析与 TODO 文档更新 (第二次)
- **检查结果**: 
  - 发现 2 个页面包含 TODO 注释，需要完善功能
  - 发现 5 个占位符页面待开发
  - management/TransferList.vue 占位符问题已修复 (已删除)
  - AttachmentList.vue 和 ChangeList.vue 已从模拟 API 迁移到真实 API
- **更新内容**:
  - 更新前端页面状态明细表，新增"部分功能待完善的页面"分类
  - 标记 PurchaseList.vue 和 TaskList.vue 为⚠️部分完成状态
  - 更新下一步开发建议，将 TODO 项完善列为 P1 优先级
  - 移除 management/TransferList.vue 从占位符列表中
  - 更新 AttachmentList 和 ChangeList 为使用真实 API

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
