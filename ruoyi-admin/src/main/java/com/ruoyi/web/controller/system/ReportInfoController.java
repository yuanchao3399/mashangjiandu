package com.ruoyi.web.controller.system;

import java.awt.*;
import java.io.*;
import java.util.*;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import cn.afterturn.easypoi.word.WordExportUtil;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.db.Session;
import com.alibaba.fastjson2.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.common.enums.ResultErrorCode;
import com.ruoyi.common.utils.AESUtil;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.domain.ReportResult;
import com.ruoyi.system.domain.vo.DateQueryVo;
import com.ruoyi.system.domain.vo.ReportExcelVo;
import com.ruoyi.system.domain.vo.ReportInfoVo;
import com.ruoyi.system.mapper.ReportResultMapper;
import com.ruoyi.system.service.ISysRoleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import jdk.nashorn.internal.parser.JSONParser;
import org.apache.poi.xwpf.usermodel.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.repository.query.Param;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.system.domain.ReportInfo;
import com.ruoyi.system.service.IReportInfoService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

import static java.awt.SystemColor.info;

/**
 * 投诉/举报信息Controller
 * 
 * @author yuanchao
 * @date 2022-07-19
 */
@Api("举报/投诉信息")
@RestController
@RequestMapping("/system/report")
public class ReportInfoController extends BaseController
{

    @Autowired
    private IReportInfoService reportInfoService;

    @Autowired
    private ReportResultMapper reportResultMapper;
    @Autowired
    private ISysRoleService iSysRoleService;
    @Autowired
    private WebSocket webSocket;


    /**
     * 查询投诉信息列表
     */
    @ApiOperation("查询投诉信息列表")
//    @PreAuthorize("@ss.hasPermi('system:report:list')")
    @GetMapping("/list")
    public TableDataInfo list(ReportInfoVo reportInfoVo)
    {

        //查询当前登录用户的角色
        Long id= SecurityUtils.getUserId();
        Set<String> roles=  iSysRoleService.selectRolePermissionByUserId(id);
        if(roles.contains("zwbjgc")){
            reportInfoVo.setRoleCode("23");
        }else if(roles.contains("swzx")){
            reportInfoVo.setRoleCode("3");
        }

        startPage();
//        List<ReportExcelVo> list = reportInfoService.selectReportExcel(reportInfoVo);
        List<ReportInfo> list = reportInfoService.selectReportInfoList(reportInfoVo);
        return getDataTable(list);
    }



    /**
     * 查询投诉信息列表
     */
    @ApiOperation("撤销下派")
//    @PreAuthorize("@ss.hasPermi('system:report:list')")
    @PostMapping("/revoke/{rid}")
    public AjaxResult revoke(@PathVariable("rid")String rid)
    {
        //查询当前登录用户的角色
        Long id= SecurityUtils.getUserId();
        Set<String> roles=  iSysRoleService.selectRolePermissionByUserId(id);
        ReportResult reportResult = reportResultMapper.selectReportResultById(rid);
        String process=reportResult.getProcess();
        String  isSuccess=reportResult.getIsSuccess();
        if(roles.contains("common")||roles.contains("jjzAdmin")){
            if("2".equals(process)&&"N".equals(isSuccess)){
                reportInfoService.revoke(rid,"1");
                 return  AjaxResult.success();
            }
        }
        else if(roles.contains("zwbjgc")){
            if("3".equals(process)&&"N".equals(isSuccess)) {
                reportInfoService.revoke(rid,"2");
                return  AjaxResult.success();
            }
        }


        return AjaxResult.error("数据不可撤回");
    }

    /**
     * 导出投诉信息列表
     */
    @Log(title = "投诉/举报信息导出", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, ReportInfoVo reportInfoVo)
    {
        List<ReportExcelVo> list = reportInfoService.selectReportExcel(reportInfoVo);
        ExcelUtil<ReportExcelVo> util = new ExcelUtil<ReportExcelVo>(ReportExcelVo.class);
        util.exportExcel(response, list, "投诉信息数据","投诉信息报表");
    }

    /**
     * 导出统计报表
     */
    @ApiOperation("导出统计报表")
//    @PreAuthorize("@ss.hasPermi('system:report:exportStatistics')")
//    @Log(title = "导出统计报表", businessType = BusinessType.EXPORT)
    @PostMapping("/exportStatistics")
    public void exportStatistics(HttpServletRequest request, HttpServletResponse response, ReportInfoVo reportInfoVo)
    {
        reportInfoService.exportStatistics(request,response,reportInfoVo);
    }





