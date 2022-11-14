package com.anshi.controller;

import com.anshi.common.R;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.connector.CoyoteOutputStream;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.UUID;


@RestController
@RequestMapping("/common")
@Slf4j
public class UpDownController {

    //定义一个基本路径，作为上传的基本路径
    @Value("${reggie.uploadPath}")
    private String basePath;

    /**
     * 文件上传
     * @param file
     * @return
     */
    @PostMapping("/upload")
    public R<String> upload(MultipartFile file){
        //查看是否能正常接受到file
        log.info(file.toString());
        //file是一个临时文件，需要转存到一个目录下，否则临时文件会被删除

        //获取原始文件名，作为转存的文件名,但是可能会文件名称重复造成文件覆盖
        String originalFilename = file.getOriginalFilename();

        //使用UUID随机生成文件名防止文件名称重复
        String fileName = UUID.randomUUID().toString();  //fileName文件名
        //文件名后面有文件后缀，比如.jpg，这个也要动态获取
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));  //suffix文件后缀
        //合成最终文件名
        String endFileName = fileName + suffix;

        //判断目录是否存在，如果不存在则创建一个目录
        File dir = new File(basePath);
        if(!dir.exists()){
            //不存在，创建目录
            dir.mkdir();
        }

        try {
            //将临时文件转存到指定位置
            file.transferTo(new File(basePath + endFileName));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return R.success(endFileName);
    }

    /**
     * 文件下载
     * @param name
     * @param response
     */
    @GetMapping("/download")
    public void download(String name, HttpServletResponse response){

        try {
            //输入流，通过输入流读取文件内容
            FileInputStream fileInputStream = new FileInputStream(new File(basePath + name));
            //输出流，通过输出流将文件写回浏览器，在浏览器展示图片
            ServletOutputStream outputStream = response.getOutputStream();

            response.setContentType("image/jpeg");

            int len = 0;
            byte[] bytes = new byte[1024];
            while ((len = fileInputStream.read(bytes)) != -1){
                outputStream.write(bytes,0,len);
                outputStream.flush();
            }

            //关闭流
            outputStream.close();
            fileInputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
