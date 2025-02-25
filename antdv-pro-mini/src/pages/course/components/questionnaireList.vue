<template>
  <div class="box">
    <template v-if="showList && !showPreview">
      <a-card mb-4>
        <a-form :model="formModel">
          <a-row :gutter="[15, 0]" style="display: flex; justify-content: space-between;">
            <a-col>
              <a-button
                @click="$emit('back')"
                shape="circle"
                :icon="h(ArrowLeftOutlined)"
              ></a-button>
            </a-col>
            <a-col>
              <a-space flex justify-end w-full>
                <a-button type="primary" @click="open">
                  <template #icon>
                    <PlusOutlined />
                  </template>
                  关联问卷
                </a-button>
              </a-space>
            </a-col>
          </a-row>
        </a-form>
      </a-card>
      <a-table :columns="columns" :data-source="tableData" :pagination="false">
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'createdAt'">
            <span>{{ parseTime(record.createdAt) }}</span>
          </template>
          <template v-else-if="column.key === 'operation'">
            <a-button style="padding: 0" type="link" @click="preview(record)"
              >预览</a-button
            >
            <a-popconfirm title="确定删除吗?" @confirm="onDelete(record.id)">
              <a-button type="link" danger>删除</a-button>
            </a-popconfirm>
          </template>
        </template>
      </a-table>
      <qEdit ref="qEditRef" @saveOk="getList"></qEdit>
    </template>
    <question-preview
      v-if="showPreview"
      :data="previewData"
      @close="() => {
        showPreview = false;
        getList();
      }"
    />
  </div>
</template>
<script setup>
import { PlusOutlined, ArrowLeftOutlined } from "@ant-design/icons-vue";
import { ref, onMounted, h } from "vue";
import { message } from "ant-design-vue";
import { parseTime } from "~/utils";
import { association, unbind } from "~/api/teacher/courses.js";
import qEdit from "./qEdit.vue";
import QuestionPreview from './questionPreview.vue'

const qEditRef = ref(null);
const expand = ref(false);
const loading = ref(false);
const tableData = ref([]);
const total = ref(0);
const formModel = ref({
  pageNum: 1,
  pageSize: 10,
  name: "",
});

const showList = ref(true);
const currentRecord = ref({});
const props = defineProps({
  courseId: {
    type: Number,
    default: 0,
  },
});
const showPreview = ref(false)
const previewData = ref({})

onMounted(() => {
  getList();
});

const onPageChange = (page) => {
  getList();
};
const onSearch = () => {
  getList();
};
const onReset = () => {
  formModel.value = {};
  getList();
};

const getList = async () => {
  loading.value = true;
  try {
    const { data } = await association(props.courseId);
    data.map((item, i) => {
      item.index = i + 1;
    });
    tableData.value = data;
  } catch (error) {
    console.log(error);
  } finally {
    loading.value = false;
  }
};

const onDelete = async (id) => {
  try {
    await unbind({ courseId: props.courseId, questionnaireId: id });
    getList();
    message.success("删除成功");
  } catch (error) {
    console.log(error);
  }
};

const preview = (record = {}) => {
  previewData.value = record
  showPreview.value = true
}

const open = () => {
    qEditRef.value.open(props.courseId, tableData.value);
};
const showQuestions = (record) => {
  currentRecord.value = record;
  showList.value = false;
};

const columns = [
  {
    title: "序号",
    dataIndex: "index",
    key: "index",
    width: 80,
  },
  {
    title: "问卷标题",
    dataIndex: "title",
    key: "title",
  },
  {
    title: "问卷描述",
    dataIndex: "description",
    key: "description",
  },
  {
    title: "创建时间",
    dataIndex: "createdAt",
    key: "createdAt",
  },
  {
    title: "操作",
    key: "operation",
    fixed: "right",
    width: 200,
  },
];
</script>

<style lang="less" scoped>
.box {
  height: calc(100vh - 170px);
  overflow: auto;
}

:deep(.ant-form-item) {
  margin-bottom: 0;
}
.pagination {
  display: flex;
  justify-content: flex-end;
  margin-top: 20px;
}
</style>
