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
        <el-menu-item index="/dashboard">
          <el-icon><House /></el-icon>
          <span>工作台</span>
        </el-menu-item>
        
        <!-- 资产取得 -->
        <el-sub-menu index="acquisition">
          <template #title>
            <el-icon><DocumentAdd /></el-icon>
            <span>资产取得</span>
          </template>
          <el-menu-item index="/acquisition/purchase">采购申请</el-menu-item>
          <el-menu-item index="/acquisition/acceptance">验收登记</el-menu-item>
          <el-menu-item index="/acquisition/storage">资产入库</el-menu-item>
        </el-sub-menu>
        
        <!-- 档案管理 -->
        <el-sub-menu index="archive">
          <template #title>
            <el-icon><Folder /></el-icon>
            <span>档案管理</span>
          </template>
          <el-menu-item index="/archive/attachment">附件管理</el-menu-item>
          <el-menu-item index="/archive/change">变更记录</el-menu-item>
          <el-menu-item index="/archive/maintenance">维修记录</el-menu-item>
        </el-sub-menu>
        
        <!-- 资产管理 -->
        <el-sub-menu index="management">
          <template #title>
            <el-icon><Setting /></el-icon>
            <span>资产管理</span>
          </template>
          <el-menu-item index="/management/requisition">领用退库</el-menu-item>
          <el-menu-item index="/management/transfer">资产调拨</el-menu-item>
          <el-menu-item index="/management/borrow">借用归还</el-menu-item>
        </el-sub-menu>
        
        <!-- 资产处置 -->
        <el-sub-menu index="disposal">
          <template #title>
            <el-icon><Delete /></el-icon>
            <span>资产处置</span>
          </template>
          <el-menu-item index="/disposal/scrap">报废申请</el-menu-item>
          <el-menu-item index="/disposal/approval">处置审批</el-menu-item>
          <el-menu-item index="/disposal/sale">出售捐赠</el-menu-item>
        </el-sub-menu>
        
        <!-- 资产盘点 -->
        <el-sub-menu index="inventory">
          <template #title>
            <el-icon><List /></el-icon>
            <span>资产盘点</span>
          </template>
          <el-menu-item index="/inventory/plan">盘点计划</el-menu-item>
          <el-menu-item index="/inventory/task">盘点任务</el-menu-item>
          <el-menu-item index="/inventory/result">盘点结果</el-menu-item>
        </el-sub-menu>
        
        <!-- 资产门户 -->
        <el-sub-menu index="portal">
          <template #title>
            <el-icon><User /></el-icon>
            <span>资产门户</span>
          </template>
          <el-menu-item index="/portal/my-assets">我的资产</el-menu-item>
          <el-menu-item index="/portal/dept-assets">部门资产</el-menu-item>
        </el-sub-menu>
        
        <!-- 资产报表 -->
        <el-sub-menu index="report">
          <template #title>
            <el-icon><DataAnalysis /></el-icon>
            <span>资产报表</span>
          </template>
          <el-menu-item index="/report/statistics">统计报表</el-menu-item>
          <el-menu-item index="/report/depreciation">折旧报表</el-menu-item>
          <el-menu-item index="/report/disposal">处置报表</el-menu-item>
        </el-sub-menu>
        
        <!-- 基础信息 -->
        <el-sub-menu index="basic">
          <template #title>
            <el-icon><Setting /></el-icon>
            <span>基础信息</span>
          </template>
          <el-menu-item index="/basic/category">资产分类</el-menu-item>
          <el-menu-item index="/basic/location">存放位置</el-menu-item>
        </el-sub-menu>
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
import { useUserStore } from '@/store/user'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const activeMenu = computed(() => route.path)
const currentTitle = computed(() => route.meta.title || '')

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
