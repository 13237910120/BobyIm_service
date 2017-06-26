package net.boby.web.italker.push;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import net.boby.web.italker.push.service.AccountService;
import org.glassfish.jersey.server.ResourceConfig;


import java.util.logging.Logger;

/**
 * Created by Administrator on 2017/5/31 0031.
 */
public class Application extends ResourceConfig {
    public Application(){
        //注册逻辑处理包名
//        packages("net.boby.web.italk.push.service.AccountService");
        packages(AccountService.class.getPackage().getName());
        //注册json解析器
        register(JacksonJsonProvider.class);
        //注册日志打印输出
        register(Logger.class);
    }
}
