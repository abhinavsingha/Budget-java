package com.sdd.exception;

import lombok.Getter;

@Getter
public class SDDException extends RuntimeException{
    private String field;
    private Integer status;

    public SDDException(String field,Integer status,String message){
        super(message);
        this.field =field;
        this.status = status;
    }

    public SDDException(Integer status,String message){
        super(message);
        this.status = status;
    }
}
