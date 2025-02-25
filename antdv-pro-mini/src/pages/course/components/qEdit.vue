<template>
  <a-modal
    v-model:open="visible"
    title="关联问卷"
    :confirm-loading="confirmLoading"
    @ok="handleOk"
    :afterClose="afterClose"
  >
    <a-form name="form">
      <a-form-item label="问卷" v-bind="validateInfos.questionnaire">
        <a-select
          ref="select"
          mode="multiple"
          v-model:value="modelRef.questionnaire"
          placeholder="请选择"
          style="width: 100%"
        >
          <a-select-option
            v-for="(v, i) in questionnaireList"
            :value="v.id"
            :key="i"
            :disabled="nowList.indexOf(v.id) !== -1"
            >{{ v.title }}</a-select-option
          >
        </a-select>
      </a-form-item>
    </a-form>
  </a-modal>
</template>
<script setup>
import { ref } from "vue";
import { Form, message } from "ant-design-vue";
import { bind } from "~/api/teacher/courses.js";
import { allList } from "~/api/teacher/questionnaires.js";
import { useUserStore } from "~@/stores/user";
const userStore = useUserStore();
const useForm = Form.useForm;
const visible = ref(false);
const nowList = ref([]);
const courseId = ref(null);
const questionnaireList = ref([]);
const confirmLoading = ref(false);
const emits = defineEmits(["saveOk"]);
const modelRef = ref({
  questionnaire: [],
});

const afterClose = () => {
  nowList.value = [];
  resetFields();
};

const { resetFields, validate, validateInfos } = useForm(
  modelRef,
  reactive({
    questionnaire: [{ required: true, message: "请选择问卷" }],
  })
);
const handleOk = async () => {
  await validate();
  confirmLoading.value = true;
  const saveData = {
    courseId: courseId.value,
    questionnaireIds: modelRef.value.questionnaire.join(","),
  };
  await bind(saveData);
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
  const { data } = await allList();
  questionnaireList.value = data;
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
