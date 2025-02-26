<template>
  <div class="fillin-container">
    <!-- 顶部导航栏 -->
    <div class="nav-bar">
      <a-button
        @click="$emit('close')"
        shape="circle"
        :icon="h(ArrowLeftOutlined)"
      ></a-button>
      <span class="nav-title">问卷填写</span>
    </div>

    <!-- 问卷内容 -->
    <div class="questionnaire-content">
      <div class="questionnaire-header">
        <h1 class="title">{{ questionnaire.title }}</h1>
        <p class="description">{{ questionnaire.description }}</p>
      </div>

      <div class="questions-list">
        <template v-if="questions.length">
          <div v-for="(question, index) in questions" :key="question.id" class="question-item">
            <!-- 题目头部 -->
            <div class="question-header">
              <span class="question-index">{{ index + 1 }}.</span>
              <span class="question-title">{{ question.title }}</span>
              <a-tag v-if="question.required" color="red">必答</a-tag>
            </div>

            <!-- 选择题选项 -->
            <div class="question-content">
              <!-- 选择题选项 -->
              <template v-if="['single', 'multiple', 'radio', 'checkbox', 'select'].includes(question.type)">
                <div class="options-container">
                  <!-- 普通单选 -->
                  <template v-if="question.type === 'single'">
                    <div class="option-list">
                      <div
                        v-for="(option, optionIndex) in question.options"
                        :key="option"
                        class="option-item"
                        :class="{ 'option-selected': answers[question.id] === option }"
                        @click="answers[question.id] = option"
                      >
                        <span class="option-dot">{{ String.fromCharCode(65 + optionIndex) }}</span>
                        <span class="option-text">{{ option }}</span>
                      </div>
                    </div>
                  </template>

                  <!-- 普通多选 -->
                  <template v-if="question.type === 'multiple'">
                    <div class="option-list">
                      <div
                        v-for="(option, optionIndex) in question.options"
                        :key="option"
                        class="option-item"
                        :class="{ 'option-selected': answers[question.id]?.includes(option) }"
                        @click="toggleMultipleOption(question.id, option)"
                      >
                        <span class="option-square">{{ String.fromCharCode(65 + optionIndex) }}</span>
                        <span class="option-text">{{ option }}</span>
                      </div>
                    </div>
                  </template>

                  <!-- Radio按钮 -->
                  <template v-if="question.type === 'radio'">
                    <a-radio-group v-model:value="answers[question.id]" style="flex-direction: row;">
                      <a-radio v-for="option in question.options" :key="option" :value="option">
                        {{ option }}
                      </a-radio>
                    </a-radio-group>
                  </template>

                  <!-- Checkbox按钮 -->
                  <template v-if="question.type === 'checkbox'">
                    <a-checkbox-group v-model:value="answers[question.id]" style="flex-direction: row;">
                      <a-checkbox v-for="option in question.options" :key="option" :value="option">
                        {{ option }}
                      </a-checkbox>
                    </a-checkbox-group>
                  </template>

                  <!-- 下拉选择 -->
                  <template v-if="question.type === 'select'">
                    <a-select
                      v-model:value="answers[question.id]"
                      style="width: 100%"
                      :placeholder="question.placeholder || '请选择'"
                    >
                      <a-select-option v-for="option in question.options" :key="option" :value="option">
                        {{ option }}
                      </a-select-option>
                    </a-select>
                  </template>
                </div>
              </template>

              <!-- 文本类输入 -->
              <template v-if="question.type === 'text'">
                <div class="input-container">
                  <a-input
                    v-model:value="answers[question.id]"
                    :placeholder="question.placeholder || '请输入'"
                  />
                </div>
              </template>

              <template v-if="question.type === 'textarea'">
                <div class="input-container">
                  <a-textarea
                    v-model:value="answers[question.id]"
                    :placeholder="question.placeholder || '请输入'"
                    :rows="4"
                  />
                </div>
              </template>

              <!-- 数字输入 -->
              <template v-if="question.type === 'number'">
                <div class="input-container">
                  <a-input-number
                    placeholder="请输入"
                    v-model:value="answers[question.id]"
                    :min="question.min"
                    :max="question.max"
                    :step="question.step"
                    style="width: 100%"
                  />
                </div>
              </template>

              <!-- 日期时间选择 -->
              <template v-if="question.type === 'date'">
                <div class="input-container">
                  <a-date-picker
                    v-model:value="answers[question.id]"
                    :format="'YYYY-MM-DD'"
                    :valueFormat="'YYYY-MM-DD'"
                    style="width: 100%"
                  />
                </div>
              </template>

              <template v-if="question.type === 'time'">
                <div class="input-container">
                  <a-time-picker
                    v-model:value="answers[question.id]"
                    :format="'HH:mm:ss'"
                    :valueFormat="'HH:mm:ss'"
                    style="width: 100%"
                  />
                </div>
              </template>
            </div>
          </div>
        </template>
      </div>

      <!-- 提交按钮 -->
      <div class="submit-container">
        <a-button type="primary" size="large" @click="handleSubmit" :loading="submitting">
          提交问卷
        </a-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, h } from 'vue'
