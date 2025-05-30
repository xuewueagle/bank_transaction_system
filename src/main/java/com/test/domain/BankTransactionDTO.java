package com.test.domain;

import jakarta.validation.constraints.DecimalMin;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

/**
 * @author xuewueagle@163.com
 * @desc 交易DTO类
 * @date 2025/05/29
 **/
@Data
public class BankTransactionDTO {
    private Long id;


    @Length(min = 36, max = 36, message = "交易流水号只能为36位字符！")
    private String serialNumber; // 交易流水号

    @NotBlank(message = "卡号不能为空！")
    @Length(min = 16, max = 16, message = "卡号只能为16位！")
    private String accountNumber; // 卡号

    @DecimalMin(message = "交易金额必须大于0", value = "0.01")
    private BigDecimal amount; // 交易金额(必须大于0)

    @NotBlank(message = "交易类型不能为空！")
    private String type; // 交易类型:存款/取款/转账

    private String description; // 交易描述信息
    private LocalDateTime timestamp; // 交易时间戳(精确到微秒)
    private String category; // 交易分类(如:工资、购物等)

    @NotBlank(message = "交易状态不能为空！")
    private String status; // 交易状态:处理中/已完成/已失败

    private LocalDateTime createdAt; // 记录创建时间
    private LocalDateTime updatedAt; // 记录最后更新时间
}
