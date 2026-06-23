<template>
  <div class="plan-list">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>盘点计划</span>
          <el-button v-if="userStore.hasPermission('inventory:plan:create')" type="primary" @click="handleCreate">
            <el-icon><Plus /></el-icon>
            新建计划
          </el-button>
        </div>
      </template>

      <!-- 搜索栏 -->
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="计划名称">
          <el-input v-model="searchForm.planName" placeholder="请输入计划名称" clearable />
        </el-form-item>
        <el-form-item label="计划类型">
          <el-select v-model="searchForm.planType" placeholder="全部类型" clearable>
            <el-option label="定期盘点" :value="1" />
            <el-option label="临时盘点" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="全部状态" clearable>
            <el-option label="启用" :value="1" />
            <el-option label="停用" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <!-- 表格 -->
      <el-table :data="tableData" v-loading="loading" border stripe>
        <el-table-column prop="planNo" label="计划编号" width="150" />
        <el-table-column prop="planName" label="计划名称" min-width="150" />
        <el-table-column prop="planType" label="计划类型" width="100" align="center">
          <template #default="{ row }">
            {{ getPlanTypeText(row.planType) }}
          </template>
        </el-table-column>
        <el-table-column prop="frequency" label="盘点频率" width="100" align="center">
          <template #default="{ row }">
            {{ getFrequencyText(row.frequency) }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="80" align="center">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="nextPlanDate" label="下次盘点日期" width="120" />
        <el-table-column prop="createUserId" label="创建人" width="100" />
        <el-table-column prop="createTime" label="创建时间" width="160" />
        <el-table-column label="操作" width="280" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleView(row)">详情</el-button>
            <el-button v-if="userStore.hasPermission('inventory:plan:edit')" link type="primary" @click="handleEdit(row)">编辑</el-button>
            <el-button 
              v-if="row.status === 1 && userStore.hasPermission('inventory:plan:execute')"
              link 
              type="warning" 
              @click="handleExecute(row)"
            >
              执行
            </el-button>
            <el-button 
              v-if="row.status === 1 && userStore.hasPermission('inventory:plan:disable')"
              link 
              type="info" 
              @click="handleDisable(row)"
            >
              停用
            </el-button>
            <el-button 
              v-if="row.status === 0 && userStore.hasPermission('inventory:plan:enable')"
              link 
              type="success" 
              @click="handleEnable(row)"
            >
              启用
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
        <el-form-item label="计划名称" prop="planName">
          <el-input v-model="formData.planName" placeholder="请输入计划名称" />
        </el-form-item>
        <el-form-item label="计划类型" prop="planType">
          <el-select v-model="formData.planType" placeholder="请选择计划类型" style="width: 100%">
            <el-option label="定期盘点" :value="1" />
            <el-option label="临时盘点" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item label="盘点频率" prop="frequency" v-if="formData.planType === 1">
          <el-select v-model="formData.frequency" placeholder="请选择盘点频率" style="width: 100%">
            <el-option label="每月" :value="1" />
            <el-option label="每季度" :value="2" />
            <el-option label="每半年" :value="3" />
            <el-option label="每年" :value="4" />
          </el-select>
        </el-form-item>
        <el-form-item label="下次盘点日期" prop="nextPlanDate" v-if="formData.planType === 1">
          <el-date-picker
            v-model="formData.nextPlanDate"
            type="date"
            placeholder="选择日期"
            style="width: 100%"
            value-format="YYYY-MM-DD"
          />
        </el-form-item>
        <el-form-item label="盘点范围" prop="scope">
          <el-select v-model="formData.scope" placeholder="请选择盘点范围" style="width: 100%">
            <el-option label="全部资产" :value="1" />
            <el-option label="指定分类" :value="2" />
            <el-option label="指定部门" :value="3" />
            <el-option label="指定位置" :value="4" />
          </el-select>
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
        <el-button type="primary" @click="handleSubmit" :loading="submitLoading">
          确定
        </el-button>
      </template>
    </el-dialog>

    <!-- 详情对话框 -->
    <el-dialog
      v-model="detailVisible"
      title="盘点计划详情"
      width="600px"
    >
      <el-descriptions :column="1" border>
        <el-descriptions-item label="计划编号">{{ detailData.planNo }}</el-descriptions-item>
        <el-descriptions-item label="计划名称">{{ detailData.planName }}</el-descriptions-item>
        <el-descriptions-item label="计划类型">
          {{ getPlanTypeText(detailData.planType) }}
        </el-descriptions-item>
        <el-descriptions-item label="盘点频率" v-if="detailData.planType === 1">
          {{ getFrequencyText(detailData.frequency) }}
        </el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="getStatusType(detailData.status)">
            {{ getStatusText(detailData.status) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="下次盘点日期" v-if="detailData.nextPlanDate">
          {{ detailData.nextPlanDate }}
        </el-descriptions-item>
        <el-descriptions-item label="盘点范围">
          {{ getScopeText(detailData.scope) }}
        </el-descriptions-item>
        <el-descriptions-item label="备注" v-if="detailData.remark">
          {{ detailData.remark }}
        </el-descriptions-item>
        <el-descriptions-item label="创建人">{{ detailData.createUserId }}</el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ detailData.createTime }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { useUserStore } from '@/store/user'
import { getPlanList, getPlanById, createPlan, updatePlan, deletePlan, executePlan, enablePlan, disablePlan } from '@/api/inventory'

const userStore = useUserStore()
const loading = ref(false)
const submitLoading = ref(false)
const tableData = ref([])
const dialogVisible = ref(false)
const detailVisible = ref(false)
const dialogTitle = ref('新建计划')
const isEdit = ref(false)

const searchForm = reactive({
  planName: '',
  planType: null,
  status: null
})

const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

const formData = reactive({
  id: null,
  planNo: null,
  planName: '',
  planType: null,
  frequency: null,
  nextPlanDate: '',
  scope: 1,
  remark: ''
})

const formRules = {
  planName: [
    { required: true, message: '请输入计划名称', trigger: 'blur' }
  ],
  planType: [
    { required: true, message: '请选择计划类型', trigger: 'change' }
  ],
  frequency: [
    { required: true, message: '请选择盘点频率', trigger: 'change' }
  ],
  nextPlanDate: [
    { required: true, message: '请选择下次盘点日期', trigger: 'change' }
  ]
}

const detailData = ref({})

const fetchData = async () => {
  loading.value = true
  try {
    const res = await getPlanList(
      pagination.pageNum, 
      pagination.pageSize, 
      searchForm.planName,
      searchForm.planType,
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
  searchForm.planName = ''
  searchForm.planType = null
  searchForm.status = null
  handleSearch()
}

const handleCreate = () => {
  isEdit.value = false
  dialogTitle.value = '新建计划'
  resetForm()
  dialogVisible.value = true
}

const handleEdit = async (row) => {
  isEdit.value = true
  dialogTitle.value = '编辑计划'
  try {
    const res = await getPlanById(row.id)
    Object.assign(formData, res.data)
    dialogVisible.value = true
  } catch (error) {
    console.error(error)
    ElMessage.error('获取详情失败')
  }
}

const handleView = async (row) => {
  try {
    const res = await getPlanById(row.id)
    detailData.value = res.data
    detailVisible.value = true
  } catch (error) {
    console.error(error)
    ElMessage.error('获取详情失败')
  }
}

const handleSubmit = async () => {
  try {
    await formRef.value.validate()
    submitLoading.value = true
    if (isEdit.value) {
      await updatePlan(formData)
      ElMessage.success('更新成功')
    } else {
      await createPlan(formData)
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
    planNo: null,
    planName: '',
    planType: null,
    frequency: null,
    nextPlanDate: '',
    scope: 1,
    remark: ''
  })
}

const handleExecute = async (row) => {
  try {
    await ElMessageBox.confirm(`确定要执行该盘点计划吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await executePlan(row.id)
    ElMessage.success('计划已执行')
    fetchData()
  } catch (error) {
    if (error !== 'cancel') {
      console.error(error)
    }
  }
}

const handleEnable = async (row) => {
  try {
    await ElMessageBox.confirm(`确定要启用该盘点计划吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await enablePlan(row.id)
    ElMessage.success('计划已启用')
    fetchData()
  } catch (error) {
    if (error !== 'cancel') {
      console.error(error)
    }
  }
}

const handleDisable = async (row) => {
  try {
    await ElMessageBox.confirm(`确定要停用该盘点计划吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await disablePlan(row.id)
    ElMessage.success('计划已停用')
    fetchData()
  } catch (error) {
    if (error !== 'cancel') {
      console.error(error)
    }
  }
}

const getPlanTypeText = (type) => {
  const texts = {
    1: '定期盘点',
    2: '临时盘点'
  }
  return texts[type] || '未知'
}

const getFrequencyText = (freq) => {
  const texts = {
    1: '每月',
    2: '每季度',
    3: '每半年',
    4: '每年'
  }
  return texts[freq] || '未知'
}

const getScopeText = (scope) => {
  const texts = {
    1: '全部资产',
    2: '指定分类',
    3: '指定部门',
    4: '指定位置'
  }
  return texts[scope] || '未知'
}

const getStatusType = (status) => {
  const types = {
    0: 'info',
    1: 'success'
  }
  return types[status] || 'info'
}

const getStatusText = (status) => {
  const texts = {
    0: '停用',
    1: '启用'
  }
  return texts[status] || '未知'
}

const formRef = ref(null)

onMounted(() => {
  fetchData()
})
</script>

<style scoped>
.plan-list {
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
