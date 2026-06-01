<template>
  <div class="change-list">
    <!-- 搜索栏 -->
    <el-card class="search-card">
      <el-form :inline="true" :model="queryForm" class="search-form">
        <el-form-item label="资产编码">
          <el-input v-model="queryForm.assetCode" placeholder="请输入资产编码" clearable />
        </el-form-item>
        <el-form-item label="资产名称">
          <el-input v-model="queryForm.assetName" placeholder="请输入资产名称" clearable />
        </el-form-item>
        <el-form-item label="变更类型">
          <el-select v-model="queryForm.changeType" placeholder="请选择变更类型" clearable>
            <el-option label="信息变更" :value="1" />
            <el-option label="部门调拨" :value="2" />
            <el-option label="位置变更" :value="3" />
            <el-option label="使用人变更" :value="4" />
            <el-option label="状态变更" :value="5" />
            <el-option label="其他" :value="6" />
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
          新增变更记录
        </el-button>
        <el-button type="warning" @click="handleExport">
          <el-icon><Download /></el-icon>
          导出记录
        </el-button>
      </div>
      
      <el-table 
        :data="tableData" 
        v-loading="loading" 
        border 
        stripe
      >
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="assetCode" label="资产编码" width="120" />
        <el-table-column prop="assetName" label="资产名称" width="150" />
        <el-table-column prop="changeType" label="变更类型" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="getChangeTypeTag(row.changeType)" size="small">
              {{ getChangeTypeText(row.changeType) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="beforeValue" label="变更前" width="200" show-overflow-tooltip />
        <el-table-column prop="afterValue" label="变更后" width="200" show-overflow-tooltip />
        <el-table-column prop="changeReason" label="变更原因" width="200" show-overflow-tooltip />
        <el-table-column prop="operatorName" label="操作人" width="100" />
        <el-table-column prop="operateTime" label="操作时间" width="180" />
        <el-table-column label="操作" fixed="right" width="150">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleView(row)">详情</el-button>
            <el-button link type="danger" @click="handleDelete(row)">删除</el-button>
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

    <!-- 新增变更记录弹窗 -->
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
            <el-form-item label="变更类型" prop="changeType">
              <el-select v-model="formData.changeType" placeholder="请选择变更类型" style="width: 100%">
                <el-option label="信息变更" :value="1" />
                <el-option label="部门调拨" :value="2" />
                <el-option label="位置变更" :value="3" />
                <el-option label="使用人变更" :value="4" />
                <el-option label="状态变更" :value="5" />
                <el-option label="其他" :value="6" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="变更前值" prop="beforeValue">
              <el-input v-model="formData.beforeValue" placeholder="请输入变更前的值" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="变更后值" prop="afterValue">
              <el-input v-model="formData.afterValue" placeholder="请输入变更后的值" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="变更字段" prop="changeField">
          <el-input v-model="formData.changeField" placeholder="请输入变更的字段名，如：departmentName、locationName 等" />
        </el-form-item>
        <el-form-item label="变更原因" prop="changeReason">
          <el-input 
            v-model="formData.changeReason" 
            type="textarea" 
            :rows="3" 
            placeholder="请详细说明变更原因" 
          />
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
        <el-button type="primary" @click="handleSubmit" :loading="submitting">确定</el-button>
      </template>
    </el-dialog>

    <!-- 详情弹窗 -->
    <el-dialog v-model="detailVisible" title="变更记录详情" width="700px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="ID">{{ detailData.id }}</el-descriptions-item>
        <el-descriptions-item label="资产编码">{{ detailData.assetCode }}</el-descriptions-item>
        <el-descriptions-item label="资产名称">{{ detailData.assetName }}</el-descriptions-item>
        <el-descriptions-item label="变更类型">
          <el-tag :type="getChangeTypeTag(detailData.changeType)" size="small">
            {{ getChangeTypeText(detailData.changeType) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="变更字段">{{ detailData.changeField || '-' }}</el-descriptions-item>
        <el-descriptions-item label="变更前">{{ detailData.beforeValue || '-' }}</el-descriptions-item>
        <el-descriptions-item label="变更后">{{ detailData.afterValue || '-' }}</el-descriptions-item>
        <el-descriptions-item label="变更原因" :span="2">{{ detailData.changeReason || '-' }}</el-descriptions-item>
        <el-descriptions-item label="操作人">{{ detailData.operatorName }}</el-descriptions-item>
        <el-descriptions-item label="操作时间">{{ detailData.operateTime }}</el-descriptions-item>
        <el-descriptions-item label="备注" :span="2">{{ detailData.remark || '-' }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Download } from '@element-plus/icons-vue'

// 模拟 API 调用（实际项目中应替换为真实 API）
const mockApi = {
  getList: (params) => {
    return new Promise((resolve) => {
      setTimeout(() => {
        resolve({
          code: 200,
          data: {
            records: [
              {
                id: 1,
                assetId: 1001,
                assetCode: 'ZC20240001',
                assetName: '联想 ThinkPad X1',
                changeType: 2,
                changeField: 'departmentName',
                beforeValue: '技术部',
                afterValue: '产品部',
                changeReason: '部门调整，资产划转',
                operatorName: '张三',
                operateTime: '2024-01-20 10:30:00',
                remark: '年度部门重组'
              },
              {
                id: 2,
                assetId: 1002,
                assetCode: 'ZC20240002',
                assetName: 'HP 打印机',
                changeType: 3,
                changeField: 'locationName',
                beforeValue: 'A 栋 301',
                afterValue: 'B 栋 205',
                changeReason: '办公室搬迁',
                operatorName: '李四',
                operateTime: '2024-02-15 14:20:00',
                remark: ''
              },
              {
                id: 3,
                assetId: 1001,
                assetCode: 'ZC20240001',
                assetName: '联想 ThinkPad X1',
                changeType: 4,
                changeField: 'userName',
                beforeValue: '王五',
                afterValue: '赵六',
                changeReason: '员工离职交接',
                operatorName: '张三',
                operateTime: '2024-03-01 09:15:00',
                remark: '工作交接完成'
              },
              {
                id: 4,
                assetId: 1003,
                assetCode: 'ZC20240003',
                assetName: '戴尔显示器',
                changeType: 1,
                changeField: 'assetName',
                beforeValue: '戴尔显示器 24 寸',
                afterValue: '戴尔 P2419H 显示器',
                changeReason: '完善资产名称信息',
                operatorName: '管理员',
                operateTime: '2024-03-10 11:00:00',
                remark: '资产信息规范化'
              }
            ],
            total: 4
          },
          message: 'success'
        })
      }, 300)
    })
  },
  create: (data) => {
    return new Promise((resolve) => {
      setTimeout(() => {
        resolve({ code: 200, message: '创建成功' })
      }, 500)
    })
  },
  delete: (id) => {
    return new Promise((resolve) => {
      setTimeout(() => {
        resolve({ code: 200, message: '删除成功' })
      }, 300)
    })
  }
}

// 查询表单
const queryForm = reactive({
  assetCode: '',
  assetName: '',
  changeType: null
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
const dialogTitle = ref('新增变更记录')
const submitting = ref(false)

// 表单引用
const formRef = ref(null)

// 当前操作的记录
const currentRow = ref(null)

// 详情数据
const detailData = ref({})

// 获取变更类型标签
const getChangeTypeTag = (type) => {
  const tagMap = {
    1: 'primary',
    2: 'success',
    3: 'warning',
    4: 'info',
    5: 'danger',
    6: 'info'
  }
  return tagMap[type] || ''
}

// 获取变更类型文本
const getChangeTypeText = (type) => {
  const textMap = {
    1: '信息变更',
    2: '部门调拨',
    3: '位置变更',
    4: '使用人变更',
    5: '状态变更',
    6: '其他'
  }
  return textMap[type] || '未知'
}

// 加载数据
const fetchData = async () => {
  loading.value = true
  try {
    const params = {
      ...queryForm,
      pageNum: pagination.pageNum,
      pageSize: pagination.pageSize
    }
    const res = await mockApi.getList(params)
    if (res.code === 200) {
      tableData.value = res.data.records
      pagination.total = res.data.total
    }
  } catch (error) {
    ElMessage.error('加载数据失败')
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
  queryForm.changeType = null
  handleSearch()
}

// 新增
const handleCreate = () => {
  dialogVisible.value = true
  dialogTitle.value = '新增变更记录'
  resetForm()
}

// 表单重置
const resetForm = () => {
  formData.assetId = null
  formData.assetCode = ''
  formData.assetName = ''
  formData.changeType = null
  formData.changeField = ''
  formData.beforeValue = ''
  formData.afterValue = ''
  formData.changeReason = ''
  formData.remark = ''
  if (formRef.value) {
    formRef.value.clearValidate()
  }
}

// 表单数据
const formData = reactive({
  assetId: null,
  assetCode: '',
  assetName: '',
  changeType: null,
  changeField: '',
  beforeValue: '',
  afterValue: '',
  changeReason: '',
  remark: ''
})

// 表单验证规则
const formRules = {
  assetId: [{ required: true, message: '请输入资产 ID', trigger: 'blur' }],
  assetCode: [{ required: true, message: '请输入资产编码', trigger: 'blur' }],
  assetName: [{ required: true, message: '请输入资产名称', trigger: 'blur' }],
  changeType: [{ required: true, message: '请选择变更类型', trigger: 'change' }],
  beforeValue: [{ required: true, message: '请输入变更前值', trigger: 'blur' }],
  afterValue: [{ required: true, message: '请输入变更后值', trigger: 'blur' }],
  changeReason: [{ required: true, message: '请输入变更原因', trigger: 'blur' }]
}

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate(async (valid) => {
    if (!valid) return
    
    submitting.value = true
    try {
      await mockApi.create(formData)
      ElMessage.success('创建成功')
      dialogVisible.value = false
      fetchData()
    } catch (error) {
      ElMessage.error('创建失败')
    } finally {
      submitting.value = false
    }
  })
}

// 弹窗关闭
const handleDialogClose = () => {
  resetForm()
}

// 查看详情
const handleView = (row) => {
  detailData.value = { ...row }
  detailVisible.value = true
}

// 删除
const handleDelete = (row) => {
  ElMessageBox.confirm('确定要删除该变更记录吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await mockApi.delete(row.id)
      ElMessage.success('删除成功')
      fetchData()
    } catch (error) {
      ElMessage.error('删除失败')
    }
  }).catch(() => {})
}

// 导出
const handleExport = () => {
  ElMessage.info('正在导出变更记录...')
  // 实际项目中应实现真实的导出逻辑
  // 可以导出为 Excel 或 CSV 格式
  setTimeout(() => {
    ElMessage.success('导出成功')
  }, 1000)
}

// 初始化
onMounted(() => {
  fetchData()
})
</script>

<style scoped>
.change-list {
  padding: 20px;
}

.search-card {
  margin-bottom: 20px;
}

.table-card {
  min-height: 500px;
}

.toolbar {
  margin-bottom: 15px;
  display: flex;
  gap: 10px;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>
