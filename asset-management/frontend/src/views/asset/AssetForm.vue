<template>
  <div class="asset-form">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>{{ isEdit ? '编辑资产' : '新增资产' }}</span>
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
            <el-form-item label="资产名称" prop="assetName">
              <el-select
                v-model="form.assetName"
                placeholder="请选择资产主数据"
                clearable
                filterable
                allow-create
                default-first-option
                style="width: 100%"
                @change="handleAssetMasterChange"
              >
                <el-option
                  v-for="item in assetMasterList"
                  :key="item.id"
                  :label="formatAssetMasterLabel(item)"
                  :value="item.assetName"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="规格型号" prop="specification">
              <el-input v-model="form.specification" placeholder="选择主数据后可自动带出" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="分类名称">
              <el-input v-model="form.categoryName" placeholder="选择主数据后可自动带出" disabled />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="单位" prop="unit">
              <el-input v-model="form.unit" placeholder="如：台、件、套" style="width: 150px;" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="数量" prop="quantity">
              <el-input-number v-model="form.quantity" :min="1" style="width: 150px;" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="供应商">
              <el-select
                v-model="form.supplierName"
                placeholder="请选择供应商"
                clearable
                filterable
                allow-create
                default-first-option
                style="width: 100%"
                @change="handleSupplierChange"
              >
                <el-option
                  v-for="item in supplierList"
                  :key="item.id"
                  :label="item.supplierName"
                  :value="item.supplierName"
                />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="单价" prop="unitPrice">
              <el-input-number v-model="form.unitPrice" :min="0" :precision="2" style="width: 200px;" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="总金额">
              <el-input :value="totalAmount" disabled style="width: 200px;" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="购置日期" prop="purchaseDate">
              <el-date-picker
                v-model="form.purchaseDate"
                type="date"
                placeholder="选择日期"
                value-format="YYYY-MM-DD"
                style="width: 200px;"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="预计使用年限" prop="expectedUseYears">
              <el-input-number v-model="form.expectedUseYears" :min="1" :max="30" style="width: 150px;" />
              <span style="margin-left: 10px; color: #999;">年</span>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item label="备注" prop="remark">
              <el-input v-model="form.remark" type="textarea" :rows="3" placeholder="请输入备注信息" />
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
import { ref, reactive, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getAssetById, createAsset, updateAsset } from '@/api/asset'
import { getEnabledAssetMasterList, getEnabledSupplierList } from '@/api/basic'

const route = useRoute()
const router = useRouter()
const formRef = ref(null)
const submitting = ref(false)
const isEdit = ref(false)
const assetMasterList = ref([])
const supplierList = ref([])

const form = reactive({
  assetName: '',
  specification: '',
  categoryId: null,
  categoryName: '',
  unit: '台',
  quantity: 1,
  unitPrice: 0,
  purchaseDate: '',
  expectedUseYears: 5,
  supplierName: '',
  contactPhone: '',
  remark: ''
})

const rules = {
  assetName: [{ required: true, message: '请输入资产名称', trigger: 'blur' }],
  quantity: [{ required: true, message: '请输入数量', trigger: 'blur' }],
  unitPrice: [{ required: true, message: '请输入单价', trigger: 'blur' }]
}

const totalAmount = computed(() => {
  return ((form.unitPrice || 0) * (form.quantity || 0)).toFixed(2)
})

const loadBaseOptions = async () => {
  try {
    const [assetMasterRes, supplierRes] = await Promise.all([
      getEnabledAssetMasterList(),
      getEnabledSupplierList()
    ])
    if (assetMasterRes.code === 200) {
      assetMasterList.value = assetMasterRes.data || []
    }
    if (supplierRes.code === 200) {
      supplierList.value = supplierRes.data || []
    }
  } catch (error) {
    console.error('加载基础主数据失败', error)
  }
}

const handleAssetMasterChange = (value) => {
  const assetMaster = assetMasterList.value.find(item => item.assetName === value)
  if (!assetMaster) {
    if (!value) {
      form.specification = ''
      form.categoryId = null
      form.categoryName = ''
    }
    return
  }

  form.assetName = assetMaster.assetName
  form.specification = assetMaster.specification || ''
  form.categoryId = assetMaster.categoryId || null
  form.categoryName = assetMaster.categoryName || ''
  form.unit = assetMaster.unit || form.unit

  if (assetMaster.defaultPrice !== null && assetMaster.defaultPrice !== undefined) {
    form.unitPrice = Number(assetMaster.defaultPrice)
  }
  if (assetMaster.useLifeMonths) {
    form.expectedUseYears = Math.max(1, Math.ceil(Number(assetMaster.useLifeMonths) / 12))
  }
}

const handleSupplierChange = (value) => {
  const supplier = supplierList.value.find(item => item.supplierName === value)
  if (!supplier) {
    if (!value) {
      form.contactPhone = ''
    }
    return
  }
  form.contactPhone = supplier.contactPhone || ''
}

const formatAssetMasterLabel = (item) => {
  const parts = [item.assetName]
  if (item.specification) {
    parts.push(item.specification)
  }
  if (item.categoryName) {
    parts.push(item.categoryName)
  }
  return parts.join(' / ')
}

const handleSubmit = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate(async (valid) => {
    if (valid) {
      submitting.value = true
      try {
        if (isEdit.value) {
          await updateAsset({ ...form, id: route.params.id })
          ElMessage.success('更新成功')
        } else {
          await createAsset(form)
          ElMessage.success('创建成功')
        }
        router.push('/assets')
      } catch (error) {
        console.error(error)
      } finally {
        submitting.value = false
      }
    }
  })
}

const loadAsset = async () => {
  if (!route.params.id) return
  isEdit.value = true
  try {
    const res = await getAssetById(route.params.id)
    Object.assign(form, res.data)
  } catch (error) {
    console.error(error)
  }
}

onMounted(() => {
  loadBaseOptions()
  if (route.params.id) {
    loadAsset()
  }
})
</script>

<style scoped>
.asset-form {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
