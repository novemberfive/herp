<template>
  <div class="attachment-list">
    <!-- 搜索栏 -->
    <el-card class="search-card">
      <el-form :inline="true" :model="queryForm" class="search-form">
        <el-form-item label="资产编码">
          <el-input v-model="queryForm.assetCode" placeholder="请输入资产编码" clearable />
        </el-form-item>
        <el-form-item label="资产名称">
          <el-input v-model="queryForm.assetName" placeholder="请输入资产名称" clearable />
        </el-form-item>
        <el-form-item label="附件类型">
          <el-select v-model="queryForm.attachmentType" placeholder="请选择附件类型" clearable>
            <el-option label="采购合同" :value="1" />
            <el-option label="验收报告" :value="2" />
            <el-option label="维修记录" :value="3" />
            <el-option label="保养记录" :value="4" />
            <el-option label="转移单" :value="5" />
            <el-option label="报废单" :value="6" />
            <el-option label="其他" :value="7" />
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
          上传附件
        </el-button>
        <el-button type="danger" :disabled="selectedRows.length === 0" @click="handleBatchDelete">
          <el-icon><Delete /></el-icon>
          批量删除
        </el-button>
      </div>
      
      <el-table 
        :data="tableData" 
        v-loading="loading" 
        border 
        stripe
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" />
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="assetCode" label="资产编码" width="120" />
        <el-table-column prop="assetName" label="资产名称" width="150" />
        <el-table-column prop="attachmentName" label="附件名称" width="200" />
        <el-table-column prop="attachmentType" label="附件类型" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="getAttachmentTypeTag(row.attachmentType)" size="small">
              {{ getAttachmentTypeText(row.attachmentType) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="fileSize" label="文件大小" width="100" align="right">
          <template #default="{ row }">
            {{ formatFileSize(row.fileSize) }}
          </template>
        </el-table-column>
        <el-table-column prop="uploadUser" label="上传人" width="100" />
        <el-table-column prop="uploadTime" label="上传时间" width="180" />
        <el-table-column label="操作" fixed="right" width="200">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleDownload(row)">下载</el-button>
            <el-button link type="primary" @click="handleView(row)">预览</el-button>
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

    <!-- 上传附件弹窗 -->
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
        label-width="120px"
      >
        <el-form-item label="资产 ID" prop="assetId">
          <el-input-number v-model="formData.assetId" :min="1" placeholder="请输入资产 ID" style="width: 100%" />
        </el-form-item>
        <el-form-item label="资产编码" prop="assetCode">
          <el-input v-model="formData.assetCode" placeholder="请输入资产编码" />
        </el-form-item>
        <el-form-item label="资产名称" prop="assetName">
          <el-input v-model="formData.assetName" placeholder="请输入资产名称" />
        </el-form-item>
        <el-form-item label="附件类型" prop="attachmentType">
          <el-select v-model="formData.attachmentType" placeholder="请选择附件类型" style="width: 100%">
            <el-option label="采购合同" :value="1" />
            <el-option label="验收报告" :value="2" />
            <el-option label="维修记录" :value="3" />
            <el-option label="保养记录" :value="4" />
            <el-option label="转移单" :value="5" />
            <el-option label="报废单" :value="6" />
            <el-option label="其他" :value="7" />
          </el-select>
        </el-form-item>
        <el-form-item label="上传文件" prop="file">
          <el-upload
            ref="uploadRef"
            :auto-upload="false"
            :limit="1"
            :on-change="handleFileChange"
            :on-remove="handleFileRemove"
          >
            <el-button type="primary">选择文件</el-button>
            <template #tip>
              <div class="el-upload__tip">支持 pdf/doc/xls/jpg/png 等格式，大小不超过 10MB</div>
            </template>
          </el-upload>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input 
            v-model="formData.remark" 
            type="textarea" 
            :rows="3" 
            placeholder="请输入备注信息" 
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitting">确定</el-button>
      </template>
    </el-dialog>

    <!-- 预览弹窗 -->
    <el-dialog v-model="previewVisible" title="附件预览" width="800px">
      <div class="preview-container">
        <el-descriptions :column="1" border>
          <el-descriptions-item label="附件名称">{{ previewData.attachmentName }}</el-descriptions-item>
          <el-descriptions-item label="附件类型">{{ getAttachmentTypeText(previewData.attachmentType) }}</el-descriptions-item>
          <el-descriptions-item label="文件大小">{{ formatFileSize(previewData.fileSize) }}</el-descriptions-item>
          <el-descriptions-item label="上传人">{{ previewData.uploadUser }}</el-descriptions-item>
          <el-descriptions-item label="上传时间">{{ previewData.uploadTime }}</el-descriptions-item>
          <el-descriptions-item label="备注">{{ previewData.remark || '-' }}</el-descriptions-item>
        </el-descriptions>
        <div class="preview-content" v-if="isImage(previewData.fileName)">
          <el-image 
            :src="previewData.fileUrl" 
            fit="contain" 
            style="width: 100%; max-height: 500px;"
          />
        </div>
        <div class="preview-content" v-else-if="isPDF(previewData.fileName)">
          <iframe :src="previewData.fileUrl" width="100%" height="500px" frameborder="0"></iframe>
        </div>
        <div class="preview-content" v-else>
          <el-empty description="该文件格式不支持在线预览，请下载后查看" />
        </div>
      </div>
      <template #footer>
        <el-button type="primary" @click="handleDownloadFromPreview">下载</el-button>
        <el-button @click="previewVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Delete } from '@element-plus/icons-vue'

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
                attachmentName: '采购合同.pdf',
                attachmentType: 1,
                fileName: 'contract_2024.pdf',
                fileUrl: '/files/contract_2024.pdf',
                fileSize: 1024567,
                uploadUser: '张三',
                uploadTime: '2024-01-15 10:30:00',
                remark: '采购合同原件扫描件'
              },
              {
                id: 2,
                assetId: 1002,
                assetCode: 'ZC20240002',
                assetName: 'HP 打印机',
                attachmentName: '验收报告.docx',
                attachmentType: 2,
                fileName: 'acceptance_report.docx',
                fileUrl: '/files/acceptance_report.docx',
                fileSize: 524288,
                uploadUser: '李四',
                uploadTime: '2024-01-16 14:20:00',
                remark: '设备验收报告'
              },
              {
                id: 3,
                assetId: 1001,
                assetCode: 'ZC20240001',
                assetName: '联想 ThinkPad X1',
                attachmentName: '维修记录.pdf',
                attachmentType: 3,
                fileName: 'maintenance_record.pdf',
                fileUrl: '/files/maintenance_record.pdf',
                fileSize: 256000,
                uploadUser: '王五',
                uploadTime: '2024-02-10 09:15:00',
                remark: '屏幕更换维修记录'
              }
            ],
            total: 3
          },
          message: 'success'
        })
      }, 300)
    })
  },
  create: (data) => {
    return new Promise((resolve) => {
      setTimeout(() => {
        resolve({ code: 200, message: '上传成功' })
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
  attachmentType: null
})

// 表格数据
const tableData = ref([])
const loading = ref(false)

// 选中行
const selectedRows = ref([])

// 分页
const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

// 弹窗控制
const dialogVisible = ref(false)
const previewVisible = ref(false)
const dialogTitle = ref('上传附件')
const submitting = ref(false)

// 表单引用
const formRef = ref(null)
const uploadRef = ref(null)

// 当前操作的记录
const currentRow = ref(null)
const selectedFile = ref(null)

// 预览数据
const previewData = ref({})

// 获取附件类型标签
const getAttachmentTypeTag = (type) => {
  const tagMap = {
    1: 'primary',
    2: 'success',
    3: 'warning',
    4: 'info',
    5: '',
    6: 'danger',
    7: 'info'
  }
  return tagMap[type] || ''
}

// 获取附件类型文本
const getAttachmentTypeText = (type) => {
  const textMap = {
    1: '采购合同',
    2: '验收报告',
    3: '维修记录',
    4: '保养记录',
    5: '转移单',
    6: '报废单',
    7: '其他'
  }
  return textMap[type] || '未知'
}

// 格式化文件大小
const formatFileSize = (bytes) => {
  if (!bytes) return '-'
  if (bytes < 1024) return bytes + ' B'
  if (bytes < 1024 * 1024) return (bytes / 1024).toFixed(2) + ' KB'
  return (bytes / (1024 * 1024)).toFixed(2) + ' MB'
}

// 判断是否为图片
const isImage = (fileName) => {
  const imageExtensions = ['.jpg', '.jpeg', '.png', '.gif', '.bmp', '.webp']
  return imageExtensions.some(ext => fileName.toLowerCase().endsWith(ext))
}

// 判断是否为 PDF
const isPDF = (fileName) => {
  return fileName.toLowerCase().endsWith('.pdf')
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
  queryForm.attachmentType = null
  handleSearch()
}

// 选择行变化
const handleSelectionChange = (rows) => {
  selectedRows.value = rows
}

// 新增
const handleCreate = () => {
  dialogVisible.value = true
  dialogTitle.value = '上传附件'
  resetForm()
}

// 表单重置
const resetForm = () => {
  formData.assetId = null
  formData.assetCode = ''
  formData.assetName = ''
  formData.attachmentType = null
  formData.remark = ''
  selectedFile.value = null
  if (uploadRef.value) {
    uploadRef.value.clearFiles()
  }
  if (formRef.value) {
    formRef.value.clearValidate()
  }
}

// 文件变化处理
const handleFileChange = (file) => {
  selectedFile.value = file.raw
}

// 文件移除处理
const handleFileRemove = () => {
  selectedFile.value = null
}

// 表单数据
const formData = reactive({
  assetId: null,
  assetCode: '',
  assetName: '',
  attachmentType: null,
  remark: ''
})

// 表单验证规则
const formRules = {
  assetId: [{ required: true, message: '请输入资产 ID', trigger: 'blur' }],
  assetCode: [{ required: true, message: '请输入资产编码', trigger: 'blur' }],
  assetName: [{ required: true, message: '请输入资产名称', trigger: 'blur' }],
  attachmentType: [{ required: true, message: '请选择附件类型', trigger: 'change' }]
}

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate(async (valid) => {
    if (!valid) return
    
    if (!selectedFile.value) {
      ElMessage.warning('请选择要上传的文件')
      return
    }
    
    submitting.value = true
    try {
      // 这里应该使用真实的文件上传 API
      const uploadData = new FormData()
      uploadData.append('file', selectedFile.value)
      uploadData.append('assetId', formData.assetId)
      uploadData.append('assetCode', formData.assetCode)
      uploadData.append('assetName', formData.assetName)
      uploadData.append('attachmentType', formData.attachmentType)
      uploadData.append('remark', formData.remark)
      
      await mockApi.create(uploadData)
      ElMessage.success('上传成功')
      dialogVisible.value = false
      fetchData()
    } catch (error) {
      ElMessage.error('上传失败')
    } finally {
      submitting.value = false
    }
  })
}

// 弹窗关闭
const handleDialogClose = () => {
  resetForm()
}

// 下载
const handleDownload = (row) => {
  ElMessage.info(`正在下载：${row.attachmentName}`)
  // 实际项目中应实现真实的下载逻辑
  // window.open(row.fileUrl, '_blank')
}

// 预览
const handleView = (row) => {
  previewData.value = { ...row }
  previewVisible.value = true
}

// 从预览弹窗下载
const handleDownloadFromPreview = () => {
  handleDownload(previewData.value)
}

// 删除
const handleDelete = (row) => {
  ElMessageBox.confirm('确定要删除该附件吗？', '提示', {
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

// 批量删除
const handleBatchDelete = () => {
  ElMessageBox.confirm(`确定要删除选中的 ${selectedRows.value.length} 个附件吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      for (const row of selectedRows.value) {
        await mockApi.delete(row.id)
      }
      ElMessage.success('批量删除成功')
      selectedRows.value = []
      fetchData()
    } catch (error) {
      ElMessage.error('批量删除失败')
    }
  }).catch(() => {})
}

// 初始化
onMounted(() => {
  fetchData()
})
</script>

<style scoped>
.attachment-list {
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

.preview-container {
  max-height: 70vh;
  overflow-y: auto;
}

.preview-content {
  margin-top: 20px;
  text-align: center;
}
</style>
