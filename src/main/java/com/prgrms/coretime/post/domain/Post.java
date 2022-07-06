package com.prgrms.coretime.post.domain;

import com.prgrms.coretime.comment.domain.Comment;
import com.prgrms.coretime.common.entity.BaseEntity;
import com.prgrms.coretime.message.domain.MessageRoom;
import com.prgrms.coretime.post.dto.request.PostUpdateRequest;
import com.prgrms.coretime.user.domain.User;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import lombok.AccessLevel;
import lombok.Builder;
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

  @OneToMany(mappedBy = "post")
  private List<Photo> photos = new ArrayList<>();

  private Integer commentCount = 0;
  private Integer likeCount = 0;

  @OneToMany(mappedBy = "createdFrom")
  private List<MessageRoom> messageRooms = new ArrayList<>();

  @Builder
  public Post(
      String title,
      String content,
      Boolean isAnonymous,
      Board board,
      User user
  ) {
    setTitle(title);
    setContent(content);
    this.isAnonymous = isAnonymous;
    setBoard(board);
    setUser(user);
  }

  private void setTitle(String title) {
    if (Objects.isNull(title)) {
      throw new IllegalArgumentException("Post의 title은 null일 수 없습니다.");
    }else if (title.isBlank()) {
      throw new IllegalArgumentException("Post의 title은 빈 문자열일 수 없습니다.");
    }else if (title.length() > 50) {
      throw new IllegalArgumentException("Post의 title은 50글자를 넘을 수 없습니다.");
    }
    this.title = title;
  }

  private void setContent(String content) {
    if (Objects.isNull(content)) {
      this.content = "";
      return;
    }else if (content.length() > 65535) {
      throw new IllegalArgumentException("Post의 content은 65535글자를 넘을 수 없습니다.");
    }
    this.content = content;
  }

  private void setBoard(Board board) {
    if (Objects.isNull(board)) {
      throw new IllegalArgumentException("Post의 Board는 null일 수 없습니다.");
    }
    this.board = board;
  }

  private void setUser(User user) {
    if (Objects.isNull(user)) {
      throw new IllegalArgumentException("Post의 User는 null일 수 없습니다.");
    }
    this.user = user;
  }

  public void addComment(Comment comment) {
    comment.setPost(this);
    this.commentCount += 1;
  }

  public void removeComment() {
    this.commentCount -= 1;
  }

  public void likePost() {
    this.likeCount += 1;
  }

  public void unlikePost() {
    this.likeCount -= 1;
  }

  public void updatePost(PostUpdateRequest request) {
    setTitle(request.getTitle());
    setContent(request.getContent());
  }

  public Integer getAnonymousSeqAndAdd() {
    return nextAnonymousSeq++;
  }

}
