package net.boby.web.italker.push.factory;

import com.google.common.base.Strings;
import net.boby.web.italker.push.bean.db.User;
import net.boby.web.italker.push.bean.db.UserFollow;
import net.boby.web.italker.push.utils.Hib;
import net.boby.web.italker.push.utils.TextUtil;
import org.hibernate.Session;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Created by boby on 2017/6/27 0027.
 */
public class UserFactory {

    //通过token找到user
    //只能自己使用，查询的是个人信息，非他人信息
    public static User findByToken(String token) {
        return Hib.query(session -> (User) session.createQuery("from User where token=:token")
                .setParameter("token", token)
                .uniqueResult());
    }

    //通过phone找到user
    public static User findByPhone(String phone) {
        return Hib.query(session -> (User) session.createQuery("from User where phone=:inPhone")
                .setParameter("inPhone", phone)
                .uniqueResult());
    }

    public static User findByName(String name) {
        return Hib.query(session -> (User) session.createQuery("from User where name=:inName")
                .setParameter("inName", name)
                .uniqueResult());
    }

    public static User findById(String id) {

        //通过Id查询更方便
        return Hib.query(session -> session.get(User.class, id));
    }

    /**
     * 更新到数据库
     *
     * @param user
     * @return
     */
    public static User update(User user) {
        return Hib.query(session -> {
            session.saveOrUpdate(user);
            return user;
        });
    }

    /**
     * 使用账户和密码进行登录
     *
     * @param account
     * @param password
     * @return
     */
    public static User login(String account, String password) {
        String accountStr = account.trim();
        //把原文进行同样的出来才能进行匹配
        String encodePassword = encodePassword(password);
        User user = Hib.query(session ->
                (User) session.createQuery("from User where phone=:phone and password=:password")
                        .setParameter("phone", accountStr)
                        .setParameter("password", encodePassword)
                        .uniqueResult()
        );
        if (user != null) {
            user = login(user);
        }
        return user;
    }

    /**
     * 用户注册
     * 注册操作需要写入数据库，并返回数据库中的user信息
     *
     * @param account  账户
     * @param password 密码
     * @param name     用户名
     * @return User
     */
    public static User register(String account, String password, String name) {
        //去掉首位空格
        account.trim();
        password = encodePassword(password);
        User user = createUser(account, password, name);
        if (user == null) {
            user = login(user);
        }

        return user;

    }

    /**
     * 给当前账户绑定lPushId
     *
     * @param user   自己的User
     * @param pushId 自己设备的PushId
     * @return User
     */
    public static User bindPushId(User user, String pushId) {
        //第一步，查询是否有其它账户绑定了这个设备
        Hib.queryOnly(session -> {
            @SuppressWarnings("unchecked")
            List<User> userList = (List<User>) session.
//                    createQuery("from User where lower(pushId)=: pushId and id!=:userId ")
        createQuery("from User where lower(pushId)=:pushId and id!=:userId")
                    .setParameter("pushId", pushId)
                    .setParameter("userId", user.getId())
                    .list();

            for (User u : userList) {
                u.setPushId(null);
                session.saveOrUpdate(u);
            }
        });
        if (pushId.equalsIgnoreCase(user.getPushId())) {
            //如果当前需要绑定的设备Id,之前已经绑定过了
            //那么不需要绑定
            return user;
        } else {
            //如果之前的设备Id,和需要绑定的不同
            //那么需要单点登录，让之前的设备退出账户
            //给之前的账户推送一条退出消息
            if (Strings.isNullOrEmpty(user.getPushId())) {
                //TODO 推送一条退出消息
            }
            //更新新的设备Id
            user.setPushId(pushId);
            return update(user);
        }

    }

    /**
     * 对密码进行加密操作
     *
     * @param password 原文
     * @return 密文
     */

