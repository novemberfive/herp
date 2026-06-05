<template>
  <div class="purchase-list">
    <!-- 搜索栏 -->
    <el-card class="search-card">
      <el-form :inline="true" :model="queryForm" class="search-form">
        <el-form-item label="申请单号">
          <el-input v-model="queryForm.requestNo" placeholder="请输入申请单号" clearable />
        </el-form-item>
        <el-form-item label="资产名称">
          <el-input v-model="queryForm.assetName" placeholder="请输入资产名称" clearable />
        </el-form-item>
        <el-form-item label="审批状态">
          <el-select v-model="queryForm.status" placeholder="请选择状态" clearable>
            <el-option label="草稿" :value="0" />
            <el-option label="待审批" :value="1" />
            <el-option label="审批通过" :value="2" />
            <el-option label="审批拒绝" :value="3" />
            <el-option label="已采购" :value="4" />
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
          新建申请
        </el-button>
      </div>
      
      <el-table :data="tableData" v-loading="loading" border stripe>
        <el-table-column prop="requestNo" label="申请单号" width="180" />
        <el-table-column prop="assetName" label="资产名称" width="150" />
        <el-table-column prop="specification" label="规格型号" width="150" />
        <el-table-column prop="categoryName" label="分类" width="120" />
        <el-table-column prop="quantity" label="数量" width="80" align="center" />
        <el-table-column prop="estimatedTotal" label="预估总价" width="120" align="right">
          <template #default="{ row }">
            ¥{{ row.estimatedTotal?.toFixed(2) }}
          </template>
        </el-table-column>
        <el-table-column prop="departmentName" label="申请部门" width="150" />
        <el-table-column prop="applicantName" label="申请人" width="100" />
        <el-table-column prop="applyDate" label="申请日期" width="160">
          <template #default="{ row }">
            {{ formatDateTime(row.applyDate) }}
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
            <el-button link type="primary" @click="handleEdit(row)" v-if="row.status === 0">编辑</el-button>
            <el-button link type="success" @click="handleSubmit(row)" v-if="row.status === 0">提交</el-button>
            <el-button link type="success" @click="handleApprove(row)" v-if="row.status === 1">审批</el-button>
            <el-button link type="danger" @click="handleDelete(row)" v-if="row.status === 0">删除</el-button>
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

    <!-- 新建/编辑申请对话框 -->
    <el-dialog v-model="formDialogVisible" :title="formData.id ? '编辑采购申请' : '新建采购申请'" width="700px">
      <el-form :model="formData" label-width="120px" ref="purchaseFormRef">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="资产名称" required>
              <el-input v-model="formData.assetName" placeholder="请输入资产名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="规格型号">
              <el-input v-model="formData.specification" placeholder="请输入规格型号" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="资产分类" required>
              <el-select v-model="formData.categoryId" placeholder="请选择资产分类" @change="handleCategoryChange" style="width: 100%">
                <el-option
                  v-for="item in categoryList"
                  :key="item.id"
                  :label="item.categoryName || item.name"
                  :value="item.id"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="申请数量" required>
              <el-input-number v-model="formData.quantity" :min="1" :max="9999" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="预估单价" required>
              <el-input-number v-model="formData.estimatedPrice" :min="0" :precision="2" :step="0.01" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="预估总价">
              <el-input :value="formData.estimatedPrice && formData.quantity ? (formData.estimatedPrice * formData.quantity).toFixed(2) : ''" disabled />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="申请部门" required>
              <el-select v-model="formData.departmentId" placeholder="请选择申请部门" @change="handleDepartmentChange" style="width: 100%">
                <el-option
                  v-for="item in departmentList"
                  :key="item.id"
                  :label="item.deptName || item.name"
                  :value="item.id"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="期望到货日期">
              <el-date-picker
                v-model="formData.expectedDeliveryDate"
                type="date"
                placeholder="选择日期"
                value-format="YYYY-MM-DD"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="采购原因" required>
          <el-input v-model="formData.purchaseReason" type="textarea" :rows="3" placeholder="请输入采购原因" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="formData.remark" type="textarea" :rows="3" placeholder="请输入备注信息" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="formDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmitForm">确定</el-button>
      </template>
    </el-dialog>

    <!-- 详情对话框 -->
    <el-dialog v-model="viewDialogVisible" title="采购申请详情" width="800px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="申请单号">{{ viewData.requestNo }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="getStatusType(viewData.status)">{{ getStatusText(viewData.status) }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="资产名称">{{ viewData.assetName }}</el-descriptions-item>
        <el-descriptions-item label="规格型号">{{ viewData.specification }}</el-descriptions-item>
        <el-descriptions-item label="资产分类">{{ viewData.categoryName }}</el-descriptions-item>
        <el-descriptions-item label="申请数量">{{ viewData.quantity }}</el-descriptions-item>
        <el-descriptions-item label="预估单价">¥{{ viewData.estimatedPrice?.toFixed(2) }}</el-descriptions-item>
        <el-descriptions-item label="预估总价">¥{{ viewData.estimatedTotal?.toFixed(2) }}</el-descriptions-item>
        <el-descriptions-item label="申请部门">{{ viewData.departmentName }}</el-descriptions-item>
        <el-descriptions-item label="申请人">{{ viewData.applicantName }}</el-descriptions-item>
        <el-descriptions-item label="申请日期">{{ formatDateTime(viewData.applyDate) }}</el-descriptions-item>
        <el-descriptions-item label="期望到货日期">{{ formatDate(viewData.expectedDeliveryDate) }}</el-descriptions-item>
        <el-descriptions-item label="采购原因" :span="2">{{ viewData.purchaseReason }}</el-descriptions-item>
        <el-descriptions-item label="备注" :span="2">{{ viewData.remark }}</el-descriptions-item>
        <el-descriptions-item v-if="viewData.status >= 2" label="审批人">{{ viewData.approverName }}</el-descriptions-item>
        <el-descriptions-item v-if="viewData.status >= 2" label="审批时间">{{ formatDateTime(viewData.approveTime) }}</el-descriptions-item>
        <el-descriptions-item v-if="viewData.status >= 2" label="审批意见" :span="2">{{ viewData.approveOpinion }}</el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <el-button @click="viewDialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { getPurchaseList, createPurchase, updatePurchase, deletePurchase, submitPurchase, approvePurchase, rejectPurchase, getCategoryList, getDepartmentList, getPurchaseById } from '@/api/purchase'

