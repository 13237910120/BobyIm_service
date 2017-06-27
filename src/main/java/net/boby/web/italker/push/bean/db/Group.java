package net.boby.web.italker.push.bean.db;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * Created by Administrator on 2017/6/27 0027.
 */
@Entity
@Table(name = "TB_GROUP")
public class Group {
    @Id
    @PrimaryKeyJoinColumn
    //主键生成存储的类型是UUID,自动生成uuid
    @GeneratedValue(generator = "uuid")
//    把uuid的生成器定义为uuid2，uuid2 是常规的uuid  toString
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    //不允许更改，不允许为null
    @Column(updatable = false, nullable = false)
    private String id;
    //用户名，必须为一

    @Column(nullable = false)
    private String name;
    //群描述
    @Column(nullable = false)
    private String descripton;
    //
    @Column(nullable = false)
    private String picture;
    //创建时间
    @CreationTimestamp
    @Column(nullable = false)
    private LocalDateTime createAt=LocalDateTime.now();

    //更新时间
    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updateAt=LocalDateTime.now();

    //群的创建者，
    //optional：可选为false,必须有一个创建者，
    // fetch加载方式为急加载，意味着加载群信息的时候就必须加载创建者的信息
    // cascade:联级的级别为ALL，所有的更改（更新，删除）都将进行关系的更新
    @ManyToOne(optional = false,fetch = FetchType.EAGER,cascade = CascadeType.ALL)
    @JoinColumn(name = "ownerId")
    private User owner;
    @Column(nullable = false,updatable = false,insertable = false)
    private String ownerId;


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

    public String getDescripton() {
        return descripton;
    }

    public void setDescripton(String descripton) {
        this.descripton = descripton;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
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
}
