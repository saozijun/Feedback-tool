<template>
  <div class="box">
    <a-row :gutter="[15, 0]" style="display: flex; align-items: center; margin-bottom: 20px;">
      <a-col>
        <a-button @click="router.back()" shape="circle" :icon="h(ArrowLeftOutlined)"></a-button>
      </a-col>
      <a-col style="font-size: 22px; font-weight: bold; color: #252525; margin-left: 10px;">
        {{ route.query.name }}
      </a-col>
    </a-row>

    <a-tabs v-model:activeKey="activeKey" v-if="!showPreview">
      <a-tab-pane key="1" tab="课程问卷">
        <div class="content-header">
          <h3>问卷列表</h3>
          <a-button type="primary" @click="openQuestionnaireEdit">
            <template #icon><PlusOutlined /></template>
            添加问卷
          </a-button>
        </div>
        <template v-if="questionnaireList.length">
          <div class="grid-list">
            <div class="grid-item" v-for="(item, index) in questionnaireList" :key="index">
              <div class="item-content">
                <div>
                  <h4>{{ item.questionnaire.title }}</h4>
                  <p>{{ item.questionnaire.description }}</p>
                </div>
                <a-tag style="margin-right: 0;" :color="getStatusColor(item.status)">{{ getStatusText(item.status) }}</a-tag>
              </div>
              <div class="item-footer">
                <a-button type="link" @click="previewQuestionnaire(item)">预览</a-button>
                <template v-if="item.status === 0">
                  <a-popconfirm 
                    title="确定发布该问卷吗?" 
                    description="发布后学生将可以查看并填写问卷"
                    @confirm="handlePublish(item.questionnaire.id)"
                  >
                    <a-button type="link">发布</a-button>
                  </a-popconfirm>
                </template>
                <template v-if="item.status === 1">
                  <a-popconfirm 
                    title="确定结束该问卷吗?" 
                    description="结束后学生将无法继续填写问卷"
                    @confirm="handleEnd(item.questionnaire.id)"
                  >
                    <a-button type="link">结束</a-button>
                  </a-popconfirm>
                  <a-popconfirm 
                    title="确定撤回该问卷吗?" 
                    description="撤回后问卷将回到待发布状态"
                    @confirm="handleRevoke(item.questionnaire.id)"
                  >
                    <a-button type="link">撤回</a-button>
                  </a-popconfirm>
                </template>
                <a-popconfirm title="确定删除该问卷吗?" @confirm="deleteQuestionnaire(item.questionnaire.id)">
                  <a-button type="link" danger>删除</a-button>
                </a-popconfirm>
              </div>
            </div>
          </div>
        </template>
        <template v-else>
            <div class="empty-container">
              <a-empty
                :image="h(FileSearchOutlined, { style: { fontSize: '64px', color: '#bfbfbf' } })"
                description="暂无问卷"
              >
                <template #extra>
                  <a-button type="primary" @click="openQuestionnaireEdit">添加问卷</a-button>
                </template>
              </a-empty>
            </div>
          </template>
      </a-tab-pane>

      <a-tab-pane key="2" tab="课程学生">
        <div class="content-header">
          <h3></h3>
          <a-button type="primary" @click="openStudentEdit">
            <template #icon><PlusOutlined /></template>
            添加学生
          </a-button>
        </div>
        <template v-if="studentList.length">
          <div class="grid-list">
              <div class="grid-item" v-for="(item, index) in studentList" :key="index">
                <div class="item-content">
                  <h4>{{ item.nickname }}</h4>
                  <p>{{ item.email }}</p>
                </div>
                <div class="item-footer">
                  <a-popconfirm title="确定删除该学生吗?" @confirm="deleteStudent(item.id)">
                    <a-button type="link" danger>删除</a-button>
                  </a-popconfirm>
                </div>
              </div>
          </div>
        </template>
        <template v-else>
            <div class="empty-container">
              <a-empty
                :image="h(TeamOutlined, { style: { fontSize: '64px', color: '#bfbfbf' } })"
                description="暂无学生"
              >
                <template #extra>
                  <a-button type="primary" @click="openStudentEdit">添加学生</a-button>
                </template>
              </a-empty>
            </div>
          </template>
      </a-tab-pane>
    </a-tabs>

    <question-preview
      v-if="showPreview"
      :data="previewData"
      @close="() => {
        showPreview = false;
        getQuestionnaireList();
      }"
    />

    <qEdit ref="qEditRef" @saveOk="getQuestionnaireList" />
    <sEdit ref="sEditRef" @saveOk="getStudentList" />
  </div>
