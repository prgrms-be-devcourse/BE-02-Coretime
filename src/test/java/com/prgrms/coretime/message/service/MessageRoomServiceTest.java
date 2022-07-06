package com.prgrms.coretime.message.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.prgrms.coretime.message.domain.Message;
import com.prgrms.coretime.message.domain.MessageRepository;
import com.prgrms.coretime.message.domain.MessageRoom;
import com.prgrms.coretime.message.domain.MessageRoomRepository;
import com.prgrms.coretime.message.domain.VisibilityState;
import com.prgrms.coretime.message.dto.MessageRoomsWithLastMessages;
import com.prgrms.coretime.message.dto.request.MessageRoomCreateRequest;
import com.prgrms.coretime.message.dto.request.MessageRoomGetRequest;
import com.prgrms.coretime.post.domain.Board;
import com.prgrms.coretime.post.domain.Post;
import com.prgrms.coretime.post.domain.PostRepository;
import com.prgrms.coretime.user.domain.TestUser;
import com.prgrms.coretime.user.domain.TestUserRepository;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@ExtendWith(MockitoExtension.class)
class MessageRoomServiceTest {

  @InjectMocks
  private MessageRoomService messageRoomService;

  @Mock
  private MessageRoomRepository messageRoomRepository;

  @Mock
  private MessageRepository messageRepository;

  @Mock
  private TestUserRepository testUserRepository;

  @Mock
  private PostRepository postRepository;

  private TestUser user1 = mock(TestUser.class);
  private TestUser user2 = mock(TestUser.class);

  private Post post = mock(Post.class);

  private Board board = mock(Board.class);

  private MessageRoom messageRoom = mock(MessageRoom.class);

  private Message message = mock(Message.class);

  private Page<Message> messages = mock(Page.class);

  @Test
  @DisplayName("쪽지방 생성하기: 성공")
  void saveMessageRoomSuccessTest() {
    MessageRoomCreateRequest request = new MessageRoomCreateRequest(100L, 1L, true,
        "first message");

    doReturn(Optional.of(user1), Optional.of(user2)).when(testUserRepository).findById(anyLong());
    doReturn(Optional.of(post)).when(postRepository).findById(anyLong());
    doReturn(messageRoom).when(messageRoomRepository).save(any());
    doReturn(message).when(messageRepository).save(any());

    messageRoomService.saveMessageRoom(user1.getId(), request);

    verify(messageRoomRepository).save(any(MessageRoom.class));
  }

  @Test
  @DisplayName("쪽지방 id 조회하기: 성공")
  void getMessageRoomIdSuccessTest() {
    doReturn(Optional.of(user1)).doReturn(Optional.of(user2)).when(testUserRepository)
        .findById(anyLong());
    doReturn(Optional.of(post)).when(postRepository).findById(anyLong());

    messageRoomService.getMessageRoomId(user1.getId(), post.getId(), user2.getId(),
        post.getIsAnonymous());

    verify(messageRoomRepository).findIdByInfo(any(), any(), any(), any());
  }

  @Test
  @DisplayName("쪽지방 조회하기: 성공")
  void getMessageRoomSuccessTest() {
    MessageRoomGetRequest request = new MessageRoomGetRequest(1L);
    doReturn(Optional.of(user1)).when(testUserRepository).findById(anyLong());
    doReturn(Optional.of(messageRoom)).when(messageRoomRepository).findById(anyLong());
    when(messageRoom.getVisibilityTo()).thenReturn(VisibilityState.BOTH);
    when(user1.getId()).thenReturn(1L);
    when(messageRoom.getInitialSender()).thenReturn(user1);
    when(messageRoom.getInitialSender().getId()).thenReturn(1L);
    doReturn(messages).when(messageRoomRepository).findMessagesByMessageRoomId(any(), any());
    when(messageRoom.getCreatedFrom()).thenReturn(post);
    when(messageRoom.getCreatedFrom().getBoard()).thenReturn(board);
    when(messageRoom.getInitialReceiver()).thenReturn(user2);

    messageRoomService.getMessageRoom(1L, request);

    verify(messageRoomRepository).findById(anyLong());
    verify(messageRoomRepository).findMessagesByMessageRoomId(any(), any());
  }

  @Test
  @DisplayName("쪽지방 리스트 조회하기: 성공")
  void getMessageRoomsSuccessTest() {
    doReturn(Optional.of(user1)).when(testUserRepository).findById(anyLong());
    Page<MessageRoomsWithLastMessages> messageRooms = Page.empty();
    doReturn(messageRooms).when(messageRoomRepository)
        .findMessageRoomsAndLastMessagesByUserId(any(), any());

    Pageable pageable = PageRequest.of(0, 20, Sort.by("createdAt").descending());
    messageRoomService.getMessageRooms(1L, pageable);

    verify(messageRoomRepository).findMessageRoomsAndLastMessagesByUserId(anyLong(), any());
  }

  @Test
  @DisplayName("쪽지방 차단하기: 성공")
  void blockMessageRoomSuccessTest() {
    doReturn(Optional.of(user1)).when(testUserRepository).findById(anyLong());
    doReturn(Optional.of(messageRoom)).when(messageRoomRepository).findById(anyLong());
    when(messageRoom.getInitialReceiver()).thenReturn(user1);
    when(messageRoom.getInitialSender()).thenReturn(user2);

    messageRoomService.blockMessageRoom(user1.getId(), messageRoom.getId());

    verify(messageRoom).changeIsBlocked(any());
  }
}