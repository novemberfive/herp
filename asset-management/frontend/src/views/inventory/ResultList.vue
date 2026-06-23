<template>
  <div class="result-list">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>盘点结果</span>
          <div>
            <el-button v-if="userStore.hasPermission('inventory:result:import')" type="success" @click="handleImport">
              <el-icon><Upload /></el-icon>
              批量导入
            </el-button>
            <el-button v-if="userStore.hasPermission('inventory:result:create')" type="primary" @click="handleCreate">
              <el-icon><Plus /></el-icon>
              新建记录
            </el-button>
          </div>
        </div>
      </template>

      <!-- 搜索栏 -->
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="盘点任务">
          <el-select v-model="searchForm.taskId" placeholder="请选择任务" clearable>
            <el-option
              v-for="item in taskList"
              :key="item.id"
              :label="item.taskName"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="全部状态" clearable>
            <el-option label="待提交" :value="0" />
            <el-option label="待复核" :value="1" />
            <el-option label="已复核" :value="2" />
            <el-option label="已处理" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <!-- 表格 -->
      <el-table :data="tableData" v-loading="loading" border stripe>
        <el-table-column prop="resultNo" label="结果编号" width="150" />
        <el-table-column prop="taskName" label="盘点任务" min-width="150" />
        <el-table-column prop="assetNo" label="资产编码" width="120" />
        <el-table-column prop="assetName" label="资产名称" min-width="150" />
        <el-table-column prop="checkStatus" label="盘点状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="getCheckStatusType(row.checkStatus)">
              {{ getCheckStatusText(row.checkStatus) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="处理状态" width="80" align="center">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="checkerUserId" label="盘点人" width="100" />
        <el-table-column prop="checkTime" label="盘点时间" width="160" />
        <el-table-column label="操作" width="280" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleView(row)">详情</el-button>
            <el-button 
              v-if="row.status === 0 && userStore.hasPermission('inventory:result:submit')"
              link 
              type="success" 
              @click="handleSubmit(row)"
            >
              提交
            </el-button>
            <el-button 
              v-if="row.status === 1 && userStore.hasPermission('inventory:result:review')"
              link 
              type="warning" 
              @click="handleReview(row)"
            >
              复核
            </el-button>
            <el-button 
              v-if="row.status === 2 && userStore.hasPermission('inventory:result:process')"
              link 
              type="primary" 
              @click="handleHandle(row)"
            >
              处理
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <el-pagination
        v-model:current-page="pagination.pageNum"
        v-model:page-size="pagination.pageSize"
        :total="pagination.total"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="fetchData"
        @current-change="fetchData"
        style="margin-top: 20px; justify-content: flex-end;"
      />
    </el-card>

    <!-- 新增/编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="600px"
      @close="handleDialogClose"
    >
      <el-form
        ref="formRef"
        :model="formData"
        :rules="formRules"
        label-width="100px"
      >
        <el-form-item label="盘点任务" prop="taskId">
          <el-select v-model="formData.taskId" placeholder="请选择盘点任务" style="width: 100%">
            <el-option
              v-for="item in taskList"
              :key="item.id"
              :label="item.taskName"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="资产编码" prop="assetNo">
          <el-input v-model="formData.assetNo" placeholder="请输入资产编码" />
        </el-form-item>
        <el-form-item label="资产名称" prop="assetName">
          <el-input v-model="formData.assetName" placeholder="请输入资产名称" />
        </el-form-item>
        <el-form-item label="盘点状态" prop="checkStatus">
          <el-select v-model="formData.checkStatus" placeholder="请选择盘点状态" style="width: 100%">
            <el-option label="盘盈" :value="1" />
            <el-option label="盘亏" :value="2" />
            <el-option label="正常" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item label="差异说明" prop="differenceDesc">
          <el-input
            v-model="formData.differenceDesc"
            type="textarea"
            :rows="3"
            placeholder="请输入差异说明"
          />
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input
            v-model="formData.remark"
            type="textarea"
            :rows="3"
            placeholder="请输入备注"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmitForm" :loading="submitLoading">
          确定
        </el-button>
      </template>
    </el-dialog>

    <!-- 详情对话框 -->
    <el-dialog
      v-model="detailVisible"
      title="盘点结果详情"
      width="600px"
    >
      <el-descriptions :column="1" border>
        <el-descriptions-item label="结果编号">{{ detailData.resultNo }}</el-descriptions-item>
        <el-descriptions-item label="盘点任务">{{ detailData.taskName }}</el-descriptions-item>
        <el-descriptions-item label="资产编码">{{ detailData.assetNo }}</el-descriptions-item>
        <el-descriptions-item label="资产名称">{{ detailData.assetName }}</el-descriptions-item>
        <el-descriptions-item label="盘点状态">
          {{ getCheckStatusText(detailData.checkStatus) }}
        </el-descriptions-item>
        <el-descriptions-item label="处理状态">
          <el-tag :type="getStatusType(detailData.status)">
            {{ getStatusText(detailData.status) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="差异说明" v-if="detailData.differenceDesc">
          {{ detailData.differenceDesc }}
        </el-descriptions-item>
        <el-descriptions-item label="盘点人">{{ detailData.checkerUserId }}</el-descriptions-item>
        <el-descriptions-item label="盘点时间">{{ detailData.checkTime }}</el-descriptions-item>
        <el-descriptions-item label="备注" v-if="detailData.remark">
          {{ detailData.remark }}
        </el-descriptions-item>
      </el-descriptions>
    </el-dialog>

    <!-- 批量导入对话框 -->
    <el-dialog
      v-model="importVisible"
      title="批量导入盘点结果"
      width="500px"
    >
      <el-upload
        drag
        :auto-upload="false"
        :on-change="handleFileChange"
        :limit="1"
        accept=".xlsx,.xls"
      >
        <el-icon class="el-icon--upload"><upload-filled /></el-icon>
        <div class="el-upload__text">
          拖拽文件到此处或 <em>点击上传</em>
        </div>
        <template #tip>
          <div class="el-upload__tip">
            只能上传 xlsx/xls 文件
          </div>
        </template>
      </el-upload>
      <template #footer>
        <el-button @click="importVisible = false">取消</el-button>
        <el-button type="primary" @click="handleImportSubmit" :loading="importLoading">
          导入
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Upload, UploadFilled } from '@element-plus/icons-vue'
import { useUserStore } from '@/store/user'
import { getResultList, getResultById, createResult, updateResult, deleteResult, submitResult, reviewResult, handleResult, importResult, getTaskList } from '@/api/inventory'

const userStore = useUserStore()
const loading = ref(false)
const submitLoading = ref(false)
const importLoading = ref(false)
const tableData = ref([])
const dialogVisible = ref(false)
const detailVisible = ref(false)
const importVisible = ref(false)
const dialogTitle = ref('新建记录')
const isEdit = ref(false)
const taskList = ref([])
const importFile = ref(null)

const searchForm = reactive({
  taskId: null,
  status: null
})

const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

const formData = reactive({
  id: null,
  resultNo: null,
  taskId: null,
  assetNo: '',
  assetName: '',
  checkStatus: null,
  differenceDesc: '',
  remark: ''
})

const formRules = {
  taskId: [
    { required: true, message: '请选择盘点任务', trigger: 'change' }
  ],
  assetNo: [
    { required: true, message: '请输入资产编码', trigger: 'blur' }
  ],
  assetName: [
    { required: true, message: '请输入资产名称', trigger: 'blur' }
  ],
  checkStatus: [
    { required: true, message: '请选择盘点状态', trigger: 'change' }
  ]
}

const detailData = ref({})

const fetchTaskList = async () => {
  try {
    const res = await getTaskList(1, 100)
    taskList.value = res.data.list || []
  } catch (error) {
    console.error(error)
  }
}

const fetchData = async () => {
  loading.value = true
  try {
    const res = await getResultList(
      pagination.pageNum, 
      pagination.pageSize, 
      searchForm.taskId,
      searchForm.status
    )
    tableData.value = res.data.list
    pagination.total = res.data.total
  } catch (error) {
    console.error(error)
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
  searchForm.taskId = null
  searchForm.status = null
  handleSearch()
}

const handleCreate = () => {
  isEdit.value = false
  dialogTitle.value = '新建记录'
  resetForm()
  dialogVisible.value = true
}

const handleView = async (row) => {
  try {
    const res = await getResultById(row.id)
    detailData.value = res.data
    detailVisible.value = true
  } catch (error) {
    console.error(error)
    ElMessage.error('获取详情失败')
  }
}

const handleSubmitForm = async () => {
  try {
    await formRef.value.validate()
    submitLoading.value = true
    if (isEdit.value) {
      await updateResult(formData)
      ElMessage.success('更新成功')
    } else {
      await createResult(formData)
      ElMessage.success('创建成功')
    }
    dialogVisible.value = false
    fetchData()
  } catch (error) {
    if (error !== 'cancel') {
      console.error(error)
    }
  } finally {
    submitLoading.value = false
  }
}

const handleDialogClose = () => {
  resetForm()
}

const resetForm = () => {
  Object.assign(formData, {
    id: null,
    resultNo: null,
    taskId: null,
    assetNo: '',
    assetName: '',
    checkStatus: null,
    differenceDesc: '',
    remark: ''
  })
}

const handleSubmit = async (row) => {
  try {
    await ElMessageBox.confirm(`确定要提交该盘点结果吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await submitResult(row.id)
    ElMessage.success('结果已提交')
    fetchData()
  } catch (error) {
    if (error !== 'cancel') {
      console.error(error)
    }
  }
}

const handleReview = async (row) => {
  try {
    await ElMessageBox.confirm(`确定要复核该盘点结果吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await reviewResult(row.id)
    ElMessage.success('结果已复核')
    fetchData()
  } catch (error) {
    if (error !== 'cancel') {
      console.error(error)
    }
  }
}

const handleHandle = async (row) => {
  try {
    await ElMessageBox.confirm(`确定要处理该盘点结果吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await handleResult(row.id)
    ElMessage.success('结果已处理')
    fetchData()
  } catch (error) {
    if (error !== 'cancel') {
      console.error(error)
    }
  }
}

const handleImport = () => {
  importFile.value = null
  importVisible.value = true
}

const handleFileChange = (file) => {
  importFile.value = file.raw
}

const handleImportSubmit = async () => {
  if (!importFile.value) {
    ElMessage.warning('请选择要导入的文件')
    return
  }
  
  try {
    importLoading.value = true
    const formDataUpload = new FormData()
    formDataUpload.append('file', importFile.value)
    await importResult(formDataUpload)
    ElMessage.success('导入成功')
    importVisible.value = false
    fetchData()
  } catch (error) {
    console.error(error)
    ElMessage.error('导入失败')
  } finally {
    importLoading.value = false
  }
}

const getCheckStatusType = (status) => {
  const types = {
    1: 'success',
    2: 'danger',
    3: 'info'
  }
  return types[status] || 'info'
}

const getCheckStatusText = (status) => {
  const texts = {
    1: '盘盈',
    2: '盘亏',
    3: '正常'
  }
  return texts[status] || '未知'
}

const getStatusType = (status) => {
  const types = {
    0: 'info',
    1: 'warning',
    2: 'success',
    3: 'primary'
  }
  return types[status] || 'info'
}

const getStatusText = (status) => {
  const texts = {
    0: '待提交',
    1: '待复核',
    2: '已复核',
    3: '已处理'
  }
  return texts[status] || '未知'
}

const formRef = ref(null)

onMounted(() => {
  fetchTaskList()
  fetchData()
})
</script>

<style scoped>
.result-list {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.search-form {
  margin-bottom: 20px;
}
</style>
