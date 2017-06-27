package net.boby.web.italker.push.bean.db;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * Created by boby on 2017/6/27 0027.
 */
@Entity
@Table(name = "TB_PUSH_HISTORY")
public class PushHistory {
    @Id
    @PrimaryKeyJoinColumn
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid",strategy = "uuid2")
    @Column(updatable = false,nullable = false)
    private String id;

    //推送的实体存储的都是Json字符串
    @Lob
    @Column(nullable = false,columnDefinition = "BLOB")
    private String entity;

    //推送实体类型
    @Column(nullable = false)
    private int entityType;

    //接收者，不允许为空
    //一个接收者可以接收很多推送消息
    //fetch = FetchType.EAGER 加载一条消息的时候加载用户信息
    @ManyToOne(optional = false,fetch = FetchType.EAGER,cascade =CascadeType.ALL )
    @JoinColumn(name = "receiverId")//默认是receiber_id
    private User receiver;
    @Column(nullable = false,updatable = false,insertable = false)
    private String receiverId;



    //发送者，不允许为空
    //一个接收者可以接收很多推送消息
    //fetch = FetchType.EAGER 加载一条消息的时候加载用户信息
    @ManyToOne(optional = false,fetch = FetchType.EAGER,cascade =CascadeType.ALL )
    @JoinColumn(name = "senderId")//默认是receiber_id
    private User sender;
    @Column(nullable = false,updatable = false,insertable = false)
    private String senderId;

    //接收者当前状态的设备推送id
    //user.push 可为null
    @Column
    private String receiverPushId;
    //创建时间
    @CreationTimestamp
    @Column(nullable = false)
    private LocalDateTime createAt=LocalDateTime.now();

    //更新时间
    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updateAt=LocalDateTime.now();

    //消息送达时间
    @Column
    private LocalDateTime arrivalAt;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEntity() {
        return entity;
    }

    public void setEntity(String entity) {
        this.entity = entity;
    }

    public int getEntityType() {
        return entityType;
    }

    public void setEntityType(int entityType) {
        this.entityType = entityType;
    }

    public User getReceiver() {
        return receiver;
    }

    public void setReceiver(User receiver) {
        this.receiver = receiver;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getReceiverPushId() {
        return receiverPushId;
    }

    public void setReceiverPushId(String receiverPushId) {
        this.receiverPushId = receiverPushId;
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

    public LocalDateTime getArrivalAt() {
        return arrivalAt;
    }

    public void setArrivalAt(LocalDateTime arrivalAt) {
        this.arrivalAt = arrivalAt;
    }
}
