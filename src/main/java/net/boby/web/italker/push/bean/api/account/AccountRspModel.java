package net.boby.web.italker.push.bean.api.account;

import com.google.gson.annotations.Expose;
import net.boby.web.italker.push.bean.card.UserCard;
import net.boby.web.italker.push.bean.db.User;

/**
 * 账户部分返回model
 * Created by boby on 2017/6/27 0027.
 */
public class AccountRspModel {
    //用户基本信息
    @Expose
    private UserCard user;
    //当前登录账号
    @Expose
    private String account;
    //当前登录的token
    //可以通过Token获取用户的所有信息
    @Expose
    private String token;
    //表示是否绑定到设备PushId
    @Expose
    private  boolean isBind;
    public AccountRspModel(User user){
        //默认无绑定
       this(user,false);
    }
    public AccountRspModel(User user,boolean isBind){
        this.account=user.getPhone();
        this.token=user.getTaken();
        this.isBind=isBind;
        this.user=new UserCard(user);
    }
}
