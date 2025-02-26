<template>
    <div class="box">
      <template v-if="showList">
        <a-table :columns="columns" :data-source="tableData" :pagination="false">
          <template #bodyCell="{ column, record }">
            <template v-if="column.key === 'subject'">
              <span>{{ record.course.name }}</span>
            </template>
            <template v-else-if="column.key === 'teacher'">
              <span>{{ record.teacher.nickname }}</span>
            </template>
            <template v-else-if="column.key === 'feedback'">
              <span>{{ record.questionnaire.title }}</span>
            </template>
            <template v-else-if="column.key === 'status'">
              <a-tag style="margin-right: 0;" :color="getStatusColor(record.status)">{{ getStatusText(record.status) }}</a-tag>
            </template>
            <template v-else-if="column.key === 'createdAt'">
              <span>{{ parseTime(record.createdAt,'{y}-{m}-{d}') }}</span>
            </template>
            <template v-else-if="column.key === 'operation'">
              <a-button 
                style="padding-left: 0" 
                type="link" 
                v-if="!record.hasSubmitted"
                @click="handleFillIn(record)"
              >立即填写</a-button>
              <a-button 
                style="padding-left: 0" 
                type="link" 
                v-else
                @click="handlePreview(record)"
              >已填写 预览</a-button>
            </template>
          </template>
        </a-table>
      </template>

      <!-- 添加填写问卷组件 -->
      <Fillin
        v-if="showFillin"
        :data="FillinData"
        :courseId="courseId"
        @close="() => {
          showFillin = false;
          getList();
        }"
      />

      <!-- 添加预览组件 -->
      <Preview 
        v-if="showPreview"
        :data="previewData"
        @close="() => {
          showPreview = false;
        }"
      />
    </div>
  </template>
  <script setup>
  import { ref, onMounted, h } from "vue";
  import { message } from "ant-design-vue";
  import { parseTime } from "~/utils";
  import { allQuestionnaires } from "~/api/student/courses.js";
  import { useUserStore } from '~@/stores/user'
  import { response } from '~/api/student/courses.js'; // 添加导入
  import Fillin from '../course/components/Fillin.vue'; // 导入填写组件
  import Preview from '../course/components/Preview.vue'; // 导入预览组件

  const userStore = useUserStore()
  const loading = ref(false);
  const tableData = ref([]);
  
  const showList = ref(true)
  const showFillin = ref(false)
  const showPreview = ref(false)
  const courseId = ref(null)
  const FillinData = ref({})
  const previewData = ref({})
  const getStatusColor = (status) => {
  const statusColorMap = {
    0: 'orange',
    1: 'green',
    2: 'blue'
  };
  return statusColorMap[status];
};

const getStatusText = (status) => {
  const statusTextMap = {
    0: '待发布',
    1: '进行中',
    2: '已完成'
  };
  return statusTextMap[status];
};
  onMounted(() => {
    getList();
  });
  
  const getList = async () => {
    loading.value = true;
    try {
      const { data } = await allQuestionnaires({studentId: userStore.userInfo.id});
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

  const columns = [
    {
      title: "序号",
      dataIndex: "index",
      key: "index",
      width: 80,
    },
    {
      title: "课程",
      dataIndex: "subject",
      key: "subject",
    },
    {
      title: "教师",
      dataIndex: "teacher",
      key: "teacher",
    },
    {
      title: "问卷标题",
      dataIndex: "feedback",
      key: "feedback",
    },
    {
      title: "状态",
      dataIndex: "status",
      key: "status",
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

  // 添加填写问卷处理函数
  const handleFillIn = (item) => {
    FillinData.value =item.questionnaire;
    courseId.value = item.course.id;
    showFillin.value = true;
  };

  // 添加预览处理函数
  const handlePreview = async (record) => {
    try {
      const { data } = await response({
        questionnaireId: record.questionnaire.id,
        courseId: record.course.id,
        studentId: userStore.userInfo.id
      });
      previewData.value = data;
      showPreview.value = true;
    } catch (error) {
      console.error(error);
      message.error('获取答案失败');
    }
  };
  </script>
  
  <style lang="less" scoped>
  .box {
    height: calc(100vh - 170px);
  }
  
  :deep(.ant-form-item) {
    margin-bottom: 0;
  }
  .pagination {
    display: flex;
    justify-content: flex-end;
    margin-top: 20px;
  }

  // 添加填写问卷容器样式
  .Fillin-container {
    position: fixed;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background: #fff;
    z-index: 100;
    overflow-y: auto;
  }
  </style>
  