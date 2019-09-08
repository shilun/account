package com.account.main.controller;

import com.account.domain.module.TokenTypeEnum;
import com.account.main.AbstractClientController;
import com.account.service.RPCServiceBean;
import com.common.exception.ApplicationException;
import com.common.util.IGlossary;
import com.common.util.RPCResult;
import com.common.util.StringUtils;
import com.common.util.model.SexEnum;
import com.common.util.model.YesOrNoEnum;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.passport.rpc.dto.ProxyDto;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping(method = {RequestMethod.POST})
public class GlobalController extends AbstractClientController {

    private static Map<String, Class> glosseryItems;

    @Resource
    private RPCServiceBean rpcServiceBean;

    static {
        glosseryItems = new HashMap<>();
        glosseryItems.put("yesorno", YesOrNoEnum.class);
        glosseryItems.put("sextype", SexEnum.class);
        glosseryItems.put("tokentype",TokenTypeEnum.class);
        //proxy 代理商
    }

    private Cache<String, List<ProxyDto>> cache = CacheBuilder.newBuilder().initialCapacity(10)
            //设置并发数为5，即同一时间最多只能有5个线程往cache执行写入操作
            .concurrencyLevel(5)
            //设置cache中的数据在写入之后的存活时间为10秒
            .expireAfterWrite(1, TimeUnit.MINUTES)
            //构建cache实例
            .build();

    @RequestMapping(value = "/global/type/{type}")
    @ResponseBody
    public Map<String, Object> buildGlossery(@PathVariable("type") String type) {
        return buildMessage(() -> {
            List<Map<String, Object>> keyValues = new ArrayList<Map<String, Object>>();
            if (StringUtils.equals(type, "proxy")) {
                try {
                    List<ProxyDto> proxyDtos = cache.get(type, new Callable<List<ProxyDto>>() {
                        @Override
                        public List<ProxyDto> call() throws Exception {
                            RPCResult<List<ProxyDto>> listRPCResult = rpcServiceBean.getProxyRpcService().queryAll();
                            if (listRPCResult.getSuccess()) {
                                return listRPCResult.getData();
                            }
                            return null;
                        }
                    });
                    for (ProxyDto dto : proxyDtos) {
                        HashMap<String, Object> item = new HashMap<>();
                        item.put("value", dto.getId());
                        item.put("name", dto.getName());
                        keyValues.add(item);
                    }
                    return keyValues;
                } catch (ExecutionException e) {
                    throw new ApplicationException("buildGlossery proxy unKnow type");
                }
            }
            if (StringUtils.isBlank(type)) {
                throw new ApplicationException("buildGlossery Error unKnow type");
            }
            Class currentEnum = glosseryItems.get(type);
            for (Object o : currentEnum.getEnumConstants()) {
                IGlossary v = (IGlossary) o;
                HashMap<String, Object> item = new HashMap<>();
                item.put("value", v.getValue());
                item.put("name", v.getName());
                keyValues.add(item);
            }
            return keyValues;
        });
    }
}
