package com.cmcorg.engine.auth.configuration.mybatisplus;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.cmcorg.engine.auth.util.AuthUserUtil;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class MetaObjectHandlerConfiguration implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {

        Date date = new Date();
        Long currentUserIdDefault = AuthUserUtil.getCurrentUserIdDefault();

        // 实体类有值时，这里不会生效
        this.strictInsertFill(metaObject, "createTime", Date.class, date);
        this.strictInsertFill(metaObject, "createId", Long.class, currentUserIdDefault);
        this.strictInsertFill(metaObject, "updateTime", Date.class, date);
        this.strictInsertFill(metaObject, "updateId", Long.class, currentUserIdDefault);
        this.strictInsertFill(metaObject, "version", Integer.class, 0);

    }

    @Override
    public void updateFill(MetaObject metaObject) {

        // 实体类有值时，这里不会生效
        this.strictUpdateFill(metaObject, "updateTime", Date.class, new Date());
        this.strictUpdateFill(metaObject, "updateId", Long.class, AuthUserUtil.getCurrentUserIdDefault());

    }

}
