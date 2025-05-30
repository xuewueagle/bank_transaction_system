package com.test.controller;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.test.domain.BankTransactionDTO;
import com.test.domain.BankTransactionListDTO;
import com.test.domain.BankTransactionListVO;
import com.test.domain.CommonResult;
import com.test.service.BankTransactionService;
import com.test.util.constant.CommonConstants;
import com.test.util.exception.TransactionNotFoundException;
import com.test.util.exception.TransactionRepeatSubmitException;
import jakarta.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
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

    private static final Logger logger = LoggerFactory.getLogger(BankTransactionController.class);

    // 读写缓存定义
    Cache<String, Object> writeCache = Caffeine.newBuilder()
            .maximumSize(500)
            .expireAfterWrite(5, TimeUnit.SECONDS)
            .build();
    Cache<String, Object> readCache = Caffeine.newBuilder()
            .maximumSize(2000)
            .expireAfterWrite(10, TimeUnit.MINUTES)
            .build();

    @Autowired
    private BankTransactionService bankTransactionService;

    /**
     * 新增交易信息
     * @param bankTransaction 交易数据
     * @return 新增成功标记
     */
    @PostMapping("/bankTransaction")
    public CommonResult<Integer> addBankTransactionInfo(@Valid @RequestBody BankTransactionDTO bankTransaction){
        // 检查是否已存在该交易 --- 这里使用本地缓存+查DB进行双重校验（交易记录流水号唯一）
        String bankTransactionAddCache = null;
        Object ifPresent = writeCache.getIfPresent(CommonConstants.ADD_OPERATE + bankTransaction.getSerialNumber());
        if(ObjectUtil.isNotNull(ifPresent)){
            bankTransactionAddCache = ifPresent.toString();
        }
        if(!StrUtil.hasEmpty(bankTransactionAddCache)){
            throw new TransactionRepeatSubmitException("该交易数据已存在，无法重复提交！");
        }else{ // 缓存失效时兜底检查
            BankTransactionListDTO bankTransactionListDTO = bankTransactionService.selectBankTransactionBySerialNumber(
                    bankTransaction.getSerialNumber());
            if(ObjectUtil.isNotNull(bankTransactionListDTO)){
                // 重写缓存
                writeCache.put(CommonConstants.ADD_OPERATE+bankTransaction.getSerialNumber(), CommonConstants.CACHE_EXIST_VALUE);
                throw new TransactionRepeatSubmitException("该交易数据已存在，无法重复提交！");
            }
        }

        // 新增交易数据
        CommonResult<Integer> commonResult = new CommonResult<>();
        int result = bankTransactionService.addBankTransaction(bankTransaction);
        // 加入本地缓存
        if(1 == result){
            // 添加新增交易数据缓存
            writeCache.put(CommonConstants.ADD_OPERATE+bankTransaction.getSerialNumber(), CommonConstants.CACHE_EXIST_VALUE);
            logger.info("新增交易数据成功加入本地写缓存～");
            // 删除读缓存， 保证查询数据的时效性
            deleteReadCache();
            logger.info("新增交易数据之后删除查询缓存～");
        }
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
    public CommonResult<List<BankTransactionListVO>> selectBankTransactionInfo(@RequestParam int pageNum, @RequestParam int pageSize){

        CommonResult<List<BankTransactionListVO>> commonResult = new CommonResult<>();

        // 这里使用本地缓存进行查询加速
        List<BankTransactionListVO> cacheBankTransactionLists = (List<BankTransactionListVO>) readCache.getIfPresent(CommonConstants.SELECT_OPERATE + pageNum + "_" + pageSize);
        if(ObjectUtil.isNotNull(cacheBankTransactionLists)){
            commonResult.setData(cacheBankTransactionLists);
            return commonResult;
        }

        // 缓存中不存在交易数据，再从数据库中查
        List<BankTransactionListVO> resultList = new ArrayList<>();
        List<BankTransactionListDTO> result = bankTransactionService.selectBankTransactionListByPage(pageNum,pageSize);
        BankTransactionListVO bankTransactionListVO;
        for(BankTransactionListDTO bankTransactionListDTO:result){
            bankTransactionListVO = new BankTransactionListVO();
            BeanUtils.copyProperties(bankTransactionListDTO,bankTransactionListVO);
            resultList.add(bankTransactionListVO);
        }
        readCache.put(CommonConstants.SELECT_OPERATE+pageNum+"_"+pageSize,resultList);
        commonResult.setData(resultList);
        return commonResult;
    }

    /**
     * 修改交易数据
     * @param bankTransaction 交易数据
     * @return 修改成功标记
     */
    @PutMapping("/bankTransaction")
    public CommonResult<Integer> updateBankTransactionInfo(@RequestBody BankTransactionDTO bankTransaction){
        CommonResult<Integer> commonResult = new CommonResult<>();
        int result = bankTransactionService.updateBankTransactionById(bankTransaction);
        commonResult.setData(result);
        if(1 == result){
            // 交易数据更新成功， 然后删除读缓存， 保证查询数据的时效性
            deleteReadCache();
        }

        return commonResult;
    }

    /**
     * 删除交易数据
     * @param id 交易数据id
     * @return 删除成功标记
     */
    @DeleteMapping("/bankTransaction")
    public CommonResult<Integer> deleteBankTransactionById(@RequestParam Long id){
        // 检查当前交易记录是否已删除 --- 这里使用本地缓存+查DB进行双重校验
        String bankTransactionDeleteCache = null;
        Object ifPresent = writeCache.getIfPresent(CommonConstants.DELETE_OPERATE + id);
        if(ObjectUtil.isNotNull(ifPresent)){
            bankTransactionDeleteCache = ifPresent.toString();
        }
        if(!StrUtil.hasEmpty(bankTransactionDeleteCache)){
            throw new TransactionNotFoundException("该交易数据已删除，无法再次删除！");
        }else{ // 缓存失效时兜底检查
            BankTransactionListDTO bankTransactionListDTO = bankTransactionService.selectBankTransactionById(id);
            if(!ObjectUtil.isNotNull(bankTransactionListDTO)){
                // 重写缓存
                writeCache.put(CommonConstants.DELETE_OPERATE+ id, CommonConstants.CACHE_EXIST_VALUE);
                throw new TransactionNotFoundException("该交易数据已删除，无法再次删除！");
            }
        }

        CommonResult<Integer> commonResult = new CommonResult<>();
        int result = bankTransactionService.deleteBankTransactionById(id);
        commonResult.setData(result);
        if(1 == result){
            // 删除读缓存， 保证查询数据的时效性
            deleteReadCache();
        }
        return commonResult;
    }

    /**
     * 删除读缓存数据
     */
    private void deleteReadCache(){
        Stream<String> matchingKeys = readCache.asMap().keySet().stream()
                .filter(k -> k.startsWith(CommonConstants.SELECT_OPERATE));
        Iterable<String> keys = matchingKeys.collect(Collectors.toList());
        if(ObjectUtil.isNotNull(keys)){
            readCache.invalidateAll(keys);
        }
    }
}
