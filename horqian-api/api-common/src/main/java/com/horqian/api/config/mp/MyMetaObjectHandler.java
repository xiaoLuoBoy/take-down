package com.horqian.api.config.mp;


import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.horqian.api.util.CommonUtils;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * mybatis-plus填充器
 *
 * @author: bz
 * @Date: 2019-05-06 14:54:22
 */
@Component
public class MyMetaObjectHandler implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        var now = LocalDateTime.now();
        this.strictInsertFill(metaObject, "sn", String.class, CommonUtils.getTimeStr());
        this.strictInsertFill(metaObject, "createTime", LocalDateTime.class, now);
        this.strictInsertFill(metaObject, "updateTime", LocalDateTime.class, now);

    }

    @Override
    public void updateFill(MetaObject metaObject) {
        var now = LocalDateTime.now();

        this.strictUpdateFill(metaObject, "updateTime", LocalDateTime.class, now);


    }

}