    /**
     * 获取投诉信息详细信息
     */
    @ApiOperation("获取投诉/举报信息详细信息")
//    @PreAuthorize("@ss.hasPermi('system:report:query')")
    @GetMapping("/detail")
    public AjaxResult getInfo( String id)
    {

        return reportInfoService.selectReportInfoVoById(id);

    }

    /**
     * 新增投诉信息
     */
    @ApiOperation("新增投诉信息")
  /*  @PreAuthorize("@ss.hasPermi('system:report:add')")*/
//    @Log(title = "投诉/举报信息", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public AjaxResult add(@RequestBody Map<String,String> map)
    {
        String params = map.get("params");


        String decrypt = AESUtil.decrypt(params);

        ReportInfo reportInfo = JSONObject.parseObject(decrypt,ReportInfo.class);

        reportInfo.mapToInfo(reportInfo.getParams(),reportInfo);
        int i = reportInfoService.insertReportInfo(reportInfo);
        if (i >0){
            String id = reportInfo.getId();
            webSocket.sendOneMessage("112","刚刚有一条新的投诉信息");
            return  AjaxResult.success(id);
        } else if (i == -1) {
            return AjaxResult.error(Integer.valueOf(ResultErrorCode.ID_REPEAT.getCode()),ResultErrorCode.ID_REPEAT.getMsg());
        }
        return AjaxResult.error(Integer.valueOf(ResultErrorCode.INTERNAL_SERVER_ERROR.getCode()),ResultErrorCode.INTERNAL_SERVER_ERROR.getMsg());
    }


    /**
     * 生成验证码
     */
 /*     @PreAuthorize("@ss.hasPermi('system:report:add')")*/
//    @Log(title = "投诉/举报信息", businessType = BusinessType.INSERT)
    @PostMapping("/code")
    public AjaxResult getCode()
    {
            String str="abcdefghjkmnpqrstuvwxyzABCDEFGHJKMNPQRSTUVWXYZ23456789";
            StringBuilder sb=new StringBuilder(4);
            for(int i=0;i<4;i++) {
                char ch = str.charAt(new Random().nextInt(32));
                sb.append(ch);
            };

            return AjaxResult.success(sb);

    }

    /**
     * 修改投诉/举报信息
     */
    @PreAuthorize("@ss.hasPermi('system:report:edit')")
    @Log(title = "投诉/举报信息", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody ReportInfo reportInfo)
    {
        return toAjax(reportInfoService.updateReportInfo(reportInfo));
    }

    /**
     * 删除投诉/举报信息
     */
    @PreAuthorize("@ss.hasPermi('system:report:remove')")
    @Log(title = "投诉/举报信息", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable String[] ids)
    {
        return toAjax(reportInfoService.deleteReportInfoByIds(ids));
    }



    /**
     * 纪检组下派案件到监管处
     */
    @ApiOperation("纪检组下派案件到监管处")
    @ApiImplicitParam(name = "id", value = "举报ID", required = true, dataType = "string", paramType = "path", dataTypeClass = String.class)
    @PreAuthorize("@ss.hasPermi('system:report:downReport')")
    @Log(title = "纪检组下派案件到监管处", businessType = BusinessType.OTHER)
    @PostMapping("/downReport/{id}")
    public AjaxResult downReport(@PathVariable("id") String id)
    {
        if (StringUtils.isEmpty(id)){
            return AjaxResult.error(ResultErrorCode.PARAMS_MISS.getCode(),ResultErrorCode.PARAMS_MISS.getMsg());
        }
        webSocket.sendOneMessage("102","刚刚有一条新的投诉信息");
        return toAjax(reportInfoService.downReport(id,getUserId()));
//        return toAjax(reportInfoService.downReport(id,188L));
    }




