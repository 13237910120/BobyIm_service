package net.boby.web.italker.push.service;

import com.google.common.base.Strings;
import net.boby.web.italker.push.bean.api.account.AccountRspModel;
import net.boby.web.italker.push.bean.api.base.ResponseModel;
import net.boby.web.italker.push.bean.api.user.UpdateInfoModel;
import net.boby.web.italker.push.bean.card.UserCard;
import net.boby.web.italker.push.bean.db.User;
import net.boby.web.italker.push.factory.UserFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.SecurityContext;

/**
 * Created by boby on 2017/6/29 0029.
 */
@Path("/user")
public class UserService extends BaseService{

    @PUT
    //@Path(""),不需要写就是当前目录
    //返回的响应体为json
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public ResponseModel<UserCard> updata(@HeaderParam("token") String  token,
                                          UpdateInfoModel  model){

        if(!UpdateInfoModel.check(model)||
                Strings.isNullOrEmpty(token)){
            //返回参数异常
            return ResponseModel.buildParameterError();
        }
        //拿到自己的信息
        User user=getSelf();

            //更新用户信息
            user=model.updateToUser(user);
           UserFactory.update(user);
            UserCard userCard=new UserCard(user,true);
            return ResponseModel.buildOk(userCard);


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
