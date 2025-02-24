<template>
  <a-modal
    v-model:visible="visible"
    :title="isEdit ? '编辑题目' : '新增题目'"
    @ok="handleSubmit"
    @cancel="handleCancel"
    width="700px"
  >
    <a-form ref="formRef" :model="formData" :rules="rules" :label-col="{ span: 4 }">
      <a-form-item label="题目" name="title">
        <a-input v-model:value="formData.title" placeholder="请输入题目" />
      </a-form-item>
      <a-form-item label="题目类型" name="type">
        <a-select 
          v-model:value="formData.type" 
          :placeholder="formData.type ? '' : '请选择题目类型'"
          allow-clear
          @change="handleTypeChange"
        >
          <a-select-option v-for="item in questionTypes" :key="item.value" :value="item.value">
            {{ item.label }}
          </a-select-option>
        </a-select>
      </a-form-item>
      <template v-if="['radio', 'checkbox', 'select', 'radio-button', 'checkbox-button'].includes(formData.type)">
        <a-form-item 
          label="选项" 
          name="options"
          :rules="[
            { required: true, message: '请输入选项' },
            { validator: rules.options[0].validator }
          ]"
        >
          <div v-for="(option, index) in formData.options" :key="index" class="option-item">
            <a-input 
              v-model:value="formData.options[index]" 
              placeholder="请输入选项"
              @blur="() => formRef.value?.validateFields(['options'])"
            >
              <template #suffix>
                <MinusCircleOutlined v-if="formData.options.length > 2" @click="removeOption(index)" />
                <PlusCircleOutlined v-if="index === formData.options.length - 1" @click="addOption" />
              </template>
            </a-input>
          </div>
        </a-form-item>
      </template>

      <template v-if="['text', 'textarea'].includes(formData.type)">
        <a-form-item label="占位提示" name="placeholder">
          <a-input v-model:value="formData.placeholder" placeholder="请输入占位提示文字" />
        </a-form-item>
      </template>

      <template v-if="formData.type === 'number'">
        <a-form-item label="数值范围">
          <a-space>
            <a-input-number v-model:value="formData.min" placeholder="最小值" />
            <span>至</span>
            <a-input-number v-model:value="formData.max" placeholder="最大值" />
          </a-space>
        </a-form-item>
        <a-form-item label="步长">
          <a-input-number v-model:value="formData.step" :min="0.01" :max="100" />
        </a-form-item>
      </template>

      <template v-if="formData.type === 'date'">
        <a-form-item label="日期格式">
          <a-select v-model:value="formData.dateFormat">
            <a-select-option value="YYYY-MM-DD">YYYY-MM-DD</a-select-option>
            <a-select-option value="YYYY/MM/DD">YYYY/MM/DD</a-select-option>
            <a-select-option value="YYYY年MM月DD日">YYYY年MM月DD日</a-select-option>
          </a-select>
        </a-form-item>
      </template>

      <template v-if="formData.type === 'time'">
        <a-form-item label="时间格式">
          <a-select v-model:value="formData.timeFormat">
            <a-select-option value="HH:mm:ss">HH:mm:ss</a-select-option>
            <a-select-option value="HH:mm">HH:mm</a-select-option>
          </a-select>
        </a-form-item>
      </template>

      <a-form-item label="必答" name="required">
        <a-switch v-model:checked="formData.required" />
      </a-form-item>
    </a-form>
  </a-modal>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { MinusCircleOutlined, PlusCircleOutlined } from '@ant-design/icons-vue'
import { message } from 'ant-design-vue'

const visible = ref(false)
const isEdit = ref(false)
const formRef = ref()

const questionTypes = [
  { label: '单选框', value: 'radio' },
  { label: '多选框', value: 'checkbox' },
  { label: '单选按钮', value: 'radio-button' },
  { label: '多选按钮', value: 'checkbox-button' },
  { label: '填空框', value: 'text' },
  { label: '文本域', value: 'textarea' },
  { label: '下拉选择', value: 'select' },
  { label: '日期选择', value: 'date' },
  { label: '时间选择', value: 'time' },
  { label: '数字输入', value: 'number' }
]

const formData = reactive({
  type: '',
  title: '',
  options: ['', ''],
  required: false,
  placeholder: '',
  min: 0,
  max: 100,
  step: 1,
  dateFormat: 'YYYY-MM-DD',
  timeFormat: 'HH:mm:ss'
})

const rules = {
  type: [{ required: true, message: '请选择题目类型' }],
  title: [{ required: true, message: '请输入题目' }],
  options: [{ 
    required: true, 
    message: '请输入选项', 
    trigger: 'change',
    validator: (rule, value) => {
      if (['radio', 'checkbox', 'select', 'radio-button', 'checkbox-button'].includes(formData.type)) {
        if (!value || !value.length) {
          return Promise.reject('请至少添加两个选项')
        }
        if (value.some(item => !item)) {
          return Promise.reject('选项内容不能为空')
        }
        if (new Set(value).size !== value.length) {
          return Promise.reject('选项不能重复')
        }
      }
      return Promise.resolve()
    }
  }]
}

const handleTypeChange = (value) => {
  if (['radio', 'checkbox', 'select', 'radio-button', 'checkbox-button'].includes(value)) {
    formData.options = ['', '']
  }
}

const addOption = () => {
  formData.options.push('')
}

const removeOption = (index) => {
  formData.options.splice(index, 1)
  formRef.value?.validateFields(['options']).catch(error => {
    console.log('选项验证失败:', error)
  })
}

const resetForm = () => {
  Object.assign(formData, {
    type: undefined,
    title: '',
    options: ['', ''],
    required: false,
    placeholder: '',
    min: 0,
    max: 100,
    step: 1,
    dateFormat: 'YYYY-MM-DD',
    timeFormat: 'HH:mm:ss'
  })
}

const open = (record) => {
  visible.value = true
  isEdit.value = !!record
  resetForm()
  formRef.value?.clearValidate()
  
  if (record) {
    Object.assign(formData, record)
  }
}

const handleSubmit = () => {
  formRef.value.validate().then(() => {
    if (['radio', 'checkbox', 'select', 'radio-button', 'checkbox-button'].includes(formData.type)) {
      if (formData.options.length < 2) {
        message.error('请至少添加两个选项')
        return
      }
    }
    
    const data = {
      ...formData,
      id: isEdit.value ? formData.id : Date.now()
    }
    emit('save', data)
    visible.value = false
    resetForm()
  }).catch(error => {
    console.log('验证失败:', error)
  })
}

const handleCancel = () => {
  visible.value = false
  resetForm()
}

defineExpose({
  open
})

const emit = defineEmits(['save'])
</script>

<style scoped>
.option-item {
  margin-bottom: 8px;
}
.option-item :deep(.anticon) {
  cursor: pointer;
  margin-left: 8px;
}
</style> 