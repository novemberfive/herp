import { createRouter, createWebHistory } from 'vue-router'
import { ROLE_PERMISSION_MAP, expandPermissions } from '@/constants/permissions'

function getStoredPermissions() {
  try {
    const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')
    return expandPermissions(userInfo.permissions?.length ? userInfo.permissions : (ROLE_PERMISSION_MAP[userInfo.role] || []))
  } catch (error) {
    return []
  }
}

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue'),
    meta: { requiresAuth: false }
  },
  {
    path: '/',
    name: 'Layout',
    component: () => import('@/layouts/MainLayout.vue'),
    redirect: '/dashboard',
    meta: { requiresAuth: true },
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/Dashboard.vue'),
        meta: { title: '工作台', permission: 'dashboard:view' }
      },
      {
        path: 'assets',
        name: 'AssetList',
        component: () => import('@/views/asset/AssetList.vue'),
        meta: { title: '资产列表', permission: 'asset:list' }
      },
      {
        path: 'assets/create',
        name: 'AssetCreate',
        component: () => import('@/views/asset/AssetForm.vue'),
        meta: { title: '新增资产', permission: 'asset:create' }
      },
      {
        path: 'assets/:id/edit',
        name: 'AssetEdit',
        component: () => import('@/views/asset/AssetForm.vue'),
        meta: { title: '编辑资产', permission: 'asset:edit' }
      },
      {
        path: 'assets/:id/detail',
        name: 'AssetDetail',
        component: () => import('@/views/asset/AssetDetail.vue'),
        meta: { title: '资产详情', permission: 'asset:list' }
      },
      // 资产取得模块
      {
        path: 'acquisition/purchase',
        name: 'PurchaseList',
        component: () => import('@/views/acquisition/PurchaseList.vue'),
        meta: { title: '采购申请', permission: 'acquisition:purchase' }
      },
      {
        path: 'acquisition/acceptance',
        name: 'AcceptanceList',
        component: () => import('@/views/acquisition/AcceptanceList.vue'),
        meta: { title: '验收登记', permission: 'acquisition:acceptance' }
      },
      {
        path: 'acquisition/storage',
        name: 'StorageList',
        component: () => import('@/views/acquisition/StorageList.vue'),
        meta: { title: '资产入库', permission: 'acquisition:storage' }
      },
      // 档案管理模块
      {
        path: 'archive/attachment',
        name: 'AttachmentList',
        component: () => import('@/views/archive/AttachmentList.vue'),
        meta: { title: '附件管理', permission: 'archive:attachment' }
      },
      {
        path: 'archive/change',
        name: 'ChangeList',
        component: () => import('@/views/archive/ChangeList.vue'),
        meta: { title: '变更记录', permission: 'archive:change' }
      },
      {
        path: 'archive/maintenance',
        name: 'MaintenanceList',
        component: () => import('@/views/archive/MaintenanceList.vue'),
        meta: { title: '维修记录', permission: 'archive:maintenance' }
      },
      // 资产管理模块
      {
        path: 'management/requisition',
        name: 'RequisitionList',
        component: () => import('@/views/management/RequisitionList.vue'),
        meta: { title: '领用退库', permission: 'management:requisition' }
      },
      {
        path: 'management/transfer',
        name: 'TransferList',
        component: () => import('@/views/transfer/TransferList.vue'),
        meta: { title: '资产调拨', permission: 'management:transfer' }
      },
      {
        path: 'transfers/create',
        name: 'TransferCreate',
        component: () => import('@/views/transfer/TransferForm.vue'),
        meta: { title: '新增调拨', permission: 'management:transfer' }
      },
      {
        path: 'transfers/:id/edit',
        name: 'TransferEdit',
        component: () => import('@/views/transfer/TransferForm.vue'),
        meta: { title: '编辑调拨', permission: 'management:transfer' }
      },
      {
        path: 'transfers/:id/detail',
        name: 'TransferDetail',
        component: () => import('@/views/transfer/TransferDetail.vue'),
        meta: { title: '调拨详情', permission: 'management:transfer' }
      },
      {
        path: 'management/borrow',
        name: 'BorrowList',
        component: () => import('@/views/management/BorrowList.vue'),
        meta: { title: '借用归还', permission: 'management:borrow' }
      },
      // 资产处置模块
      {
        path: 'disposal/scrap',
        name: 'ScrapList',
        component: () => import('@/views/disposal/ScrapList.vue'),
        meta: { title: '报废申请', permission: 'disposal:scrap' }
      },
      {
        path: 'disposal/approval',
        name: 'DisposalApproval',
        component: () => import('@/views/disposal/ApprovalList.vue'),
        meta: { title: '处置审批', permission: 'disposal:approval' }
      },
      {
        path: 'disposal/sale',
        name: 'SaleList',
        component: () => import('@/views/disposal/SaleList.vue'),
        meta: { title: '出售捐赠', permission: 'disposal:sale' }
      },
      // 资产盘点模块
      {
        path: 'inventory/plan',
        name: 'InventoryPlan',
        component: () => import('@/views/inventory/PlanList.vue'),
        meta: { title: '盘点计划', permission: 'inventory:plan' }
      },
      {
        path: 'inventory/task',
        name: 'InventoryTask',
        component: () => import('@/views/inventory/TaskList.vue'),
        meta: { title: '盘点任务', permission: 'inventory:task' }
      },
      {
        path: 'inventory/result',
        name: 'InventoryResult',
        component: () => import('@/views/inventory/ResultList.vue'),
        meta: { title: '盘点结果', permission: 'inventory:result' }
      },
      // 资产门户模块
      {
        path: 'portal/my-assets',
        name: 'MyAssets',
        component: () => import('@/views/portal/MyAssets.vue'),
        meta: { title: '我的资产', permission: 'portal:my-assets' }
      },
      {
        path: 'portal/dept-assets',
        name: 'DeptAssets',
        component: () => import('@/views/portal/DeptAssets.vue'),
        meta: { title: '部门资产', permission: 'portal:dept-assets' }
      },
      // 资产报表模块
      {
        path: 'report/statistics',
        name: 'StatisticsReport',
        component: () => import('@/views/report/StatisticsReport.vue'),
        meta: { title: '统计报表', permission: 'report:statistics' }
      },
      {
        path: 'report/depreciation',
        name: 'DepreciationReport',
        component: () => import('@/views/report/DepreciationReport.vue'),
        meta: { title: '折旧报表', permission: 'report:depreciation' }
      },
      {
        path: 'report/disposal',
        name: 'DisposalReport',
        component: () => import('@/views/report/DisposalReport.vue'),
        meta: { title: '处置报表', permission: 'report:disposal' }
      },
      // 基础信息模块
      {
        path: 'system/users',
        name: 'UserList',
        component: () => import('@/views/basic/UserList.vue'),
        meta: { title: '用户管理', permission: 'system:user' }
      },
      {
        path: 'system/roles',
        name: 'RoleList',
        component: () => import('@/views/basic/RoleList.vue'),
        meta: { title: '角色管理', permission: 'system:role' }
      },
      {
        path: 'system/operation-logs',
        name: 'OperationLogList',
        component: () => import('@/views/basic/OperationLogList.vue'),
        meta: { title: '审计日志', permission: 'system:role' }
      },
      {
        path: 'basic/category',
        name: 'CategoryList',
        component: () => import('@/views/basic/CategoryList.vue'),
        meta: { title: '资产分类', permission: 'basic:category' }
      },
      {
        path: 'basic/location',
        name: 'LocationList',
        component: () => import('@/views/basic/LocationList.vue'),
        meta: { title: '存放位置', permission: 'basic:location' }
      },
      {
        path: 'basic/department',
        name: 'DepartmentList',
        component: () => import('@/views/basic/DepartmentList.vue'),
        meta: { title: '部门管理', permission: 'basic:department' }
      },
      {
        path: 'basic/supplier',
        name: 'SupplierList',
        component: () => import('@/views/basic/SupplierList.vue'),
        meta: { title: '供应商管理', permission: 'basic:supplier' }
      },
      {
        path: 'basic/asset-master',
        name: 'AssetMasterList',
        component: () => import('@/views/basic/AssetMasterList.vue'),
        meta: { title: '资产主数据', permission: 'basic:asset-master' }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  const permissions = getStoredPermissions()

  if (to.meta.requiresAuth && !token) {
    next('/login')
  } else if (to.path === '/login' && token) {
    next('/')
  } else if (to.meta.permission && token && !permissions.includes(to.meta.permission)) {
    next(from.path && from.path !== to.path ? from.path : '/')
  } else {
    next()
  }
})

export default router
