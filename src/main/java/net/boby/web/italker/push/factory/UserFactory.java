package net.boby.web.italker.push.factory;

import net.boby.web.italker.push.bean.db.User;
import net.boby.web.italker.push.utils.Hib;
import net.boby.web.italker.push.utils.TextUtil;
import org.hibernate.Session;

/**
 * Created by boby on 2017/6/27 0027.
 */
public class UserFactory {

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
        User user =new User();
        user.setName(name);
        user.setPassword(password);
        //账户就是手机号
        user.setPhone(account);

        //进行数据库操作
        //首先创建一个会话，
        Session session= Hib.session();
        //开启一个事务
        session.beginTransaction();
        try {
            //保存操作
            session.save(user);
            //提交我们的事务
            session.getTransaction().commit();

            return user;
        }catch (Exception e ){
            //回滚事务
            session.getTransaction().rollback();
            return null;
        }


    }

    private static String encodePassword(String password){
        //密码去除首位空格
        password=password.trim();
        //进行一次md5加密，加盐会更安全，盐也需要存储
        password= TextUtil.getMD5(password);
        //再进行一次对称的Base64加密,当然可以采取加盐方案
        return TextUtil.encodeBase64(password);
    }
}