    /**
     * 监管处下派案件到事务中心
     */
    @ApiOperation("监管处下派案件到事务中心")
    @ApiImplicitParam(name = "id", value = "举报ID", required = true, dataType = "string", paramType = "path", dataTypeClass = String.class)
    @Log(title = "监管处下派案件到事务中心", businessType = BusinessType.OTHER)
    @PostMapping("/downReport2/{id}")
    public AjaxResult downReport2(@PathVariable("id") String id)
    {
        if (StringUtils.isEmpty(id)){
            return AjaxResult.error(ResultErrorCode.PARAMS_MISS.getCode(),ResultErrorCode.PARAMS_MISS.getMsg());
        }
        webSocket.sendOneMessage("102","刚刚有一条新的投诉信息");
        return toAjax(reportInfoService.downReport2(id,getUserId()));
//        return toAjax(reportInfoService.downReport(id,188L));
    }
    /**
     * 完成举报案件
     */
    @ApiOperation("submitReport")
    @ApiImplicitParam(name = "id", value = "举报ID", required = true, dataType = "string", paramType = "path", dataTypeClass = String.class)
    @PreAuthorize("@ss.hasPermi('system:report:submitReport')")
    @Log(title = "完成举报案件", businessType = BusinessType.OTHER)
    @PostMapping("/submitReport")
    public AjaxResult submitReport(@RequestBody ReportInfoVo reportInfoVo)
    {
        String id = reportInfoVo.getId();
        String opinion = reportInfoVo.getOpinion();
        if (StringUtils.isEmpty(id)){
            return AjaxResult.error(ResultErrorCode.PARAMS_MISS.getCode(),ResultErrorCode.PARAMS_MISS.getMsg());
        }
        return toAjax(reportInfoService.submitReport(id,opinion,getUserId()));
    }

    /**
     * 用户对办理情况进行评价
     */
    @ApiOperation("userEvaluate")
    @ApiImplicitParam(name = "id", value = "举报ID", required = true, dataType = "string", paramType = "path", dataTypeClass = String.class)
//    @PreAuthorize("@ss.hasPermi('system:report:userEvaluate')")
//    @Log(title = "用户对办理情况进行评价", businessType = BusinessType.OTHER)
    @PostMapping("/userEvaluate")
    public AjaxResult userEvaluate(@RequestBody ReportInfoVo reportInfoVo)
    {
        reportInfoVo.mapToInfo(reportInfoVo.getParams(),reportInfoVo);
        String id = reportInfoVo.getId();
        String evaluate = reportInfoVo.getEvaluate();
        String type = reportInfoVo.getType();
        if (StringUtils.isEmpty(id)){
            return AjaxResult.error(ResultErrorCode.PARAMS_MISS.getCode(),ResultErrorCode.PARAMS_MISS.getMsg());
        }
        return toAjax(reportInfoService.userEvaluate(id,evaluate,type));
    }


    /**
     * 事务中心用户对举报案件办理
     */
    @ApiOperation("userHand")
    @ApiImplicitParam(name = "id", value = "举报ID", required = true, dataType = "string", paramType = "path", dataTypeClass = String.class)
//    @PreAuthorize("@ss.hasPermi('system:report:userHand')")
//    @Log(title = "事务中心用户对办理情况进行评价", businessType = BusinessType.OTHER)
    @PostMapping("/userHand")
    public AjaxResult userHand(@RequestBody ReportInfoVo reportInfoVo)
    {
        String id = reportInfoVo.getId();
        String opinion=reportInfoVo.getOpinion();
        if (StringUtils.isEmpty(id)){
            return AjaxResult.error(ResultErrorCode.PARAMS_MISS.getCode(),ResultErrorCode.PARAMS_MISS.getMsg());
        }
        return toAjax(reportInfoService.userHand(id,opinion,getUserId()));
    }


    /**
     * 事监管处用户对事务中心办理完的案件审核
     */
    @ApiOperation("examine")
    @ApiImplicitParam(name = "id", value = "举报ID", required = true, dataType = "string", paramType = "path", dataTypeClass = String.class)
//    @PreAuthorize("@ss.hasPermi('system:report:userHand')")
//    @Log(title = "事务中心用户对办理情况进行评价", businessType = BusinessType.OTHER)
    @PostMapping("examine")
    public AjaxResult examine(@RequestBody ReportInfoVo reportInfoVo)
    {
        reportInfoVo.mapToInfo(reportInfoVo.getParams(),reportInfoVo);
        String id = reportInfoVo.getId();
        if (StringUtils.isEmpty(id)){
            return AjaxResult.error(ResultErrorCode.PARAMS_MISS.getCode(),ResultErrorCode.PARAMS_MISS.getMsg());
        }
        return toAjax(reportInfoService.examine(id));
    }


