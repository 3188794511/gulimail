package com.lj.gulimail.util;

import cn.hutool.core.lang.UUID;
import com.aliyun.oss.OSS;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AliyunOssUtil {
    /**
     * 阿里云文件上传
     * @param ossClient
     * @param bucketName
     * @param filePath
     * @return
     */
    public static String upload(OSS ossClient,String bucketName,String filePath){
        try {
            String dir = new SimpleDateFormat("yyyy-MM-dd").format(new Date()) + "/";
            //生成文件完整路径
            String objectName = dir + UUID.randomUUID() + ".jpg";
            //输入流
            InputStream inputStream = new FileInputStream(filePath);
            // 创建PutObject请求。
            ossClient.putObject(bucketName, objectName, inputStream);
            return objectName;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return "";
        }
        finally {
//            if (ossClient != null) {
//                ossClient.shutdown();
//            }
        }
    }

    /**
     * 查找本地文件路径
     * @param fileName
     * @param root
     * @return
     */
    public static boolean isFind = false;//判断文件是否找到的标识
    public static String filePath = "";//待查找文件的路径
    public static void findFilePath(String fileName,String root) throws Exception {
        File rootPath = new File(root);
        File[] files = rootPath.listFiles();
        if (files != null && files.length > 0){
            for (File file : files) {
                if (file.isFile()){
                    if (file.getName().equals(fileName)){
                        filePath = file.getAbsolutePath();
                        isFind = true;
                    }else{
                        continue;
                    }
                }else{
                    findFilePath(fileName,file.getAbsolutePath());
                }
            }
        }
    }

}
