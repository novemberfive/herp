<template>
  <div class="borrow-list">
    <!-- 搜索栏 -->
    <el-card class="search-card">
      <el-form :inline="true" :model="queryForm" class="search-form">
        <el-form-item label="借用单号">
          <el-input v-model="queryForm.borrowNo" placeholder="请输入借用单号" clearable />
        </el-form-item>
        <el-form-item label="资产编码">
          <el-input v-model="queryForm.assetCode" placeholder="请输入资产编码" clearable />
        </el-form-item>
        <el-form-item label="资产名称">
          <el-input v-model="queryForm.assetName" placeholder="请输入资产名称" clearable />
        </el-form-item>
        <el-form-item label="借用人">
          <el-input v-model="queryForm.borrowerName" placeholder="请输入借用人" clearable />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryForm.status" placeholder="请选择状态" clearable>
            <el-option label="待审批" :value="1" />
            <el-option label="审批通过" :value="2" />
            <el-option label="审批拒绝" :value="3" />
            <el-option label="已归还" :value="4" />
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
          新建借用申请
        </el-button>
      </div>
      
      <el-table :data="tableData" v-loading="loading" border stripe>
        <el-table-column prop="borrowNo" label="借用单号" width="180" />
        <el-table-column prop="assetCode" label="资产编码" width="150" />
        <el-table-column prop="assetName" label="资产名称" width="150" />
        <el-table-column prop="specification" label="规格型号" width="150" />
        <el-table-column prop="borrowerName" label="借用人" width="100" />
        <el-table-column prop="borrowerDept" label="借用部门" width="120" />
        <el-table-column label="预计归还日期" width="120">
          <template #default="{ row }">
            {{ row.expectedReturnDate }}
          </template>
        </el-table-column>
        <el-table-column label="实际归还日期" width="120">
          <template #default="{ row }">
            {{ row.actualReturnDate || '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" fixed="right" width="280">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleView(row)">详情</el-button>
            <el-button link type="success" @click="handleApprove(row)" v-if="row.status === 1">审批</el-button>
            <el-button link type="warning" @click="handleReturn(row)" v-if="row.status === 2">归还</el-button>
            <el-button link type="danger" @click="handleDelete(row)" v-if="row.status === 1 || row.status === 3">删除</el-button>
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

    <!-- 归还对话框 -->
    <el-dialog v-model="returnDialogVisible" title="资产归还" width="500px">
      <el-form :model="returnForm" label-width="100px">
        <el-form-item label="归还备注">
          <el-input v-model="returnForm.remark" type="textarea" :rows="3" placeholder="请输入归还备注" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="returnDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmReturn">确认归还</el-button>
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
const returnDialogVisible = ref(false)
const currentRow = ref(null)

const queryForm = reactive({
  borrowNo: '',
  assetCode: '',
  assetName: '',
  borrowerName: '',
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

const returnForm = reactive({
  remark: ''
})

const fetchData = async () => {
  loading.value = true
  try {
    const params = {
      pageNum: pagination.pageNum,
      pageSize: pagination.pageSize,
      ...queryForm
    }
    const res = await request.get('/management/borrow/list', { params })
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
  queryForm.borrowNo = ''
  queryForm.assetCode = ''
  queryForm.assetName = ''
  queryForm.borrowerName = ''
  queryForm.status = null
  handleSearch()
}

const handleCreate = () => {
  ElMessage.info('新建借用申请功能待实现')
}

const handleView = (row) => {
  ElMessage.info('查看详情：' + row.borrowNo)
}

const handleApprove = (row) => {
  currentRow.value = row
  approveForm.opinion = ''
  approveDialogVisible.value = true
}

const confirmApprove = async (approved) => {
  try {
    const url = approved 
      ? `/management/borrow/${currentRow.value.id}/approve?opinion=${encodeURIComponent(approveForm.opinion)}`
      : `/management/borrow/${currentRow.value.id}/reject?opinion=${encodeURIComponent(approveForm.opinion)}`
    await request.post(url)
    ElMessage.success(approved ? '审批通过' : '审批拒绝')
    approveDialogVisible.value = false
    fetchData()
  } catch (error) {
    ElMessage.error('审批操作失败')
  }
}

const handleReturn = (row) => {
  currentRow.value = row
  returnForm.remark = ''
  returnDialogVisible.value = true
}

const confirmReturn = async () => {
  try {
    await request.post(`/management/borrow/${currentRow.value.id}/return`, {
      remark: returnForm.remark
    })
    ElMessage.success('归还成功')
    returnDialogVisible.value = false
    fetchData()
  } catch (error) {
    ElMessage.error('归还操作失败')
  }
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除该借用记录吗？', '警告', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await request.delete(`/management/borrow/${row.id}`)
    ElMessage.success('删除成功')
    fetchData()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

const getStatusType = (status) => {
  const types = { 1: 'warning', 2: 'success', 3: 'danger', 4: '' }
  return types[status] || 'info'
}

const getStatusText = (status) => {
  const texts = { 1: '待审批', 2: '审批通过', 3: '审批拒绝', 4: '已归还' }
  return texts[status] || '未知'
}

onMounted(() => {
  fetchData()
})
</script>

<style scoped>
.borrow-list {
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
