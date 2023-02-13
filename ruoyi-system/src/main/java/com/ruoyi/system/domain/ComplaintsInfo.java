package com.ruoyi.system.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import org.springframework.cglib.beans.BeanMap;

import java.util.Map;

/**
 * 举报信息对象 complaints_info
 * 
 * @author ruoyi
 * @date 2022-08-04
 */
public class ComplaintsInfo extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 举报id */
    private String id;

    /** 被举报人 */
    @Excel(name = "被举报人")
    private String reprotPerson;

    /** 举报单位 */
    @Excel(name = "举报单位")
    private String reportCompany;

    /** 举报单位 */
    @Excel(name = "举报标题")
    private String title;

    /** 被举报人级别 */
    @Excel(name = "被举报人级别")
    private String level;

    /** 职务 */
    @Excel(name = "职务")
    private String post;

    /** 问题类别 */
    @Excel(name = "问题类别")
    private String problemType;

    /** 主要问题 */
    @Excel(name = "主要问题")
    private String content;

    /** 是否实名（Y实名，N匿名） */
    @Excel(name = "是否实名", readConverterExp = "Y=实名,N=匿名")
    private String isReal;

    /** 真实姓名 */
    @Excel(name = "真实姓名")
    private String realName;

    /** 手机号 */
    @Excel(name = "手机号")
    private String phone;

    /** 是否成功（Y已办结，N办理中） */
    @Excel(name = "是否成功", readConverterExp = "Y=已办结,N=办理中")
    private String isSuccess;

    /** 处理意见 */
    @Excel(name = "处理意见")
    private String opinion;

    /** 评价 */
    @Excel(name = "评价")
    private String evaluate;


    /** 处理人 */

    private String updateUser;




    /** 身份证号 */
    @Excel(name = "身份证号")
    private String idCard;

    /** 政治面貌 */
    @Excel(name = "政治面貌")
    private String politicsFace;




    private Long completionTime;

    /** $column.columnComment */
//    @Excel(name = "${comment}")
    private String spare3;

    private String startTime;



    public Long getCompletionTime() {
        return completionTime;
    }

    public void setCompletionTime(Long completionTime) {
        this.completionTime = completionTime;
    }
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    private String endTime;

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }



    public void setId(String id) 
    {
        this.id = id;
    }

    public String getId() 
    {
        return id;
    }
    public void setReprotPerson(String reprotPerson) 
    {
        this.reprotPerson = reprotPerson;
    }

    public String getReprotPerson() 
    {
        return reprotPerson;
    }
    public void setReportCompany(String reportCompany) 
    {
        this.reportCompany = reportCompany;
    }

    public String getReportCompany() 
    {
        return reportCompany;
    }
    public void setPost(String post) 
    {
        this.post = post;
    }

    public String getPost() 
    {
        return post;
    }
    public void setProblemType(String problemType) 
    {
        this.problemType = problemType;
    }

    public String getProblemType() 
    {
        return problemType;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUpdateUser() {
        return updateUser;
    }

    public void setUpdateUser(String updateUser) {
        this.updateUser = updateUser;
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

    public void setSpare3(String spare3)
    {
        this.spare3 = spare3;
    }

    public String getSpare3() 
    {
        return spare3;
    }

    public static ComplaintsInfo mapToInfo(Map<String, Object> params, ComplaintsInfo info){
        BeanMap beanMap=BeanMap.create(info);
        beanMap.putAll(params);
        return info;
    }


    @Override
    public String toString() {
        return "ComplaintsInfo{" +
                "id='" + id + '\'' +
                ", reprotPerson='" + reprotPerson + '\'' +
                ", reportCompany='" + reportCompany + '\'' +
                ", post='" + post + '\'' +
                ", problemType='" + problemType + '\'' +
                ", content='" + content + '\'' +
                ", isReal='" + isReal + '\'' +
                ", realName='" + realName + '\'' +
                ", phone='" + phone + '\'' +
                ", isSuccess='" + isSuccess + '\'' +
                ", opinion='" + opinion + '\'' +
                ", evaluate='" + evaluate + '\'' +
                ", idCard='" + idCard + '\'' +
                ", politicsFace='" + politicsFace + '\'' +
                ", spare3='" + spare3 + '\'' +
                ", startTime='" + startTime + '\'' +
                ", endTime='" + endTime + '\'' +
                '}';
    }
}
