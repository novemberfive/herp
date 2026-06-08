<template>
  <div class="asset-master-list">
    <el-card class="search-card">
      <el-form :inline="true" :model="queryForm" class="search-form">
        <el-form-item label="资产编码">
          <el-input v-model="queryForm.assetCode" placeholder="请输入资产编码" clearable />
        </el-form-item>
        <el-form-item label="资产名称">
          <el-input v-model="queryForm.assetName" placeholder="请输入资产名称" clearable />
        </el-form-item>
        <el-form-item label="分类">
          <el-select v-model="queryForm.categoryId" placeholder="请选择分类" clearable filterable>
            <el-option
              v-for="item in categoryOptions"
              :key="item.id"
              :label="item.categoryName"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="品牌">
          <el-input v-model="queryForm.brand" placeholder="请输入品牌" clearable />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryForm.status" placeholder="请选择状态" clearable>
            <el-option label="启用" :value="1" />
            <el-option label="停用" :value="0" />
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
        <el-button v-if="userStore.hasPermission('basic:asset-master:create')" type="primary" @click="handleCreate">
          <el-icon><Plus /></el-icon>
          新增资产主数据
        </el-button>
      </div>

      <el-table :data="tableData" v-loading="loading" border stripe>
        <el-table-column prop="assetCode" label="资产编码" width="180" />
        <el-table-column prop="assetName" label="资产名称" min-width="160" />
        <el-table-column prop="categoryName" label="分类" width="140" />
        <el-table-column prop="specification" label="规格型号" width="160" />
        <el-table-column prop="brand" label="品牌" width="120" />
        <el-table-column prop="unit" label="单位" width="80" align="center" />
        <el-table-column prop="defaultPrice" label="默认单价" width="120" align="right">
          <template #default="{ row }">¥{{ formatMoney(row.defaultPrice) }}</template>
        </el-table-column>
        <el-table-column prop="stockQuantity" label="库存" width="80" align="center" />
        <el-table-column prop="warrantyMonths" label="保修(月)" width="90" align="center" />
        <el-table-column prop="status" label="状态" width="90" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'" size="small">
              {{ row.status === 1 ? '启用' : '停用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" fixed="right" width="300">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleView(row)">详情</el-button>
            <el-button v-if="userStore.hasPermission('basic:asset-master:edit')" link type="primary" @click="handleEdit(row)">编辑</el-button>
            <el-button
              v-if="userStore.hasPermission('basic:asset-master:status')"
              link
              :type="row.status === 1 ? 'warning' : 'success'"
              @click="handleToggleStatus(row)"
            >
              {{ row.status === 1 ? '停用' : '启用' }}
            </el-button>
            <el-button v-if="userStore.hasPermission('basic:asset-master:delete')" link type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="720px"
      @close="handleDialogClose"
    >
      <el-form ref="formRef" :model="formData" :rules="formRules" label-width="110px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="资产编码" prop="assetCode">
              <el-input v-model="formData.assetCode" placeholder="请输入资产编码" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="资产名称" prop="assetName">
              <el-input v-model="formData.assetName" placeholder="请输入资产名称" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="资产分类" prop="categoryId">
              <el-select v-model="formData.categoryId" placeholder="请选择分类" filterable style="width: 100%">
                <el-option
                  v-for="item in categoryOptions"
                  :key="item.id"
                  :label="item.categoryName"
                  :value="item.id"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="规格型号" prop="specification">
              <el-input v-model="formData.specification" placeholder="请输入规格型号" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="品牌" prop="brand">
              <el-input v-model="formData.brand" placeholder="请输入品牌" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="计量单位" prop="unit">
              <el-input v-model="formData.unit" placeholder="如：台、件、套" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="默认单价" prop="defaultPrice">
              <el-input-number v-model="formData.defaultPrice" :min="0" :precision="2" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="库存数量" prop="stockQuantity">
              <el-input-number v-model="formData.stockQuantity" :min="0" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="8">
            <el-form-item label="保修(月)" prop="warrantyMonths">
              <el-input-number v-model="formData.warrantyMonths" :min="0" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="保养周期" prop="maintenanceCycleDays">
              <el-input-number v-model="formData.maintenanceCycleDays" :min="0" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="使用寿命" prop="useLifeMonths">
              <el-input-number v-model="formData.useLifeMonths" :min="0" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="8">
            <el-form-item label="最低库存" prop="minStock">
              <el-input-number v-model="formData.minStock" :min="0" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="最高库存" prop="maxStock">
              <el-input-number v-model="formData.maxStock" :min="0" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="状态" prop="status">
              <el-radio-group v-model="formData.status">
                <el-radio-button :label="1">启用</el-radio-button>
                <el-radio-button :label="0">停用</el-radio-button>
              </el-radio-group>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="formData.remark" type="textarea" :rows="3" placeholder="请输入备注" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="detailVisible" title="资产主数据详情" width="720px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="资产编码">{{ detailData.assetCode }}</el-descriptions-item>
        <el-descriptions-item label="资产名称">{{ detailData.assetName }}</el-descriptions-item>
        <el-descriptions-item label="分类">{{ detailData.categoryName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="规格型号">{{ detailData.specification || '-' }}</el-descriptions-item>
        <el-descriptions-item label="品牌">{{ detailData.brand || '-' }}</el-descriptions-item>
        <el-descriptions-item label="计量单位">{{ detailData.unit || '-' }}</el-descriptions-item>
        <el-descriptions-item label="默认单价">¥{{ formatMoney(detailData.defaultPrice) }}</el-descriptions-item>
        <el-descriptions-item label="库存数量">{{ detailData.stockQuantity ?? 0 }}</el-descriptions-item>
        <el-descriptions-item label="保修(月)">{{ detailData.warrantyMonths ?? '-' }}</el-descriptions-item>
        <el-descriptions-item label="保养周期(天)">{{ detailData.maintenanceCycleDays ?? '-' }}</el-descriptions-item>
        <el-descriptions-item label="使用寿命(月)">{{ detailData.useLifeMonths ?? '-' }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="detailData.status === 1 ? 'success' : 'info'" size="small">
            {{ detailData.status === 1 ? '启用' : '停用' }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="最低库存">{{ detailData.minStock ?? 0 }}</el-descriptions-item>
        <el-descriptions-item label="最高库存">{{ detailData.maxStock ?? '-' }}</el-descriptions-item>
        <el-descriptions-item label="备注" :span="2">{{ detailData.remark || '-' }}</el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ detailData.createTime || '-' }}</el-descriptions-item>
        <el-descriptions-item label="更新时间">{{ detailData.updateTime || '-' }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { useUserStore } from '@/store/user'
import {
  createAssetMaster,
  deleteAssetMaster,
  getAssetMasterList,
  updateAssetMaster,
  updateAssetMasterStatus,
  getCategoryList
} from '@/api/basic'

const userStore = useUserStore()

const loading = ref(false)
const submitting = ref(false)
const dialogVisible = ref(false)
const detailVisible = ref(false)
const dialogTitle = ref('')
const formRef = ref(null)
const sourceData = ref([])
const tableData = ref([])
const categoryOptions = ref([])
const detailData = ref({})

const queryForm = reactive({
  assetCode: '',
  assetName: '',
  categoryId: null,
  brand: '',
  status: null
})

const formData = reactive({
  id: null,
  assetCode: '',
  assetName: '',
  categoryId: null,
  specification: '',
  brand: '',
  unit: '台',
  defaultPrice: 0,
  warrantyMonths: null,
  maintenanceCycleDays: null,
  useLifeMonths: null,
  stockQuantity: 0,
  minStock: 0,
  maxStock: null,
  status: 1,
  remark: ''
})

const formRules = {
  assetCode: [{ required: true, message: '请输入资产编码', trigger: 'blur' }],
  assetName: [{ required: true, message: '请输入资产名称', trigger: 'blur' }],
  categoryId: [{ required: true, message: '请选择资产分类', trigger: 'change' }],
  unit: [{ required: true, message: '请输入计量单位', trigger: 'blur' }],
  status: [{ required: true, message: '请选择状态', trigger: 'change' }]
}

const fetchData = async () => {
  loading.value = true
  try {
    const res = await getAssetMasterList()
    sourceData.value = res.data || []
    applyFilter()
  } catch (error) {
    ElMessage.error('获取资产主数据列表失败')
    console.error(error)
  } finally {
    loading.value = false
  }
}

const fetchCategories = async () => {
  try {
    const res = await getCategoryList()
    categoryOptions.value = res.data || []
  } catch (error) {
    console.error('获取分类失败', error)
  }
}

const applyFilter = () => {
  tableData.value = sourceData.value.filter(item => {
    const matchCode = !queryForm.assetCode || item.assetCode?.includes(queryForm.assetCode)
    const matchName = !queryForm.assetName || item.assetName?.includes(queryForm.assetName)
    const matchCategory = !queryForm.categoryId || item.categoryId === queryForm.categoryId
    const matchBrand = !queryForm.brand || item.brand?.includes(queryForm.brand)
    const matchStatus = queryForm.status === null || item.status === queryForm.status
    return matchCode && matchName && matchCategory && matchBrand && matchStatus
  })
}

const handleSearch = () => {
  applyFilter()
}

const handleReset = () => {
  queryForm.assetCode = ''
  queryForm.assetName = ''
  queryForm.categoryId = null
  queryForm.brand = ''
  queryForm.status = null
  applyFilter()
}

const resetForm = () => {
  Object.assign(formData, {
    id: null,
    assetCode: '',
    assetName: '',
    categoryId: null,
    specification: '',
    brand: '',
    unit: '台',
    defaultPrice: 0,
    warrantyMonths: null,
    maintenanceCycleDays: null,
    useLifeMonths: null,
    stockQuantity: 0,
    minStock: 0,
    maxStock: null,
    status: 1,
    remark: ''
  })
}

const handleCreate = () => {
  dialogTitle.value = '新增资产主数据'
  resetForm()
  dialogVisible.value = true
}

const handleEdit = (row) => {
  dialogTitle.value = '编辑资产主数据'
  Object.assign(formData, {
    id: row.id,
    assetCode: row.assetCode,
    assetName: row.assetName,
    categoryId: row.categoryId,
    specification: row.specification,
    brand: row.brand,
    unit: row.unit || '台',
    defaultPrice: Number(row.defaultPrice || 0),
    warrantyMonths: row.warrantyMonths,
    maintenanceCycleDays: row.maintenanceCycleDays,
    useLifeMonths: row.useLifeMonths,
    stockQuantity: row.stockQuantity ?? 0,
    minStock: row.minStock ?? 0,
    maxStock: row.maxStock,
    status: row.status,
    remark: row.remark
  })
  dialogVisible.value = true
}

const handleView = (row) => {
  detailData.value = { ...row }
  detailVisible.value = true
}

const handleDelete = (row) => {
  ElMessageBox.confirm(
    `确定要删除资产主数据"${row.assetName}"吗？`,
    '提示',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    try {
      await deleteAssetMaster(row.id)
      ElMessage.success('删除成功')
      fetchData()
    } catch (error) {
      ElMessage.error('删除失败')
      console.error(error)
    }
  }).catch(() => {})
}

const handleToggleStatus = (row) => {
  const nextStatus = row.status === 1 ? 0 : 1
  const actionText = nextStatus === 1 ? '启用' : '停用'
  ElMessageBox.confirm(
    `确定要${actionText}资产主数据"${row.assetName}"吗？`,
    '提示',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    try {
      await updateAssetMasterStatus(row.id, nextStatus)
      ElMessage.success(`${actionText}成功`)
      fetchData()
    } catch (error) {
      ElMessage.error(`${actionText}失败`)
      console.error(error)
    }
  }).catch(() => {})
}

const handleSubmit = async () => {
  if (!formRef.value) return

  await formRef.value.validate(async (valid) => {
    if (!valid) return

    submitting.value = true
    try {
      const data = {
        assetCode: formData.assetCode,
        assetName: formData.assetName,
        categoryId: formData.categoryId,
        specification: formData.specification,
        brand: formData.brand,
        unit: formData.unit,
        defaultPrice: formData.defaultPrice,
        warrantyMonths: formData.warrantyMonths,
        maintenanceCycleDays: formData.maintenanceCycleDays,
        useLifeMonths: formData.useLifeMonths,
        stockQuantity: formData.stockQuantity,
        minStock: formData.minStock,
        maxStock: formData.maxStock,
        status: formData.status,
        remark: formData.remark
      }

      if (formData.id) {
        data.id = formData.id
        await updateAssetMaster(data)
      } else {
        await createAssetMaster(data)
      }

      ElMessage.success(formData.id ? '更新成功' : '创建成功')
      dialogVisible.value = false
      fetchData()
    } catch (error) {
      ElMessage.error(formData.id ? '更新失败' : '创建失败')
      console.error(error)
    } finally {
      submitting.value = false
    }
  })
}

const handleDialogClose = () => {
  formRef.value?.resetFields()
  resetForm()
}

const formatMoney = (value) => {
  if (value === null || value === undefined || value === '') return '0.00'
  return Number(value).toFixed(2)
}

onMounted(() => {
  fetchData()
  fetchCategories()
})
</script>

<style scoped>
.asset-master-list {
  padding: 20px;
}

.search-card {
  margin-bottom: 20px;
}

.table-card {
  margin-bottom: 20px;
}

.toolbar {
  display: flex;
  gap: 12px;
  align-items: center;
  margin-bottom: 16px;
}
</style>
