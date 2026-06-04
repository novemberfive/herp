# 资产管理系统开发 TODO

## 项目进度概览

**总体进度：95% (29/31 核心页面)**

*最后更新时间：2024-06-04*

---

## ✅ 已完成模块

### 1. 资产管理模块 (100%)
- [x] 资产列表 (AssetList.vue)
- [x] 我的资产 (MyAssets.vue) - P2
- [x] 部门资产 (DeptAssets.vue) - P2
- [x] 资产详情 (AssetDetail.vue)
- [x] 资产创建/编辑 (AssetForm.vue)

### 2. 采购管理模块 (100%)
- [x] 采购申请列表 (PurchaseRequestList.vue → RequisitionList.vue)
- [x] 采购清单 (PurchaseList.vue)
- [x] 入库质检 (StorageList.vue)
- [x] 验收管理 (AcceptanceList.vue)

### 3. 资产处置模块 (100%)
- [x] 处置审批列表 (ApprovalList.vue)
- [x] 出售列表 (SaleList.vue)
- [x] 报废列表 (ScrapList.vue)

### 4. 资产调拨模块 (100%)
- [x] 调拨申请列表 (TransferList.vue)
- [x] 调拨申请详情 (TransferDetail.vue)
- [x] 调拨申请创建 (TransferForm.vue)

### 5. 库存盘点模块 (100%)
- [x] 盘点计划列表 (PlanList.vue)
- [x] 盘点任务列表 (TaskList.vue)
- [x] 盘点结果录入 (ResultList.vue)

### 6. 报表模块 (100%)
- [x] 统计报表 (StatisticsReport.vue) - ECharts 图表展示
- [x] 处置报表 (DisposalReport.vue) - 分页表格 + 统计卡片
- [x] 折旧报表 (DepreciationReport.vue) - 进度条 + 折旧计算

### 7. 基础数据模块 (67%)
- [x] 分类管理 (CategoryList.vue)
- [x] 位置管理 (LocationList.vue)
- [ ] 部门管理 (DepartmentList.vue) - **缺失**
- [ ] 供应商管理 (SupplierList.vue) - **缺失**

### 8. 系统管理模块 (0%)
- [ ] 用户管理 (UserList.vue) - **缺失**
- [ ] 角色管理 (RoleList.vue) - **缺失**
- [x] 登录页面 (Login.vue)

### 9. 资产档案模块 (100%)
- [x] 附件管理 (AttachmentList.vue)
- [x] 变更管理 (ChangeList.vue)
- [x] 维修管理 (MaintenanceList.vue)

### 10. 资产领用模块 (100%)
- [x] 借用列表 (BorrowList.vue)
- [x] 领用列表 (RequisitionList.vue)

---

## 🔧 后端服务完成情况

### 已实现 Service (16个接口，12个实现)
- [x] AssetService - 资产管理服务 (**实现缺失**)
- [x] AssetBorrowService - 借用服务
- [x] AssetDisposalService - 处置服务
- [x] AssetTransferService - 调拨服务
- [x] AssetRequisitionService - 领用服务
- [x] AssetAcceptanceService - 验收服务
- [x] AssetStorageService - 入库服务
- [x] AssetMaintenanceService - 维修服务
- [x] PurchaseRequestService - 采购申请服务
- [x] InventoryPlanService - 盘点计划服务
- [x] InventoryTaskService - 盘点任务服务
- [x] InventoryResultService - 盘点结果服务
- [x] ReportService - 报表服务
- [x] AuthService - 认证服务 (**实现缺失**)
- [x] AssetCategoryService - 分类服务 (**实现缺失，逻辑在 Service 文件中**)
- [x] AssetLocationService - 位置服务 (**实现缺失，逻辑在 Service 文件中**)

### 已实现 Controller (16个)
- [x] AssetController
- [x] AssetBorrowController
- [x] AssetDisposalController
- [x] AssetTransferController
- [x] AssetRequisitionController
- [x] AssetAcceptanceController
- [x] AssetStorageController
- [x] AssetMaintenanceController
- [x] PurchaseRequestController
- [x] InventoryPlanController
- [x] InventoryTaskController
- [x] InventoryResultController
- [x] ReportController
- [x] AuthController
- [x] AssetCategoryController
- [x] AssetLocationController

### 数据访问层 (15个)
- **Mapper**: 11个 (AssetBorrowMapper, AssetCardMapper, AssetCategoryMapper, AssetDisposalMapper, AssetLocationMapper, AssetMaintenanceMapper, AssetTransferMapper, InventoryPlanMapper, InventoryResultMapper, InventoryTaskMapper, SysUserMapper)
- **Repository**: 4个 (AssetAcceptanceRepository, AssetRequisitionRepository, AssetStorageRepository, PurchaseRequestRepository)

