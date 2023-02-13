package com.ruoyi.system.domain;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import cn.hutool.core.bean.BeanUtil;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.utils.bean.BeanUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import org.springframework.cglib.beans.BeanMap;

/**
 * 投诉/举报信息对象 report_info
 * 
 * @author yuanchao
 * @date 2022-07-19
 */
public class ReportInfo extends BaseEntity
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
    @Excel(name = "投诉内容")
    private String content;

    @Excel(name = "投诉问题")
    private String detailInfo;

    /** 举报类型 */
    @Excel(name = "举报类型")
    private String type;

    /** 附件ID */
    @Excel(name = "附件ID")
    private String fileId;

    /** 是否实名（Y实名，N匿名） */
    @Excel(name = "是否实名", readConverterExp = "Y=实名,N=匿名")
    private String isReal;

    /** 真实姓名 */
    @Excel(name = "真实姓名")
    private String realName;

    public String getDetailInfo() {
        return detailInfo;
    }

    public void setDetailInfo(String detailInfo) {
        this.detailInfo = detailInfo;
    }


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
    @Excel(name = "转办人")
    private String transferUser;

    /** 转办人 */
    @Excel(name = "转办时间")
    private Date transferTime;

    /** 手机号 */
    @Excel(name = "手机号")
    private String phone;

    /** spare1 */
    @Excel(name = "idCard")
    private String idCard;

    /** spare2 */
    @Excel(name = "politicsFace")
    private String politicsFace;

    /** spare3 */
    /*@Excel(name = "spare3")*/
    private String spare3;
    @Excel(name = "politicsFace")


    private Long completionTime;



    /** 投诉/举报结果信息 */
    private List<ReportResult> reportResultList;

    public void setId(String id) 
    {
        this.id = id;
    }

    public String getId() 
    {
        return id;
    }

    public Long getCompletionTime() {
        return completionTime;
    }

    public void setCompletionTime(Long completionTime) {
        this.completionTime = completionTime;
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
    public void setSpare3(String spare3)
    {
        this.spare3 = spare3;
    }

    public String getSpare3() 
    {
        return spare3;
    }

    public List<ReportResult> getReportResultList()
    {
        return reportResultList;
    }

    public void setReportResultList(List<ReportResult> reportResultList)
    {
        this.reportResultList = reportResultList;
    }


    public String getIdCard() {
        return idCard;
    }

    public void setIdCard(String idCard) {
        this.idCard = idCard;
    }

    public String getPoliticsFace() {
        return politicsFace;
    }

    public void setPoliticsFace(String politicsFace) {
        this.politicsFace = politicsFace;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("serviceType", getServiceType())
            .append("windowNum", getWindowNum())
            .append("content", getContent())
            .append("detailInfo", getDetailInfo())
            .append("type", getType())
            .append("fileId", getFileId())
            .append("isReal", getIsReal())
            .append("realName", getRealName())
            .append("phone", getPhone())
            .append("createTime", getCreateTime())
            .append("updateTime", getUpdateTime())
            .append("idCard", getIdCard())
            .append("politicsFace", getPoliticsFace())
            .append("spare3", getSpare3())
            .append("reportResultList", getReportResultList())
            .toString();
    }

    public static ReportInfo mapToInfo(Map<String, Object> params,ReportInfo info){
        BeanMap beanMap=BeanMap.create(info);
        beanMap.putAll(params);
        return info;
    }

}
