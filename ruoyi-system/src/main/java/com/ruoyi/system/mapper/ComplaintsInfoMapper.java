package com.ruoyi.system.mapper;

import java.util.List;
import com.ruoyi.system.domain.ComplaintsInfo;

/**
 * 举报信息Mapper接口
 * 
 * @author ruoyi
 * @date 2022-08-04
 */
public interface ComplaintsInfoMapper 
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
     * 删除举报信息
     * 
     * @param id 举报信息主键
     * @return 结果
     */
    public int deleteComplaintsInfoById(String id);

    /**
     * 批量删除举报信息
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteComplaintsInfoByIds(String[] ids);
}
