<template>
  <div class="user-list">
    <el-card class="search-card">
      <el-form :inline="true" :model="queryForm" class="search-form">
        <el-form-item label="用户名">
          <el-input v-model="queryForm.username" placeholder="请输入用户名" clearable />
        </el-form-item>
        <el-form-item label="姓名">
          <el-input v-model="queryForm.realName" placeholder="请输入姓名" clearable />
        </el-form-item>
        <el-form-item label="角色">
          <el-select v-model="queryForm.role" placeholder="请选择角色" clearable>
            <el-option
              v-for="item in roleOptions"
              :key="item.roleCode"
              :label="item.roleName"
              :value="item.roleCode"
            />
          </el-select>
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
        <el-button v-if="userStore.hasPermission('system:user:create')" type="primary" @click="handleCreate">
          <el-icon><Plus /></el-icon>
          新增用户
        </el-button>
      </div>

      <el-table :data="tableData" v-loading="loading" border stripe>
        <el-table-column prop="username" label="用户名" width="140" />
        <el-table-column prop="realName" label="姓名" width="120" />
        <el-table-column prop="departmentName" label="所属部门" min-width="140" />
        <el-table-column prop="role" label="角色" width="120" align="center">
          <template #default="{ row }">
            <el-tag :type="getRoleTagType(row.role)" size="small">
              {{ getRoleLabel(row.role) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="phone" label="手机号" width="140" />
        <el-table-column prop="email" label="邮箱" min-width="180" show-overflow-tooltip />
        <el-table-column prop="status" label="状态" width="90" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'" size="small">
              {{ row.status === 1 ? '正常' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="lastLoginTime" label="最后登录" width="180" />
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" fixed="right" width="300">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleView(row)">详情</el-button>
            <el-button v-if="userStore.hasPermission('system:user:edit')" link type="primary" @click="handleEdit(row)">编辑</el-button>
            <el-button v-if="userStore.hasPermission('system:user:reset-password')" link type="warning" @click="handleResetPassword(row)">重置密码</el-button>
            <el-button v-if="userStore.hasPermission('system:user:delete')" link type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="720px"
      @close="handleDialogClose"
    >
      <el-form ref="formRef" :model="formData" :rules="formRules" label-width="100px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="用户名" prop="username">
              <el-input v-model="formData.username" :disabled="!!formData.id" placeholder="请输入用户名" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item :label="formData.id ? '新密码' : '初始密码'" prop="password">
              <el-input
                v-model="formData.password"
                type="password"
                show-password
                :placeholder="formData.id ? '留空则保持原密码' : '请输入初始密码'"
              />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="真实姓名" prop="realName">
              <el-input v-model="formData.realName" placeholder="请输入真实姓名" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="角色" prop="role">
              <el-select v-model="formData.role" placeholder="请选择角色">
                <el-option
                  v-for="item in roleOptions"
                  :key="item.roleCode"
                  :label="item.roleName"
                  :value="item.roleCode"
                />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="所属部门" prop="departmentId">
              <el-tree-select
                v-model="formData.departmentId"
                :data="departmentOptions"
                clearable
                check-strictly
                node-key="id"
                :props="treeProps"
                placeholder="请选择所属部门"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="状态" prop="status">
              <el-radio-group v-model="formData.status">
                <el-radio-button :label="1">正常</el-radio-button>
                <el-radio-button :label="0">禁用</el-radio-button>
              </el-radio-group>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="手机号" prop="phone">
              <el-input v-model="formData.phone" placeholder="请输入手机号" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="邮箱" prop="email">
              <el-input v-model="formData.email" placeholder="请输入邮箱" />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="detailVisible" title="用户详情" width="620px">
      <el-descriptions :column="1" border>
        <el-descriptions-item label="用户 ID">{{ detailData.id }}</el-descriptions-item>
        <el-descriptions-item label="用户名">{{ detailData.username }}</el-descriptions-item>
        <el-descriptions-item label="真实姓名">{{ detailData.realName }}</el-descriptions-item>
        <el-descriptions-item label="所属部门">{{ detailData.departmentName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="角色">{{ getRoleLabel(detailData.role) }}</el-descriptions-item>
        <el-descriptions-item label="手机号">{{ detailData.phone || '-' }}</el-descriptions-item>
        <el-descriptions-item label="邮箱">{{ detailData.email || '-' }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="detailData.status === 1 ? 'success' : 'info'" size="small">
            {{ detailData.status === 1 ? '正常' : '禁用' }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="最后登录时间">{{ detailData.lastLoginTime || '-' }}</el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ detailData.createTime || '-' }}</el-descriptions-item>
        <el-descriptions-item label="更新时间">{{ detailData.updateTime || '-' }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { useUserStore } from '@/store/user'
import {
  createUser,
  deleteUser,
  getDepartmentList,
  getEnabledRoleList,
  getUserList,
  resetUserPassword,
  updateUser
} from '@/api/basic'

const userStore = useUserStore()

const queryForm = reactive({
  username: '',
  realName: '',
  role: '',
  status: null
})

const loading = ref(false)
const sourceData = ref([])
const tableData = ref([])
const departmentOptions = ref([])
const roleOptions = ref([])
const dialogVisible = ref(false)
const detailVisible = ref(false)
const dialogTitle = ref('')
const submitting = ref(false)
const formRef = ref(null)
const detailData = ref({})

const treeProps = {
  label: 'deptName',
  children: 'children'
}

const formData = reactive({
  id: null,
  username: '',
  password: '',
  realName: '',
  phone: '',
  email: '',
  departmentId: null,
  role: 'operator',
  status: 1
})

const validatePassword = (_rule, value, callback) => {
  if (!formData.id && !value) {
    callback(new Error('请输入初始密码'))
    return
  }
  callback()
}

const formRules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ validator: validatePassword, trigger: 'blur' }],
  realName: [{ required: true, message: '请输入真实姓名', trigger: 'blur' }],
  role: [{ required: true, message: '请选择角色', trigger: 'change' }],
  status: [{ required: true, message: '请选择状态', trigger: 'change' }]
}

const getRoleLabel = (role) => {
  const matchedRole = roleOptions.value.find(item => item.roleCode === role)
  if (matchedRole) return matchedRole.roleName
  if (role === 'admin') return '管理员'
  if (role === 'operator') return '操作员'
  if (role === 'viewer') return '查看者'
  return role || '-'
}

const getRoleTagType = (role) => {
  if (role === 'admin') return 'danger'
  if (role === 'operator') return 'primary'
  return 'info'
}

const buildTree = (list, parentId = 0) => {
  return list
    .filter(item => (item.parentId ?? 0) === parentId)
    .map(item => ({
      ...item,
      children: buildTree(list, item.id)
    }))
}

const fetchDepartmentOptions = async () => {
  const res = await getDepartmentList()
  departmentOptions.value = buildTree(res.data || [])
}

const fetchRoleOptions = async () => {
  const res = await getEnabledRoleList()
  roleOptions.value = res.data || []
}

const fetchData = async () => {
  loading.value = true
  try {
    const res = await getUserList()
    sourceData.value = res.data || []
    applyFilter()
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

const applyFilter = () => {
  tableData.value = sourceData.value.filter(item => {
    const matchUsername = !queryForm.username || item.username?.includes(queryForm.username)
    const matchRealName = !queryForm.realName || item.realName?.includes(queryForm.realName)
    const matchRole = !queryForm.role || item.role === queryForm.role
    const matchStatus = queryForm.status === null || item.status === queryForm.status
    return matchUsername && matchRealName && matchRole && matchStatus
  })
}

const handleSearch = () => {
  applyFilter()
}

const handleReset = () => {
  queryForm.username = ''
  queryForm.realName = ''
  queryForm.role = ''
  queryForm.status = null
  applyFilter()
}

const resetForm = () => {
  Object.assign(formData, {
    id: null,
    username: '',
    password: '',
    realName: '',
    phone: '',
    email: '',
    departmentId: null,
    role: roleOptions.value[0]?.roleCode || 'operator',
    status: 1
  })
}

const handleCreate = () => {
  dialogTitle.value = '新增用户'
  resetForm()
  dialogVisible.value = true
}

const handleEdit = (row) => {
  dialogTitle.value = '编辑用户'
  Object.assign(formData, {
    id: row.id,
    username: row.username,
    password: '',
    realName: row.realName,
    phone: row.phone,
    email: row.email,
    departmentId: row.departmentId,
    role: row.role,
    status: row.status
  })
  dialogVisible.value = true
}

const handleView = (row) => {
  detailData.value = { ...row }
  detailVisible.value = true
}

const handleResetPassword = (row) => {
  ElMessageBox.confirm(
    `确定要将用户“${row.realName || row.username}”的密码重置为 admin123 吗？`,
    '提示',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    await resetUserPassword(row.id)
    ElMessage.success('密码已重置为 admin123')
  }).catch(() => {})
}

const handleDelete = (row) => {
  ElMessageBox.confirm(
    `确定要删除用户“${row.realName || row.username}”吗？`,
    '提示',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    await deleteUser(row.id)
    ElMessage.success('删除成功')
    fetchData()
  }).catch(() => {})
}

const handleSubmit = async () => {
  if (!formRef.value) return

  await formRef.value.validate(async (valid) => {
    if (!valid) return

    submitting.value = true
    try {
      const payload = {
        id: formData.id,
        username: formData.username,
        password: formData.password,
        realName: formData.realName,
        phone: formData.phone,
        email: formData.email,
        departmentId: formData.departmentId,
        role: formData.role,
        status: formData.status
      }

      if (formData.id) {
        await updateUser(payload)
      } else {
        await createUser(payload)
      }

      ElMessage.success(formData.id ? '更新成功' : '创建成功')
      dialogVisible.value = false
      fetchData()
    } catch (error) {
      console.error(error)
    } finally {
      submitting.value = false
    }
  })
}

const handleDialogClose = () => {
  formRef.value?.resetFields()
  resetForm()
}

onMounted(async () => {
  await Promise.all([fetchDepartmentOptions(), fetchRoleOptions(), fetchData()])
})
</script>

<style scoped>
.user-list {
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
