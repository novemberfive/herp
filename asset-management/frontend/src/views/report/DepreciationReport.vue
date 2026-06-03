<template>
  <div class="depreciation-report">
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
        <el-form-item label="资产分类">
          <el-select v-model="filterForm.categoryId" placeholder="请选择分类" clearable style="width: 150px">
            <el-option label="全部分类" :value="null" />
            <el-option v-for="cat in categoryList" :key="cat.id" :label="cat.name" :value="cat.id" />
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
            <div class="stat-icon" style="background: linear-gradient(135deg, #667eea 0%, #764ba2 100%)">
              <el-icon><Document /></el-icon>
            </div>
            <div class="stat-content">
              <div class="stat-label">资产原值</div>
              <div class="stat-value">¥{{ formatMoney(statistics.totalOriginalValue) }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-item">
            <div class="stat-icon" style="background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%)">
              <el-icon><Download /></el-icon>
            </div>
            <div class="stat-content">
              <div class="stat-label">累计折旧</div>
              <div class="stat-value">¥{{ formatMoney(statistics.totalAccumulatedDepreciation) }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-item">
            <div class="stat-icon" style="background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)">
              <el-icon><Money /></el-icon>
            </div>
            <div class="stat-content">
              <div class="stat-label">资产净值</div>
              <div class="stat-value">¥{{ formatMoney(statistics.totalNetValue) }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-item">
            <div class="stat-icon" style="background: linear-gradient(135deg, #fa709a 0%, #fee140 100%)">
              <el-icon><PieChart /></el-icon>
            </div>
            <div class="stat-content">
              <div class="stat-label">折旧率</div>
              <div class="stat-value">{{ calculateDepreciationRate }}%</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 数据表格 -->
    <el-card class="table-card" shadow="hover">
      <template #header>
        <div class="card-header">
          <span class="card-title">折旧明细</span>
        </div>
      </template>
      <el-table :data="tableData" style="width: 100%" v-loading="loading" border stripe highlight-current-row>
        <el-table-column prop="assetCode" label="资产编码" width="140" fixed />
        <el-table-column prop="assetName" label="资产名称" min-width="150" />
        <el-table-column prop="categoryName" label="分类" width="120" />
        <el-table-column label="原值" width="120" align="right">
          <template #default="{ row }">
            ¥{{ formatMoney(row.totalAmount) }}
          </template>
        </el-table-column>
        <el-table-column label="月折旧额" width="120" align="right">
          <template #default="{ row }">
            ¥{{ formatMoney(row.monthlyDepreciation) }}
          </template>
        </el-table-column>
        <el-table-column label="累计折旧" width="120" align="right">
          <template #default="{ row }">
            ¥{{ formatMoney(row.accumulatedDepreciation) }}
          </template>
        </el-table-column>
        <el-table-column label="净值" width="120" align="right">
          <template #default="{ row }">
            <span :class="{ 'warning-text': row.netValue && Number(row.netValue) < Number(row.totalAmount) * 0.2 }">
              ¥{{ formatMoney(row.netValue) }}
            </span>
          </template>
        </el-table-column>
        <el-table-column label="已使用月数" width="100" align="center">
          <template #default="{ row }">
            {{ row.useMonths || 0 }}月
          </template>
        </el-table-column>
        <el-table-column label="折旧进度" width="200" align="center">
          <template #default="{ row }">
            <el-progress
              :percentage="calculateProgress(row)"
              :status="getProgressStatus(row)"
              :stroke-width="18"
            />
          </template>
        </el-table-column>
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
import { Search, Refresh, Document, Download, Money, PieChart } from '@element-plus/icons-vue'
import { getDepreciationReport } from '@/api/report'
import { getCategoryList } from '@/api/basic'

// 筛选表单
const filterForm = reactive({
  startDate: null,
  endDate: null,
  categoryId: null
})

// 统计数据
const statistics = ref({})
const tableData = ref([])
const loading = ref(false)
const categoryList = ref([])

// 分页
const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

// 计算折旧率
const calculateDepreciationRate = computed(() => {
  if (!statistics.value.totalOriginalValue || statistics.value.totalOriginalValue === 0) {
    return '0.00'
  }
  const rate = (Number(statistics.value.totalAccumulatedDepreciation) / Number(statistics.value.totalOriginalValue)) * 100
  return rate.toFixed(2)
})

// 格式化金额
const formatMoney = (value) => {
  if (!value) return '0.00'
  return Number(value).toLocaleString('zh-CN', { minimumFractionDigits: 2, maximumFractionDigits: 2 })
}

// 计算折旧进度
const calculateProgress = (row) => {
  if (!row.totalAmount || Number(row.totalAmount) === 0) {
    return 0
  }
  const progress = (Number(row.accumulatedDepreciation) / Number(row.totalAmount)) * 100
  return Math.min(progress, 100)
}

// 获取进度条状态
const getProgressStatus = (row) => {
  const progress = calculateProgress(row)
  if (progress >= 95) {
    return 'success'
  } else if (progress >= 50) {
    return undefined
  } else {
    return undefined
  }
}

// 获取分类列表
const fetchCategoryList = async () => {
  try {
    const res = await getCategoryList()
    if (res.code === 200) {
      categoryList.value = res.data || []
    }
  } catch (error) {
    console.error('获取分类列表失败:', error)
  }
}

// 获取折旧报表数据
const fetchDepreciationReport = async () => {
  loading.value = true
  try {
    const params = {
      startDate: filterForm.startDate,
      endDate: filterForm.endDate,
      categoryId: filterForm.categoryId,
      pageNum: pagination.pageNum,
      pageSize: pagination.pageSize
    }
    const res = await getDepreciationReport(params)
    if (res.code === 200) {
      tableData.value = res.data.list || []
      pagination.total = res.data.total || 0
      statistics.value = {
        totalOriginalValue: res.data.totalOriginalValue,
        totalAccumulatedDepreciation: res.data.totalAccumulatedDepreciation,
        totalNetValue: res.data.totalNetValue
      }
    } else {
      ElMessage.error(res.message || '获取折旧报表失败')
    }
  } catch (error) {
    console.error('获取折旧报表失败:', error)
    ElMessage.error('获取折旧报表失败')
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = () => {
  pagination.pageNum = 1
  fetchDepreciationReport()
}

// 重置
const handleReset = () => {
  filterForm.startDate = null
  filterForm.endDate = null
  filterForm.categoryId = null
  pagination.pageNum = 1
  fetchDepreciationReport()
}

// 分页大小改变
const handleSizeChange = (size) => {
  pagination.pageSize = size
  pagination.pageNum = 1
  fetchDepreciationReport()
}

// 页码改变
const handleCurrentChange = (page) => {
  pagination.pageNum = page
  fetchDepreciationReport()
}

onMounted(() => {
  fetchCategoryList()
  fetchDepreciationReport()
})
</script>

<style scoped>
.depreciation-report {
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

.warning-text {
  color: #e6a23c;
  font-weight: 600;
}
</style>
