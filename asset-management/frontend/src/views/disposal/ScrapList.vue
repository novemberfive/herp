<template>
  <div class="scrap-list">
    <!-- 搜索栏 -->
    <el-card class="search-card">
      <el-form :inline="true" :model="queryForm" class="search-form">
        <el-form-item label="处置单号">
          <el-input v-model="queryForm.disposalNo" placeholder="请输入处置单号" clearable />
        </el-form-item>
        <el-form-item label="资产编码">
          <el-input v-model="queryForm.assetCode" placeholder="请输入资产编码" clearable />
        </el-form-item>
        <el-form-item label="资产名称">
          <el-input v-model="queryForm.assetName" placeholder="请输入资产名称" clearable />
        </el-form-item>
        <el-form-item label="处置方式">
          <el-select v-model="queryForm.disposalType" placeholder="请选择处置方式" clearable>
            <el-option label="报废" :value="1" />
            <el-option label="出售" :value="2" />
            <el-option label="捐赠" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryForm.status" placeholder="请选择状态" clearable>
            <el-option label="草稿" :value="0" />
            <el-option label="待审批" :value="1" />
            <el-option label="审批通过" :value="2" />
            <el-option label="审批拒绝" :value="3" />
            <el-option label="已完成" :value="4" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 列表区 -->
    <el-card class="table-card">
      <div class="toolbar">
        <el-button type="primary" @click="handleCreate">
          <el-icon><Plus /></el-icon>
          新建报废申请
        </el-button>
      </div>
      
      <el-table :data="tableData" v-loading="loading" border stripe>
        <el-table-column prop="disposalNo" label="处置单号" width="180" />
        <el-table-column prop="disposalType" label="处置方式" width="80" align="center">
          <template #default="{ row }">
            <el-tag :type="row.disposalType === 1 ? 'danger' : (row.disposalType === 2 ? 'warning' : 'info')">
              {{ getDisposalTypeText(row.disposalType) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="assetCode" label="资产编码" width="150" />
        <el-table-column prop="assetName" label="资产名称" width="150" />
        <el-table-column prop="specification" label="规格型号" width="150" />
        <el-table-column prop="originalValue" label="原值" width="120" align="right">
          <template #default="{ row }">
            ¥{{ row.originalValue?.toFixed(2) }}
          </template>
        </el-table-column>
        <el-table-column prop="residualValue" label="残值" width="120" align="right">
          <template #default="{ row }">
            ¥{{ row.residualValue?.toFixed(2) }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" fixed="right" width="320">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleView(row)">详情</el-button>
            <el-button link type="primary" @click="handleEdit(row)" v-if="row.status === 0">编辑</el-button>
            <el-button link type="success" @click="handleSubmit(row)" v-if="row.status === 0">提交</el-button>
            <el-button link type="success" @click="handleApprove(row)" v-if="row.status === 1">审批</el-button>
            <el-button link type="danger" @click="handleDelete(row)" v-if="row.status === 0 || row.status === 3">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <!-- 分页 -->
      <div class="pagination">
        <el-pagination
          v-model:current-page="pagination.pageNum"
          v-model:page-size="pagination.pageSize"
          :total="pagination.total"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="fetchData"
          @current-change="fetchData"
        />
      </div>
    </el-card>

    <!-- 审批对话框 -->
    <el-dialog v-model="approveDialogVisible" title="审批" width="500px">
      <el-form :model="approveForm" label-width="80px">
        <el-form-item label="审批意见" required>
          <el-input v-model="approveForm.opinion" type="textarea" :rows="4" placeholder="请输入审批意见" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="approveDialogVisible = false">取消</el-button>
        <el-button type="success" @click="confirmApprove(true)">通过</el-button>
        <el-button type="danger" @click="confirmApprove(false)">拒绝</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '@/utils/request'

const loading = ref(false)
const tableData = ref([])
const approveDialogVisible = ref(false)
const currentRow = ref(null)

const queryForm = reactive({
  disposalNo: '',
  assetCode: '',
  assetName: '',
  disposalType: null,
  status: null
})

const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

const approveForm = reactive({
  opinion: ''
})

const fetchData = async () => {
  loading.value = true
  try {
    const params = {
      pageNum: pagination.pageNum,
      pageSize: pagination.pageSize,
      ...queryForm
    }
    const res = await request.get('/asset/disposal/list', { params })
    if (res.code === 200) {
      tableData.value = res.data.records
      pagination.total = res.data.total
    }
  } catch (error) {
    ElMessage.error('获取数据失败')
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  pagination.pageNum = 1
  fetchData()
}

const handleReset = () => {
  queryForm.disposalNo = ''
  queryForm.assetCode = ''
  queryForm.assetName = ''
  queryForm.disposalType = null
  queryForm.status = null
  handleSearch()
}

const handleCreate = () => {
  ElMessage.info('新建报废申请功能待实现')
}

const handleView = (row) => {
  ElMessage.info('查看详情：' + row.disposalNo)
}

const handleEdit = (row) => {
  ElMessage.info('编辑：' + row.disposalNo)
}

const handleSubmit = async (row) => {
  try {
    await ElMessageBox.confirm('确定要提交该申请吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await request.post(`/asset/disposal/${row.id}/submit`)
    ElMessage.success('提交成功')
    fetchData()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('提交失败')
    }
  }
}

const handleApprove = (row) => {
  currentRow.value = row
  approveForm.opinion = ''
  approveDialogVisible.value = true
}

const confirmApprove = async (approved) => {
  try {
    const url = approved 
      ? `/asset/disposal/${currentRow.value.id}/approve?opinion=${encodeURIComponent(approveForm.opinion)}`
      : `/asset/disposal/${currentRow.value.id}/reject?opinion=${encodeURIComponent(approveForm.opinion)}`
    await request.post(url)
    ElMessage.success(approved ? '审批通过' : '审批拒绝')
    approveDialogVisible.value = false
    fetchData()
  } catch (error) {
    ElMessage.error('审批操作失败')
  }
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除该处置记录吗？', '警告', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await request.delete(`/asset/disposal/${row.id}`)
    ElMessage.success('删除成功')
    fetchData()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

const getDisposalTypeText = (type) => {
  const texts = { 1: '报废', 2: '出售', 3: '捐赠' }
  return texts[type] || '未知'
}

const getStatusType = (status) => {
  const types = { 0: 'info', 1: 'warning', 2: 'success', 3: 'danger', 4: '' }
  return types[status] || 'info'
}

const getStatusText = (status) => {
  const texts = { 0: '草稿', 1: '待审批', 2: '审批通过', 3: '审批拒绝', 4: '已完成' }
  return texts[status] || '未知'
}

onMounted(() => {
  fetchData()
})
</script>

<style scoped>
.scrap-list {
  padding: 20px;
}

.search-card {
  margin-bottom: 20px;
}

.search-form {
  display: flex;
  flex-wrap: wrap;
}

.table-card {
  min-height: 500px;
}

.toolbar {
  margin-bottom: 15px;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>
