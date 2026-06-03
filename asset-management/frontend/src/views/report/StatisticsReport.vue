<template>
  <div class="statistics-report">
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
        <el-form-item label="部门">
          <el-select v-model="filterForm.departmentId" placeholder="请选择部门" clearable style="width: 180px">
            <el-option label="全部部门" :value="null" />
            <el-option v-for="dept in departmentList" :key="dept.id" :label="dept.name" :value="dept.id" />
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
              <div class="stat-label">资产总数</div>
              <div class="stat-value">{{ statistics.total || 0 }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-item">
            <div class="stat-icon" style="background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%)">
              <el-icon><Money /></el-icon>
            </div>
            <div class="stat-content">
              <div class="stat-label">资产总额</div>
              <div class="stat-value">¥{{ formatMoney(statistics.totalAmount) }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-item">
            <div class="stat-icon" style="background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)">
              <el-icon><CircleCheck /></el-icon>
            </div>
            <div class="stat-content">
              <div class="stat-label">闲置资产</div>
              <div class="stat-value">{{ statistics.statusCounts?.闲置 || 0 }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-item">
            <div class="stat-icon" style="background: linear-gradient(135deg, #fa709a 0%, #fee140 100%)">
              <el-icon><User /></el-icon>
            </div>
            <div class="stat-content">
              <div class="stat-label">在用资产</div>
              <div class="stat-value">{{ statistics.statusCounts?.在用 || 0 }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 图表区域 -->
    <el-row :gutter="20" class="chart-row">
      <!-- 状态分布饼图 -->
      <el-col :span="12">
        <el-card class="chart-card" shadow="hover">
          <template #header>
            <div class="card-header">
              <span class="card-title">资产状态分布</span>
            </div>
          </template>
          <v-chart class="chart" :option="statusChartOption" autoresize />
        </el-card>
      </el-col>
      <!-- 趋势折线图 -->
      <el-col :span="12">
        <el-card class="chart-card" shadow="hover">
          <template #header>
            <div class="card-header">
              <span class="card-title">近 12 个月新增趋势</span>
            </div>
          </template>
          <v-chart class="chart" :option="trendChartOption" autoresize />
        </el-card>
      </el-col>
    </el-row>

    <!-- 分类和部门柱状图 -->
    <el-row :gutter="20" class="chart-row">
      <el-col :span="12">
        <el-card class="chart-card" shadow="hover">
          <template #header>
            <div class="card-header">
              <span class="card-title">资产分类统计 TOP10</span>
            </div>
          </template>
          <v-chart class="chart" :option="categoryChartOption" autoresize />
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card class="chart-card" shadow="hover">
          <template #header>
            <div class="card-header">
              <span class="card-title">部门资产分布 TOP10</span>
            </div>
          </template>
          <v-chart class="chart" :option="departmentChartOption" autoresize />
        </el-card>
      </el-col>
    </el-row>

    <!-- 加载状态 -->
    <div v-loading="loading" class="loading-overlay" />
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Search, Refresh, Document, Money, CircleCheck, User, Download } from '@element-plus/icons-vue'
import { use } from 'echarts/core'
import { CanvasRenderer } from 'echarts/renderers'
import { PieChart, BarChart, LineChart } from 'echarts/charts'
import { TitleComponent, TooltipComponent, LegendComponent, GridComponent } from 'echarts/components'
import VChart from 'vue-echarts'
import { getStatisticsReport, exportStatisticsReport } from '@/api/report'
import { getDepartmentList } from '@/api/basic'

// 注册 ECharts 组件
use([CanvasRenderer, PieChart, BarChart, LineChart, TitleComponent, TooltipComponent, LegendComponent, GridComponent])

// 筛选表单
const filterForm = reactive({
  startDate: null,
  endDate: null,
  departmentId: null
})

// 统计数据
const statistics = ref({})
const loading = ref(false)
const departmentList = ref([])

// 图表选项
const statusChartOption = computed(() => ({
  tooltip: {
    trigger: 'item',
    formatter: '{b}: {c} ({d}%)'
  },
  legend: {
    orient: 'vertical',
    right: 10,
    top: 'center'
  },
  color: ['#5470c6', '#91cc75', '#fac858', '#ee6666', '#73c0de', '#3ba272', '#fc8452'],
  series: [
    {
      name: '资产状态',
      type: 'pie',
      radius: ['40%', '70%'],
      center: ['35%', '50%'],
      avoidLabelOverlap: false,
      itemStyle: {
        borderRadius: 10,
        borderColor: '#fff',
        borderWidth: 2
      },
      label: {
        show: true,
        position: 'outside',
        formatter: '{b}\n{d}%'
      },
      emphasis: {
        label: {
          show: true,
          fontSize: 14,
          fontWeight: 'bold'
        }
      },
      data: statistics.value.statusChart || []
    }
  ]
}))

const trendChartOption = computed(() => ({
  tooltip: {
    trigger: 'axis',
    axisPointer: { type: 'shadow' }
  },
  grid: {
    left: '3%',
    right: '4%',
    bottom: '3%',
    containLabel: true
  },
  xAxis: {
    type: 'category',
    data: (statistics.value.trendData || []).map(item => item.month),
    axisLabel: { rotate: 45 }
  },
  yAxis: {
    type: 'value',
    name: '数量'
  },
  series: [
    {
      name: '新增资产',
      type: 'line',
      smooth: true,
      data: (statistics.value.trendData || []).map(item => item.count),
      areaStyle: {
        color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
          { offset: 0, color: 'rgba(64, 158, 255, 0.5)' },
          { offset: 1, color: 'rgba(64, 158, 255, 0.05)' }
        ])
      },
      lineStyle: {
        width: 3,
        color: '#409EFF'
      },
      itemStyle: {
        color: '#409EFF'
      }
    }
  ]
}))

const categoryChartOption = computed(() => ({
  tooltip: {
    trigger: 'axis',
    axisPointer: { type: 'shadow' }
  },
  grid: {
    left: '3%',
    right: '4%',
    bottom: '3%',
    containLabel: true
  },
  xAxis: {
    type: 'value',
    name: '数量'
  },
  yAxis: {
    type: 'category',
    data: []
  },
  series: [
    {
      name: '资产数量',
      type: 'bar',
      data: [],
      itemStyle: {
        color: new echarts.graphic.LinearGradient(0, 0, 1, 0, [
          { offset: 0, color: '#83bff6' },
          { offset: 0.5, color: '#188df0' },
          { offset: 1, color: '#188df0' }
        ])
      },
      barWidth: '60%'
    }
  ]
}))

const departmentChartOption = computed(() => ({
  tooltip: {
    trigger: 'axis',
    axisPointer: { type: 'shadow' }
  },
  grid: {
    left: '3%',
    right: '4%',
    bottom: '3%',
    containLabel: true
  },
  xAxis: {
    type: 'value',
    name: '数量'
  },
  yAxis: {
    type: 'category',
    data: []
  },
  series: [
    {
      name: '资产数量',
      type: 'bar',
      data: [],
      itemStyle: {
        color: new echarts.graphic.LinearGradient(0, 0, 1, 0, [
          { offset: 0, color: '#f687b3' },
          { offset: 0.5, color: '#f05c8a' },
          { offset: 1, color: '#e02e6a' }
        ])
      },
      barWidth: '60%'
    }
  ]
}))

// 格式化金额
const formatMoney = (value) => {
  if (!value) return '0.00'
  return Number(value).toLocaleString('zh-CN', { minimumFractionDigits: 2, maximumFractionDigits: 2 })
}

// 获取部门列表
const fetchDepartmentList = async () => {
  try {
    const res = await getDepartmentList()
    if (res.code === 200) {
      departmentList.value = res.data || []
    }
  } catch (error) {
    console.error('获取部门列表失败:', error)
  }
}

// 获取统计数据
const fetchStatistics = async () => {
  loading.value = true
  try {
    const params = {
      startDate: filterForm.startDate,
      endDate: filterForm.endDate,
      departmentId: filterForm.departmentId
    }
    const res = await getStatisticsReport(params)
    if (res.code === 200) {
      statistics.value = res.data
      
      // 更新分类图表数据（简化处理，使用 ID）
      if (res.data.categoryCounts) {
        const entries = Object.entries(res.data.categoryCounts)
          .sort((a, b) => b[1] - a[1])
          .slice(0, 10)
        categoryChartOption.value.yAxis.data = entries.map(e => `分类${e[0]}`)
        categoryChartOption.value.series[0].data = entries.map(e => e[1])
      }
      
      // 更新部门图表数据（简化处理，使用 ID）
      if (res.data.departmentCounts) {
        const entries = Object.entries(res.data.departmentCounts)
          .sort((a, b) => b[1] - a[1])
          .slice(0, 10)
        departmentChartOption.value.yAxis.data = entries.map(e => `部门${e[0]}`)
        departmentChartOption.value.series[0].data = entries.map(e => e[1])
      }
    } else {
      ElMessage.error(res.message || '获取统计数据失败')
    }
  } catch (error) {
    console.error('获取统计数据失败:', error)
    ElMessage.error('获取统计数据失败')
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = () => {
  fetchStatistics()
}

// 重置
const handleReset = () => {
  filterForm.startDate = null
  filterForm.endDate = null
  filterForm.departmentId = null
  fetchStatistics()
}

onMounted(() => {
  fetchDepartmentList()
  fetchStatistics()
})
</script>

<style scoped>
.statistics-report {
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

.chart-row {
  margin-bottom: 20px;
}

.chart-card {
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

.chart {
  width: 100%;
  height: 350px;
}

.loading-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(255, 255, 255, 0.5);
  z-index: 999;
}
</style>
