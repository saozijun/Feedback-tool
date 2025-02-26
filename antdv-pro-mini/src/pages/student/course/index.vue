<template>
  <div class="box">
    <a-row :gutter="[15, 0]" style="display: flex; justify-content: space-between;align-items: center;margin-bottom: 20px;">
      <a-col style="font-size: 22px; font-weight: bold; color: #252525;"> 我的课程 </a-col>
    </a-row>
    <div class="course-list">
      <div class="course-item" v-for="(v,i) in tableData" :key="i" @click="goDetail(v)">
        <p>{{ v.name }}</p>
        <span>点击查看详情</span>
        <div class="tips">
          <a-tooltip title="课程反馈">
            <div class="course-edit" @click.stop="open(v)">
              <ExceptionOutlined />
            </div>
          </a-tooltip>
        </div>
      </div>
    </div>
  </div>

  <!-- 添加课程反馈弹窗 -->
  <a-modal
    v-model:visible="feedbackVisible"
    title="课程反馈"
    @ok="handleFeedbackSubmit"
    @cancel="feedbackVisible = false"
    :confirmLoading="submitLoading"
  >
    <a-form :model="feedbackForm" :rules="rules" ref="feedbackFormRef" layout="vertical">
      <a-form-item label="课程评分" name="rating">
        <a-rate v-model:value="feedbackForm.rating" />
        <span class="ant-rate-text">{{ desc[feedbackForm.rating - 1] }}</span>
      </a-form-item>
      <a-form-item label="反馈内容" name="content">
        <a-textarea
          v-model:value="feedbackForm.content"
          :rows="4"
          placeholder="请输入您的课程反馈"
        />
      </a-form-item>
    </a-form>
  </a-modal>
</template>
<script setup>
import { PlusOutlined,ExceptionOutlined } from "@ant-design/icons-vue";
import { ref, onMounted } from "vue";
import { message } from "ant-design-vue";
import { parseTime } from "~/utils";
import { coursesStudent, submitCourseFeedback } from "~/api/student/courses.js";
import { useRouter } from "vue-router";
import { useUserStore } from '~@/stores/user'

const userStore = useUserStore()
const expand = ref(false);
const loading = ref(false);
const tableData = ref([]);
const courseId = ref(0);
const router = useRouter();

// 反馈相关数据
const desc = ref(['terrible', 'bad', 'normal', 'good', 'wonderful']);
const feedbackVisible = ref(false);
const submitLoading = ref(false);
const feedbackFormRef = ref();
const feedbackForm = ref({
  courseId: null,
  rating: null,
  content: ''
});

const rules = {
  rating: [{ required: true, message: '请对课程进行评分' }],
  content: [{ required: true, message: '请输入反馈内容' }]
};

onMounted(() => {
  getList();
});

const getList = async () => {
  loading.value = true;
  try {
    const { data } = await coursesStudent(userStore.userInfo.id);
    tableData.value = data;
  } catch (error) {
    console.log(error);
  } finally {
    loading.value = false;
  }
};

const goDetail = (course) => {
  router.push({
    path: `/student/course/detail/${course.id}`,
    query: { name: course.name }
  });
};

// 打开反馈弹窗
const open = (course) => {
  feedbackForm.value.courseId = course.id;
  feedbackVisible.value = true;
};

// 提交反馈
const handleFeedbackSubmit = async () => {
  try {
    await feedbackFormRef.value.validate();
    submitLoading.value = true;
    
    await submitCourseFeedback({
      courseId: feedbackForm.value.courseId,
      studentId: userStore.userInfo.id,
      rating: feedbackForm.value.rating,
      content: feedbackForm.value.content
    });
    
    message.success('反馈提交成功');
    feedbackVisible.value = false;
    
    // 重置表单
    feedbackForm.value = {
      courseId: null,
      rating: null,
      content: ''
    };
  } catch (error) {
    console.error(error);
  } finally {
    submitLoading.value = false;
  }
};
</script>

<style lang="less" scoped>
.box {
  height: calc(100vh - 170px);
  &:hover {
    &::-webkit-scrollbar-thumb {
      background-color: rgba(117, 117, 117, 0.184);
    }
  }

  &::-webkit-scrollbar {
    width: 0.3vw;
    background-color: rgba(0, 0, 0, 0);
  }

  &::-webkit-scrollbar-thumb {
    background-color: rgba(0, 0, 0, 0);
    border-radius: 50vh;
    transition: 0.3s all;

    &:hover {
      background-color: rgba(117, 117, 117, 0.34);
    }
  }
}
.course-list{
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
  .course-item{
    background: #fff;
    padding: 80px 30px;
    border-radius: 10px;
    text-align: center;
    display: flex;
    flex-direction: column;
    justify-content: center;
    cursor: pointer;
    transition: .3s all;
    position: relative;
    .tips{
      position: absolute;
      top: 20px;
      right: 20px;
      display: flex;
      align-items: center;
      gap: 10px;
      z-index: 11;
      transition: .3s all;
      .course-edit{
          cursor: pointer;
          width: 40px;
          height: 40px;
          display: flex;
          justify-content: center;
          background-color: #f3f3f3;
          align-items: center;
          border-radius: 6px;
          color: #000;
          transition: .3s all;
          &:hover{
            background-color: #ffffff;
            box-shadow: 1px 4px 12px rgba(0, 0, 0, 0.1);
            border-radius: 50px;
            color: #fff;
          }
      }
    }
    &:hover{
      box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
    }
    p{
      font-size: 26px;
    }
    span{
      font-size: 14px;
      color: #919191;
    }
  }
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
