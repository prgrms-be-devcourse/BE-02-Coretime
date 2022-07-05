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
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.Assert;

@Getter
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

  @OneToMany(mappedBy = "comment")
  private List<CommentLike> likes = new ArrayList<>();

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
  public Comment(User user, Post post, Comment parent, Boolean isAnonymous, Integer anonymousSeq,
      String content) {
    validatePost(post);
    validateUser(user);
    validateIsAnonymous(isAnonymous);
    validateContent(content);

    setPost(post); // 양방향
    this.user = user; // 단방향
    setParent(parent); // 양방향
    this.isAnonymous = isAnonymous;
    this.anonymousSeq = anonymousSeq;
    this.isDelete = false;
    this.content = content;
  }

  // 1. Post - Comment 양방향 연관관계 메서드
  public void setPost(Post post) {
    if (Objects.nonNull(this.post)) {
      this.post.getComments().remove(this);
    }

    this.post = post;
    post.getComments().add(this);
  }

  // 2. Parent - Child 댓글 대댓글 양방향 연관관계.
  public void setParent(Comment parent) {
    if (Objects.isNull(parent)) {
      return;
    }
    this.parent = parent;
    parent.getChildren().add(this);
  }

  public void addChild(Comment child) {
    child.setParent(this);
  }

  // 3. 댓글과 좋아요 양방향 연관관계.
  public void addLike(CommentLike commentLike) {
    commentLike.setComment(this);
  }


  // Getter
  public Long getId() {
    return id;
  }

  public Comment getParent() {
    return parent;
  }

  public List<Comment> getChildren() {
    return children;
  }

  public Integer getAnonymousSeq() {
    return anonymousSeq;
  }

  public Boolean getAnonymous() {
    return isAnonymous;
  }

  public String getContent() {
    return content;
  }

  // Update field
  public void updateDelete() {
    this.isDelete = true;
  }

  // Private Setter
  private void setAnonymousSeq(Integer anonymousSeq) {
    this.anonymousSeq = anonymousSeq;
  }

  //Validation
  private void validatePost(Post post) {
    Assert.notNull(post, "게시글은 필수 입니다.");
  }

  private void validateUser(User user) {
    Assert.notNull(user, "사용자는 필수 입니다.");
  }

  private void validateIsAnonymous(Boolean isAnonymous) {
    Assert.notNull(isAnonymous, "익명 여부는 필수입니다.");
  }

  private void validateContent(String content) {
    Assert.hasText(content, "댓글은 필수입니다.");
    Assert.isTrue(content.length() > 0 && content.length() <= 300, "댓글은 300자 이내로 작성되어야 합니다");
  }
}
