package com.test.util.exception;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.test.domain.CommonResult;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.ConversionNotSupportedException;
import org.springframework.beans.TypeMismatchException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.async.AsyncRequestTimeoutException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.NoHandlerFoundException;

/**
 * @author xuewueagle@163.com
 * @desc 全局统一的异常处理
 * @date 2025/05/29
 **/
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @Autowired
    private ObjectMapper objectMapper;


    @ExceptionHandler(TransactionRepeatSubmitException.class)
    public CommonResult<String> onException(TransactionRepeatSubmitException ex){
        // 打印日志
        log.error(ex.getMessage());

        // 统一结果返回
        CommonResult<String> commonResult = new CommonResult<>();
        commonResult.setCode(409);
        commonResult.setMessage(ex.getMessage());

        return commonResult;
    }

    @ExceptionHandler(TransactionNotFoundException.class)
    public CommonResult<String> onException(TransactionNotFoundException ex){
        // 打印日志
        log.error(ex.getMessage());

        // 统一结果返回
        CommonResult<String> commonResult = new CommonResult<>();
        commonResult.setCode(404);
        commonResult.setMessage(ex.getMessage());

        return commonResult;
    }

    @ExceptionHandler({HttpRequestMethodNotSupportedException.class,
            HttpMediaTypeNotSupportedException.class, HttpMediaTypeNotAcceptableException.class,
            MissingPathVariableException.class, MissingServletRequestParameterException.class,
            ServletRequestBindingException.class, ConversionNotSupportedException.class,
            TypeMismatchException.class, HttpMessageNotReadableException.class,
            HttpMessageNotWritableException.class, SQLException.class,
            MissingServletRequestPartException.class,
            NoHandlerFoundException.class, AsyncRequestTimeoutException.class})
    public CommonResult<String> onException(Exception ex){
        // 打印日志
        log.error(ex.getMessage());

        // 统一结果返回
        CommonResult<String> commonResult = new CommonResult<>();
        commonResult.setCode(400);
        commonResult.setMessage(ex.getMessage());

        return commonResult;
    }

    /**
     * 参数校验异常
     */
    @ExceptionHandler(value= {MethodArgumentNotValidException.class , BindException.class})
    public CommonResult<String> onMethodArgumentException(Exception e) throws JsonProcessingException {
        BindingResult bindingResult = null;
        if (e instanceof MethodArgumentNotValidException) {
            bindingResult = ((MethodArgumentNotValidException)e).getBindingResult();
        } else if (e instanceof BindException) {
            bindingResult = ((BindException)e).getBindingResult();
        }
        Map<String,String> errorMap = new HashMap<>(16);
        bindingResult.getFieldErrors().forEach((fieldError)->
                errorMap.put(fieldError.getField(),fieldError.getDefaultMessage())
        );
        String msg = objectMapper.writeValueAsString(errorMap);

        // 打印日志
        log.error(msg);

        // 统一结果返回
        CommonResult<String> commonResult = new CommonResult<>();
        commonResult.setCode(400);
        commonResult.setMessage(msg);
        return commonResult;
    }
}
