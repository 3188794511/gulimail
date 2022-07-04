package com.lj.gulimail;

import com.aliyun.oss.OSS;
import com.lj.gulimail.util.AliyunOssUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.FileNotFoundException;

import static com.lj.gulimail.util.AliyunOssUtil.filePath;
import static com.lj.gulimail.util.AliyunOssUtil.isFind;

@SpringBootTest
class GulimailThirdPartyApplicationTests {
    @Autowired
    private OSS ossClient;

    @Value("${alibaba.cloud.oss.bucket}")
    private String bucketName;
    @Test
    void test1() {
        AliyunOssUtil.upload(ossClient,bucketName,"E:\\pic\\reggie-img\\resource\\057dd338-e487-4bbc-a74c-0384c44a9ca3.jpg");
    }

    @Test
    void test2() throws Exception {
        AliyunOssUtil.findFilePath("2a50628e-7758-4c51-9fbb-d37c61cdacad.jpg", "E:\\");
        if (isFind){
            System.out.println(filePath);
        }
        else{
            throw new FileNotFoundException();
        }
    }
}
