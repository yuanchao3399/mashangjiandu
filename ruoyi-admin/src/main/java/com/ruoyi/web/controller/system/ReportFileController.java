package com.ruoyi.web.controller.system;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.servlet.http.HttpServletResponse;

import com.ruoyi.common.config.RuoYiConfig;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SysPath;
import com.ruoyi.common.utils.file.FileUploadUtils;
import com.ruoyi.common.utils.file.FileUtils;
import com.ruoyi.framework.config.ServerConfig;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import com.ruoyi.system.domain.ReportFile;
import com.ruoyi.system.service.IReportFileService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;
import org.springframework.web.multipart.MultipartFile;

/**
 * 举报/投诉文件上传Controller
 * 
 * @author ruoyi
 * @date 2022-07-22
 */
@RestController
@RequestMapping("/system/file")
public class ReportFileController extends BaseController
{
    @Autowired
    private IReportFileService reportFileService;

    @Autowired
    private ServerConfig serverConfig;



    private static final Logger log = LoggerFactory.getLogger(ReportFileController.class);

    /**
     * 查询举报/投诉文件上传列表
     */
    @PreAuthorize("@ss.hasPermi('system:file:list')")
    @GetMapping("/list")
    public TableDataInfo list(ReportFile reportFile)
    {
        startPage();
        List<ReportFile> list = reportFileService.selectReportFileList(reportFile);
        return getDataTable(list);
    }

    /**
     * 导出举报/投诉文件上传列表
     */
    @PreAuthorize("@ss.hasPermi('system:file:export')")
    @Log(title = "举报/投诉文件上传", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, ReportFile reportFile)
    {
        List<ReportFile> list = reportFileService.selectReportFileList(reportFile);
        ExcelUtil<ReportFile> util = new ExcelUtil<ReportFile>(ReportFile.class);
        util.exportExcel(response, list, "举报/投诉文件上传数据");
    }

    /**
     * 获取举报/投诉文件上传详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:file:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") String id)
    {
        return AjaxResult.success(reportFileService.selectReportFileById(id));
    }

    /**
     * 新增举报/投诉文件上传
     */
/*
    @PreAuthorize("@ss.hasPermi('system:file:add')")
*/
    @Log(title = "举报/投诉文件上传", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public AjaxResult add(@RequestBody ReportFile reportFile)
    {



        return toAjax(reportFileService.insertReportFile(reportFile));
    }

    /**
     * 修改举报/投诉文件上传
     */
    @PreAuthorize("@ss.hasPermi('system:file:edit')")
    @Log(title = "举报/投诉文件上传", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody ReportFile reportFile)
    {
        return toAjax(reportFileService.updateReportFile(reportFile));
    }

    /**
     * 删除举报/投诉文件上传
     */
    @PreAuthorize("@ss.hasPermi('system:file:remove')")
    @Log(title = "举报/投诉文件上传", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable String[] ids)
    {
        return toAjax(reportFileService.deleteReportFileByIds(ids));
    }

    /**
     * 删除举报/投诉文件上传-单文件刪除
     */
    @PostMapping("/deleteFile")
    public AjaxResult remove(String id)
    {
        return toAjax(reportFileService.deleteReportFileById(id));
    }



    /**
     * 通用上传请求（单个）
     */
    @PostMapping("/upload")
    public AjaxResult uploadFile(MultipartFile file,String id,String type) throws Exception
    {
        try
        {
            // 上传文件路径
            String filePath = RuoYiConfig.getUploadPath();
            //获取文件后缀
            String extension = FileUploadUtils.getExtension(file);

            //用于转换苹果手机上传视频的格式，MOV转MP4
            if ("MOV".equals(extension)){
                file = FileUploadUtils.enCoderMovToMp4(file);
            }
            // 上传并返回新文件名称
            String fileName = FileUploadUtils.upload(filePath,  file);

            String url = SysPath.FILEURL + fileName;
            ReportFile reportFile=new ReportFile();
            reportFile.setId(UUID.randomUUID().toString().replace("-", ""));
            reportFile.setPid(id);
            reportFile.setSize(Long.toString(file.getSize()));
            reportFile.setUrl(url);
            reportFile.setIsactive("Y");
            reportFile.setOsname( FileUtils.getName(fileName));
            reportFile.setCreateTime(DateUtils.getNowDate());
            reportFile.setFileName(file.getOriginalFilename());
            reportFile.setType(type);
            int index= fileName.lastIndexOf(".");
            String ex=fileName.substring(index);
            reportFile.setExtension(ex);
            reportFileService.insertReportFile(reportFile);

            Map<String,Object> map = new HashMap<String,Object>();
            map.put("id",reportFile.getId());
            AjaxResult ajax = AjaxResult.success(map);
            return ajax;

        }
        catch (Exception e)
        {
            log.error("下载文件失败",e);
            return AjaxResult.error(e.getMessage());
        }
    }
}
