<template>
  <div class="preview-container">
    <!-- 顶部导航栏 -->
    <a-row :gutter="[15, 0]" style="display: flex; align-items: center; position: sticky; top: 24px;">
        <a-col>
          <a-button
            @click="$emit('close')"
            shape="circle"
            :icon="h(ArrowLeftOutlined)"
          ></a-button>
        </a-col>
    </a-row>

    <!-- 问卷内容 -->
    <a-card class="questionnaire-content">
      <div class="questionnaire-header">
        <h2 class="questionnaire-title">{{ questionnaire.title }}</h2>
        <p class="questionnaire-desc">{{ questionnaire.description }}</p>
      </div>

      <div class="questions-list">
        <template v-if="questions.length">
          <div v-for="(question, index) in questions" :key="question.id" class="question-item">
            <!-- 题目头部 -->
            <div class="question-header">
              <span class="question-index">{{ index + 1 }}.</span>
              <span class="question-title">{{ question.title }}</span>
              <a-tag color="blue" class="question-type">{{ getQuestionTypeLabel(question.type) }}</a-tag>
              <a-tag v-if="question.required" color="red" class="required-tag">必答</a-tag>
            </div>

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
                    >
                      <span class="option-square">{{ String.fromCharCode(65 + optionIndex) }}</span>
                      <span class="option-text">{{ option }}</span>
                    </div>
                  </div>
                </template>

                <!-- Radio按钮 -->
                <template v-if="question.type === 'radio'">
                  <a-radio-group v-model:value="answers[question.id]" :disabled="true" style="flex-direction: row;">
                    <a-radio v-for="option in question.options" :key="option" :value="option">
                      {{ option }}
                    </a-radio>
                  </a-radio-group>
                </template>

                <!-- Checkbox按钮 -->
                <template v-if="question.type === 'checkbox'">
                  <a-checkbox-group v-model:value="answers[question.id]" :disabled="true" style="flex-direction: row;">
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
                    :disabled="true"
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
                  :disabled="true"
                  :placeholder="question.placeholder || '请输入'"
                  class="disabled-input"
                />
              </div>
            </template>

            <template v-if="question.type === 'textarea'">
              <div class="input-container">
                <a-textarea
                  v-model:value="answers[question.id]"
                  :disabled="true"
                  :placeholder="question.placeholder || '请输入'"
                  :rows="4"
                  class="disabled-input"
                />
              </div>
            </template>

            <!-- 数字输入 -->
            <template v-if="question.type === 'number'">
              <div class="input-container">
                <a-input-number
                  v-model:value="answers[question.id]"
                  :disabled="true"
                  :min="question.min"
                  :max="question.max"
                  :step="question.step"
                  class="disabled-input"
                  style="width: 100%"
                />
              </div>
            </template>

            <!-- 日期时间选择 -->
            <template v-if="question.type === 'date'">
              <div class="input-container">
                <a-date-picker
                  v-model:value="answers[question.id]"
                  :disabled="true"
                  :format="question.dateFormat || 'YYYY-MM-DD'"
                  class="disabled-input"
                />
              </div>
            </template>

            <template v-if="question.type === 'time'">
              <div class="input-container">
                <a-time-picker
                  v-model:value="answers[question.id]"
                  :disabled="true"
                  :format="question.timeFormat || 'HH:mm:ss'"
                  class="disabled-input"
                  style="width: 100%"
                />
              </div>
            </template>
          </div>
        </template>
        <a-empty v-else description="暂无题目" />
      </div>
    </a-card>
  </div>
</template>

<script setup>
import { ref, h } from 'vue'
import { ArrowLeftOutlined } from '@ant-design/icons-vue'

