package com.prgrms.coretime.comment.domain.repository;

import static com.prgrms.coretime.comment.domain.QComment.comment;

import com.prgrms.coretime.comment.domain.Comment;
import com.prgrms.coretime.comment.dto.response.CommentOneResponse;
import com.prgrms.coretime.comment.dto.response.CommentsOnPostResponse;
import com.querydsl.core.types.dsl.Wildcard;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

public class CommentRepositoryImpl implements CommentRepositoryCustom {

  private final JPAQueryFactory queryFactory;

  public CommentRepositoryImpl(EntityManager entityManager) {
    this.queryFactory = new JPAQueryFactory(entityManager);
  }

  public Page<CommentsOnPostResponse> findByPost(Long postId, Pageable pageable) {

    // 부모댓글은 자식 댓글이 있을 경우 삭제되어도 표시를 해줘야한다.
    List<Comment> parentComments = queryFactory
        .select(comment)
        .from(comment)
        .where(
            comment.post.id.eq(postId)
                .and(comment.parent.isNull())
                .and(comment.children.size().goe(0))
        )
        .orderBy(comment.createdAt.asc())
        .fetch();

    // 자식 댓글은 삭제되면 메시지를 보여주지 않는다.
    List<Comment> childComments = queryFactory
        .select(comment)
        .from(comment)
        .where(
            comment.post.id.eq(postId),
            comment.parent.isNotNull(),
            comment.isDelete.isFalse()
        )
        .orderBy(comment.createdAt.asc())
        .fetch();

    List<CommentsOnPostResponse> parentResponse = parentComments.stream()
        .filter(c -> !(c.getChildren().size() == 0 && c.getIsDelete()))
        .map(CommentsOnPostResponse::of)
        .collect(Collectors.toList());

    List<CommentsOnPostResponse> childrenResponse = childComments.stream()
        .filter(c -> c != null)
        .map(CommentsOnPostResponse::of)
        .collect(Collectors.toList());

    parentResponse.stream()
        .forEach(parent -> {
          parent.setChildren(childrenResponse.stream()
              .filter(child -> child.getParentId().equals(parent.getCommentId()))
              .collect(Collectors.toList()));
        });

    //Count Query
    long total = queryFactory
        .select(Wildcard.count)
        .from(comment)
        .where(
            comment.post.id.eq(postId),
            comment.parent.isNull(),
            comment.children.size().goe(0)
                .or(comment.children.size().eq(0)
                    .and(comment.isDelete.isFalse()))
        )
        .fetch().get(0);

    return new PageImpl<>(parentResponse, pageable, total);
  }

  @Override
  public Optional<CommentOneResponse> findBestCommentByPost(Long postId) {
//
//    select
//    comment0_.comment_id as comment_1_1_,
//        comment0_.created_at as created_2_1_,
//    comment0_.updated_at as updated_3_1_,
//        comment0_.anonymous_seq as anonymou4_1_,
//    comment0_.content as content5_1_,
//        comment0_.is_anonymous as is_anony6_1_,
//    comment0_.is_deleted as is_delet7_1_,
//        comment0_.parent_id as parent_i8_1_,
//    comment0_.post_id as post_id9_1_,
//        comment0_.user_id as user_id10_1_
//    from
//    comment comment0_
//    where
//    comment0_.post_id=?
//    and (
//        select
//        count(likes1_.comment_id)
//        from
//        comment_like likes1_
//        where
//        comment0_.comment_id = likes1_.comment_id
//    )>=?
//        order by
//        (select
//            count(likes2_.comment_id)
//            from
//            comment_like likes2_
//            where
//            comment0_.comment_id = likes2_.comment_id) desc limit ?
    Comment bestComment = queryFactory.select(comment)
        .from(comment)
        .where(comment.post.id.eq(postId),
            comment.likes.size().goe(10),
            comment.isDelete.isFalse()
        )
        .orderBy(comment.likes.size().desc())
        .fetchFirst();

    // 베스트 댓글 없음
    if (bestComment == null) {
      return Optional.empty();
    }
//    select
//    likes0_.comment_id as comment_1_2_3_,
//        likes0_.user_id as user_id2_2_3_,
//    likes0_.comment_id as comment_1_2_2_,
//        likes0_.user_id as user_id2_2_2_,
//    likes0_.created_at as created_3_2_2_,
//        likes0_.updated_at as updated_4_2_2_,
//    user1_.user_id as user_id2_19_0_,
//        user1_.created_at as created_3_19_0_,
//    user1_.updated_at as updated_4_19_0_,
//        user1_.email as email5_19_0_,
//    user1_.name as name6_19_0_,
//        user1_.nickname as nickname7_19_0_,
//    user1_.profile_image as profile_8_19_0_,
//        user1_.school_id as school_i9_19_0_,
//    user1_1_.password as password1_8_0_,
//        user1_2_.provider as provider1_11_0_,
//    user1_2_.provider_id as provider2_11_0_,
//        user1_.dtype as dtype1_19_0_,
//    school2_.school_id as school_i1_16_1_,
//        school2_.created_at as created_2_16_1_,
//    school2_.updated_at as updated_3_16_1_,
//        school2_.email as email4_16_1_,
//    school2_.name as name5_16_1_
//        from
//    comment_like likes0_
//    inner join
//    users user1_
//    on likes0_.user_id=user1_.user_id
//    left outer join
//    local_user user1_1_
//    on user1_.user_id=user1_1_.user_id
//    left outer join
//    oauth_user user1_2_
//    on user1_.user_id=user1_2_.user_id
//    left outer join
//    school school2_
//    on user1_.school_id=school2_.school_id
//    where
//    likes0_.comment_id=?

    CommentOneResponse bestCommentResponse = new CommentOneResponse(
        bestComment.getUser().getId(),
        bestComment.getParent() == null ? null : bestComment.getParent().getId(),
        bestComment.getId(),
        bestComment.getLikes().size(),
        bestComment.getAnonymous() ? "익명" + bestComment.getAnonymousSeq()
            : bestComment.getUser().getNickname(),
        bestComment.getContent()
    );

    return Optional.of(bestCommentResponse);
  }

}
