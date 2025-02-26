/**
 * 查询学生关联课程
 * @param {*} data
 * @returns
 */
export const coursesStudent = (id) => {
  return useGet("/courses/student/" + id);
};

/**
 * 课程反馈
 * @param {*} data
 * @returns
 */
export const submitCourseFeedback = (data) => {
  return usePost("/courseFeedback/save", data);
};

/**
 * 提交问卷
 * @param {*} data 
 * @returns 
 */
export const fillin = (data) => {
  return usePost("/questionnaireResponses/save", data);
};

/**
 * 获取课程进行中和已结束的问卷
 * @param {*} data
 * @returns
 */
export const ongoing = (id, data) => {
  return useGet("/courseQuestionnaire/status/" + id, data);
};

/**
 * 查询学生的问卷答案
 * @param {*} data
 * @returns
 */
export const response = (data) => {
  return useGet("/courseQuestionnaire/response", data);
};

/**
 * 查询学生所有课程问卷
 * @param {*} data
 * @returns
 */
export const allQuestionnaires = (data) => {
  return useGet("/courseQuestionnaire/student/questionnaires", data);
};