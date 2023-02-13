package com.ruoyi.system.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

import java.util.Date;

/**
 * 投诉/举报结果对象 report_result
 * 
 * @author yuanchao
 * @date 2022-07-19
 */
public class ReportResult extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** id */
    private String id;

    /** 举报ID */
    @Excel(name = "举报ID")
    private String reportId;

    /** 流转过程（1纪检组  ，2监管处） */
    @Excel(name = "流转过程", readConverterExp = "1=纪检组,，=2监管处")
    private String process;

    /** 是否成功 */
    @Excel(name = "是否成功")
    private String isSuccess;

    /** 处理意见 */
    @Excel(name = "处理意见")
    private String opinion;

    /** 评价 */
    @Excel(name = "评价")
    private String evaluate;

    /** 转办人 */
    @Excel(name = "一级转办人")
    private String transferUserOne;

    /** 转办人 */
    @Excel(name = "一级转办时间")
    private Date transferTimeOne;

    @Excel(name = "二级转办人")
    private String transferUserTwo;

    @Excel(name = "二级转办时间")
    private Date transferTimeTwo;

    /** 修改人 */
    @Excel(name = "修改人")
    private String updateUser;

    /** spare1 */
    private String handResults;

    /** spare2 */
    private String handOpinion;

    /** spare3 */
    @Excel(name = "spare3")
    private String spare3;

    public void setId(String id) 
    {
        this.id = id;
    }

    public String getId() 
    {
        return id;
    }
    public void setReportId(String reportId) 
    {
        this.reportId = reportId;
    }

    public String getReportId() 
    {
        return reportId;
    }
    public void setProcess(String process) 
    {
        this.process = process;
    }

    public String getProcess() 
    {
        return process;
    }
    public void setIsSuccess(String isSuccess) 
    {
        this.isSuccess = isSuccess;
    }

    public String getIsSuccess() 
    {
        return isSuccess;
    }
    public void setOpinion(String opinion) 
    {
        this.opinion = opinion;
    }

    public String getOpinion() 
    {
        return opinion;
    }
    public void setEvaluate(String evaluate) 
    {
        this.evaluate = evaluate;
    }

    public String getEvaluate() 
    {
        return evaluate;
    }
    public void setUpdateUser(String updateUser) 
    {
        this.updateUser = updateUser;
    }

    public String getUpdateUser() 
    {
        return updateUser;
    }

    public String getTransferUserOne() {
        return transferUserOne;
    }

    public void setTransferUserOne(String transferUserOne) {
        this.transferUserOne = transferUserOne;
    }

    public Date getTransferTimeOne() {
        return transferTimeOne;
    }

    public void setTransferTimeOne(Date transferTimeOne) {
        this.transferTimeOne = transferTimeOne;
    }

    public String getTransferUserTwo() {
        return transferUserTwo;
    }

    public void setTransferUserTwo(String transferUserTwo) {
        this.transferUserTwo = transferUserTwo;
    }

    public Date getTransferTimeTwo() {
        return transferTimeTwo;
    }

    public void setTransferTimeTwo(Date transferTimeTwo) {
        this.transferTimeTwo = transferTimeTwo;
    }

    public String getHandResults() {
        return handResults;
    }

    public void setHandResults(String handResults) {
        this.handResults = handResults;
    }

    public String getHandOpinion() {
        return handOpinion;
    }

    public void setHandOpinion(String handOpinion) {
        this.handOpinion = handOpinion;
    }

    public void setSpare3(String spare3)
    {
        this.spare3 = spare3;
    }

    public String getSpare3() 
    {
        return spare3;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("reportId", getReportId())
            .append("process", getProcess())
            .append("isSuccess", getIsSuccess())
            .append("opinion", getOpinion())
            .append("evaluate", getEvaluate())
            .append("createTime", getCreateTime())
            .append("updateTime", getUpdateTime())
            .append("updateUser", getUpdateUser())
            .append("spare3", getSpare3())
            .toString();
    }
}
