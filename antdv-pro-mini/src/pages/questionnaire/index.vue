<template>
    <div class="box">
      <template v-if="showList">
        <a-card mb-4>
          <a-form :model="formModel">
            <a-row :gutter="[15, 0]">
              <a-col>
                <a-form-item name="name" label="问卷标题	">
                  <a-input
                    v-model:value="formModel.name"
                    placeholder="请输入问卷标题"
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
                    新增
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
              <a-button style="padding-left: 0" type="link" @click="showQuestions(record)">题目</a-button>
              <a-button style="padding: 0" type="link" @click="open(record)">编辑</a-button>
              <a-popconfirm title="确定删除吗?" @confirm="onDelete(record.id)">
                <a-button type="link" danger>删除</a-button>
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
        <Edit ref="editRef" @saveOk="getList"></Edit>
      </template>
      
      <question-list 
        v-else
        :data="currentRecord" 
        @back="showList = true"
      />
    </div>
  </template>
  <script setup>
  import { PlusOutlined } from "@ant-design/icons-vue";
  import { ref, onMounted, h } from "vue";
  import { message } from "ant-design-vue";
  import { parseTime } from "~/utils";
  import { list, del } from "~/api/teacher/questionnaires.js";
  import Edit from "./components/Edit.vue";
  import QuestionList from './components/questionList.vue'

  const editRef = ref(null);
  const expand = ref(false);
  const loading = ref(false);
  const tableData = ref([]);
  const total = ref(0);
  const formModel = ref({
    pageNum: 1,
    pageSize: 10,
    name: "",
  });
  
  const showList = ref(true)
  const currentRecord = ref({})
  
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
      const { data } = await list(formModel.value);
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

  const showQuestions = (record) => {
    currentRecord.value = record
    showList.value = false
  }

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
  