package com.ruoyi.system.domain;

import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.List;

/**
 * 投诉/举报信息对象 report_info
 * 
 * @author yuanchao
 * @date 2022-07-19
 */
public class ReportFile extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键id */
    private String id;

    /** 业务id */
    private String pid;

    /** 是否有效 */
    @Excel(name = "是否有效")
    private String isactive;

    /** 服务类别 */
    @Excel(name = "文件路径")
    private String url;


    /** 文件名称 */
    @Excel(name = "文件名称")
    private String fileName;


    /** 扩展名 */
    @Excel(name = "扩展名")
    private String extension;

    /** 磁盘上名称 */
    @Excel(name = "磁盘上名称")
    private String   osname;

    /** 文件大小 */
    @Excel(name = "文件大小")
    private String size;

    /** 文件类型 */
    @Excel(name = "文件类型")
    private String type;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIsactive() {
        return isactive;
    }

    public void setIsactive(String isactive) {
        this.isactive = isactive;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public String getOsname() {
        return osname;
    }

    public void setOsname(String osname) {
        this.osname = osname;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }




    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }



    @Override
    public String toString() {
        return "ReportFile{" +
                "id='" + id + '\'' +
                ", pid='" + pid + '\'' +
                ", isactive='" + isactive + '\'' +
                ", url='" + url + '\'' +
                ", fileName='" + fileName + '\'' +
                ", extension='" + extension + '\'' +
                ", osname='" + osname + '\'' +
                ", size='" + size + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}
