<template>
  <div class="acceptance-list">
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

    <el-card class="table-card">
      <div class="toolbar">
        <el-button v-if="userStore.hasPermission('acquisition:acceptance:create')" type="primary" @click="handleCreate">
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
          <template #default="{ row }">¥{{ formatMoney(row.actualTotal) }}</template>
        </el-table-column>
        <el-table-column prop="supplierName" label="供应商" width="150" />
        <el-table-column prop="acceptanceDate" label="验收日期" width="160">
          <template #default="{ row }">{{ formatDateTime(row.acceptanceDate) }}</template>
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
            <el-button v-if="row.acceptanceResult === 0 && userStore.hasPermission('acquisition:acceptance:edit')" link type="primary" @click="handleEdit(row)">编辑</el-button>
            <el-button v-if="row.acceptanceResult === 0 && userStore.hasPermission('acquisition:acceptance:submit')" link type="success" @click="handleSubmit(row)">提交</el-button>
            <el-button v-if="row.acceptanceResult === 0 && userStore.hasPermission('acquisition:acceptance:approve')" link type="success" @click="handleAccept(row)">验收</el-button>
            <el-button v-if="row.acceptanceResult === 0 && userStore.hasPermission('acquisition:acceptance:delete')" link type="danger" @click="handleDelete(row)">删除</el-button>
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

    <el-dialog v-model="formDialogVisible" :title="form.id ? '编辑验收登记' : '新建验收登记'" width="820px">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="120px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="采购单号" prop="purchaseRequestNo">
              <el-input v-model="form.purchaseRequestNo" placeholder="请输入采购申请单号" clearable />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="资产名称" prop="assetName">
              <el-select
                v-model="form.assetName"
                placeholder="请选择资产主数据"
                clearable
                filterable
                allow-create
                default-first-option
                style="width: 100%"
                @change="handleAssetMasterChange"
              >
                <el-option
                  v-for="item in assetMasterList"
                  :key="item.id"
                  :label="formatAssetMasterLabel(item)"
                  :value="item.assetName"
                />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="规格型号" prop="specification">
              <el-input v-model="form.specification" placeholder="选择主数据后可自动带出" clearable />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="分类名称" prop="categoryName">
              <el-input v-model="form.categoryName" placeholder="选择主数据后可自动带出" clearable />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="8">
            <el-form-item label="验收数量" prop="quantity">
              <el-input-number v-model="form.quantity" :min="1" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="实际单价" prop="actualPrice">
              <el-input-number v-model="form.actualPrice" :min="0" :precision="2" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="实际总价">
              <el-input-number v-model="form.actualTotal" :min="0" :precision="2" disabled style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="供应商" prop="supplierName">
              <el-select
                v-model="form.supplierName"
                placeholder="请选择供应商"
                clearable
                filterable
                style="width: 100%"
                @change="handleSupplierChange"
              >
                <el-option
                  v-for="item in supplierList"
                  :key="item.id"
                  :label="item.supplierName"
                  :value="item.supplierName"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="供应商电话" prop="supplierPhone">
              <el-input v-model="form.supplierPhone" placeholder="请输入供应商电话" clearable />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="验收人" prop="acceptorName">
              <el-input v-model="form.acceptorName" placeholder="请输入验收人" clearable />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="使用部门" prop="departmentName">
              <el-input v-model="form.departmentName" placeholder="请输入使用部门" clearable />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="存放位置" prop="locationName">
              <el-input v-model="form.locationName" placeholder="请输入存放位置" clearable />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="交付日期" prop="deliveryDate">
              <el-date-picker
                v-model="form.deliveryDate"
                type="datetime"
                value-format="YYYY-MM-DDTHH:mm:ss"
                placeholder="请选择交付日期"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" :rows="3" placeholder="请输入备注" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="formDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitForm">确定</el-button>
      </template>
    </el-dialog>

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

    <el-dialog v-model="detailDialogVisible" title="验收详情" width="820px">
      <el-descriptions :column="2" border v-if="detailData">
        <el-descriptions-item label="验收单号">{{ detailData.acceptanceNo }}</el-descriptions-item>
        <el-descriptions-item label="采购单号">{{ detailData.purchaseRequestNo || '-' }}</el-descriptions-item>
        <el-descriptions-item label="资产名称">{{ detailData.assetName }}</el-descriptions-item>
        <el-descriptions-item label="规格型号">{{ detailData.specification || '-' }}</el-descriptions-item>
        <el-descriptions-item label="分类名称">{{ detailData.categoryName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="数量">{{ detailData.quantity }}</el-descriptions-item>
        <el-descriptions-item label="实际单价">¥{{ formatMoney(detailData.actualPrice) }}</el-descriptions-item>
        <el-descriptions-item label="实际总价">¥{{ formatMoney(detailData.actualTotal) }}</el-descriptions-item>
        <el-descriptions-item label="合格数量">{{ detailData.qualifiedQuantity ?? '-' }}</el-descriptions-item>
        <el-descriptions-item label="不合格数量">{{ detailData.unqualifiedQuantity ?? '-' }}</el-descriptions-item>
        <el-descriptions-item label="供应商">{{ detailData.supplierName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="供应商电话">{{ detailData.supplierPhone || '-' }}</el-descriptions-item>
        <el-descriptions-item label="验收人">{{ detailData.acceptorName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="使用部门">{{ detailData.departmentName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="存放位置">{{ detailData.locationName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="验收日期">{{ formatDateTime(detailData.acceptanceDate) }}</el-descriptions-item>
        <el-descriptions-item label="验收结果">
          <el-tag :type="getResultType(detailData.acceptanceResult)">
            {{ getResultText(detailData.acceptanceResult) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="入库状态">
          {{ detailData.storageStatus === 1 ? '已入库' : '未入库' }}
        </el-descriptions-item>
        <el-descriptions-item label="处理意见" :span="2">{{ detailData.handlingOpinion || '-' }}</el-descriptions-item>
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
import { useUserStore } from '@/store/user'
import { getEnabledAssetMasterList, getEnabledSupplierList } from '@/api/basic'
import request from '@/utils/request'

const userStore = useUserStore()
const loading = ref(false)
const tableData = ref([])
const formDialogVisible = ref(false)
const acceptDialogVisible = ref(false)
const detailDialogVisible = ref(false)
const currentRow = ref(null)
const detailData = ref(null)
const formRef = ref(null)
const assetMasterList = ref([])
const supplierList = ref([])

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

const form = reactive({
  id: null,
  purchaseRequestNo: '',
  assetName: '',
  specification: '',
  categoryName: '',
  quantity: 1,
  actualPrice: 0,
  actualTotal: 0,
  supplierName: '',
  supplierPhone: '',
  acceptorName: '',
  departmentName: '',
  locationName: '',
  deliveryDate: '',
  remark: ''
})

const acceptForm = reactive({
  qualifiedQuantity: 0,
  unqualifiedQuantity: 0,
  opinion: ''
})

const rules = {
  assetName: [{ required: true, message: '请输入资产名称', trigger: 'blur' }],
  quantity: [{ required: true, message: '请输入验收数量', trigger: 'blur' }],
  actualPrice: [{ required: true, message: '请输入实际单价', trigger: 'blur' }]
}

watch([() => form.quantity, () => form.actualPrice], () => {
  form.actualTotal = Number((Number(form.quantity || 0) * Number(form.actualPrice || 0)).toFixed(2))
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

const loadBaseOptions = async () => {
  try {
    const [assetMasterRes, supplierRes] = await Promise.all([
      getEnabledAssetMasterList(),
      getEnabledSupplierList()
    ])
    if (assetMasterRes.code === 200) {
      assetMasterList.value = assetMasterRes.data || []
    }
    if (supplierRes.code === 200) {
      supplierList.value = supplierRes.data || []
    }
  } catch (error) {
    console.error('加载基础主数据失败', error)
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

const resetForm = () => {
  Object.assign(form, {
    id: null,
    purchaseRequestNo: '',
    assetName: '',
    specification: '',
    categoryName: '',
    quantity: 1,
    actualPrice: 0,
    actualTotal: 0,
    supplierName: '',
    supplierPhone: '',
    acceptorName: '',
    departmentName: '',
    locationName: '',
    deliveryDate: '',
    remark: ''
  })
  formRef.value?.clearValidate()
}

const handleAssetMasterChange = (value) => {
  const assetMaster = assetMasterList.value.find(item => item.assetName === value)
  if (!assetMaster) {
    if (!value) {
      form.specification = ''
      form.categoryName = ''
    }
    return
  }

  form.assetName = assetMaster.assetName
  form.specification = assetMaster.specification || ''
  form.categoryName = assetMaster.categoryName || ''

  if (assetMaster.defaultPrice !== null && assetMaster.defaultPrice !== undefined) {
    form.actualPrice = Number(assetMaster.defaultPrice)
  }
}

const handleSupplierChange = (value) => {
  const supplier = supplierList.value.find(item => item.supplierName === value)
  if (!supplier) {
    form.supplierPhone = ''
    return
  }
  form.supplierPhone = supplier.contactPhone || ''
}

const handleCreate = () => {
  resetForm()
  formDialogVisible.value = true
}

const handleView = async (row) => {
  try {
    const res = await request.get(`/acquisition/acceptance/${row.id}`)
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

const formatAssetMasterLabel = (item) => {
  const parts = [item.assetName]
  if (item.specification) {
    parts.push(item.specification)
  }
  if (item.categoryName) {
    parts.push(item.categoryName)
  }
  return parts.join(' / ')
}

const handleEdit = async (row) => {
  try {
    const res = await request.get(`/acquisition/acceptance/${row.id}`)
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
      await request.put('/acquisition/acceptance', form)
      ElMessage.success('更新成功')
    } else {
      await request.post('/acquisition/acceptance', form)
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
    if (!acceptForm.opinion.trim()) {
      ElMessage.warning('请输入验收意见')
      return
    }
    if (acceptForm.qualifiedQuantity + acceptForm.unqualifiedQuantity !== currentRow.value.quantity) {
      ElMessage.error('合格数量与不合格数量之和必须等于验收数量')
      return
    }

    const url = approved
      ? `/acquisition/acceptance/${currentRow.value.id}/accept?opinion=${encodeURIComponent(acceptForm.opinion)}`
      : `/acquisition/acceptance/${currentRow.value.id}/reject?reason=${encodeURIComponent(acceptForm.opinion)}`

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
  loadBaseOptions()
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
