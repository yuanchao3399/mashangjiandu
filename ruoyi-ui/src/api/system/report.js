import request from '@/utils/request'

// 查询投诉/举报信息列表
export function listReport(query) {
  return request({
    url: '/system/report/list',
    method: 'get',
    params: query
  })
}

// 查询投诉/举报信息详细
export function getReport(id) {
  return request({
    url: '/system/report/' + id,
    method: 'get'
  })
}

// 新增投诉/举报信息
export function addReport(data) {
  return request({
    url: '/system/report',
    method: 'post',
    data: data
  })
}

// 修改投诉/举报信息
export function updateReport(data) {
  return request({
    url: '/system/report',
    method: 'put',
    data: data
  })
}

// 删除投诉/举报信息
export function delReport(id) {
  return request({
    url: '/system/report/' + id,
    method: 'delete'
  })
}
