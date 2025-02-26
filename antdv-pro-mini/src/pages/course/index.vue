<template>
  <div class="box" v-if="!showQuestionnaire && !showStudent">
    <a-row :gutter="[15, 0]" style="display: flex; justify-content: space-between;align-items: center;margin-bottom: 20px;">
      <a-col style="font-size: 22px; font-weight: bold; color: #252525;"> 我的课程 </a-col>
      <a-col>
        <a-space flex justify-end w-full>
          <a-button type="primary" @click="open">
            <template #icon>
              <PlusOutlined />
            </template>
            添加课程
          </a-button>
        </a-space>
      </a-col>
    </a-row>
    <div class="course-list">
      <div class="course-item" v-for="(v,i) in tableData" :key="i" @click="goDetail(v)">
        <div class="tips">
          <div class="course-edit" @click.stop="open(v)">
            <EditOutlined />
          </div>
          <a-popconfirm title="确定删除该课程吗?" @confirm="onDelete(v.id)">
            <div class="course-del" @click.stop>
              <DeleteOutlined />
            </div>
          </a-popconfirm>
        </div>
        <p>{{ v.name }}</p>
        <span>点击查看详情</span>
      </div>
    </div>
    <Edit ref="editRef" @saveOk="getList"></Edit>
  </div>
  <QuestionnaireList
    v-if="showQuestionnaire"
    ref="questionnaireListRef"
    :courseId="courseId"
    @back="showQuestionnaire = false"
  ></QuestionnaireList>
  <Student
    v-if="showStudent"
    ref="studentRef"
    :courseId="courseId"
    @back="showStudent = false"
  ></Student>
</template>
<script setup>
import { PlusOutlined, EditOutlined, DeleteOutlined } from "@ant-design/icons-vue";
import { ref, onMounted } from "vue";
import { message } from "ant-design-vue";
import { parseTime } from "~/utils";
import { allList, del } from "~/api/teacher/courses.js";
import Edit from "./components/Edit.vue";
import QuestionnaireList from "./components/questionnaireList.vue";
import Student from "./components/Student.vue";
import { useRouter } from "vue-router";
import { useUserStore } from '~@/stores/user'

const userStore = useUserStore()
const editRef = ref(null);
const expand = ref(false);
const loading = ref(false);
const tableData = ref([]);
const courseId = ref(0);
const showQuestionnaire = ref(false);
const showStudent = ref(false);
const router = useRouter();

onMounted(() => {
  getList();
});

const getList = async () => {
  loading.value = true;
  try {
    const { data } = await allList({teacherId:userStore.userInfo.id});
    tableData.value = data;
  } catch (error) {
    console.log(error);
  } finally {
    loading.value = false;
  }
};

const onDelete = async (id) => {
  try {
    await del({ id });
    getList();
    message.success("删除成功");
  } catch (error) {
    console.log(error);
  }
};

const open = (record = {}) => {
  editRef.value.open(record);
};

const wjOpen = (record, type) => {
  courseId.value = record.id;
  if (type === "questionnaire") {
    showQuestionnaire.value = true;
  } else {
    showStudent.value = true;
  }
};

const goDetail = (course) => {
  router.push({
    path: `/course/detail/${course.id}`,
    query: { name: course.name }
  });
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
    box-shadow: 1px 1px 8px rgba(0, 0, 0, 0.05);
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
      // opacity: 0;
      // transform: scale(0);
      transition: .3s all;
      .course-edit, .course-del{
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
            background-color: #252525;
            color: #fff;
          }
      }
    }
    &:hover{
      box-shadow: 1px 1px 8px rgba(0, 0, 0, 0.1);
      .tips{
        opacity: 1;
        transform: scale(1);
      }
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
