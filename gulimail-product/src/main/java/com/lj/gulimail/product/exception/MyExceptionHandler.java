package com.lj.gulimail.product.exception;

import com.lj.common.utils.R;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

import static com.lj.gulimail.product.enums.MyExceptionEnum.COMMON_EXCEPTION;
import static com.lj.gulimail.product.enums.MyExceptionEnum.VALID_EXCEPTION;

/**
 * 全局异常处理
 */
@RestControllerAdvice(basePackages = "com.lj.gulimail.product.controller")
public class MyExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public R validExceptionHandler(MethodArgumentNotValidException e){
        BindingResult bindingResult = e.getBindingResult();
        Map<String,String> map = new HashMap<>();
        bindingResult.getAllErrors().forEach(item -> {
            map.put(item.getObjectName(), item.getDefaultMessage());
        });
        return R.error(VALID_EXCEPTION.getCode(),VALID_EXCEPTION.getMsg()).put("data",map);
    }

    @ExceptionHandler(Exception.class)
    public R exceptionHandler(Exception e){
        return R.error(COMMON_EXCEPTION.getCode(),COMMON_EXCEPTION.getMsg()).put("data",e.getMessage());
    }

}
