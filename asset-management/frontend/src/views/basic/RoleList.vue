<template>
  <div class="role-list">
    <el-card class="search-card">
      <el-form :inline="true" :model="queryForm" class="search-form">
        <el-form-item label="角色名称">
          <el-input v-model="queryForm.roleName" placeholder="请输入角色名称" clearable />
        </el-form-item>
        <el-form-item label="角色编码">
          <el-input v-model="queryForm.roleCode" placeholder="请输入角色编码" clearable />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryForm.status" placeholder="请选择状态" clearable>
            <el-option label="启用" :value="1" />
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
        <el-button v-if="userStore.hasPermission('system:role:create')" type="primary" @click="handleCreate">
          <el-icon><Plus /></el-icon>
          新增角色
        </el-button>
      </div>

      <el-table :data="tableData" v-loading="loading" border stripe>
        <el-table-column prop="roleName" label="角色名称" min-width="160" />
        <el-table-column prop="roleCode" label="角色编码" width="140" />
        <el-table-column prop="status" label="状态" width="90" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'" size="small">
              {{ row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="权限项" min-width="240" show-overflow-tooltip>
          <template #default="{ row }">
            {{ formatPermissions(row.permissions) }}
          </template>
        </el-table-column>
        <el-table-column prop="sortOrder" label="排序" width="80" align="center" />
        <el-table-column prop="remark" label="备注" min-width="220" show-overflow-tooltip />
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" fixed="right" width="220">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleView(row)">详情</el-button>
            <el-button v-if="userStore.hasPermission('system:role:edit')" link type="primary" @click="handleEdit(row)">编辑</el-button>
            <el-button v-if="userStore.hasPermission('system:role:delete')" link type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="620px"
      @close="handleDialogClose"
    >
      <el-form ref="formRef" :model="formData" :rules="formRules" label-width="100px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="角色名称" prop="roleName">
              <el-input v-model="formData.roleName" placeholder="请输入角色名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="角色编码" prop="roleCode">
              <el-input
                v-model="formData.roleCode"
                :disabled="!!formData.id"
                placeholder="例如 operator_manager"
              />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="状态" prop="status">
              <el-radio-group v-model="formData.status">
                <el-radio-button :label="1">启用</el-radio-button>
                <el-radio-button :label="0">禁用</el-radio-button>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="排序" prop="sortOrder">
              <el-input-number v-model="formData.sortOrder" :min="0" :max="999" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="权限项" prop="permissionList">
          <el-checkbox-group v-model="formData.permissionList">
            <div class="permission-grid">
              <el-checkbox
                v-for="option in permissionOptions"
                :key="option.value"
                :label="option.value"
              >
                {{ option.label }}
              </el-checkbox>
            </div>
          </el-checkbox-group>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="formData.remark" type="textarea" :rows="3" placeholder="请输入备注" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="detailVisible" title="角色详情" width="620px">
      <el-descriptions :column="1" border>
        <el-descriptions-item label="角色 ID">{{ detailData.id }}</el-descriptions-item>
        <el-descriptions-item label="角色名称">{{ detailData.roleName }}</el-descriptions-item>
        <el-descriptions-item label="角色编码">{{ detailData.roleCode }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="detailData.status === 1 ? 'success' : 'info'" size="small">
            {{ detailData.status === 1 ? '启用' : '禁用' }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="权限项">{{ formatPermissions(detailData.permissions) }}</el-descriptions-item>
        <el-descriptions-item label="排序">{{ detailData.sortOrder }}</el-descriptions-item>
        <el-descriptions-item label="备注">{{ detailData.remark || '-' }}</el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ detailData.createTime }}</el-descriptions-item>
        <el-descriptions-item label="更新时间">{{ detailData.updateTime }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup>
import { reactive, ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { useUserStore } from '@/store/user'
import { expandPermissions } from '@/constants/permissions'
import {
  createRole,
  deleteRole,
  getRoleList,
  updateRole
} from '@/api/basic'

const userStore = useUserStore()

const queryForm = reactive({
  roleName: '',
  roleCode: '',
  status: null
})

const loading = ref(false)
const sourceData = ref([])
const tableData = ref([])
const dialogVisible = ref(false)
const detailVisible = ref(false)
const dialogTitle = ref('')
const submitting = ref(false)
const formRef = ref(null)
const detailData = ref({})

const formData = reactive({
  id: null,
  roleName: '',
  roleCode: '',
  status: 1,
  permissionList: [],
  sortOrder: 0,
  remark: ''
})

const permissionOptions = [
  { label: '工作台', value: 'dashboard:view' },
  { label: '资产列表', value: 'asset:list' },
  { label: '新增资产', value: 'asset:create' },
  { label: '编辑资产', value: 'asset:edit' },
  { label: '删除资产', value: 'asset:delete' },
  { label: '采购申请', value: 'acquisition:purchase' },
  { label: '新建采购申请', value: 'acquisition:purchase:create' },
  { label: '编辑采购申请', value: 'acquisition:purchase:edit' },
  { label: '删除采购申请', value: 'acquisition:purchase:delete' },
  { label: '提交采购申请', value: 'acquisition:purchase:submit' },
  { label: '审批采购申请', value: 'acquisition:purchase:approve' },
  { label: '验收登记', value: 'acquisition:acceptance' },
  { label: '新建验收登记', value: 'acquisition:acceptance:create' },
  { label: '编辑验收登记', value: 'acquisition:acceptance:edit' },
  { label: '删除验收登记', value: 'acquisition:acceptance:delete' },
  { label: '提交验收登记', value: 'acquisition:acceptance:submit' },
  { label: '审批验收登记', value: 'acquisition:acceptance:approve' },
  { label: '资产入库', value: 'acquisition:storage' },
  { label: '新建入库', value: 'acquisition:storage:create' },
  { label: '编辑入库', value: 'acquisition:storage:edit' },
  { label: '删除入库', value: 'acquisition:storage:delete' },
  { label: '确认入库', value: 'acquisition:storage:confirm' },
  { label: '附件管理', value: 'archive:attachment' },
  { label: '变更记录', value: 'archive:change' },
  { label: '维修记录', value: 'archive:maintenance' },
  { label: '新增维修申请', value: 'archive:maintenance:create' },
  { label: '编辑维修申请', value: 'archive:maintenance:edit' },
  { label: '删除维修申请', value: 'archive:maintenance:delete' },
  { label: '审批维修申请', value: 'archive:maintenance:approve' },
  { label: '开始维修', value: 'archive:maintenance:start' },
  { label: '完成维修', value: 'archive:maintenance:complete' },
  { label: '取消维修', value: 'archive:maintenance:cancel' },
  { label: '领用退库', value: 'management:requisition' },
  { label: '新建领用退库', value: 'management:requisition:create' },
  { label: '编辑领用退库', value: 'management:requisition:edit' },
  { label: '删除领用退库', value: 'management:requisition:delete' },
  { label: '提交领用退库', value: 'management:requisition:submit' },
  { label: '审批领用退库', value: 'management:requisition:approve' },
  { label: '资产调拨', value: 'management:transfer' },
  { label: '新建资产调拨', value: 'management:transfer:create' },
  { label: '编辑资产调拨', value: 'management:transfer:edit' },
  { label: '删除资产调拨', value: 'management:transfer:delete' },
  { label: '审批资产调拨', value: 'management:transfer:approve' },
  { label: '完成资产调拨', value: 'management:transfer:complete' },
  { label: '取消资产调拨', value: 'management:transfer:cancel' },
  { label: '借用归还', value: 'management:borrow' },
  { label: '新建借用申请', value: 'management:borrow:create' },
  { label: '编辑借用申请', value: 'management:borrow:edit' },
  { label: '删除借用申请', value: 'management:borrow:delete' },
  { label: '审批借用申请', value: 'management:borrow:approve' },
  { label: '归还借用资产', value: 'management:borrow:return' },
  { label: '取消借用申请', value: 'management:borrow:cancel' },
  { label: '发送逾期提醒', value: 'management:borrow:remind' },
  { label: '报废申请', value: 'disposal:scrap' },
  { label: '新建报废申请', value: 'disposal:scrap:create' },
  { label: '编辑报废申请', value: 'disposal:scrap:edit' },
  { label: '删除报废申请', value: 'disposal:scrap:delete' },
  { label: '审批报废申请', value: 'disposal:scrap:approve' },
  { label: '处置审批', value: 'disposal:approval' },
  { label: '审批处置单', value: 'disposal:approval:approve' },
  { label: '执行处置单', value: 'disposal:approval:execute' },
  { label: '完成处置单', value: 'disposal:approval:complete' },
  { label: '取消处置单', value: 'disposal:approval:cancel' },
  { label: '出售捐赠', value: 'disposal:sale' },
  { label: '新建出售捐赠', value: 'disposal:sale:create' },
  { label: '编辑出售捐赠', value: 'disposal:sale:edit' },
  { label: '删除出售捐赠', value: 'disposal:sale:delete' },
  { label: '审批出售捐赠', value: 'disposal:sale:approve' },
  { label: '执行出售捐赠', value: 'disposal:sale:execute' },
  { label: '完成出售捐赠', value: 'disposal:sale:complete' },
  { label: '取消出售捐赠', value: 'disposal:sale:cancel' },
  { label: '盘点计划', value: 'inventory:plan' },
  { label: '新建盘点计划', value: 'inventory:plan:create' },
  { label: '编辑盘点计划', value: 'inventory:plan:edit' },
  { label: '删除盘点计划', value: 'inventory:plan:delete' },
  { label: '启用盘点计划', value: 'inventory:plan:enable' },
  { label: '停用盘点计划', value: 'inventory:plan:disable' },
  { label: '执行盘点计划', value: 'inventory:plan:execute' },
  { label: '盘点任务', value: 'inventory:task' },
  { label: '新建盘点任务', value: 'inventory:task:create' },
  { label: '编辑盘点任务', value: 'inventory:task:edit' },
  { label: '删除盘点任务', value: 'inventory:task:delete' },
  { label: '开始盘点任务', value: 'inventory:task:start' },
  { label: '完成盘点任务', value: 'inventory:task:complete' },
  { label: '终止盘点任务', value: 'inventory:task:cancel' },
  { label: '盘点结果', value: 'inventory:result' },
  { label: '新建盘点结果', value: 'inventory:result:create' },
  { label: '编辑盘点结果', value: 'inventory:result:edit' },
  { label: '删除盘点结果', value: 'inventory:result:delete' },
  { label: '提交盘点结果', value: 'inventory:result:submit' },
  { label: '复核盘点结果', value: 'inventory:result:review' },
  { label: '处理盘点结果', value: 'inventory:result:process' },
  { label: '导入盘点结果', value: 'inventory:result:import' },
  { label: '我的资产', value: 'portal:my-assets' },
  { label: '部门资产', value: 'portal:dept-assets' },
  { label: '统计报表', value: 'report:statistics' },
  { label: '导出统计报表', value: 'report:statistics:export' },
  { label: '折旧报表', value: 'report:depreciation' },
  { label: '导出折旧报表', value: 'report:depreciation:export' },
  { label: '处置报表', value: 'report:disposal' },
  { label: '导出处置报表', value: 'report:disposal:export' },
  { label: '资产分类', value: 'basic:category' },
  { label: '新增资产分类', value: 'basic:category:create' },
  { label: '编辑资产分类', value: 'basic:category:edit' },
  { label: '删除资产分类', value: 'basic:category:delete' },
  { label: '存放位置', value: 'basic:location' },
  { label: '新增存放位置', value: 'basic:location:create' },
  { label: '编辑存放位置', value: 'basic:location:edit' },
  { label: '删除存放位置', value: 'basic:location:delete' },
  { label: '部门管理', value: 'basic:department' },
  { label: '新增部门', value: 'basic:department:create' },
  { label: '编辑部门', value: 'basic:department:edit' },
  { label: '删除部门', value: 'basic:department:delete' },
  { label: '供应商管理', value: 'basic:supplier' },
  { label: '新增供应商', value: 'basic:supplier:create' },
  { label: '编辑供应商', value: 'basic:supplier:edit' },
  { label: '删除供应商', value: 'basic:supplier:delete' },
  { label: '资产主数据', value: 'basic:asset-master' },
  { label: '新增资产主数据', value: 'basic:asset-master:create' },
  { label: '编辑资产主数据', value: 'basic:asset-master:edit' },
  { label: '删除资产主数据', value: 'basic:asset-master:delete' },
  { label: '启停资产主数据', value: 'basic:asset-master:status' },
  { label: '用户管理', value: 'system:user' },
  { label: '新增用户', value: 'system:user:create' },
  { label: '编辑用户', value: 'system:user:edit' },
  { label: '删除用户', value: 'system:user:delete' },
  { label: '重置用户密码', value: 'system:user:reset-password' },
  { label: '角色管理', value: 'system:role' },
  { label: '新增角色', value: 'system:role:create' },
  { label: '编辑角色', value: 'system:role:edit' },
  { label: '删除角色', value: 'system:role:delete' }
]

const formRules = {
  roleName: [{ required: true, message: '请输入角色名称', trigger: 'blur' }],
  roleCode: [
    { required: true, message: '请输入角色编码', trigger: 'blur' },
    { pattern: /^[a-z][a-z0-9_]{1,19}$/, message: '角色编码需以字母开头，支持小写字母、数字和下划线', trigger: 'blur' }
  ],
  status: [{ required: true, message: '请选择状态', trigger: 'change' }],
  permissionList: [{ type: 'array', required: true, min: 1, message: '请至少选择一个菜单权限', trigger: 'change' }]
}

const parsePermissions = (value) => {
  if (!value) return []
  return expandPermissions(value.split(',').map(item => item.trim()).filter(Boolean))
}

const formatPermissions = (value) => {
  const values = parsePermissions(value)
  if (!values.length) return '-'
  const labels = values.map(permission => {
    return permissionOptions.find(item => item.value === permission)?.label || permission
  })
  return labels.join('、')
}

const fetchData = async () => {
  loading.value = true
  try {
    const res = await getRoleList()
    if (res.code === 200) {
      sourceData.value = res.data || []
      applyFilter()
    } else {
      ElMessage.error(res.message || '获取角色列表失败')
    }
  } catch (error) {
    ElMessage.error('获取角色列表失败')
    console.error(error)
  } finally {
    loading.value = false
  }
}

const applyFilter = () => {
  tableData.value = sourceData.value.filter(item => {
    const matchName = !queryForm.roleName || item.roleName?.includes(queryForm.roleName)
    const matchCode = !queryForm.roleCode || item.roleCode?.includes(queryForm.roleCode)
    const matchStatus = queryForm.status === null || item.status === queryForm.status
    return matchName && matchCode && matchStatus
  })
}

const handleSearch = () => {
  applyFilter()
}

const handleReset = () => {
  queryForm.roleName = ''
  queryForm.roleCode = ''
  queryForm.status = null
  applyFilter()
}

const resetForm = () => {
  Object.assign(formData, {
    id: null,
    roleName: '',
    roleCode: '',
    status: 1,
    permissionList: [],
    sortOrder: 0,
    remark: ''
  })
}

const handleCreate = () => {
  dialogTitle.value = '新增角色'
  resetForm()
  dialogVisible.value = true
}

const handleEdit = (row) => {
  dialogTitle.value = '编辑角色'
  Object.assign(formData, {
    id: row.id,
    roleName: row.roleName,
    roleCode: row.roleCode,
    status: row.status,
    permissionList: parsePermissions(row.permissions),
    sortOrder: row.sortOrder,
    remark: row.remark
  })
  dialogVisible.value = true
}

const handleView = (row) => {
  detailData.value = { ...row }
  detailVisible.value = true
}

const handleDelete = (row) => {
  ElMessageBox.confirm(
    `确定要删除角色“${row.roleName}”吗？`,
    '提示',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    try {
      const res = await deleteRole(row.id)
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
        roleName: formData.roleName,
        roleCode: formData.roleCode,
        status: formData.status,
        permissions: formData.permissionList.join(','),
        sortOrder: formData.sortOrder,
        remark: formData.remark
      }

      let res
      if (formData.id) {
        data.id = formData.id
        res = await updateRole(data)
      } else {
        res = await createRole(data)
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
  formRef.value?.resetFields()
  resetForm()
}

onMounted(() => {
  fetchData()
})
</script>

<style scoped>
.role-list {
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

.permission-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 8px 12px;
}
</style>
