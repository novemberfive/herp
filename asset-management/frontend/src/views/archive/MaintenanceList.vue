<template>
  <div class="maintenance-list">
    <!-- 搜索栏 -->
    <el-card class="search-card">
      <el-form :inline="true" :model="queryForm" class="search-form">
        <el-form-item label="资产编码">
          <el-input v-model="queryForm.assetCode" placeholder="请输入资产编码" clearable />
        </el-form-item>
        <el-form-item label="资产名称">
          <el-input v-model="queryForm.assetName" placeholder="请输入资产名称" clearable />
        </el-form-item>
        <el-form-item label="维修结果">
          <el-select v-model="queryForm.maintenanceResult" placeholder="请选择维修结果" clearable>
            <el-option label="待维修" :value="0" />
            <el-option label="维修中" :value="1" />
            <el-option label="维修完成" :value="2" />
            <el-option label="无法修复" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item label="审批状态">
          <el-select v-model="queryForm.approveStatus" placeholder="请选择审批状态" clearable>
            <el-option label="待审批" :value="0" />
            <el-option label="已通过" :value="1" />
            <el-option label="已拒绝" :value="2" />
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
        <el-button v-if="userStore.hasPermission('archive:maintenance:create')" type="primary" @click="handleCreate">
          <el-icon><Plus /></el-icon>
          新增维保申请
        </el-button>
      </div>
      
      <el-table 
        :data="tableData" 
        v-loading="loading" 
        border 
        stripe
      >
        <el-table-column prop="maintenanceNo" label="维修单号" width="160" />
        <el-table-column prop="assetCode" label="资产编码" width="120" />
        <el-table-column prop="assetName" label="资产名称" width="150" />
        <el-table-column prop="departmentName" label="使用部门" width="120" />
        <el-table-column prop="maintenanceType" label="维修类型" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="getMaintenanceTypeTag(row.maintenanceType)" size="small">
              {{ getMaintenanceTypeText(row.maintenanceType) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="faultType" label="故障类型" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="getFaultTypeTag(row.faultType)" size="small">
              {{ getFaultTypeText(row.faultType) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="maintenanceResult" label="维修结果" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="getResultTypeTag(row.maintenanceResult)" size="small">
              {{ getResultTypeText(row.maintenanceResult) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="approveStatus" label="审批状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="getApproveTypeTag(row.approveStatus)" size="small">
              {{ getApproveTypeText(row.approveStatus) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="maintenanceCost" label="维修费用" width="100" align="right">
          <template #default="{ row }">
            <span v-if="row.maintenanceCost">¥{{ row.maintenanceCost }}</span>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column prop="applyTime" label="申请时间" width="180" />
        <el-table-column label="操作" fixed="right" width="320">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleView(row)">详情</el-button>
            <el-button link type="primary" @click="handleEdit(row)" v-if="row.approveStatus === 0 && userStore.hasPermission('archive:maintenance:edit')">编辑</el-button>
            <el-button link type="success" @click="handleApprove(row)" v-if="row.approveStatus === 0 && userStore.hasPermission('archive:maintenance:approve')">审批</el-button>
            <el-button link type="warning" @click="handleStart(row)" v-if="row.approveStatus === 1 && row.maintenanceResult === 0 && userStore.hasPermission('archive:maintenance:start')">开始维修</el-button>
            <el-button link type="success" @click="handleComplete(row)" v-if="row.maintenanceResult === 1 && userStore.hasPermission('archive:maintenance:complete')">完成维修</el-button>
            <el-button link type="danger" @click="handleDelete(row)" v-if="userStore.hasPermission('archive:maintenance:delete')">删除</el-button>
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

    <!-- 新增/编辑弹窗 -->
    <el-dialog 
      v-model="dialogVisible" 
      :title="dialogTitle" 
      width="700px"
      @close="handleDialogClose"
    >
      <el-form 
        ref="formRef"
        :model="formData" 
        :rules="formRules" 
        label-width="120px"
      >
        <el-row>
          <el-col :span="12">
            <el-form-item label="资产 ID" prop="assetId">
              <el-input-number v-model="formData.assetId" :min="1" placeholder="请输入资产 ID" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="资产编码" prop="assetCode">
              <el-input v-model="formData.assetCode" placeholder="请输入资产编码" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="资产名称" prop="assetName">
              <el-input v-model="formData.assetName" placeholder="请输入资产名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="分类 ID" prop="categoryId">
              <el-input-number v-model="formData.categoryId" :min="1" placeholder="请输入分类 ID" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="部门 ID" prop="departmentId">
              <el-input-number v-model="formData.departmentId" :min="1" placeholder="请输入部门 ID" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="位置 ID" prop="locationId">
              <el-input-number v-model="formData.locationId" :min="1" placeholder="请输入位置 ID" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="维修类型" prop="maintenanceType">
              <el-select v-model="formData.maintenanceType" placeholder="请选择维修类型" style="width: 100%">
                <el-option label="日常维修" :value="1" />
                <el-option label="大修" :value="2" />
                <el-option label="保养" :value="3" />
                <el-option label="巡检" :value="4" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="故障类型" prop="faultType">
              <el-select v-model="formData.faultType" placeholder="请选择故障类型" style="width: 100%">
                <el-option label="其他" :value="0" />
                <el-option label="硬件故障" :value="1" />
                <el-option label="软件故障" :value="2" />
                <el-option label="人为损坏" :value="3" />
                <el-option label="自然老化" :value="4" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="故障描述" prop="faultDescription">
          <el-input 
            v-model="formData.faultDescription" 
            type="textarea" 
            :rows="3" 
            placeholder="请详细描述故障情况" 
          />
        </el-form-item>
        <el-form-item label="申请人姓名" prop="applicantName">
          <el-input v-model="formData.applicantName" placeholder="请输入申请人姓名" />
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input 
            v-model="formData.remark" 
            type="textarea" 
            :rows="2" 
            placeholder="请输入备注信息" 
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button
          v-if="!formData.id ? userStore.hasPermission('archive:maintenance:create') : userStore.hasPermission('archive:maintenance:edit')"
          type="primary"
          @click="handleSubmit"
          :loading="submitting"
        >
          确定
        </el-button>
      </template>
    </el-dialog>

    <!-- 详情弹窗 -->
    <el-dialog v-model="detailVisible" title="维保详情" width="800px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="维修单号">{{ detailData.maintenanceNo }}</el-descriptions-item>
        <el-descriptions-item label="资产编码">{{ detailData.assetCode }}</el-descriptions-item>
        <el-descriptions-item label="资产名称">{{ detailData.assetName }}</el-descriptions-item>
        <el-descriptions-item label="使用部门">{{ detailData.departmentName }}</el-descriptions-item>
        <el-descriptions-item label="存放位置">{{ detailData.locationName }}</el-descriptions-item>
        <el-descriptions-item label="维修类型">
          <el-tag :type="getMaintenanceTypeTag(detailData.maintenanceType)" size="small">
            {{ getMaintenanceTypeText(detailData.maintenanceType) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="故障类型">
          <el-tag :type="getFaultTypeTag(detailData.faultType)" size="small">
            {{ getFaultTypeText(detailData.faultType) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="维修结果">
          <el-tag :type="getResultTypeTag(detailData.maintenanceResult)" size="small">
            {{ getResultTypeText(detailData.maintenanceResult) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="审批状态">
          <el-tag :type="getApproveTypeTag(detailData.approveStatus)" size="small">
            {{ getApproveTypeText(detailData.approveStatus) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="故障描述" :span="2">{{ detailData.faultDescription || '-' }}</el-descriptions-item>
        <el-descriptions-item label="维修方式">{{ detailData.maintenanceMethod || '-' }}</el-descriptions-item>
        <el-descriptions-item label="维修费用">
          <span v-if="detailData.maintenanceCost">¥{{ detailData.maintenanceCost }}</span>
          <span v-else>-</span>
        </el-descriptions-item>
        <el-descriptions-item label="维修人">{{ detailData.maintainerName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="申请人">{{ detailData.applicantName }}</el-descriptions-item>
        <el-descriptions-item label="申请时间">{{ detailData.applyTime }}</el-descriptions-item>
        <el-descriptions-item label="审批人">{{ detailData.approverName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="审批时间">{{ detailData.approveTime || '-' }}</el-descriptions-item>
        <el-descriptions-item label="审批意见" :span="2">{{ detailData.approveRemark || '-' }}</el-descriptions-item>
        <el-descriptions-item label="备注" :span="2">{{ detailData.remark || '-' }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>

    <!-- 审批弹窗 -->
    <el-dialog 
      v-model="approveDialogVisible" 
      title="审批维保申请" 
      width="500px"
      @close="handleApproveDialogClose"
    >
      <el-form 
        ref="approveFormRef"
        :model="approveFormData" 
        :rules="approveFormRules" 
        label-width="100px"
      >
        <el-form-item label="审批结果" prop="approveStatus">
          <el-radio-group v-model="approveFormData.approveStatus">
            <el-radio :label="1">通过</el-radio>
            <el-radio :label="2">拒绝</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="审批意见" prop="approveRemark">
          <el-input 
            v-model="approveFormData.approveRemark" 
            type="textarea" 
            :rows="3" 
            placeholder="请输入审批意见" 
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="approveDialogVisible = false">取消</el-button>
        <el-button v-if="userStore.hasPermission('archive:maintenance:approve')" type="primary" @click="handleApproveSubmit" :loading="submitting">确定</el-button>
      </template>
    </el-dialog>

    <!-- 开始维修弹窗 -->
    <el-dialog 
      v-model="startDialogVisible" 
      title="开始维修" 
      width="500px"
      @close="handleStartDialogClose"
    >
      <el-form 
        ref="startFormRef"
        :model="startFormData" 
        :rules="startFormRules" 
        label-width="100px"
      >
        <el-form-item label="维修人 ID" prop="maintainerId">
          <el-input-number v-model="startFormData.maintainerId" :min="1" placeholder="请输入维修人 ID" style="width: 100%" />
        </el-form-item>
        <el-form-item label="维修人姓名" prop="maintainerName">
          <el-input v-model="startFormData.maintainerName" placeholder="请输入维修人姓名" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="startDialogVisible = false">取消</el-button>
        <el-button v-if="userStore.hasPermission('archive:maintenance:start')" type="primary" @click="handleStartSubmit" :loading="submitting">确定</el-button>
      </template>
    </el-dialog>

    <!-- 完成维修弹窗 -->
    <el-dialog 
      v-model="completeDialogVisible" 
      title="完成维修" 
      width="600px"
      @close="handleCompleteDialogClose"
    >
      <el-form 
        ref="completeFormRef"
        :model="completeFormData" 
        :rules="completeFormRules" 
        label-width="120px"
      >
        <el-form-item label="维修结果" prop="maintenanceResult">
          <el-radio-group v-model="completeFormData.maintenanceResult">
            <el-radio :label="2">维修完成</el-radio>
            <el-radio :label="3">无法修复</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="维修方式" prop="maintenanceMethod">
          <el-input 
            v-model="completeFormData.maintenanceMethod" 
            type="textarea" 
            :rows="3" 
            placeholder="请描述维修方式和处理过程" 
          />
        </el-form-item>
        <el-form-item label="维修费用" prop="maintenanceCost">
          <el-input-number 
            v-model="completeFormData.maintenanceCost" 
            :min="0" 
            :precision="2" 
            :step="0.01"
            placeholder="请输入维修费用"
            style="width: 100%"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="completeDialogVisible = false">取消</el-button>
        <el-button v-if="userStore.hasPermission('archive:maintenance:complete')" type="primary" @click="handleCompleteSubmit" :loading="submitting">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { useUserStore } from '@/store/user'
import { 
  getMaintenanceList, 
  getMaintenanceById,
  createMaintenance, 
  updateMaintenance, 
  deleteMaintenance,
  approveMaintenance,
  startMaintenance,
  completeMaintenance,
  cancelMaintenance
} from '@/api/basic'

const userStore = useUserStore()
// 查询表单
const queryForm = reactive({
  assetCode: '',
  assetName: '',
  maintenanceResult: null,
  approveStatus: null
})

// 表格数据
const tableData = ref([])
const loading = ref(false)

// 分页
const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

// 弹窗控制
const dialogVisible = ref(false)
const detailVisible = ref(false)
const approveDialogVisible = ref(false)
const startDialogVisible = ref(false)
const completeDialogVisible = ref(false)
const dialogTitle = ref('')
const submitting = ref(false)

// 表单引用
const formRef = ref(null)
const approveFormRef = ref(null)
const startFormRef = ref(null)
const completeFormRef = ref(null)

// 当前操作的记录 ID
const currentId = ref(null)

// 表单数据
const formData = reactive({
  id: null,
  assetId: null,
  assetCode: '',
  assetName: '',
  categoryId: null,
  categoryName: '',
  departmentId: null,
  departmentName: '',
  locationId: null,
  locationName: '',
  maintenanceType: 1,
  faultType: 0,
  faultDescription: '',
  applicantName: '',
  remark: ''
})

// 表单验证规则
const formRules = {
  assetId: [
    { required: true, message: '请输入资产 ID', trigger: 'blur' }
  ],
  assetCode: [
    { required: true, message: '请输入资产编码', trigger: 'blur' }
  ],
  assetName: [
    { required: true, message: '请输入资产名称', trigger: 'blur' }
  ],
  maintenanceType: [
    { required: true, message: '请选择维修类型', trigger: 'change' }
  ]
}

// 审批表单数据
const approveFormData = reactive({
  approveStatus: 1,
  approveRemark: ''
})

// 审批表单验证规则
const approveFormRules = {
  approveStatus: [
    { required: true, message: '请选择审批结果', trigger: 'change' }
  ]
}

// 开始维修表单数据
const startFormData = reactive({
  maintainerId: null,
  maintainerName: ''
})

// 开始维修表单验证规则
const startFormRules = {
  maintainerId: [
    { required: true, message: '请输入维修人 ID', trigger: 'blur' }
  ],
  maintainerName: [
    { required: true, message: '请输入维修人姓名', trigger: 'blur' }
  ]
}

// 完成维修表单数据
const completeFormData = reactive({
  maintenanceResult: 2,
  maintenanceMethod: '',
  maintenanceCost: null
})

// 完成维修表单验证规则
const completeFormRules = {
  maintenanceResult: [
    { required: true, message: '请选择维修结果', trigger: 'change' }
  ],
  maintenanceMethod: [
    { required: true, message: '请输入维修方式', trigger: 'blur' }
  ]
}

// 详情数据
const detailData = ref({})

// 获取维保记录列表
const fetchData = async () => {
  loading.value = true
  try {
    const params = {
      pageNum: pagination.pageNum,
      pageSize: pagination.pageSize
    }
    if (queryForm.maintenanceResult !== null && queryForm.maintenanceResult !== '') {
      params.maintenanceResult = queryForm.maintenanceResult
    }
    if (queryForm.approveStatus !== null && queryForm.approveStatus !== '') {
      params.approveStatus = queryForm.approveStatus
    }
    
    const res = await getMaintenanceList(params)
    if (res.code === 200) {
      tableData.value = res.data.records || []
      pagination.total = res.data.total || 0
    } else {
      ElMessage.error(res.message || '获取维保记录列表失败')
    }
  } catch (error) {
    ElMessage.error('获取维保记录列表失败')
    console.error(error)
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = () => {
  pagination.pageNum = 1
  fetchData()
}

// 重置
const handleReset = () => {
  queryForm.assetCode = ''
  queryForm.assetName = ''
  queryForm.maintenanceResult = null
  queryForm.approveStatus = null
  pagination.pageNum = 1
  fetchData()
}

// 新增
const handleCreate = () => {
  dialogTitle.value = '新增维保申请'
  dialogVisible.value = true
  resetFormData()
}

// 编辑
const handleEdit = async (row) => {
  dialogTitle.value = '编辑维保申请'
  dialogVisible.value = true
  
  try {
    const res = await getMaintenanceById(row.id)
    if (res.code === 200) {
      const data = res.data
      Object.assign(formData, {
        id: data.id,
        assetId: data.assetId,
        assetCode: data.assetCode,
        assetName: data.assetName,
        categoryId: data.categoryId,
        categoryName: data.categoryName,
        departmentId: data.departmentId,
        departmentName: data.departmentName,
        locationId: data.locationId,
        locationName: data.locationName,
        maintenanceType: data.maintenanceType,
        faultType: data.faultType,
        faultDescription: data.faultDescription,
        applicantName: data.applicantName,
        remark: data.remark
      })
    } else {
      ElMessage.error(res.message || '获取维保详情失败')
    }
  } catch (error) {
    ElMessage.error('获取维保详情失败')
    console.error(error)
  }
}

// 查看详情
const handleView = async (row) => {
  try {
    const res = await getMaintenanceById(row.id)
    if (res.code === 200) {
      detailData.value = res.data
      detailVisible.value = true
    } else {
      ElMessage.error(res.message || '获取维保详情失败')
    }
  } catch (error) {
    ElMessage.error('获取维保详情失败')
    console.error(error)
  }
}

// 删除
const handleDelete = (row) => {
  ElMessageBox.confirm(
    `确定要删除维修记录"${row.maintenanceNo}"吗？`,
    '提示',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    try {
      const res = await deleteMaintenance(row.id)
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

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate(async (valid) => {
    if (!valid) return
    
    submitting.value = true
    try {
      const data = {
        assetId: formData.assetId,
        assetCode: formData.assetCode,
        assetName: formData.assetName,
        categoryId: formData.categoryId || 0,
        categoryName: formData.categoryName || '',
        departmentId: formData.departmentId || 0,
        departmentName: formData.departmentName || '',
        locationId: formData.locationId || 0,
        locationName: formData.locationName || '',
        maintenanceType: formData.maintenanceType,
        faultType: formData.faultType,
        faultDescription: formData.faultDescription,
        applicantName: formData.applicantName,
        remark: formData.remark || ''
      }
      
      let res
      if (formData.id) {
        data.id = formData.id
        res = await updateMaintenance(data)
      } else {
        res = await createMaintenance(data)
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

// 关闭弹窗
const handleDialogClose = () => {
  if (formRef.value) {
    formRef.value.resetFields()
  }
  resetFormData()
}

// 重置表单数据
const resetFormData = () => {
  Object.assign(formData, {
    id: null,
    assetId: null,
    assetCode: '',
    assetName: '',
    categoryId: null,
    categoryName: '',
    departmentId: null,
    departmentName: '',
    locationId: null,
    locationName: '',
    maintenanceType: 1,
    faultType: 0,
    faultDescription: '',
    applicantName: '',
    remark: ''
  })
}

// 审批
const handleApprove = (row) => {
  currentId.value = row.id
  approveDialogVisible.value = true
  approveFormData.approveStatus = 1
  approveFormData.approveRemark = ''
}

// 关闭审批弹窗
const handleApproveDialogClose = () => {
  if (approveFormRef.value) {
    approveFormRef.value.resetFields()
  }
}

// 提交审批
const handleApproveSubmit = async () => {
  if (!approveFormRef.value) return
  
  await approveFormRef.value.validate(async (valid) => {
    if (!valid) return
    
    submitting.value = true
    try {
      // 假设当前登录用户 ID 为 1，实际应从用户信息中获取
      const approverId = 1
      const res = await approveMaintenance(
        currentId.value, 
        approveFormData.approveStatus, 
        approveFormData.approveRemark, 
        approverId
      )
      if (res.code === 200) {
        ElMessage.success('审批成功')
        approveDialogVisible.value = false
        fetchData()
      } else {
        ElMessage.error(res.message || '审批失败')
      }
    } catch (error) {
      ElMessage.error('审批失败')
      console.error(error)
    } finally {
      submitting.value = false
    }
  })
}

// 开始维修
const handleStart = (row) => {
  currentId.value = row.id
  startDialogVisible.value = true
  startFormData.maintainerId = null
  startFormData.maintainerName = ''
}

// 关闭开始维修弹窗
const handleStartDialogClose = () => {
  if (startFormRef.value) {
    startFormRef.value.resetFields()
  }
}

// 提交开始维修
const handleStartSubmit = async () => {
  if (!startFormRef.value) return
  
  await startFormRef.value.validate(async (valid) => {
    if (!valid) return
    
    submitting.value = true
    try {
      const res = await startMaintenance(
        currentId.value, 
        startFormData.maintainerId, 
        startFormData.maintainerName
      )
      if (res.code === 200) {
        ElMessage.success('已开始维修')
        startDialogVisible.value = false
        fetchData()
      } else {
        ElMessage.error(res.message || '操作失败')
      }
    } catch (error) {
      ElMessage.error('操作失败')
      console.error(error)
    } finally {
      submitting.value = false
    }
  })
}

// 完成维修
const handleComplete = (row) => {
  currentId.value = row.id
  completeDialogVisible.value = true
  completeFormData.maintenanceResult = 2
  completeFormData.maintenanceMethod = ''
  completeFormData.maintenanceCost = null
}

// 关闭完成维修弹窗
const handleCompleteDialogClose = () => {
  if (completeFormRef.value) {
    completeFormRef.value.resetFields()
  }
}

// 提交完成维修
const handleCompleteSubmit = async () => {
  if (!completeFormRef.value) return
  
  await completeFormRef.value.validate(async (valid) => {
    if (!valid) return
    
    submitting.value = true
    try {
      const res = await completeMaintenance(
        currentId.value, 
        completeFormData.maintenanceResult, 
        completeFormData.maintenanceMethod, 
        completeFormData.maintenanceCost || 0
      )
      if (res.code === 200) {
        ElMessage.success('维修已完成')
        completeDialogVisible.value = false
        fetchData()
      } else {
        ElMessage.error(res.message || '操作失败')
      }
    } catch (error) {
      ElMessage.error('操作失败')
      console.error(error)
    } finally {
      submitting.value = false
    }
  })
}

// 获取维修类型文本
const getMaintenanceTypeText = (type) => {
  const texts = { 1: '日常维修', 2: '大修', 3: '保养', 4: '巡检' }
  return texts[type] || '未知'
}

// 获取维修类型标签颜色
const getMaintenanceTypeTag = (type) => {
  const types = { 1: 'info', 2: 'danger', 3: 'success', 4: 'warning' }
  return types[type] || 'info'
}

// 获取故障类型文本
const getFaultTypeText = (type) => {
  const texts = { 0: '其他', 1: '硬件故障', 2: '软件故障', 3: '人为损坏', 4: '自然老化' }
  return texts[type] || '未知'
}

// 获取故障类型标签颜色
const getFaultTypeTag = (type) => {
  const types = { 0: 'info', 1: 'danger', 2: 'warning', 3: 'danger', 4: 'success' }
  return types[type] || 'info'
}

// 获取维修结果文本
const getResultTypeText = (result) => {
  const texts = { 0: '待维修', 1: '维修中', 2: '维修完成', 3: '无法修复' }
  return texts[result] || '未知'
}

// 获取维修结果标签颜色
const getResultTypeTag = (result) => {
  const types = { 0: 'info', 1: 'warning', 2: 'success', 3: 'danger' }
  return types[result] || 'info'
}

// 获取审批状态文本
const getApproveTypeText = (status) => {
  const texts = { 0: '待审批', 1: '已通过', 2: '已拒绝' }
  return texts[status] || '未知'
}

// 获取审批状态标签颜色
const getApproveTypeTag = (status) => {
  const types = { 0: 'warning', 1: 'success', 2: 'danger' }
  return types[status] || 'info'
}

onMounted(() => {
  fetchData()
})
</script>

<style scoped>
.maintenance-list {
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
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.pagination {
  display: flex;
  justify-content: flex-end;
  margin-top: 16px;
}
</style>
