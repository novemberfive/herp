<template>
  <div class="borrow-list">
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
        <el-form-item label="借用状态">
          <el-select v-model="queryForm.borrowStatus" placeholder="请选择状态" clearable>
            <el-option label="待审批" :value="0" />
            <el-option label="借用中" :value="1" />
            <el-option label="已归还" :value="2" />
            <el-option label="已逾期" :value="3" />
            <el-option label="已取消" :value="4" />
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
          新建借用申请
        </el-button>
      </div>

      <el-table :data="tableData" v-loading="loading" border stripe>
        <el-table-column prop="borrowNo" label="借用单号" width="180" />
        <el-table-column prop="assetCode" label="资产编码" width="150" />
        <el-table-column prop="assetName" label="资产名称" width="150" />
        <el-table-column prop="specification" label="规格型号" width="150" />
        <el-table-column prop="borrowerName" label="借用人" width="100" />
        <el-table-column prop="borrowerDepartmentName" label="借用部门" width="120" />
        <el-table-column label="预计归还日期" width="120">
          <template #default="{ row }">{{ row.expectedReturnDate || '-' }}</template>
        </el-table-column>
        <el-table-column label="实际归还日期" width="120">
          <template #default="{ row }">{{ row.actualReturnDate || '-' }}</template>
        </el-table-column>
        <el-table-column prop="approveStatus" label="审批状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="getApproveStatusTag(row.approveStatus)">
              {{ getApproveStatusText(row.approveStatus) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="borrowStatus" label="借用状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="getBorrowStatusTag(row.borrowStatus)">
              {{ getBorrowStatusText(row.borrowStatus) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" fixed="right" width="320">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleView(row)">详情</el-button>
            <el-button link type="primary" @click="handleEdit(row)" v-if="row.approveStatus === 0">编辑</el-button>
            <el-button link type="success" @click="handleApprove(row)" v-if="row.approveStatus === 0">审批</el-button>
            <el-button link type="warning" @click="handleReturn(row)" v-if="row.borrowStatus === 1">归还</el-button>
            <el-button link type="danger" @click="handleDelete(row)" v-if="row.approveStatus === 0 || row.approveStatus === 2">删除</el-button>
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

    <el-dialog v-model="formDialogVisible" :title="form.id ? '编辑借用申请' : '新建借用申请'" width="820px">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="120px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="资产编码" prop="assetCode">
              <el-input v-model="form.assetCode" placeholder="请输入资产编码" clearable />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="资产名称" prop="assetName">
              <el-input v-model="form.assetName" placeholder="请输入资产名称" clearable />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="规格型号" prop="specification">
              <el-input v-model="form.specification" placeholder="请输入规格型号" clearable />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="分类名称" prop="categoryName">
              <el-input v-model="form.categoryName" placeholder="请输入分类名称" clearable />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="借用人" prop="borrowerName">
              <el-input v-model="form.borrowerName" placeholder="请输入借用人" clearable />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="借用部门" prop="borrowerDepartmentName">
              <el-input v-model="form.borrowerDepartmentName" placeholder="请输入借用部门" clearable />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="联系电话" prop="borrowerPhone">
              <el-input v-model="form.borrowerPhone" placeholder="请输入联系电话" clearable />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="借用日期" prop="borrowDate">
              <el-date-picker
                v-model="form.borrowDate"
                type="date"
                value-format="YYYY-MM-DD"
                placeholder="请选择借用日期"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="预计归还日期" prop="expectedReturnDate">
              <el-date-picker
                v-model="form.expectedReturnDate"
                type="date"
                value-format="YYYY-MM-DD"
                placeholder="请选择预计归还日期"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="借用用途" prop="borrowPurpose">
          <el-input v-model="form.borrowPurpose" type="textarea" :rows="3" placeholder="请输入借用用途" />
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

    <el-dialog v-model="returnDialogVisible" title="资产归还" width="500px">
      <el-form :model="returnForm" label-width="100px">
        <el-form-item label="归还状态">
          <el-input v-model="returnForm.returnCondition" placeholder="请输入归还状态，如良好、损坏" />
        </el-form-item>
        <el-form-item label="归还备注">
          <el-input v-model="returnForm.returnRemark" type="textarea" :rows="3" placeholder="请输入归还备注" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="returnDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmReturn">确认归还</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="detailDialogVisible" title="借用详情" width="820px">
      <el-descriptions :column="2" border v-if="detailData">
        <el-descriptions-item label="借用单号">{{ detailData.borrowNo }}</el-descriptions-item>
        <el-descriptions-item label="资产编码">{{ detailData.assetCode }}</el-descriptions-item>
        <el-descriptions-item label="资产名称">{{ detailData.assetName }}</el-descriptions-item>
        <el-descriptions-item label="规格型号">{{ detailData.specification || '-' }}</el-descriptions-item>
        <el-descriptions-item label="分类名称">{{ detailData.categoryName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="借用人">{{ detailData.borrowerName }}</el-descriptions-item>
        <el-descriptions-item label="借用部门">{{ detailData.borrowerDepartmentName }}</el-descriptions-item>
        <el-descriptions-item label="联系电话">{{ detailData.borrowerPhone || '-' }}</el-descriptions-item>
        <el-descriptions-item label="借用日期">{{ detailData.borrowDate || '-' }}</el-descriptions-item>
        <el-descriptions-item label="预计归还">{{ detailData.expectedReturnDate || '-' }}</el-descriptions-item>
        <el-descriptions-item label="实际归还">{{ detailData.actualReturnDate || '-' }}</el-descriptions-item>
        <el-descriptions-item label="审批状态">
          <el-tag :type="getApproveStatusTag(detailData.approveStatus)">
            {{ getApproveStatusText(detailData.approveStatus) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="借用状态">
          <el-tag :type="getBorrowStatusTag(detailData.borrowStatus)">
            {{ getBorrowStatusText(detailData.borrowStatus) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="审批人">{{ detailData.approverName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="审批时间">{{ formatDateTime(detailData.approveTime) }}</el-descriptions-item>
        <el-descriptions-item label="审批意见" :span="2">{{ detailData.approveRemark || '-' }}</el-descriptions-item>
        <el-descriptions-item label="借用用途" :span="2">{{ detailData.borrowPurpose || '-' }}</el-descriptions-item>
        <el-descriptions-item label="归还备注" :span="2">{{ detailData.returnRemark || '-' }}</el-descriptions-item>
        <el-descriptions-item label="备注" :span="2">{{ detailData.remark || '-' }}</el-descriptions-item>
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
import { Plus } from '@element-plus/icons-vue'
import request from '@/utils/request'

const loading = ref(false)
const tableData = ref([])
const formDialogVisible = ref(false)
const approveDialogVisible = ref(false)
const returnDialogVisible = ref(false)
const detailDialogVisible = ref(false)
const currentRow = ref(null)
const detailData = ref(null)
const formRef = ref(null)

const queryForm = reactive({
  borrowNo: '',
  assetCode: '',
  assetName: '',
  borrowerName: '',
  borrowStatus: null
})

const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

const form = reactive({
  id: null,
  assetCode: '',
  assetName: '',
  specification: '',
  categoryName: '',
  borrowerName: '',
  borrowerDepartmentName: '',
  borrowerPhone: '',
  borrowDate: '',
  expectedReturnDate: '',
  borrowPurpose: '',
  remark: ''
})

const approveForm = reactive({
  opinion: ''
})

const returnForm = reactive({
  returnCondition: '',
  returnRemark: ''
})

const rules = {
  assetCode: [{ required: true, message: '请输入资产编码', trigger: 'blur' }],
  assetName: [{ required: true, message: '请输入资产名称', trigger: 'blur' }],
  borrowerName: [{ required: true, message: '请输入借用人', trigger: 'blur' }],
  borrowerDepartmentName: [{ required: true, message: '请输入借用部门', trigger: 'blur' }],
  borrowDate: [{ required: true, message: '请选择借用日期', trigger: 'change' }],
  expectedReturnDate: [{ required: true, message: '请选择预计归还日期', trigger: 'change' }],
  borrowPurpose: [{ required: true, message: '请输入借用用途', trigger: 'blur' }]
}

const fetchData = async () => {
  loading.value = true
  try {
    const params = {
      pageNum: pagination.pageNum,
      pageSize: pagination.pageSize,
      borrowStatus: queryForm.borrowStatus,
      borrowNo: queryForm.borrowNo,
      assetCode: queryForm.assetCode,
      assetName: queryForm.assetName,
      borrowerName: queryForm.borrowerName
    }
    const res = await request.get('/asset/borrow/list', { params })
    if (res.code === 200) {
      tableData.value = res.data.list || []
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
  queryForm.borrowStatus = null
  handleSearch()
}

const resetForm = () => {
  Object.assign(form, {
    id: null,
    assetCode: '',
    assetName: '',
    specification: '',
    categoryName: '',
    borrowerName: '',
    borrowerDepartmentName: '',
    borrowerPhone: '',
    borrowDate: '',
    expectedReturnDate: '',
    borrowPurpose: '',
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
    const res = await request.get(`/asset/borrow/${row.id}`)
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
    const res = await request.get(`/asset/borrow/${row.id}`)
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
      await request.put('/asset/borrow', form)
      ElMessage.success('更新成功')
    } else {
      await request.post('/asset/borrow', form)
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
    await request.post(`/asset/borrow/${currentRow.value.id}/approve`, null, {
      params: {
        approverId: 1,
        approverName: '当前用户',
        approveStatus: approved ? 1 : 2,
        approveRemark: approveForm.opinion
      }
    })
    ElMessage.success(approved ? '审批通过' : '审批拒绝')
    approveDialogVisible.value = false
    fetchData()
  } catch (error) {
    ElMessage.error('审批操作失败')
  }
}

const handleReturn = (row) => {
  currentRow.value = row
  returnForm.returnCondition = ''
  returnForm.returnRemark = ''
  returnDialogVisible.value = true
}

const confirmReturn = async () => {
  try {
    await request.post(`/asset/borrow/${currentRow.value.id}/return`, null, {
      params: {
        returnCondition: returnForm.returnCondition,
        returnRemark: returnForm.returnRemark
      }
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
    await request.delete(`/asset/borrow/${row.id}`)
    ElMessage.success('删除成功')
    fetchData()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

const getApproveStatusText = (status) => {
  const texts = { 0: '待审批', 1: '已通过', 2: '已拒绝' }
  return texts[status] || '未知'
}

const getApproveStatusTag = (status) => {
  const types = { 0: 'warning', 1: 'success', 2: 'danger' }
  return types[status] || 'info'
}

const getBorrowStatusText = (status) => {
  const texts = { 0: '待审批', 1: '借用中', 2: '已归还', 3: '已逾期', 4: '已取消' }
  return texts[status] || '未知'
}

const getBorrowStatusTag = (status) => {
  const types = { 0: 'warning', 1: 'success', 2: '', 3: 'danger', 4: 'info' }
  return types[status] || 'info'
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
