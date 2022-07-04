package com.lj.gulimail.product;

import com.lj.gulimail.product.entity.CategoryEntity;
import com.lj.gulimail.product.service.BrandService;
import com.lj.gulimail.product.service.CategoryService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.List;

@SpringBootTest
class GulimailProductApplicationTests {
    @Resource
    private BrandService brandService;

    @Resource
    private CategoryService categoryService;

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

}
