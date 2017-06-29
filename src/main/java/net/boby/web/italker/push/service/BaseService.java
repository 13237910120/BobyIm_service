package net.boby.web.italker.push.service;

import net.boby.web.italker.push.bean.db.User;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.SecurityContext;

/**
 * Created by boby on 2017/6/29 0029.
 */
public class BaseService {

    //添加一个上下文注解，改注解会给SecurityContext赋值；
    //具体的值为我们的拦截器中返回的值
    @Context
    private SecurityContext securityContext;
    //用户修改接口
    //返回自己的个人信息
    protected User getSelf(){
        return (User) securityContext.getUserPrincipal();
    }
}
