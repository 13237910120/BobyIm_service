package net.boby.web.italker.push.service;

import com.sun.org.apache.regexp.internal.RE;
import net.boby.web.italker.push.bean.api.account.AccountRspModel;
import net.boby.web.italker.push.bean.api.account.RegisterModel;
import net.boby.web.italker.push.bean.api.base.ResponseModel;
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
    public ResponseModel<AccountRspModel> register(RegisterModel model){
        User user=UserFactory.findByPhone(model.getAccount().trim());
        if(user!=null){
            //已有账户
            return ResponseModel.buildHaveAccountError();
        }
        user=UserFactory.findByName(model.getName().trim());
        if(user!=null){
            //已有名字
             return ResponseModel.buildHaveNameError();
        }

         user= UserFactory.register(model.getAccount(),
                model.getPassword(),
                model.getName());
        if(user!=null){
           AccountRspModel rspModel=new AccountRspModel(user);
            return ResponseModel.buildOk(rspModel);
        }else {
            //注册异常
            return ResponseModel.buildRegisterError();
        }
    }

}
