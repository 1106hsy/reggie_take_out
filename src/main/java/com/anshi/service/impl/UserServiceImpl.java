package com.anshi.service.impl;


import com.anshi.dao.UserDao;
import com.anshi.domain.User;
import com.anshi.service.UserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends ServiceImpl<UserDao, User> implements UserService {
}
