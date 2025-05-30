package com.test.util.exception;

/**
 * @author xuewueagle@163.com
 * @desc 交易信息不存在异常
 * @date 2025/05/29
 **/
public class TransactionNotFoundException extends RuntimeException {
    private static final long serialVersionUID = -2261010614139072895L;

    public  TransactionNotFoundException(){
        super();
    }

    public  TransactionNotFoundException(String message){
        super(message);
    }

    public TransactionNotFoundException(String message,Throwable cause){
        super(message,cause);
    }

    public TransactionNotFoundException(Throwable cause){
        super(cause);
    }
}
