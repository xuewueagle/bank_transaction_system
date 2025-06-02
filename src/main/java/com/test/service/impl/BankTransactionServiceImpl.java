package com.test.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.pagehelper.PageHelper;
import com.test.dao.BankTransactionDao;
import com.test.domain.BankTransactionDTO;
import com.test.domain.BankTransactionListDTO;
import com.test.domain.BankTransactionListVO;
import com.test.service.BankTransactionService;
import com.test.util.constant.CommonConstants;
import com.test.util.exception.TransactionNotFoundException;
import com.test.util.exception.TransactionRepeatSubmitException;
import jakarta.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * @author xuewueagle@163.com
 * @desc 交易业务处理实现类
 * @date 2025/05/29
 **/
@Service
public class BankTransactionServiceImpl implements BankTransactionService {

    private static final Logger logger = LoggerFactory.getLogger(BankTransactionServiceImpl.class);

    // 读取本地缓存配置信息
    @Value("${local.cache.writeCacheMaximumSize}")
    private long nonStaticWriteCacheMaximumSize;
    @Value("${local.cache.writeCacheExpireTime}")
    private long  nonStaticWriteCacheExpireTime;
    @Value("${local.cache.readCacheMaximumSize}")
    private long nonStaticReadCacheMaximumSize;
    @Value("${local.cache.readCacheExpireTime}")
    private long  nonStaticReadCacheExpireTime;

    // 定义写缓存变量
    private static Cache<String, Object> writeCache;
    // 定义读缓存变量
    private static Cache<String, Object> readCache;

    @PostConstruct
    private void init(){
        // 配置读写缓存参数
        writeCache = Caffeine.newBuilder()
                .maximumSize(nonStaticWriteCacheMaximumSize).expireAfterWrite(nonStaticWriteCacheExpireTime, TimeUnit.SECONDS).build();
        readCache = Caffeine.newBuilder()
                .maximumSize(nonStaticReadCacheMaximumSize).expireAfterWrite(nonStaticReadCacheExpireTime, TimeUnit.MINUTES).build();
    }

    @Autowired
    private BankTransactionDao bankTransactionDao;

    @Override
    public int addBankTransaction(BankTransactionDTO bankTransaction) {
        // 检查是否已存在该交易 --- 这里使用本地缓存+查DB进行双重校验（交易记录流水号唯一）
        // 检查本地缓存中是否存在该交易数据
        boolean existingTransactionCache = existWriteCache(CommonConstants.ADD_OPERATE + bankTransaction.getSerialNumber());
        if(existingTransactionCache){
            throw new TransactionRepeatSubmitException(CommonConstants.REPEAT_SUBMIT_DATA_ERROR);
        }else{
            // 检查数据库中是否存在该交易数据
            boolean existingTransactionData = existBankTransactionBySerialNumber(bankTransaction.getSerialNumber());
            if(existingTransactionData){
                // 刷新写缓存
                writeCache.put(CommonConstants.ADD_OPERATE+bankTransaction.getSerialNumber(), CommonConstants.CACHE_EXIST_VALUE);
                throw new TransactionRepeatSubmitException(CommonConstants.REPEAT_SUBMIT_DATA_ERROR);
            }
        }

        // 该交易数据未曾添加过，新增交易数据至数据库
        int addResult = bankTransactionDao.addBankTransaction(bankTransaction);
        if(1 == addResult){
            // 添加新增交易数据缓存
            logger.info(CommonConstants.ADD_QUERY_CACHE_AFTER_ADD);
            writeCache.put(CommonConstants.ADD_OPERATE+bankTransaction.getSerialNumber(), CommonConstants.CACHE_EXIST_VALUE);
            // 添加交易数据成功之后删除查询缓存， 保证查询数据的时效性
            logger.info(CommonConstants.DELETE_QUERY_CACHE_AFTER_ADD);
            deleteLocalQueryCache();
        }

        return addResult;
    }

    @Override
    public int updateBankTransactionById(BankTransactionDTO bankTransaction) {
        int updateResult = bankTransactionDao.updateBankTransactionById(bankTransaction);
        if(1 == updateResult){
            // 修改交易数据成功之后删除查询缓存， 保证查询数据的时效性
            logger.info(CommonConstants.DELETE_QUERY_CACHE_AFTER_UPDATE);
            deleteLocalQueryCache();
        }

        return updateResult;
    }

