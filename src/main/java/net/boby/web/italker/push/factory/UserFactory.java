package net.boby.web.italker.push.factory;

import com.google.common.base.Strings;
import net.boby.web.italker.push.bean.db.User;
import net.boby.web.italker.push.utils.Hib;
import net.boby.web.italker.push.utils.TextUtil;
import org.hibernate.Session;

import java.util.List;
import java.util.UUID;

/**
 * Created by boby on 2017/6/27 0027.
 */
public class UserFactory {

    //通过token找到user
    //只能自己使用，查询的是个人信息，非他人信息
    public static User findByToken(String token){
        return   Hib.query(session -> (User) session.createQuery("from User where token=:token")
                .setParameter("token",token)
                .uniqueResult());
    }

    //通过phone找到user
    public static User findByPhone(String phone){
      return   Hib.query(session -> (User) session.createQuery("from User where phone=:inPhone")
              .setParameter("inPhone",phone)
              .uniqueResult());
    }

    public static User findByName(String name){
        return   Hib.query(session -> (User) session.createQuery("from User where name=:inName")
                .setParameter("inName",name)
                .uniqueResult());
    }

    /**
     * 使用账户和密码进行登录
     * @param account
     * @param password
     * @return
     */
    public static User login(String account,String password){
        String accountStr=account.trim();
        //把原文进行同样的出来才能进行匹配
        String encodePassword=encodePassword(password);
        User user=Hib.query(session ->
              (User)session.createQuery("from User where phone=:phone and password=:password")
                .setParameter("phone",accountStr)
                .setParameter("password",encodePassword)
                 .uniqueResult()
        );
        if(user!=null){
            user=login(user);
        }
        return user;
    }

    /**
     * 用户注册
     * 注册操作需要写入数据库，并返回数据库中的user信息
     * @param account 账户
     * @param password 密码
     * @param name 用户名
     * @return User
     */
    public static User register(String account,String password,String name){
       //去掉首位空格
        account.trim();
        password=encodePassword(password);
        User user =createUser(account,password,name);
        if (user == null) {
            user=login(user);
        }

        return user;

    }

    /**
     * 给当前账户绑定lPushId
     * @param user 自己的User
     * @param pushId 自己设备的PushId
     * @return User
     */
    public static User bindPushId(User user,String pushId){
        //第一步，查询是否有其它账户绑定了这个设备
        Hib.queryOnly(session ->{
            @SuppressWarnings("unchecked")
            List<User> userList=( List<User> )session.
                    createQuery("from User where lower(pushId)=: pushId and phone !=: userId ")
                    .setParameter("pushId",pushId)
                    .setParameter("userId",user.getId())
                    .list();

            for(User u: userList){
                u.setPushId(null);
                session.saveOrUpdate(u);
            }
        } );
        if(pushId.equalsIgnoreCase(user.getPushId())){
            //如果当前需要绑定的设备Id,之前已经绑定过了
            //那么不需要绑定
            return user;
        }else {
            //如果之前的设备Id,和需要绑定的不同
            //那么需要单点登录，让之前的设备退出账户
            //给之前的账户推送一条退出消息
            if(Strings.isNullOrEmpty(user.getPushId())){
                //TODO 推送一条退出消息
            }
            //更新新的设备Id
            user.setPushId(pushId);
            return Hib.query(session -> {
                session.saveOrUpdate(user);
                return user;
            });
        }

    }

    /**
     * 对密码进行加密操作
     * @param password 原文
     * @return 密文
     */

    private static String encodePassword(String password){
        //密码去除首位空格
        password=password.trim();
        //进行一次md5加密，加盐会更安全，盐也需要存储
        password= TextUtil.getMD5(password);
        //再进行一次对称的Base64加密,当然可以采取加盐方案
        return TextUtil.encodeBase64(password);
    }

    /**
     * 注册部分新建用户逻辑
     * @param account
     * @param password
     * @param name
     * @return 一个用户
     */
    private  static User createUser(String account,String password,String name){
        User user =new User();
        user.setName(name);
        user.setPassword(password);
        //账户就是手机号
        user.setPhone(account);
        //数据储存
        return Hib.query(session -> (User)session.save(user));
    }

    /**
     * 把一个User 进行登录操作
     * 本质上是进行保存token
     * @param user
     * @return
     */
    private static User login(User user){
        //使用一个随机的UUID值充当Token
        String newToken= UUID.randomUUID().toString();
        //进行一次Base64格式化
        newToken=TextUtil.encodeBase64(newToken);
        //保存token
        user.setToken(newToken);
        return Hib.query(session -> {
            session.saveOrUpdate(user);
            return user;
        });
    }


}
