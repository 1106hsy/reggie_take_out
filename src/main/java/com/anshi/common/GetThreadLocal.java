package com.anshi.common;

import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Component
public class GetThreadLocal {

    public void threadLocal(){
        ThreadLocal<Long> threadLocal = new ThreadLocal<>();
    }
}
