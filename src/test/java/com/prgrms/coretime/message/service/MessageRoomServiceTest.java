package com.prgrms.coretime.message.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import com.prgrms.coretime.message.domain.Message;
import com.prgrms.coretime.message.domain.MessageRepository;
import com.prgrms.coretime.message.domain.MessageRoom;
import com.prgrms.coretime.message.domain.MessageRoomRepository;
import com.prgrms.coretime.message.dto.request.MessageRoomCreateRequest;
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

}