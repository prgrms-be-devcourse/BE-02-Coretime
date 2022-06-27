package com.prgrms.coretime.user.domain;

import com.prgrms.coretime.common.entity.BaseEntity;
import com.prgrms.coretime.friend.domain.Friend;
import com.prgrms.coretime.message.domain.Message;
import com.prgrms.coretime.message.domain.MessageRoom;
import com.prgrms.coretime.school.domain.School;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "users")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn
@Getter
public class User extends BaseEntity {

  @Id
  @Column(name = "user_id")
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "school_id")
  private School school;

  @Column(name = "email")
  private String email;

  @Column(name = "profile_image")
  private String profileImage;

  @Column(name = "nickname")
  private String nickname;

  @Column(name = "name")
  private String name;

  @OneToMany(mappedBy = "followerUser")
  private List<Friend> followers = new ArrayList<>();

  @OneToMany(mappedBy = "followeeUser")
  private List<Friend> followees = new ArrayList<>();

  @OneToMany(mappedBy = "writer")
  private List<Message> messageWriters = new ArrayList<>();

  @OneToMany(mappedBy = "initialSender")
  private List<MessageRoom> messageRoomInitialSenders = new ArrayList<>();

  @OneToMany(mappedBy = "initialReceiver")
  private List<MessageRoom> messageRoomInitialReceivers = new ArrayList<>();

  public User(String email, String name) {
    this.email = email;
    this.name = name;
  }
}
