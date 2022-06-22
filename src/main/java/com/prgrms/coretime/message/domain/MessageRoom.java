package com.prgrms.coretime.message.domain;

import com.prgrms.coretime.user.domain.User;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "message_room")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class MessageRoom {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "initial_sender_id", referencedColumnName = "id")
  private User initialSender;

  @ManyToOne
  @JoinColumn(name = "initial_receiver_id", referencedColumnName = "id")
  private User initialReceiver;

  // TODO: Post로 수정
  // private User createdFrom;

  private boolean isBlocked;

  @OneToMany(mappedBy = "messageRoom")
  private List<Message> messages = new ArrayList<>();

  public void setInitialSender(User initialSender) {
    if (Objects.nonNull(this.initialSender)) {
      initialSender.getMessageRoomInitialSenders().remove(this);
    }
    this.initialSender = initialSender;
    initialSender.getMessageRoomInitialSenders().add(this);

  }

  public void setInitialReceiver(User initialReceiver) {
    if (Objects.nonNull(this.initialReceiver)) {
      initialReceiver.getMessageRoomInitialReceivers().remove(this);
    }
    this.initialReceiver = initialReceiver;
    initialReceiver.getMessageRoomInitialReceivers().add(this);
  }

}