</template>

<script setup>
import { ref, onMounted, h } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { message } from 'ant-design-vue';
import { ArrowLeftOutlined, PlusOutlined, FileSearchOutlined, TeamOutlined } from '@ant-design/icons-vue';
import { association, unbind, courseStudents, unbindstudent, publish, end, revoke } from '~/api/teacher/courses.js';
import qEdit from './components/qEdit.vue';
import sEdit from './components/sEdit.vue';
import QuestionPreview from './components/questionPreview.vue';

const route = useRoute();
const router = useRouter();
const courseId = ref(route.params.id);
const activeKey = ref('1');

const questionnaireList = ref([]);
const studentList = ref([]);
const qEditRef = ref(null);
const sEditRef = ref(null);

const showPreview = ref(false);
const previewData = ref({});

onMounted(() => {
  getQuestionnaireList();
  getStudentList();
});

const getQuestionnaireList = async () => {
  try {
    const { data } = await association(courseId.value);
    questionnaireList.value = data;
  } catch (error) {
    console.error(error);
  }
};

const getStudentList = async () => {
  try {
    const { data } = await courseStudents(courseId.value, {});
    studentList.value = data.records;
  } catch (error) {
    console.error(error);
  }
};

const deleteQuestionnaire = async (id) => {
  try {
    await unbind({ courseId: courseId.value, questionnaireId: id });
    message.success('删除成功');
    getQuestionnaireList();
  } catch (error) {
    console.error(error);
  }
};

const deleteStudent = async (id) => {
  try {
    await unbindstudent({ courseId: courseId.value, studentId: id });
    message.success('删除成功');
    getStudentList();
  } catch (error) {
    console.error(error);
  }
};

const openQuestionnaireEdit = () => {
  qEditRef.value.open(courseId.value, questionnaireList.value);
};

const openStudentEdit = () => {
  sEditRef.value.open(courseId.value, studentList.value);
};

const previewQuestionnaire = (item) => {
  previewData.value = item.questionnaire;
  showPreview.value = true;
};

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

const handlePublish = async (id) => {
  try {
    await publish({ courseId: courseId.value, questionnaireId: id });
    message.success('发布成功');
    getQuestionnaireList();
  } catch (error) {
    console.error(error);
  }
};

const handleEnd = async (id) => {
  try {
    await end({ courseId: courseId.value, questionnaireId: id });
    message.success('问卷已结束');
    getQuestionnaireList();
  } catch (error) {
    console.error(error);
  }
};

const handleRevoke = async (id) => {
  try {
    await revoke({ courseId: courseId.value, questionnaireId: id });
    message.success('问卷已撤回');
    getQuestionnaireList();
  } catch (error) {
    console.error(error);
  }
};
</script>

<style lang="less" scoped>
.box {
  padding: 24px;
  background: #fff;
  border-radius: 8px;
}

.content-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;

  h3 {
    margin: 0;
    font-size: 18px;
  }
}

.grid-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 20px;
}

.grid-item {
  background: #fff;
  border: 1px solid #f0f0f0;
  border-radius: 8px;
  padding: 16px;
  transition: all 0.3s;

  &:hover {
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  }

  .item-header {
    margin-bottom: 12px;
  }

  .item-content {
    display: flex;
    align-items: flex-start;
    justify-content: space-between;
    h4 {
      margin: 0 0 8px;
      font-size: 16px;
    }

    p {
      margin: 0;
      color: #666;
      font-size: 14px;
    }
  }

  .item-footer {
    margin-top: 16px;
    padding-top: 16px;
    border-top: 1px solid #f0f0f0;
    display: flex;
    justify-content: flex-end;
    gap: 8px;
    
    .ant-btn {
      padding: 4px 0;
      
      &:not(:last-child) {
        margin-right: 12px;
      }
    }
  }
}

.preview-container {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: #fff;
  z-index: 100;
  overflow-y: auto;
}

.empty-container {
  padding: 40px 0;
  display: flex;
  justify-content: center;
  align-items: center;
  border-radius: 8px;
  min-height: 300px;

  :deep(.ant-empty) {
    .ant-empty-image {
      height: auto;
      margin-bottom: 16px;
    }
    .ant-empty-description {
      color: #8c8c8c;
      font-size: 14px;
    }
    .ant-empty-footer {
      margin-top: 16px;
    }
  }
}
</style> 