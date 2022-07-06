package com.prgrms.coretime.message.domain;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.samePropertyValuesAs;

import com.prgrms.coretime.post.domain.Board;
import com.prgrms.coretime.post.domain.BoardRepository;
import com.prgrms.coretime.post.domain.BoardType;
import com.prgrms.coretime.post.domain.Post;
import com.prgrms.coretime.post.domain.PostRepository;
import com.prgrms.coretime.school.domain.School;
import com.prgrms.coretime.school.domain.respository.SchoolRepository;
import com.prgrms.coretime.user.domain.TestUser;
import com.prgrms.coretime.user.domain.TestUserRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class MessageRoomRepositoryTest {

  @Autowired
  private MessageRoomRepository messageRoomRepository;

  @Autowired
  private TestUserRepository testUserRepository;

  @Autowired
  private SchoolRepository schoolRepository;

  @Autowired
  private BoardRepository boardRepository;

  @Autowired
  private PostRepository postRepository;

  @Autowired
  private MessageRepository messageRepository;

  private TestUser user1 = new TestUser("1111");
  private TestUser user2 = new TestUser("2222");
  private TestUser user3 = new TestUser("3333");

  private School school = new School("school1", "school1@example.com");

  private Board board = Board.builder()
      .name("testBoard")
      .category(BoardType.CAREER)
      .school(school)
      .build();

  private Post postCreatedAnonymousWriter = Post.builder()
      .board(board)
      .user(user1)
      .title("post title created by anonymous writer (user1)")
      .content("post contents created by anonymous writer (user1)")
      .isAnonymous(true)
      .build();

  private Post postCreatedNotAnonymousWriter = Post.builder()
      .board(board)
      .user(user1)
      .title("post title 2")
      .content("post contents 2")
      .isAnonymous(false)
      .build();

  private MessageRoom messageRoom = MessageRoom.builder()
      .initialSender(user1)
      .initialReceiver(user2)
      .createdFrom(postCreatedAnonymousWriter)
      .isAnonymous(true)
      .build();

  @BeforeEach
  void setUp() {
    testUserRepository.save(user1);
    testUserRepository.save(user2);
    testUserRepository.save(user3);

    schoolRepository.save(school);

    boardRepository.save(board);

    postRepository.save(postCreatedAnonymousWriter);
    postRepository.save(postCreatedNotAnonymousWriter);
  }

  @Test
  @DisplayName("MessageRoom 저장")
  void saveMessageRoomTest() {
    MessageRoom savedMessageRoom = messageRoomRepository.save(messageRoom);

    Optional<MessageRoom> maybeMessageRoom = messageRoomRepository.findById(
        savedMessageRoom.getId());
    assertThat(maybeMessageRoom.isPresent(), is(true));
    assertThat(maybeMessageRoom.get(), samePropertyValuesAs(savedMessageRoom));
  }

  @Test
  @DisplayName("정보(익명 여부, 게시물 id, 대화 참여자)로 MessageRoom 조회")
  void findMessageRoomByInfoTest() {
    MessageRoom savedMessageRoom = messageRoomRepository.save(messageRoom);

    Optional<MessageRoom> maybeMessageRoom1 = messageRoomRepository.findMessageRoomByInfo(
        postCreatedAnonymousWriter.getId(), postCreatedAnonymousWriter.getIsAnonymous(),
        user1.getId(), user2.getId());
    Optional<MessageRoom> maybeMessageRoom2 = messageRoomRepository.findMessageRoomByInfo(
        postCreatedAnonymousWriter.getId(), postCreatedAnonymousWriter.getIsAnonymous(),
        user1.getId(), user3.getId());

    assertThat(maybeMessageRoom1.isPresent(), is(true));
    assertThat(maybeMessageRoom1.get(), samePropertyValuesAs(savedMessageRoom));
    assertThat(maybeMessageRoom2.isPresent(), is(false));
  }

  @Test
  @DisplayName("정보(익명 여부, 게시물 id, 대화 참여자)로 MessageRoomId 조회")
  void findIdByInfoTest() {
    MessageRoom savedMessageRoom = messageRoomRepository.save(messageRoom);

    Optional<Long> maybeMessageRoom1 = messageRoomRepository.findIdByInfo(
        postCreatedAnonymousWriter.getId(), postCreatedAnonymousWriter.getIsAnonymous(),
        user1.getId(), user2.getId());
    Optional<Long> maybeMessageRoom2 = messageRoomRepository.findIdByInfo(
        postCreatedAnonymousWriter.getId(), postCreatedAnonymousWriter.getIsAnonymous(),
        user1.getId(), user3.getId());

    assertThat(maybeMessageRoom1.isPresent(), is(true));
    assertThat(maybeMessageRoom1.get(), samePropertyValuesAs(savedMessageRoom.getId()));
    assertThat(maybeMessageRoom2.isPresent(), is(false));
  }

  @Test
  @DisplayName("정보(익명 여부, 게시물 id, 대화 참여자)로 MessageRoom 존재 여부 조회")
  void existsByInfoTest() {
    messageRoomRepository.save(messageRoom);

    boolean result1 = messageRoomRepository.existsByInfo(
        postCreatedAnonymousWriter.getId(), postCreatedAnonymousWriter.getIsAnonymous(),
        user1.getId(), user2.getId());
    boolean result2 = messageRoomRepository.existsByInfo(
        postCreatedAnonymousWriter.getId(), postCreatedAnonymousWriter.getIsAnonymous(),
        user1.getId(), user3.getId());

    assertThat(result1, is(true));
    assertThat(result2, is(false));
  }

}