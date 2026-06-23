<template>
  <div class="task-list">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>盘点任务</span>
          <el-button v-if="userStore.hasPermission('inventory:task:create')" type="primary" @click="handleCreate">
            <el-icon><Plus /></el-icon>
            新建任务
          </el-button>
        </div>
      </template>

      <!-- 搜索栏 -->
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="任务类型">
          <el-select v-model="searchForm.taskType" placeholder="全部类型" clearable>
            <el-option label="全盘" :value="1" />
            <el-option label="抽盘" :value="2" />
            <el-option label="循环盘点" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item label="任务状态">
          <el-select v-model="searchForm.status" placeholder="全部状态" clearable>
            <el-option label="未开始" :value="0" />
            <el-option label="进行中" :value="1" />
            <el-option label="已完成" :value="2" />
            <el-option label="已终止" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <!-- 表格 -->
      <el-table :data="tableData" v-loading="loading" border stripe>
        <el-table-column prop="taskNo" label="任务编号" width="150" />
        <el-table-column prop="taskName" label="任务名称" min-width="150" />
        <el-table-column prop="taskType" label="任务类型" width="100" align="center">
          <template #default="{ row }">
            {{ getTaskTypeText(row.taskType) }}
          </template>
        </el-table-column>
        <el-table-column prop="startDate" label="开始日期" width="120" />
        <el-table-column prop="endDate" label="结束日期" width="120" />
        <el-table-column prop="status" label="状态" width="80" align="center">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createUserId" label="创建人" width="100" />
        <el-table-column prop="createTime" label="创建时间" width="160" />
        <el-table-column label="操作" width="280" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleView(row)">详情</el-button>
            <el-button 
              v-if="row.status === 0 && userStore.hasPermission('inventory:task:start')"
              link 
              type="success" 
              @click="handleStart(row)"
            >
              开始
            </el-button>
            <el-button 
              v-if="row.status === 1 && userStore.hasPermission('inventory:task:complete')"
              link 
              type="warning" 
              @click="handleComplete(row)"
            >
              完成
            </el-button>
            <el-button 
              v-if="row.status !== 2 && row.status !== 3 && userStore.hasPermission('inventory:task:cancel')"
              link 
              type="danger" 
              @click="handleCancel(row)"
            >
              终止
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

    <!-- 新建/编辑任务对话框 -->
    <el-dialog v-model="formDialogVisible" :title="formData.id ? '编辑盘点任务' : '新建盘点任务'" width="700px">
      <el-form :model="formData" label-width="120px">
        <el-form-item label="任务名称" required>
          <el-input v-model="formData.taskName" placeholder="请输入任务名称" />
        </el-form-item>
        <el-form-item label="任务类型" required>
          <el-select v-model="formData.taskType" placeholder="请选择任务类型" style="width: 100%">
            <el-option label="全盘" :value="1" />
            <el-option label="抽盘" :value="2" />
            <el-option label="循环盘点" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item label="盘点分类 IDs">
          <el-input v-model="formData.categoryIds" placeholder="请输入分类 IDs，多个用逗号分隔" />
        </el-form-item>
        <el-form-item label="盘点位置 IDs">
          <el-input v-model="formData.locationIds" placeholder="请输入位置 IDs，多个用逗号分隔" />
        </el-form-item>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="开始日期">
              <el-date-picker
                v-model="formData.startDate"
                type="date"
                placeholder="选择日期"
                value-format="YYYY-MM-DD"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="结束日期">
              <el-date-picker
                v-model="formData.endDate"
                type="date"
                placeholder="选择日期"
                value-format="YYYY-MM-DD"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
        </el-row>
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
    <el-dialog v-model="viewDialogVisible" title="盘点任务详情" width="800px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="任务编号">{{ viewData.taskNo }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="getStatusType(viewData.status)">{{ getStatusText(viewData.status) }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="任务名称">{{ viewData.taskName }}</el-descriptions-item>
        <el-descriptions-item label="任务类型">{{ getTaskTypeText(viewData.taskType) }}</el-descriptions-item>
        <el-descriptions-item label="盘点分类 IDs">{{ viewData.categoryIds || '-' }}</el-descriptions-item>
        <el-descriptions-item label="盘点位置 IDs">{{ viewData.locationIds || '-' }}</el-descriptions-item>
        <el-descriptions-item label="开始日期">{{ formatDate(viewData.startDate) }}</el-descriptions-item>
        <el-descriptions-item label="结束日期">{{ formatDate(viewData.endDate) }}</el-descriptions-item>
        <el-descriptions-item label="创建人 ID">{{ viewData.createUserId }}</el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ viewData.createTime }}</el-descriptions-item>
        <el-descriptions-item v-if="viewData.status === 2" label="完成时间">{{ viewData.completeTime }}</el-descriptions-item>
        <el-descriptions-item label="备注" :span="2">{{ viewData.remark || '-' }}</el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <el-button @click="viewDialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { useUserStore } from '@/store/user'
