<template>
  <el-container class="layout-container">
    <!-- 侧边栏 -->
    <el-aside width="220px">
      <div class="logo">
        <h3>资产管理系统</h3>
      </div>
      <el-menu
        :default-active="activeMenu"
        background-color="#304156"
        text-color="#bfcbd9"
        active-text-color="#409EFF"
        router
      >
        <template v-for="menu in visibleMenus" :key="menu.index">
          <el-menu-item v-if="!menu.children" :index="menu.index">
            <el-icon><component :is="menu.icon" /></el-icon>
            <span>{{ menu.title }}</span>
          </el-menu-item>
          <el-sub-menu v-else :index="menu.index">
            <template #title>
              <el-icon><component :is="menu.icon" /></el-icon>
              <span>{{ menu.title }}</span>
            </template>
            <el-menu-item
              v-for="child in menu.children"
              :key="child.index"
              :index="child.index"
            >
              {{ child.title }}
            </el-menu-item>
          </el-sub-menu>
        </template>
      </el-menu>
    </el-aside>

    <!-- 主内容区 -->
    <el-container>
      <!-- 顶部导航 -->
      <el-header class="header">
        <div class="header-left">
          <el-breadcrumb separator="/">
            <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
            <el-breadcrumb-item v-if="currentTitle">{{ currentTitle }}</el-breadcrumb-item>
          </el-breadcrumb>
        </div>
        <div class="header-right">
          <span class="username">{{ userStore.realName || userStore.username }}</span>
          <el-dropdown @command="handleCommand">
            <el-avatar :size="32" icon="User" style="cursor: pointer; margin-left: 10px;" />
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="logout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>

      <!-- 内容区 -->
      <el-main class="main-content">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessageBox } from 'element-plus'
import {
  DataAnalysis,
  Delete,
  DocumentAdd,
  Folder,
  House,
  List,
  Setting,
  User,
  UserFilled
} from '@element-plus/icons-vue'
import { useUserStore } from '@/store/user'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const activeMenu = computed(() => route.path)
const currentTitle = computed(() => route.meta.title || '')

const menuItems = [
  { index: '/dashboard', title: '工作台', icon: House, permission: 'dashboard:view' },
  {
    index: 'acquisition',
    title: '资产取得',
    icon: DocumentAdd,
    children: [
      { index: '/acquisition/purchase', title: '采购申请', permission: 'acquisition:purchase' },
      { index: '/acquisition/acceptance', title: '验收登记', permission: 'acquisition:acceptance' },
      { index: '/acquisition/storage', title: '资产入库', permission: 'acquisition:storage' }
    ]
  },
  {
    index: 'archive',
    title: '档案管理',
    icon: Folder,
    children: [
      { index: '/archive/attachment', title: '附件管理', permission: 'archive:attachment' },
      { index: '/archive/change', title: '变更记录', permission: 'archive:change' },
      { index: '/archive/maintenance', title: '维修记录', permission: 'archive:maintenance' }
    ]
  },
  {
    index: 'management',
    title: '资产管理',
    icon: Setting,
    children: [
      { index: '/management/requisition', title: '领用退库', permission: 'management:requisition' },
      { index: '/management/transfer', title: '资产调拨', permission: 'management:transfer' },
      { index: '/management/borrow', title: '借用归还', permission: 'management:borrow' }
    ]
  },
  {
    index: 'disposal',
    title: '资产处置',
    icon: Delete,
    children: [
      { index: '/disposal/scrap', title: '报废申请', permission: 'disposal:scrap' },
      { index: '/disposal/approval', title: '处置审批', permission: 'disposal:approval' },
      { index: '/disposal/sale', title: '出售捐赠', permission: 'disposal:sale' }
    ]
  },
  {
    index: 'inventory',
    title: '资产盘点',
    icon: List,
    children: [
      { index: '/inventory/plan', title: '盘点计划', permission: 'inventory:plan' },
      { index: '/inventory/task', title: '盘点任务', permission: 'inventory:task' },
      { index: '/inventory/result', title: '盘点结果', permission: 'inventory:result' }
    ]
  },
  {
    index: 'portal',
    title: '资产门户',
    icon: User,
    children: [
      { index: '/portal/my-assets', title: '我的资产', permission: 'portal:my-assets' },
      { index: '/portal/dept-assets', title: '部门资产', permission: 'portal:dept-assets' }
    ]
  },
  {
    index: 'report',
    title: '资产报表',
    icon: DataAnalysis,
    children: [
      { index: '/report/statistics', title: '统计报表', permission: 'report:statistics' },
      { index: '/report/depreciation', title: '折旧报表', permission: 'report:depreciation' },
      { index: '/report/disposal', title: '处置报表', permission: 'report:disposal' }
    ]
  },
  {
    index: 'basic',
    title: '基础信息',
    icon: Setting,
    children: [
      { index: '/basic/category', title: '资产分类', permission: 'basic:category' },
      { index: '/basic/location', title: '存放位置', permission: 'basic:location' },
      { index: '/basic/department', title: '部门管理', permission: 'basic:department' },
      { index: '/basic/supplier', title: '供应商管理', permission: 'basic:supplier' },
      { index: '/basic/asset-master', title: '资产主数据', permission: 'basic:asset-master' }
    ]
  },
  {
    index: 'system',
    title: '系统管理',
    icon: UserFilled,
    children: [
      { index: '/system/users', title: '用户管理', permission: 'system:user' },
      { index: '/system/roles', title: '角色管理', permission: 'system:role' },
      { index: '/system/operation-logs', title: '审计日志', permission: 'system:role' }
    ]
  }
]

const visibleMenus = computed(() => {
  return menuItems
    .map(menu => {
      if (!menu.children) {
        return userStore.hasPermission(menu.permission) ? menu : null
      }
      const children = menu.children.filter(child => userStore.hasPermission(child.permission))
      if (!children.length) {
        return null
      }
      return { ...menu, children }
    })
    .filter(Boolean)
})

const handleCommand = async (command) => {
  if (command === 'logout') {
    try {
      await ElMessageBox.confirm('确定要退出登录吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      })
      await userStore.logoutAction()
      router.push('/login')
    } catch (error) {
      // 用户取消
    }
  }
}
</script>

<style scoped>
.layout-container {
  height: 100vh;
}

.el-aside {
  background-color: #304156;
  color: #fff;
}

.logo {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #2b3a4b;
}

.logo h3 {
  color: #fff;
  font-size: 16px;
  margin: 0;
}

.el-menu {
  border-right: none;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background-color: #fff;
  border-bottom: 1px solid #e6e6e6;
  padding: 0 20px;
}

.header-left {
  flex: 1;
}

.header-right {
  display: flex;
  align-items: center;
}

.username {
  font-size: 14px;
  color: #606266;
}

.main-content {
  background-color: #f5f7fa;
  padding: 0;
}
</style>
