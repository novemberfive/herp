<template>
  <div class="transfer-detail">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>调拨详情</span>
          <div>
            <el-button @click="$router.back()">返回</el-button>
            <el-button 
              v-if="transfer.approveStatus === 0" 
              type="warning" 
              @click="handleApprove"
            >
              审批
            </el-button>
            <el-button 
              v-if="transfer.approveStatus === 1 && transfer.transferStatus === 1" 
              type="success" 
              @click="handleComplete"
            >
              完成
            </el-button>
          </div>
        </div>
      </template>

      <el-descriptions title="基本信息" :column="2" border>
        <el-descriptions-item label="调拨单号">{{ transfer.transferCode }}</el-descriptions-item>
        <el-descriptions-item label="资产名称">{{ transfer.assetName }}</el-descriptions-item>
        <el-descriptions-item label="资产编码">{{ transfer.assetCode }}</el-descriptions-item>
        <el-descriptions-item label="调拨类型">
          {{ getTransferTypeText(transfer.transferType) }}
        </el-descriptions-item>
      </el-descriptions>

      <el-descriptions title="部门信息" :column="2" border style="margin-top: 20px;">
        <el-descriptions-item label="调出部门">{{ transfer.fromDepartmentName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="调入部门">{{ transfer.toDepartmentName || '-' }}</el-descriptions-item>
      </el-descriptions>

      <el-descriptions title="申请信息" :column="2" border style="margin-top: 20px;">
        <el-descriptions-item label="申请人">{{ transfer.applicantName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="申请时间">{{ transfer.applyTime || '-' }}</el-descriptions-item>
        <el-descriptions-item label="审批状态">
          <el-tag :type="getApproveStatusType(transfer.approveStatus)">
            {{ getApproveStatusText(transfer.approveStatus) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="调拨状态">
          <el-tag :type="getTransferStatusType(transfer.transferStatus)">
            {{ getTransferStatusText(transfer.transferStatus) }}
          </el-tag>
        </el-descriptions-item>
      </el-descriptions>

      <el-descriptions title="审批信息" :column="2" border style="margin-top: 20px;">
        <el-descriptions-item label="审批人">{{ transfer.approverName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="审批时间">{{ transfer.approveTime || '-' }}</el-descriptions-item>
        <el-descriptions-item label="审批意见" :span="2">{{ transfer.approveRemark || '-' }}</el-descriptions-item>
      </el-descriptions>

      <el-descriptions title="其他信息" :column="1" border style="margin-top: 20px;">
        <el-descriptions-item label="调拨原因">{{ transfer.transferReason || '-' }}</el-descriptions-item>
        <el-descriptions-item label="备注">{{ transfer.remark || '-' }}</el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ transfer.createTime || '-' }}</el-descriptions-item>
        <el-descriptions-item label="完成时间">{{ transfer.completeTime || '-' }}</el-descriptions-item>
      </el-descriptions>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getTransferById, approveTransfer, completeTransfer } from '@/api/transfer'

const route = useRoute()
const router = useRouter()
const transfer = ref({})

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

const handleApprove = async () => {
  try {
    const { value } = await ElMessageBox.prompt('请输入审批意见', '审批调拨申请', {
      confirmButtonText: '通过',
      cancelButtonText: '拒绝',
      inputPattern: /.+/,
      inputErrorMessage: '请输入审批意见'
    })
    await approveTransfer(route.params.id, 1, value)
    ElMessage.success('审批通过')
    loadTransfer()
  } catch (error) {
    if (error === 'cancel') {
      try {
        const { value } = await ElMessageBox.prompt('请输入拒绝原因', '拒绝调拨申请', {
          confirmButtonText: '拒绝',
          cancelButtonText: '取消',
          inputPattern: /.+/,
          inputErrorMessage: '请输入拒绝原因'
        })
        await approveTransfer(route.params.id, 2, value)
        ElMessage.success('已拒绝申请')
        loadTransfer()
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

const handleComplete = async () => {
  try {
    await ElMessageBox.confirm('确定要完成该调拨吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await completeTransfer(route.params.id)
    ElMessage.success('调拨已完成')
    loadTransfer()
  } catch (error) {
    if (error !== 'cancel') {
      console.error(error)
    }
  }
}

const loadTransfer = async () => {
  try {
    const res = await getTransferById(route.params.id)
    transfer.value = res.data
  } catch (error) {
    console.error(error)
  }
}

onMounted(() => {
  loadTransfer()
})
</script>

<style scoped>
.transfer-detail {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
