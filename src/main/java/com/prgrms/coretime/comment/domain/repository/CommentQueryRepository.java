package com.prgrms.coretime.comment.domain.repository;

import static com.prgrms.coretime.comment.domain.QComment.comment;
import static com.prgrms.coretime.comment.domain.QCommentLike.commentLike;

import com.prgrms.coretime.comment.dto.response.CommentsResponse;
import com.prgrms.coretime.comment.dto.response.QCommentsResponse;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
public class CommentQueryRepository {

  private final JPAQueryFactory queryFactory;

  public CommentQueryRepository(EntityManager entityManager) {
    this.queryFactory = new JPAQueryFactory(entityManager);
  }

  public Page<CommentsResponse> findSearchComments(Long postId, Pageable pageable) {

    // 부모 댓글
    List<CommentsResponse> response = queryFactory
        .select(new QCommentsResponse(
            comment.user.id,
            comment.parent.id,
            comment.id,
            commentLike.count(),
            comment.anonymousSeq,
            comment.content
        ))
        .from(comment)
        .leftJoin(comment, commentLike.comment)
        .where(comment.post.id.eq(postId),
            comment.parent.isNull())
        .orderBy(comment.createdAt.asc())
        .offset(pageable.getOffset())
        .limit(pageable.getPageSize())
        .fetch();

    long total = response.size();

    List<CommentsResponse> childrenResponse = queryFactory
        .select(new QCommentsResponse(
            comment.user.id,
            comment.parent.id,
            comment.id,
            commentLike.count(),
            comment.anonymousSeq,
            comment.content
        ))
        .from(comment)
        .leftJoin(comment, commentLike.comment)
        .where(comment.post.id.eq(postId)
            .and(comment.parent.isNotNull()))
        .orderBy(comment.createdAt.asc())
        .offset(pageable.getOffset())
        .limit(pageable.getPageSize())
        .fetch();

    response
        .forEach(parent -> {
          parent.setChildren(childrenResponse.stream()
              .filter(child -> child.getParentId().equals(parent.getCommentId()))
              .collect(Collectors.toList()));
        });

    return new PageImpl<>(response, pageable, total);
  }
}
