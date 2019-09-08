package com.account.main.controller;

import com.account.domain.Config;
import com.account.main.AbstractClientController;
import com.account.main.controller.dto.ConfigDto;
import com.account.service.ConfigService;
import com.common.annotation.RoleResource;
import com.common.util.BeanCoper;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Map;

@RestController
@RequestMapping(method = {RequestMethod.POST})
public class ConfigController extends AbstractClientController {

    @Resource
    private ConfigService configService;

    /**
     * 查询
     *
     * @param info
     * @return
     */
    @RoleResource(resource = "account")
    @RequestMapping("/config/list")
    public Map<String, Object> list(@RequestBody ConfigDto info) {
        return buildMessage(() -> {
            Config query = new Config();
            BeanCoper.copyProperties(query, info);
            return configService.queryByPage(query, info.getPageinfo().getPage());
        });
    }

    /**
     * 查询
     *
     * @param content
     * @return
     */
    @RoleResource(resource = "account")
    @RequestMapping("/config/view")
    public Map<String, Object> view(@RequestBody String content) {
        return buildMessage(() ->
                configService.findById(getIdByJson(content)));
    }

    /**
     * 查询
     *
     * @param dto
     * @return
     */
    @RoleResource(resource = "account")
    @RequestMapping("/config/save")
    public Map<String, Object> save(@RequestBody ConfigDto dto) {
        return buildMessage(() -> {
            Config query = new Config();
            BeanCoper.copyProperties(query, dto);
            configService.save(query);
            return null;
        });
    }

}
