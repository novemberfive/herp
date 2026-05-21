import { createRouter, createWebHistory } from 'vue-router'

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
        meta: { title: '工作台' }
      },
      {
        path: 'assets',
        name: 'AssetList',
        component: () => import('@/views/asset/AssetList.vue'),
        meta: { title: '资产列表' }
      },
      {
        path: 'assets/create',
        name: 'AssetCreate',
        component: () => import('@/views/asset/AssetForm.vue'),
        meta: { title: '新增资产' }
      },
      {
        path: 'assets/:id/edit',
        name: 'AssetEdit',
        component: () => import('@/views/asset/AssetForm.vue'),
        meta: { title: '编辑资产' }
      },
      {
        path: 'assets/:id/detail',
        name: 'AssetDetail',
        component: () => import('@/views/asset/AssetDetail.vue'),
        meta: { title: '资产详情' }
      },
      // 资产取得模块
      {
        path: 'acquisition/purchase',
        name: 'PurchaseList',
        component: () => import('@/views/acquisition/PurchaseList.vue'),
        meta: { title: '采购申请' }
      },
      {
        path: 'acquisition/acceptance',
        name: 'AcceptanceList',
        component: () => import('@/views/acquisition/AcceptanceList.vue'),
        meta: { title: '验收登记' }
      },
      {
        path: 'acquisition/storage',
        name: 'StorageList',
        component: () => import('@/views/acquisition/StorageList.vue'),
        meta: { title: '资产入库' }
      },
      // 档案管理模块
      {
        path: 'archive/attachment',
        name: 'AttachmentList',
        component: () => import('@/views/archive/AttachmentList.vue'),
        meta: { title: '附件管理' }
      },
      {
        path: 'archive/change',
        name: 'ChangeList',
        component: () => import('@/views/archive/ChangeList.vue'),
        meta: { title: '变更记录' }
      },
      {
        path: 'archive/maintenance',
        name: 'MaintenanceList',
        component: () => import('@/views/archive/MaintenanceList.vue'),
        meta: { title: '维修记录' }
      },
      // 资产管理模块
      {
        path: 'management/requisition',
        name: 'RequisitionList',
        component: () => import('@/views/management/RequisitionList.vue'),
        meta: { title: '领用退库' }
      },
      {
        path: 'management/transfer',
        name: 'TransferList',
        component: () => import('@/views/management/TransferList.vue'),
        meta: { title: '资产调拨' }
      },
      {
        path: 'management/borrow',
        name: 'BorrowList',
        component: () => import('@/views/management/BorrowList.vue'),
        meta: { title: '借用归还' }
      },
      // 资产处置模块
      {
        path: 'disposal/scrap',
        name: 'ScrapList',
        component: () => import('@/views/disposal/ScrapList.vue'),
        meta: { title: '报废申请' }
      },
      {
        path: 'disposal/approval',
        name: 'DisposalApproval',
        component: () => import('@/views/disposal/ApprovalList.vue'),
        meta: { title: '处置审批' }
      },
      {
        path: 'disposal/sale',
        name: 'SaleList',
        component: () => import('@/views/disposal/SaleList.vue'),
        meta: { title: '出售捐赠' }
      },
      // 资产盘点模块
      {
        path: 'inventory/plan',
        name: 'InventoryPlan',
        component: () => import('@/views/inventory/PlanList.vue'),
        meta: { title: '盘点计划' }
      },
      {
        path: 'inventory/task',
        name: 'InventoryTask',
        component: () => import('@/views/inventory/TaskList.vue'),
        meta: { title: '盘点任务' }
      },
      {
        path: 'inventory/result',
        name: 'InventoryResult',
        component: () => import('@/views/inventory/ResultList.vue'),
        meta: { title: '盘点结果' }
      },
      // 资产门户模块
      {
        path: 'portal/my-assets',
        name: 'MyAssets',
        component: () => import('@/views/portal/MyAssets.vue'),
        meta: { title: '我的资产' }
      },
      {
        path: 'portal/dept-assets',
        name: 'DeptAssets',
        component: () => import('@/views/portal/DeptAssets.vue'),
        meta: { title: '部门资产' }
      },
      // 资产报表模块
      {
        path: 'report/statistics',
        name: 'StatisticsReport',
        component: () => import('@/views/report/StatisticsReport.vue'),
        meta: { title: '统计报表' }
      },
      {
        path: 'report/depreciation',
        name: 'DepreciationReport',
        component: () => import('@/views/report/DepreciationReport.vue'),
        meta: { title: '折旧报表' }
      },
      {
        path: 'report/disposal',
        name: 'DisposalReport',
        component: () => import('@/views/report/DisposalReport.vue'),
        meta: { title: '处置报表' }
      },
      // 基础信息模块
      {
        path: 'basic/category',
        name: 'CategoryList',
        component: () => import('@/views/basic/CategoryList.vue'),
        meta: { title: '资产分类' }
      },
      {
        path: 'basic/location',
        name: 'LocationList',
        component: () => import('@/views/basic/LocationList.vue'),
        meta: { title: '存放位置' }
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

  if (to.meta.requiresAuth && !token) {
    next('/login')
  } else if (to.path === '/login' && token) {
    next('/')
  } else {
    next()
  }
})

export default router
