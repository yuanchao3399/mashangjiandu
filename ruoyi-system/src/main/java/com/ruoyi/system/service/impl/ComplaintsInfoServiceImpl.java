package com.ruoyi.system.service.impl;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.afterturn.easypoi.word.WordExportUtil;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSONUtil;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.utils.AESUtil;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.system.domain.ReportInfo;
import com.ruoyi.system.domain.ReportResult;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.ComplaintsInfoMapper;
import com.ruoyi.system.domain.ComplaintsInfo;
import com.ruoyi.system.service.IComplaintsInfoService;
import org.springframework.util.ResourceUtils;

import javax.servlet.http.HttpServletResponse;

import static java.awt.SystemColor.info;

/**
 * 举报信息Service业务层处理
 * 
 * @author ruoyi
 * @date 2022-08-04
 */
@Service
public class ComplaintsInfoServiceImpl implements IComplaintsInfoService 
{
    @Autowired
    private ComplaintsInfoMapper complaintsInfoMapper;

    /**
     * 查询举报信息
     * 
     * @param id 举报信息主键
     * @return 举报信息
     */
    @Override
    public ComplaintsInfo selectComplaintsInfoById(String id)
    {
        return complaintsInfoMapper.selectComplaintsInfoById(id);
    }

    @Override
    public AjaxResult getInfo(String id) {
        ComplaintsInfo info = complaintsInfoMapper.selectComplaintsInfoById(id);

        if (null == info){
            return AjaxResult.error("查询失败");
        }

//        Object encrypt = AESUtil.encrypt(JSONUtil.toJsonStr(info));
        return AjaxResult.success(info);
    }

    /**
     * 查询举报信息列表
     * 
     * @param complaintsInfo 举报信息
     * @return 举报信息
     */
    @Override
    public List<ComplaintsInfo> selectComplaintsInfoList(ComplaintsInfo complaintsInfo)
    {
        List<ComplaintsInfo> list=complaintsInfoMapper.selectComplaintsInfoList(complaintsInfo);

      /*  for(ComplaintsInfo complaintsInfo1 :list){
            if(complaintsInfo1.getIsSuccess().equals("Y"))
                try {
                    String stateTime =DateUtils.getDayFormat(complaintsInfo1.getUpdateTime(),"yyyy-MM-dd");
                    String  endTime=  DateUtils.getDayFormat(complaintsInfo1.getCreateTime(),"yyyy-MM-dd");
                    SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
                    Long state= sdf.parse(stateTime).getTime();
                    Long end= sdf.parse(endTime).getTime();
                    complaintsInfo1.setCompletionTime((state-end)/(24*60*60*1000));

                }catch (Exception E){

                }
        }*/
        return list;
    }

    /**
     * 新增举报信息
     * 
     * @param complaintsInfo 举报信息
     * @return 结果
     */
    @Override
    public int insertComplaintsInfo(ComplaintsInfo complaintsInfo)
    {
        ComplaintsInfo info = complaintsInfoMapper.selectComplaintsInfoById(complaintsInfo.getId());

        if (null != info){
            return -1;
        }

        complaintsInfo.mapToInfo(complaintsInfo.getParams(),complaintsInfo);
        complaintsInfo.setIsSuccess("N");
        complaintsInfo.setCreateTime(DateUtils.getNowDate());
        return complaintsInfoMapper.insertComplaintsInfo(complaintsInfo);
    }

    /**
     * 修改举报信息
     * 
     * @param complaintsInfo 举报信息
     * @return 结果
     */
    @Override
    public int updateComplaintsInfo(ComplaintsInfo complaintsInfo)
    {
        complaintsInfo.setUpdateTime(DateUtils.getNowDate());
        return complaintsInfoMapper.updateComplaintsInfo(complaintsInfo);
    }

    /**
     * 批量删除举报信息
     * 
     * @param ids 需要删除的举报信息主键
     * @return 结果
     */
    @Override
    public int deleteComplaintsInfoByIds(String[] ids)
    {
        return complaintsInfoMapper.deleteComplaintsInfoByIds(ids);
    }

    /**
     * 删除举报信息信息
     * 
     * @param id 举报信息主键
     * @return 结果
     */
    @Override
    public int deleteComplaintsInfoById(String id)
    {
        return complaintsInfoMapper.deleteComplaintsInfoById(id);
    }

