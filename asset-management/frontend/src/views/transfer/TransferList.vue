<template>
  <div class="transfer-list">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>资产调拨</span>
          <el-button type="primary" @click="handleCreate">
            <el-icon><Plus /></el-icon>
            新增调拨
          </el-button>
        </div>
      </template>

      <!-- 搜索栏 -->
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="审批状态">
          <el-select v-model="searchForm.approveStatus" placeholder="全部状态" clearable>
            <el-option label="待审批" :value="0" />
            <el-option label="已通过" :value="1" />
            <el-option label="已拒绝" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item label="调拨状态">
          <el-select v-model="searchForm.transferStatus" placeholder="全部状态" clearable>
            <el-option label="待调拨" :value="0" />
            <el-option label="调拨中" :value="1" />
            <el-option label="已完成" :value="2" />
            <el-option label="已取消" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <!-- 表格 -->
      <el-table :data="tableData" v-loading="loading" border stripe>
        <el-table-column prop="transferCode" label="调拨单号" width="150" />
        <el-table-column prop="assetName" label="资产名称" min-width="120" />
        <el-table-column prop="fromDepartmentName" label="调出部门" width="120" />
        <el-table-column prop="toDepartmentName" label="调入部门" width="120" />
        <el-table-column prop="transferType" label="调拨类型" width="100" align="center">
          <template #default="{ row }">
            {{ getTransferTypeText(row.transferType) }}
          </template>
        </el-table-column>
        <el-table-column prop="approveStatus" label="审批状态" width="80" align="center">
          <template #default="{ row }">
            <el-tag :type="getApproveStatusType(row.approveStatus)">
              {{ getApproveStatusText(row.approveStatus) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="transferStatus" label="调拨状态" width="80" align="center">
          <template #default="{ row }">
            <el-tag :type="getTransferStatusType(row.transferStatus)">
              {{ getTransferStatusText(row.transferStatus) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="applicantName" label="申请人" width="100" />
        <el-table-column prop="applyTime" label="申请时间" width="160" />
        <el-table-column label="操作" width="280" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleView(row)">详情</el-button>
            <el-button 
              v-if="row.approveStatus === 0" 
              link 
              type="warning" 
              @click="handleApprove(row)"
            >
              审批
            </el-button>
            <el-button 
              v-if="row.approveStatus === 1 && row.transferStatus === 1" 
              link 
              type="success" 
              @click="handleComplete(row)"
            >
              完成
            </el-button>
            <el-button 
              v-if="row.transferStatus !== 2" 
              link 
              type="danger" 
              @click="handleCancel(row)"
            >
              取消
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
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getTransferList, deleteTransfer, approveTransfer, completeTransfer, cancelTransfer } from '@/api/transfer'

const router = useRouter()
const loading = ref(false)
const tableData = ref([])

const searchForm = reactive({
  approveStatus: null,
  transferStatus: null
})

const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

const fetchData = async () => {
  loading.value = true
  try {
    const res = await getTransferList(
      pagination.pageNum, 
      pagination.pageSize, 
      searchForm.approveStatus,
      searchForm.transferStatus
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
  searchForm.approveStatus = null
  searchForm.transferStatus = null
  handleSearch()
}

const handleCreate = () => {
  router.push('/transfers/create')
}

const handleView = (row) => {
  router.push(`/transfers/${row.id}/detail`)
}

const handleApprove = async (row) => {
  try {
    const { value } = await ElMessageBox.prompt('请输入审批意见', '审批调拨申请', {
      confirmButtonText: '通过',
      cancelButtonText: '拒绝',
      inputPattern: /.+/,
      inputErrorMessage: '请输入审批意见'
    })
    await approveTransfer(row.id, 1, value)
    ElMessage.success('审批通过')
    fetchData()
  } catch (error) {
    if (error === 'cancel') {
      try {
        const { value } = await ElMessageBox.prompt('请输入拒绝原因', '拒绝调拨申请', {
          confirmButtonText: '拒绝',
          cancelButtonText: '取消',
          inputPattern: /.+/,
          inputErrorMessage: '请输入拒绝原因'
        })
        await approveTransfer(row.id, 2, value)
        ElMessage.success('已拒绝申请')
        fetchData()
      } catch (e) {
        if (e !== 'cancel') {
          console.error(e)
        }
      }
    } else {
      console.error(error)
    }
  }
}

const handleComplete = async (row) => {
  try {
    await ElMessageBox.confirm(`确定要完成该调拨吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await completeTransfer(row.id)
    ElMessage.success('调拨已完成')
    fetchData()
  } catch (error) {
    if (error !== 'cancel') {
      console.error(error)
    }
  }
}

const handleCancel = async (row) => {
  try {
    await ElMessageBox.confirm(`确定要取消该调拨吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await cancelTransfer(row.id)
    ElMessage.success('调拨已取消')
    fetchData()
  } catch (error) {
    if (error !== 'cancel') {
      console.error(error)
    }
  }
}

const getTransferTypeText = (type) => {
  const texts = {
    1: '部门间调拨',
    2: '人员变更',
    3: '位置变更'
  }
  return texts[type] || '未知'
}

const getApproveStatusType = (status) => {
  const types = {
    0: 'warning',
    1: 'success',
    2: 'danger'
  }
  return types[status] || 'info'
}

const getApproveStatusText = (status) => {
  const texts = {
    0: '待审批',
    1: '已通过',
    2: '已拒绝'
  }
  return texts[status] || '未知'
}

const getTransferStatusType = (status) => {
  const types = {
    0: 'info',
    1: 'warning',
    2: 'success',
    3: 'danger'
  }
  return types[status] || 'info'
}

const getTransferStatusText = (status) => {
  const texts = {
    0: '待调拨',
    1: '调拨中',
    2: '已完成',
    3: '已取消'
  }
  return texts[status] || '未知'
}

onMounted(() => {
  fetchData()
})
</script>

<style scoped>
.transfer-list {
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
