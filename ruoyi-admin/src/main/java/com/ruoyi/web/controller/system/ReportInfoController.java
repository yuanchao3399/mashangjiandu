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
 * ??????/????????????Controller
 * 
 * @author yuanchao
 * @date 2022-07-19
 */
@Api("??????/????????????")
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
     * ????????????????????????
     */
    @ApiOperation("????????????????????????")
//    @PreAuthorize("@ss.hasPermi('system:report:list')")
    @GetMapping("/list")
    public TableDataInfo list(ReportInfoVo reportInfoVo)
    {

        //?????????????????????????????????
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
     * ????????????????????????
     */
    @ApiOperation("????????????")
//    @PreAuthorize("@ss.hasPermi('system:report:list')")
    @PostMapping("/revoke/{rid}")
    public AjaxResult revoke(@PathVariable("rid")String rid)
    {
        //?????????????????????????????????
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


        return AjaxResult.error("??????????????????");
    }

    /**
     * ????????????????????????
     */
    @Log(title = "??????/??????????????????", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, ReportInfoVo reportInfoVo)
    {
        List<ReportExcelVo> list = reportInfoService.selectReportExcel(reportInfoVo);
        ExcelUtil<ReportExcelVo> util = new ExcelUtil<ReportExcelVo>(ReportExcelVo.class);
        util.exportExcel(response, list, "??????????????????","??????????????????");
    }

    /**
     * ??????????????????
     */
    @ApiOperation("??????????????????")
//    @PreAuthorize("@ss.hasPermi('system:report:exportStatistics')")
//    @Log(title = "??????????????????", businessType = BusinessType.EXPORT)
    @PostMapping("/exportStatistics")
    public void exportStatistics(HttpServletRequest request, HttpServletResponse response, ReportInfoVo reportInfoVo)
    {
        reportInfoService.exportStatistics(request,response,reportInfoVo);
    }





    /**
     * ??????????????????????????????
     */
    @ApiOperation("????????????/????????????????????????")
//    @PreAuthorize("@ss.hasPermi('system:report:query')")
    @GetMapping("/detail")
    public AjaxResult getInfo( String id)
    {

        return reportInfoService.selectReportInfoVoById(id);

    }

    /**
     * ??????????????????
     */
    @ApiOperation("??????????????????")
  /*  @PreAuthorize("@ss.hasPermi('system:report:add')")*/
//    @Log(title = "??????/????????????", businessType = BusinessType.INSERT)
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
            webSocket.sendOneMessage("112","?????????????????????????????????");
            return  AjaxResult.success(id);
        } else if (i == -1) {
            return AjaxResult.error(Integer.valueOf(ResultErrorCode.ID_REPEAT.getCode()),ResultErrorCode.ID_REPEAT.getMsg());
        }
        return AjaxResult.error(Integer.valueOf(ResultErrorCode.INTERNAL_SERVER_ERROR.getCode()),ResultErrorCode.INTERNAL_SERVER_ERROR.getMsg());
    }


    /**
     * ???????????????
     */
 /*     @PreAuthorize("@ss.hasPermi('system:report:add')")*/
//    @Log(title = "??????/????????????", businessType = BusinessType.INSERT)
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
     * ????????????/????????????
     */
    @PreAuthorize("@ss.hasPermi('system:report:edit')")
    @Log(title = "??????/????????????", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody ReportInfo reportInfo)
    {
        return toAjax(reportInfoService.updateReportInfo(reportInfo));
    }

    /**
     * ????????????/????????????
     */
    @PreAuthorize("@ss.hasPermi('system:report:remove')")
    @Log(title = "??????/????????????", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable String[] ids)
    {
        return toAjax(reportInfoService.deleteReportInfoByIds(ids));
    }



    /**
     * ?????????????????????????????????
     */
    @ApiOperation("?????????????????????????????????")
    @ApiImplicitParam(name = "id", value = "??????ID", required = true, dataType = "string", paramType = "path", dataTypeClass = String.class)
    @PreAuthorize("@ss.hasPermi('system:report:downReport')")
    @Log(title = "?????????????????????????????????", businessType = BusinessType.OTHER)
    @PostMapping("/downReport/{id}")
    public AjaxResult downReport(@PathVariable("id") String id)
    {
        if (StringUtils.isEmpty(id)){
            return AjaxResult.error(ResultErrorCode.PARAMS_MISS.getCode(),ResultErrorCode.PARAMS_MISS.getMsg());
        }
        webSocket.sendOneMessage("102","?????????????????????????????????");
        return toAjax(reportInfoService.downReport(id,getUserId()));
//        return toAjax(reportInfoService.downReport(id,188L));
    }




    /**
     * ????????????????????????????????????
     */
    @ApiOperation("????????????????????????????????????")
    @ApiImplicitParam(name = "id", value = "??????ID", required = true, dataType = "string", paramType = "path", dataTypeClass = String.class)
    @Log(title = "????????????????????????????????????", businessType = BusinessType.OTHER)
    @PostMapping("/downReport2/{id}")
    public AjaxResult downReport2(@PathVariable("id") String id)
    {
        if (StringUtils.isEmpty(id)){
            return AjaxResult.error(ResultErrorCode.PARAMS_MISS.getCode(),ResultErrorCode.PARAMS_MISS.getMsg());
        }
        webSocket.sendOneMessage("102","?????????????????????????????????");
        return toAjax(reportInfoService.downReport2(id,getUserId()));
//        return toAjax(reportInfoService.downReport(id,188L));
    }
    /**
     * ??????????????????
     */
    @ApiOperation("submitReport")
    @ApiImplicitParam(name = "id", value = "??????ID", required = true, dataType = "string", paramType = "path", dataTypeClass = String.class)
    @PreAuthorize("@ss.hasPermi('system:report:submitReport')")
    @Log(title = "??????????????????", businessType = BusinessType.OTHER)
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
     * ?????????????????????????????????
     */
    @ApiOperation("userEvaluate")
    @ApiImplicitParam(name = "id", value = "??????ID", required = true, dataType = "string", paramType = "path", dataTypeClass = String.class)
