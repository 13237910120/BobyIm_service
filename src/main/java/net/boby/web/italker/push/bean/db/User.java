package net.boby.web.italker.push.bean.db;

import org.hibernate.annotations.*;

import javax.persistence.*;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.security.auth.Subject;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * 用户modeld，对应数据库
 * Created by boby on 2017/6/26 0026.
 */
@Entity
@Table(name = "TB_USER")
public class User implements Principal {
    //这是一个主键
    @Id
    @PrimaryKeyJoinColumn
    //主键生成存储的类型是UUID
    @GeneratedValue(generator = "uuid")
//    把uuid的生成器定义为uuid2，uuid2 是常规的uuid  toString
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    //不允许更改，不允许为null
    @Column(updatable = false, nullable = false)
    private String id;
    //用户名，必须为一
    @Column(nullable = false, length = 128, unique = true)
    private String name;
    @Column(nullable = false, length = 62, unique = true)
    private String phone;
    @Column(nullable = false)
    private String password;
    @Column
    private String portrait;
    @Column
    private String description;
    //性别有初始值，不允许为空
    @Column(nullable = false)
    private int sex = 0;
    //可以拉去用户信息，所以token必须唯一
    @Column(unique = true)
    private String token;
    //用于推送Id
    @Column
    private String pushId;
    //定义为创建时间戳，在创建时就已经写入
    @CreationTimestamp
    @Column(nullable = false)
    private LocalDateTime createAt = LocalDateTime.now();

    //定义为更新时间戳，在创建时就已经写入
    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updateAt = LocalDateTime.now();

    //最后一次收到消息的时间
    @Column
    private LocalDateTime lastReceivedAt = LocalDateTime.now();

    //我关注的人的列表方法
    //对应的数据库字段为TB_FOLLOW.originId
    @JoinColumn(name = "originId",nullable = true)
    //定义为懒加载，默认加载User信息的时候并不查询这个集合
    @LazyCollection(LazyCollectionOption.EXTRA)
    //一对多，一个用户可以关注很多人
    @OneToMany(fetch = FetchType.LAZY,cascade = CascadeType.ALL)
    private Set<UserFollow> following=new HashSet<>();

    //关注的我人的列表方法
    //对应的数据库字段为TB_FOLLOW.targetId
    @JoinColumn(name = "targetId",nullable = true)
    //定义为懒加载，默认加载User信息的时候并不查询这个集合
    @LazyCollection(LazyCollectionOption.EXTRA)
    //一对多，一个用户可以被很多人关注
    @OneToMany(fetch = FetchType.LAZY,cascade = CascadeType.ALL)
    private Set<UserFollow> follows=new HashSet<>();
    //我所创建的群，
    //对应字段的时候 Group.ownerId
    @JoinColumn(name = "ownerId")
    //懒加载集合尽可能不加载具体数据
    //当访问Group.size的时候也不加载，仅仅查询数量不加载具体的数据，
    //只有遍历时才加载
    @LazyCollection(LazyCollectionOption.EXTRA)
    //FetchType.LAZY:懒加载，加载用户信息时不加载这个集合
    @OneToMany(fetch =FetchType.LAZY,cascade = CascadeType.ALL)
    private Set<Group> groups=new HashSet<>();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPortrait() {
        return portrait;
    }

    public void setPortrait(String portrait) {
        this.portrait = portrait;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getPushId() {
        return pushId;
    }

    public void setPushId(String pushId) {
        this.pushId = pushId;
    }

    public LocalDateTime getCreateAt() {
        return createAt;
    }


    public LocalDateTime getUpdateAt() {
        return updateAt;
    }

    public void setUpdateAt(LocalDateTime updateAt) {
        this.updateAt = updateAt;
    }

    public LocalDateTime getLastReceivedAt() {
        return lastReceivedAt;
    }

    public void setLastReceivedAt(LocalDateTime lastReceivedAt) {
        this.lastReceivedAt = lastReceivedAt;
    }

    public void setCreateAt(LocalDateTime createAt) {
        this.createAt = createAt;
    }

    public Set<UserFollow> getFollowing() {
        return following;
    }

    public void setFollowing(Set<UserFollow> following) {
        this.following = following;
    }

    public Set<UserFollow> getFollows() {
        return follows;
    }

    public void setFollows(Set<UserFollow> follows) {
        this.follows = follows;
    }

    public Set<Group> getGroups() {
        return groups;
    }

    public void setGroups(Set<Group> groups) {
        this.groups = groups;
    }
}
