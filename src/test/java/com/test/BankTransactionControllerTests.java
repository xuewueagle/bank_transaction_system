package com.test;

import cn.hutool.core.util.IdUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.test.domain.BankTransactionDTO;
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

    private MockMvc mockMvc;

    @Before
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
    }

    /**
     * 添加交易信息接口测试-正确数据
     * @throws Exception
     */
    @Test
    public void testAddBankTransactionInfoWithValidData() throws Exception{
        BankTransactionDTO bankTransactionDTO = new BankTransactionDTO();
        bankTransactionDTO.setSerialNumber(IdUtil.randomUUID());
        bankTransactionDTO.setAccountNumber("6214830265899263");
        bankTransactionDTO.setAmount(BigDecimal.valueOf(5000.00));
        bankTransactionDTO.setType("DEPOSIT");
        bankTransactionDTO.setDescription("Initial deposit");
        bankTransactionDTO.setCategory("工资");
        bankTransactionDTO.setStatus("COMPLETED");

        // 构建JSON请求体
        String JSON = JSONUtil.toJsonStr(bankTransactionDTO);

        RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/api/v1/bankTransaction")
                .accept(MediaType.APPLICATION_JSON).content(JSON)
                .contentType(MediaType.APPLICATION_JSON);
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        MockHttpServletResponse response = result.getResponse();
        String contentAsString = response.getContentAsString();
        JSONObject jsonObject = JSONUtil.parseObj(contentAsString);
        int code = jsonObject.getInt("code");
        Assert.assertEquals(HttpStatus.OK.value(),code);
    }

    /**
     * 添加交易信息接口测试-异常数据
     * @throws Exception
     */
    @Test
    public void testAddBankTransactionInfoWithInvalidData() throws Exception {
        BankTransactionDTO bankTransactionDTO = new BankTransactionDTO();
        bankTransactionDTO.setSerialNumber(IdUtil.randomUUID());
        bankTransactionDTO.setAccountNumber("6214830265899263qw");
        bankTransactionDTO.setAmount(BigDecimal.valueOf(6000.00));
        bankTransactionDTO.setType("DEPOSIT");
        bankTransactionDTO.setDescription("Initial deposit");
        bankTransactionDTO.setCategory("工资");
        bankTransactionDTO.setStatus("COMPLETED");

        // 构建JSON请求体
        String JSON = JSONUtil.toJsonStr(bankTransactionDTO);

        RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/api/v1/bankTransaction")
                .accept(MediaType.APPLICATION_JSON).content(JSON)
                .contentType(MediaType.APPLICATION_JSON);
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        MockHttpServletResponse response = result.getResponse();
        String contentAsString = response.getContentAsString();
        JSONObject jsonObject = JSONUtil.parseObj(contentAsString);
        int code = jsonObject.getInt("code");
        Assert.assertNotEquals(code,200);
    }

    /**
     * 交易信息分页查询接口测试-存在数据
     * @throws Exception
     */
    @Test
    public void testSelectBankTransactionInfoWithExistingData() throws Exception {
        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/api/v1/bankTransaction?pageNum=1&pageSize=2");
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        String contentAsString = result.getResponse().getContentAsString();
        JSONObject jsonObject = JSONUtil.parseObj(contentAsString);
        int code = jsonObject.getInt("code");
        JSONArray data = jsonObject.getJSONArray("data");
        Assert.assertEquals(HttpStatus.OK.value(),code);
        Assert.assertNotEquals(data.stream().count(),0);
    }

    /**
     * 交易信息分页查询接口测试-不存在数据
     * @throws Exception
     */
    @Test
    public void testSelectBankTransactionInfoWithNonExistingData() throws Exception {
        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/api/v1/bankTransaction?pageNum=0&pageSize=2");
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        String contentAsString = result.getResponse().getContentAsString();
        JSONObject jsonObject = JSONUtil.parseObj(contentAsString);
        int code = jsonObject.getInt("code");
        JSONArray data = jsonObject.getJSONArray("data");
        Assert.assertEquals(HttpStatus.OK.value(),code);
        Assert.assertEquals(data.stream().count(),0);
    }

    /**
     * 更新交易信息接口测试-正常数据
     * @throws Exception
     */
    @Test
    public void testUpdateBankTransactionInfoWithValidData() throws Exception {
        BankTransactionDTO bankTransactionDTO = new BankTransactionDTO();
        bankTransactionDTO.setId(1L);
        bankTransactionDTO.setSerialNumber(IdUtil.randomUUID());
        bankTransactionDTO.setAccountNumber("6214830265890001");
        bankTransactionDTO.setAmount(BigDecimal.valueOf(5000.00));
        bankTransactionDTO.setType("DEPOSIT");
        bankTransactionDTO.setDescription("Initial deposit");
        bankTransactionDTO.setCategory("工资");
        bankTransactionDTO.setStatus("COMPLETED");

        // 构建JSON请求体
        String JSON = JSONUtil.toJsonStr(bankTransactionDTO);

        RequestBuilder requestBuilder = MockMvcRequestBuilders.put("/api/v1/bankTransaction")
                .accept(MediaType.APPLICATION_JSON).content(JSON)
                .contentType(MediaType.APPLICATION_JSON);
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        MockHttpServletResponse response = result.getResponse();
        String contentAsString = response.getContentAsString();
        JSONObject jsonObject = JSONUtil.parseObj(contentAsString);
        int code = jsonObject.getInt("code");
        Assert.assertEquals(HttpStatus.OK.value(),code);
    }

    /**
     * 更新交易信息接口测试-异常数据
     * @throws Exception
     */
    @Test
    public void testUpdateBankTransactionInfoWithInvalidData() throws Exception {
        BankTransactionDTO bankTransactionDTO = new BankTransactionDTO();

        bankTransactionDTO.setId(1L);
        bankTransactionDTO.setSerialNumber(IdUtil.randomUUID());
        bankTransactionDTO.setAccountNumber("6214830265890001a");
        bankTransactionDTO.setAmount(BigDecimal.valueOf(5000.00));
        bankTransactionDTO.setType("DEPOSIT1");
        bankTransactionDTO.setDescription("Initial deposit");
        bankTransactionDTO.setCategory("工资");
        bankTransactionDTO.setStatus("COMPLETED");

        // 构建JSON请求体
        String JSON = JSONUtil.toJsonStr(bankTransactionDTO);

        RequestBuilder requestBuilder = MockMvcRequestBuilders.put("/api/v1/bankTransaction")
                .accept(MediaType.APPLICATION_JSON).content(JSON)
                .contentType(MediaType.APPLICATION_JSON);
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        MockHttpServletResponse response = result.getResponse();
        String contentAsString = response.getContentAsString();
        JSONObject jsonObject = JSONUtil.parseObj(contentAsString);
        int code = jsonObject.getInt("code");
        Assert.assertEquals(HttpStatus.BAD_REQUEST.value(),code);
    }

    /**
     * 删除交易信息接口测试
     * @throws Exception
     */
    @Test
    public void testDeleteBankTransactionById() throws Exception {
        RequestBuilder requestBuilder = MockMvcRequestBuilders.delete("/api/v1/bankTransaction?id=12");
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        String contentAsString = result.getResponse().getContentAsString();
        JSONObject jsonObject = JSONUtil.parseObj(contentAsString);
        int code = jsonObject.getInt("code");
        if(code == 404){
            Assert.assertEquals(HttpStatus.NOT_FOUND.value(),code);
        }else{
            Assert.assertEquals(HttpStatus.OK.value(),code);
        }

    }
}
