/*
 * Copyright 2017-2018 the original author(https://github.com/wj596)
 *
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * </p>
 */
package com.liuyang19900520.shiro.filter;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.liuyang19900520.commons.interceptor.LHttpServletRequestWrapper;
import com.liuyang19900520.commons.interceptor.LRequestJsonUtils;
import com.liuyang19900520.commons.pojo.Messages;
import com.liuyang19900520.commons.pojo.ResultVo;
import com.liuyang19900520.shiro.LoginUser;
import com.liuyang19900520.shiro.token.HmacToken;
import com.liuyang19900520.shiro.token.JwtToken;
import com.liuyang19900520.utils.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.AccessControlFilter;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

/**
 * 无状态过滤器--抽象父类
 *
 * @author wangjie (http://www.jianshu.com/u/ffa3cba4c604)
 * @date 2016年6月24日 下午2:55:15
 */
@Slf4j
public abstract class StatelessFilter extends AccessControlFilter {

    protected boolean isHmacSubmission(ServletRequest request) throws IOException {
        HttpServletRequest req = (HttpServletRequest) request;
        LHttpServletRequestWrapper myWrapper = new LHttpServletRequestWrapper(req);
        String jsonStr = LRequestJsonUtils.getRequestJsonString(myWrapper);
        LoginUser loginUser = JsonUtils.jsonToPojo(jsonStr, LoginUser.class);
        String clientKey = loginUser.getUsername();
        String timeStamp = req.getHeader("Date");
        String digest = req.getHeader("Authorization");
        String jwt = req.getHeader("token");
        return (request instanceof HttpServletRequest)
                && StringUtils.isNotBlank(clientKey)
                && StringUtils.isNotBlank(timeStamp)
                && StringUtils.isNotBlank(digest)
                && StringUtils.isBlank(jwt);
    }

    protected AuthenticationToken createHmacToken(ServletRequest request, ServletResponse response) throws IOException {


        HttpServletRequest req = (HttpServletRequest) request;
        LHttpServletRequestWrapper myWrapper = new LHttpServletRequestWrapper(req);
        String jsonStr = LRequestJsonUtils.getRequestJsonString(myWrapper);
        LoginUser loginUser = JsonUtils.jsonToPojo(jsonStr, LoginUser.class);
        String clientKey = loginUser.getUsername();
        String timeStamp = req.getHeader("Date");
        String digest = req.getHeader("Authorization");
        Map<String, Object> parameters = Maps.newHashMap();
        parameters.put("loginUser", loginUser);
        String host = request.getRemoteHost();
        return new HmacToken(clientKey, timeStamp, digest, host, parameters);

    }

    protected boolean isJwtSubmission(ServletRequest request) {
        HttpServletRequest req = (HttpServletRequest) request;
        String jwt = req.getHeader("token");
        return (request instanceof HttpServletRequest)
                && StringUtils.isNotBlank(jwt);
    }

    protected AuthenticationToken createJwtToken(ServletRequest request, ServletResponse response) {
        HttpServletRequest req = (HttpServletRequest) request;
        String jwt = req.getHeader("token");
        String host = request.getRemoteHost();
        log.info("authenticate token token:" + jwt);
        System.out.println("token:" + jwt);
        return new JwtToken(jwt, host);
    }

    protected boolean checkRoles(Subject subject, Object mappedValue) {
        String[] rolesArray = (String[]) mappedValue;
        if (rolesArray == null || rolesArray.length == 0) {
            return true;
        }


        boolean b = subject.hasRole(rolesArray[0]);
        return Stream.of(rolesArray)
                .anyMatch(role -> subject.hasRole(role));
    }

    protected boolean checkPerms(Subject subject, Object mappedValue) {
        String[] perms = (String[]) mappedValue;
        boolean isPermitted = true;
        if (perms != null && perms.length > 0) {
            if (perms.length == 1) {
                if (!subject.isPermitted(perms[0])) {
                    isPermitted = false;
                }
            } else {
                if (!subject.isPermittedAll(perms)) {
                    isPermitted = false;
                }
            }
        }
        return isPermitted;
    }

    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
        Subject subject = getSubject(request, response);
        //未认证
        if (null == subject || !subject.isAuthenticated()) {
            ResultVo.error(Messages.UNAUTHORIZED
                    , "未认证");
            //未授权
        } else {
            ResultVo.error(Messages.FORBIDDEN
                    , "未授权");
        }
        return false;
    }

}