package com.ruoyi.common.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
@Component
public class SysPath {

    public static String ENDPOINT;
    public static String ACCESSKEYID;
    public static String ACCESSKEYSECRET;
    public static String BUCKETNAME;
    public static String AUDIOPATH;

    public static String FILEURL;


    @Value("${oss.endpoint}")
    private String endpoint;
    @Value("${oss.accessKeyId}")
    private String accessKeyId;
    @Value("${oss.accessKeySecret}")
    private String accessKeySecret;
    @Value("${oss.bucketName}")
    private String bucketName;
    @Value("${oss.audioPath}")
    private String audioPath;

    @Value("${file.url}")
    private String fileUrl;


    //利用@PostConstruct将yml中配置的值赋给本地的变量
    @PostConstruct
    public void getSysPath(){
        ENDPOINT = this.endpoint;
        ACCESSKEYID = this.accessKeyId;
        ACCESSKEYSECRET = this.accessKeySecret;
        BUCKETNAME = this.bucketName;
//        FILEPREVIEWURL ="dev".equals(this.active)?"http://10.41.41.189:9877"+this.filePreviewUrl:"http://59.207.61.15:11361"+this.filePreviewUrl;
        AUDIOPATH = this.audioPath;

        FILEURL = this.fileUrl;


    }
}
