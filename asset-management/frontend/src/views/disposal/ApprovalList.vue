<template>
  <div class="approval-list">
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
        <el-form-item label="处置类型">
          <el-select v-model="queryForm.disposalType" placeholder="请选择处置类型" clearable>
            <el-option label="报废" :value="1" />
            <el-option label="出售" :value="2" />
            <el-option label="捐赠" :value="3" />
            <el-option label="调出" :value="4" />
          </el-select>
        </el-form-item>
        <el-form-item label="审批状态">
          <el-select v-model="queryForm.approveStatus" placeholder="请选择审批状态" clearable>
            <el-option label="待审批" :value="0" />
            <el-option label="已通过" :value="1" />
            <el-option label="已拒绝" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item label="处置状态">
          <el-select v-model="queryForm.disposalStatus" placeholder="请选择处置状态" clearable>
            <el-option label="待处置" :value="0" />
            <el-option label="处置中" :value="1" />
            <el-option label="已完成" :value="2" />
            <el-option label="已取消" :value="3" />
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
      <el-table :data="tableData" v-loading="loading" border stripe>
        <el-table-column prop="disposalNo" label="处置单号" width="180" />
        <el-table-column prop="assetCode" label="资产编码" width="150" />
        <el-table-column prop="assetName" label="资产名称" width="150" />
        <el-table-column prop="categoryName" label="分类名称" width="120" />
        <el-table-column prop="disposalType" label="处置类型" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="getDisposalTypeTag(row.disposalType)">
              {{ getDisposalTypeText(row.disposalType) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="originalValue" label="原值" width="120" align="right">
          <template #default="{ row }">
            ¥{{ formatMoney(row.originalValue) }}
          </template>
        </el-table-column>
        <el-table-column prop="netValue" label="净值" width="120" align="right">
          <template #default="{ row }">
            ¥{{ formatMoney(row.netValue) }}
          </template>
        </el-table-column>
        <el-table-column prop="approveStatus" label="审批状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="getApproveStatusTag(row.approveStatus)">
              {{ getApproveStatusText(row.approveStatus) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="disposalStatus" label="处置状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="getDisposalStatusTag(row.disposalStatus)">
              {{ getDisposalStatusText(row.disposalStatus) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="applicantName" label="申请人" width="100" />
        <el-table-column prop="departmentName" label="申请部门" width="120" />
        <el-table-column prop="applyTime" label="申请时间" width="160">
          <template #default="{ row }">
            {{ formatDateTime(row.applyTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" fixed="right" width="280">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleView(row)">详情</el-button>
            <el-button link type="success" @click="handleApprove(row)" v-if="row.approveStatus === 0">审批</el-button>
            <el-button link type="warning" @click="handleExecute(row)" v-if="row.approveStatus === 1 && row.disposalStatus === 0">执行</el-button>
            <el-button link type="success" @click="handleComplete(row)" v-if="row.disposalStatus === 1">完成</el-button>
            <el-button link type="danger" @click="handleCancel(row)" v-if="row.disposalStatus !== 2 && row.disposalStatus !== 3">取消</el-button>
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
    <el-dialog v-model="approveDialogVisible" title="审批处置申请" width="500px">
      <el-form :model="approveForm" label-width="100px">
        <el-form-item label="处置单号">
          <span>{{ currentRow?.disposalNo }}</span>
        </el-form-item>
        <el-form-item label="资产名称">
          <span>{{ currentRow?.assetName }}</span>
        </el-form-item>
        <el-form-item label="处置类型">
          <span>{{ getDisposalTypeText(currentRow?.disposalType) }}</span>
        </el-form-item>
        <el-form-item label="审批意见" required>
          <el-radio-group v-model="approveForm.approveStatus">
            <el-radio :label="1">通过</el-radio>
            <el-radio :label="2">拒绝</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="备注" required>
          <el-input v-model="approveForm.approveRemark" type="textarea" :rows="4" placeholder="请输入审批意见" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="approveDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmApprove">确定</el-button>
      </template>
    </el-dialog>

    <!-- 执行处置对话框 -->
    <el-dialog v-model="executeDialogVisible" title="执行处置" width="600px">
      <el-form :model="executeForm" label-width="120px">
        <el-form-item label="处置单号">
          <span>{{ currentRow?.disposalNo }}</span>
        </el-form-item>
        <el-form-item label="资产名称">
          <span>{{ currentRow?.assetName }}</span>
        </el-form-item>
        <el-form-item label="处置方式" required>
          <el-select v-model="executeForm.disposalMethod" placeholder="请选择处置方式">
            <el-option label="公开拍卖" :value="1" />
            <el-option label="协议转让" :value="2" />
            <el-option label="废品回收" :value="3" />
            <el-option label="其他" :value="4" />
          </el-select>
        </el-form-item>
        <el-form-item label="实际成交金额" v-if="currentRow?.disposalType === 2">
          <el-input-number v-model="executeForm.actualValue" :min="0" :precision="2" :step="0.01" style="width: 200px" />
        </el-form-item>
        <el-form-item label="受让方名称" v-if="currentRow?.disposalType === 2">
          <el-input v-model="executeForm.buyerName" placeholder="请输入受让方名称" />
        </el-form-item>
        <el-form-item label="受让方联系人" v-if="currentRow?.disposalType === 2">
          <el-input v-model="executeForm.buyerContact" placeholder="请输入联系人" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="executeDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmExecute">确定</el-button>
      </template>
    </el-dialog>

    <!-- 详情对话框 -->
    <el-dialog v-model="detailDialogVisible" title="处置详情" width="800px">
      <el-descriptions :column="2" border v-if="currentRow">
        <el-descriptions-item label="处置单号">{{ currentRow.disposalNo }}</el-descriptions-item>
        <el-descriptions-item label="资产编码">{{ currentRow.assetCode }}</el-descriptions-item>
        <el-descriptions-item label="资产名称">{{ currentRow.assetName }}</el-descriptions-item>
        <el-descriptions-item label="规格型号">{{ currentRow.specification }}</el-descriptions-item>
        <el-descriptions-item label="分类名称">{{ currentRow.categoryName }}</el-descriptions-item>
        <el-descriptions-item label="处置类型">{{ getDisposalTypeText(currentRow.disposalType) }}</el-descriptions-item>
        <el-descriptions-item label="处置原因" :span="2">{{ currentRow.disposalReason }}</el-descriptions-item>
        <el-descriptions-item label="原值">¥{{ formatMoney(currentRow.originalValue) }}</el-descriptions-item>
        <el-descriptions-item label="净值">¥{{ formatMoney(currentRow.netValue) }}</el-descriptions-item>
        <el-descriptions-item label="申请人">{{ currentRow.applicantName }}</el-descriptions-item>
        <el-descriptions-item label="申请部门">{{ currentRow.departmentName }}</el-descriptions-item>
        <el-descriptions-item label="申请时间">{{ formatDateTime(currentRow.applyTime) }}</el-descriptions-item>
        <el-descriptions-item label="审批人">{{ currentRow.approverName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="审批时间">{{ formatDateTime(currentRow.approveTime) }}</el-descriptions-item>
        <el-descriptions-item label="审批状态">
          <el-tag :type="getApproveStatusTag(currentRow.approveStatus)">
            {{ getApproveStatusText(currentRow.approveStatus) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="审批意见" :span="2">{{ currentRow.approveRemark || '-' }}</el-descriptions-item>
        <el-descriptions-item label="处置状态">
          <el-tag :type="getDisposalStatusTag(currentRow.disposalStatus)">
            {{ getDisposalStatusText(currentRow.disposalStatus) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="备注" :span="2">{{ currentRow.remark || '-' }}</el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <el-button @click="detailDialogVisible = false">关闭</el-button>
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
const executeDialogVisible = ref(false)
const detailDialogVisible = ref(false)
const currentRow = ref(null)

const queryForm = reactive({
  disposalNo: '',
  assetCode: '',
  assetName: '',
  disposalType: null,
  approveStatus: null,
  disposalStatus: null
})

const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

const approveForm = reactive({
  approveStatus: 1,
  approveRemark: ''
})

const executeForm = reactive({
  disposalMethod: null,
  actualValue: null,
  buyerName: '',
  buyerContact: ''
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
    } else {
      ElMessage.error(res.message || '获取数据失败')
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
  queryForm.approveStatus = null
  queryForm.disposalStatus = null
  handleSearch()
}

const handleView = async (row) => {
  try {
    const res = await request.get(`/asset/disposal/${row.id}`)
    if (res.code === 200) {
      currentRow.value = res.data
      detailDialogVisible.value = true
    } else {
      ElMessage.error('获取详情失败')
    }
  } catch (error) {
    ElMessage.error('获取详情失败')
  }
}

const handleApprove = (row) => {
  currentRow.value = row
  approveForm.approveStatus = 1
  approveForm.approveRemark = ''
  approveDialogVisible.value = true
}

const confirmApprove = async () => {
  if (!approveForm.approveRemark.trim()) {
    ElMessage.warning('请输入审批意见')
    return
  }
  try {
    await request.post(`/asset/disposal/${currentRow.value.id}/approve`, null, {
      params: {
        approverId: 1, // 实际应从用户信息中获取
        approverName: '当前用户', // 实际应从用户信息中获取
        approveStatus: approveForm.approveStatus,
        approveRemark: approveForm.approveRemark
      }
    })
    ElMessage.success(approveForm.approveStatus === 1 ? '审批通过' : '审批拒绝')
    approveDialogVisible.value = false
    fetchData()
  } catch (error) {
    ElMessage.error('审批操作失败')
  }
}

const handleExecute = (row) => {
  currentRow.value = row
  executeForm.disposalMethod = null
  executeForm.actualValue = null
  executeForm.buyerName = ''
  executeForm.buyerContact = ''
  executeDialogVisible.value = true
}

const confirmExecute = async () => {
  if (!executeForm.disposalMethod) {
    ElMessage.warning('请选择处置方式')
    return
  }
  try {
    const params = {
      disposalMethod: executeForm.disposalMethod
    }
    if (currentRow.value.disposalType === 2) {
      if (executeForm.actualValue !== null) {
        params.actualValue = executeForm.actualValue
      }
      if (executeForm.buyerName) {
        params.buyerName = executeForm.buyerName
      }
      if (executeForm.buyerContact) {
        params.buyerContact = executeForm.buyerContact
      }
    }
    await request.post(`/asset/disposal/${currentRow.value.id}/execute`, null, { params })
    ElMessage.success('执行成功')
    executeDialogVisible.value = false
    fetchData()
  } catch (error) {
    ElMessage.error('执行操作失败')
  }
}

const handleComplete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要完成该处置吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await request.post(`/asset/disposal/${row.id}/complete`)
    ElMessage.success('完成成功')
    fetchData()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('完成操作失败')
    }
  }
}

const handleCancel = async (row) => {
  try {
    const { value } = await ElMessageBox.prompt('请输入取消原因', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      inputPattern: /.+/,
      inputErrorMessage: '原因不能为空'
    })
    await request.post(`/asset/disposal/${row.id}/cancel`, null, {
      params: { reason: value }
    })
    ElMessage.success('取消成功')
    fetchData()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('取消操作失败')
    }
  }
}

const getDisposalTypeText = (type) => {
  const texts = { 1: '报废', 2: '出售', 3: '捐赠', 4: '调出' }
  return texts[type] || '未知'
}

const getDisposalTypeTag = (type) => {
  const types = { 1: 'danger', 2: 'warning', 3: 'info', 4: '' }
  return types[type] || 'info'
}

const getApproveStatusText = (status) => {
  const texts = { 0: '待审批', 1: '已通过', 2: '已拒绝' }
  return texts[status] || '未知'
}

const getApproveStatusTag = (status) => {
  const types = { 0: 'warning', 1: 'success', 2: 'danger' }
  return types[status] || 'info'
}

const getDisposalStatusText = (status) => {
  const texts = { 0: '待处置', 1: '处置中', 2: '已完成', 3: '已取消' }
  return texts[status] || '未知'
}

const getDisposalStatusTag = (status) => {
  const types = { 0: 'info', 1: 'warning', 2: 'success', 3: 'info' }
  return types[status] || 'info'
}

const formatMoney = (value) => {
  if (value === null || value === undefined) return '0.00'
  return Number(value).toFixed(2)
}

const formatDateTime = (datetime) => {
  if (!datetime) return '-'
  return datetime.replace('T', ' ').substring(0, 19)
}

onMounted(() => {
  fetchData()
})
</script>

<style scoped>
.approval-list {
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

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>
