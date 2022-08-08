package com.lj.gulimail.product.web;

import com.lj.gulimail.product.entity.CategoryEntity;
import com.lj.gulimail.product.service.CategoryService;
import com.lj.gulimail.product.vo.Category2Vo;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RReadWriteLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Controller
@Slf4j
public class IndexController {
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private RedissonClient redissonClient;
    @GetMapping({"/","/index.html"})
    public String indexPage(Model model){
        //查找所有一级分类
        List<CategoryEntity> categoryEntityList = categoryService.getLevel1Categorys();
        model.addAttribute("categories", categoryEntityList);
        return "index";
    }

    @GetMapping("/index/json/catalog.json")
    @ResponseBody
    public Map<String,List<Category2Vo>> cateLogJson(){
        Map<String,List<Category2Vo>> data = categoryService.getLevel2Categorys();
        return data;
    }

    /**
     * 只有读锁时相当于无锁,可以并发读
     * 写锁是互斥锁,只有在写锁释放时,其他锁才能被占用
     * @return
     */
    @GetMapping("/write")
    @ResponseBody
    public String write(){
        String s = null;
        RReadWriteLock lock = redissonClient.getReadWriteLock("read-write-lock");
        RLock writeLock = lock.writeLock();
        writeLock.lock();
        try {
            log.info("{}获取写锁",Thread.currentThread().getName());
            s = UUID.randomUUID().toString();
            redisTemplate.opsForValue().set("value",s );
            Thread.sleep(30000);
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            writeLock.unlock();
            log.info("{}释放写锁",Thread.currentThread().getName());
        }
        return s;
    }

    @GetMapping("/read")
    @ResponseBody
    public String read(){
        RReadWriteLock lock = redissonClient.getReadWriteLock("read-write-lock");
        RLock readLock = lock.readLock();
        readLock.lock();
        String value;
        try {
            log.info("{}获取读锁",Thread.currentThread().getName());
            value = redisTemplate.opsForValue().get("value");
        } finally {
            readLock.unlock();
            log.info("{}释放读锁",Thread.currentThread().getName());
        }
        return value;
    }
}
