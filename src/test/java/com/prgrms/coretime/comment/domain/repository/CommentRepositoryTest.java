package com.prgrms.coretime.comment.domain.repository;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import com.prgrms.coretime.TestConfig;
import com.prgrms.coretime.comment.domain.Comment;
import com.prgrms.coretime.comment.domain.CommentLike;
import com.prgrms.coretime.comment.dto.response.CommentOneResponse;
import com.prgrms.coretime.comment.dto.response.CommentsOnPostResponse;
import com.prgrms.coretime.common.ErrorCode;
import com.prgrms.coretime.common.error.exception.NotFoundException;
import com.prgrms.coretime.post.domain.Board;
import com.prgrms.coretime.post.domain.BoardType;
import com.prgrms.coretime.post.domain.Post;
import com.prgrms.coretime.post.domain.repository.BoardRepository;
import com.prgrms.coretime.post.domain.repository.PostRepository;
import com.prgrms.coretime.school.domain.School;
import com.prgrms.coretime.school.domain.respository.SchoolRepository;
import com.prgrms.coretime.user.domain.LocalUser;
import com.prgrms.coretime.user.domain.OAuthUser;
import com.prgrms.coretime.user.domain.User;
import com.prgrms.coretime.user.domain.repository.UserRepository;
import java.nio.channels.IllegalChannelGroupException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
@Import(TestConfig.class)
@TestInstance(Lifecycle.PER_CLASS)
class CommentRepositoryTest {

  @PersistenceContext
  EntityManager em;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private PostRepository postRepository;

  @Autowired
  private BoardRepository boardRepository;

  @Autowired
  private CommentRepository commentRepository;

  @Autowired
  private CommentLikeRepository commentLikeRepository;

  @Autowired
  private SchoolRepository schoolRepository;

  private String localTestEmail = "local@university.ac.kr";
  private String oauthTestEmail = "oauth@ajou.ac.kr";

  private User localUser;

  private User oauthUser;

  private Board board;

  private Post anonyPost;

  private Post realPost;

  private Comment parent;

  private School school;

  void setSchool() {
    school = new School("university", "university@university.ac.kr");
    school = schoolRepository.save(school);
  }

  void setUser() {
    localUser = LocalUser.builder()
        .nickname("local??????")
        .profileImage("?????? ??????")
        .email(localTestEmail)
        .name("???????????????")
        .school(school)
        .password("test1234!")
        .build();

    oauthUser = OAuthUser.builder()
        .nickname("oauth??????")
        .profileImage("?????? ??????")
        .email(oauthTestEmail)
        .name("?????????oauth")
        .school(school)
        .provider("?????????")
        .providerId("?????????id")
        .build();

    localUser = userRepository.save(localUser);
    oauthUser = userRepository.save(oauthUser);
  }

  void setBoard() {
    board = Board.builder()
        .category(BoardType.BASIC)
        .name("?????????")
        .school(school)
        .build();
    board = boardRepository.save(board);
  }

  void setPost() {
    anonyPost = Post.builder()
        .title("??? ????????? ??????????????? ????????? ?????????")
        .content("?????????")
        .isAnonymous(true)
        .user(localUser)
        .board(board)
        .build();
    anonyPost = postRepository.save(anonyPost);

    realPost = Post.builder()
        .title("??? ????????? ??????????????? ????????? ?????????")
        .content("?????????")
        .isAnonymous(false)
        .user(localUser)
        .board(board)
        .build();
    realPost = postRepository.save(realPost);
  }

  void setComment() {
    parent = Comment.builder()
        .user(localUser)
        .post(anonyPost)
        .parent(null)
        .isAnonymous(true)
        .anonymousSeq(anonyPost.getAnonymousSeqAndAdd())
        .content("??? ?????? ????????? ????????? ??? ??????~")
        .build();

    parent = commentRepository.save(parent);
  }

  @BeforeAll
  void setup() {
    setSchool();
    setUser();
    setBoard();
    setPost();
    setComment();
  }

