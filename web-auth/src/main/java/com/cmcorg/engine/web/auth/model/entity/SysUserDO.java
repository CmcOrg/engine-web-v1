package com.cmcorg.engine.web.auth.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cmcorg.engine.web.model.generate.model.annotation.RequestClass;
import com.cmcorg.engine.web.model.generate.model.annotation.RequestField;
import com.cmcorg.engine.web.model.generate.model.constant.WebModelConstant;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@RequestClass(tableIgnoreFields = WebModelConstant.TABLE_IGNORE_FIELDS_ONE)
@EqualsAndHashCode(callSuper = true)
@TableName(value = "sys_user")
@Data
@Schema(description = "用户主表")
public class SysUserDO extends BaseEntity {

    @RequestField(formTitle = "是否正常")
    @Schema(description = "正常/冻结")
    private Boolean enableFlag;

    @Schema(description = "是否注销，未使用，而是采取直接删除的方式，目的：防止数据量越来越大")
    private Boolean delFlag;

    @Schema(description = "用户 jwt私钥后缀（uuid）")
    private String jwtSecretSuf;

    @Schema(description = "密码，可为空，如果为空，则登录时需要提示【进行忘记密码操作】")
    private String password;

    @Schema(description = "邮箱，可以为空")
    private String email;

    @Schema(description = "登录名，可以为空")
    private String signInName;

    @Schema(description = "手机号，可以为空")
    private String phone;

    @Schema(description = "租户主键 id")
    private Long tenantId;

    @Schema(description = "微信 openId，可以为空")
    private String wxOpenId;

}
