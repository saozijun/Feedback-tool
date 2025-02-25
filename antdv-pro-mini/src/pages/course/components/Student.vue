<template>
  <div class="box">
    <template v-if="showList && !showPreview">
      <a-card mb-4>
        <a-form :model="formModel">
          <a-row :gutter="[15, 0]">
            <a-col>
              <a-button
                @click="$emit('back')"
                shape="circle"
                :icon="h(ArrowLeftOutlined)"
              ></a-button>
            </a-col>
            <a-col>
              <a-form-item name="name" label="姓名">
                <a-input
                  v-model:value="formModel.nickname"
                  placeholder="请输入姓名"
                />
              </a-form-item>
            </a-col>
            <a-col>
              <a-space flex justify-end w-full>
                <a-button :loading="loading" type="primary" @click="onSearch">
                  查询
                </a-button>
                <a-button :loading="loading" @click="onReset"> 重置 </a-button>
                <a-button type="primary" @click="open">
                  <template #icon>
                    <PlusOutlined />
                  </template>
                  关联学生
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
            <a-popconfirm title="确定删除吗?" @confirm="onDelete(record.id)">
              <a-button style="padding: 0" type="link" danger>删除</a-button>
            </a-popconfirm>
          </template>
        </template>
      </a-table>
      <div class="pagination">
        <a-pagination
          v-model:current="formModel.pageNum"
          :total="total"
          @change="onPageChange"
        />
      </div>
      <sEdit ref="qEditRef" @saveOk="getList"></sEdit>
    </template>
    <question-preview
      v-if="showPreview"
      :data="previewData"
      @close="
        () => {
          showPreview = false;
          getList();
        }
      "
    />
  </div>
</template>
<script setup>
import { PlusOutlined, ArrowLeftOutlined } from "@ant-design/icons-vue";
import { ref, onMounted, h } from "vue";
import { message } from "ant-design-vue";
import { parseTime } from "~/utils";
import { courseStudents, unbindstudent } from "~/api/teacher/courses.js";
import sEdit from "./sEdit.vue";
import QuestionPreview from "./questionPreview.vue";

const qEditRef = ref(null);
const expand = ref(false);
const loading = ref(false);
const tableData = ref([]);
const total = ref(0);
const formModel = ref({
  pageNum: 1,
  pageSize: 10,
  nickname: "",
});

const showList = ref(true);
const currentRecord = ref({});
const props = defineProps({
  courseId: {
    type: Number,
    default: 0,
  },
});
const showPreview = ref(false);
const previewData = ref({});

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
    const { data } = await courseStudents(props.courseId, {...formModel.value});
    total.value = data.total;
    data.records.map((item, i) => {
      item.index = i + 1;
    });
    tableData.value = data.records;
  } catch (error) {
    console.log(error);
  } finally {
    loading.value = false;
  }
};

const onDelete = async (id) => {
  try {
    await unbindstudent({ courseId: props.courseId, studentId: id });
    getList();
    message.success("删除成功");
  } catch (error) {
    console.log(error);
  }
};

const preview = (record = {}) => {
  previewData.value = record;
  showPreview.value = true;
};

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
    title: "姓名",
    dataIndex: "nickname",
    key: "nickname",
  },
  {
    title: "邮箱",
    dataIndex: "email",
    key: "email",
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
