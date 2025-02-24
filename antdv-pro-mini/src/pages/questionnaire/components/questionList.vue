<template>
  <div>
    <a-card mb-4>
      <a-form :model="searchForm">
        <a-row :gutter="[15, 0]">
          <a-col>
            <a-button
              @click="$emit('back')"
              shape="circle"
              :icon="h(ArrowLeftOutlined)"
            ></a-button>
          </a-col>
          <a-col>
            <a-form-item name="title" label="题目">
              <a-input
                v-model:value="searchForm.title"
                placeholder="请输入题目关键字"
              />
            </a-form-item>
          </a-col>
          <a-col>
            <a-form-item name="type" label="类型">
              <a-select
                v-model:value="searchForm.type"
                style="width: 150px"
                placeholder="请选择题目类型"
                allowClear
              >
                <a-select-option
                  v-for="item in questionTypes"
                  :key="item.value"
                  :value="item.value"
                >
                  {{ item.label }}
                </a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
          <a-col>
            <a-space>
              <a-button @click="handleReset">重置</a-button>
              <a-button type="primary" @click="handleAdd">
                <template #icon><PlusOutlined /></template>
                新增题目
              </a-button>
            </a-space>
          </a-col>
        </a-row>
      </a-form>
    </a-card>

    <a-table
      :columns="columns"
      :data-source="filteredQuestions"
      :pagination="false"
    >
      <template #bodyCell="{ column, record }">
        <template v-if="column.key === 'options'">
          <div style="display: flex; align-items: center">
            <div style="border-right: 1px solid #eee; padding-right: 5px;">
            <!-- 选项类题目 -->
            <template v-if="['radio', 'checkbox', 'select', 'radio-button', 'checkbox-button'].includes(record.type)">
              {{ record.options.join('、') }}
            </template>
            
            <!-- 文本类题目 -->
            <template v-else-if="['text', 'textarea'].includes(record.type)">
              <span class="text-gray">{{ record.placeholder || '无占位提示' }}</span>
            </template>
            
            <!-- 数字输入 -->
            <template v-else-if="record.type === 'number'">
              <span>{{ record.min }} 至 {{ record.max }}，步长 {{ record.step }}</span>
            </template>
            
            <!-- 日期选择 -->
            <template v-else-if="record.type === 'date'">
              <span>格式：{{ record.dateFormat }}</span>
            </template>
            
            <!-- 时间选择 -->
            <template v-else-if="record.type === 'time'">
              <span>格式：{{ record.timeFormat }}</span>
            </template>
          </div>
          
          <!-- 必答标记 -->
          <a-tag v-if="record.required" color="red" size="small" class="ml-2">
            必答
          </a-tag>
          </div>
        </template>
        <template v-else-if="column.key === 'type'">
          {{ getQuestionTypeLabel(record.type) }}
        </template>
        <template v-else-if="column.key === 'operation'">
          <a-space>
            <a-button style="padding: 0" type="link" @click="handleEdit(record)">编辑</a-button>
            <a-popconfirm
              title="确定删除该题目吗?"
              @confirm="handleDelete(record)"
            >
              <a-button type="link" danger>删除</a-button>
            </a-popconfirm>
          </a-space>
        </template>
      </template>
    </a-table>

    <question-edit ref="editRef" @save="handleSave" />
  </div>
</template>

<script setup>
import { ArrowLeftOutlined, PlusOutlined } from "@ant-design/icons-vue";
import { ref, computed, h, onMounted } from "vue";
import { message } from "ant-design-vue";
import QuestionEdit from "./QuestionEdit.vue";
import { save } from "~/api/teacher/questionnaires.js";
const props = defineProps({
  data: {
    type: String,
    default: null,
  },
});

const questionTypes = [
  { label: "单选框", value: "radio" },
  { label: "多选框", value: "checkbox" },
  { label: "单选按钮", value: "radio-button" },
  { label: "多选按钮", value: "checkbox-button" },
  { label: "填空框", value: "text" },
  { label: "文本域", value: "textarea" },
  { label: "下拉选择", value: "select" },
  { label: "日期选择", value: "date" },
  { label: "时间选择", value: "time" },
  { label: "数字输入", value: "number" },
];

const searchForm = ref({
  title: "",
  type: undefined,
});

const questionsList = ref([]);
const editRef = ref(null);
onMounted(() => {
  try {
    if (props.data.questions) {
        questionsList.value = JSON.parse(props.data.questions);
        questionsList.value.forEach((item, index) => {
            item.index = index + 1;
        });
    }
    
  } catch (error) {
    console.error("解析题目数据失败:", error);
    questionsList.value = [];
  }
});

const filteredQuestions = computed(() => {
  return questionsList.value.filter((item) => {
    const matchTitle =
      !searchForm.value.title ||
      item.title.toLowerCase().includes(searchForm.value.title.toLowerCase());
    const matchType =
      !searchForm.value.type || item.type === searchForm.value.type;
    return matchTitle && matchType;
  });
});

const getQuestionTypeLabel = (type) => {
  const found = questionTypes.find((item) => item.value === type);
  return found ? found.label : type;
};

const updateQuestions = async () => {
  let temp = JSON.parse(JSON.stringify(props.data));
  temp.questions = JSON.stringify(questionsList.value);
  await save(temp);
};

const handleReset = () => {
  searchForm.value = {
    title: "",
    type: undefined,
  };
};

const handleAdd = () => {
  editRef.value.open();
};

const handleEdit = (record) => {
  editRef.value.open(record);
};

const handleDelete = (record) => {
  const index = questionsList.value.findIndex((item) => item.id === record.id);
  if (index > -1) {
    questionsList.value.splice(index, 1);
    updateQuestions();
    message.success("删除成功");
  }
};

const handleSave = (data) => {
  const index = questionsList.value.findIndex((item) => item.id === data.id);
  if (index > -1) {
    questionsList.value[index] = data;
    message.success("编辑成功");
  } else {
    data.index = questionsList.value.length + 1;
    questionsList.value.push(data);
    message.success("添加成功");
  }
  updateQuestions();
};

const columns = [
  {
    title: "序号",
    dataIndex: "index",
    key: "index",
    width: 80,
  },
  {
    title: "题目",
    dataIndex: "title",
    key: "title",
    ellipsis: true,
  },
  {
    title: "题目类型",
    dataIndex: "type",
    key: "type",
  },
  {
    title: "选项/设置",
    dataIndex: "options",
    key: "options",
    ellipsis: true,
    width: 300,
  },
  {
    title: "操作",
    key: "operation",
    width: 150,
    fixed: 'right'
  },
];

defineEmits(["back"]);
</script>

<style lang="less" scoped>
:deep(.ant-form-item) {
  margin-bottom: 0;
}

.text-gray {
  color: #999;
}

.ml-2 {
  margin-left: 8px;
}
</style>
