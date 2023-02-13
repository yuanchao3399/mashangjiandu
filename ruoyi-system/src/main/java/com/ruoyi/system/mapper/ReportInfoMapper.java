package com.ruoyi.system.mapper;

import java.util.List;
import java.util.Map;

import com.ruoyi.system.domain.vo.ReportExcelVo;
import com.ruoyi.system.domain.ReportInfo;
import com.ruoyi.system.domain.ReportResult;
import com.ruoyi.system.domain.vo.ReportInfoVo;
import org.apache.ibatis.annotations.Param;

/**
 * 投诉/举报信息Mapper接口
 * 
 * @author yuanchao
 * @date 2022-07-19
 */
public interface ReportInfoMapper 
{
    /**
     * 查询投诉/举报信息
     * 
     * @param id 投诉/举报信息主键
     * @return 投诉/举报信息
     */
    public ReportInfo selectReportInfoById(String id);

    /**
     * 查询投诉/举报信息列表
     * 
     * @param reportInfoVo 投诉/举报信息
     * @return 投诉/举报信息集合
     */
    public List<ReportInfo> selectReportInfoList(ReportInfoVo reportInfoVo);

    /**
     * 查询投诉/举报信息列表
     *
     * @param reportInfoVo 投诉/举报信息
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
     * 删除投诉/举报信息
     * 
     * @param id 投诉/举报信息主键
     * @return 结果
     */
    public int deleteReportInfoById(String id);

    /**
     * 批量删除投诉/举报信息
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteReportInfoByIds(String[] ids);

    /**
     * 批量删除投诉/举报结果
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteReportResultByIds(String[] ids);
    
    /**
     * 批量新增投诉/举报结果
     * 
     * @param reportResultList 投诉/举报结果列表
     * @return 结果
     */
    public int batchReportResult(List<ReportResult> reportResultList);
    

    /**
     * 通过投诉/举报信息主键删除投诉/举报结果信息
     * 
     * @param id 投诉/举报信息ID
     * @return 结果
     */
    public int deleteReportResultById(String id);

    Map<String,Object> allNum();

    Map<String, Object> tsNum();

   List<Map<String,Object>> barNum();

    List<Map<String, Object>> homePieChartJb(@Param("startTime") String startTime, @Param("endTime")String endTime);
    List<Map<String, Object>> homePieChartTs(@Param("startTime") String startTime, @Param("endTime")String endTime);
    List<Map<String, Object>> homeLineChart();

    Map<String, Object> getNumByTime(@Param("startTime") String startTime, @Param("endTime")String endTime);

    Map<String, Object> getTsNumByTime(@Param("startTime") String startTime, @Param("endTime")String endTime);
    List<Map<String, Object>> homeYearBarChart();


}
