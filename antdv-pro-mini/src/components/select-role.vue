<template>
    <div class="selectRole">
        <div class="selectRole-title">欢迎使用学生反馈工具</div>
        <div class="selectRole-content">
            <div class="selectRole-content-item">
                <img src="/src/assets/images/student.png" alt="">
                <p>我是学生</p>
                <span>查看问卷和提交反馈</span>
                <a-button type="primary" block @click="setRole('student')">选择学生身份</a-button>
            </div>
            <div class="selectRole-content-item">
                <img src="/src/assets/images/teacher.png" alt="">
                <p>我是教师</p>
                <span>发布问卷和查看结果分析</span>
                <a-button type="primary" block  @click="setRole('teacher')">选择教师身份</a-button>
            </div>
        </div>
    </div>
</template>

<script setup>

const emits = defineEmits(['setRole'])
const setRole = (role) => {
    emits('setRole',role)
}
</script>

<style lang="less" scoped>
    .selectRole {
        flex: 1;
        padding: 40px 0;
        display: flex;
        flex-direction: column;
        align-items: center;
        justify-content: center;
        .selectRole-title{
            font-size: 38px;
            font-weight: bold;
            color: #252525;
            margin-bottom: 40px;
        }
        .selectRole-content{
            display: flex;
            justify-content: space-between;
            gap: 50px;
            .selectRole-content-item{
                img{
                    width: 300px;
                    height: 150px;
                    object-fit: cover;
                    margin-bottom: 20px;
                    border-radius: 10px;
                }
                p{
                    font-size: 22px;
                    font-weight: bold;
                    margin-bottom: 0;
                }
                span{
                    font-size: 16px;
                    margin: 20px 0;
                    color: #252525;
                }
                display: flex;
                flex-direction: column;
                justify-content: center;
                padding: 20px 30px;
                align-items: center;
                box-shadow: 1px 1px 10px rgba(0, 0, 0, 0.1);
                border-radius: 10px;
                background: #ffffff94;
                backdrop-filter: blur(10px);
            }
        }
    }
</style>