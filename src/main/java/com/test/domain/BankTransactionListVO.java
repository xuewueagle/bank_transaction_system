package com.test.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * @author xuewueagle@163.com
 * @desc 交易信息列表VO类
 * @date 2025/05/29
 **/
@Data
public class BankTransactionListVO {
    private long id;
    private String serialNumber; // 交易流水号
    private String accountNumber; // 卡号
    private BigDecimal amount; // 交易金额(必须大于0)
    private String type; // 交易类型:存款/取款/转账
    private String description; // 交易描述信息
    private LocalDateTime timestamp; // 交易时间戳(精确到微秒)
    private String category; // 交易分类(如:工资、购物等)
    private String status; // 交易状态:处理中/已完成/已失败
    private LocalDateTime createdAt; // 记录创建时间
    private LocalDateTime updatedAt; // 记录最后更新时间
}
