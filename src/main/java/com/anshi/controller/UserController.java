package com.anshi.controller;

import com.anshi.common.R;
import com.anshi.domain.User;
import com.anshi.service.UserService;
import com.anshi.utils.JavaMailServiceUtils;
import com.anshi.utils.ValidateCodeUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private JavaMailServiceUtils javaMailServiceUtils;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 发送手机短信验证码
     * @param user
     * @return
     */
    @PostMapping("/sendMsg")
    private R<String> sendMsg(@RequestBody User user, HttpSession httpSession){
        //获取手机号
        String phone = user.getPhone();
        if(phone != null){
            //生成随机的四位验证码
            String code = ValidateCodeUtils.generateValidateCode(4).toString();

            System.out.println("code:" + code);

            //调用邮箱提供的API发送邮件
//            javaMailServiceUtils.sendMail("2777833922@qq.com","hhssyy1106@163.com",code);

            //需要将生成的验证码保存到session
//            httpSession.setAttribute(phone,code);

            //将获取到的验证码保存到redis缓存,保存时间5分钟
            redisTemplate.opsForValue().set(phone,code,5, TimeUnit.MINUTES);

            return R.success("手机短信验证码发送成功");
        }

        return R.error("短信验证码发送失败");
    }

    /**
     * 登录
     * @param map
     * @param httpSession
     * @return
     */
    @PostMapping("/login")
    public R<User> login(@RequestBody Map map,HttpSession httpSession){
        //获取手机号
        String phone = map.get("phone").toString();
        //获取验证码
        String code = map.get("code").toString();
        //获取session中保存的验证码
//        String codeInSession = (String) httpSession.getAttribute(phone);

        //获取redis缓存中的验证码
        String codeInSession = (String) redisTemplate.opsForValue().get(phone);

        //进行验证码的比对(页面提交的和session保存的)
        if(codeInSession != null && codeInSession.equals(code)){
            //比对成功，登录

            //判断当前手机号对应的用户是否为新用户，如果为新用户保存到数据库
            LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(phone != null,User::getPhone,phone);

            User user = userService.getOne(queryWrapper);
            if(user == null){
                user = new User();
                user.setPhone(phone);
                userService.save(user);
            }
            httpSession.setAttribute("user",user.getId());
//            System.out.println("类型：" + user.getId().getClass());

            //如果用户登录成功，删除redis中缓存的验证码数据
            redisTemplate.delete(phone);

            return R.success(user);
        }
        return R.error("登录失败");
    }
}
