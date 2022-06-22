package com.prgrms.coretime.comment.domain;

import static javax.persistence.FetchType.LAZY;
import static javax.persistence.GenerationType.AUTO;

import com.prgrms.coretime.common.entity.BaseEntity;
import com.prgrms.coretime.post.domain.Post;
import com.prgrms.coretime.user.domain.User;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "comment")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Comment extends BaseEntity {

  @Id
  @GeneratedValue(strategy = AUTO)
  @Column(name = "comment_id")
  private Long id;

  @ManyToOne(fetch = LAZY)
  @JoinColumn(name = "parent_id")
  private Comment parent;

  @OneToMany(mappedBy = "parent", orphanRemoval = false)
  private List<Comment> children = new ArrayList<>();

  // temp column : user
  @ManyToOne(fetch = LAZY)
  @JoinColumn(name = "user_id")
  private User user;

  // temp column : post
  @ManyToOne(fetch = LAZY)
  @JoinColumn(name = "post_id")
  private Post post;

  @Column(name = "anonymous_seq", nullable = true)
  private Integer anonymousSeq;

  @Column(name = "isAnonymous", nullable = false)
  private Boolean isAnonymous;

  @Column(name = "content", nullable = false, length = 300)
  private String content;

  @Column(name = "is_deleted", nullable = false)
  private Boolean isDelete;

  //TODO: 생성자 처리 어떻게 할 건지, RequestDto 말고 여기서 Dto 받아서 생성할 건지 고민
  @Builder
  public Comment(Post post, User user, Boolean isAnonymous, String content) {
    this.post = post;
    this.user = user;
    this.isAnonymous = isAnonymous;
    this.content = content;
  }

  // Post - Comment 연관관계 메서드
  public void setPost(Post post) {
    if (Objects.nonNull(this.post)) {
      this.post.getComments().remove(this);
    }

    this.post = post;
    post.getComments().add(this);
  }

  /**
   * TODO : 대댓글 연관관계 설정 1:N 양방향
   * */

  /**
   * TODO : USER 1:N 일단 단방향
   */
  public void setUser(User user) {
    this.user = user;
  }

  /***
   * TODO : validation
   */

}