const loading = ref(false)
const tableData = ref([])
const approveDialogVisible = ref(false)
const formDialogVisible = ref(false)
const viewDialogVisible = ref(false)
const currentRow = ref(null)
const categoryList = ref([])
const departmentList = ref([])

const queryForm = reactive({
  requestNo: '',
  assetName: '',
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

const formData = reactive({
  id: null,
  assetName: '',
  specification: '',
  categoryId: null,
  categoryName: '',
  quantity: 1,
  estimatedPrice: null,
  departmentId: null,
  departmentName: '',
  expectedDeliveryDate: '',
  purchaseReason: '',
  remark: ''
})

const viewData = ref({})

const fetchData = async () => {
  loading.value = true
  try {
    const params = {
      pageNum: pagination.pageNum,
      pageSize: pagination.pageSize,
      ...queryForm
    }
    const res = await getPurchaseList(params.pageNum, params.pageSize, params)
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

const loadCategoryAndDepartment = async () => {
  try {
    const [categoryRes, deptRes] = await Promise.all([
      getCategoryList(),
      getDepartmentList()
    ])
    if (categoryRes.code === 200) {
      categoryList.value = Array.isArray(categoryRes.data) ? categoryRes.data : (categoryRes.data.records || [])
    }
    if (deptRes.code === 200) {
      departmentList.value = Array.isArray(deptRes.data) ? deptRes.data : (deptRes.data.records || [])
    }
  } catch (error) {
    console.error('加载分类和部门失败', error)
  }
}

const handleSearch = () => {
  pagination.pageNum = 1
  fetchData()
}

const handleReset = () => {
  queryForm.requestNo = ''
  queryForm.assetName = ''
  queryForm.status = null
  handleSearch()
}

const handleCreate = () => {
  resetForm()
  formDialogVisible.value = true
}

const resetForm = () => {
  formData.id = null
  formData.assetName = ''
  formData.specification = ''
  formData.categoryId = null
  formData.categoryName = ''
  formData.quantity = 1
  formData.estimatedPrice = null
  formData.departmentId = null
  formData.departmentName = ''
  formData.expectedDeliveryDate = ''
  formData.purchaseReason = ''
  formData.remark = ''
}

const handleCategoryChange = (value) => {
  const category = categoryList.value.find(item => item.id === value)
  if (category) {
    formData.categoryName = category.categoryName || category.name
  }
}

const handleDepartmentChange = (value) => {
  const dept = departmentList.value.find(item => item.id === value)
  if (dept) {
    formData.departmentName = dept.deptName || dept.name
  }
}

const handleSubmitForm = async () => {
  try {
    const data = {
      assetName: formData.assetName,
      specification: formData.specification,
      categoryId: formData.categoryId,
      categoryName: formData.categoryName,
      quantity: formData.quantity,
      estimatedPrice: formData.estimatedPrice,
      estimatedTotal: formData.estimatedPrice ? formData.estimatedPrice * formData.quantity : null,
      departmentId: formData.departmentId,
      departmentName: formData.departmentName,
      expectedDeliveryDate: formData.expectedDeliveryDate || null,
      purchaseReason: formData.purchaseReason,
      remark: formData.remark
    }
    
    if (formData.id) {
      data.id = formData.id
      await updatePurchase(data)
      ElMessage.success('更新成功')
    } else {
      await createPurchase(data)
      ElMessage.success('创建成功')
    }
    formDialogVisible.value = false
    fetchData()
  } catch (error) {
    ElMessage.error(error.response?.data?.msg || (formData.id ? '更新失败' : '创建失败'))
  }
}

const handleView = async (row) => {
  try {
    const res = await getPurchaseById(row.id)
    if (res.code === 200) {
      viewData.value = res.data
      viewDialogVisible.value = true
    }
  } catch (error) {
    ElMessage.error('获取详情失败')
  }
}

const handleEdit = async (row) => {
  try {
    const res = await getPurchaseById(row.id)
    if (res.code === 200) {
      const data = res.data
      formData.id = data.id
      formData.assetName = data.assetName
      formData.specification = data.specification
      formData.categoryId = data.categoryId
      formData.categoryName = data.categoryName
      formData.quantity = data.quantity
      formData.estimatedPrice = data.estimatedPrice
      formData.departmentId = data.departmentId
      formData.departmentName = data.departmentName
      formData.expectedDeliveryDate = data.expectedDeliveryDate
      formData.purchaseReason = data.purchaseReason
      formData.remark = data.remark
      formDialogVisible.value = true
    }
  } catch (error) {
    ElMessage.error('获取详情失败')
  }
}

const handleSubmit = async (row) => {
  try {
    await ElMessageBox.confirm('确定要提交该申请吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await submitPurchase(row.id)
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
    if (approved) {
      await approvePurchase(currentRow.value.id, approveForm.opinion)
      ElMessage.success('审批通过')
    } else {
      await rejectPurchase(currentRow.value.id, approveForm.opinion)
      ElMessage.success('审批拒绝')
    }
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
    await deletePurchase(row.id)
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
  const texts = { 0: '草稿', 1: '待审批', 2: '审批通过', 3: '审批拒绝', 4: '已采购' }
  return texts[status] || '未知'
}

const formatDateTime = (datetime) => {
  if (!datetime) return ''
  return new Date(datetime).toLocaleString('zh-CN', { hour12: false })
}

const formatDate = (date) => {
  if (!date) return ''
  return new Date(date).toISOString().split('T')[0]
}

onMounted(() => {
  fetchData()
  loadCategoryAndDepartment()
})
</script>

<style scoped>
.purchase-list {
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
