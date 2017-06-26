package net.boby.web.italker.push.bean.db;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 用户关系model
 * 用于用户直接进行好友关系的实现
 * Created by boby on 2017/6/26 0026.
 */
@Entity
@Table(name = "TB_USER")
public class UserFollow {
    //这是一个主键
    @Id
    @PrimaryKeyJoinColumn
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    @Column(updatable = false, nullable = false)
    private String id;

    //定义一个发起人，你关注莫人，这里就是你
    //多对一 -> 你可以关注很多人，你的每一次关注都是一条数据
    //你可以创建很多个关注的信息，所以是多对一
    //这里的多对一是 多个User 对 多个UserFollow
    //optional 不可选，必须存储，一条记录一定要有一个 “你”
    @ManyToOne(optional = false)
    //定义关联表字段名为originid ,对应的是User.id
    @JoinColumn(name = "originId")
    private User origin;
    //把这个列提前到我哦们的mode中，不允许为空，不允许更新插入
    @Column(nullable = false,updatable = false,insertable = false)
    private String originId;

    //定义关注的目标，你关注的人
    //也是多对一，可以被很多人关注，每一次关注都是一条记录
    //所有就是 多个UserFollow 对应一个User 的情况
    @ManyToOne(optional = false)
    //定义关联表字段名为originid ,对应的是User.id
    @JoinColumn(name = "targetId")
    private User target;
    //把这个列提前到我哦们的mode中，不允许为空，不允许更新插入
    @Column(nullable = false,updatable = false,insertable = false)
    private String targetId;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public User getOrigin() {
        return origin;
    }

    public void setOrigin(User origin) {
        this.origin = origin;
    }

    public String getOriginId() {
        return originId;
    }

    public void setOriginId(String originId) {
        this.originId = originId;
    }

    public User getTarget() {
        return target;
    }

    public void setTarget(User target) {
        this.target = target;
    }

    public String getTargetId() {
        return targetId;
    }

    public void setTargetId(String targetId) {
        this.targetId = targetId;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public LocalDateTime getCreateAt() {
        return createAt;
    }

    public void setCreateAt(LocalDateTime createAt) {
        this.createAt = createAt;
    }

    public LocalDateTime getUpdateAt() {
        return updateAt;
    }

    public void setUpdateAt(LocalDateTime updateAt) {
        this.updateAt = updateAt;
    }

    //别名，也就是对target的备注，可以为空
    @Column
    private String alias;
    //定义为创建时间戳，在创建时就已经写入
    @CreationTimestamp
    @Column(nullable = false)
    private LocalDateTime createAt = LocalDateTime.now();

    //定义为更新时间戳，在创建时就已经写入
    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updateAt = LocalDateTime.now();

}
