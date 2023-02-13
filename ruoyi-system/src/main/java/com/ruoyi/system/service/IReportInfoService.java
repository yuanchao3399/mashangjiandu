package com.ruoyi.system.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.system.domain.ReportFile;
import com.ruoyi.system.domain.vo.ReportExcelVo;
import com.ruoyi.system.domain.ReportInfo;
import com.ruoyi.system.domain.vo.ReportInfoVo;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 投诉/举报信息Service接口
 * 
 * @author yuanchao
 * @date 2022-07-19
 */
public interface IReportInfoService 
{
    /**
     * 查询投诉/举报信息
     * 
     * @param id 投诉/举报信息主键
     * @return 投诉/举报信息
     */
    public ReportInfo selectReportInfoById(String id);

    AjaxResult selectReportInfoVoById(String id);
    /**
     * 查询投诉/举报信息列表
     * 
     * @param reportInfo 投诉/举报信息
     * @return 投诉/举报信息集合
     */
    public List<ReportInfo> selectReportInfoList(ReportInfoVo reportInfoVo);

    /**
     * 查询投诉/举报信息列表
     *
     * @param reportInfo 投诉/举报信息
     * @return 投诉/举报信息集合
     */
    public List<ReportExcelVo> selectReportExcel(ReportInfoVo reportInfoVo);


    /**
     * 新增投诉/举报信息
     *
     * @param reportInfo 投诉/举报信息
     * @return 结果
     */
    public int insertReportInfo(ReportInfo reportInfo);

    /**
     * 修改投诉/举报信息
     * 
     * @param reportInfo 投诉/举报信息
     * @return 结果
     */
    public int updateReportInfo(ReportInfo reportInfo);

    /**
     * 批量删除投诉/举报信息
     * 
     * @param ids 需要删除的投诉/举报信息主键集合
     * @return 结果
     */
    public int deleteReportInfoByIds(String[] ids);

    /**
     * 删除投诉/举报信息信息
     * 
     * @param id 投诉/举报信息主键
     * @return 结果
     */
    public int deleteReportInfoById(String id);

    /**
     * 纪检组下派案件到监管处
     */
    int downReport(String id,Long userId);


    /**
     * 监管处下派案件到事务中心
     */
    int downReport2(String id,Long userId);

    int submitReport(String id,String opinion, Long userId);

    int userEvaluate(String id, String evaluate,String type);


    int userHand(String id,String opinion,Long userId);



    int examine(String id);

    int revoke(String id,String process);


/*    int revoke2(String id);*/
    Map<String,Object> homeNumAll(SysUser user);


    Map<String,Object>  barNum();

    Map<String,Object> homePieChart(String startTime, String endTime,SysUser user);

    Map<String,Object> homeLineChart(SysUser user);


    List<ReportFile> getFileListById(String id);

    Map<String,Object> homeYearBarChart();

    void exportStatistics(HttpServletRequest request, HttpServletResponse response, ReportInfoVo reportInfoVo);

    void exportDocx( HttpServletResponse response, String id) throws Exception;

}
