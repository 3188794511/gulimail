package com.lj.gulimail.controller;

import com.aliyun.oss.OSS;
import com.aliyun.oss.common.utils.BinaryUtil;
import com.aliyun.oss.model.MatchMode;
import com.aliyun.oss.model.PolicyConditions;
import com.lj.common.utils.R;
import com.lj.gulimail.util.AliyunOssUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import static com.lj.gulimail.constant.OssConstant.UPLOAD_ROOT_PATH;
import static com.lj.gulimail.constant.OssConstant.UPLOAD_URL;
import static com.lj.gulimail.util.AliyunOssUtil.filePath;

@RestController
@RequestMapping("/thirdparty")
@Slf4j
public class OSSController {
    @Autowired
    private OSS ossClient;
    @Value("${alibaba.cloud.oss.endpoint}")
    private String endpoint;
    @Value("${alibaba.cloud.oss.bucket}")
    private String bucket;
    @Value("${alibaba.cloud.access-key}")
    private String accessKey;
    @Value("${alibaba.cloud.secret-key}")
    private String secretKey;


    @RequestMapping("/oss/policy")
    public R policy(){
        // 填写Host地址，格式为https://bucketname.endpoint。
        String host = "https://" + bucket + "." + endpoint;
        // 设置上传回调URL，即回调服务器地址，用于处理应用服务器与OSS之间的通信。OSS会在文件上传完成后，把文件上传信息通过此回调URL发送给应用服务器。
        //String callbackUrl = "https://192.168.0.0:8888";
        // 设置上传到OSS文件的前缀，可置空此项。置空后，文件将上传至Bucket的根目录下。
        String dir = new SimpleDateFormat("yyyy-MM-dd").format(new Date()) + "/";
        Map<String, String> respMap = null;
        try {
            long expireTime = 30;
            long expireEndTime = System.currentTimeMillis() + expireTime * 1000;
            Date expiration = new Date(expireEndTime);
            PolicyConditions policyConds = new PolicyConditions();
            policyConds.addConditionItem(PolicyConditions.COND_CONTENT_LENGTH_RANGE, 0, 1048576000);
            policyConds.addConditionItem(MatchMode.StartWith, PolicyConditions.COND_KEY, dir);
            String postPolicy = ossClient.generatePostPolicy(expiration, policyConds);
            byte[] binaryData = postPolicy.getBytes("utf-8");
            String encodedPolicy = BinaryUtil.toBase64String(binaryData);
            String postSignature = ossClient.calculatePostSignature(postPolicy);
            respMap = new LinkedHashMap<String, String>();
            respMap.put("accessid", accessKey);
            respMap.put("policy", encodedPolicy);
            respMap.put("signature", postSignature);
            respMap.put("dir", dir);
            respMap.put("host", host);
            respMap.put("expire", String.valueOf(expireEndTime / 1000));
            // respMap.put("expire", formatISO8601Date(expiration));

        } catch (Exception e) {
            // Assert.fail(e.getMessage());
            System.out.println(e.getMessage());
        }
        return R.ok().put("data",respMap);
    }

    /**
     * 文件上传
     * @return
     */
    @PostMapping("/upload")
    public R upload(@RequestParam("file") MultipartFile uploadFile){
        try {
            String originalFilename = uploadFile.getOriginalFilename();
            AliyunOssUtil.findFilePath(originalFilename,UPLOAD_ROOT_PATH);
            String fileName = AliyunOssUtil.upload(ossClient, bucket, filePath);
            String fileUrl = UPLOAD_URL + fileName;
            log.info("生成的文件地址为:{}",fileUrl);
            return R.ok().put("data",fileUrl);
        } catch (Exception e) {
            e.printStackTrace();
            return R.error();
        }
    }
}
