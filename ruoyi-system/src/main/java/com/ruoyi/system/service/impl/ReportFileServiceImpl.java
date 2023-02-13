package com.ruoyi.system.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.ReportFileMapper;
import com.ruoyi.system.domain.ReportFile;
import com.ruoyi.system.service.IReportFileService;

/**
 * 举报/投诉文件上传Service业务层处理
 * 
 * @author ruoyi
 * @date 2022-07-22
 */
@Service
public class ReportFileServiceImpl implements IReportFileService 
{
    @Autowired
    private ReportFileMapper reportFileMapper;

    /**
     * 查询举报/投诉文件上传
     * 
     * @param id 举报/投诉文件上传主键
     * @return 举报/投诉文件上传
     */
    @Override
    public ReportFile selectReportFileById(String id)
    {
        return reportFileMapper.selectReportFileById(id);
    }

    /**
     * 查询举报/投诉文件上传列表
     * 
     * @param reportFile 举报/投诉文件上传
     * @return 举报/投诉文件上传
     */
    @Override
    public List<ReportFile> selectReportFileList(ReportFile reportFile)
    {
        return reportFileMapper.selectReportFileList(reportFile);
    }

    /**
     * 新增举报/投诉文件上传
     * 
     * @param reportFile 举报/投诉文件上传
     * @return 结果
     */
    @Override
    public int insertReportFile(ReportFile reportFile)
    {
        reportFile.setCreateTime(DateUtils.getNowDate());
        return reportFileMapper.insertReportFile(reportFile);
    }

    /**
     * 修改举报/投诉文件上传
     * 
     * @param reportFile 举报/投诉文件上传
     * @return 结果
     */
    @Override
    public int updateReportFile(ReportFile reportFile)
    {
        return reportFileMapper.updateReportFile(reportFile);
    }

    /**
     * 批量删除举报/投诉文件上传
     * 
     * @param ids 需要删除的举报/投诉文件上传主键
     * @return 结果
     */
    @Override
    public int deleteReportFileByIds(String[] ids)
    {
        return reportFileMapper.deleteReportFileByIds(ids);
    }

    /**
     * 删除举报/投诉文件上传信息
     * 
     * @param id 举报/投诉文件上传主键
     * @return 结果
     */
    @Override
    public int deleteReportFileById(String id)
    {
        return reportFileMapper.deleteReportFileById(id);
    }
}
