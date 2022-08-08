package com.lj.gulimail.product;

import com.lj.gulimail.product.entity.CategoryEntity;
import com.lj.gulimail.product.service.BrandService;
import com.lj.gulimail.product.service.CategoryService;
import org.junit.jupiter.api.Test;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;

@SpringBootTest
class GulimailProductApplicationTests {
    @Resource
    private BrandService brandService;
    @Autowired
    private RedissonClient redissonClient;
    @Resource
    private CategoryService categoryService;

    @Test
    void name() {
        System.out.println(redissonClient);
    }

    @Test
    void testBrandServiceAdd() {
//        BrandEntity brand = new BrandEntity();
//        brand.setName("华为");
//        brandService.save(brand);
    }

    @Test
    void testCategoryService() {
        List<CategoryEntity> entityList = categoryService.listByTree();
        System.out.println(entityList);
    }

    @Test
    void testFindPathById() {
        System.out.println(Arrays.asList(categoryService.findPathById(255L)));
    }

    @Test
    void testEq() {
        Integer i1 = 128;
        Integer i2 = 128;
        long l1 = 128;
        System.out.println(i1 == i2);
        System.out.println(i1 == l1);
    }

    @Test
    void testGetById() {
        System.out.println(categoryService.getById(225));
    }
}
