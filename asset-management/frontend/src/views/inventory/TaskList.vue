<template>
  <div class="task-list">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>盘点任务</span>
          <el-button type="primary" @click="handleCreate">
            <el-icon><Plus /></el-icon>
            新建任务
          </el-button>
        </div>
      </template>

      <!-- 搜索栏 -->
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="任务类型">
          <el-select v-model="searchForm.taskType" placeholder="全部类型" clearable>
            <el-option label="全盘" :value="1" />
            <el-option label="抽盘" :value="2" />
            <el-option label="循环盘点" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item label="任务状态">
          <el-select v-model="searchForm.status" placeholder="全部状态" clearable>
            <el-option label="未开始" :value="0" />
            <el-option label="进行中" :value="1" />
            <el-option label="已完成" :value="2" />
            <el-option label="已终止" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <!-- 表格 -->
      <el-table :data="tableData" v-loading="loading" border stripe>
        <el-table-column prop="taskNo" label="任务编号" width="150" />
        <el-table-column prop="taskName" label="任务名称" min-width="150" />
        <el-table-column prop="taskType" label="任务类型" width="100" align="center">
          <template #default="{ row }">
            {{ getTaskTypeText(row.taskType) }}
          </template>
        </el-table-column>
        <el-table-column prop="startDate" label="开始日期" width="120" />
        <el-table-column prop="endDate" label="结束日期" width="120" />
        <el-table-column prop="status" label="状态" width="80" align="center">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createUserId" label="创建人" width="100" />
        <el-table-column prop="createTime" label="创建时间" width="160" />
        <el-table-column label="操作" width="280" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleView(row)">详情</el-button>
            <el-button 
              v-if="row.status === 0" 
              link 
              type="success" 
              @click="handleStart(row)"
            >
              开始
            </el-button>
            <el-button 
              v-if="row.status === 1" 
              link 
              type="warning" 
              @click="handleComplete(row)"
            >
              完成
            </el-button>
            <el-button 
              v-if="row.status !== 2 && row.status !== 3" 
              link 
              type="danger" 
              @click="handleCancel(row)"
            >
              终止
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <el-pagination
        v-model:current-page="pagination.pageNum"
        v-model:page-size="pagination.pageSize"
        :total="pagination.total"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="fetchData"
        @current-change="fetchData"
        style="margin-top: 20px; justify-content: flex-end;"
      />
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getTaskList, startTask, completeTask, cancelTask } from '@/api/inventory'

const router = useRouter()
const loading = ref(false)
const tableData = ref([])

const searchForm = reactive({
  taskType: null,
  status: null
})

const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

const fetchData = async () => {
  loading.value = true
  try {
    const res = await getTaskList(
      pagination.pageNum, 
      pagination.pageSize, 
      searchForm.status,
      searchForm.taskType
    )
    tableData.value = res.data.list
    pagination.total = res.data.total
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  pagination.pageNum = 1
  fetchData()
}

const handleReset = () => {
  searchForm.taskType = null
  searchForm.status = null
  handleSearch()
}

const handleCreate = () => {
  // TODO: 跳转到创建页面或使用对话框
  ElMessage.info('创建任务功能待实现')
}

const handleView = (row) => {
  // TODO: 跳转到详情页面或使用对话框
  ElMessage.info('查看详情功能待实现')
}

const handleStart = async (row) => {
  try {
    await ElMessageBox.confirm(`确定要开始该盘点任务吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await startTask(row.id)
    ElMessage.success('任务已启动')
    fetchData()
  } catch (error) {
    if (error !== 'cancel') {
      console.error(error)
    }
  }
}

const handleComplete = async (row) => {
  try {
    await ElMessageBox.confirm(`确定要完成该盘点任务吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await completeTask(row.id)
    ElMessage.success('任务已完成')
    fetchData()
  } catch (error) {
    if (error !== 'cancel') {
      console.error(error)
    }
  }
}

const handleCancel = async (row) => {
  try {
    await ElMessageBox.confirm(`确定要终止该盘点任务吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await cancelTask(row.id)
    ElMessage.success('任务已终止')
    fetchData()
  } catch (error) {
    if (error !== 'cancel') {
      console.error(error)
    }
  }
}

const getTaskTypeText = (type) => {
  const texts = {
    1: '全盘',
    2: '抽盘',
    3: '循环盘点'
  }
  return texts[type] || '未知'
}

const getStatusType = (status) => {
  const types = {
    0: 'info',
    1: 'warning',
    2: 'success',
    3: 'danger'
  }
  return types[status] || 'info'
}

const getStatusText = (status) => {
  const texts = {
    0: '未开始',
    1: '进行中',
    2: '已完成',
    3: '已终止'
  }
  return texts[status] || '未知'
}

onMounted(() => {
  fetchData()
})
</script>

<style scoped>
.task-list {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.search-form {
  margin-bottom: 20px;
}
</style>
