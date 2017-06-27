package net.boby.web.italker.push.service;

import net.boby.web.italker.push.bean.api.account.RegisterModel;
import net.boby.web.italker.push.bean.card.UserCard;
import net.boby.web.italker.push.bean.db.User;
import net.boby.web.italker.push.bean.db.UserFollow;
import net.boby.web.italker.push.factory.UserFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;


/**
 * Created by Administrator on 2017/5/31 0031.
 */
@Path("/account")//访问路劲
public class AccountService {


    @POST
    @Path("/register")
    //指定请求与返回的响应体为json
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public UserCard register(RegisterModel model){
        User user=UserFactory.findByPhone(model.getAccount().trim());
        if(user!=null){
            UserCard card=new UserCard();
            card.setPhone("已有了");
            return card;
        }
        user=UserFactory.findByName(model.getName().trim());
        if(user!=null){
            UserCard card=new UserCard();
            card.setName("已有了name");
            return card;
        }

         user= UserFactory.register(model.getAccount(),
                model.getPassword(),
                model.getName());
        if(user!=null){
            UserCard card=new UserCard();
            card.setName(user.getName());
            card.setPhone(user.getPhone());
            card.setSex(user.getSex());
            card.setFollow(true);
            card.setModifyAt(user.getUpdateAt());
            return card;
        }

        return null;
    }

}