import { ArrowLeftOutlined } from '@ant-design/icons-vue'
import { message } from 'ant-design-vue'
import { useUserStore } from '~@/stores/user'
import { fillin } from '~@/api/student/courses'
import { parseTime } from "~/utils";

const userStore = useUserStore()
const props = defineProps({
  data: {
    type: Object,
    required: true
  },
  courseId: {
    type: String || Number,
    required: true
  }
})

const questionnaire = ref(props.data)
const questions = ref([])
const answers = ref({})
const submitting = ref(false)

// 解析问卷数据
const parseQuestionnaireData = () => {
  try {
    if (props.data.questions) {
      questions.value = JSON.parse(props.data.questions)
      // 初始化答案对象
      questions.value.forEach(question => {
        if (['multiple', 'checkbox'].includes(question.type)) {
          answers.value[question.id] = []
        } else {
          answers.value[question.id] = undefined
        }
      })
    }
  } catch (error) {
    console.error('解析问卷数据失败:', error)
    questions.value = []
  }
}

// 验证答案
const validateAnswers = () => {
  const errors = []
  questions.value.forEach(question => {
    if (question.required) {
      const answer = answers.value[question.id]
      if (answer === undefined || answer === '' || (Array.isArray(answer) && answer.length === 0)) {
        errors.push(`第${question.index}题为必答题`)
      }
    }
  })
  return errors
}

// 提交问卷
const handleSubmit = async () => {
  const errors = validateAnswers()
  if (errors.length) {
    message.error(errors[0])
    return
  }

  submitting.value = true
  try {
    await fillin({
      questionnaireId: questionnaire.value.id,
      courseId: props.courseId,
      studentId: userStore.userInfo.id,
      answers: JSON.stringify(answers.value),
      submittedAt: new Date()
    })
    message.success('提交成功')
    emit('close')
  } catch (error) {
    console.error(error)
    message.error('提交失败')
  } finally {
    submitting.value = false
  }
}

// 添加多选题选项切换方法
const toggleMultipleOption = (questionId, option) => {
  if (!answers.value[questionId]) {
    answers.value[questionId] = []
  }
  const index = answers.value[questionId].indexOf(option)
  if (index === -1) {
    answers.value[questionId].push(option)
  } else {
    answers.value[questionId].splice(index, 1)
  }
}

onMounted(() => {
  parseQuestionnaireData()
})

const emit = defineEmits(['close'])
</script>

<style lang="less" scoped>
.fillin-container {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: #f5f5f5;
  z-index: 1000;
  overflow-y: auto;
}

.nav-bar {
  position: sticky;
  top: 0;
  display: flex;
  align-items: center;
  padding: 16px 24px;
  background: #fff;
  box-shadow: 0 2px 8px rgba(0,0,0,0.06);
  z-index: 100;

  .nav-title {
    margin-left: 16px;
    font-size: 18px;
    font-weight: 500;
  }
}

.questionnaire-content {
  max-width: 800px;
  margin: 24px auto;
  padding: 32px;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.06);

  .questionnaire-header {
    text-align: center;
    margin-bottom: 48px;

    .title {
      font-size: 28px;
      font-weight: bold;
      color: #262626;
      margin-bottom: 16px;
    }

    .description {
      font-size: 16px;
      color: #595959;
    }
  }
}

.question-item {
  margin-bottom: 32px;
  padding: 24px;
  background: #fafafa;
  border-radius: 8px;

  .question-header {
    margin-bottom: 16px;
    display: flex;
    align-items: center;
    gap: 8px;

    .question-index {
      font-weight: 500;
      color: #262626;
    }

    .question-title {
      flex: 1;
      font-size: 16px;
      color: #262626;
    }
  }
}

.options-container {
  padding: 8px 0;

  .option-list {
    display: flex;
    flex-direction: column;
    gap: 12px;

    .option-item {
      display: flex;
      align-items: center;
      padding: 8px 12px;
      border-radius: 4px;
      cursor: pointer;
      transition: all 0.3s;
      background-color: #fff;
      border: 1px solid #d9d9d9;
      
      &:hover {
        border-color: #40a9ff;
      }
      
      &.option-selected {
        background-color: #e6f4ff;
        border-color: #1890ff;
        
        .option-dot,
        .option-square {
          background-color: #1890ff;
          border-color: #1890ff;
          color: #fff;
        }
      }

      .option-dot,
      .option-square {
        width: 24px;
        height: 24px;
        margin-right: 8px;
        display: flex;
        align-items: center;
        justify-content: center;
        font-size: 14px;
        color: #595959;
        border: 1px solid #d9d9d9;
        transition: all 0.3s;
      }

      .option-dot {
        border-radius: 50%;
      }

      .option-square {
        border-radius: 4px;
      }

      .option-text {
        color: #262626;
        font-size: 14px;
      }
    }
  }
}

.input-container {
  padding: 8px 0;
  
  :deep(.ant-input),
  :deep(.ant-input-number),
  :deep(.ant-picker) {
    width: 100%;
  }
}

:deep(.ant-radio-group),
:deep(.ant-checkbox-group) {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.submit-container {
  margin-top: 48px;
  text-align: center;
}
</style> 