    /**
     * 根据详情ID查询附件
     */
    @ApiOperation("getFileListById")
//    @PreAuthorize("@ss.hasPermi('system:report:userEvaluate')")
    @Log(title = "根据详情ID查询附件", businessType = BusinessType.OTHER)
    @GetMapping("/getFileListById")
    public AjaxResult getFileListById(String id)
    {
        return AjaxResult.success("成功",reportInfoService.getFileListById(id));
    }



    public static void main(String[] args) {


        System.out.println(UUID.randomUUID().toString().replace("-", ""));
        System.out.println(UUID.randomUUID().toString().replace("-", ""));
        System.out.println(UUID.randomUUID().toString().replace("-", ""));


    }


    /**
     * 首页数量数据展示
     */
    @ApiOperation("homeNumAll")
    @ApiImplicitParam(name = "id", value = "举报ID", required = true, dataType = "string", paramType = "path", dataTypeClass = String.class)
//    @PreAuthorize("@ss.hasPermi('system:report:userEvaluate')")
    @Log(title = "用户对办理情况进行评价", businessType = BusinessType.OTHER)
    @GetMapping("/homeNumAll")
    public AjaxResult homeNumAll()
    {
        SysUser user = getLoginUser().getUser();
        return AjaxResult.success("成功",reportInfoService.homeNumAll(user));
    }

    /**
     * 首页饼图数据展示
     */
    @ApiOperation("homePieChart")
//    @PreAuthorize("@ss.hasPermi('system:report:userEvaluate')")
    @Log(title = "首页饼图数据展示", businessType = BusinessType.OTHER)
    @PostMapping("/homePieChart")
    public AjaxResult homePieChart(@RequestBody DateQueryVo dateQueryVo)
    {
        SysUser user = getLoginUser().getUser();
        String startTime = dateQueryVo.getStartTime();
        String endTime = dateQueryVo.getEndTime();
        return AjaxResult.success("成功",reportInfoService.homePieChart(startTime,endTime,user));
    }

    /**
     * 首页饼图数据展示
     */
    @ApiOperation("homePieChartByTime")
//    @PreAuthorize("@ss.hasPermi('system:report:userEvaluate')")
    @Log(title = "首页饼图数据展示,根据时间查询", businessType = BusinessType.OTHER)
    @PostMapping("/homePieChartByTime")
    public AjaxResult homePieChartByTime(@RequestBody DateQueryVo dateQueryVo)
    {
        SysUser user = getLoginUser().getUser();
        String startTime = dateQueryVo.getStartTime();
        String endTime = dateQueryVo.getEndTime();
        return AjaxResult.success("成功",reportInfoService.homePieChart(startTime,endTime,user));
    }

    /**
     * 首页折线图数据展示
     */
    @ApiOperation("homeLineChart")
//    @PreAuthorize("@ss.hasPermi('system:report:userEvaluate')")
    @Log(title = "首页折线图数据展示", businessType = BusinessType.OTHER)
    @GetMapping("/homeLineChart")
    public AjaxResult homeLineChart()
    {
        SysUser user = getLoginUser().getUser();

        return AjaxResult.success("成功",reportInfoService.homeLineChart(user));
    }

    /**
     * 首页柱状图数据展示
     */
    @ApiOperation("homeBarChart")
    @Log(title = "首页柱状图数据展示", businessType = BusinessType.OTHER)
    @GetMapping("/homeBarChart")
    public AjaxResult homeBarChart()
    {
        return AjaxResult.success("成功",reportInfoService.barNum());
    }

    /**
     * 首页-本年度每月投诉/举报量柱状图
     */
    @ApiOperation("homeYearBarChart")
    @Log(title = "首页-本年度每月各单位投诉/举报量柱状图", businessType = BusinessType.OTHER)
    @GetMapping("/homeYearBarChart")
    public AjaxResult homeYearBarChart()
    {
        return AjaxResult.success("成功",reportInfoService.homeYearBarChart());
    }



    @PostMapping("/exportDocx")
    public  void exportDocx( HttpServletResponse response, String id) throws Exception {

       this.reportInfoService.exportDocx(response,id);

    }


}
