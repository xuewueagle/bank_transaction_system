package com.test.controller;

import com.test.domain.BankTransactionDTO;
import com.test.domain.BankTransactionListDTO;
import com.test.domain.BankTransactionListVO;
import com.test.domain.CommonPageResult;
import com.test.domain.CommonResult;
import com.test.service.BankTransactionService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author xuewueagle@163.com
 * @desc 交易业务控制类
 * @date 2025/05/29
 **/
@RestController
@RequestMapping("/api/v1")
public class BankTransactionController {
    @Autowired
    private BankTransactionService bankTransactionService;

    /**
     * 新增交易信息
     * @param bankTransaction 交易数据
     * @return 新增成功标记
     */
    @PostMapping("/bankTransaction")
    public CommonResult<Integer> addBankTransactionInfo(@Valid @RequestBody BankTransactionDTO bankTransaction){
        CommonResult<Integer> commonResult = new CommonResult<>();
        int result = bankTransactionService.addBankTransaction(bankTransaction);
        commonResult.setData(result);

        return commonResult;
    }

    /**
     * 交易数据分页查询
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @return 交易数据列表
     */
    @GetMapping("/bankTransaction")
    public CommonPageResult<List<BankTransactionListVO>> selectBankTransactionInfo(@RequestParam int pageNum, @RequestParam int pageSize){
        CommonPageResult<List<BankTransactionListVO>> commonPageResult = new CommonPageResult<>();
        // 查询交易列表数据
        List<BankTransactionListDTO> bankTransactionList = bankTransactionService.selectBankTransactionListByPage(pageNum,pageSize);
        // 转换VO对象，并返回
        commonPageResult.setData(bankTransactionService.bankTransactionDto2Vo(bankTransactionList));
        commonPageResult.setTotalNum(bankTransactionService.selectBankTransactionCount());

        return commonPageResult;
    }

    /**
     * 修改交易数据
     * @param bankTransaction 交易数据
     * @return 修改成功标记
     */
    @PutMapping("/bankTransaction")
    public CommonResult<Integer> updateBankTransactionInfo(@RequestBody BankTransactionDTO bankTransaction){
        CommonResult<Integer> commonResult = new CommonResult<>();
        commonResult.setData(bankTransactionService.updateBankTransactionById(bankTransaction));

        return commonResult;
    }

    /**
     * 删除交易数据
     * @param id 交易数据id
     * @return 删除成功标记
     */
    @DeleteMapping("/bankTransaction")
    public CommonResult<Integer> deleteBankTransactionById(@RequestParam Long id){
        CommonResult<Integer> commonResult = new CommonResult<>();
        commonResult.setData(bankTransactionService.deleteBankTransactionById(id));

        return commonResult;
    }
}
