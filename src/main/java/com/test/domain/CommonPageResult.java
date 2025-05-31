package com.test.domain;

import lombok.Data;

/**
 * @author xuewueagle@163.com
 * @desc 分页查询接口返回结构公共类
 * @date 2025/05/31
 **/
@Data
public class CommonPageResult<T> {
    private String message = "ok";
    private int code = 200;
    private T data;
    private int totalNum = 0;
}
