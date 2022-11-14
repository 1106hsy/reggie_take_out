package com.anshi.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Component
public class JavaMailServiceUtils {


//    @Autowired
//    public static JavaMailSender javaMailSender;

//    //发送人
//    private String send = "2777833922@qq.com";
//
//    //接收人
//    private String receive = "hhssyy1106@163.com";

//    public static String title = "【hsy 验证码】";
//    public static String text = "您的验证码为：";

    @Autowired
    public JavaMailSender javaMailSender;

    public void sendMail(String send,String receive,String code){

        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message);

            helper.setFrom(send + "(雨哥)");
            helper.setTo(receive);
            helper.setSubject("验证码");
            helper.setText("您的验证码为：" + code + "请您勿告知他人");

            javaMailSender.send(message);
            System.out.println("发送成功！");
        } catch (Exception e) {
            e.printStackTrace();
        }



    }
}
