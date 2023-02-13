package com.ruoyi.system.service.impl;

import java.io.*;
import java.text.SimpleDateFormat;
import java.time.Year;
import java.util.*;
import java.util.stream.Collectors;

import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.entity.TemplateExportParams;
import cn.afterturn.easypoi.word.WordExportUtil;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.domain.entity.SysRole;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.utils.*;
import com.ruoyi.common.utils.bean.BeanUtils;
import com.ruoyi.common.utils.file.FileUtils;
import com.ruoyi.system.domain.ComplaintsInfo;
import com.ruoyi.system.domain.ReportFile;
import com.ruoyi.system.domain.vo.ReportExcelVo;
import com.ruoyi.system.domain.vo.ReportInfoVo;
import com.ruoyi.system.mapper.ComplaintsInfoMapper;
import com.ruoyi.system.mapper.ReportFileMapper;
import com.ruoyi.system.mapper.ReportResultMapper;
import com.ruoyi.system.service.ISysRoleService;
import com.sun.corba.se.spi.ior.ObjectKey;
import org.apache.poi.POIDocument;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.system.domain.ReportResult;
import com.ruoyi.system.mapper.ReportInfoMapper;
import com.ruoyi.system.domain.ReportInfo;
import com.ruoyi.system.service.IReportInfoService;
import org.springframework.util.ClassUtils;
import org.springframework.util.ResourceUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 投诉/举报信息Service业务层处理
 * 
 * @author yuanchao
 * @date 2022-07-19
 */
@Service
public class ReportInfoServiceImpl implements IReportInfoService 
{
    @Autowired
    private ReportInfoMapper reportInfoMapper;

    @Autowired
    private ReportResultMapper reportResultMapper;

    @Autowired
    private ComplaintsInfoMapper complaintsInfoMapper;

    @Autowired
    private ReportFileMapper reportFileMapper;

    @Autowired
    private ISysRoleService iSysRoleService;

    /**
     * 查询投诉/举报信息
     * 
     * @param id 投诉/举报信息主键
     * @return 投诉/举报信息
     */
    @Override
    public ReportInfo selectReportInfoById(String id)
    {
        return reportInfoMapper.selectReportInfoById(id);
    }

    @Override
    public AjaxResult selectReportInfoVoById(String id) {
        ReportInfo reportInfo = this.selectReportInfoById(id);

        if (null == reportInfo){
            return AjaxResult.error("查询失败");
        }

        ReportInfoVo vo = (ReportInfoVo)BeanUtils.copyBeanProp1(new ReportInfoVo(),reportInfo);
        vo.setOpinion(reportInfo.getReportResultList().get(0).getOpinion());
        vo.setEvaluate(reportInfo.getReportResultList().get(0).getEvaluate());
        vo.setIsSuccess(reportInfo.getReportResultList().get(0).getIsSuccess());
        vo.setCreateTime(DateUtils.getDayFormat(reportInfo.getCreateTime(),"yyyy-MM-dd HH:mm:ss"));

//        Object encrypt = AESUtil.encrypt(JSONUtil.toJsonStr(vo));
        return AjaxResult.success(vo);

    }

    /**
     * 查询投诉/举报信息列表
     * 
     * @param reportInfoVo 投诉/举报信息
     * @return 投诉/举报信息
     */
    @Override
    public List<ReportInfo> selectReportInfoList (ReportInfoVo reportInfoVo)
    {
        List<ReportInfo> list=reportInfoMapper.selectReportInfoList(reportInfoVo);

//        for(ReportInfo reportInfo :list){
//            if(reportInfo.getReportResultList().get(0).getIsSuccess().equals("Y"))
//                try {
//                    String stateTime =DateUtils.getDayFormat(reportInfo.getReportResultList().get(0).getUpdateTime(),"yyyy-MM-dd");
//                 String  endTime=  DateUtils.getDayFormat(reportInfo.getCreateTime(),"yyyy-MM-dd");
//                    SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
//                    Long state= sdf.parse(stateTime).getTime();
//                   Long end= sdf.parse(endTime).getTime();
//                    reportInfo.setCompletionTime((state-end)/(24*60*60*1000));
//
//                }catch (Exception E){
//
//                }
//        }
        return list;
    }


