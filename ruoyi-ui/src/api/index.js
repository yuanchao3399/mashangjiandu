import request from '@/utils/request'

// 查询举报/投诉详情列表
export function getHomeNumAll (query) {
  return request({
    url: '/system/report/homeNumAll',
    method: 'get',
    params: query
  })
}
// 通过时间获取aside数据
export function getHomePieChart (data) {
  return request({
    url: '/system/report/homePieChart',
    method: 'post',
    data: data
  })
}
// 通过时间获取aside数据
export function getHomePieChartByTime (data) {
  return request({
    url: '/system/report/homePieChartByTime',
    method: 'post',
    data: data
  })
}

// 折线图
export function getHomeLineChart (query) {
  return request({
    url: '/system/report/homeLineChart ',
    method: 'get',
    params: query
  })
}
// 柱状图
export function getHomeBarChart (query) {
  return request({
    url: '/system/report/homeYearBarChart',
    method: 'get',
    params: query
  })
}