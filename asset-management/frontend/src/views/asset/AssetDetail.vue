<template>
  <div class="asset-detail">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>资产详情</span>
          <div>
            <el-button @click="$router.back()">返回</el-button>
            <el-button type="primary" @click="handleEdit">编辑</el-button>
          </div>
        </div>
      </template>

      <el-descriptions title="基本信息" :column="2" border>
        <el-descriptions-item label="资产编码">{{ asset.assetCode }}</el-descriptions-item>
        <el-descriptions-item label="资产名称">{{ asset.assetName }}</el-descriptions-item>
        <el-descriptions-item label="规格型号">{{ asset.specification || '-' }}</el-descriptions-item>
        <el-descriptions-item label="分类">{{ asset.categoryName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="单位">{{ asset.unit || '-' }}</el-descriptions-item>
        <el-descriptions-item label="数量">{{ asset.quantity || 0 }}</el-descriptions-item>
        <el-descriptions-item label="单价">¥{{ asset.unitPrice?.toFixed(2) || '0.00' }}</el-descriptions-item>
        <el-descriptions-item label="总金额">¥{{ asset.totalAmount?.toFixed(2) || '0.00' }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="getStatusType(asset.status)">{{ getStatusText(asset.status) }}</el-tag>
        </el-descriptions-item>
      </el-descriptions>

      <el-descriptions title="位置信息" :column="2" border style="margin-top: 20px;">
        <el-descriptions-item label="存放位置">{{ asset.locationName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="使用部门">{{ asset.departmentName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="使用人">{{ asset.userName || '-' }}</el-descriptions-item>
      </el-descriptions>

      <el-descriptions title="时间信息" :column="2" border style="margin-top: 20px;">
        <el-descriptions-item label="购置日期">{{ asset.purchaseDate || '-' }}</el-descriptions-item>
        <el-descriptions-item label="启用日期">{{ asset.enableDate || '-' }}</el-descriptions-item>
        <el-descriptions-item label="预计使用年限">{{ asset.expectedUseYears ? asset.expectedUseYears + '年' : '-' }}</el-descriptions-item>
        <el-descriptions-item label="保修截止">{{ asset.warrantyEndDate || '-' }}</el-descriptions-item>
      </el-descriptions>

      <el-descriptions title="财务信息" :column="2" border style="margin-top: 20px;">
        <el-descriptions-item label="折旧方法">{{ getDepreciationMethodText(asset.depreciationMethod) }}</el-descriptions-item>
        <el-descriptions-item label="已计提折旧">¥{{ asset.accumulatedDepreciation?.toFixed(2) || '0.00' }}</el-descriptions-item>
        <el-descriptions-item label="净值">¥{{ asset.netValue?.toFixed(2) || '0.00' }}</el-descriptions-item>
      </el-descriptions>

      <el-descriptions title="供应商信息" :column="2" border style="margin-top: 20px;">
        <el-descriptions-item label="供应商名称">{{ asset.supplierName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="联系人">{{ asset.contactPerson || '-' }}</el-descriptions-item>
        <el-descriptions-item label="联系电话">{{ asset.contactPhone || '-' }}</el-descriptions-item>
      </el-descriptions>

      <el-descriptions title="其他信息" :column="1" border style="margin-top: 20px;">
        <el-descriptions-item label="备注">{{ asset.remark || '-' }}</el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ asset.createTime || '-' }}</el-descriptions-item>
        <el-descriptions-item label="更新时间">{{ asset.updateTime || '-' }}</el-descriptions-item>
      </el-descriptions>
    </el-card>

    <!-- 操作记录 -->
    <el-card style="margin-top: 20px;">
      <template #header>
        <span>操作记录</span>
      </template>
      <el-empty description="暂无操作记录" />
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getAssetById } from '@/api/asset'

const route = useRoute()
const router = useRouter()
const asset = ref({})

const getStatusType = (status) => {
  const types = {
    0: 'info',
    1: 'success',
    2: 'warning',
    3: 'warning',
    4: 'danger',
    5: 'danger',
    6: ''
  }
  return types[status] || 'info'
}

const getStatusText = (status) => {
  const texts = {
    0: '闲置',
    1: '在用',
    2: '维修中',
    3: '待处置',
    4: '已处置',
    5: '丢失',
    6: '借出'
  }
  return texts[status] || '未知'
}

const getDepreciationMethodText = (method) => {
  const texts = {
    1: '平均年限法',
    2: '工作量法',
    3: '双倍余额递减法'
  }
  return texts[method] || '未知'
}

const loadAsset = async () => {
  try {
    const res = await getAssetById(route.params.id)
    asset.value = res.data
  } catch (error) {
    console.error(error)
  }
}

const handleEdit = () => {
  router.push(`/assets/${route.params.id}/edit`)
}

onMounted(() => {
  loadAsset()
})
</script>

<style scoped>
.asset-detail {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
