package com.prgrms.coretime.message.domain;

import com.prgrms.coretime.common.entity.BaseEntity;
import com.prgrms.coretime.user.domain.TestUser;
import com.prgrms.coretime.user.domain.User;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.Assert;

@Entity
@Table(name = "message")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Message extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "message_id")
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "message_room_id", referencedColumnName = "message_room_id")
  private MessageRoom messageRoom;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "writer_id", referencedColumnName = "user_id")
  private TestUser writer;

  @Column(name = "content", nullable = false, length = 300)
  private String content;

  @Builder
  public Message(MessageRoom messageRoom, TestUser writer, String content) {
    Assert.notNull(messageRoom, "messageRoom은 null이 아니여야 합니다.");
    Assert.notNull(writer, "writer는 null이 아니여야 합니다.");
    validateContent(content);
    this.messageRoom = messageRoom;
    this.writer = writer;
    this.content = content;
  }

  private void validateContent(String content) {
    Assert.notNull(content, "메세지 내용은 비어있을 수 없습니다.");
    Assert.isTrue(content.length() <= 300,
        "메세지 길이는 300자 이하여야 합니다.");
  }

}
