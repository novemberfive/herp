<template>
  <div class="disposal-report">
    <!-- 筛选区域 -->
    <el-card class="filter-card" shadow="hover">
      <el-form :inline="true" :model="filterForm" class="filter-form">
        <el-form-item label="开始日期">
          <el-date-picker
            v-model="filterForm.startDate"
            type="date"
            placeholder="选择开始日期"
            value-format="YYYY-MM-DD"
            style="width: 180px"
          />
        </el-form-item>
        <el-form-item label="结束日期">
          <el-date-picker
            v-model="filterForm.endDate"
            type="date"
            placeholder="选择结束日期"
            value-format="YYYY-MM-DD"
            style="width: 180px"
          />
        </el-form-item>
        <el-form-item label="处置类型">
          <el-select v-model="filterForm.disposalType" placeholder="请选择处置类型" clearable style="width: 150px">
            <el-option label="全部" :value="null" />
            <el-option label="报废" :value="1" />
            <el-option label="出售" :value="2" />
            <el-option label="捐赠" :value="3" />
            <el-option label="调出" :value="4" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">
            <el-icon><Search /></el-icon>查询
          </el-button>
          <el-button @click="handleReset">
            <el-icon><Refresh /></el-icon>重置
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 统计卡片 -->
    <el-row :gutter="20" class="stat-cards">
      <el-col :span="6">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-item">
            <div class="stat-icon" style="background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%)">
              <el-icon><Document /></el-icon>
            </div>
            <div class="stat-content">
              <div class="stat-label">处置总数</div>
              <div class="stat-value">{{ statistics.totalCount || 0 }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-item">
            <div class="stat-icon" style="background: linear-gradient(135deg, #667eea 0%, #764ba2 100%)">
              <el-icon><Money /></el-icon>
            </div>
            <div class="stat-content">
              <div class="stat-label">评估总值</div>
              <div class="stat-value">¥{{ formatMoney(statistics.totalEstimatedValue) }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-item">
            <div class="stat-icon" style="background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)">
              <el-icon><Coin /></el-icon>
            </div>
            <div class="stat-content">
              <div class="stat-label">成交总额</div>
              <div class="stat-value">¥{{ formatMoney(statistics.totalActualValue) }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-item">
            <div class="stat-icon" style="background: linear-gradient(135deg, #fa709a 0%, #fee140 100%)">
              <el-icon><TrendCharts /></el-icon>
            </div>
            <div class="stat-content">
              <div class="stat-label">回收率</div>
              <div class="stat-value">{{ calculateRecoveryRate }}%</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 数据表格 -->
    <el-card class="table-card" shadow="hover">
      <template #header>
        <div class="card-header">
          <span class="card-title">处置明细</span>
        </div>
      </template>
      <el-table :data="tableData" style="width: 100%" v-loading="loading" border stripe>
        <el-table-column prop="disposalNo" label="处置单号" width="140" />
        <el-table-column prop="assetCode" label="资产编码" width="120" />
        <el-table-column prop="assetName" label="资产名称" min-width="150" />
        <el-table-column prop="categoryName" label="分类" width="100" />
        <el-table-column label="处置类型" width="100">
          <template #default="{ row }">
            <el-tag :type="getDisposalTypeTag(row.disposalType)">
              {{ getDisposalTypeText(row.disposalType) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="处置状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getDisposalStatusTag(row.disposalStatus)">
              {{ getDisposalStatusText(row.disposalStatus) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="评估价值" width="120" align="right">
          <template #default="{ row }">
            ¥{{ formatMoney(row.estimatedValue) }}
          </template>
        </el-table-column>
        <el-table-column label="成交金额" width="120" align="right">
          <template #default="{ row }">
            ¥{{ formatMoney(row.actualValue) }}
          </template>
        </el-table-column>
        <el-table-column prop="applyTime" label="申请时间" width="160" />
        <el-table-column prop="departmentName" label="申请部门" width="120" />
        <el-table-column prop="applicantName" label="申请人" width="100" />
      </el-table>

      <!-- 分页 -->
      <div class="pagination-container">
        <el-pagination
          v-model:current-page="pagination.pageNum"
          v-model:page-size="pagination.pageSize"
          :page-sizes="[10, 20, 50, 100]"
          :total="pagination.total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Search, Refresh, Document, Money, Coin, TrendCharts } from '@element-plus/icons-vue'
import { getDisposalReport } from '@/api/report'

// 筛选表单
const filterForm = reactive({
  startDate: null,
  endDate: null,
  disposalType: null
})

// 统计数据
const statistics = ref({})
const tableData = ref([])
const loading = ref(false)

// 分页
const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

// 计算回收率
const calculateRecoveryRate = computed(() => {
  if (!statistics.value.totalEstimatedValue || statistics.value.totalEstimatedValue === 0) {
    return '0.00'
  }
  const rate = (Number(statistics.value.totalActualValue) / Number(statistics.value.totalEstimatedValue)) * 100
  return rate.toFixed(2)
})

// 格式化金额
const formatMoney = (value) => {
  if (!value) return '0.00'
  return Number(value).toLocaleString('zh-CN', { minimumFractionDigits: 2, maximumFractionDigits: 2 })
}

// 获取处置类型文本
const getDisposalTypeText = (type) => {
  const map = { 1: '报废', 2: '出售', 3: '捐赠', 4: '调出' }
  return map[type] || '未知'
}

// 获取处置类型标签颜色
const getDisposalTypeTag = (type) => {
  const map = { 1: 'danger', 2: 'success', 3: 'warning', 4: 'info' }
  return map[type] || ''
}

// 获取处置状态文本
const getDisposalStatusText = (status) => {
  const map = { 0: '待处置', 1: '处置中', 2: '已完成', 3: '已取消' }
  return map[status] || '未知'
}

// 获取处置状态标签颜色
const getDisposalStatusTag = (status) => {
  const map = { 0: 'warning', 1: 'primary', 2: 'success', 3: 'info' }
  return map[status] || ''
}

// 获取处置报表数据
const fetchDisposalReport = async () => {
  loading.value = true
  try {
    const params = {
      startDate: filterForm.startDate,
      endDate: filterForm.endDate,
      disposalType: filterForm.disposalType,
      pageNum: pagination.pageNum,
      pageSize: pagination.pageSize
    }
    const res = await getDisposalReport(params)
    if (res.code === 200) {
      tableData.value = res.data.list || []
      pagination.total = res.data.total || 0
      statistics.value = {
        totalCount: res.data.totalCount,
        totalEstimatedValue: res.data.totalEstimatedValue,
        totalActualValue: res.data.totalActualValue
      }
    } else {
      ElMessage.error(res.message || '获取处置报表失败')
    }
  } catch (error) {
    console.error('获取处置报表失败:', error)
    ElMessage.error('获取处置报表失败')
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = () => {
  pagination.pageNum = 1
  fetchDisposalReport()
}

// 重置
const handleReset = () => {
  filterForm.startDate = null
  filterForm.endDate = null
  filterForm.disposalType = null
  pagination.pageNum = 1
  fetchDisposalReport()
}

// 分页大小改变
const handleSizeChange = (size) => {
  pagination.pageSize = size
  pagination.pageNum = 1
  fetchDisposalReport()
}

// 页码改变
const handleCurrentChange = (page) => {
  pagination.pageNum = page
  fetchDisposalReport()
}

onMounted(() => {
  fetchDisposalReport()
})
</script>

<style scoped>
.disposal-report {
  padding: 20px;
  background: #f5f7fa;
  min-height: calc(100vh - 84px);
}

.filter-card {
  margin-bottom: 20px;
}

.filter-form {
  display: flex;
  flex-wrap: wrap;
}

.stat-cards {
  margin-bottom: 20px;
}

.stat-card {
  border-radius: 12px;
  overflow: hidden;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 16px;
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 28px;
  color: #fff;
  flex-shrink: 0;
}

.stat-content {
  flex: 1;
}

.stat-label {
  font-size: 14px;
  color: #909399;
  margin-bottom: 8px;
}

.stat-value {
  font-size: 28px;
  font-weight: bold;
  color: #303133;
  line-height: 1;
}

.table-card {
  border-radius: 12px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-title {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.pagination-container {
  display: flex;
  justify-content: flex-end;
  margin-top: 20px;
}
</style>
