package com.lj.gulimail.search;

import com.alibaba.fastjson.JSON;
import lombok.Data;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

@SpringBootTest
class GulimailSearchApplicationTests {

    @Autowired
    private RestHighLevelClient client;
    @Test
    void test01() {
        System.out.println(client);
    }

    @Test
    void test02() throws IOException {
        IndexRequest request = new IndexRequest("users");
        request.id("1");
        User user = new User("张三", 23);
        String jsonString = JSON.toJSONString(user);
        request.source(jsonString, XContentType.JSON);
        IndexResponse index = client.index(request, RequestOptions.DEFAULT);
        System.out.println(index);
    }

    @Data
    static class User{
        private String userName;
        private Integer age;

        public User(String userName, Integer age) {
            this.userName = userName;
            this.age = age;
        }
    }
}
