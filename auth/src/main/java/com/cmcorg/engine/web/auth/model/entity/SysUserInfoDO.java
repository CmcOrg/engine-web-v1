package com.cmcorg.engine.web.auth.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@TableName(value = "sys_user_info")
@Data
@Schema(description = "用户基本信息表")
public class SysUserInfoDO {

    @TableId(type = IdType.ASSIGN_ID)
    @Schema(description = "用户主键 id")
    private Long id;

    @Schema(description = "该用户的 uuid，本系统使用 id，不使用 uuid")
    private String uuid;

    @Schema(description = "昵称")
    private String nickname;

    @Schema(description = "个人简介")
    private String bio;

    @Schema(description = "头像uri")
    private String avatarUri;

}
