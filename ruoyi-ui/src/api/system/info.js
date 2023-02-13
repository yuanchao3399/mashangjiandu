import request from '@/utils/request'

// 查询举报/投诉详情列表
export function listInfo (query) {
  return request({
    url: '/system/report/list',
    method: 'get',
    params: query
  })
}

// 查询举报/投诉详情详细
export function getInfo (id) {
  return request({
    url: '/system/report/' + id,
    method: 'get'
  })
}

// 修改投诉/举报信息
export function updateInfo (data) {
  return request({
    url: '/system/report',
    method: 'put',
    data: data
  })
}

// 处理举报/投诉详情
export function submitReport (data) {
  return request({
    url: '/system/report/submitReport',
    method: 'post',
    data: data
  })
}

// 下派投诉信息
export function downInfo (id) {
  return request({
    url: '/system/report/downReport/' + id,
    method: 'post'
  })
}

// 投诉下拉数据
export function getDicAll () {
  return request({
    url: '/system/dict/data/type/msjdDictTypeAll',
    method: 'get'
  })
}

// 获取文件
export function fileListById (query) {
  return request({
    url: '/system/report/getFileListById',
    method: 'get',
    params: query
  })
}

// 举报table信息
export function complaintsList (query) {
  return request({
    url: '/system/complaints/list',
    method: 'get',
    params: query
  })
}
// 举报下拉数据
export function msjdjbAll (query) {
  return request({
    url: '/system/dict/data/type/msjdjbAll',
    method: 'get',
    params: query
  })
}

export function submitComplaints (data) {
  return request({
    url: '/system/complaints/submitComplaints',
    method: 'post',
    data: data
  })
}
//交办投诉信息
export function handOver (id) {
  return request({
    url: '/system/report/downReport2/' + id,
    method: 'post'
  })
}
// 办事中心办理
export function reportUserHand (data) {
  return request({
    url: '/system/report/userHand',
    method: 'post',
    data: data
  })
}
// 监管处审核
// export function getDicAll () {
//   return request({
//     url: '/system/report/userHand',
//     method: 'post'
//   })
// }