//    @PreAuthorize("@ss.hasPermi('system:report:userEvaluate')")
//    @Log(title = "?????????????????????????????????", businessType = BusinessType.OTHER)
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
     * ???????????????????????????????????????
     */
    @ApiOperation("userHand")
    @ApiImplicitParam(name = "id", value = "??????ID", required = true, dataType = "string", paramType = "path", dataTypeClass = String.class)
//    @PreAuthorize("@ss.hasPermi('system:report:userHand')")
//    @Log(title = "?????????????????????????????????????????????", businessType = BusinessType.OTHER)
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
     * ?????????????????????????????????????????????????????????
     */
    @ApiOperation("examine")
    @ApiImplicitParam(name = "id", value = "??????ID", required = true, dataType = "string", paramType = "path", dataTypeClass = String.class)
//    @PreAuthorize("@ss.hasPermi('system:report:userHand')")
//    @Log(title = "?????????????????????????????????????????????", businessType = BusinessType.OTHER)
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
     * ????????????ID????????????
     */
    @ApiOperation("getFileListById")
//    @PreAuthorize("@ss.hasPermi('system:report:userEvaluate')")
    @Log(title = "????????????ID????????????", businessType = BusinessType.OTHER)
    @GetMapping("/getFileListById")
    public AjaxResult getFileListById(String id)
    {
        return AjaxResult.success("??????",reportInfoService.getFileListById(id));
    }



    public static void main(String[] args) {


        System.out.println(UUID.randomUUID().toString().replace("-", ""));
        System.out.println(UUID.randomUUID().toString().replace("-", ""));
        System.out.println(UUID.randomUUID().toString().replace("-", ""));


    }


    /**
     * ????????????????????????
     */
    @ApiOperation("homeNumAll")
    @ApiImplicitParam(name = "id", value = "??????ID", required = true, dataType = "string", paramType = "path", dataTypeClass = String.class)
//    @PreAuthorize("@ss.hasPermi('system:report:userEvaluate')")
    @Log(title = "?????????????????????????????????", businessType = BusinessType.OTHER)
    @GetMapping("/homeNumAll")
    public AjaxResult homeNumAll()
    {
        SysUser user = getLoginUser().getUser();
        return AjaxResult.success("??????",reportInfoService.homeNumAll(user));
    }

    /**
     * ????????????????????????
     */
    @ApiOperation("homePieChart")
//    @PreAuthorize("@ss.hasPermi('system:report:userEvaluate')")
    @Log(title = "????????????????????????", businessType = BusinessType.OTHER)
    @PostMapping("/homePieChart")
    public AjaxResult homePieChart(@RequestBody DateQueryVo dateQueryVo)
    {
        SysUser user = getLoginUser().getUser();
        String startTime = dateQueryVo.getStartTime();
        String endTime = dateQueryVo.getEndTime();
        return AjaxResult.success("??????",reportInfoService.homePieChart(startTime,endTime,user));
    }

    /**
     * ????????????????????????
     */
    @ApiOperation("homePieChartByTime")
//    @PreAuthorize("@ss.hasPermi('system:report:userEvaluate')")
    @Log(title = "????????????????????????,??????????????????", businessType = BusinessType.OTHER)
    @PostMapping("/homePieChartByTime")
    public AjaxResult homePieChartByTime(@RequestBody DateQueryVo dateQueryVo)
    {
        SysUser user = getLoginUser().getUser();
        String startTime = dateQueryVo.getStartTime();
        String endTime = dateQueryVo.getEndTime();
        return AjaxResult.success("??????",reportInfoService.homePieChart(startTime,endTime,user));
    }

    /**
     * ???????????????????????????
     */
    @ApiOperation("homeLineChart")
//    @PreAuthorize("@ss.hasPermi('system:report:userEvaluate')")
    @Log(title = "???????????????????????????", businessType = BusinessType.OTHER)
    @GetMapping("/homeLineChart")
    public AjaxResult homeLineChart()
    {
        SysUser user = getLoginUser().getUser();

        return AjaxResult.success("??????",reportInfoService.homeLineChart(user));
    }

    /**
     * ???????????????????????????
     */
    @ApiOperation("homeBarChart")
    @Log(title = "???????????????????????????", businessType = BusinessType.OTHER)
    @GetMapping("/homeBarChart")
    public AjaxResult homeBarChart()
    {
        return AjaxResult.success("??????",reportInfoService.barNum());
    }

    /**
     * ??????-?????????????????????/??????????????????
     */
    @ApiOperation("homeYearBarChart")
    @Log(title = "??????-??????????????????????????????/??????????????????", businessType = BusinessType.OTHER)
    @GetMapping("/homeYearBarChart")
    public AjaxResult homeYearBarChart()
    {
        return AjaxResult.success("??????",reportInfoService.homeYearBarChart());
    }



    @PostMapping("/exportDocx")
    public  void exportDocx( HttpServletResponse response, String id) throws Exception {

       this.reportInfoService.exportDocx(response,id);

    }


}
