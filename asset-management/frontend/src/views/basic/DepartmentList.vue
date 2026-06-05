<template>
  <div class="department-list">
    <el-card class="search-card">
      <el-form :inline="true" :model="queryForm" class="search-form">
        <el-form-item label="部门名称">
          <el-input v-model="queryForm.deptName" placeholder="请输入部门名称" clearable />
        </el-form-item>
        <el-form-item label="部门编码">
          <el-input v-model="queryForm.deptCode" placeholder="请输入部门编码" clearable />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryForm.status" placeholder="请选择状态" clearable>
            <el-option label="正常" :value="1" />
            <el-option label="禁用" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card class="table-card">
      <div class="toolbar">
        <el-button type="primary" @click="handleCreate">
          <el-icon><Plus /></el-icon>
          新增部门
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
        ref="tableRef"
        :data="tableData"
        v-loading="loading"
        border
        stripe
        row-key="id"
        :tree-props="{ children: 'children', hasChildren: 'hasChildren' }"
      >
        <el-table-column prop="deptName" label="部门名称" min-width="180" />
        <el-table-column prop="deptCode" label="部门编码" width="150" />
        <el-table-column prop="leaderName" label="负责人" width="120" />
        <el-table-column prop="phone" label="联系电话" width="140" />
        <el-table-column prop="status" label="状态" width="90" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'" size="small">
              {{ row.status === 1 ? '正常' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
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
            <el-button link type="primary" @click="handleEdit(row)">编辑</el-button>
            <el-button link type="primary" @click="handleAddChild(row)">子部门</el-button>
            <el-button link type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="560px"
      @close="handleDialogClose"
    >
      <el-form
        ref="formRef"
        :model="formData"
        :rules="formRules"
        label-width="100px"
      >
        <el-form-item label="父级部门" prop="parentId">
          <el-tree-select
            v-model="formData.parentId"
            :data="treeOptions"
            check-strictly
            placeholder="请选择父级部门（不选则为一级部门）"
            clearable
          />
        </el-form-item>
        <el-form-item label="部门名称" prop="deptName">
          <el-input v-model="formData.deptName" placeholder="请输入部门名称" />
        </el-form-item>
        <el-form-item label="部门编码" prop="deptCode">
          <el-input v-model="formData.deptCode" placeholder="请输入部门编码" />
        </el-form-item>
        <el-form-item label="负责人" prop="leaderName">
          <el-input v-model="formData.leaderName" placeholder="请输入负责人" />
        </el-form-item>
        <el-form-item label="联系电话" prop="phone">
          <el-input v-model="formData.phone" placeholder="请输入联系电话" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="formData.status">
            <el-radio-button :label="1">正常</el-radio-button>
            <el-radio-button :label="0">禁用</el-radio-button>
          </el-radio-group>
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

    <el-dialog v-model="detailVisible" title="部门详情" width="560px">
      <el-descriptions :column="1" border>
        <el-descriptions-item label="部门 ID">{{ detailData.id }}</el-descriptions-item>
        <el-descriptions-item label="部门名称">{{ detailData.deptName }}</el-descriptions-item>
        <el-descriptions-item label="部门编码">{{ detailData.deptCode }}</el-descriptions-item>
        <el-descriptions-item label="父级部门">{{ detailData.parentName || '无' }}</el-descriptions-item>
        <el-descriptions-item label="负责人">{{ detailData.leaderName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="联系电话">{{ detailData.phone || '-' }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="detailData.status === 1 ? 'success' : 'info'" size="small">
            {{ detailData.status === 1 ? '正常' : '禁用' }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="层级">
          <el-tag :type="getLevelType(detailData.level)" size="small">
            {{ getLevelText(detailData.level) }}
          </el-tag>
        </el-descriptions-item>
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
import {
  getDepartmentList,
  createDepartment,
  updateDepartment,
  deleteDepartment
} from '@/api/basic'

const queryForm = reactive({
  deptName: '',
  deptCode: '',
  status: null
})

const tableRef = ref(null)
const tableData = ref([])
const sourceData = ref([])
const treeOptions = ref([])
const loading = ref(false)
const dialogVisible = ref(false)
const detailVisible = ref(false)
const dialogTitle = ref('')
const submitting = ref(false)
const formRef = ref(null)
const detailData = ref({})

const formData = reactive({
  id: null,
  parentId: null,
  deptName: '',
  deptCode: '',
  leaderName: '',
  phone: '',
  status: 1,
  sortOrder: 0
})

const formRules = {
  deptName: [
    { required: true, message: '请输入部门名称', trigger: 'blur' }
  ],
  deptCode: [
    { required: true, message: '请输入部门编码', trigger: 'blur' }
  ],
  status: [
    { required: true, message: '请选择状态', trigger: 'change' }
  ]
}

const fetchData = async () => {
  loading.value = true
  try {
    const res = await getDepartmentList()
    if (res.code === 200) {
      sourceData.value = res.data || []
      applyFilter()
      treeOptions.value = buildTreeOptions(sourceData.value)
    } else {
      ElMessage.error(res.message || '获取部门列表失败')
    }
  } catch (error) {
    ElMessage.error('获取部门列表失败')
    console.error(error)
  } finally {
    loading.value = false
  }
}

const applyFilter = () => {
  const list = sourceData.value.filter(item => {
    const matchName = !queryForm.deptName || item.deptName?.includes(queryForm.deptName)
    const matchCode = !queryForm.deptCode || item.deptCode?.includes(queryForm.deptCode)
    const matchStatus = queryForm.status === null || item.status === queryForm.status
    return matchName && matchCode && matchStatus
  })
  tableData.value = buildTreeData(list)
}

const buildTreeData = (list) => {
  const allMap = {}
  sourceData.value.forEach(item => {
    allMap[item.id] = item
  })

  const map = {}
  const result = []
  list.forEach(item => {
    const parent = allMap[item.parentId]
    map[item.id] = {
      ...item,
      parentName: parent ? parent.deptName : '',
      children: []
    }
  })

  list.forEach(item => {
    const node = map[item.id]
    if (item.parentId === 0 || item.parentId === null || !map[item.parentId]) {
      result.push(node)
    } else {
      map[item.parentId].children.push(node)
    }
  })

  return result
}

const buildTreeOptions = (list) => {
  const map = {}
  const result = []
  list.forEach(item => {
    map[item.id] = {
      ...item,
      label: item.deptName,
      value: item.id,
      children: []
    }
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

const handleSearch = () => {
  applyFilter()
}

const handleReset = () => {
  queryForm.deptName = ''
  queryForm.deptCode = ''
  queryForm.status = null
  applyFilter()
}

const resetForm = (extra = {}) => {
  Object.assign(formData, {
    id: null,
    parentId: null,
    deptName: '',
    deptCode: '',
    leaderName: '',
    phone: '',
    status: 1,
    sortOrder: 0,
    ...extra
  })
}

const handleCreate = () => {
  dialogTitle.value = '新增部门'
  resetForm()
  dialogVisible.value = true
}

const handleAddChild = (row) => {
  dialogTitle.value = '新增子部门'
  resetForm({ parentId: row.id })
  dialogVisible.value = true
}

const handleEdit = (row) => {
  dialogTitle.value = '编辑部门'
  resetForm({
    id: row.id,
    parentId: row.parentId,
    deptName: row.deptName,
    deptCode: row.deptCode,
    leaderName: row.leaderName,
    phone: row.phone,
    status: row.status,
    sortOrder: row.sortOrder
  })
  dialogVisible.value = true
}

const handleView = (row) => {
  detailData.value = { ...row }
  detailVisible.value = true
}

const handleDelete = (row) => {
  ElMessageBox.confirm(
    `确定要删除部门"${row.deptName}"吗？`,
    '提示',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    try {
      const res = await deleteDepartment(row.id)
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

const handleSubmit = async () => {
  if (!formRef.value) return

  await formRef.value.validate(async (valid) => {
    if (!valid) return

    submitting.value = true
    try {
      const data = {
        parentId: formData.parentId || 0,
        deptName: formData.deptName,
        deptCode: formData.deptCode,
        leaderName: formData.leaderName,
        phone: formData.phone,
        status: formData.status,
        sortOrder: formData.sortOrder
      }

      let res
      if (formData.id) {
        data.id = formData.id
        res = await updateDepartment(data)
      } else {
        res = await createDepartment(data)
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

const handleDialogClose = () => {
  if (formRef.value) {
    formRef.value.resetFields()
  }
}

const handleExpandAll = () => {
  setExpand(tableData.value, true)
}

const handleCollapseAll = () => {
  setExpand(tableData.value, false)
}

const setExpand = (data, expanded) => {
  data.forEach(item => {
    tableRef.value?.toggleRowExpansion(item, expanded)
    if (item.children && item.children.length > 0) {
      setExpand(item.children, expanded)
    }
  })
}

const getLevelType = (level) => {
  const types = ['', 'success', 'warning', 'danger']
  return types[level] || 'info'
}

const getLevelText = (level) => {
  const texts = ['', '一级', '二级', '三级']
  return texts[level] || `${level}级`
}

onMounted(() => {
  fetchData()
})
</script>

<style scoped>
.department-list {
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
  gap: 12px;
  align-items: center;
  margin-bottom: 16px;
}
</style>
