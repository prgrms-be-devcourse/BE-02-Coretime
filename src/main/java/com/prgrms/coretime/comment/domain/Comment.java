package com.prgrms.coretime.comment.domain;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Comment {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "parent_id")
  private Comment parent;

  @OneToMany(mappedBy = "parent", orphanRemoval = false)
  private List<Comment> children = new ArrayList<>();

  // temp column : user
  @Column(name = "user_id")
  private Long userId;

  // temp column : post
  @Column(name = "post_id")
  private Long postId;

  @Column(name = "anonymous_seq")
  private Integer anonymousSeq;

  @Column(name = "content", nullable = false, length = 300)
  private String content;

  @Column(name = "is_deleted", nullable = false)
  private Boolean isDelete;

  //TODO: 생성자 처리 어떻게 할 건지, RequestDto 말고 여기서 Dto 받아서 생성할 건지 고민

  /**
   * TODO : 대댓글 연관관계 설정 1:N 양방향
   * */

  /**
   * TODO : USER 1:N 일단 단방향
   * */

  /**
   * TODO : POST 1:N 단방향
   * */

  /***
   * TODO : validation
   */
}