    /**
     * 查询投诉/举报信息列表
     *
     * @param reportInfoVo 投诉/举报信息
     * @return 投诉/举报信息
     */
    @Override
    public List<ReportExcelVo> selectReportExcel(ReportInfoVo reportInfoVo)
    {
        Long id= SecurityUtils.getUserId();

        Set<String> roles=  iSysRoleService.selectRolePermissionByUserId(id);

        if(roles.contains("zwbjgc")){
            reportInfoVo.setRoleCode("2");
        }

        return reportInfoMapper.selectReportExcel(reportInfoVo);
    }
    /**
     * 新增投诉/举报信息
     * 
     * @param reportInfo 投诉/举报信息
     * @return 结果
     */
    @Transactional
    @Override
    public int insertReportInfo(ReportInfo reportInfo)
    {

        ReportInfo info = reportInfoMapper.selectReportInfoById(reportInfo.getId());
        if (null != info){
            //id重复
            return -1;
        }
        reportInfo.setCreateTime(DateUtils.getNowDate());
        int rows = reportInfoMapper.insertReportInfo(reportInfo);

        //初始化投诉/举报結果表
        insertReportResult(reportInfo.getId());
        return rows;
    }

    /**
     * 修改投诉/举报信息
     * 
     * @param reportInfo 投诉/举报信息
     * @return 结果
     */
    @Transactional
    @Override
    public int updateReportInfo(ReportInfo reportInfo)
    {
        reportInfo.setUpdateTime(DateUtils.getNowDate());
        reportInfoMapper.deleteReportResultById(reportInfo.getId());
//        insertReportResult(reportInfo);
        return reportInfoMapper.updateReportInfo(reportInfo);
    }

    /**
     * 批量删除投诉/举报信息
     * 
     * @param ids 需要删除的投诉/举报信息主键
     * @return 结果
     */
    @Transactional
    @Override
    public int deleteReportInfoByIds(String[] ids)
    {
        reportInfoMapper.deleteReportResultByIds(ids);
        return reportInfoMapper.deleteReportInfoByIds(ids);
    }

    /**
     * 删除投诉/举报信息信息
     * 
     * @param id 投诉/举报信息主键
     * @return 结果
     */
    @Transactional
    @Override
    public int deleteReportInfoById(String id)
    {
        reportInfoMapper.deleteReportResultById(id);
        return reportInfoMapper.deleteReportInfoById(id);
    }

    @Override
    public int downReport(String id,Long userId) {

        ReportResult reportResult = new ReportResult();
        reportResult.setId(id);
        reportResult.setProcess("2");
        reportResult.setTransferUserOne(String.valueOf(userId));
        reportResult.setTransferTimeOne(DateUtils.getNowDate());
        return reportResultMapper.updateReportResult(reportResult);

    }

    @Override
    public int downReport2(String id,Long userId) {

        ReportResult reportResult = new ReportResult();
        reportResult.setId(id);
        reportResult.setProcess("3");
        reportResult.setTransferUserTwo(String.valueOf(userId));
        reportResult.setTransferTimeTwo(DateUtils.getNowDate());
        return reportResultMapper.updateReportResult(reportResult);

    }

    @Override
    public int submitReport(String id , String opinion , Long userId) {
        ReportResult reportResult = new ReportResult();
        reportResult.setId(id);
        reportResult.setIsSuccess("Y");
        reportResult.setOpinion(opinion);
        reportResult.setUpdateUser(String.valueOf(userId));
        reportResult.setUpdateTime(DateUtils.getNowDate());
        return reportResultMapper.updateReportResult(reportResult);
    }

