package com.ruoyi.system.mapper;

import java.util.List;
import com.ruoyi.system.domain.ReportResult;

/**
 * 投诉/举报结果Mapper接口
 * 
 * @author ruoyi
 * @date 2022-07-20
 */
public interface ReportResultMapper 
{
    /**
     * 查询投诉/举报结果
     * 
     * @param id 投诉/举报结果主键
     * @return 投诉/举报结果
     */
    public ReportResult selectReportResultById(String id);

    /**
     * 查询投诉/举报结果列表
     * 
     * @param reportResult 投诉/举报结果
     * @return 投诉/举报结果集合
     */
    public List<ReportResult> selectReportResultList(ReportResult reportResult);

    /**
     * 新增投诉/举报结果
     * 
     * @param reportResult 投诉/举报结果
     * @return 结果
     */
    public int insertReportResult(ReportResult reportResult);

    /**
     * 修改投诉/举报结果
     * 
     * @param reportResult 投诉/举报结果
     * @return 结果
     */
    public int updateReportResult(ReportResult reportResult);

    int revokeReportResultById(ReportResult reportResult);

    /**
     * 删除投诉/举报结果
     * 
     * @param id 投诉/举报结果主键
     * @return 结果
     */
    public int deleteReportResultById(String id);

    /**
     * 批量删除投诉/举报结果
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteReportResultByIds(String[] ids);



}
