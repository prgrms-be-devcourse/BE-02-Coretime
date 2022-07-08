package com.prgrms.coretime.message.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.prgrms.coretime.common.ErrorCode;
import com.prgrms.coretime.common.error.exception.InvalidRequestException;
import com.prgrms.coretime.common.error.exception.NotFoundException;
import com.prgrms.coretime.common.error.exception.PermissionDeniedException;
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
import com.prgrms.coretime.post.domain.repository.PostRepository;
import com.prgrms.coretime.user.domain.User;
import com.prgrms.coretime.user.domain.repository.UserRepository;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
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
  private UserRepository userRepository;

  @Mock
  private PostRepository postRepository;

  private User user1 = mock(User.class);
  private User user2 = mock(User.class);

  private Post post = mock(Post.class);

  private Board board = mock(Board.class);

  private MessageRoom messageRoom = mock(MessageRoom.class);

  private Message message = mock(Message.class);

  private Page<Message> messages = mock(Page.class);

  @Nested
  @DisplayName("쪽지방 생성하기")
  class SaveMessageRoomTest {

    MessageRoomCreateRequest request = new MessageRoomCreateRequest(100L, 1L, true,
        "first message");

    @Test
    @DisplayName("성공")
    void success() {
      doReturn(Optional.of(user1), Optional.of(user2)).when(userRepository).findById(anyLong());
      doReturn(Optional.of(post)).when(postRepository).findById(anyLong());
      doReturn(messageRoom).when(messageRoomRepository).save(any());
      doReturn(message).when(messageRepository).save(any());

      messageRoomService.saveMessageRoom(user1.getId(), request);

      verify(messageRoomRepository).save(any(MessageRoom.class));
    }

    @Nested
    @DisplayName("실패")
    class Fail {

      @Test
      @DisplayName("현재 유저와 쪽지방 대화 상대가 동일한 경우 InvalidRequestException 발생")
      void InvalidRequestException() {
        when(user1.getId() == request.getReceiverId())
            .thenThrow(new InvalidRequestException(ErrorCode.INVALID_MESSAGE_TARGET));

        assertThrows(InvalidRequestException.class, () -> {
          messageRoomService.saveMessageRoom(user1.getId(), request);
        });
      }

      @Test
      @DisplayName("user가 존재하지 않은 경우 NotFoundException 발생")
      void invokeUserNotFoundException() {
        doThrow(new NotFoundException(ErrorCode.USER_NOT_FOUND))
            .when(userRepository).findById(any());

        assertThrows(NotFoundException.class, () -> {
          messageRoomService.saveMessageRoom(user1.getId(), request);
        });
      }

      @Test
      @DisplayName("post가 존재하지 않은 경우 NotFoundException 발생")
      void invokePostNotFoundException() {
        doReturn(Optional.of(user1)).when(userRepository).findById(any());
        doThrow(new NotFoundException(ErrorCode.POST_NOT_FOUND))
            .when(postRepository).findById(any());

        assertThrows(NotFoundException.class, () -> {
          messageRoomService.saveMessageRoom(user1.getId(), request);
        });
      }

    }

  }

  @Nested
  @DisplayName("쪽지방 id 조회하기")
  class GetMessageRoomIdTest {

    @Test
    @DisplayName("성공")
    void success() {
      doReturn(Optional.of(user1)).doReturn(Optional.of(user2)).when(userRepository)
          .findById(anyLong());
      doReturn(Optional.of(post)).when(postRepository).findById(anyLong());

      messageRoomService.getMessageRoomId(user1.getId(), post.getId(), user2.getId(),
          post.getIsAnonymous());

      verify(messageRoomRepository).findIdByInfo(any(), any(), any(), any());
    }

    @Nested
    @DisplayName("실패")
    class Fail {

      @Test
      @DisplayName("user가 존재하지 않은 경우 NotFoundException 발생")
      void invokeUserNotFoundException() {
        doThrow(new NotFoundException(ErrorCode.USER_NOT_FOUND))
            .when(userRepository).findById(any());

        assertThrows(NotFoundException.class, () -> {
          messageRoomService.getMessageRoomId(user1.getId(), post.getId(), user2.getId(),
              post.getIsAnonymous());
        });
      }

      @Test
      @DisplayName("post가 존재하지 않은 경우 NotFoundException 발생")
      void invokePostNotFoundException() {
        doReturn(Optional.of(user1)).when(userRepository).findById(any());
        doThrow(new NotFoundException(ErrorCode.POST_NOT_FOUND))
            .when(postRepository).findById(any());

        assertThrows(NotFoundException.class, () -> {
          messageRoomService.getMessageRoomId(user1.getId(), post.getId(), user2.getId(),
              post.getIsAnonymous());
        });
      }

    }

  }

  @Nested
  @DisplayName("쪽지방 조회하기")
  class GetMessageRoomTest {

    MessageRoomGetRequest request = new MessageRoomGetRequest(1L);

    @Test
    @DisplayName("성공")
    void success() {
      doReturn(Optional.of(user1)).when(userRepository).findById(anyLong());
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

    @Nested
    @DisplayName("실패")
    class Fail {

      @Test
      @DisplayName("user가 존재하지 않은 경우 NotFoundException 발생")
      void invokeUserNotFoundException() {
        doThrow(new NotFoundException(ErrorCode.USER_NOT_FOUND))
            .when(userRepository).findById(any());

        assertThrows(NotFoundException.class, () -> {
          messageRoomService.getMessageRoom(1L, request);
        });
      }

      @Test
      @DisplayName("존재하지 않는 쪽지방을 조회하려고 하는 경우 NotFoundException 발생")
      void invokeMessageRoomNotFoundException() {
        doReturn(Optional.of(user1)).when(userRepository).findById(any());
        doThrow(new NotFoundException(ErrorCode.MESSAGE_ROOM_NOT_FOUND))
            .when(messageRoomRepository).findById(any());

        assertThrows(NotFoundException.class, () -> {
          messageRoomService.getMessageRoom(1L, request);
        });
      }

      @Test
      @DisplayName("삭제한 쪽지방을 조회하려고 하는 경우 PermissionDeniedException 발생")
      void invokePermissionDeniedException() {
        doReturn(Optional.of(user1)).when(userRepository).findById(any());
        doReturn(Optional.of(messageRoom)).when(messageRoomRepository).findById(any());
        when(messageRoom.getInitialReceiver()).thenReturn(user1);
        when(messageRoom.getInitialSender()).thenReturn(user2);
        when(messageRoom.getVisibilityTo()).thenReturn(VisibilityState.NO_ONE);

        Object visibility = mock(Object.class);
        when(visibility.equals(VisibilityState.NO_ONE) ||
            (messageRoom.getInitialSender().getId() == user1.getId() &&
                visibility.equals(VisibilityState.ONLY_INITIAL_RECEIVER)) ||
            (messageRoom.getInitialReceiver().getId() == user1.getId() &&
                visibility.equals(VisibilityState.ONLY_INITIAL_SENDER)))
            .thenThrow(new PermissionDeniedException(ErrorCode.NO_PERMISSION_TO_READ_DATA));

        assertThrows(PermissionDeniedException.class, () -> {
          messageRoomService.getMessageRoom(1L, request);
        });
      }

    }

  }

  @Nested
  @DisplayName("쪽지방 리스트 조회하기")
  class GetMessageRoomsTest {

    Pageable pageable = PageRequest.of(0, 20, Sort.by("createdAt").descending());

    @Test
    @DisplayName("성공")
    void success() {
      doReturn(Optional.of(user1)).when(userRepository).findById(anyLong());
      Page<MessageRoomsWithLastMessages> messageRooms = Page.empty();
      doReturn(messageRooms).when(messageRoomRepository)
          .findMessageRoomsAndLastMessagesByUserId(any(), any());

      messageRoomService.getMessageRooms(1L, pageable);

      verify(messageRoomRepository).findMessageRoomsAndLastMessagesByUserId(anyLong(), any());
    }

    @Nested
    @DisplayName("실패")
    class Fail {

      @Test
      @DisplayName("user가 존재하지 않은 경우 NotFoundException 발생")
      void invokeUserNotFoundException() {
        doThrow(new NotFoundException(ErrorCode.USER_NOT_FOUND))
            .when(userRepository).findById(any());

        assertThrows(NotFoundException.class, () -> {
          messageRoomService.getMessageRooms(1L, pageable);
        });
      }
    }

  }

  @Nested
  @DisplayName("쪽지방 삭제하기")
  class DeleteMessageRoomTest {


    @Test
    @DisplayName("쪽지방 삭제하기: 성공")
    void deleteMessageRoomSuccessTest() {
      doReturn(Optional.of(user1)).when(userRepository).findById(anyLong());
      doReturn(Optional.of(messageRoom)).when(messageRoomRepository).findById(anyLong());
      when(messageRoom.getInitialReceiver()).thenReturn(user1);
      when(messageRoom.getInitialSender()).thenReturn(user2);

      messageRoomService.deleteMessageRoom(user1.getId(), messageRoom.getId());

      verify(messageRoom).changeVisibilityTo(any());
    }

    @Nested
    @DisplayName("실패")
    class Fail {

      @Test
      @DisplayName("user가 존재하지 않은 경우 NotFoundException 발생")
      void invokeUserNotFoundException() {
        doThrow(new NotFoundException(ErrorCode.USER_NOT_FOUND))
            .when(userRepository).findById(any());

        assertThrows(NotFoundException.class, () -> {
          messageRoomService.deleteMessageRoom(user1.getId(), messageRoom.getId());
        });
      }

      @Test
      @DisplayName("존재하지 않는 쪽지방을 삭제하려고 하는 경우 NotFoundException 발생")
      void invokeMessageRoomNotFoundException() {
        doReturn(Optional.of(user1)).when(userRepository).findById(any());
        doThrow(new NotFoundException(ErrorCode.MESSAGE_ROOM_NOT_FOUND))
            .when(messageRoomRepository).findById(any());

        assertThrows(NotFoundException.class, () -> {
          messageRoomService.deleteMessageRoom(user1.getId(), messageRoom.getId());
        });
      }

      @Test
      @DisplayName("삭제하려는 쪽지방의 대화 참여자가 아닌 유저가 쪽지방 삭제를 하려는 경우 PermissionDeniedException 발생")
      void invokePermissionDeniedException() {
        when(messageRoom.getInitialReceiver()).thenReturn(user1);
        when(messageRoom.getInitialSender()).thenReturn(user2);
        when(!(messageRoom.getInitialSender().getId() == user1.getId()) &&
            !(messageRoom.getInitialReceiver().getId() == user1.getId()))
            .thenThrow(new PermissionDeniedException(ErrorCode.NO_PERMISSION_TO_SEND_MESSAGE));

        assertThrows(PermissionDeniedException.class, () -> {
          messageRoomService.deleteMessageRoom(user1.getId(), messageRoom.getId());
        });
      }

    }

  }

  @Nested
  @DisplayName("쪽지방 차단하기")
  class BlockMessageRoomTest {

    @Test
    @DisplayName("성공")
    void success() {
      doReturn(Optional.of(user1)).when(userRepository).findById(anyLong());
      doReturn(Optional.of(messageRoom)).when(messageRoomRepository).findById(anyLong());
      when(messageRoom.getInitialReceiver()).thenReturn(user1);
      when(messageRoom.getInitialSender()).thenReturn(user2);

      messageRoomService.blockMessageRoom(user1.getId(), messageRoom.getId());

      verify(messageRoom).changeIsBlocked(any());
    }

    @Nested
    @DisplayName("실패")
    class Fail {

      @Test
      @DisplayName("user가 존재하지 않은 경우 NotFoundException 발생")
      void invokeUserNotFoundException() {
        doThrow(new NotFoundException(ErrorCode.USER_NOT_FOUND))
            .when(userRepository).findById(any());

        assertThrows(NotFoundException.class, () -> {
          messageRoomService.blockMessageRoom(user1.getId(), messageRoom.getId());
        });
      }

      @Test
      @DisplayName("존재하지 않는 쪽지방을 차단하려고 하는 경우 NotFoundException 발생")
      void invokeMessageRoomNotFoundException() {
        doReturn(Optional.of(user1)).when(userRepository).findById(any());
        doThrow(new NotFoundException(ErrorCode.MESSAGE_ROOM_NOT_FOUND))
            .when(messageRoomRepository).findById(any());

        assertThrows(NotFoundException.class, () -> {
          messageRoomService.blockMessageRoom(user1.getId(), messageRoom.getId());
        });
      }

      @Test
      @DisplayName("차단하려는 쪽지방의 대화 참여자가 아닌 유저가 쪽지방 차단을 하려는 경우 PermissionDeniedException 발생")
      void invokePermissionDeniedException() {
        when(messageRoom.getInitialReceiver()).thenReturn(user1);
        when(messageRoom.getInitialSender()).thenReturn(user2);
        when(!(messageRoom.getInitialSender().getId() == user1.getId()) &&
            !(messageRoom.getInitialReceiver().getId() == user1.getId()))
            .thenThrow(new PermissionDeniedException(ErrorCode.NO_PERMISSION_TO_SEND_MESSAGE));

        assertThrows(PermissionDeniedException.class, () -> {
          messageRoomService.blockMessageRoom(user1.getId(), messageRoom.getId());
        });
      }

    }

  }

}