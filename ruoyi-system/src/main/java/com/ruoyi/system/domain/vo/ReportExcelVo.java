package com.ruoyi.system.domain.vo;

import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import com.ruoyi.system.domain.ReportResult;
import org.springframework.cglib.beans.BeanMap;

import java.util.List;
import java.util.Map;

/**
 * 投诉/举报信息对象 report_info
 * 
 * @author yuanchao
 * @date 2022-07-19
 */
public class ReportExcelVo extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** id */
    private String id;

    /** 服务类别 */
    @Excel(name = "服务类别")
    private String serviceType;

    /** 窗口号码 */
    @Excel(name = "窗口号码")
    private String windowNum;

    /** 举报/投诉内容 */
    @Excel(name = "举报或投诉内容")
    private String content;

    @Excel(name = "举报或投诉问题")
    private String detailInfo;

    /** 举报类型 */

    private String type;

    /** 附件ID */
  /*  @Excel(name = "附件ID")*/
    private String fileId;

    /** 是否实名（Y实名，N匿名） */
    @Excel(name = "是否实名", readConverterExp = "Y=实名,N=匿名")
    private String isReal;

    /** 真实姓名 */
    @Excel(name = "真实姓名")
    private String realName;

    /** 手机号 */
    @Excel(name = "手机号")
    private String phone;

    /** 流转过程（1纪检组  ，2监管处） */
    @Excel(name = "流转过程", readConverterExp = "1=纪检组,2=监管处")
    private String process;

    /** 是否成功 */
    @Excel(name = "是否成功",readConverterExp = "Y=是,N=否")
    private String isSuccess;

    /** 处理意见 */
    @Excel(name = "处理意见")
    private String opinion;

    /** 评价 */
    @Excel(name = "评价")
    private String evaluate;


    /** 投诉/举报结果信息 */
    private List<ReportResult> reportResultList;

    public void setId(String id) 
    {
        this.id = id;
    }

    public String getProcess() {
        return process;
    }

    public void setProcess(String process) {
        this.process = process;
    }

    public String getIsSuccess() {
        return isSuccess;
    }

    public void setIsSuccess(String isSuccess) {
        this.isSuccess = isSuccess;
    }
    public String getDetailInfo() {
        return detailInfo;
    }

    public void setDetailInfo(String detailInfo) {
        this.detailInfo = detailInfo;
    }

    public String getOpinion() {
        return opinion;
    }

    public void setOpinion(String opinion) {
        this.opinion = opinion;
    }

    public String getEvaluate() {
        return evaluate;
    }

    public void setEvaluate(String evaluate) {
        this.evaluate = evaluate;
    }

    public String getId()
    {
        return id;
    }
    public void setServiceType(String serviceType) 
    {
        this.serviceType = serviceType;
    }

    public String getServiceType() 
    {
        return serviceType;
    }
    public void setWindowNum(String windowNum) 
    {
        this.windowNum = windowNum;
    }

    public String getWindowNum() 
    {
        return windowNum;
    }
    public void setContent(String content) 
    {
        this.content = content;
    }

    public String getContent() 
    {
        return content;
    }
    public void setType(String type) 
    {
        this.type = type;
    }

    public String getType() 
    {
        return type;
    }
    public void setFileId(String fileId) 
    {
        this.fileId = fileId;
    }

    public String getFileId() 
    {
        return fileId;
    }
    public void setIsReal(String isReal) 
    {
        this.isReal = isReal;
    }

    public String getIsReal() 
    {
        return isReal;
    }
    public void setRealName(String realName) 
    {
        this.realName = realName;
    }

    public String getRealName() 
    {
        return realName;
    }
    public void setPhone(String phone) 
    {
        this.phone = phone;
    }

    public String getPhone() 
    {
        return phone;
    }


    public List<ReportResult> getReportResultList()
    {
        return reportResultList;
    }

    public void setReportResultList(List<ReportResult> reportResultList)
    {
        this.reportResultList = reportResultList;
    }

    public static ReportExcelVo mapToInfo(Map<String, Object> params, ReportExcelVo info){
        BeanMap beanMap=BeanMap.create(info);
        beanMap.putAll(params);
        return info;
    }

}
