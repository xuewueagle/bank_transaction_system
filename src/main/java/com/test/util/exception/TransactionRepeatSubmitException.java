package com.test.util.exception;

/**
 * @author xuewueagle@163.com
 * @desc 重复提交异常类
 * @date 2025/05/29
 **/
public class TransactionRepeatSubmitException extends RuntimeException {
    private static final long serialVersionUID = -2261010614139072894L;

    public  TransactionRepeatSubmitException(){
        super();
    }

    public  TransactionRepeatSubmitException(String message){
        super(message);
    }

    public TransactionRepeatSubmitException(String message,Throwable cause){
        super(message,cause);
    }

    public TransactionRepeatSubmitException(Throwable cause){
        super(cause);
    }
}
