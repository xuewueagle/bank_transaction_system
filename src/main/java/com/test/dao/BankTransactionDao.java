package com.test.dao;

import com.test.domain.BankTransactionDTO;
import com.test.domain.BankTransactionListDTO;
import java.util.List;
import org.springframework.stereotype.Repository;

/**
 * @author xuewueagle@163.com
 * @desc 交易信息Dao层
 * @date 2025/05/29
 **/
@Repository
public interface BankTransactionDao {

    // 新增交易信息
    int addBankTransaction(BankTransactionDTO bankTransaction);

    // 更新交易信息
    int updateBankTransactionById(BankTransactionDTO bankTransaction);

    // 删除交易信息
    int deleteBankTransactionById(Long id);

    // 查询交易信息
    List<BankTransactionListDTO> selectBankTransactionListByPage();

    // 根据唯一流水号查交易数据
    BankTransactionListDTO selectBankTransactionBySerialNumber(String serialNumber);

    // 根据主键id查交易数据
    BankTransactionListDTO selectBankTransactionById(Long id);

    // 查询交易信息总记录数
    int selectBankTransactionCount();
}