    @Override
    public int userEvaluate(String id, String evaluate,String type) {

        ReportResult reportResult = new ReportResult();
        reportResult.setId(id);
        reportResult.setEvaluate(evaluate);
        return reportResultMapper.updateReportResult(reportResult);
    }
    @Override
    public int userHand(String id,String opinion,Long userId) {

        ReportResult reportResult = new ReportResult();
        reportResult.setId(id);
        reportResult.setHandResults("Y");
        reportResult.setOpinion(opinion);
        reportResult.setIsSuccess("Y");
        reportResult.setUpdateUser(String.valueOf(userId));
        reportResult.setUpdateTime(DateUtils.getNowDate());
        return reportResultMapper.updateReportResult(reportResult);
    }

    @Override
    public int examine(String id) {
        ReportResult reportResult = new ReportResult();
        reportResult.setId(id);
        return reportResultMapper.updateReportResult(reportResult);
    }


    /**
     * 撤销下派到下级数据
     *
     * @param id 投诉/举报信息对象id
     */
    @Override
    public int revoke(String id,String process) {
        ReportResult reportResult = new ReportResult();
        reportResult.setId(id);
        reportResult.setProcess(process);

        return reportResultMapper.revokeReportResultById(reportResult);
    }

/*    *//**
     * 监管处撤销下派到事务中心的数据
     *
     * @param id 投诉/举报信息对象id
     *//*

    @Override
    public int revoke2(String id) {
        ReportResult reportResult = new ReportResult();
        reportResult.setId(id);
        reportResult.setProcess("2");
        return reportResultMapper.updateReportResultById(reportResult);
    }*/
    /**
     * 新增投诉/举报结果信息
     * 
     * @param id 投诉/举报信息对象id
     */
    public void insertReportResult(String id)
    {

        ReportResult reportResult = new ReportResult();
        reportResult.setId(UUID.randomUUID().toString().replace("-",""));
        reportResult.setReportId(id);
        reportResult.setProcess("1");
        reportResult.setIsSuccess("N");
        reportResult.setCreateTime(DateUtils.getNowDate());
        reportResultMapper.insertReportResult(reportResult);

    }


    @Override
    public Map<String,Object> homeNumAll(SysUser user) {

        Map<String,Object> map = new HashMap<>();

        List<SysRole> roles = user.getRoles();
        if (null != roles && roles.size() > 0){
            for (SysRole role : roles) {
                if(roles.size() == 1 && "zwbjgc".equals(role.getRoleKey())){
                    //仅投诉
                    Map<String, Object> tsNumMap = reportInfoMapper.tsNum();
                    map.put("all",tsNumMap);
                    return map;
                }
            }
        }

        //总量
        Map<String, Object> allNumMap = reportInfoMapper.allNum();

        map.put("all",allNumMap);

        return map;
    }

    @Override
    public  Map<String,Object>   barNum() {


        List<Map<String,Object>> list=reportInfoMapper.barNum();


        List label=new ArrayList();
        List num=new ArrayList();
        List num2=new ArrayList();
        for(Map<String,Object> map:list){
            String yy="0";
            if(map.get("label")!=null){
                label.add(String.valueOf(map.get("label")));
            }
            if(map.get("num")!=null){
                num.add(String.valueOf(map.get("num")));
            }else{
                num.add(yy);
            }

            if(map.get("num2")!=null){
                num2.add(String.valueOf(map.get("num2")));
            }else{
                num2.add(yy);
            }
        }

        Map<String,Object> resultMap = new HashMap<>();
        resultMap.put("name",label);
        resultMap.put("tscount",num);
        resultMap.put("jbcount",num2);

        return resultMap;
    }

