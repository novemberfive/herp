<template>
  <div class="transfer-form">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>{{ isEdit ? '编辑调拨' : '新增调拨' }}</span>
          <el-button @click="$router.back()">返回</el-button>
        </div>
      </template>

      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="120px"
      >
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="选择资产" prop="assetId">
              <el-select 
                v-model="form.assetId" 
                placeholder="请选择资产" 
                style="width: 100%;"
                @change="handleAssetChange"
              >
                <el-option
                  v-for="item in assetList"
                  :key="item.id"
                  :label="item.assetName + ' (' + item.assetCode + ')'"
                  :value="item.id"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="资产名称">
              <el-input v-model="form.assetName" disabled />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="调出部门" prop="fromDepartmentId">
              <el-select 
                v-model="form.fromDepartmentId" 
                placeholder="请选择调出部门" 
                style="width: 100%;"
              >
                <el-option
                  v-for="dept in departmentList"
                  :key="dept.id"
                  :label="dept.deptName"
                  :value="dept.id"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="调入部门" prop="toDepartmentId">
              <el-select 
                v-model="form.toDepartmentId" 
                placeholder="请选择调入部门" 
                style="width: 100%;"
              >
                <el-option
                  v-for="dept in departmentList"
                  :key="dept.id"
                  :label="dept.deptName"
                  :value="dept.id"
                />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="调拨类型" prop="transferType">
              <el-select v-model="form.transferType" placeholder="请选择调拨类型" style="width: 100%;">
                <el-option label="部门间调拨" :value="1" />
                <el-option label="人员变更" :value="2" />
                <el-option label="位置变更" :value="3" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="申请人" prop="applicantName">
              <el-input v-model="form.applicantName" placeholder="请输入申请人姓名" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item label="调拨原因" prop="transferReason">
              <el-input 
                v-model="form.transferReason" 
                type="textarea" 
                :rows="3" 
                placeholder="请输入调拨原因" 
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item label="备注" prop="remark">
              <el-input 
                v-model="form.remark" 
                type="textarea" 
                :rows="2" 
                placeholder="请输入备注信息" 
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item>
          <el-button type="primary" :loading="submitting" @click="handleSubmit">提交</el-button>
          <el-button @click="$router.back()">取消</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getTransferById, createTransfer, updateTransfer } from '@/api/transfer'
import { getAssetList } from '@/api/asset'
import { getDepartmentList } from '@/api/basic'

const route = useRoute()
const router = useRouter()
const formRef = ref(null)
const submitting = ref(false)
const isEdit = ref(false)
const assetList = ref([])
const departmentList = ref([])

const form = reactive({
  assetId: null,
  assetName: '',
  fromDepartmentId: null,
  fromDepartmentName: '',
  toDepartmentId: null,
  toDepartmentName: '',
  transferType: 1,
  transferReason: '',
  applicantName: '',
  remark: ''
})

const rules = {
  assetId: [{ required: true, message: '请选择资产', trigger: 'change' }],
  fromDepartmentId: [{ required: true, message: '请选择调出部门', trigger: 'change' }],
  toDepartmentId: [{ required: true, message: '请选择调入部门', trigger: 'change' }],
  transferType: [{ required: true, message: '请选择调拨类型', trigger: 'change' }],
  transferReason: [{ required: true, message: '请输入调拨原因', trigger: 'blur' }],
  applicantName: [{ required: true, message: '请输入申请人姓名', trigger: 'blur' }]
}

const loadAssetList = async () => {
  try {
    const res = await getAssetList(1, 100, null)
    assetList.value = res.data.list || []
  } catch (error) {
    console.error(error)
  }
}

const loadDepartmentList = async () => {
  try {
    const res = await getDepartmentList()
    if (res.code === 200) {
      departmentList.value = res.data || []
    }
  } catch (error) {
    ElMessage.error('获取部门列表失败')
    console.error(error)
  }
}

const handleAssetChange = (assetId) => {
  const asset = assetList.value.find(a => a.id === assetId)
  if (asset) {
    form.assetName = asset.assetName
    form.fromDepartmentId = asset.departmentId
    form.fromDepartmentName = asset.departmentName
  }
}

const fillDepartmentNames = () => {
  const fromDept = departmentList.value.find(item => item.id === form.fromDepartmentId)
  const toDept = departmentList.value.find(item => item.id === form.toDepartmentId)
  form.fromDepartmentName = fromDept?.deptName || form.fromDepartmentName || ''
  form.toDepartmentName = toDept?.deptName || form.toDepartmentName || ''
}

const handleSubmit = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate(async (valid) => {
    if (valid) {
      submitting.value = true
      try {
        fillDepartmentNames()
        if (isEdit.value) {
          await updateTransfer({ ...form, id: route.params.id })
          ElMessage.success('更新成功')
        } else {
          await createTransfer(form)
          ElMessage.success('创建成功')
        }
        router.push('/management/transfer')
      } catch (error) {
        console.error(error)
      } finally {
        submitting.value = false
      }
    }
  })
}

const loadTransfer = async () => {
  if (!route.params.id) return
  isEdit.value = true
  try {
    const res = await getTransferById(route.params.id)
    Object.assign(form, res.data)
  } catch (error) {
    console.error(error)
  }
}

onMounted(() => {
  loadAssetList()
  loadDepartmentList()
  if (route.params.id) {
    loadTransfer()
  }
})
</script>

<style scoped>
.transfer-form {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
