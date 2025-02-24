<template>
  <a-modal
    v-model:open="visible"
    :title="modelRef.id ? '编辑' : '新增'"
    :confirm-loading="confirmLoading"
    @ok="handleOk"
    :afterClose="afterClose"
  >
    <a-form name="form">
      <a-form-item label="问卷标题" v-bind="validateInfos.title" >
        <a-input v-model:value="modelRef.title" placeholder="请输入"/>
      </a-form-item>
      <a-form-item label="问卷描述" v-bind="validateInfos.description" >
        <a-textarea v-model:value="modelRef.description" placeholder="请输入"/>
      </a-form-item>
    </a-form>
  </a-modal>
</template>
<script setup>

import { ref } from "vue";
import { Form, message } from 'ant-design-vue';
import { save } from "~/api/teacher/questionnaires.js";
import { useUserStore } from '~@/stores/user'
const userStore = useUserStore()
const useForm = Form.useForm;
const visible = ref(false);
const confirmLoading = ref(false);
const emits = defineEmits(["saveOk"]);
const modelRef = ref({
  title: "",
  description: "",
  // questions: []
})

const afterClose = () => {
  resetFields(); 
};

const { resetFields, validate, validateInfos } = useForm(
  modelRef,
  reactive({
    title: [{ required: true, message: '请输入问卷标题'}],
    description: [{ required: true, message: '请输入问卷描述'}],
    questions: [{ required: true, message: '请输入问题列表'}],
  }),
);
const handleOk = async () => {
  await validate();
  confirmLoading.value = true;
  if (!modelRef.value.id) modelRef.value.createdBy = userStore.userInfo.id
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