    @Override
    public Map<String,Object> homePieChart(String startTime, String endTime,SysUser user) {

        Map<Object,Object> tsMap = new HashMap<>();
        Map<Object,Object> jbMap = new HashMap<>();
        Map<String,Object> map = new HashMap<>();

        List<SysRole> roles = user.getRoles();
        if (null != roles && roles.size() > 0){
            for (SysRole role : roles) {
                if( !"zwbjgc".equals(role.getRoleKey())){
                    List<Map<String,Object>> jblist = reportInfoMapper.homePieChartJb(startTime,endTime);
                    for (Map<String, Object> stringObjectMap : jblist) {
                        jbMap.put(stringObjectMap.get("name"),stringObjectMap.get("value"));
                    }
                    map.put("jb",jbMap);

                    //根据时间查询总数
                    Map<String,Object> numMap = reportInfoMapper.getNumByTime(startTime,endTime);
                    map.put("num",numMap);
                }else {
                    //根据时间查询投诉总数
                    Map<String,Object> numMap = reportInfoMapper.getTsNumByTime(startTime,endTime);
                    map.put("num",numMap);
                }
            }
        }

        List<Map<String,Object>> tslist = reportInfoMapper.homePieChartTs(startTime,endTime);
        for (Map<String, Object> stringObjectMap : tslist) {
            tsMap.put(stringObjectMap.get("name"),stringObjectMap.get("value"));
        }
        map.put("ts",tsMap);

        return map;
    }

    @Override
    public Map<String,Object> homeLineChart(SysUser user) {

        List<Map<String,Object>> list = reportInfoMapper.homeLineChart();
        Map<String,Object> map = new HashMap<>();
        List<SysRole> roles = user.getRoles();
        if (null != roles && roles.size() > 0){
            for (SysRole role : roles) {
                if(!"zwbjgc".equals(role.getRoleKey())){
                    List<String> jbcount = list.stream().map(s -> String.valueOf(s.get("jbcount"))).collect(Collectors.toList());
                    map.put("jbcount",jbcount);
                }
            }
        }

       /* List<String> nameList = new ArrayList<>();
        List<String> tscountList = new ArrayList<>();
        List<String> jbcountList = new ArrayList<>();
        Map<String,Object> map1 = null;
        for (Map<String, Object> stringObjectMap : list) {
            String time = String.valueOf(stringObjectMap.get("name"));
            String tscount = String.valueOf(stringObjectMap.get("tscount"));
            String jbcount = String.valueOf(stringObjectMap.get("jbcount"));
            nameList.add(time);
            tscountList.add(tscount);
            jbcountList.add(jbcount);
        }*/

        List<String> nameList = list.stream().map(s -> String.valueOf(s.get("name"))).collect(Collectors.toList());

        List<String> tscountList = list.stream().map(s -> String.valueOf(s.get("tscount"))).collect(Collectors.toList());
//        List<String> jbcountList = list.stream().map(s -> String.valueOf(s.get("jbcount"))).collect(Collectors.toList());
//        map.put("jbcount",jbcountList);
        map.put("name",nameList);
        map.put("tscount",tscountList);

        return map;

    }

    @Override
    public List<ReportFile> getFileListById(String id) {

        List<ReportFile> reportFile = reportFileMapper.selectReportFileByPid(id);
        return reportFile;
    }

    @Override
    public Map<String, Object> homeYearBarChart() {

        List<Map<String, Object>> list = reportInfoMapper.homeYearBarChart();
        List<String> nameList = new ArrayList<>();
        List<String> dsjjNumList = new ArrayList<>();
        List<String> zwbNumList = new ArrayList<>();
        Map<String,Object> map1 = null;
        for (Map<String, Object> stringObjectMap : list) {
            String time = String.valueOf(stringObjectMap.get("time"));
            String dsjjNum = String.valueOf(stringObjectMap.get("dsjjNum"));
            String zwbNum = String.valueOf(stringObjectMap.get("zwbNum"));
            nameList.add(time);
            dsjjNumList.add(dsjjNum);
            zwbNumList.add(zwbNum);
        }

//        List<String> name = list.stream().map(s -> String.valueOf(s.get("time"))).collect(Collectors.toList());
//
//        List<String> dsjjNum = list.stream().map(s -> String.valueOf(s.get("dsjjNum"))).collect(Collectors.toList());
//
//        List<String> zwbNum = list.stream().map(s -> String.valueOf(s.get("zwbNum"))).collect(Collectors.toList());

        Map<String,Object> map = new HashMap<>();
        map.put("name",nameList);
        map.put("dsjjNum",dsjjNumList);
        map.put("zwbNum",zwbNumList);

        return map;
    }