    private static String encodePassword(String password) {
        //密码去除首位空格
        password = password.trim();
        //进行一次md5加密，加盐会更安全，盐也需要存储
        password = TextUtil.getMD5(password);
        //再进行一次对称的Base64加密,当然可以采取加盐方案
        return TextUtil.encodeBase64(password);
    }

    /**
     * 注册部分新建用户逻辑
     *
     * @param account
     * @param password
     * @param name
     * @return 一个用户
     */
    private static User createUser(String account, String password, String name) {
        User user = new User();
        user.setName(name);
        user.setPassword(password);
        //账户就是手机号
        user.setPhone(account);
        //数据储存
        // 数据库存储
        return Hib.query(session -> {
            session.save(user);
            return user;
        });
    }

    /**
     * 把一个User 进行登录操作
     * 本质上是进行保存token
     *
     * @param user
     * @return
     */
    private static User login(User user) {
        //使用一个随机的UUID值充当Token
        String newToken = UUID.randomUUID().toString();
        //进行一次Base64格式化
        newToken = TextUtil.encodeBase64(newToken);
        //保存token
        user.setToken(newToken);
        return update(user);
    }

    /**
     * 获取联系人的列表
     *
     * @param self
     * @return
     */
    public static List<User> contacts(User self) {

        return Hib.query(session -> {
            //重新加载一次用户信息到self中，和当前的session绑定
            session.load(self, self.getId());
            //获取我关注的人
            Set<UserFollow> follows = self.getFollowing();

            //使用简写方式
            return follows.stream()
                    .map(UserFollow::getTarget)
                    .collect(Collectors.toList());


        });

    }

    /**
     * 关注人 操作
     *
     * @param orgin  发起者
     * @param target 被关注饿得人
     * @param alias  备注名
     * @return 备注人的信息
     */

    public static User follow(final User orgin, final User target, final String alias) {
        UserFollow userFollow = getUserFollow(orgin, target);
        if (userFollow != null) {
            //已关注直接返回
            return userFollow.getTarget();
        }
        return Hib.query(session -> {
            //想要重新操作懒加载的数据需要重新load一次
            session.load(orgin, orgin.getId());
            session.load(target, target.getId());
            //我关注人的时候同时他也关注我，所以需要添加两条UserFollows数据
            UserFollow orginFollow = new UserFollow();
            orginFollow.setOrigin(orgin);
            orginFollow.setTarget(target);
            //备注是我对他的，他对我默认是没哟备注的
            orginFollow.setAlias(alias);

            UserFollow targetFollow = new UserFollow();
            targetFollow.setOrigin(target);
            targetFollow.setTarget(orgin);
            targetFollow.setAlias(alias);
            session.save(orginFollow);
            session.save(targetFollow);
            return target;

        });


    }

    /**
     * 查询两个人 是否已经被关注
     *
     * @param orgin
     * @param target 发起者
     * @return 返回中间类，
     */
    public static UserFollow getUserFollow(final User orgin, final User target) {

        return Hib.query(session -> (UserFollow) session.createQuery("from UserFollow where originId=:originId and targetId =:targetId")
                .setParameter("originId", orgin.getId())
                .setParameter("targetId", target.getId())
                //查询一条数据
                .setMaxResults(1)
                .uniqueResult());
    }

    /**
     * 搜索联系人的实现
     * 为了简化分页只返回20条数据
     *
     * @param name 查询的name,允许为空
     * @return 查询到的用户集合，如果name为空，则返回最近的用户
     */
    @SuppressWarnings("unchecked")
    public static List<User> search(String name) {
        if (Strings.isNullOrEmpty(name)) {
            name = "";
        }
        final String searchName = "%" + name + "%";

        return Hib.query(session ->
            //查询的条件: name 忽略大小写， 并且使用like模糊查询
            //头像和描述必须完整才能查询的到
            (List<User>)session.createQuery("from User where lower(name) like :name and portrait is not null and description is not null")
                    .setParameter("name", searchName)
                    .setMaxResults(20)//至多20条
                    .list()
        );
    }
}
