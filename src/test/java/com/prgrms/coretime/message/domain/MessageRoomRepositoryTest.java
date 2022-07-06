package com.prgrms.coretime.message.domain;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.samePropertyValuesAs;

import com.prgrms.coretime.message.dto.MessageRoomsWithLastMessages;
import com.prgrms.coretime.post.domain.Board;
import com.prgrms.coretime.post.domain.BoardType;
import com.prgrms.coretime.post.domain.Post;
import com.prgrms.coretime.post.domain.repository.BoardRepository;
import com.prgrms.coretime.post.domain.repository.PostRepository;
import com.prgrms.coretime.school.domain.School;
import com.prgrms.coretime.school.domain.respository.SchoolRepository;
import com.prgrms.coretime.user.domain.LocalUser;
import com.prgrms.coretime.user.domain.User;
import com.prgrms.coretime.user.domain.repository.UserRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@DataJpaTest
class MessageRoomRepositoryTest {

  @Autowired
  private MessageRoomRepository messageRoomRepository;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private SchoolRepository schoolRepository;

  @Autowired
  private BoardRepository boardRepository;

  @Autowired
  private PostRepository postRepository;

  @Autowired
  private MessageRepository messageRepository;

  private School school = new School("schoolName", "school1@example.com");

  private User user1 = LocalUser.builder()
      .nickname("userOne")
      .profileImage("profileImage1")
      .email("example1@example.co.kr")
      .name("userOne")
      .school(school)
      .password("pw123$%^")
      .build();

  private User user2 = LocalUser.builder()
      .nickname("userTwo")
      .profileImage("profileImage2")
      .email("example2@example.co.kr")
      .name("userTwo")
      .school(school)
      .password("pw123$%^")
      .build();

  private User user3 = LocalUser.builder()
      .nickname("userThree")
      .profileImage("profileImage3")
      .email("example3@example.co.kr")
      .name("userThree")
      .school(school)
      .password("pw123$%^")
      .build();

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
    userRepository.save(user1);
    userRepository.save(user2);
    userRepository.save(user3);

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

  @Test
  @DisplayName("MessageRoomId로 해당하는 Message를 다건 조회(pagination)")
  void findMessagesByMessageRoomIdTest() {
    MessageRoom savedMessageRoom = messageRoomRepository.save(messageRoom);
    int messageSize = 50;
    Message[] messages = new Message[messageSize];
    for (int i = 0; i < messageSize; i++) {
      messages[i] = messageRepository.save(Message.builder()
          .messageRoom(messageRoom)
          .writer(user1)
          .content(new StringBuilder().append("content ").append(i).toString())
          .build()
      );
    }
    Pageable pageable = PageRequest.of(2, 20, Sort.by("createdAt").descending());

    Page<Message> messagePage = messageRoomRepository.findMessagesByMessageRoomId(
        savedMessageRoom.getId(), pageable);

    assertThat(messagePage.getNumberOfElements(), is(10));
    assertThat(messagePage.getTotalPages(), is(3));
    assertThat(messagePage.getContent().get(0), samePropertyValuesAs(messages[9]));
  }

  @Test
  @DisplayName("userId로 해당하는 MessageRoom과 각 MessageRoom의 마지막 쪽지 다건 조회(pagination)")
  void findMessageRoomsAndLastMessagesByUserIdTest() {
    MessageRoom messageRoom2 = MessageRoom.builder()
        .initialSender(user1)
        .initialReceiver(user3)
        .createdFrom(postCreatedAnonymousWriter)
        .isAnonymous(false)
        .build();
    MessageRoom savedMessageRoom1 = messageRoomRepository.save(messageRoom);
    MessageRoom savedMessageRoom2 = messageRoomRepository.save(messageRoom2);

    int messageSize = 20;
    Message[] messages1 = new Message[messageSize];
    for (int i = 0; i < messageSize; i++) {
      messages1[i] = messageRepository.save(Message.builder()
          .messageRoom(savedMessageRoom1)
          .writer(user1)
          .content(new StringBuilder().append("content ").append(i).toString())
          .build()
      );
    }
    Message[] messages2 = new Message[messageSize];
    for (int i = 0; i < messageSize; i++) {
      messages2[i] = messageRepository.save(Message.builder()
          .messageRoom(savedMessageRoom2)
          .writer(user3)
          .content(new StringBuilder().append("content ").append(i).toString())
          .build()
      );
    }

    Pageable pageable = PageRequest.of(0, 10, Sort.by("updated_at").descending());

    Page<MessageRoomsWithLastMessages> messagePage = messageRoomRepository.findMessageRoomsAndLastMessagesByUserId(
        user1.getId(), pageable);

    assertThat(messagePage.getNumberOfElements(), is(2));
    assertThat(messagePage.getContent().get(0).getMessageRoomId().intValue(), is(savedMessageRoom2.getId().intValue()));
  }
}