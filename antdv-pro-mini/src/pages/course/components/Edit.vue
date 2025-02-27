<template>
  <a-modal
    v-model:open="visible"
    :title="modelRef.id ? '编辑' : '新增'"
    :confirm-loading="confirmLoading"
    @ok="handleOk"
    :afterClose="afterClose"
  >
    <a-form name="form">
      <a-form-item label="课程名称" v-bind="validateInfos.name" >
        <a-input v-model:value="modelRef.name" placeholder="请输入"/>
      </a-form-item>
      <a-form-item label="课程学年" v-bind="validateInfos.academicYear" >
        <a-date-picker v-model:value="modelRef.academicYear" picker="year" valueFormat="YYYY" placeholder="请选择"/>
      </a-form-item>
      <a-form-item label="课程学期" v-bind="validateInfos.semester" >
        <a-select v-model:value="modelRef.semester" placeholder="请选择">
          <a-select-option :value="1">春季学期</a-select-option>
          <a-select-option :value="2">秋季学期</a-select-option>
        </a-select>
      </a-form-item>
      <a-form-item label="课程代码" v-bind="validateInfos.code" >
        <a-input v-model:value="modelRef.code" placeholder="请输入"/>
      </a-form-item>
    </a-form>
  </a-modal>
</template>
<script setup>
import { ref } from "vue";
import { Form, message } from 'ant-design-vue';
import { save } from "~/api/teacher/courses.js";
import { useUserStore } from '~@/stores/user'
const userStore = useUserStore()
const useForm = Form.useForm;
const visible = ref(false);
const confirmLoading = ref(false);
const emits = defineEmits(["saveOk"]);
const modelRef = ref({
  name: "",
  code: ""
})

const afterClose = () => {
  resetFields(); 
};

const { resetFields, validate, validateInfos } = useForm(
  modelRef,
  reactive({
    name: [{ required: true, message: '请输入课程名称'}],
    code: [{ required: true, message: '请输入课程代码'}],
    academicYear: [{ required: true, message: '请输入课程学年'}],
    semester: [{ required: true, message: '请选择学期'}]
  }),
);
const handleOk = async () => {
  await validate();
  confirmLoading.value = true;
  if (!modelRef.value.id) modelRef.value.teacherId = userStore.userInfo.id
  await save(modelRef.value);
  message.success('操作成功');
  emits('saveOk');
  visible.value = false;
  confirmLoading.value = false;
};
const open = (row) => {
  modelRef.value = JSON.parse(JSON.stringify(row));
  visible.value = true;
};
defineExpose({
  open,
});
</script>
<style lang="less" scoped>
.ant-form{
  margin-top: 20px;
}
</style>