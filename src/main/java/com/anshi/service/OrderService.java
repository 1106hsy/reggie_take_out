package com.anshi.service;

import com.anshi.domain.Orders;
import com.baomidou.mybatisplus.extension.service.IService;

import javax.servlet.http.HttpSession;


public interface OrderService extends IService<Orders> {

//    用户下单
    void submit(Orders order, HttpSession httpSession);
}
