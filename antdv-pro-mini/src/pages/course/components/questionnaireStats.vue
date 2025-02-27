<template>
  <div class="preview-container">
    <!-- 顶部导航栏 -->
    <a-row
      :gutter="[15, 0]"
      style="display: flex; align-items: center; position: sticky; top: 24px;padding: 0 24px;"
    >
      <a-col>
        <a-button
          @click="$emit('close')"
          shape="circle"
          :icon="h(ArrowLeftOutlined)"
        ></a-button>
      </a-col>
      <a-col
        style="
          font-size: 18px;
          font-weight: bold;
          color: #252525;
          margin-left: 10px;
        "
      >
        问卷填写详情
      </a-col>
    </a-row>

    <!-- 问卷基本信息 -->
    <a-card class="questionnaire-content" style="margin-bottom: 20px">
      <h2>{{ data.questionnaire.title }}</h2>
      <p class="description">{{ data.questionnaire.description }}</p>
      <div class="stats-info">
        <div class="stat-item">
          <span class="label">总人数：</span>
          <span class="value">{{ data.totalStudents }}</span>
        </div>
        <div class="stat-item">
          <span class="label">已填写：</span>
          <span class="value">{{ data.submittedCount }}</span>
        </div>
        <div class="stat-item">
          <span class="label">完成率：</span>
          <span class="value"
            >{{
              ((data.submittedCount / data.totalStudents) * 100).toFixed(1)
            }}%</span
          >
        </div>
      </div>
    </a-card>

    <!-- 填写列表 -->
    <a-card class="questionnaire-content" title="填写列表">
      <template v-if="data.submissions.length">
        <div class="submission-list">
          <div
            v-for="(item, index) in data.submissions"
            :key="index"
            class="submission-item"
          >
            <div class="student-info">
              <img
                :src="item.student.avatarUrl || '/src/assets/images/avatar.png'"
                alt="avatar"
              />
              <span class="student-name">{{ item.student.nickname }}</span>
            </div>
            <div class="submission-actions">
              <span class="submission-time">{{
                parseTime(item.response.submittedAt, "{y}-{m}-{d} {h}:{i}")
              }}</span>
              <a-button type="link" @click="handlePreview(item)"
                >查看答案</a-button
              >
            </div>
          </div>
        </div>
      </template>
      <template v-else>
        <a-empty description="暂无填写记录" />
      </template>
    </a-card>
    <!-- 预览弹窗 -->
    <Preview
      v-if="showPreview"
      :data="previewData"
      @close="() => (showPreview = false)"
    />
  </div>
</template>

<script setup>
import { ref, h } from "vue";
import { ArrowLeftOutlined } from "@ant-design/icons-vue";
import { parseTime } from "~/utils";
import Preview from "./Preview.vue";
import { response } from '~/api/student/courses.js';
const props = defineProps({
  data: {
    type: Object,
    required: true,
  },
  courseId: {
    type: Number || String,
    default: 0,
  },
});
const showPreview = ref(false);
const previewData = ref({});

const handlePreview = async (item) => {
  // 获取该学生的问卷答案
  let { data } = await response({
    questionnaireId: item.response.questionnaireId,
    courseId: item.response.courseId,
    studentId: item.response.studentId,
  });
  previewData.value = data;
  showPreview.value = true;
};

defineEmits(["close"]);
</script>

<style lang="less" scoped>
.preview-container {
  width: 100%;
  overflow: hidden;
  min-height: 100vh;
  background-color: #f0f2f5;
}

.questionnaire-content {
  max-width: 800px;
  margin: 24px auto;

  h2 {
    margin: 0 0 8px;
    color: #262626;
  }

  .description {
    color: #595959;
    margin-bottom: 24px;
  }
}

.stats-info {
  display: flex;
  gap: 48px;
  padding: 16px 0;
  border-top: 1px solid #f0f0f0;

  .stat-item {
    .label {
      color: #8c8c8c;
      margin-right: 8px;
    }
    .value {
      font-size: 16px;
      font-weight: 500;
      color: #262626;
    }
  }
}

.submission-list {
  .submission-item {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 16px 0;
    border-bottom: 1px solid #f0f0f0;

    &:last-child {
      border-bottom: none;
    }

    .student-info {
      display: flex;
      align-items: center;
      gap: 12px;

      img {
        width: 40px;
        height: 40px;
        border-radius: 50%;
        object-fit: cover;
      }

      .student-name {
        font-weight: 500;
        color: #262626;
      }
    }

    .submission-actions {
      display: flex;
      align-items: center;
      gap: 16px;

      .submission-time {
        color: #8c8c8c;
        font-size: 14px;
      }
    }
  }
}
</style>