  @AfterAll
  void teardown() {
    commentRepository.deleteAll();
    postRepository.deleteAll();
    boardRepository.deleteAll();
    userRepository.deleteAll();
    schoolRepository.deleteAll();
  }

  @Test
  @DisplayName("?????? ????????? ?????? ????????? ??????????????? ?????? ?????? ?????????")
  public void testParentChild() {
    Comment child = Comment.builder()
        .user(localUser)
        .post(anonyPost)
        .parent(parent)
        .isAnonymous(true)
        .content("?????? ????????????")
        .build();

    Comment savedChild = commentRepository.save(child);

    em.flush();
    em.clear();

    Comment calledChild = commentRepository.findById(savedChild.getId())
        .orElseThrow(IllegalArgumentException::new);
    Comment calledParent = commentRepository.findById(parent.getId())
        .orElseThrow(IllegalArgumentException::new);

    assertThat(calledChild.getParent()).isEqualTo(calledParent);
  }

  @Test
  @DisplayName("Post??? ?????? ????????? ?????????????????? ????????????")
  public void testCommentOfPost() {
    Comment realComment = Comment.builder()
        .user(localUser)
        .post(anonyPost)
        .parent(null)
        .isAnonymous(false)
        .content("?????? ????????????")
        .build();

    Comment child = Comment.builder()
        .user(localUser)
        .post(anonyPost)
        .parent(parent)
        .isAnonymous(true)
        .content("?????? ????????????")
        .build();

    Comment savedRealParent = commentRepository.save(realComment);
    Comment savedChild = commentRepository.save(child);

    em.flush();
    em.clear();

    Post masterPost = postRepository.findById(anonyPost.getId())
        .orElseThrow(IllegalArgumentException::new);

    assertThat(masterPost.getComments().size()).isEqualTo(3);
  }

  @Test
  @DisplayName("update Delete ????????? ?????? ?????????")
  public void testDelete() {
    Comment calledParent = commentRepository.findById(parent.getId())
        .orElseThrow(IllegalChannelGroupException::new);
    calledParent.updateDelete();

    em.flush();
    em.clear();

    Comment updatedComment = commentRepository.findById(parent.getId())
        .orElseThrow(IllegalArgumentException::new);

    assertThat(updatedComment.getIsDelete()).isTrue();
  }


  @Test
  @DisplayName("best ????????? ????????? ??? ?????? ???????????????")
  public void testFindBest() {
    //given
    List<Long> userIds = new ArrayList<>();

    for (int i = 0; i < 10; i++) {
      LocalUser savedUser = userRepository.save(LocalUser.builder()
          .nickname("local??????" + i)
          .profileImage("?????? ??????" + i)
          .email(localTestEmail + i)
          .name("???????????????")
          .school(school)
          .password("test1234!")
          .build());
      userIds.add(savedUser.getId());
    }

    em.flush();
    em.clear();

    for (int i = 0; i < 10; i++) {
      User user = userRepository.findById(userIds.get(i)).get();
      commentLikeRepository.save(new CommentLike(user, parent));
    }

    commentRepository.save(Comment.builder()
        .user(localUser)
        .post(anonyPost)
        .parent(parent)
        .anonymousSeq(null)
        .isAnonymous(false)
        .content("?????? ????????????")
        .build());

    em.flush();
    em.clear();

    //when
    Optional<CommentOneResponse> bestComment = commentRepository.findBestCommentByPost(
        anonyPost.getId());

    //then
    assertThat(bestComment).isNotEmpty();
    CommentOneResponse bestCommentResponse = bestComment.get();
    assertThat(bestCommentResponse.getCommentId()).isEqualTo(parent.getId());
  }