// 定义题目类型常量
const questionTypes = [
  { label: '单选题', value: 'single' },
  { label: '多选题', value: 'multiple' },
  { label: '单选框', value: 'radio' },
  { label: '多选框', value: 'checkbox' },
  { label: '填空框', value: 'text' },
  { label: '文本域', value: 'textarea' },
  { label: '下拉选择', value: 'select' },
  { label: '日期选择', value: 'date' },
  { label: '时间选择', value: 'time' },
  { label: '数字输入', value: 'number' }
]

const props = defineProps({
  data: {
    type: Object,
    required: true
  }
})

const questionnaire = ref(props.data)
const questions = ref([])
const answers = ref({})

// 解析问卷数据
const parseQuestionnaireData = () => {
  try {
    if (props.data.questions) {
      questions.value = JSON.parse(props.data.questions)
      // 初始化答案对象
      questions.value.forEach(question => {
        // 根据题目类型初始化答案
        if (['checkbox', 'checkbox-button'].includes(question.type)) {
          answers.value[question.id] = []
        } else if (question.type === 'date') {
          answers.value[question.id] = null
        } else if (question.type === 'time') {
          answers.value[question.id] = null
        } else if (question.type === 'number') {
          answers.value[question.id] = null
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

// 获取题目类型的中文标签
const getQuestionTypeLabel = (type) => {
  const questionType = questionTypes.find(item => item.value === type)
  return questionType ? questionType.label : type
}

parseQuestionnaireData()

defineEmits(['close'])
</script>

<style lang="less" scoped>
.preview-container {
  min-height: 100vh;
  background-color: #f0f2f5;
  padding: 24px;
}

.questionnaire-content {
  max-width: 800px;
  margin: 0 auto;
}

.questionnaire-header {
  text-align: center;
  margin-bottom: 40px;
  
  .questionnaire-title {
    font-size: 28px;
    font-weight: bold;
    margin-bottom: 16px;
    color: #262626;
  }

  .questionnaire-desc {
    font-size: 14px;
    color: #8c8c8c;
  }
}

.questions-list {
  .question-item {
    margin-bottom: 24px;
    padding: 24px;
    background: #fafafa;
    border-radius: 8px;
    box-shadow: 0 1px 2px rgba(0, 0, 0, 0.03);

    .question-header {
      margin-bottom: 20px;
      display: flex;
      align-items: center;
      gap: 8px;

      .question-index {
        font-weight: 600;
        color: #262626;
      }

      .question-title {
        flex: 1;
        font-size: 16px;
        color: #262626;
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
          cursor: not-allowed;
          transition: all 0.3s;
          background-color: #e9e9e9;
          
          &.option-selected {
            background-color: #e6f4ff;
            
            .option-dot,
            .option-square {
              background-color: #1890ff;
              border-color: #1890ff;
            }
          }

          .option-dot,
          .option-square {
            width: 24px;
            height: 24px;
            margin-right: 8px;
            position: relative;
            display: flex;
            align-items: center;
            justify-content: center;
            font-size: 14px;
            color: #595959;
            &::after {
              display: none;
            }
          }

          .option-dot {
            border-radius: 50%;
          }

          .option-square {
            border-radius: 4px;
          }

          .option-item.option-selected {
            background-color: #e6f4ff;
            
            .option-dot,
            .option-square {
              background-color: #1890ff;
              border-color: #1890ff;
              color: #fff;
            }
          }

          .option-text {
            color: #262626;
            font-size: 14px;
          }
        }
      }

      :deep(.ant-radio-group),
      :deep(.ant-checkbox-group) {
        display: flex;
        flex-direction: column;
        gap: 12px;
      }
    }

    .input-container {
      padding: 8px 0;
      
      .disabled-input {
        background-color: #e9e9e9;
        cursor: not-allowed;
      }
    }

    :deep(.ant-input),
    :deep(.ant-input-number),
    :deep(.ant-picker) {
      width: 100%;
    }

    :deep(.ant-radio-wrapper),
    :deep(.ant-checkbox-wrapper) {
      font-size: 14px;
      color: #595959;
    }
  }
}
</style> 