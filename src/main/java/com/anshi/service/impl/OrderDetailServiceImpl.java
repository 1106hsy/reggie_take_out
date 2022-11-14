package com.anshi.service.impl;


import com.anshi.dao.OrderDetailDao;
import com.anshi.domain.OrderDetail;
import com.anshi.service.OrderDetailService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class OrderDetailServiceImpl extends ServiceImpl<OrderDetailDao, OrderDetail> implements OrderDetailService {
}