import { getTaskList, createTask, updateTask, deleteTask, startTask, completeTask, cancelTask, getTaskById } from '@/api/inventory'

const router = useRouter()
const userStore = useUserStore()
const loading = ref(false)
const tableData = ref([])
const formDialogVisible = ref(false)
const viewDialogVisible = ref(false)
const currentRow = ref(null)

const searchForm = reactive({
  taskType: null,
  status: null
})

const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

const formData = reactive({
  id: null,
  taskName: '',
  taskType: null,
  categoryIds: '',
  locationIds: '',
  startDate: '',
  endDate: '',
  remark: ''
})

const viewData = ref({})

const fetchData = async () => {
  loading.value = true
  try {
    const res = await getTaskList(
      pagination.pageNum, 
      pagination.pageSize, 
      searchForm.status,
      searchForm.taskType
    )
    tableData.value = res.data.list
    pagination.total = res.data.total
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  pagination.pageNum = 1
  fetchData()
}

const handleReset = () => {
  searchForm.taskType = null
  searchForm.status = null
  handleSearch()
}

const handleCreate = () => {
  resetForm()
  formDialogVisible.value = true
}

const resetForm = () => {
  formData.id = null
  formData.taskName = ''
  formData.taskType = null
  formData.categoryIds = ''
  formData.locationIds = ''
  formData.startDate = ''
  formData.endDate = ''
  formData.remark = ''
}

const handleSubmitForm = async () => {
  try {
    const data = {
      taskName: formData.taskName,
      taskType: formData.taskType,
      categoryIds: formData.categoryIds,
      locationIds: formData.locationIds,
      startDate: formData.startDate || null,
      endDate: formData.endDate || null,
      remark: formData.remark
    }
    
    if (formData.id) {
      data.id = formData.id
      await updateTask(data)
      ElMessage.success('更新成功')
    } else {
      await createTask(data)
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
    const res = await getTaskById(row.id)
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
    const res = await getTaskById(row.id)
    if (res.code === 200) {
      const data = res.data
      formData.id = data.id
      formData.taskName = data.taskName
      formData.taskType = data.taskType
      formData.categoryIds = data.categoryIds
      formData.locationIds = data.locationIds
      formData.startDate = data.startDate
      formData.endDate = data.endDate
      formData.remark = data.remark
      formDialogVisible.value = true
    }
  } catch (error) {
    ElMessage.error('获取详情失败')
  }
}

const handleStart = async (row) => {
  try {
    await ElMessageBox.confirm(`确定要开始该盘点任务吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await startTask(row.id)
    ElMessage.success('任务已启动')
    fetchData()
  } catch (error) {
    if (error !== 'cancel') {
      console.error(error)
    }
  }
}

const handleComplete = async (row) => {
  try {
    await ElMessageBox.confirm(`确定要完成该盘点任务吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await completeTask(row.id)
    ElMessage.success('任务已完成')
    fetchData()
  } catch (error) {
    if (error !== 'cancel') {
      console.error(error)
    }
  }
}

const handleCancel = async (row) => {
  try {
    await ElMessageBox.confirm(`确定要终止该盘点任务吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await cancelTask(row.id)
    ElMessage.success('任务已终止')
    fetchData()
  } catch (error) {
    if (error !== 'cancel') {
      console.error(error)
    }
  }
}

const getTaskTypeText = (type) => {
  const texts = {
    1: '全盘',
    2: '抽盘',
    3: '循环盘点'
  }
  return texts[type] || '未知'
}

const getStatusType = (status) => {
  const types = {
    0: 'info',
    1: 'warning',
    2: 'success',
    3: 'danger'
  }
  return types[status] || 'info'
}

const getStatusText = (status) => {
  const texts = {
    0: '未开始',
    1: '进行中',
    2: '已完成',
    3: '已终止'
  }
  return texts[status] || '未知'
}

const formatDate = (date) => {
  if (!date) return ''
  return new Date(date).toLocaleDateString('zh-CN')
}

onMounted(() => {
  fetchData()
})
</script>

<style scoped>
.task-list {
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
