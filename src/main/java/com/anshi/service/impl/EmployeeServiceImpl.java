package com.anshi.service.impl;


import com.anshi.dao.EmployeeDao;
import com.anshi.domain.Employee;
import com.anshi.service.EmployeeService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class EmployeeServiceImpl extends ServiceImpl<EmployeeDao,Employee> implements EmployeeService {
}
