package net.boby.web.italker.push.bean.api.account;

import com.google.common.base.Strings;
import com.google.gson.annotations.Expose;

/**
 * Created by boby on 2017/6/27 0027.
 */
public class RegisterModel {
    @Expose
    private String account;
    @Expose
    private String password;
    @Expose
    private String name;
    @Expose
    private String pushId;

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPushId() {
        return pushId;
    }

    public void setPushId(String pushId) {
        this.pushId = pushId;
    }

    //校验
    public static boolean check(RegisterModel loginModel){
        return loginModel!=null
                && !Strings.isNullOrEmpty(loginModel.account)
                && !Strings.isNullOrEmpty(loginModel.password)
                && !Strings.isNullOrEmpty(loginModel.name);
    }
}
