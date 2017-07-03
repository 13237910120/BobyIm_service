package net.boby.web.italker.push.service;

import com.google.common.base.Function;
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
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by boby on 2017/6/29 0029.
 */
@Path("/user")
public class UserService extends BaseService {

    @PUT
    //@Path(""),不需要写就是当前目录
    //返回的响应体为json
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public ResponseModel<UserCard> updata(@HeaderParam("token") String token,
                                          UpdateInfoModel model) {

        if (!UpdateInfoModel.check(model) ||
                Strings.isNullOrEmpty(token)) {
            //返回参数异常
            return ResponseModel.buildParameterError();
        }
        //拿到自己的信息
        User user = getSelf();

        //更新用户信息
        user = model.updateToUser(user);
        UserFactory.update(user);
        UserCard userCard = new UserCard(user, true);
        return ResponseModel.buildOk(userCard);


    }


    private ResponseModel<AccountRspModel> bind(User self, String pushId) {
        //进行设备号绑定的操作
        User user = UserFactory.bindPushId(self, pushId);
        if (user == null) {
            //绑定失败则是服务器异常
            return ResponseModel.buildServiceError();
        }
        //返回当前账户，并且已经绑定了
        AccountRspModel rspModel = new AccountRspModel(user, true);
        return ResponseModel.buildOk(rspModel);
    }

    @GET //拉取联系人
    @Path("contact")
    //返回的响应体为json
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public ResponseModel<List<UserCard>> contact() {
        User self = getSelf();
        //拿到我 的联系人
        List<User> users = UserFactory.contacts(self);
        List<UserCard> userCards = users.stream()
                .map(user -> new UserCard(user, true))
                .collect(Collectors.toList());

        return ResponseModel.buildOk(userCards);
    }

    //关注人，
    // 其实是双方同时关注，
    @PUT //修改类使用put
    @Path("contact/{followId}")
    //返回的响应体为json
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public ResponseModel<UserCard> follow(@PathParam("followId") String follwold) {

        if (Strings.isNullOrEmpty(follwold)) {
            return ResponseModel.buildParameterError();
        }
        User self = getSelf();
        //不能关注自己
        if (self.getId().equalsIgnoreCase(follwold)) {
            //返回参数有问题
            return ResponseModel.buildParameterError();
        }
        //找到我要关注的人
        User followUser = UserFactory.findById(follwold);

        if (followUser == null) {
            return ResponseModel.buildNotFoundUserError(null);
        }
        followUser = UserFactory.follow(self, followUser, null);

        if (followUser == null) {
            //关注失败，返回服务器异常
            return ResponseModel.buildServiceError();
        }
        //TODO 通知我关注的人我关注了她

        //返回关注人的信息
        return ResponseModel.buildOk(new UserCard(followUser, true));
    }

    /**
     * 获取默认啥的信息
     *
     * @param id
     * @return
     */
    @GET //修改类使用put
    @Path("{id}")
    //返回的响应体为json
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public ResponseModel<UserCard> getuser(@PathParam("id") String id) {
        if (Strings.isNullOrEmpty(id)) {
            //参数异常
            return ResponseModel.buildParameterError();
        }
        User self = getSelf();
        if (self.getId().equalsIgnoreCase(id)) {
            //返回自己不必查询数据库
            return ResponseModel.buildOk(new UserCard(self, true));

        }
        User user = UserFactory.findById(id);
        if (user == null) {
            //没找到用户
            return ResponseModel.buildNotFoundUserError(null);
        }
        //如果我们有关注的记录，则已我关注需要查询的用户的信息
        boolean isFollow = UserFactory.getUserFollow(self, user) != null;
        return ResponseModel.buildOk(new UserCard(user, isFollow));

    }

    /**
     * 搜索人接口的实现
     *
     * @param name
     * @return
     */
    @GET //搜索人不涉及数据的更爱只是查询则为get
    //http://127.0.0.1.api/user/search
    @Path("/search/{name:(.*)?}") //  名字为任意字符可以为空
    //返回的响应体为json
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public ResponseModel<List<UserCard>> search(@DefaultValue("") @PathParam("name") String name) {
        User self = getSelf();
        //先查询数据
        List<User> searchs = UserFactory.search(name);
        //判断这些人是否有我已经关注的人，
        //如果有，则返回的关注状态中应该设置好状态

        //拿出我的联系人
       final List<User> contacts = UserFactory.contacts(self);
        //把User —>UserCard
        List<UserCard> userCards = searchs.stream()
                .map(user -> {
                    //判断这个人是否是我自己或者是我联系人中的人
                    boolean isFollow = user.getId().equalsIgnoreCase(self.getId())
                            //进行联系人任意匹配，匹配其中的联系人
                            || contacts.stream().anyMatch(
                            contactUser -> user.getId()
                                    .equalsIgnoreCase(user.getId()));
                    //判断这个人是否在我的联系人中
                    return new UserCard(user, isFollow);
                }).collect(Collectors.toList());
        return ResponseModel.buildOk(userCards);

    }


}