    @Override
    public int submitComplaints(String id, String opinion, Long userId) {
        ComplaintsInfo complaintsInfo = new ComplaintsInfo();
        complaintsInfo.setId(id);
        complaintsInfo.setIsSuccess("Y");
        complaintsInfo.setOpinion(opinion);
        complaintsInfo.setUpdateUser(String.valueOf(userId));
        complaintsInfo.setUpdateTime(DateUtils.getNowDate());
        return complaintsInfoMapper.updateComplaintsInfo(complaintsInfo);
    }

    @Override
    public int userEvaluate(String id, String evaluate) {
        ComplaintsInfo complaintsInfo = new ComplaintsInfo();
        complaintsInfo.setId(id);
        complaintsInfo.setEvaluate(evaluate);
        return complaintsInfoMapper.updateComplaintsInfo(complaintsInfo);
    }

    @Override
    public void exportDocx(HttpServletResponse response, String id) throws Exception {

        ClassPathResource classPathResource = new ClassPathResource("static/jubao.docx");
        ComplaintsInfo complaintsInfo=  this.selectComplaintsInfoById(id);
        String opinion="  ";
        String success="";
        String name=" 匿 名 ";
        String phone="  ";
        String politicsFace="  ";
        String idCard="   ";
        String title="   ";
        String post="   ";
        String level="  ";
        String problemType="  ";
        String reportPerson="  ";
        String reportCompany="  ";
        String content="  ";
        String createTime=" ";
        if(complaintsInfo.getIsSuccess().equals("Y")){
            success= "已办结";

        }else {
            success="未办结";
        }

        if(complaintsInfo.getContent()!=null&&!complaintsInfo.getContent().equals("")){
            content=complaintsInfo.getContent();
        }
        if(complaintsInfo.getCreateTime()!=null&&!complaintsInfo.getCreateTime().equals("")){
            createTime=DateUtils.parseDateToStr("YYYY年MM月dd日",complaintsInfo.getCreateTime());
        }
        if(complaintsInfo.getRealName()!=null&&!complaintsInfo.getRealName().equals("")){
            name=complaintsInfo.getRealName();
        }
        if(complaintsInfo.getPhone()!=null&&!complaintsInfo.getPhone().equals("")){
            phone=complaintsInfo.getPhone();
        }
        if(complaintsInfo.getPoliticsFace()!=null&&!complaintsInfo.getPoliticsFace().equals("")){
            politicsFace=complaintsInfo.getPoliticsFace();
        }
        if(complaintsInfo.getIdCard()!=null&&!complaintsInfo.getIdCard().equals("")){
            idCard=complaintsInfo.getIdCard();
        }
        if(complaintsInfo.getOpinion()!=null&&!complaintsInfo.getOpinion().equals("")){
            opinion=complaintsInfo.getOpinion();
        }

        if(complaintsInfo.getTitle()!=null&&!complaintsInfo.getTitle().equals("")){
            title=complaintsInfo.getTitle();
        }
        if(complaintsInfo.getPost()!=null&&!complaintsInfo.getPost().equals("")){
            post=complaintsInfo.getPost();
        }
        if(complaintsInfo.getLevel()!=null&&!complaintsInfo.getLevel().equals("")){
            level=complaintsInfo.getLevel();
        }
        if(complaintsInfo.getProblemType()!=null&&!complaintsInfo.getProblemType().equals("")){
            problemType=complaintsInfo.getProblemType();
        }
        if(complaintsInfo.getReprotPerson()!=null&&!complaintsInfo.getReprotPerson().equals("")){
            reportPerson=complaintsInfo.getReprotPerson();
        }
        if(complaintsInfo.getReportCompany()!=null&&!complaintsInfo.getReportCompany().equals("")){
            reportCompany=complaintsInfo.getReportCompany();
        }
        //准备数据
        Map<String,Object> params =new HashMap<>();
        params.put("name",name);
        params.put("createTime", createTime );
        params.put("phone",phone);
        params.put("content",content);
        params.put("idCard",idCard);
        params.put("politicsFace",politicsFace);
        params.put("isSuccess",success);
        params.put("opinion",opinion);
        params.put("title",title);
        params.put("post",post);
        params.put("level",level);
        params.put("reportPerson",reportPerson);
        params.put("problemType",problemType);
        params.put("reportCompany",reportCompany);
        XWPFDocument word = WordExportUtil.exportWord07(classPathResource.getPath(),params);
        String filename = "投诉详情信息.docx";
        response.setHeader("content-disposition","attachment;filename="+new String(filename.getBytes(),"ISO8859-1"));
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        word.write(response.getOutputStream());
    }


}
