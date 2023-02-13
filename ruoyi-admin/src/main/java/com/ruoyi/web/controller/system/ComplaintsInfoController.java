package com.ruoyi.web.controller.system;

import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.common.enums.ResultErrorCode;
import com.ruoyi.common.utils.AESUtil;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.domain.ReportInfo;
import com.ruoyi.system.domain.vo.ReportInfoVo;
import com.ruoyi.system.service.ISysRoleService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
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
import com.ruoyi.system.domain.ComplaintsInfo;
import com.ruoyi.system.service.IComplaintsInfoService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 举报信息Controller
 * 
 * @author ruoyi
 * @date 2022-08-04
 */
@RestController
@RequestMapping("/system/complaints")
public class ComplaintsInfoController extends BaseController
{
    @Autowired
    private IComplaintsInfoService complaintsInfoService;

    @Autowired
    private ISysRoleService iSysRoleService;
    @Autowired
    private WebSocket webSocket;
    /**
     * 查询举报信息列表
     */
    @PreAuthorize("@ss.hasPermi('system:complaints:list')")
    @GetMapping("/list")
    public TableDataInfo list(ComplaintsInfo complaintsInfo)
    {
        complaintsInfo.mapToInfo(complaintsInfo.getParams(),complaintsInfo);
        startPage();
        List<ComplaintsInfo> list = complaintsInfoService.selectComplaintsInfoList(complaintsInfo);
        return getDataTable(list);
    }

    /**
     * 导出举报信息列表
     */
    @Log(title = "举报信息", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, ComplaintsInfo complaintsInfo)
    {
        List<ComplaintsInfo> list = complaintsInfoService.selectComplaintsInfoList(complaintsInfo);
        ExcelUtil<ComplaintsInfo> util = new ExcelUtil<ComplaintsInfo>(ComplaintsInfo.class);
        util.exportExcel(response, list, "举报信息数据","举报信息报表");
    }

    /**
     * 获取举报信息详细信息
     */
    @GetMapping("/detail")
    public AjaxResult getInfo(String id)
    {
        return complaintsInfoService.getInfo(id);

    }

    /**
     * 新增举报信息
     */
    @ApiOperation("新增举报信息")
/*    @PreAuthorize("@ss.hasPermi('system:info:add')")
    @Log(title = "举报信息", businessType = BusinessType.INSERT)*/
    @PostMapping("/add")
    public AjaxResult add(@RequestBody Map<String,String> map)
    {
        String params = map.get("params");


        String decrypt = AESUtil.decrypt(params);

        ComplaintsInfo complaintsInfo = JSONObject.parseObject(decrypt,ComplaintsInfo.class);

        int i = complaintsInfoService.insertComplaintsInfo(complaintsInfo);
        if (i >0){
            String id = complaintsInfo.getId();
            webSocket.sendOneMessage("112","刚刚有一条新的举报信息");
            return AjaxResult.success(id);
        }else if (i == -1){
            return AjaxResult.error(Integer.valueOf(ResultErrorCode.ID_REPEAT.getCode()),ResultErrorCode.ID_REPEAT.getMsg());
        }
        return toAjax(complaintsInfoService.insertComplaintsInfo(complaintsInfo));
    }



    /**
     * 完成举报案件
     */
    @ApiOperation("submitComplaints")
    @ApiImplicitParam(name = "id", value = "举报ID", required = true, dataType = "string", paramType = "path", dataTypeClass = String.class)
    @PreAuthorize("@ss.hasPermi('system:complaints:submitComplaints')")
    @Log(title = "完成举报案件", businessType = BusinessType.OTHER)
    @PostMapping("/submitComplaints")
    public AjaxResult submitComplaints(@RequestBody ComplaintsInfo complaintsInfo)
    {
        complaintsInfo.mapToInfo(complaintsInfo.getParams(),complaintsInfo);

        String id = complaintsInfo.getId();
        String opinion = complaintsInfo.getOpinion();
        if (StringUtils.isEmpty(id)){
            return AjaxResult.error(ResultErrorCode.PARAMS_MISS.getCode(),ResultErrorCode.PARAMS_MISS.getMsg());
        }
        return toAjax(complaintsInfoService.submitComplaints(id,opinion,getUserId()));
    }

    /**
     * 用户对办理情况进行评价
     */
    @ApiOperation("userEvaluate")
    @ApiImplicitParam(name = "id", value = "举报ID", required = true, dataType = "string", paramType = "path", dataTypeClass = String.class)
//    @PreAuthorize("@ss.hasPermi('system:report:userEvaluate')")
//    @Log(title = "用户对办理情况进行评价", businessType = BusinessType.OTHER)
    @PostMapping("/userEvaluate")
    public AjaxResult userEvaluate(@RequestBody ComplaintsInfo complaintsInfo)
    {
        complaintsInfo.mapToInfo(complaintsInfo.getParams(),complaintsInfo);
        String id = complaintsInfo.getId();
        String evaluate = complaintsInfo.getEvaluate();
        if (StringUtils.isEmpty(id)){
            return AjaxResult.error(ResultErrorCode.PARAMS_MISS.getCode(),ResultErrorCode.PARAMS_MISS.getMsg());
        }
        return toAjax(complaintsInfoService.userEvaluate(id,evaluate));
    }




    /**
     * 修改举报信息
     */
    @PreAuthorize("@ss.hasPermi('system:complaints:edit')")
    @Log(title = "举报信息", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody ComplaintsInfo complaintsInfo)
    {
        return toAjax(complaintsInfoService.updateComplaintsInfo(complaintsInfo));
    }

    /**
     * 删除举报信息
     */
    @PreAuthorize("@ss.hasPermi('system:complaints:remove')")
    @Log(title = "举报信息", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable String[] ids)
    {
        return toAjax(complaintsInfoService.deleteComplaintsInfoByIds(ids));
    }


    @PostMapping("/exportDocx")
    public  void exportDocx( HttpServletResponse response, String id) throws Exception {

        this.complaintsInfoService.exportDocx(response,id);

    }
}
