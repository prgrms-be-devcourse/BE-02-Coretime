package com.prgrms.coretime.post.domain;

import com.prgrms.coretime.comment.domain.Comment;
import com.prgrms.coretime.common.entity.BaseEntity;
import com.prgrms.coretime.message.domain.MessageRoom;
import com.prgrms.coretime.user.domain.User;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Post extends BaseEntity {

  @Id
  @GeneratedValue
  @Column(name = "post_id")
  private Long id;

  @Column(name = "title", nullable = false, length = 50)
  private String title;

  @Column(name = "content", length = 65535)
  private String content;

  @Column(name = "next_anonymous_seq", nullable = false)
  private Integer nextAnonymousSeq = 1;

  @Column(name = "is_anonymous", nullable = false)
  private Boolean isAnonymous = true;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "board_id", referencedColumnName = "board_id")
  private Board board;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", referencedColumnName = "user_id")
  private User user;

  @OneToMany(mappedBy = "post")
  private List<Comment> comments = new ArrayList<>();

  @OneToMany(mappedBy = "createdFrom")
  private List<MessageRoom> messageRooms = new ArrayList<>();

  public void addComment(Comment comment) {
    comment.setPost(this);
  }


  /**
   * Author : 정경일 CreatedAt : 2022.06.22. 22:32 이유 : Comment 생성시 시퀀스번호를 받아오고 증가해야하는 연산 필요해서 메서드 정의
   **/
  public Integer getNextAnonymousSeq() {
    return nextAnonymousSeq++;
  }
}