    @Override
    public int deleteBankTransactionById(Long id) {

        // 检查当前交易记录是否已删除，不存在的数据无法再进行删除 --- 这里使用本地缓存+查DB进行双重校验
        if(existWriteCache(CommonConstants.DELETE_OPERATE + id)){
            throw new TransactionNotFoundException(CommonConstants.DELETE_DATA_ERROR);
        }
        BankTransactionListDTO bankTransactionListDTO = bankTransactionDao.selectBankTransactionById(id);
        if(ObjectUtil.isNull(bankTransactionListDTO)){
            // 重写缓存
            writeCache.put(CommonConstants.DELETE_OPERATE+ id, CommonConstants.CACHE_EXIST_VALUE);
            throw new TransactionNotFoundException(CommonConstants.DELETE_DATA_ERROR);
        }

        // 删除交易数据
        int deleteResult = bankTransactionDao.deleteBankTransactionById(id);
        if(1 == deleteResult){
            // 删除交易数据成功之后删除查询缓存， 保证查询数据的时效性
            logger.info(CommonConstants.DELETE_QUERY_CACHE_AFTER_DELETE);
            deleteLocalQueryCache();
        }

        return deleteResult;
    }

    @Override
    public List<BankTransactionListDTO> selectBankTransactionListByPage(int pageNum, int pageSize) {
        // 这里使用本地缓存进行查询加速
        List<BankTransactionListDTO> cacheBankTransactionLists = queryLocalQueryCache(
                CommonConstants.SELECT_OPERATE + pageNum + "_" + pageSize);
        if(ObjectUtil.isNotNull(cacheBankTransactionLists)){
            logger.info(CommonConstants.GET_QUERY_CACHE_DATA);
            return cacheBankTransactionLists;
        }

        // 缓存中不存在交易数据，则从数据库中查
        PageHelper.startPage(pageNum,pageSize);
        cacheBankTransactionLists = bankTransactionDao.selectBankTransactionListByPage();

        // 加入查询缓存
        readCache.put(CommonConstants.SELECT_OPERATE+pageNum+"_"+pageSize,cacheBankTransactionLists);

        return cacheBankTransactionLists;
    }

    @Override
    public int selectBankTransactionCount() {

        return bankTransactionDao.selectBankTransactionCount();
    }

    /**
     * 检查写缓存是否存在
     * @param key 缓存key
     * @return boolean
     */
    private boolean existWriteCache(String key){

        return writeCache.getIfPresent(key) != null;
    }

    /**
     * 根据serialNumber检查交易数据是否存在
     * @param serialNumber 缓存key
     * @return boolean
     */
    private boolean existBankTransactionBySerialNumber(String serialNumber){

        return bankTransactionDao.selectBankTransactionBySerialNumber(serialNumber) != null;
    }

    /**
     * 获取本地查询缓存
     * @param key 缓存key
     * @return List<BankTransactionListVO>
     */
    private List<BankTransactionListDTO> queryLocalQueryCache(String key){
        List<BankTransactionListDTO> cacheBankTransactionListsData = null;
        Object localQueryCache = readCache.getIfPresent(key);
        if(ObjectUtil.isNotNull(localQueryCache)){
            cacheBankTransactionListsData = (List<BankTransactionListDTO>) localQueryCache;
        }

        return cacheBankTransactionListsData;
    }

    /**
     * 删除本地查询缓存
     */
    private void deleteLocalQueryCache(){
        Stream<String> matchingKeys = readCache.asMap().keySet().stream()
                .filter(k -> k.startsWith(CommonConstants.SELECT_OPERATE));
        Iterable<String> keys = matchingKeys.collect(Collectors.toList());
        if(ObjectUtil.isNotNull(keys)){
            readCache.invalidateAll(keys);
        }
    }

    /**
     * BankTransaction DTO转VO
     * @param bankTransactionListData
     * @return
     */
    public  List<BankTransactionListVO> bankTransactionDto2Vo(List<BankTransactionListDTO> bankTransactionListData){
        if(ObjectUtil.isNull(bankTransactionListData)){
            return Collections.emptyList();
        }
        List<BankTransactionListVO> bankTransactionList = new ArrayList<>();
        BankTransactionListVO bankTransactionListVO;
        for(BankTransactionListDTO bankTransactionListDTO:bankTransactionListData){
            bankTransactionListVO = new BankTransactionListVO();
            BeanUtils.copyProperties(bankTransactionListDTO,bankTransactionListVO);
            bankTransactionList.add(bankTransactionListVO);
        }

        return bankTransactionList;
    }
}
