package com.test.domain;

import lombok.Data;

/**
 * @author xuewueagle@163.com
 * @desc 接口返回结构公共类
 * @date 2025/05/29
 **/
@Data
public class CommonResult<T> {
    private String message = "ok";

    private int code = 200;

    private T data;
}
