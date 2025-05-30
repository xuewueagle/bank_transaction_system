package com.test.service.impl;

import com.github.pagehelper.PageHelper;
import com.test.dao.BankTransactionDao;
import com.test.domain.BankTransactionDTO;
import com.test.domain.BankTransactionListDTO;
import com.test.service.BankTransactionService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author xuewueagle@163.com
 * @desc 交易业务处理实现类
 * @date 2025/05/29
 **/
@Service
public class BankTransactionServiceImpl implements BankTransactionService {

    @Autowired
    private BankTransactionDao bankTransactionDao;

    @Override
    public int addBankTransaction(BankTransactionDTO bankTransaction) {

        return bankTransactionDao.addBankTransaction(bankTransaction);
    }

    @Override
    public int updateBankTransactionById(BankTransactionDTO bankTransaction) {

        return bankTransactionDao.updateBankTransactionById(bankTransaction);
    }

    @Override
    public int deleteBankTransactionById(Long id) {
        return bankTransactionDao.deleteBankTransactionById(id);
    }

    @Override
    public List<BankTransactionListDTO> selectBankTransactionListByPage(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum,pageSize);
        return bankTransactionDao.selectBankTransactionListByPage();
    }

    @Override
    public BankTransactionListDTO selectBankTransactionBySerialNumber(String serialNumber) {

        return bankTransactionDao.selectBankTransactionBySerialNumber(serialNumber);
    }

    @Override
    public BankTransactionListDTO selectBankTransactionById(Long id) {
        return bankTransactionDao.selectBankTransactionById(id);
    }
}
