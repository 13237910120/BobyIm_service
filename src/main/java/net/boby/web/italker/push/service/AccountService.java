package net.boby.web.italker.push.service;

import com.google.common.base.Strings;
import com.sun.org.apache.regexp.internal.RE;
import net.boby.web.italker.push.bean.api.account.AccountRspModel;
import net.boby.web.italker.push.bean.api.account.LoginModel;
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
public class AccountService extends BaseService{

    @POST
    @Path("/login")
    //指定请求与返回的响应体为json
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public ResponseModel<AccountRspModel> login(LoginModel model){
        if(!LoginModel.check(model)){
            //返回参数异常
            return ResponseModel.buildParameterError();
        }

        User user=UserFactory.login(model.getAccount().trim(),model.getPassword());
        if(user!=null){
            //登录成功
            //如果携带pushId
            if(!Strings.isNullOrEmpty(model.getPushId())){
                return bind(user,model.getPushId());
            }
            //返回当前账户
            AccountRspModel rspModel=new AccountRspModel(user);
            return ResponseModel.buildOk(rspModel);
        }else {
            //登录失败
            return ResponseModel.buildLoginError();
        }

    }


    @POST
    @Path("/register")
    //指定请求与返回的响应体为json
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public ResponseModel<AccountRspModel> register(RegisterModel model){

        if(!RegisterModel.check(model)){
            //返回参数异常
            return ResponseModel.buildParameterError();
        }
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

            //如果携带pushId
            if(!Strings.isNullOrEmpty(model.getPushId())){
                return bind(user,model.getPushId());
            }
            //返回当前账户
           AccountRspModel rspModel=new AccountRspModel(user);
            return ResponseModel.buildOk(rspModel);
        }else {
            //注册异常
            return ResponseModel.buildRegisterError();
        }
    }

    @POST
    @Path("/bind/{pushId}")
    //指定请求与返回的响应体为json
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    //从请求头中获取token字段
    //从url地址中国获取
    public ResponseModel<AccountRspModel> bind(@HeaderParam("token") String token,
                                               @PathParam("pushId") String pushId) {
            if(Strings.isNullOrEmpty(pushId)){
                return ResponseModel.buildParameterError();
            }
            //拿到当前账户
        User self= getSelf();
                //进行设备号绑定的操作
        self= UserFactory.bindPushId(self,pushId);
          return  bind(self,pushId);


    }

        private ResponseModel<AccountRspModel> bind(User self,String pushId){
            //进行设备号绑定的操作
          User user=  UserFactory.bindPushId(self,pushId);
            if(user==null) {
                //绑定失败则是服务器异常
                return ResponseModel.buildServiceError();
            }
            //返回当前账户，并且已经绑定了
            AccountRspModel rspModel = new AccountRspModel(user,true);
            return ResponseModel.buildOk(rspModel);
        }

}
