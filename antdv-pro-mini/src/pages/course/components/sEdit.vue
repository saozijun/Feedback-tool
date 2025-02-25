<template>
  <a-modal
    v-model:open="visible"
    title="关联学生"
    :confirm-loading="confirmLoading"
    @ok="handleOk"
    :afterClose="afterClose"
  >
    <a-form name="form">
      <a-form-item label="学生" v-bind="validateInfos.studentIds">
        <a-select
          ref="select"
          mode="multiple"
          v-model:value="modelRef.studentIds"
          placeholder="请选择"
          style="width: 100%"
        >
          <a-select-option
            v-for="(v, i) in studentLists"
            :value="v.id"
            :key="i"
            :disabled="nowList.indexOf(v.id) !== -1"
            >{{ v.nickname }} 
            <span style="color: #2881ff; font-size: 12px;" v-if="nowList.indexOf(v.id) !== -1">已关联</span>
            </a-select-option
          >
        </a-select>
      </a-form-item>
    </a-form>
  </a-modal>
</template>
<script setup>
import { ref } from "vue";
import { Form, message } from "ant-design-vue";
import { studentsBind, studentList } from "~/api/teacher/courses.js";
import { useUserStore } from "~@/stores/user";
const userStore = useUserStore();
const useForm = Form.useForm;
const visible = ref(false);
const nowList = ref([]);
const courseId = ref(null);
const studentLists = ref([]);
const confirmLoading = ref(false);
const emits = defineEmits(["saveOk"]);
const modelRef = ref({
  studentIds: [],
});

const afterClose = () => {
  nowList.value = [];
  resetFields();
};

const { resetFields, validate, validateInfos } = useForm(
  modelRef,
  reactive({
    studentIds: [{ required: true, message: "请选择学生" }],
  })
);
const handleOk = async () => {
  await validate();
  confirmLoading.value = true;
  const saveData = {
    courseId: courseId.value,
    studentIds: modelRef.value.studentIds.join(","),
  };
  await studentsBind(saveData);
  message.success("操作成功");
  emits("saveOk");
  visible.value = false;
  confirmLoading.value = false;
};
const open = async (id, list) => {
  list.map((v) => {
    nowList.value.push(v.id);
  });
  modelRef.value.questionnaire = nowList.value;
  courseId.value = id;
  visible.value = true;
  const { data } = await studentList();
  studentLists.value = data;
};
defineExpose({
  open,
});
</script>
<style lang="less" scoped>
.ant-form {
  margin-top: 20px;
}
</style>
