package com.prgrms.coretime.message.domain;

import com.prgrms.coretime.common.entity.BaseEntity;
import com.prgrms.coretime.user.domain.User;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "message")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Message extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "message_id")
  private Long id;

  @ManyToOne
  @JoinColumn(name = "message_room_id", referencedColumnName = "message_room_id")
  private MessageRoom messageRoom;

  @ManyToOne
  @JoinColumn(name = "writer_id", referencedColumnName = "user_id")
  private User writer;

  @Column(name = "content", nullable = false, length = 300)
  private String content;

  public void setMessageRoom(MessageRoom messageRoom) {
    if (Objects.nonNull(this.messageRoom)) {
      messageRoom.getMessages().remove(this);
    }
    this.messageRoom = messageRoom;
    messageRoom.getMessages().add(this);
  }

  public void setWriter(User writer) {
    if (Objects.nonNull(this.writer)) {
      writer.getMessageWriters().remove(this);
    }
    this.writer = writer;
    writer.getMessageWriters().add(this);
  }

}
