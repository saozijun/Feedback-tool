/**
 * 列表
 * @param {*} data
 * @returns
 */
export const list = (data) => {
  return useGet("/questionnaires/page", data);
};

/**
 * 新增编辑
 * @param {*} data
 * @returns
 */
export const save = (data) => {
  return usePost("/questionnaires/save", data);
};

/**
 * 删除
 * @param {*} data
 * @returns
 */
export const del = (data) => {
  return usePost("/questionnaires/delete", data);
};

/**
 * 删除
 * @param {*} data
 * @returns
 */
export const allList = (data) => {
  return useGet("/questionnaires/list", data);
};

/**
 * 查看问卷填写情况
 * @param {*} data
 * @returns
 */
export const stats = (id) => {
  return useGet("/courseQuestionnaire/stats/" + id);
};

