package com.test.service;

import com.test.domain.BankTransactionDTO;
import com.test.domain.BankTransactionListDTO;
import com.test.domain.BankTransactionListVO;
import java.util.List;

/**
 * @author xuewueagle@163.com
 * @desc 交易业务处理接口
 * @date 2025/05/29
 **/
public interface BankTransactionService {
    // 添加交易信息
    int addBankTransaction(BankTransactionDTO bankTransaction);

    // 修改交易信息
    int updateBankTransactionById(BankTransactionDTO bankTransaction);

    // 删除交易信息
    int deleteBankTransactionById(Long id);

    // 分页查询交易信息
    List<BankTransactionListDTO> selectBankTransactionListByPage(int pageNum, int pageSize);

    // 查询交易信息总记录数
    int selectBankTransactionCount();

    // DTO转VO
    List<BankTransactionListVO> bankTransactionDto2Vo(List<BankTransactionListDTO> bankTransactionListData);

}
