<template>
  <div class="category-list">
    <!-- 搜索栏 -->
    <el-card class="search-card">
      <el-form :inline="true" :model="queryForm" class="search-form">
        <el-form-item label="分类名称">
          <el-input v-model="queryForm.categoryName" placeholder="请输入分类名称" clearable />
        </el-form-item>
        <el-form-item label="分类编码">
          <el-input v-model="queryForm.categoryCode" placeholder="请输入分类编码" clearable />
        </el-form-item>
        <el-form-item label="层级">
          <el-select v-model="queryForm.level" placeholder="请选择层级" clearable>
            <el-option label="一级" :value="1" />
            <el-option label="二级" :value="2" />
            <el-option label="三级" :value="3" />
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
        <el-button v-if="userStore.hasPermission('basic:category:create')" type="primary" @click="handleCreate">
          <el-icon><Plus /></el-icon>
          新增分类
        </el-button>
        <el-button type="success" @click="handleExpandAll">
          <el-icon><FolderOpened /></el-icon>
          展开全部
        </el-button>
        <el-button @click="handleCollapseAll">
          <el-icon><Folder /></el-icon>
          收起全部
        </el-button>
      </div>
      
      <el-table 
        :data="tableData" 
        v-loading="loading" 
        border 
        stripe
        row-key="id"
        :tree-props="{ children: 'children', hasChildren: 'hasChildren' }"
      >
        <el-table-column prop="categoryName" label="分类名称" width="200" />
        <el-table-column prop="categoryCode" label="分类编码" width="150" />
        <el-table-column prop="level" label="层级" width="80" align="center">
          <template #default="{ row }">
            <el-tag :type="getLevelType(row.level)" size="small">
              {{ getLevelText(row.level) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="sortOrder" label="排序" width="80" align="center" />
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" fixed="right" width="280">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleView(row)">详情</el-button>
            <el-button v-if="userStore.hasPermission('basic:category:edit')" link type="primary" @click="handleEdit(row)">编辑</el-button>
            <el-button v-if="userStore.hasPermission('basic:category:create')" link type="primary" @click="handleAddChild(row)">子分类</el-button>
            <el-button v-if="userStore.hasPermission('basic:category:delete')" link type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 新增/编辑弹窗 -->
    <el-dialog 
      v-model="dialogVisible" 
      :title="dialogTitle" 
      width="500px"
      @close="handleDialogClose"
    >
      <el-form 
        ref="formRef"
        :model="formData" 
        :rules="formRules" 
        label-width="100px"
      >
        <el-form-item label="父级分类" prop="parentId">
          <el-tree-select
            v-model="formData.parentId"
            :data="treeOptions"
            check-strictly
            placeholder="请选择父级分类（不选则为一级分类）"
            clearable
          />
        </el-form-item>
        <el-form-item label="分类名称" prop="categoryName">
          <el-input v-model="formData.categoryName" placeholder="请输入分类名称" />
        </el-form-item>
        <el-form-item label="分类编码" prop="categoryCode">
          <el-input v-model="formData.categoryCode" placeholder="请输入分类编码" />
        </el-form-item>
        <el-form-item label="排序" prop="sortOrder">
          <el-input-number v-model="formData.sortOrder" :min="0" :max="999" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitting">确定</el-button>
      </template>
    </el-dialog>

    <!-- 详情弹窗 -->
    <el-dialog v-model="detailVisible" title="分类详情" width="500px">
      <el-descriptions :column="1" border>
        <el-descriptions-item label="分类 ID">{{ detailData.id }}</el-descriptions-item>
        <el-descriptions-item label="分类名称">{{ detailData.categoryName }}</el-descriptions-item>
        <el-descriptions-item label="分类编码">{{ detailData.categoryCode }}</el-descriptions-item>
        <el-descriptions-item label="层级">
          <el-tag :type="getLevelType(detailData.level)" size="small">
            {{ getLevelText(detailData.level) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="父级分类">{{ detailData.parentName || '无' }}</el-descriptions-item>
        <el-descriptions-item label="排序">{{ detailData.sortOrder }}</el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ detailData.createTime }}</el-descriptions-item>
        <el-descriptions-item label="更新时间">{{ detailData.updateTime }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Folder, FolderOpened } from '@element-plus/icons-vue'
import { useUserStore } from '@/store/user'
import { 
  getCategoryList, 
  createCategory, 
  updateCategory, 
  deleteCategory,
  getTopLevelCategories 
} from '@/api/basic'

const userStore = useUserStore()

// 查询表单
const queryForm = reactive({
  categoryName: '',
  categoryCode: '',
  level: null
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
const dialogTitle = ref('')
const submitting = ref(false)

// 表单引用
const formRef = ref(null)

// 表单数据
const formData = reactive({
  id: null,
  parentId: null,
  categoryName: '',
  categoryCode: '',
  sortOrder: 0
})

// 表单验证规则
const formRules = {
  categoryName: [
    { required: true, message: '请输入分类名称', trigger: 'blur' }
  ],
  categoryCode: [
    { required: true, message: '请输入分类编码', trigger: 'blur' }
  ]
}

// 详情数据
const detailData = ref({})

// 树形选项
const treeOptions = ref([])

// 获取分类列表
const fetchData = async () => {
  loading.value = true
  try {
    const res = await getCategoryList()
    if (res.code === 200) {
      tableData.value = buildTreeData(res.data)
      treeOptions.value = buildTreeOptions(res.data)
    } else {
      ElMessage.error(res.message || '获取分类列表失败')
    }
  } catch (error) {
    ElMessage.error('获取分类列表失败')
    console.error(error)
  } finally {
    loading.value = false
  }
}

// 构建树形数据
const buildTreeData = (list) => {
  const map = {}
  const result = []
  
  // 创建映射
  list.forEach(item => {
    map[item.id] = { ...item, children: [] }
  })
  
  // 构建树形结构
  list.forEach(item => {
    const node = map[item.id]
    if (item.parentId === 0 || item.parentId === null) {
      result.push(node)
    } else if (map[item.parentId]) {
      map[item.parentId].children.push(node)
    }
  })
  
  return result
}

// 构建树形选项
const buildTreeOptions = (list) => {
  const map = {}
  const result = []
  
  list.forEach(item => {
    map[item.id] = { ...item, label: item.categoryName, value: item.id, children: [] }
  })
  
  list.forEach(item => {
    const node = map[item.id]
    if (item.parentId === 0 || item.parentId === null) {
      result.push(node)
    } else if (map[item.parentId]) {
      map[item.parentId].children.push(node)
    }
  })
  
  return result
}

// 搜索
const handleSearch = () => {
  fetchData()
}

// 重置
const handleReset = () => {
  queryForm.categoryName = ''
  queryForm.categoryCode = ''
  queryForm.level = null
  fetchData()
}

// 新增
const handleCreate = () => {
  dialogTitle.value = '新增分类'
  dialogVisible.value = true
  Object.assign(formData, {
    id: null,
    parentId: null,
    categoryName: '',
    categoryCode: '',
    sortOrder: 0
  })
}

// 添加子分类
const handleAddChild = (row) => {
  dialogTitle.value = '新增子分类'
  dialogVisible.value = true
  Object.assign(formData, {
    id: null,
    parentId: row.id,
    categoryName: '',
    categoryCode: '',
    sortOrder: 0
  })
}

// 编辑
const handleEdit = (row) => {
  dialogTitle.value = '编辑分类'
  dialogVisible.value = true
  Object.assign(formData, {
    id: row.id,
    parentId: row.parentId,
    categoryName: row.categoryName,
    categoryCode: row.categoryCode,
    sortOrder: row.sortOrder
  })
}

// 查看详情
const handleView = (row) => {
  detailData.value = { ...row }
  detailVisible.value = true
}

// 删除
const handleDelete = (row) => {
  ElMessageBox.confirm(
    `确定要删除分类"${row.categoryName}"吗？`,
    '提示',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    try {
      const res = await deleteCategory(row.id)
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
        parentId: formData.parentId || 0,
        categoryName: formData.categoryName,
        categoryCode: formData.categoryCode,
        sortOrder: formData.sortOrder
      }
      
      let res
      if (formData.id) {
        data.id = formData.id
        res = await updateCategory(data)
      } else {
        res = await createCategory(data)
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
}

// 展开全部
const handleExpandAll = () => {
  expandAll(tableData.value, true)
}

// 收起全部
const handleCollapseAll = () => {
  expandAll(tableData.value, false)
}

// 递归展开/收起
const expandAll = (data, isExpand) => {
  data.forEach(item => {
    if (item.children && item.children.length > 0) {
      expandAll(item.children, isExpand)
    }
  })
}

// 获取层级类型
const getLevelType = (level) => {
  const types = ['', 'success', 'warning', 'danger']
  return types[level] || 'info'
}

// 获取层级文本
const getLevelText = (level) => {
  const texts = ['', '一级', '二级', '三级']
  return texts[level] || `${level}级`
}

onMounted(() => {
  fetchData()
})
</script>

<style scoped>
.category-list {
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
