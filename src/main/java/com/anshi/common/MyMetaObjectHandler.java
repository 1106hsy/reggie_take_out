package com.anshi.common;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;

@Component
@Slf4j
public class MyMetaObjectHandler implements MetaObjectHandler {

    @Autowired
    private HttpSession httpSession;

    /**
     * 公共字段方法，插入时生效
     * @param metaObject
     */
    @Override
    public void insertFill(MetaObject metaObject) {
        log.info("公共字段自动填充[insert]");
        log.info(metaObject.toString());

        metaObject.setValue("createTime", LocalDateTime.now());
        metaObject.setValue("updateTime", LocalDateTime.now());
        metaObject.setValue("createUser",httpSession.getAttribute("employee"));
        metaObject.setValue("updateUser",httpSession.getAttribute("employee"));
        metaObject.setValue("createUser",httpSession.getAttribute("user"));
        metaObject.setValue("updateUser",httpSession.getAttribute("user"));
    }

    /**
     * 公共字段方法，修改时生效
     * @param metaObject
     */
    @Override
    public void updateFill(MetaObject metaObject) {
        log.info("公共字段自动填充[update]");
        log.info(metaObject.toString());
        metaObject.setValue("updateTime",LocalDateTime.now());
        metaObject.setValue("updateUser",httpSession.getAttribute("employee"));
        metaObject.setValue("updateUser",httpSession.getAttribute("user"));
    }
}
