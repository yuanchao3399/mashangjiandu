package com.ruoyi.system.domain.vo;

import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import com.ruoyi.system.domain.ReportInfo;
import com.ruoyi.system.domain.ReportResult;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.cglib.beans.BeanMap;

import java.util.List;
import java.util.Map;

/**
 * 投诉/举报信息对象 report_info_VO
 * 
 * @author yuanchao
 * @date 2022-07-19
 */
public class ReportInfoVo
{
    private static final long serialVersionUID = 1L;

    private String id;

    /** 服务类别 */
//    @Excel(name = "服务类别")
    private String serviceType;


//    @Excel(name = "举报/投诉问题")
    private String detailInfo;

    /** 举报类型 */
//    @Excel(name = "举报类型")
    private String type;

    private String isSuccess;

    private String roleCode;

    private String  opinion;


    private  Map<String, Object> params;

    public Map<String, Object> getParams() {
        return params;
    }

    public void setParams(Map<String, Object> params) {
        this.params = params;
    }

    private String startTime;
    private String createTime;

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    private String endTime;

    private String evaluate;

    private String content;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public String getId()
    {
        return id;
    } public void setServiceType(String serviceType)
    {
        this.serviceType = serviceType;
    }

    public String getServiceType()
    {
        return serviceType;
    }
    public void setType(String type)
    {
        this.type = type;
    }

    public String getType() 
    {
        return type;
    }

    public String getDetailInfo() {
        return detailInfo;
    }

    public void setDetailInfo(String detailInfo) {
        this.detailInfo = detailInfo;
    }

    public String getIsSuccess() {
        return isSuccess;
    }

    public void setIsSuccess(String isSuccess) {
        this.isSuccess = isSuccess;
    }

    public String getRoleCode(){
            return roleCode ;
    }
    public void setRoleCode(String roleCode){
            this.roleCode = roleCode;
    }
    public String getOpinion(){
            return opinion ;
    }
    public void setOpinion(String opinion){
            this.opinion = opinion;
    }

    public String getStartTime(){
        return startTime ;
    }
    public void setStartTime(String startTime){
        this.startTime = startTime;
    }

    public String getEvaluate() {
        return evaluate;
    }

    public void setEvaluate(String evaluate) {
        this.evaluate = evaluate;
    }

    @Override
    public String toString() {
        return "ReportInfoVo{" +
                "id='" + id + '\'' +
                ", serviceType='" + serviceType + '\'' +
                ", detailInfo='" + detailInfo + '\'' +
                ", type='" + type + '\'' +
                ", isSuccess='" + isSuccess + '\'' +
                ", roleCode='" + roleCode + '\'' +
                ", opinion='" + opinion + '\'' +
                ", startTime='" + startTime + '\'' +
                ", endTime='" + endTime + '\'' +
                ", evaluate='" + evaluate + '\'' +
                ", content='" + content + '\'' +
                '}';
    }

    public String getEndTime(){
        return endTime ;
    }
    public void setEndTime(String endTime){
        this.endTime = endTime;
    }

    public static ReportInfoVo mapToInfo(Map<String, Object> params, ReportInfoVo infoVo){
        BeanMap beanMap=BeanMap.create(infoVo);
        beanMap.putAll(params);
        return infoVo;
    }


}
