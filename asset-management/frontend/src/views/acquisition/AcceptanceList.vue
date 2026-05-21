<template>
  <div class="acceptance-list">
    <!-- 搜索栏 -->
    <el-card class="search-card">
      <el-form :inline="true" :model="queryForm" class="search-form">
        <el-form-item label="验收单号">
          <el-input v-model="queryForm.acceptanceNo" placeholder="请输入验收单号" clearable />
        </el-form-item>
        <el-form-item label="采购单号">
          <el-input v-model="queryForm.purchaseRequestNo" placeholder="请输入采购申请单号" clearable />
        </el-form-item>
        <el-form-item label="资产名称">
          <el-input v-model="queryForm.assetName" placeholder="请输入资产名称" clearable />
        </el-form-item>
        <el-form-item label="验收结果">
          <el-select v-model="queryForm.acceptanceResult" placeholder="请选择状态" clearable>
            <el-option label="待验收" :value="0" />
            <el-option label="验收合格" :value="1" />
            <el-option label="验收不合格" :value="2" />
            <el-option label="部分合格" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item label="入库状态">
          <el-select v-model="queryForm.storageStatus" placeholder="请选择" clearable>
            <el-option label="未入库" :value="0" />
            <el-option label="已入库" :value="1" />
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
          新建验收
        </el-button>
      </div>
      
      <el-table :data="tableData" v-loading="loading" border stripe>
        <el-table-column prop="acceptanceNo" label="验收单号" width="180" />
        <el-table-column prop="purchaseRequestNo" label="采购单号" width="160" />
        <el-table-column prop="assetName" label="资产名称" width="150" />
        <el-table-column prop="specification" label="规格型号" width="140" />
        <el-table-column prop="quantity" label="数量" width="80" align="center" />
        <el-table-column prop="qualifiedQuantity" label="合格数" width="80" align="center" />
        <el-table-column prop="unqualifiedQuantity" label="不合格数" width="90" align="center" />
        <el-table-column prop="actualTotal" label="实际总价" width="120" align="right">
          <template #default="{ row }">
            ¥{{ row.actualTotal?.toFixed(2) }}
          </template>
        </el-table-column>
        <el-table-column prop="supplierName" label="供应商" width="150" />
        <el-table-column prop="acceptanceDate" label="验收日期" width="160">
          <template #default="{ row }">
            {{ formatDateTime(row.acceptanceDate) }}
          </template>
        </el-table-column>
        <el-table-column prop="acceptanceResult" label="验收结果" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="getResultType(row.acceptanceResult)">
              {{ getResultText(row.acceptanceResult) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="storageStatus" label="入库状态" width="90" align="center">
          <template #default="{ row }">
            <el-tag :type="row.storageStatus === 1 ? 'success' : 'info'">
              {{ row.storageStatus === 1 ? '已入库' : '未入库' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" fixed="right" width="320">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleView(row)">详情</el-button>
            <el-button link type="primary" @click="handleEdit(row)" v-if="row.acceptanceResult === 0">编辑</el-button>
            <el-button link type="success" @click="handleSubmit(row)" v-if="row.acceptanceResult === 0">提交</el-button>
            <el-button link type="success" @click="handleAccept(row)" v-if="row.acceptanceResult === 0">验收</el-button>
            <el-button link type="danger" @click="handleDelete(row)" v-if="row.acceptanceResult === 0">删除</el-button>
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

    <!-- 验收对话框 -->
    <el-dialog v-model="acceptDialogVisible" title="验收" width="600px">
      <el-form :model="acceptForm" label-width="100px">
        <el-form-item label="合格数量" required>
          <el-input-number v-model="acceptForm.qualifiedQuantity" :min="0" :max="currentRow?.quantity" />
        </el-form-item>
        <el-form-item label="不合格数量" required>
          <el-input-number v-model="acceptForm.unqualifiedQuantity" :min="0" :max="currentRow?.quantity" />
        </el-form-item>
        <el-form-item label="验收意见" required>
          <el-input v-model="acceptForm.opinion" type="textarea" :rows="4" placeholder="请输入验收意见" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="acceptDialogVisible = false">取消</el-button>
        <el-button type="success" @click="confirmAccept(true)">通过</el-button>
        <el-button type="danger" @click="confirmAccept(false)">拒绝</el-button>
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
const acceptDialogVisible = ref(false)
const currentRow = ref(null)

const queryForm = reactive({
  acceptanceNo: '',
  purchaseRequestNo: '',
  assetName: '',
  acceptanceResult: null,
  storageStatus: null
})

const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

const acceptForm = reactive({
  qualifiedQuantity: 0,
  unqualifiedQuantity: 0,
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
    const res = await request.get('/acquisition/acceptance/list', { params })
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
  queryForm.acceptanceNo = ''
  queryForm.purchaseRequestNo = ''
  queryForm.assetName = ''
  queryForm.acceptanceResult = null
  queryForm.storageStatus = null
  handleSearch()
}

const handleCreate = () => {
  ElMessage.info('新建验收功能待实现')
}

const handleView = (row) => {
  ElMessage.info('查看详情：' + row.acceptanceNo)
}

const handleEdit = (row) => {
  ElMessage.info('编辑：' + row.acceptanceNo)
}

const handleSubmit = async (row) => {
  try {
    await ElMessageBox.confirm('确定要提交该验收吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await request.post(`/acquisition/acceptance/${row.id}/submit`)
    ElMessage.success('提交成功')
    fetchData()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('提交失败')
    }
  }
}

const handleAccept = (row) => {
  currentRow.value = row
  acceptForm.qualifiedQuantity = row.quantity
  acceptForm.unqualifiedQuantity = 0
  acceptForm.opinion = ''
  acceptDialogVisible.value = true
}

const confirmAccept = async (approved) => {
  try {
    if (acceptForm.qualifiedQuantity + acceptForm.unqualifiedQuantity !== currentRow.value.quantity) {
      ElMessage.error('合格数量与不合格数量之和必须等于验收数量')
      return
    }
    
    const url = approved 
      ? `/acquisition/acceptance/${currentRow.value.id}/accept?opinion=${encodeURIComponent(acceptForm.opinion)}`
      : `/acquisition/acceptance/${currentRow.value.id}/reject?reason=${encodeURIComponent(acceptForm.opinion)}`
    
    // 如果是通过，需要先更新合格/不合格数量
    if (approved) {
      await request.put('/acquisition/acceptance', {
        id: currentRow.value.id,
        qualifiedQuantity: acceptForm.qualifiedQuantity,
        unqualifiedQuantity: acceptForm.unqualifiedQuantity
      })
    }
    
    await request.post(url)
    ElMessage.success(approved ? '验收通过' : '验收拒绝')
    acceptDialogVisible.value = false
    fetchData()
  } catch (error) {
    ElMessage.error('验收操作失败')
  }
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除该验收吗？', '警告', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await request.delete(`/acquisition/acceptance/${row.id}`)
    ElMessage.success('删除成功')
    fetchData()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

const getResultType = (result) => {
  const types = { 0: 'info', 1: 'success', 2: 'danger', 3: 'warning' }
  return types[result] || 'info'
}

const getResultText = (result) => {
  const texts = { 0: '待验收', 1: '验收合格', 2: '验收不合格', 3: '部分合格' }
  return texts[result] || '未知'
}

const formatDateTime = (datetime) => {
  if (!datetime) return ''
  return new Date(datetime).toLocaleString('zh-CN', { hour12: false })
}

onMounted(() => {
  fetchData()
})
</script>

<style scoped>
.acceptance-list {
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