  @Test
  @DisplayName("best ????????? ???????????? 10??? ?????? ??? null??? ???????????????")
  public void testBestNull() {
    //given
    List<Long> userIds = new ArrayList<>();

    for (int i = 0; i < 9; i++) {
      LocalUser savedUser = userRepository.save(LocalUser.builder()
          .nickname("local??????" + i)
          .profileImage("?????? ??????" + i)
          .email(localTestEmail + i)
          .name("???????????????")
          .school(school)
          .password("test1234!")
          .build());
      userIds.add(savedUser.getId());
    }

    em.flush();
    em.clear();

    for (int i = 0; i < 9; i++) {
      User user = userRepository.findById(userIds.get(i)).get();
      commentLikeRepository.save(new CommentLike(user, parent));
    }

    commentRepository.save(Comment.builder()
        .user(localUser)
        .post(anonyPost)
        .parent(parent)
        .isAnonymous(true)
        .content("?????? ????????????")
        .build());

    em.flush();
    em.clear();

    //when
    Optional<CommentOneResponse> bestComment = commentRepository.findBestCommentByPost(
        anonyPost.getId());

    //then
    assertThat(bestComment).isEmpty();
  }

  @Test
  @DisplayName("??? ???????????? ???????????? 10??? ????????? ????????? ??? ??? ????????? ??? ?????? ?????? ??? ???????????? ???")
  public void testCorrectBest() {
    //given
    List<Long> userIds = new ArrayList<>();
    List<Long> extraIds = new ArrayList<>();

    Comment child = Comment.builder()
        .user(localUser)
        .post(anonyPost)
        .parent(parent)
        .anonymousSeq(null)
        .isAnonymous(false)
        .content("?????? ????????????")
        .build();

    commentRepository.save(child);

    for (int i = 0; i < 10; i++) {
      LocalUser savedUser = userRepository.save(LocalUser.builder()
          .nickname("local??????" + i)
          .profileImage("?????? ??????" + i)
          .email(localTestEmail + i)
          .name("???????????????")
          .school(school)
          .password("test1234!")
          .build());
      userIds.add(savedUser.getId());
    }

    for (int i = 10; i < 20; i++) {
      LocalUser savedUser = userRepository.save(LocalUser.builder()
          .nickname("local??????" + i)
          .profileImage("?????? ??????" + i)
          .email(localTestEmail + i)
          .name("???????????????")
          .school(school)
          .password("test1234!")
          .build());
      extraIds.add(savedUser.getId());
    }

    em.flush();
    em.clear();

    for (int i = 0; i < 10; i++) {
      User user = userRepository.findById(userIds.get(i)).get();
      commentLikeRepository.save(new CommentLike(user, parent));
      commentLikeRepository.save(new CommentLike(user, child));
    }

    for (int i = 0; i < 10; i++) {
      User user = userRepository.findById(extraIds.get(i)).get();
      commentLikeRepository.save(new CommentLike(user, child));
    }

    em.flush();
    em.clear();

    //when
    Optional<CommentOneResponse> bestComment = commentRepository.findBestCommentByPost(
        anonyPost.getId());

    //then
    assertThat(bestComment).isNotEmpty();
    CommentOneResponse bestCommentResponse = bestComment.get();
    assertThat(bestCommentResponse.getLike()).isEqualTo(20);
    assertThat(bestCommentResponse.getName()).isEqualTo(child.getUser().getNickname());
    assertThat(bestCommentResponse.getCommentId()).isEqualTo(child.getId());
  }

  @Test
  @DisplayName("1. ????????? ????????? ?????????")
  public void testCommentEmpty() {
    commentRepository.deleteById(parent.getId());

    em.flush();
    em.clear();

    Page<CommentsOnPostResponse> commentsResponse = commentRepository.findByPost(anonyPost.getId(),
        PageRequest.of(0, 20));

    assertThat(commentsResponse.getTotalElements()).isEqualTo(0);
    List<CommentsOnPostResponse> content = commentsResponse.getContent();
    content.forEach(c -> assertThat(c).isNull());
  }

