package com.test.util.constant;

/**
 * @author xuewueagle@163.com
 * @desc 公共常量定义
 * @date 2025/05/30
 **/
public class CommonConstants {
    public static final String CACHE_EXIST_VALUE = "exist";
    public static final String ADD_OPERATE = "add_transaction_";
    public static final String SELECT_OPERATE = "select_transaction_";
    public static final String DELETE_OPERATE = "delete_transaction_";
    public static final String DELETE_DATA_ERROR = "该交易数据已删除，无法再次删除！";
    public static final String REPEAT_SUBMIT_DATA_ERROR = "该交易数据已存在，无法重复提交！";

    public static final String DELETE_QUERY_CACHE_AFTER_DELETE = "删除交易数据之后删除查询缓存～";
    public static final String DELETE_QUERY_CACHE_AFTER_UPDATE = "修改交易数据之后删除查询缓存～";
    public static final String DELETE_QUERY_CACHE_AFTER_ADD = "添加交易数据之后删除查询缓存～";
    public static final String ADD_QUERY_CACHE_AFTER_ADD = "新增交易数据成功加入本地写缓存～";
    public static final String GET_QUERY_CACHE_DATA = "从缓存中获取了数据～";

    private CommonConstants(){}
}
