package com.ruoyi.system.service;

import java.util.List;
import com.ruoyi.system.domain.ReportFile;

/**
 * 举报/投诉文件上传Service接口
 * 
 * @author ruoyi
 * @date 2022-07-22
 */
public interface IReportFileService 
{
    /**
     * 查询举报/投诉文件上传
     * 
     * @param id 举报/投诉文件上传主键
     * @return 举报/投诉文件上传
     */
    public ReportFile selectReportFileById(String id);

    /**
     * 查询举报/投诉文件上传列表
     * 
     * @param reportFile 举报/投诉文件上传
     * @return 举报/投诉文件上传集合
     */
    public List<ReportFile> selectReportFileList(ReportFile reportFile);

    /**
     * 新增举报/投诉文件上传
     * 
     * @param reportFile 举报/投诉文件上传
     * @return 结果
     */
    public int insertReportFile(ReportFile reportFile);

    /**
     * 修改举报/投诉文件上传
     * 
     * @param reportFile 举报/投诉文件上传
     * @return 结果
     */
    public int updateReportFile(ReportFile reportFile);

    /**
     * 批量删除举报/投诉文件上传
     * 
     * @param ids 需要删除的举报/投诉文件上传主键集合
     * @return 结果
     */
    public int deleteReportFileByIds(String[] ids);

    /**
     * 删除举报/投诉文件上传信息
     * 
     * @param id 举报/投诉文件上传主键
     * @return 结果
     */
    public int deleteReportFileById(String id);
}