  @Test
  @DisplayName("2. ?????? ????????? ?????? ???")
  public void testChildEmpty() {
    commentRepository.save(Comment.builder()
        .user(localUser)
        .post(anonyPost)
        .parent(null)
        .isAnonymous(true)
        .anonymousSeq(anonyPost.getNextAnonymousSeq())
        .content("????????? ????????????")
        .build());

    em.flush();
    em.clear();

    Page<CommentsOnPostResponse> commentsResponse = commentRepository.findByPost(anonyPost.getId(),
        PageRequest.of(0, 20));

    assertThat(commentsResponse.getTotalElements()).isEqualTo(2);
    List<CommentsOnPostResponse> content = commentsResponse.getContent();
    content.forEach(c -> assertThat(c.getChildrenSize()).isEqualTo(0));
  }

  @Test
  @DisplayName("3. ?????? ????????? ???????????? ???????????? ?????????.")
  public void testChildDelete() {
    Comment child = commentRepository.save(Comment.builder()
        .user(localUser)
        .post(anonyPost)
        .parent(parent)
        .isAnonymous(true)
        .anonymousSeq(anonyPost.getNextAnonymousSeq())
        .content("????????? ????????????")
        .build());

    em.flush();
    em.clear();

    Page<CommentsOnPostResponse> commentsResponse = commentRepository.findByPost(anonyPost.getId(),
        PageRequest.of(0, 20));

    assertThat(commentsResponse.getTotalElements()).isEqualTo(1);
    List<CommentsOnPostResponse> content = commentsResponse.getContent();
    assertThat(content.get(0).getChildrenSize()).isEqualTo(1);

    // ????????? ??????
    child = commentRepository.findById(child.getId())
        .orElseThrow(() -> new NotFoundException(ErrorCode.COMMENT_NOT_FOUND));
    child.updateDelete();

    em.flush();
    em.clear();

    commentsResponse = commentRepository.findByPost(anonyPost.getId(),
        PageRequest.of(0, 20));

    assertThat(commentsResponse.getTotalElements()).isEqualTo(1);
    content = commentsResponse.getContent();
    assertThat(content.get(0).getChildrenSize()).isEqualTo(0);

  }

  @Test
  @DisplayName("4. ?????? ????????? ????????? ?????? ????????? ???????????? ???????????? ?????????.")
  public void testParentDelete() {
    Comment comment = commentRepository.findById(parent.getId())
        .orElseThrow(() -> new NotFoundException(ErrorCode.COMMENT_NOT_FOUND));

    comment.updateDelete();
    em.flush();
    em.clear();

    Page<CommentsOnPostResponse> commentsResponse = commentRepository.findByPost(anonyPost.getId(),
        PageRequest.of(0, 20));
    List<CommentsOnPostResponse> content = commentsResponse.getContent();
    assertThat(content.size()).isEqualTo(0);

  }

  @Test
  @DisplayName("5. ????????? ???????????? ?????? ???????????? ?????? ??? ????????? '??????(?????????)'??????")
  public void testPostUserSameCommentUser() {
    Page<CommentsOnPostResponse> commentsResponse = commentRepository.findByPost(anonyPost.getId(),
        PageRequest.of(0, 20));
    List<CommentsOnPostResponse> content = commentsResponse.getContent();
    assertThat(content.get(0).getName()).isEqualTo("??????(?????????)");
  }

  @Test
  @DisplayName("7. ????????? ???????????? ?????? ?????????(??????)??? ????????? ?????? ??????????????? '(?????? + seq)'?????? ??????.")
  public void testRealPostComment() {
    commentRepository.save(Comment.builder()
        .user(localUser)
        .post(realPost)
        .parent(null)
        .anonymousSeq(realPost.getAnonymousSeqAndAdd())
        .isAnonymous(true)
        .content("?????? ???????????? ????????? ?????? + num?????? ?????????!")
        .build());

    em.flush();
    em.clear();

    Page<CommentsOnPostResponse> commentsResponse = commentRepository.findByPost(realPost.getId(),
        PageRequest.of(0, 20));
    List<CommentsOnPostResponse> content = commentsResponse.getContent();
    assertThat(content.get(0).getName()).isEqualTo("??????" + (realPost.getNextAnonymousSeq() - 1));
    assertThat(content.get(0).getName()).isNotEqualTo("??????(?????????)");
  }


}