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
@Table(name = "TB_APPLY")
public class Apply {
    public  static final  int TYPE_ADD_USER=1;//添加好友
    public  static final  int TYPE_ADD_GROUP=2;//加入群

    @Id
    @PrimaryKeyJoinColumn
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid",strategy = "uuid2")
    @Column(updatable = false,nullable = false)
    private String id;

    //描述部分，对我们的申请信息做描述
    //
    @Column(nullable = false)
    private String description;
    //附件可为空
    //可以附带图片地址，或者其他
    @Column(columnDefinition = "TEXT")
    private String attach;

    //当前申请的类型
    @Column(nullable = false)
    private int type;

    //目标Id,不进行强关联，不建立主外键关系
    //type-->TYPE_ADD_USER : user.Id;
    //type-->TYPE_ADD_GROUP : Group.Id;
    @Column(nullable = false)
    private String targetId;

    //创建时间
    @CreationTimestamp
    @Column(nullable = false)
    private LocalDateTime createAt=LocalDateTime.now();

    //更新时间
    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updateAt=LocalDateTime.now();

    //申请人 可为空，为系统人员
    //一个人可以有很多个申请
    @ManyToOne
    @JoinColumn(name ="applicantId" )
    private User applicant;
    @Column(updatable = false,insertable = false)
    private String applicantId;
}
