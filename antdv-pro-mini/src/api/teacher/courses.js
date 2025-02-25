/**
 * 列表
 * @param {*} data
 * @returns
 */
export const list = (data) => {
  return useGet("/courses/page", data);
};
/**
 * 全部列表
 * @param {*} data
 * @returns
 */
export const allList = (data) => {
  return useGet("/courses/allList", data);
};
/**
 * 新增编辑
 * @param {*} data
 * @returns
 */
export const save = (data) => {
  return usePost("/courses/save", data);
};

/**
 * 删除
 * @param {*} data
 * @returns
 */
export const del = (data) => {
  return usePost("/courses/delete", data);
};

/**
 * 查看课程关联的问卷
 * @param {*} data
 * @returns
 */
export const association = (id) => {
  return useGet("/courseQuestionnaire/course/" + id );
};

/**
 * 保存问卷关联
 * @param {*} data
 * @returns
 */
export const bind = (data) => {
  return usePost("/courseQuestionnaire/bind/" + data.courseId + "?questionnaireIds=" + data.questionnaireIds);
};

/**
 * 取消问卷关联
 * @param {*} data
 * @returns
 */
export const unbind = (data) => {
  return usePost("/courseQuestionnaire/unbind", data);
};


/**
 * 查看课程关联的学生
 * @param {*} data
 * @returns
 */
export const courseStudents = (id, data) => {
  return useGet("/courseStudents/course/" + id, data);
};

/**
 * 保存学生关联
 * @param {*} data
 * @returns
 */
export const studentsBind = (data) => {
  return usePost("/courseStudents/bind", data);
};

/**
 * 取消学生关联
 * @param {*} data
 * @returns
 */
export const unbindstudent = (data) => {
  return usePost("/courseStudents/unbind", data);
};

/**
 * 学生列表
 * @param {*} data
 * @returns
 */
export const studentList = (data) => {
  return useGet("/user/student/list", data);
};

/**
 * 发布问卷
 * @param {*} data
 * @returns
 */
export const publish = (data) => {
  return usePost("/courseQuestionnaire/publish", data);
};

/**
 * 结束问卷
 * @param {*} data
 * @returns
 */
export const end = (data) => {
  return usePost("/courseQuestionnaire/end", data);
};

/**
 * 撤回问卷
 * @param {*} data
 * @returns
 */
export const revoke = (data) => {
  return usePost("/courseQuestionnaire/revoke", data);
};