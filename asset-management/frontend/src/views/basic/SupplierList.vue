<template>
  <div class="supplier-list">
    <el-card class="search-card">
      <el-form :inline="true" :model="queryForm" class="search-form">
        <el-form-item label="供应商名称">
          <el-input v-model="queryForm.supplierName" placeholder="请输入供应商名称" clearable />
        </el-form-item>
        <el-form-item label="供应商编码">
          <el-input v-model="queryForm.supplierCode" placeholder="请输入供应商编码" clearable />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryForm.status" placeholder="请选择状态" clearable>
            <el-option label="正常" :value="1" />
            <el-option label="禁用" :value="0" />
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
        <el-button v-if="userStore.hasPermission('basic:supplier:create')" type="primary" @click="handleCreate">
          <el-icon><Plus /></el-icon>
          新增供应商
        </el-button>
      </div>

      <el-table :data="tableData" v-loading="loading" border stripe>
        <el-table-column prop="supplierName" label="供应商名称" min-width="180" />
        <el-table-column prop="supplierCode" label="供应商编码" width="150" />
        <el-table-column prop="contactPerson" label="联系人" width="120" />
        <el-table-column prop="contactPhone" label="联系电话" width="140" />
        <el-table-column prop="address" label="联系地址" min-width="220" show-overflow-tooltip />
        <el-table-column prop="status" label="状态" width="90" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'" size="small">
              {{ row.status === 1 ? '正常' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="sortOrder" label="排序" width="80" align="center" />
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" fixed="right" width="220">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleView(row)">详情</el-button>
            <el-button v-if="userStore.hasPermission('basic:supplier:edit')" link type="primary" @click="handleEdit(row)">编辑</el-button>
            <el-button v-if="userStore.hasPermission('basic:supplier:delete')" link type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="620px"
      @close="handleDialogClose"
    >
      <el-form ref="formRef" :model="formData" :rules="formRules" label-width="100px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="供应商名称" prop="supplierName">
              <el-input v-model="formData.supplierName" placeholder="请输入供应商名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="供应商编码" prop="supplierCode">
              <el-input v-model="formData.supplierCode" placeholder="请输入供应商编码" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="联系人" prop="contactPerson">
              <el-input v-model="formData.contactPerson" placeholder="请输入联系人" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="联系电话" prop="contactPhone">
              <el-input v-model="formData.contactPhone" placeholder="请输入联系电话" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="联系地址" prop="address">
          <el-input v-model="formData.address" placeholder="请输入联系地址" />
        </el-form-item>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="状态" prop="status">
              <el-radio-group v-model="formData.status">
                <el-radio-button :label="1">正常</el-radio-button>
                <el-radio-button :label="0">禁用</el-radio-button>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="排序" prop="sortOrder">
              <el-input-number v-model="formData.sortOrder" :min="0" :max="999" style="width: 100%" />
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

    <el-dialog v-model="detailVisible" title="供应商详情" width="620px">
      <el-descriptions :column="1" border>
        <el-descriptions-item label="供应商 ID">{{ detailData.id }}</el-descriptions-item>
        <el-descriptions-item label="供应商名称">{{ detailData.supplierName }}</el-descriptions-item>
        <el-descriptions-item label="供应商编码">{{ detailData.supplierCode }}</el-descriptions-item>
        <el-descriptions-item label="联系人">{{ detailData.contactPerson || '-' }}</el-descriptions-item>
        <el-descriptions-item label="联系电话">{{ detailData.contactPhone || '-' }}</el-descriptions-item>
        <el-descriptions-item label="联系地址">{{ detailData.address || '-' }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="detailData.status === 1 ? 'success' : 'info'" size="small">
            {{ detailData.status === 1 ? '正常' : '禁用' }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="排序">{{ detailData.sortOrder }}</el-descriptions-item>
        <el-descriptions-item label="备注">{{ detailData.remark || '-' }}</el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ detailData.createTime }}</el-descriptions-item>
        <el-descriptions-item label="更新时间">{{ detailData.updateTime }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { useUserStore } from '@/store/user'
import {
  getSupplierList,
  createSupplier,
  updateSupplier,
  deleteSupplier
} from '@/api/basic'

const userStore = useUserStore()

const queryForm = reactive({
  supplierName: '',
  supplierCode: '',
  status: null
})

const loading = ref(false)
const sourceData = ref([])
const tableData = ref([])
const dialogVisible = ref(false)
const detailVisible = ref(false)
const dialogTitle = ref('')
const submitting = ref(false)
const formRef = ref(null)
const detailData = ref({})

const formData = reactive({
  id: null,
  supplierName: '',
  supplierCode: '',
  contactPerson: '',
  contactPhone: '',
  address: '',
  status: 1,
  sortOrder: 0,
  remark: ''
})

const formRules = {
  supplierName: [
    { required: true, message: '请输入供应商名称', trigger: 'blur' }
  ],
  supplierCode: [
    { required: true, message: '请输入供应商编码', trigger: 'blur' }
  ],
  status: [
    { required: true, message: '请选择状态', trigger: 'change' }
  ]
}

const fetchData = async () => {
  loading.value = true
  try {
    const res = await getSupplierList()
    if (res.code === 200) {
      sourceData.value = res.data || []
      applyFilter()
    } else {
      ElMessage.error(res.message || '获取供应商列表失败')
    }
  } catch (error) {
    ElMessage.error('获取供应商列表失败')
    console.error(error)
  } finally {
    loading.value = false
  }
}

const applyFilter = () => {
  tableData.value = sourceData.value.filter(item => {
    const matchName = !queryForm.supplierName || item.supplierName?.includes(queryForm.supplierName)
    const matchCode = !queryForm.supplierCode || item.supplierCode?.includes(queryForm.supplierCode)
    const matchStatus = queryForm.status === null || item.status === queryForm.status
    return matchName && matchCode && matchStatus
  })
}

const handleSearch = () => {
  applyFilter()
}

const handleReset = () => {
  queryForm.supplierName = ''
  queryForm.supplierCode = ''
  queryForm.status = null
  applyFilter()
}

const resetForm = () => {
  Object.assign(formData, {
    id: null,
    supplierName: '',
    supplierCode: '',
    contactPerson: '',
    contactPhone: '',
    address: '',
    status: 1,
    sortOrder: 0,
    remark: ''
  })
}

const handleCreate = () => {
  dialogTitle.value = '新增供应商'
  resetForm()
  dialogVisible.value = true
}

const handleEdit = (row) => {
  dialogTitle.value = '编辑供应商'
  Object.assign(formData, {
    id: row.id,
    supplierName: row.supplierName,
    supplierCode: row.supplierCode,
    contactPerson: row.contactPerson,
    contactPhone: row.contactPhone,
    address: row.address,
    status: row.status,
    sortOrder: row.sortOrder,
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
    `确定要删除供应商"${row.supplierName}"吗？`,
    '提示',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    try {
      const res = await deleteSupplier(row.id)
      if (res.code === 200) {
        ElMessage.success('删除成功')
        fetchData()
      } else {
        ElMessage.error(res.message || '删除失败')
      }
    } catch (error) {
      ElMessage.error('删除失败')
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
        supplierName: formData.supplierName,
        supplierCode: formData.supplierCode,
        contactPerson: formData.contactPerson,
        contactPhone: formData.contactPhone,
        address: formData.address,
        status: formData.status,
        sortOrder: formData.sortOrder,
        remark: formData.remark
      }

      let res
      if (formData.id) {
        data.id = formData.id
        res = await updateSupplier(data)
      } else {
        res = await createSupplier(data)
      }

      if (res.code === 200) {
        ElMessage.success(formData.id ? '更新成功' : '创建成功')
        dialogVisible.value = false
        fetchData()
      } else {
        ElMessage.error(res.message || (formData.id ? '更新失败' : '创建失败'))
      }
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

onMounted(() => {
  fetchData()
})
</script>

<style scoped>
.supplier-list {
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
