package net.boby.web.italker.push.service;

import com.sun.media.jfxmedia.Media;
import net.boby.web.italker.push.bean.User;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.logging.Logger;


/**
 * Created by Administrator on 2017/5/31 0031.
 */
@Path("/account")//访问路劲
public class AccountService {
    @GET
    @Path("/Login")
    public String get(){
        System.out.println("log");
        return  "你好，boby";
    }
    @POST
    @Path("/Login")
    //指定请求与返回的响应体为json
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public User post(){
        User user=new User();
        user.setId(1);
        user.setName("波比");
        user.setSex(2);
        return  user;
    }

}
