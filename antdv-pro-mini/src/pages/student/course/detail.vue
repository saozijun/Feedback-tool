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

    <a-tabs v-model:activeKey="activeKey" v-if="!showFillin && !showPreview">
      <a-tab-pane key="1" tab="课程问卷">
        <div class="content-header">
          <h3></h3>
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
              <div class="item-footer" v-if="item.status === 1">
                <a-button 
                  type="link"
                  v-if="!item.hasSubmitted"
                  @click="handleFillIn(item.questionnaire)"
                >
                  立即填写
                </a-button>
                <a-button 
                  type="link"
                  v-else
                  @click="handlePreview(item)"
                >
                  已填写 预览
                </a-button>
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
              </a-empty>
            </div>
          </template>
      </a-tab-pane>
    </a-tabs>

    <Fillin
      v-if="showFillin"
      :data="FillinData"
      :courseId="courseId"
      @close="() => {
        showFillin = false;
        getQuestionnaireList();
      }"
    />

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
import { ref, onMounted, h } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { message } from 'ant-design-vue';
import { ArrowLeftOutlined, PlusOutlined, FileSearchOutlined, TeamOutlined } from '@ant-design/icons-vue';
import { ongoing, response } from '~/api/student/courses.js';
import Fillin from './components/Fillin.vue';
import { useUserStore } from '~@/stores/user'
import Preview from './components/Preview.vue';

const userStore = useUserStore()
const route = useRoute();
const router = useRouter();
const courseId = ref(route.params.id);
const activeKey = ref('1');

const questionnaireList = ref([]);
const showFillin = ref(false);
const FillinData = ref({});
const showPreview = ref(false);
const previewData = ref({});

onMounted(() => {
  getQuestionnaireList();
});

const getQuestionnaireList = async () => {
  try {
    const { data } = await ongoing(courseId.value,{
      studentId: userStore.userInfo.id
    });
    questionnaireList.value = data.ongoing.concat(data.completed);
  } catch (error) {
    console.error(error);
  }
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

const handleFillIn = (questionnaire) => {
  FillinData.value = questionnaire;
  showFillin.value = true;
};

const handlePreview = async (item) => {
  // 获取该学生的问卷答案
  let { data } = await response({
    questionnaireId: item.questionnaire.id,
    courseId:courseId.value,
    studentId: userStore.userInfo.id
  });
  previewData.value = data;
  showPreview.value = true;
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