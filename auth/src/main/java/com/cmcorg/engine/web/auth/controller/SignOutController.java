package com.cmcorg.engine.web.auth.controller;

import com.cmcorg.engine.web.auth.model.vo.ApiResultVO;
import com.cmcorg.engine.web.auth.service.SignOutService;
import com.cmcorg.engine.web.model.model.annotation.WebPage;
import com.cmcorg.engine.web.model.model.enums.PageTypeEnum;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@WebPage(type = PageTypeEnum.NONE)
@RestController
@RequestMapping(value = "/signOut")
@Tag(name = "退出登录")
public class SignOutController {

    @Resource
    SignOutService baseService;

    @PostMapping(value = "/self")
    @Operation(summary = "当前用户-退出登录")
    public ApiResultVO<String> signOut() {
        return ApiResultVO.ok(baseService.signOut());
    }

}
