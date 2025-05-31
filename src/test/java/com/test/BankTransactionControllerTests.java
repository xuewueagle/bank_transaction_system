package com.test;

import cn.hutool.core.util.IdUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.test.dao.BankTransactionDao;
import com.test.domain.BankTransactionDTO;
import com.test.domain.BankTransactionListDTO;
import java.math.BigDecimal;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

/**
 * @author xuewueagle@163.com
 * @desc BankTransactionController测试类
 * @date 2025/05/30
 **/
@RunWith(SpringRunner.class)
@SpringBootTest
public class BankTransactionControllerTests {

    @Autowired
    private WebApplicationContext wac;

    @Autowired
    private BankTransactionDao bankTransactionDao;

    private MockMvc mockMvc;

    @Before
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
    }

    /**
     * case1: 添加交易信息接口测试-正确数据
     * @throws Exception
     */
    @Test
    public void testAddBankTransactionInfoWithValidData() throws Exception{
        // 构造数据
        BankTransactionDTO bankTransactionDTO = createBankTransactionDTO();
        String bodyData = JSONUtil.toJsonStr(bankTransactionDTO);
        // 模拟请求调用
        JSONObject jsonObject = callApiTest("post", "/api/v1/bankTransaction", bodyData);
        // 测试验证
        int code = jsonObject.getInt("code");
        Assert.assertEquals(HttpStatus.OK.value(),code);
        int addFlag = jsonObject.getInt("data");
        Assert.assertEquals(1,addFlag);
    }

    /**
     * case2: 添加交易信息接口测试-accountNumber参数校验异常数据
     * @throws Exception
     */
    @Test
    public void testAddBankTransactionInfoWithInvalidAccountNumberData() throws Exception {
        // 构造数据
        BankTransactionDTO bankTransactionDTO = createBankTransactionDTO();
        bankTransactionDTO.setAccountNumber("6214830265899263qw");
        String bodyData = JSONUtil.toJsonStr(bankTransactionDTO);
        // 模拟请求调用
        JSONObject jsonObject = callApiTest("post", "/api/v1/bankTransaction", bodyData);
        // 测试验证
        int code = jsonObject.getInt("code");
        Assert.assertEquals(HttpStatus.BAD_REQUEST.value(),code);
    }

    /**
     * case3: 添加交易信息接口测试-serialNumber参数校验异常数据
     * @throws Exception
     */
    @Test
    public void testAddBankTransactionInfoWithInvalidSerialNumberData() throws Exception {
        // 构造数据
        BankTransactionDTO bankTransactionDTO = createBankTransactionDTO();
        bankTransactionDTO.setSerialNumber("550e8400-e29b-41d4-a716-4466554400211111111");
        String bodyData = JSONUtil.toJsonStr(bankTransactionDTO);
        // 模拟请求调用
        JSONObject jsonObject = callApiTest("post", "/api/v1/bankTransaction", bodyData);
        // 测试验证
        int code = jsonObject.getInt("code");
        Assert.assertEquals(HttpStatus.BAD_REQUEST.value(),code);
    }

    /**
     * case4: 添加交易信息接口测试-amount参数校验异常数据
     * @throws Exception
     */
    @Test
    public void testAddBankTransactionInfoWithInvalidAmountData() throws Exception {
        // 构造数据
        BankTransactionDTO bankTransactionDTO = createBankTransactionDTO();
        bankTransactionDTO.setAmount(BigDecimal.valueOf(0.00));
        String bodyData = JSONUtil.toJsonStr(bankTransactionDTO);
        // 模拟请求调用
        JSONObject jsonObject = callApiTest("post", "/api/v1/bankTransaction", bodyData);
        // 测试验证
        int code = jsonObject.getInt("code");
        Assert.assertEquals(HttpStatus.BAD_REQUEST.value(),code);
    }

    /**
     * case5: 添加交易信息接口测试-type参数校验异常数据
     * @throws Exception
     */
    @Test
    public void testAddBankTransactionInfoWithInvalidTypeData() throws Exception {
        // 构造数据
        BankTransactionDTO bankTransactionDTO = createBankTransactionDTO();
        bankTransactionDTO.setType("");
        String bodyData = JSONUtil.toJsonStr(bankTransactionDTO);
        // 模拟请求调用
        JSONObject jsonObject = callApiTest("post", "/api/v1/bankTransaction", bodyData);
        // 测试验证
        int code = jsonObject.getInt("code");
        Assert.assertEquals(HttpStatus.BAD_REQUEST.value(),code);
    }

    /**
     * case6: 添加交易信息接口测试-status参数校验异常数据
     * @throws Exception
     */
    @Test
    public void testAddBankTransactionInfoWithInvalidStatusData() throws Exception {
        // 构造数据
        BankTransactionDTO bankTransactionDTO = createBankTransactionDTO();
        bankTransactionDTO.setStatus("");
        String bodyData = JSONUtil.toJsonStr(bankTransactionDTO);
        // 模拟请求调用
        JSONObject jsonObject = callApiTest("post", "/api/v1/bankTransaction", bodyData);
        // 测试验证
        int code = jsonObject.getInt("code");
        Assert.assertEquals(HttpStatus.BAD_REQUEST.value(),code);
    }

    /**
     * case7: 添加交易信息接口测试-参数缺省
     * @throws Exception
     */
    @Test
    public void testAddBankTransactionInfoWithDefaultParameters() throws Exception {
        // 构造数据
        BankTransactionDTO bankTransactionDTO = new BankTransactionDTO();
        String bodyData = JSONUtil.toJsonStr(bankTransactionDTO);
        // 模拟请求调用
        JSONObject jsonObject = callApiTest("post", "/api/v1/bankTransaction", bodyData);
        // 测试验证
        int code = jsonObject.getInt("code");
        Assert.assertEquals(HttpStatus.BAD_REQUEST.value(),code);
    }

    /**
     *  case8: 交易信息分页查询接口测试-存在数据
     * @throws Exception
     */
    @Test
    public void testSelectBankTransactionInfoWithExistingData() throws Exception {
        // 模拟请求调用
        JSONObject jsonObject = callApiTest("get", "/api/v1/bankTransaction?pageNum=1&pageSize=2", null);
        // 测试验证
        int code = jsonObject.getInt("code");
        Assert.assertEquals(HttpStatus.OK.value(),code);
    }

    /**
     *  case9: 交易信息分页查询接口测试-不存在数据
     * @throws Exception
     */
    @Test
    public void testSelectBankTransactionInfoWithNonExistingData() throws Exception {
        // 模拟请求调用
        JSONObject jsonObject = callApiTest("get", "/api/v1/bankTransaction?pageNum=0&pageSize=2", null);
        // 测试验证
        int code = jsonObject.getInt("code");
        JSONArray data = jsonObject.getJSONArray("data");
        Assert.assertEquals(HttpStatus.OK.value(),code);
        Assert.assertEquals(0,data.stream().count());
    }

    /**
     *  case10: 交易信息分页查询接口测试-参数缺省
     * @throws Exception
     */
    @Test
    public void testSelectBankTransactionInfoWithDefaultParameters() throws Exception {
        // 模拟请求调用
        JSONObject jsonObject = callApiTest("get", "/api/v1/bankTransaction?pageNum=0", null);
        // 测试验证
        int code = jsonObject.getInt("code");
        JSONArray data = jsonObject.getJSONArray("data");
        Assert.assertEquals(HttpStatus.BAD_REQUEST.value(),code);
    }

    /**
     * case11: 更新交易信息接口测试-正常数据
     * @throws Exception
     */
    @Test
    public void testUpdateBankTransactionInfoWithValidData() throws Exception {
        // 构造数据
        BankTransactionDTO bankTransactionDTO = createBankTransactionDTO();
        bankTransactionDTO.setId(1L);
        bankTransactionDTO.setSerialNumber(IdUtil.randomUUID());
        String bodyData = JSONUtil.toJsonStr(bankTransactionDTO);
        // 模拟请求调用
        JSONObject jsonObject = callApiTest("put", "/api/v1/bankTransaction", bodyData);
        // 测试验证
        int code = jsonObject.getInt("code");
        Assert.assertEquals(HttpStatus.OK.value(),code);
    }

    /**
     * case12: 更新交易信息接口测试-AccountNumber参数校验异常数据
     * @throws Exception
     */
    @Test
    public void testUpdateBankTransactionInfoWithInvalidAccountNumberData() throws Exception {
        // 构造数据
        BankTransactionDTO bankTransactionDTO = createBankTransactionDTO();
        bankTransactionDTO.setAccountNumber("6214830265899263qw");
        String bodyData = JSONUtil.toJsonStr(bankTransactionDTO);
        // 模拟请求调用
        JSONObject jsonObject = callApiTest("put", "/api/v1/bankTransaction", bodyData);
        // 测试验证
        int code = jsonObject.getInt("code");
        Assert.assertEquals(HttpStatus.BAD_REQUEST.value(),code);
    }

    /**
     * case13: 更新交易信息接口测试-SerialNumber参数校验异常数据
     * @throws Exception
     */
    @Test
    public void testUpdateBankTransactionInfoWithInvalidSerialNumberData() throws Exception {
        // 构造数据
        BankTransactionDTO bankTransactionDTO = createBankTransactionDTO();
        bankTransactionDTO.setSerialNumber("550e8400-e29b-41d4-a716-446655440021123");
        String bodyData = JSONUtil.toJsonStr(bankTransactionDTO);
        // 模拟请求调用
        JSONObject jsonObject = callApiTest("put", "/api/v1/bankTransaction", bodyData);
        // 测试验证
        int code = jsonObject.getInt("code");
        Assert.assertEquals(HttpStatus.BAD_REQUEST.value(),code);
    }

    /**
     * case14: 更新交易信息接口测试-Amount参数校验异常数据
     * @throws Exception
     */
    @Test
    public void testUpdateBankTransactionInfoWithInvalidAmountData() throws Exception {
        // 构造数据
        BankTransactionDTO bankTransactionDTO = createBankTransactionDTO();
        bankTransactionDTO.setAmount(BigDecimal.valueOf(0.00));
        String bodyData = JSONUtil.toJsonStr(bankTransactionDTO);
        // 模拟请求调用
        JSONObject jsonObject = callApiTest("put", "/api/v1/bankTransaction", bodyData);
        // 测试验证
        int code = jsonObject.getInt("code");
        Assert.assertEquals(HttpStatus.BAD_REQUEST.value(),code);
    }

    /**
     * case15: 更新交易信息接口测试-Type参数校验异常数据
     * @throws Exception
     */
    @Test
    public void testUpdateBankTransactionInfoWithInvalidTypeData() throws Exception {
        // 构造数据
        BankTransactionDTO bankTransactionDTO = createBankTransactionDTO();
        bankTransactionDTO.setType("");
        String bodyData = JSONUtil.toJsonStr(bankTransactionDTO);
        // 模拟请求调用
        JSONObject jsonObject = callApiTest("put", "/api/v1/bankTransaction", bodyData);
        // 测试验证
        int code = jsonObject.getInt("code");
        Assert.assertEquals(HttpStatus.BAD_REQUEST.value(),code);
    }

    /**
     * case16: 更新交易信息接口测试-Status参数校验异常数据
     * @throws Exception
     */
    @Test
    public void testUpdateBankTransactionInfoWithInvalidStatusData() throws Exception {
        // 构造数据
        BankTransactionDTO bankTransactionDTO = createBankTransactionDTO();
        bankTransactionDTO.setStatus("");
        String bodyData = JSONUtil.toJsonStr(bankTransactionDTO);
        // 模拟请求调用
        JSONObject jsonObject = callApiTest("put", "/api/v1/bankTransaction", bodyData);
        // 测试验证
        int code = jsonObject.getInt("code");
        Assert.assertEquals(HttpStatus.BAD_REQUEST.value(),code);
    }

    /**
     * case17: 修改交易信息接口测试-参数缺省
     * @throws Exception
     */
    @Test
    public void testUpdateBankTransactionInfoWithDefaultParameters() throws Exception {
        // 构造数据
        BankTransactionDTO bankTransactionDTO = new BankTransactionDTO();
        String bodyData = JSONUtil.toJsonStr(bankTransactionDTO);
        // 模拟请求调用
        JSONObject jsonObject = callApiTest("put", "/api/v1/bankTransaction", bodyData);
        // 测试验证
        int code = jsonObject.getInt("code");
        Assert.assertEquals(HttpStatus.BAD_REQUEST.value(),code);
    }

    /**
     * case18: 删除交易信息接口测试
     * @throws Exception
     */
    @Test
    public void testDeleteBankTransactionById() throws Exception {

        // 构造数据
        BankTransactionDTO bankTransactionDTO = createBankTransactionDTO();
        String bodyData = JSONUtil.toJsonStr(bankTransactionDTO);
        // 模拟请求调用新增一条数据
        JSONObject addJsonObject = callApiTest("post", "/api/v1/bankTransaction", bodyData);
        int addCode = addJsonObject.getInt("code");
        Assert.assertEquals(HttpStatus.OK.value(), addCode);
        BankTransactionListDTO bankTransactionListDTO = bankTransactionDao.selectBankTransactionBySerialNumber(
                bankTransactionDTO.getSerialNumber());
        // 删除新增的该条数据
        JSONObject deleteJsonObject = callApiTest("delete", "/api/v1/bankTransaction?id="+bankTransactionListDTO.getId(), null);
        int deleteCode = deleteJsonObject.getInt("code");
        Assert.assertEquals(HttpStatus.OK.value(), deleteCode);
    }

    /**
     * 构造交易模拟数据
     * @return BankTransactionDTO
     */
    private BankTransactionDTO createBankTransactionDTO(){
        BankTransactionDTO bankTransactionDTO = new BankTransactionDTO();
        bankTransactionDTO.setSerialNumber(IdUtil.randomUUID());
        bankTransactionDTO.setAccountNumber("6214830265890001");
        bankTransactionDTO.setAmount(BigDecimal.valueOf(5000.00));
        bankTransactionDTO.setType("DEPOSIT");
        bankTransactionDTO.setDescription("Initial deposit");
        bankTransactionDTO.setCategory("工资");
        bankTransactionDTO.setStatus("COMPLETED");

        return bankTransactionDTO;
    }


    /**
     * 模拟请求调用
     * @param type 请求方式
     * @param uri 请求路由
     * @param body body参数
     * @return JSONObject
     */
    private JSONObject callApiTest(String type, String uri, String body) throws Exception {

        RequestBuilder requestBuilder = null;
        switch (type){
            case "post":
                requestBuilder = MockMvcRequestBuilders.post(uri)
                        .accept(MediaType.APPLICATION_JSON).content(body)
                        .contentType(MediaType.APPLICATION_JSON);
                break;
            case "put":
                requestBuilder = MockMvcRequestBuilders.put(uri)
                        .accept(MediaType.APPLICATION_JSON).content(body)
                        .contentType(MediaType.APPLICATION_JSON);
                break;
            case "get":
                requestBuilder = MockMvcRequestBuilders.get(uri);
                break;
            case "delete":
                requestBuilder = MockMvcRequestBuilders.delete(uri);
                break;
        }
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        // 处理结果
        MockHttpServletResponse response = result.getResponse();
        String contentAsString = response.getContentAsString();
        JSONObject jsonObject = JSONUtil.parseObj(contentAsString);

        return jsonObject;
    }
}
