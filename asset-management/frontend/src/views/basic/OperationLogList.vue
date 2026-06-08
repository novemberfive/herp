<template>
  <div class="operation-log-list">
    <el-card class="search-card">
      <el-form :inline="true" :model="queryForm" class="search-form">
        <el-form-item label="模块">
          <el-input v-model="queryForm.module" placeholder="请输入模块" clearable />
        </el-form-item>
        <el-form-item label="动作">
          <el-input v-model="queryForm.action" placeholder="请输入动作" clearable />
        </el-form-item>
        <el-form-item label="操作人">
          <el-input v-model="queryForm.operatorUsername" placeholder="请输入用户名" clearable />
        </el-form-item>
        <el-form-item label="请求路径">
          <el-input v-model="queryForm.requestUri" placeholder="请输入请求路径" clearable />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryForm.status" placeholder="全部状态" clearable style="width: 140px">
            <el-option label="成功" :value="1" />
            <el-option label="失败" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item label="操作日期">
          <el-date-picker
            v-model="dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format="YYYY-MM-DD"
            clearable
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card class="table-card">
      <div class="toolbar">
        <el-button @click="fetchData">刷新</el-button>
      </div>

      <el-table :data="tableData" v-loading="loading" border stripe>
        <el-table-column prop="createTime" label="操作时间" width="180" />
        <el-table-column prop="module" label="模块" min-width="140" show-overflow-tooltip />
        <el-table-column prop="action" label="动作" width="120" show-overflow-tooltip />
        <el-table-column label="状态" width="90" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'" size="small">
              {{ row.status === 1 ? '成功' : '失败' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作人" width="160">
          <template #default="{ row }">
            <div>{{ row.operatorName || '-' }}</div>
            <div class="sub-text">{{ row.operatorUsername || '-' }}</div>
          </template>
        </el-table-column>
        <el-table-column prop="operatorRole" label="角色" width="120" show-overflow-tooltip />
        <el-table-column prop="requestMethod" label="方法" width="90" align="center" />
        <el-table-column prop="requestUri" label="请求路径" min-width="220" show-overflow-tooltip />
        <el-table-column prop="bizType" label="业务类型" width="120" show-overflow-tooltip />
        <el-table-column prop="bizId" label="业务主键" width="140" show-overflow-tooltip />
        <el-table-column prop="ipAddress" label="IP 地址" width="140" show-overflow-tooltip />
        <el-table-column prop="description" label="描述" min-width="220" show-overflow-tooltip />
        <el-table-column prop="resultMessage" label="结果信息" min-width="220" show-overflow-tooltip />
      </el-table>

      <el-pagination
        v-model:current-page="pagination.pageNum"
        v-model:page-size="pagination.pageSize"
        :total="pagination.total"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next, jumper"
        style="margin-top: 20px; justify-content: flex-end;"
        @size-change="handleSizeChange"
        @current-change="fetchData"
      />
    </el-card>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { getOperationLogList } from '@/api/basic'

const loading = ref(false)
const tableData = ref([])
const dateRange = ref([])

const queryForm = reactive({
  module: '',
  action: '',
  operatorUsername: '',
  requestUri: '',
  status: null
})

const pagination = reactive({
  pageNum: 1,
  pageSize: 20,
  total: 0
})

const fetchData = async () => {
  loading.value = true
  try {
    const [startDate, endDate] = dateRange.value || []
    const res = await getOperationLogList({
      module: queryForm.module || undefined,
      action: queryForm.action || undefined,
      operatorUsername: queryForm.operatorUsername || undefined,
      requestUri: queryForm.requestUri || undefined,
      status: queryForm.status,
      startDate: startDate || undefined,
      endDate: endDate || undefined,
      pageNum: pagination.pageNum,
      pageSize: pagination.pageSize
    })
    tableData.value = res.data.list || []
    pagination.total = res.data.total || 0
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
  queryForm.module = ''
  queryForm.action = ''
  queryForm.operatorUsername = ''
  queryForm.requestUri = ''
  queryForm.status = null
  dateRange.value = []
  pagination.pageNum = 1
  fetchData()
}

const handleSizeChange = () => {
  pagination.pageNum = 1
  fetchData()
}

onMounted(() => {
  fetchData()
})
</script>

<style scoped>
.operation-log-list {
  padding: 20px;
}

.search-card {
  margin-bottom: 20px;
}

.toolbar {
  display: flex;
  justify-content: flex-end;
  margin-bottom: 16px;
}

.sub-text {
  color: #909399;
  font-size: 12px;
  line-height: 1.4;
}
</style>
