package com.prgrms.coretime.comment.domain;

import static javax.persistence.FetchType.LAZY;
import static javax.persistence.GenerationType.AUTO;

import com.prgrms.coretime.common.entity.BaseEntity;
import com.prgrms.coretime.post.domain.Post;
import com.prgrms.coretime.user.domain.User;
import java.nio.charset.StandardCharsets;
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
import org.springframework.util.Assert;

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
  private Comment(Post post, User user, Comment parent, Boolean isAnonymous, String content) {
    validatePost(post);
    validateUser(user);
    validateIsAnonymous(isAnonymous);
    validateContent(content);

    this.post = post;
    this.user = user;
    this.parent = parent;
    this.isAnonymous = isAnonymous;
    if (this.isAnonymous) {
      setAnonymousSeq(this.post.getNextAnonymousSeq());
    }
    this.content = content;
  }


  @Builder
  public static Comment create(User user, Post post, Comment parent, Boolean isAnonymous,
      String content) {
    return Comment.builder()
        .user(user)
        .post(post)
        .parent(parent)
        .isAnonymous(isAnonymous)
        .content(content)
        .build();
  }

  // Post - Comment 양방향 연관관계 메서드
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
   * TODO : USER 1:N 양방향 필요하면!
   */

  // private setter
  private void setAnonymousSeq(Integer anonymousSeq) {
    this.anonymousSeq = anonymousSeq;
  }

  //Validation

  private void validatePost(Post post) {
    Assert.notNull(post, "게시글은 필수 입니다.");
  }

  /**
   * TODO : User가 현재 로그인한 유저가 맞는지 판단하는 검증 필요
   * 추후 인증 구현 되면 추가할 것
   */
  private void validateUser(User user) {
    Assert.notNull(user, "사용자는 필수 입니다.");
  }

  private void validateIsAnonymous(Boolean isAnonymous) {
    Assert.notNull(isAnonymous, "익명 여부는 필수입니다.");
  }

  /**
   * 목적 : 댓글 검증 1. 댓글이 null이거나 공백인지 3. 댓글 바이트 검증
   */
  private void validateContent(String content) {
    Assert.hasText(content, "댓글은 필수입니다.");
    Assert.isTrue(content.getBytes(StandardCharsets.UTF_8).length > 0 && content.getBytes(
        StandardCharsets.UTF_8).length <= 300, "댓글은 300바이트 이내로 작성되어야 합니다");
  }
}
