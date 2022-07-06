package com.prgrms.coretime.message.domain;

import com.prgrms.coretime.common.entity.BaseEntity;
import com.prgrms.coretime.post.domain.Post;
import com.prgrms.coretime.user.domain.TestUser;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.Assert;

@Entity
@Table(name = "message_room")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class MessageRoom extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "message_room_id")
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "initial_sender_id", referencedColumnName = "user_id")
  private TestUser initialSender;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "initial_receiver_id", referencedColumnName = "user_id")
  private TestUser initialReceiver;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "created_from", referencedColumnName = "post_id")
  private Post createdFrom;

  @Column(name = "is_anonymous", nullable = false)
  private Boolean isAnonymous;

  @Column(name = "is_blocked", nullable = false)
  private Boolean isBlocked;

  @Column(name = "visible_to", nullable = false)
  @Enumerated(EnumType.STRING)
  private VisibilityState visibilityTo;

  @OneToMany(mappedBy = "messageRoom")
  private List<Message> messages = new ArrayList<>();

  @Builder
  public MessageRoom(TestUser initialSender, TestUser initialReceiver, Post createdFrom, Boolean isAnonymous) {
    Assert.notNull(initialSender, "initialSender는 null이 아니어야 합니다.");
    Assert.notNull(initialReceiver, "initialReceiver는 null이 아니어야 합니다.");
    Assert.notNull(createdFrom, "createdFrom은 null이 아니어야 합니다.");
    Assert.notNull(isAnonymous, "isAnonymous는 null이 아이어야 합니다.");

    this.initialSender = initialSender;
    this.initialReceiver = initialReceiver;
    this.createdFrom = createdFrom;
    this.isAnonymous = isAnonymous;
    this.isBlocked = false;
    this.visibilityTo = VisibilityState.BOTH;
  }

  public void changeIsBlocked(Boolean isBlocked) {
    this.isBlocked = isBlocked;
  }

  public void changeVisibilityTo(VisibilityState visibilityTo) {
    if (this.visibilityTo.equals(VisibilityState.BOTH)) {
      this.visibilityTo = visibilityTo;
    } else if (this.visibilityTo.equals(VisibilityState.ONLY_INITIAL_RECEIVER)) {
      if (visibilityTo.equals(VisibilityState.ONLY_INITIAL_SENDER)) {
        this.visibilityTo = VisibilityState.NO_ONE;
      }
    } else if (this.visibilityTo.equals(VisibilityState.ONLY_INITIAL_SENDER)) {
      if (visibilityTo.equals(VisibilityState.ONLY_INITIAL_RECEIVER)) {
        this.visibilityTo = VisibilityState.NO_ONE;
      }
    }
  }

}
