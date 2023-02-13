package com.ruoyi.common.utils;

import com.aliyun.oss.HttpMethod;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.*;
import com.ruoyi.common.utils.file.FileUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.mockito.internal.util.io.IOUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class OssUtils {

    private static final Logger log = LoggerFactory.getLogger(OssUtils.class);


    //oss客户端
    private static OSS ossClient;
    //区域。 杭州 北京。 美国。。。
    private static String endpoint;
    //用户id
    private static String accessKeyId;
    //秘钥
    private static String accessKeySecret;
    //bucket名称
    private static String bucketName;
    //创建ossClient 的工具类
    private static OSSClientBuilder ossClientBuilder;

    //初始化链接。 每次操作之前都需要  重新链接一下。  因为 操作结束 都 关闭链接了。
    private static void buildossClient() {
        if(ossClientBuilder==null){
            endpoint = SysPath.ENDPOINT;
            // Endpoint以杭州为例，其它Region请按实际情况填写。
            // 阿里云主账号AccessKey拥有所有API的访问权限，风险很高。强烈建议您创建并使用RAM账号进行API访问或日常运维，请登录 https://ram.console.aliyun.com 创建RAM账号。
            accessKeyId = SysPath.ACCESSKEYID;
            accessKeySecret = SysPath.ACCESSKEYSECRET;
            bucketName = SysPath.BUCKETNAME;
            // 创建OSSClient实例。
            ossClientBuilder=new OSSClientBuilder();
        }
        ossClient=ossClientBuilder.build(endpoint, accessKeyId, accessKeySecret);
    }



    //流式上传
    //    key   map结构 的 key ，   要保证唯一。  key 可以带文件夹结构  比如  aa/bb/cc/123
    //    inputStream  需要上传的字节数组流
    public static void uploadByInput(String key, ByteArrayInputStream inputStream){
        buildossClient();
        key = getKey(key);
// 创建PutObjectRequest对象。
        String content = "Hello OSS";
// <yourObjectName>表示上传文件到OSS时需要指定包含文件后缀在内的完整路径，例如abc/efg/123.jpg。
        PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, key,inputStream);

// 如果需要上传时设置存储类型与访问权限，请参考以下示例代码。
// ObjectMetadata metadata = new ObjectMetadata();
// metadata.setHeader(OSSHeaders.OSS_STORAGE_CLASS, StorageClass.Standard.toString());
// metadata.setObjectAcl(CannedAccessControlList.Private);
// putObjectRequest.setMetadata(metadata);

// 上传字符串。
        ossClient.putObject(putObjectRequest);

// 关闭OSSClient。
        ossClient.shutdown();
    }

    //上传单个文件
    //    key   map结构 的 key ，   要保证唯一。  key 可以带文件夹结构  比如  aa/bb/cc/123
    //    file  需要上传的文件
    public static   void uploadone(String key, File file) {
        buildossClient();
        // 创建PutObjectRequest对象。
        PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, key, file);
        // 如果需要上传时设置存储类型与访问权限，请参考以下示例代码。
        // ObjectMetadata metadata = new ObjectMetadata();
        // metadata.setHeader(OSSHeaders.OSS_STORAGE_CLASS, StorageClass.Standard.toString());
        // metadata.setObjectAcl(CannedAccessControlList.Private);
        // putObjectRequest.setMetadata(metadata);

        // 上传文件。
        PutObjectResult putObjectResult = ossClient.putObject(putObjectRequest);
        // 关闭OSSClient。
        ossClient.shutdown();
    }


    //上传一个文件夹      上传后的  key  可以自己修改逻辑。  这里写的 是  文件夹全路径加文件名
    //    file  需要上传的文件
    public static   void uploadmany(File file) {
        buildossClient();
        // 创建PutObjectRequest对象。
        File[] files = file.listFiles();
        for (File file1 : files) {
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, file.getPath()+file1.getName(), file1);

            // 如果需要上传时设置存储类型与访问权限，请参考以下示例代码。
            // ObjectMetadata metadata = new ObjectMetadata();
            // metadata.setHeader(OSSHeaders.OSS_STORAGE_CLASS, StorageClass.Standard.toString());
            // metadata.setObjectAcl(CannedAccessControlList.Private);
            // putObjectRequest.setMetadata(metadata);

            // 上传文件。
            ossClient.putObject(putObjectRequest);
        }
        // 关闭OSSClient。
        ossClient.shutdown();
    }

    //    下载到本地文件
    //    objectName  oss上文件名    map 的 key
    //    下载的文件 保存到本地 上的 file
    public void dowload2File(String objectName,File file) {
        buildossClient();
        // 下载OSS文件到本地文件。如果指定的本地文件存在会覆盖，不存在则新建。
        ossClient.getObject(new GetObjectRequest(bucketName, objectName), file);
        // 关闭OSSClient。
        ossClient.shutdown();
    }


    //    流式下载    需要根据业务 场景 具体改造
    //    objectName  oss上文件名    map 的 key
    public static void dowloadB2BufferedReader(String objectName) throws IOException {
        buildossClient();
        // ossObject包含文件所在的存储空间名称、文件名称、文件元信息以及一个输入流。
        objectName = getKey(objectName);
        OSSObject ossObject = ossClient.getObject(bucketName, objectName);
        // 读取文件内容。
        System.out.println("Object content:");
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(ossObject.getObjectContent()));
            while (true) {
                String line = reader.readLine();
                if (line == null) break;

                System.out.println("\n" + line);
            }
            // 数据读取完成后，获取的流必须关闭，否则会造成连接泄漏，导致请求无连接可用，程序无法正常工作。
            reader.close();

        } catch (Exception e) {
            // ...
        } finally {
            if(reader!= null){
                reader.close();
            }
        }
        // 关闭OSSClient。
        ossClient.shutdown();
    }

    public static void dowloadB2BufferedReader2(String objectName, HttpServletRequest request, HttpServletResponse response, String fileName) throws IOException {
        buildossClient();
        String key = getKey(objectName);
        OSSObject ossObject = ossClient.getObject(bucketName, key);
        InputStream in = ossObject.getObjectContent();
        OutputStream out = null;
        //根据浏览器类型,设置响应头
        String agent = request.getHeader("User-Agent");
        if (agent.contains("Firefox")) {  // 如果是火狐浏览器
            fileName = new String(fileName.getBytes("UTF-8"), "ISO-8859-1");
        } else {  // 其它浏览器
            fileName = new String(fileName.getBytes("UTF-8"), "ISO-8859-1");
            fileName = fileName.replace("+", " ");
        }
        // 设置相应头，控制浏览器下载该文件，这里就是会出现当你点击下载后，出现的下载地址框
        response.setHeader("content-disposition",
                "attachment;filename=\"" + fileName + "\"");
        response.setContentType("application/octet-stream");
        out = response.getOutputStream();
        // 创建缓冲区
        byte buffer[] = new byte[1024];
        int len = 0;
        try {
            while ((len = in.read(buffer)) > 0) {
                out.write(buffer, 0, len);
            }
            out.flush();
            FileUtils.close(in, out);
            ossClient.shutdown();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            FileUtils.close(in, out);
            ossClient.shutdown();
        }
    }

    //带进度条下载
    //    objectName  oss上文件名    map 的 key
    //    下载的文件 保存到本地 上的 file
    public static void dowloadwithProgressListener(String objectName,File file) {
        buildossClient();
        try {
            // 带进度条的下载。
          /*  ossClient.getObject(new GetObjectRequest(bucketName, objectName).
                    <GetObjectRequest>withProgressListener(new GetObjectProgressListener()),file);*/
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 关闭OSSClient。
        ossClient.shutdown();
    }


    public static void previewFile(String objectName, HttpServletRequest request,
                                   HttpServletResponse response,String fileName,String extension) throws IOException {
        buildossClient();
        InputStream fis = null;
        OutputStream os = response.getOutputStream();
        objectName = getKey(objectName);
        OSSObject ossObject = ossClient.getObject(bucketName, objectName);
        fis = ossObject.getObjectContent();
        if (extension.contains("jpg") || extension.contains("jpeg")){
            response.setContentType(MediaType.IMAGE_JPEG_VALUE);
        }else if(extension.contains("png")){
            response.setContentType(MediaType.IMAGE_PNG_VALUE);
        }else if(extension.contains("gif")){
            response.setContentType(MediaType.IMAGE_GIF_VALUE);
        }else if(extension.contains("pdf")){
            response.setContentType(MediaType.APPLICATION_PDF_VALUE);
        }else if(extension.contains("bmp")){
            response.setContentType("image/bmp");
        }
        //response.addHeader("Content-Length", "" +fileSize);
               /* Response.Body body = res.body();
                fis = body.asInputStream();*/
        byte[] buffer = new byte[1024];
        boolean var8 = false;
        try {
            int len;
            while((len = fis.read(buffer)) > 0) {
                os.write(buffer, 0, len);
            }
            os.flush();
            FileUtils.close(fis, os);
            ossClient.shutdown();
        } catch (IOException var13) {
            log.error(var13.getMessage());
        } finally {
            FileUtils.close(fis, os);
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }
    }



    //删除单个文件
    //    objectName  oss上文件名    map 的 key
    public static void delOne(String objectName){
        buildossClient();
        // 删除文件。如需删除文件夹，请将ObjectName设置为对应的文件夹名称。如果文件夹非空，则需要将文件夹下的所有object删除后才能删除该文件夹。
        ossClient.deleteObject(bucketName, objectName);
        // 关闭OSSClient。
        ossClient.shutdown();

    }


    //    删除多个文件
    //    参数 	描述 	方法
    //    Keys 	需要删除的文件。 	setKeys(List<String>)
    //    quiet 	返回模式。true表示简单模式，false表示详细模式。默认为详细模式。 	setQuiet(boolean)
    //    encodingType 	对返回的文件名称进行编码。编码类型目前仅支持url。 	setEncodingType(String)
    public static void delMany(List<String> keys){
        buildossClient();
        DeleteObjectsResult deleteObjectsResult = ossClient.deleteObjects(new DeleteObjectsRequest(bucketName).withKeys(keys));
        List<String> deletedObjects = deleteObjectsResult.getDeletedObjects();

        // 关闭OSSClient。
        ossClient.shutdown();

    }


    //    删除指定前缀（prefix）的文件
    //    prefix 指定前缀   (文件名不带后缀。  要带上文件夹   比如  aa/1）
    public static void delManyByprefix(String prefix){
        buildossClient();
        // 列举所有包含指定前缀的文件并删除。
        String nextMarker = null;
        ObjectListing objectListing = null;
        do {
            ListObjectsRequest listObjectsRequest = new ListObjectsRequest(bucketName)
                    .withPrefix(prefix)
                    .withMarker(nextMarker);

            objectListing = ossClient.listObjects(listObjectsRequest);
            if (objectListing.getObjectSummaries().size() > 0) {
                List<String> keys = new ArrayList<String>();
                for (OSSObjectSummary s : objectListing.getObjectSummaries()) {
                    System.out.println("key name: " + s.getKey());
                    keys.add(s.getKey());
                }
                DeleteObjectsRequest deleteObjectsRequest = new DeleteObjectsRequest(bucketName).withKeys(keys);
                ossClient.deleteObjects(deleteObjectsRequest);
            }

            nextMarker = objectListing.getNextMarker();
        } while (objectListing.isTruncated());

        // 关闭OSSClient。
        ossClient.shutdown();
    }

    //    列举文件
    public static void listObjects(){
        buildossClient();
        ObjectListing objectListing = ossClient.listObjects(bucketName);
// objectListing.getObjectSummaries获取所有文件的描述信息。
        for (OSSObjectSummary objectSummary : objectListing.getObjectSummaries()) {
            System.out.println(" - " + objectSummary.getKey() + "  " +
                    "(size = " + objectSummary.getSize() + ")");
        }

// 关闭OSSClient。
        ossClient.shutdown();

    }

    /**
     * oss获取文件下载路径
     * @param key
     * @return
     */
    public static URL getUrl(String key){
        buildossClient();

        // 设置URL过期时间为1小时
        Date expiration = new Date(new Date().getTime() + 3600 * 10000);

        // 生成URL
        URL url = ossClient.generatePresignedUrl(bucketName, key, expiration);

        return url;
    }



    // 以下是测试
    //测试
    public static void main(String[] args) throws IOException, ParseException {
//        delManyByprefix("11/更新user表中的curorg_id");
//        log.info("---------------删除前-----------------");
//        listObjects();

//        uploadAll();
//        uploadone("33333",new File("C:\\Documents\\Downloads\\jetbrains-agent-latest\\jetbrains-agent"));
//        uploadmany(new File("C:\\Documents\\Downloads\\jetbrains-agent-latest\\jetbrains-agent"));

//        delOne("1234.docx");

//        List<String> list = new ArrayList<String>();
//        list.add("D:\\test\\01\\c9f0147f0a1a4b75becbd9b88052d1ab\\c9f0147f0a1a4b75becbd9b88052d1ab");
//        list.add("D:\\test\\02\\d5b94c02e68a48c4bce7dde1da867daa\\d5b94c02e68a48c4bce7dde1da867daa");
//        list.add("D:\\test\\03\\0d44de5131e34100819cfed17c9e65ca.pdf\\0d44de5131e34100819cfed17c9e65ca.pdf");
//        list.add("D:\\test\\03\\1224355491374aa6a08cc7f41bd3d1b3.docx\\1224355491374aa6a08cc7f41bd3d1b3.docx");
//        list.add("D:\\test\\03\\1587b9ad0a2d4c1991f9c0fe83c8442a.pdf\\1587b9ad0a2d4c1991f9c0fe83c8442a.pdf");
//        list.add("D:\\test\\03\\a663b362eea4473db5867ef8d79663ed.docx\\a663b362eea4473db5867ef8d79663ed.docx");
//        list.add("D:\\test\\03\\ad657cae09654ec3a6660ff8acf4431c.docx\\ad657cae09654ec3a6660ff8acf4431c.docx");
//        list.add("D:\\test\\04\\536eba274a52403380584a5024672c50\\536eba274a52403380584a5024672c50");
//        list.add("D:\\test\\48c2ce86ea54404cb425fa84f926c25b\\48c2ce86ea54404cb425fa84f926c25b");
//        list.add("D:\\test\\83d1cfa5cec84abbb6ebde593e98cb37\\83d1cfa5cec84abbb6ebde593e98cb37");
//        list.add("D:\\test\\a8dff3e3ad604c3a9036bb3b67dc0b6c\\a8dff3e3ad604c3a9036bb3b67dc0b6c");
//
//        delMany(list);

        listObjects();

        String key = "ubp/home/upload/gztzfj/00039e6ecfef4d22bb40e7060936ee64";
    }


    //测试将某个文件夹下的 所有文件上传到oss
    public static void  uploadAll() throws ParseException {
        buildossClient();
        File file=new File("C:\\Documents\\Downloads\\jetbrains-agent-latest\\jetbrains-agent");
        File[] files = file.listFiles();
        for (File file1 : files) {
            uploadone(file1.getName(),file1);
        }

        // wangli测试，请勿删除
//        testUploadFile("d:\\test");
    }

    /**
     * 遍历XX路径下的文件，并上传到OSS
     * @param path
     * @throws ParseException
     */
    public static void testUploadFile(String path) throws ParseException {
//        String path = "d:\\test";

        // 获取所有文件
        List<File> list = getFiles(path, new ArrayList<File>());
        if(CollectionUtils.isEmpty(list)){
            log.info("未获取到任何文件，请检查！");
            return;
        }
        log.info("共获取"+ list.size() +"文件");

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        // 获取当天零点的毫秒值
        Long currentDateTime = getStartTime();
        log.info("当天零点的毫秒值："+ currentDateTime);
        Date currentDate = new Date(currentDateTime);
//      log.info("当天起始时间："+ currentDate);
        log.info("当天时间："+ sdf.format(currentDate.getTime()));

        // 获取指定时间的毫秒值
        Date specialDate = sdf.parse("2020-02-12 00:00:00");
        Long specialTime = specialDate.getTime();
        log.info("指定时间的毫秒值："+ specialTime);
        Date temp = new Date(specialTime);
        log.info("指定时间："+ sdf.format(temp.getTime()));

        // 遍历文件并判断是否需要上传
        for(File file:list){
            // 文件的最后更新时间
            Long time = file.lastModified();

            // 第1种方式
            Date calendar = new Date(time);
            // 最后修改时间的毫秒值
            log.info("当前文件 ["+ file.getName() +"]的最后更新时间为"+ sdf.format(calendar.getTime()) +"(毫秒值="+ calendar.getTime() +")");

//            // 第2种方式
//            Calendar calendar = Calendar.getInstance();
//            calendar.setTimeInMillis(time);
//            // 最后修改时间的毫秒值
//            log.info("当前文件["+ file.getName() +"]的最后更新时间为"+ calendar.get(Calendar.YEAR) +"-"+ calendar.get(Calendar.MONTH) +"-"+ calendar.get(Calendar.DATE) +" "+ calendar.get(Calendar.HOUR_OF_DAY) +":"+ calendar.get(Calendar.MINUTE) +":"+ calendar.get(Calendar.SECOND) +"(毫秒值="+ calendar.getTime() +") ");

            // 如果文件最后更新的时间大于specialTime值，则跳过
            if(time > specialTime){
                continue;
            }

            log.info("开始上传......"+ "["+ file.getName() +"]");
//            // 开始上传
//            uploadone(file.getName(), file);
            log.info("上传完毕......"+ "["+ file.getName() +"]");
        }

    }

    /**
     * 获取当天的开始时间 单位：毫秒
     * @return
     */
    private static Long getStartTime(){
        Calendar todayStart = Calendar.getInstance();
        todayStart.set(Calendar.HOUR_OF_DAY, 0);
        todayStart.set(Calendar.MINUTE, 0);
        todayStart.set(Calendar.SECOND, 0);
        todayStart.set(Calendar.MILLISECOND, 0);

        return todayStart.getTime().getTime();
    }

    /**
     * 获取目录下所有文件
     * @param realpath
     * @param files
     * @return
     */
    public static List<File> getFiles(String realpath, List<File> files) {
        File realFile = new File(realpath);
        if (realFile.isDirectory()) {
            File[] subfiles = realFile.listFiles();
            for (File file : subfiles) {
                if (file.isDirectory()) {
                    getFiles(file.getAbsolutePath(), files);
                } else {
                    files.add(file);
                }
            }
        }

        return files;
    }

    /**
     * 获取目录下所有文件(按时间排序)
     * @param path
     * @return
     */
    public static List<File> getFileSort(String path) {
        List<File> list = getFiles(path, new ArrayList<File>());

        // 排序
        if (list != null && list.size() > 0) {
            Collections.sort(list, new Comparator<File>() {
                public int compare(File file, File newFile) {
                    // 降序<  升序>
                    if (file.lastModified() < newFile.lastModified()) {
                        return 1;
                    } else if (file.lastModified() == newFile.lastModified()) {
                        return 0;
                    } else {
                        return -1;
                    }
                }
            });
        }

        return list;
    }

    public static String getKey(String filepath){
        String path = new File(filepath).getPath();
        path = path.replaceAll("\\\\", "/");
        if(path.startsWith("/")||path.startsWith("\\")){
            path=path.substring(1,path.length());
        }
        return path;
    }

//    private static final OSSClient OSS_CLIENT =
//            new OSSClient(OSS_ENDPOINT, OSS_ACCESS_KEY, OSS_SECRET);

    /**
     * 批量下载oss 文件 并打成zip 包 返回到response中
     *
     * @param fileNames
     *            oss上的文件名集合 如：product/image/3448275920.png  * @param zipFileName
     *            压缩包文件名
     * @param response
     *            HttpServletResponse
     */
    public static void batchDownLoadOssFile(List<String> fileNames, String zipFileName, HttpServletResponse response) {
        buildossClient();
        response.setCharacterEncoding("utf-8");
        response.setContentType("application/octet-stream");
        //要下载成什么类型的文件，这里直接加后缀
        response.setHeader("Content-Disposition", "attachment;fileName=" + zipFileName + ".zip");
        BufferedInputStream bis = null;
        ServletOutputStream outputStream = null;
        try {
            outputStream = response.getOutputStream();
            ZipOutputStream zos = new ZipOutputStream(outputStream);
            for (String fileName : fileNames) {
                fileName = getKey(fileName);
                Date expiration = new Date(System.currentTimeMillis() + 3600 * 1000);
                GeneratePresignedUrlRequest request = new GeneratePresignedUrlRequest(bucketName, fileName,
                        HttpMethod.GET);
                // 设置过期时间。
                request.setExpiration(expiration);
//                request.setContentType("application/pdf");
                // 生成签名URL（HTTP GET请求）。
                URL signedUrl = ossClient.generatePresignedUrl(request);
                // 使用签名URL发送请求。
                OSSObject ossObject = ossClient.getObject(signedUrl, new HashMap<>());
                if (ossObject != null) {
                    InputStream inputStream = ossObject.getObjectContent();
                    byte[] buffs = new byte[1024 * 10];

                    String zipFile = fileName.substring(fileName.lastIndexOf("/") + 1);
                    ZipEntry zipEntry = new ZipEntry(zipFile);
                    zos.putNextEntry(zipEntry);
                    bis = new BufferedInputStream(inputStream, 1024 * 10);

                    int read;
                    while ((read = bis.read(buffs, 0, 1024 * 10)) != -1) {
                        zos.write(buffs, 0, read);
                    }
                    ossObject.close();
                }
            }
            zos.close();
//            outputStream.flush();
//            outputStream.close();
            //关闭流
            IOUtil.close(bis);
        } catch (Exception e) {
            log.error("打包下载发生异常：",e);
        } finally {
            // 关闭流
            try {
                outputStream.flush();
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }



}
