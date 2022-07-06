package com.prgrms.coretime.message.domain;

import com.prgrms.coretime.common.entity.BaseEntity;
import com.prgrms.coretime.user.domain.TestUser;
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

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "message_room_id", referencedColumnName = "message_room_id")
  private MessageRoom messageRoom;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "writer_id", referencedColumnName = "user_id")
  private TestUser writer;

  @Column(name = "content", nullable = false, length = 300)
  private String content;

}