### 实体类 (15个)
- AssetAcceptance, AssetBorrow, AssetCard, AssetCategory, AssetDisposal, AssetLocation
- AssetMaintenance, AssetRequisition, AssetStorage, AssetTransfer
- InventoryPlan, InventoryResult, InventoryTask, PurchaseRequest, SysUser

---

## 📋 待办事项 (按优先级排序)

### P0 - 高优先级 (核心功能缺失)

1. **[后端] AssetServiceImpl 实现**
   - 说明：AssetService 接口已存在但缺少实现类
   - 影响：资产核心管理功能无法正常工作
   - 优先级：P0

2. **[后端] AuthServiceImpl 实现**
   - 说明：AuthService 接口已存在但缺少实现类
   - 影响：用户认证和授权功能无法工作
   - 优先级：P0

3. **[前端] 系统管理模块**
   - 文件：`UserList.vue`, `RoleList.vue`
   - 说明：用户管理和角色管理页面缺失
   - 优先级：P0

4. **[前端] 基础数据模块补充**
   - 文件：`DepartmentList.vue`, `SupplierList.vue`
   - 说明：部门管理和供应商管理页面缺失
   - 优先级：P0

### P1 - 中优先级 (功能完善)

5. **[后端] 分类和位置服务重构**
   - 文件：`AssetCategoryService.java`, `AssetLocationService.java`
   - 说明：当前业务逻辑直接写在 Service 类中，需要提取到 ServiceImpl
   - 优先级：P1

6. **[后端] 资产主数据管理**
   - 说明：根据需求文档，需要实现资产主数据的新建、修改、删除、启用、停用、预警等功能
   - 优先级：P1

7. **[前端] 资产卡片视图**
   - 说明：需要实现按类型、按使用状态、临期提醒等多种资产卡片视图
   - 优先级：P1

### P2 - 低优先级 (优化项)

8. **[后端] 消息通知系统集成**
   - 文件：`AssetBorrowServiceImpl.java:217`
   - 说明：归还提醒消息推送功能
   - 优先级：P2

9. **[后端] 盘点计划自动生成任务**
   - 文件：`InventoryPlanServiceImpl.java:149`
   - 说明：定时任务自动生成盘点计划
   - 优先级：P2

10. **[前端] 报表导出功能**
    - 说明：支持 Excel/PDF 格式导出
    - 优先级：P2

11. **[前端] 高级搜索组件封装**
    - 说明：提取通用搜索表单组件
    - 优先级：P2

12. **[优化] 数据库索引优化**
    - 说明：针对常用查询条件添加索引
    - 优先级：P2

---

## 🎯 下一步开发建议

### 推荐开发顺序：

#### 第一阶段：完成核心功能 (P0)
1. **实现 AssetServiceImpl** - 确保资产核心管理功能完整
2. **实现 AuthServiceImpl** - 完善用户认证和权限管理
3. **开发系统管理页面** - UserList.vue, RoleList.vue
4. **开发基础数据页面** - DepartmentList.vue, SupplierList.vue

#### 第二阶段：功能完善 (P1)
1. **重构分类和位置服务** - 统一服务层架构
2. **实现资产主数据管理** - 完善资产基础信息管理
3. **开发资产卡片视图** - 实现多种维度的资产展示

#### 第三阶段：优化提升 (P2)
1. **消息通知系统** - 提升用户体验
2. **定时任务** - 自动化业务流程
3. **报表导出** - 增强数据分析能力
4. **组件封装** - 提高代码复用性
5. **性能优化** - 数据库索引、缓存优化

---

## 📊 技术指标

- **前端页面总数**: 29 个
- **后端 Controller**: 16 个
- **后端 Service**: 16 个接口 / 12 个实现 (75%)
- **数据访问层**: 15 个 (11 Mapper + 4 Repository)
- **实体类**: 15 个
- **API 文档**: 待完善
- **单元测试**: 待编写

---

## ⚠️ 发现的问题

1. **服务层架构不统一**: 
   - AssetCategoryService 和 AssetLocationService 的业务逻辑直接写在 Service 类中，而非 ServiceImpl
   - AssetService 和 AuthService 缺少实现类

2. **前端模块命名不一致**:
   - 采购申请：PurchaseRequestList.vue → RequisitionList.vue
   - 处置模块：使用 ApprovalList/SaleList/ScrapList 替代了原有的 DisposalList/DisposalForm/DisposalDetail

3. **缺失功能**:
   - 系统管理模块（用户管理、角色管理）页面完全缺失
   - 基础数据模块缺少部门和供应商管理页面

---

*最后更新：2024-06-04*
