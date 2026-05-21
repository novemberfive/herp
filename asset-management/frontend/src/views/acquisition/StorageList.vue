<template>
  <div class="storage-list">
    <!-- 搜索栏 -->
    <el-card class="search-card">
      <el-form :inline="true" :model="queryForm" class="search-form">
        <el-form-item label="入库单号">
          <el-input v-model="queryForm.storageNo" placeholder="请输入入库单号" clearable />
        </el-form-item>
        <el-form-item label="验收单号">
          <el-input v-model="queryForm.acceptanceNo" placeholder="请输入验收单号" clearable />
        </el-form-item>
        <el-form-item label="采购单号">
          <el-input v-model="queryForm.purchaseRequestNo" placeholder="请输入采购申请单号" clearable />
        </el-form-item>
        <el-form-item label="资产名称">
          <el-input v-model="queryForm.assetName" placeholder="请输入资产名称" clearable />
        </el-form-item>
        <el-form-item label="入库状态">
          <el-select v-model="queryForm.status" placeholder="请选择状态" clearable>
            <el-option label="待入库" :value="0" />
            <el-option label="已入库" :value="1" />
          </el-select>
        </el-form-item>
        <el-form-item label="入库类型">
          <el-select v-model="queryForm.storageType" placeholder="请选择类型" clearable>
            <el-option label="采购入库" :value="1" />
            <el-option label="退库入库" :value="2" />
            <el-option label="调拨入库" :value="3" />
            <el-option label="其他入库" :value="4" />
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
          新建入库
        </el-button>
      </div>
      
      <el-table :data="tableData" v-loading="loading" border stripe>
        <el-table-column prop="storageNo" label="入库单号" width="180" />
        <el-table-column prop="acceptanceNo" label="验收单号" width="160" />
        <el-table-column prop="purchaseRequestNo" label="采购单号" width="160" />
        <el-table-column prop="assetCode" label="资产编码" width="140" />
        <el-table-column prop="assetName" label="资产名称" width="150" />
        <el-table-column prop="specification" label="规格型号" width="140" />
        <el-table-column prop="quantity" label="数量" width="80" align="center" />
        <el-table-column prop="unitPrice" label="单价" width="100" align="right">
          <template #default="{ row }">
            ¥{{ row.unitPrice?.toFixed(2) }}
          </template>
        </el-table-column>
        <el-table-column prop="totalAmount" label="总金额" width="120" align="right">
          <template #default="{ row }">
            ¥{{ row.totalAmount?.toFixed(2) }}
          </template>
        </el-table-column>
        <el-table-column prop="departmentName" label="使用部门" width="120" />
        <el-table-column prop="locationName" label="存放位置" width="120" />
        <el-table-column prop="keeperName" label="保管人" width="100" />
        <el-table-column prop="storageDate" label="入库日期" width="160">
          <template #default="{ row }">
            {{ formatDateTime(row.storageDate) }}
          </template>
        </el-table-column>
        <el-table-column prop="storageType" label="入库类型" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="getStorageTypeTag(row.storageType)">
              {{ getStorageTypeText(row.storageType) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="90" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'warning'">
              {{ row.status === 1 ? '已入库' : '待入库' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" fixed="right" width="240">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleView(row)">详情</el-button>
            <el-button link type="primary" @click="handleEdit(row)" v-if="row.status === 0">编辑</el-button>
            <el-button link type="success" @click="handleConfirm(row)" v-if="row.status === 0">确认入库</el-button>
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

    <!-- 新建/编辑入库对话框 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="800px">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="120px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="关联验收单号" prop="acceptanceNo">
              <el-input v-model="form.acceptanceNo" placeholder="请输入验收单号" clearable />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="关联采购单号" prop="purchaseRequestNo">
              <el-input v-model="form.purchaseRequestNo" placeholder="请输入采购申请单号" clearable />
            </el-form-item>
          </el-col>
        </el-row>
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
          <el-col :span="8">
            <el-form-item label="入库数量" prop="quantity">
              <el-input-number v-model="form.quantity" :min="1" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="单价" prop="unitPrice">
              <el-input-number v-model="form.unitPrice" :min="0" :precision="2" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="总金额" prop="totalAmount">
              <el-input-number v-model="form.totalAmount" :min="0" :precision="2" disabled style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="供应商名称" prop="supplierName">
              <el-input v-model="form.supplierName" placeholder="请输入供应商名称" clearable />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="入库类型" prop="storageType">
              <el-select v-model="form.storageType" placeholder="请选择入库类型" style="width: 100%">
                <el-option label="采购入库" :value="1" />
                <el-option label="退库入库" :value="2" />
                <el-option label="调拨入库" :value="3" />
                <el-option label="其他入库" :value="4" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="使用部门" prop="departmentName">
              <el-input v-model="form.departmentName" placeholder="请输入使用部门" clearable />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="存放位置" prop="locationName">
              <el-input v-model="form.locationName" placeholder="请输入存放位置" clearable />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="保管人" prop="keeperName">
              <el-input v-model="form.keeperName" placeholder="请输入保管人" clearable />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item label="备注" prop="remark">
              <el-input v-model="form.remark" type="textarea" :rows="3" placeholder="请输入备注" />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitForm">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '@/utils/request'

const loading = ref(false)
const tableData = ref([])
const dialogVisible = ref(false)
const dialogTitle = ref('')
const formRef = ref(null)
const isEdit = ref(false)

const queryForm = reactive({
  storageNo: '',
  acceptanceNo: '',
  purchaseRequestNo: '',
  assetName: '',
  status: null,
  storageType: null
})

const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

const form = reactive({
  id: null,
  acceptanceNo: '',
  purchaseRequestNo: '',
  assetCode: '',
  assetName: '',
  specification: '',
  categoryName: '',
  quantity: 1,
  unitPrice: 0,
  totalAmount: 0,
  supplierName: '',
  storageType: 1,
  departmentName: '',
  locationName: '',
  keeperName: '',
  remark: ''
})

const rules = {
  assetName: [{ required: true, message: '请输入资产名称', trigger: 'blur' }],
  quantity: [{ required: true, message: '请输入入库数量', trigger: 'blur' }]
}

// 监听数量和单价变化，自动计算总金额
watch([() => form.quantity, () => form.unitPrice], () => {
  form.totalAmount = (form.quantity * form.unitPrice).toFixed(2)
})

const fetchData = async () => {
  loading.value = true
  try {
    const params = {
      pageNum: pagination.pageNum,
      pageSize: pagination.pageSize,
      ...queryForm
    }
    const res = await request.get('/acquisition/storage/list', { params })
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
  queryForm.storageNo = ''
  queryForm.acceptanceNo = ''
  queryForm.purchaseRequestNo = ''
  queryForm.assetName = ''
  queryForm.status = null
  queryForm.storageType = null
  handleSearch()
}

const handleCreate = () => {
  isEdit.value = false
  dialogTitle.value = '新建入库'
  resetForm()
  dialogVisible.value = true
}

const handleView = (row) => {
  ElMessage.info('查看详情：' + row.storageNo)
}

const handleEdit = (row) => {
  isEdit.value = true
  dialogTitle.value = '编辑入库'
  Object.assign(form, row)
  dialogVisible.value = true
}

const handleConfirm = async (row) => {
  try {
    await ElMessageBox.confirm('确定要确认该资产入库吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await request.post(`/acquisition/storage/${row.id}/confirm`)
    ElMessage.success('确认入库成功')
    fetchData()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('确认入库失败')
    }
  }
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除该入库记录吗？', '警告', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await request.delete(`/acquisition/storage/${row.id}`)
    ElMessage.success('删除成功')
    fetchData()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

const submitForm = async () => {
  if (!formRef.value) return
  
  try {
    await formRef.value.validate()
    
    if (isEdit.value) {
      await request.put('/acquisition/storage', form)
      ElMessage.success('更新成功')
    } else {
      await request.post('/acquisition/storage', form)
      ElMessage.success('创建成功')
    }
    
    dialogVisible.value = false
    fetchData()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(isEdit.value ? '更新失败' : '创建失败')
    }
  }
}

const resetForm = () => {
  form.id = null
  form.acceptanceNo = ''
  form.purchaseRequestNo = ''
  form.assetCode = ''
  form.assetName = ''
  form.specification = ''
  form.categoryName = ''
  form.quantity = 1
  form.unitPrice = 0
  form.totalAmount = 0
  form.supplierName = ''
  form.storageType = 1
  form.departmentName = ''
  form.locationName = ''
  form.keeperName = ''
  form.remark = ''
  if (formRef.value) {
    formRef.value.clearValidate()
  }
}

const getStorageTypeTag = (type) => {
  const types = { 1: 'primary', 2: 'success', 3: 'warning', 4: 'info' }
  return types[type] || 'info'
}

const getStorageTypeText = (type) => {
  const texts = { 1: '采购入库', 2: '退库入库', 3: '调拨入库', 4: '其他入库' }
  return texts[type] || '未知'
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
.storage-list {
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
