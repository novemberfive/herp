<template>
  <div class="requisition-list">
    <el-card class="search-card">
      <el-form :inline="true" :model="queryForm" class="search-form">
        <el-form-item label="领用单号">
          <el-input v-model="queryForm.requisitionNo" placeholder="请输入领用单号" clearable />
        </el-form-item>
        <el-form-item label="资产编码">
          <el-input v-model="queryForm.assetCode" placeholder="请输入资产编码" clearable />
        </el-form-item>
        <el-form-item label="资产名称">
          <el-input v-model="queryForm.assetName" placeholder="请输入资产名称" clearable />
        </el-form-item>
        <el-form-item label="业务类型">
          <el-select v-model="queryForm.businessType" placeholder="请选择类型" clearable>
            <el-option label="领用" :value="1" />
            <el-option label="退库" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item label="审批状态">
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

    <el-card class="table-card">
      <div class="toolbar">
        <el-button type="primary" @click="handleCreate">
          <el-icon><Plus /></el-icon>
          新建领用/退库
        </el-button>
      </div>

      <el-table :data="tableData" v-loading="loading" border stripe>
        <el-table-column prop="requisitionNo" label="领用单号" width="180" />
        <el-table-column prop="businessType" label="类型" width="80" align="center">
          <template #default="{ row }">
            <el-tag :type="row.businessType === 1 ? 'success' : 'warning'">
              {{ row.businessType === 1 ? '领用' : '退库' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="assetCode" label="资产编码" width="150" />
        <el-table-column prop="assetName" label="资产名称" width="150" />
        <el-table-column prop="specification" label="规格型号" width="150" />
        <el-table-column prop="quantity" label="数量" width="80" align="center" />
        <el-table-column prop="totalAmount" label="总金额" width="120" align="right">
          <template #default="{ row }">¥{{ formatMoney(row.totalAmount) }}</template>
        </el-table-column>
        <el-table-column label="原部门/保管人" width="200">
          <template #default="{ row }">
            {{ row.originalDepartmentName || '-' }} / {{ row.originalKeeperName || '-' }}
          </template>
        </el-table-column>
        <el-table-column label="新部门/保管人" width="200">
          <template #default="{ row }">
            {{ row.newDepartmentName || '-' }} / {{ row.newKeeperName || '-' }}
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
            <el-button link type="danger" @click="handleDelete(row)" v-if="row.status === 0">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

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

    <el-dialog v-model="formDialogVisible" :title="form.id ? '编辑领用/退库' : '新建领用/退库'" width="860px">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="120px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="业务类型" prop="businessType">
              <el-select v-model="form.businessType" placeholder="请选择业务类型" style="width: 100%">
                <el-option label="领用" :value="1" />
                <el-option label="退库" :value="2" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="资产编码" prop="assetCode">
              <el-input v-model="form.assetCode" placeholder="请输入资产编码" clearable />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="资产名称" prop="assetName">
              <el-input v-model="form.assetName" placeholder="请输入资产名称" clearable />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="规格型号" prop="specification">
              <el-input v-model="form.specification" placeholder="请输入规格型号" clearable />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="8">
            <el-form-item label="数量" prop="quantity">
              <el-input-number v-model="form.quantity" :min="1" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="单价" prop="unitPrice">
              <el-input-number v-model="form.unitPrice" :min="0" :precision="2" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="总金额">
              <el-input-number v-model="form.totalAmount" :min="0" :precision="2" disabled style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="原部门" prop="originalDepartmentName">
              <el-input v-model="form.originalDepartmentName" placeholder="请输入原部门" clearable />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="原保管人" prop="originalKeeperName">
              <el-input v-model="form.originalKeeperName" placeholder="请输入原保管人" clearable />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="新部门" prop="newDepartmentName">
              <el-input v-model="form.newDepartmentName" placeholder="请输入新部门" clearable />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="新保管人" prop="newKeeperName">
              <el-input v-model="form.newKeeperName" placeholder="请输入新保管人" clearable />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="预计归还日期" prop="expectedReturnDate">
              <el-date-picker
                v-model="form.expectedReturnDate"
                type="datetime"
                value-format="YYYY-MM-DDTHH:mm:ss"
                placeholder="请选择预计归还日期"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="分类名称" prop="categoryName">
              <el-input v-model="form.categoryName" placeholder="请输入分类名称" clearable />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="原因" prop="reason">
          <el-input v-model="form.reason" type="textarea" :rows="3" placeholder="请输入领用/退库原因" />
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" :rows="3" placeholder="请输入备注" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="formDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitForm">确定</el-button>
      </template>
    </el-dialog>

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

    <el-dialog v-model="detailDialogVisible" title="领用/退库详情" width="820px">
      <el-descriptions :column="2" border v-if="detailData">
        <el-descriptions-item label="领用单号">{{ detailData.requisitionNo }}</el-descriptions-item>
        <el-descriptions-item label="业务类型">{{ detailData.businessType === 1 ? '领用' : '退库' }}</el-descriptions-item>
        <el-descriptions-item label="资产编码">{{ detailData.assetCode }}</el-descriptions-item>
        <el-descriptions-item label="资产名称">{{ detailData.assetName }}</el-descriptions-item>
        <el-descriptions-item label="规格型号">{{ detailData.specification || '-' }}</el-descriptions-item>
        <el-descriptions-item label="分类名称">{{ detailData.categoryName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="数量">{{ detailData.quantity }}</el-descriptions-item>
        <el-descriptions-item label="单价">¥{{ formatMoney(detailData.unitPrice) }}</el-descriptions-item>
        <el-descriptions-item label="总金额">¥{{ formatMoney(detailData.totalAmount) }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="getStatusType(detailData.status)">{{ getStatusText(detailData.status) }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="原部门">{{ detailData.originalDepartmentName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="原保管人">{{ detailData.originalKeeperName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="新部门">{{ detailData.newDepartmentName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="新保管人">{{ detailData.newKeeperName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="申请时间">{{ formatDateTime(detailData.requisitionDate) }}</el-descriptions-item>
        <el-descriptions-item label="预计归还">{{ formatDateTime(detailData.expectedReturnDate) }}</el-descriptions-item>
        <el-descriptions-item label="审批人">{{ detailData.approverName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="审批时间">{{ formatDateTime(detailData.approveTime) }}</el-descriptions-item>
        <el-descriptions-item label="审批意见" :span="2">{{ detailData.approveOpinion || '-' }}</el-descriptions-item>
        <el-descriptions-item label="原因" :span="2">{{ detailData.reason || '-' }}</el-descriptions-item>
        <el-descriptions-item label="备注" :span="2">{{ detailData.remark || '-' }}</el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <el-button @click="detailDialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import request from '@/utils/request'

const loading = ref(false)
const tableData = ref([])
const formDialogVisible = ref(false)
const approveDialogVisible = ref(false)
const detailDialogVisible = ref(false)
const currentRow = ref(null)
const detailData = ref(null)
const formRef = ref(null)

const queryForm = reactive({
  requisitionNo: '',
  assetCode: '',
  assetName: '',
  businessType: null,
  status: null
})

const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

const form = reactive({
  id: null,
  businessType: 1,
  assetCode: '',
  assetName: '',
  specification: '',
  categoryName: '',
  quantity: 1,
  unitPrice: 0,
  totalAmount: 0,
  originalDepartmentName: '',
  originalKeeperName: '',
  newDepartmentName: '',
  newKeeperName: '',
  expectedReturnDate: '',
  reason: '',
  remark: ''
})

const approveForm = reactive({
  opinion: ''
})

const rules = {
  businessType: [{ required: true, message: '请选择业务类型', trigger: 'change' }],
  assetCode: [{ required: true, message: '请输入资产编码', trigger: 'blur' }],
  assetName: [{ required: true, message: '请输入资产名称', trigger: 'blur' }],
  quantity: [{ required: true, message: '请输入数量', trigger: 'blur' }],
  originalDepartmentName: [{ required: true, message: '请输入原部门', trigger: 'blur' }],
  originalKeeperName: [{ required: true, message: '请输入原保管人', trigger: 'blur' }],
  reason: [{ required: true, message: '请输入原因', trigger: 'blur' }],
  newDepartmentName: [
    {
      validator: (_rule, value, callback) => {
        if (form.businessType === 1 && !value) {
          callback(new Error('请输入新部门'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ],
  newKeeperName: [
    {
      validator: (_rule, value, callback) => {
        if (form.businessType === 1 && !value) {
          callback(new Error('请输入新保管人'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ]
}

watch([() => form.quantity, () => form.unitPrice], () => {
  form.totalAmount = Number((Number(form.quantity || 0) * Number(form.unitPrice || 0)).toFixed(2))
})

const fetchData = async () => {
  loading.value = true
  try {
    const params = {
      pageNum: pagination.pageNum,
      pageSize: pagination.pageSize,
      ...queryForm
    }
    const res = await request.get('/management/requisition/list', { params })
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
  queryForm.requisitionNo = ''
  queryForm.assetCode = ''
  queryForm.assetName = ''
  queryForm.businessType = null
  queryForm.status = null
  handleSearch()
}

const resetForm = () => {
  Object.assign(form, {
    id: null,
    businessType: 1,
    assetCode: '',
    assetName: '',
    specification: '',
    categoryName: '',
    quantity: 1,
    unitPrice: 0,
    totalAmount: 0,
    originalDepartmentName: '',
    originalKeeperName: '',
    newDepartmentName: '',
    newKeeperName: '',
    expectedReturnDate: '',
    reason: '',
    remark: ''
  })
  formRef.value?.clearValidate()
}

const handleCreate = () => {
  resetForm()
  formDialogVisible.value = true
}

const handleView = async (row) => {
  try {
    const res = await request.get(`/management/requisition/${row.id}`)
    if (res.code === 200) {
      detailData.value = res.data
      detailDialogVisible.value = true
    } else {
      ElMessage.error(res.message || '获取详情失败')
    }
  } catch (error) {
    ElMessage.error('获取详情失败')
  }
}

const handleEdit = async (row) => {
  try {
    const res = await request.get(`/management/requisition/${row.id}`)
    if (res.code === 200) {
      resetForm()
      Object.assign(form, res.data)
      formDialogVisible.value = true
    } else {
      ElMessage.error(res.message || '获取详情失败')
    }
  } catch (error) {
    ElMessage.error('获取详情失败')
  }
}

const submitForm = async () => {
  if (!formRef.value) return
  try {
    await formRef.value.validate()
    if (form.id) {
      await request.put('/management/requisition', form)
      ElMessage.success('更新成功')
    } else {
      await request.post('/management/requisition', form)
      ElMessage.success('创建成功')
    }
    formDialogVisible.value = false
    fetchData()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(error.response?.data?.message || '保存失败')
    }
  }
}

const handleSubmit = async (row) => {
  try {
    await ElMessageBox.confirm('确定要提交该申请吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await request.post(`/management/requisition/${row.id}/submit`)
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
    if (!approveForm.opinion.trim()) {
      ElMessage.warning('请输入审批意见')
      return
    }
    const url = approved
      ? `/management/requisition/${currentRow.value.id}/approve?opinion=${encodeURIComponent(approveForm.opinion)}`
      : `/management/requisition/${currentRow.value.id}/reject?opinion=${encodeURIComponent(approveForm.opinion)}`
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
    await ElMessageBox.confirm('确定要删除该申请吗？', '警告', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await request.delete(`/management/requisition/${row.id}`)
    ElMessage.success('删除成功')
    fetchData()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

const getStatusType = (status) => {
  const types = { 0: 'info', 1: 'warning', 2: 'success', 3: 'danger', 4: '' }
  return types[status] || 'info'
}

const getStatusText = (status) => {
  const texts = { 0: '草稿', 1: '待审批', 2: '审批通过', 3: '审批拒绝', 4: '已完成' }
  return texts[status] || '未知'
}

const formatMoney = (value) => {
  if (value === null || value === undefined) return '0.00'
  return Number(value).toFixed(2)
}

const formatDateTime = (datetime) => {
  if (!datetime) return '-'
  return String(datetime).replace('T', ' ').substring(0, 19)
}

onMounted(() => {
  fetchData()
})
</script>

<style scoped>
.requisition-list {
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
