package com.ruoyi.system.service;

import java.io.IOException;
import java.util.List;

import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.system.domain.ComplaintsInfo;

import javax.servlet.http.HttpServletResponse;

/**
 * 举报信息Service接口
 * 
 * @author ruoyi
 * @date 2022-08-04
 */
public interface IComplaintsInfoService 
{
    /**
     * 查询举报信息
     * 
     * @param id 举报信息主键
     * @return 举报信息
     */
    public ComplaintsInfo selectComplaintsInfoById(String id);

    /**
     * 查询举报信息列表
     * 
     * @param complaintsInfo 举报信息
     * @return 举报信息集合
     */
    public List<ComplaintsInfo> selectComplaintsInfoList(ComplaintsInfo complaintsInfo);

    /**
     * 新增举报信息
     * 
     * @param complaintsInfo 举报信息
     * @return 结果
     */
    public int insertComplaintsInfo(ComplaintsInfo complaintsInfo);

    /**
     * 修改举报信息
     * 
     * @param complaintsInfo 举报信息
     * @return 结果
     */
    public int updateComplaintsInfo(ComplaintsInfo complaintsInfo);

    /**
     * 批量删除举报信息
     * 
     * @param ids 需要删除的举报信息主键集合
     * @return 结果
     */
    public int deleteComplaintsInfoByIds(String[] ids);

    /**
     * 删除举报信息信息
     * 
     * @param id 举报信息主键
     * @return 结果
     */
    public int deleteComplaintsInfoById(String id);

    public  int submitComplaints(String id, String opinion, Long userId);

    public  int userEvaluate(String id, String evaluate);

    void exportDocx(HttpServletResponse response, String id) throws Exception;

    AjaxResult getInfo(String id);
}
