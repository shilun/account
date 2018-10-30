package com.account.web;

import com.account.service.utils.RPCBeanService;
import com.common.exception.BizException;
import com.common.util.StringUtils;
import com.common.web.AbstractController;
import com.passport.rpc.dto.ProxyDto;
import com.passport.rpc.dto.UserDTO;
import org.apache.log4j.Logger;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by shilun on 2017/5/12.
 */
public abstract class AbstractClientController extends AbstractController {
    private static final Logger LOGGER = Logger.getLogger(AbstractClientController.class);

    @Resource
    private RPCBeanService rpcBeanService;
    private Map<String, ProxyDto> proxyMap = new HashMap<>();

    protected String getVer() {
        String ver = getRequest().getHeader("ver");
        if (StringUtils.isBlank(ver)) {
            throw new BizException("ver.null", "获取当前版本信息失败");
        }
        return ver;
    }

    /**
     * 获取用户
     *
     * @return
     */
    protected UserDTO getUserDto() {
        HttpServletRequest request = getRequest();
        UserDTO dto = (UserDTO) request.getSession().getAttribute("userDto");
        if (dto != null) {
            return dto;
        }
       return null;
    }

    public  boolean  isMobileDevice(){
        String requestHeader = getRequest().getHeader("user-agent");
        /**
         * android : 所有android设备
         * mac os : iphone ipad
         * windows phone:Nokia等windows系统的手机
         */
        String[] deviceArray = new String[]{"android","mac os","windows phone"};
        if(requestHeader == null)
            return false;
        requestHeader = requestHeader.toLowerCase();
        for(int i=0;i<deviceArray.length;i++){
            if(requestHeader.indexOf(deviceArray[i])>0){
                return true;
            }
        }
        return false;
    }
}
