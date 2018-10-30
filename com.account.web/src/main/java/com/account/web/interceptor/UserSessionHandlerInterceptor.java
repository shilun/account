package com.account.web.interceptor;

import com.alibaba.dubbo.config.annotation.Reference;
import com.common.util.RPCResult;
import com.common.util.StringUtils;
import com.passport.rpc.UserRPCService;
import com.passport.rpc.dto.UserDTO;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class UserSessionHandlerInterceptor implements HandlerInterceptor {
    private Logger logger = Logger.getLogger(UserSessionHandlerInterceptor.class);
    @Reference
    private UserRPCService userRPCService;

    @Value("${app.cookie.encode.key}")
    private String cookieEncodeKey;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        UserDTO userDto = (UserDTO) request.getSession().getAttribute("userDto");
        if (userDto == null) {
            userDto = getUserDto(request);
            if (userDto == null) {
                return true;
            }
            request.getSession().setAttribute("userDto", userDto);
        }
        return true;
    }

    protected UserDTO getUserDto(HttpServletRequest request) {
        String token = request.getHeader("cToken");
        if (StringUtils.isBlank(token)) {
            Cookie tokenCookie = null;
            for (Cookie item : request.getCookies()) {
                if (StringUtils.equals(item.getName(), "cToken")) {
                    tokenCookie = item;
                    break;
                }
            }
            if (tokenCookie == null) {
                return null;
            }
            token = tokenCookie.getValue();
        }
        if (StringUtils.isBlank(token)) {
            return null;
        }
        RPCResult<UserDTO> result = userRPCService.verfiyToken(token);
        if (result.getSuccess()) {
            return result.getData();
        }
        return result.getData();
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }
}