    @Override
    public void exportStatistics(HttpServletRequest request, HttpServletResponse response, ReportInfoVo reportInfoVo) {

        Map<String, Object> map = reportInfoMapper.allNum();
        map.put("closingdate",DateUtils.getDate1());

        List<Map<String,Object>> tsList = reportInfoMapper.homePieChartTs(reportInfoVo.getStartTime(),reportInfoVo.getEndTime());
        List<Map<String,Object>> jbList = reportInfoMapper.homePieChartJb(reportInfoVo.getStartTime(),reportInfoVo.getEndTime());
        if (StringUtils.isNotNull(reportInfoVo.getStartTime()) && StringUtils.isNotNull(reportInfoVo.getEndTime())){
            String startTime = DateUtils.getDayFormat(DateUtils.parseDate(reportInfoVo.getStartTime()), "YYYY年MM月dd日");
            String endTime = DateUtils.getDayFormat(DateUtils.parseDate(reportInfoVo.getEndTime()), "YYYY年MM月dd日");
            map.put("time",startTime + "至" + endTime);
        }else {
            map.put("time","截止至"+DateUtils.getDate1());
        }

        map.put("tslist", tsList);
        map.put("jblist", jbList);

        /*  List<Map<String, String>> listMap = new ArrayList<Map<String, String>>();
        for (int i = 0; i < 4; i++) {
            Map<String, String> lm = new HashMap<String, String>();
            lm.put("id", i * 10000 + "");

            listMap.add(lm);
        }
        map.put("maplist", listMap);*/

        ClassPathResource classPathResource = new ClassPathResource("static/msjdExcel.xlsx");
        TemplateExportParams params = new TemplateExportParams(
                classPathResource.getPath());
        Workbook workbook = ExcelExportUtil.exportExcel(params, map);

        CellStyle cellStyle = ExcelUtils.Border(workbook);

        Sheet sheetAt = workbook.getSheetAt(0);
        int tssize = tsList.size();
        int jbsize = jbList.size();

        for (int i = 4; i < tssize + 4 ; i++) {
            Row row = sheetAt.getRow(i);
            for (int j = 4; j <= 6; j++) {
                Cell cell = row.getCell(j);
                cell.setCellStyle(cellStyle);
            }
        }
        for (int i = 4; i < jbsize + 4 ; i++) {
            Row row = sheetAt.getRow(i);
            for (int j = 7; j <= 9; j++) {
                Cell cell = row.getCell(j);
                cell.setCellStyle(cellStyle);
            }
        }

//        CellRangeAddress region = new CellRangeAddress(size+2, size+2, (short) 4, (short) 9);
//        sheetAt.addMergedRegion(region);

        OutputStream out = null;
        InputStream inputStream = null;

        String fileName = "码上监督统计.xlsx";
        try {
            //根据浏览器类型,设置响应头
            String agent = request.getHeader("User-Agent");
            if (agent.contains("Firefox")) {  // 如果是火狐浏览器
                fileName = new String(fileName.getBytes("UTF-8"), "ISO-8859-1");
            } else {  // 其它浏览器
                fileName = new String(fileName.getBytes("UTF-8"), "ISO-8859-1");
                fileName = fileName.replace("+", " ");
            }
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        // 设置相应头，控制浏览器下载该文件，这里就是会出现当你点击下载后，出现的下载地址框
        response.setHeader("content-disposition",
                "attachment;filename=\"" + fileName + "\"");
        response.setContentType("application/octet-stream");
        try {
            out = response.getOutputStream();
            workbook.write(out);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }finally {
            try {
                out.flush();
                workbook.close();
            }catch (Exception e){
                throw new RuntimeException("导出异常");
            }
        }
    }

    @Override
    public void exportDocx(HttpServletResponse response, String id) throws Exception {
        ClassPathResource classPathResource = new ClassPathResource("static/tousu.docx");


        ReportInfo reportInfo=  this.selectReportInfoById(id);
        String opinion="  ";
        String success="";
        String name=" 匿 名 ";
        String phone="  ";
        String polotocsFace="  ";
        String idCard="   ";
        String content="  ";
        String createTime=" ";
        if(reportInfo.getContent()!=null&&!reportInfo.getContent().equals("")){
            content=reportInfo.getContent();
        }
        if(reportInfo.getCreateTime()!=null&&!reportInfo.getCreateTime().equals("")){
            createTime=DateUtils.parseDateToStr("YYYY年MM月dd日",reportInfo.getCreateTime());
        }
        if(reportInfo.getRealName()!=null&&!reportInfo.getRealName().equals("")){
            name=reportInfo.getRealName();
        }
        if(reportInfo.getPhone()!=null&&!reportInfo.getPhone().equals("")){
            phone=reportInfo.getPhone();
        }
        if(reportInfo.getPoliticsFace()!=null&&!reportInfo.getPoliticsFace().equals("")){
            polotocsFace=reportInfo.getPoliticsFace();
        }
        if(reportInfo.getIdCard()!=null&&!reportInfo.getIdCard().equals("")){
            idCard=reportInfo.getIdCard();
        }

        if(reportInfo.getReportResultList().get(0).getOpinion()!=null&&!reportInfo.getReportResultList().get(0).getOpinion().equals("")){
            opinion= reportInfo.getReportResultList().get(0).getOpinion();

        }
        if(reportInfo.getReportResultList().get(0).getIsSuccess().equals("Y")){
            success= "已办结";

        }else {
            success="未办结";
        }



        //准备数据
        Map<String,Object> params =new HashMap<>();
        params.put("name",name);
        params.put("createTime",  createTime);
        params.put("phone",phone);
        params.put("content",content);
        params.put("idCard",idCard);
        params.put("politicsFace",polotocsFace);
        params.put("windowNum",reportInfo.getWindowNum());
        params.put("serviceType",reportInfo.getServiceType());
        params.put("detailInfo",reportInfo.getDetailInfo());
        params.put("isSuccess",success);
        params.put("opinion",opinion);
        XWPFDocument word = WordExportUtil.exportWord07(classPathResource.getPath(),params);
        String filename = "投诉详情信息.docx";
        response.setHeader("content-disposition","attachment;filename="+new String(filename.getBytes(),"ISO8859-1"));
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        word.write(response.getOutputStream());
    }







    public int size(int i1,int i2){
        if (i1 > i2){
            return i1;
        }else {
            return i2;
        }
    }


    public static void main(String[] args) {
       /* List<Map<String,Object>> list1 = new ArrayList<>();
        Map<String, Object> map1 = new HashMap<>();
        map1.put("asdf",0);
        map1.put("asdf1",0);
        map1.put("asdf2",0);
        list1.add(map1);


        List<Map<String,Object>> list2 = new ArrayList<>();
        Map<String, Object> map2 = new HashMap<>();
        map2.put("qwer",0);
        map2.put("qwer1",0);
        map2.put("qwer2",0);
        list2.add(map2);

        System.out.println(list1);
        System.out.println(list2);

        list1.addAll(list2);
        System.out.println(list1);*/

        class  A {
            String a ;
            String b ;

            public String getA() {
                return a;
            }

            public void setA(String a) {
                this.a = a;
            }

            public String getB() {
                return b;
            }

            public void setB(String b) {
                this.b = b;
            }
        }

        A a = new A();
        a.setA("123");
        a.setB("123");
        String s = JSONUtil.toJsonStr(a);
        String $1$2 = s.replaceAll("\"(\\w+)\"(\\s*:\\s*)", "$1$2");
        System.out.println($1$2);


        System.out.println();
    }


}
