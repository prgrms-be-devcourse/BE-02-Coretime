package com.prgrms.coretime.message.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.prgrms.coretime.common.ErrorCode;
import com.prgrms.coretime.common.error.exception.CannotSendMessageException;
import com.prgrms.coretime.common.error.exception.NotFoundException;
import com.prgrms.coretime.common.error.exception.PermissionDeniedException;
import com.prgrms.coretime.message.domain.Message;
import com.prgrms.coretime.message.domain.MessageRepository;
import com.prgrms.coretime.message.domain.MessageRoom;
import com.prgrms.coretime.message.domain.MessageRoomRepository;
import com.prgrms.coretime.message.domain.VisibilityState;
import com.prgrms.coretime.message.dto.request.MessageSendRequest;
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
class MessageServiceTest {

  @InjectMocks
  private MessageService messageService;

  @Mock
  private MessageRepository messageRepository;

  @Mock
  private MessageRoomRepository messageRoomRepository;

  @Mock
  private UserRepository userRepository;

  private User user1 = mock(User.class);
  private User user2 = mock(User.class);

  private MessageRoom messageRoom = mock(MessageRoom.class);

  private Page<Message> messages = mock(Page.class);

  @Nested
  @DisplayName("쪽지 전송(생성)")
  class SendMessageTest {

    MessageSendRequest request = new MessageSendRequest("This is the message.");

    @Test
    @DisplayName("성공")
    void success() {
      doReturn(Optional.of(user1)).when(userRepository).findById(any());
      doReturn(Optional.of(messageRoom)).when(messageRoomRepository).findById(any());
      when(messageRoom.getInitialReceiver()).thenReturn(user1);
      when(messageRoom.getInitialSender()).thenReturn(user2);

      messageService.sendMessage(user1.getId(), messageRoom.getId(), request);

      verify(messageRepository).save(any());
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
          messageService.sendMessage(user1.getId(), messageRoom.getId(), request);
        });
      }

      @Test
      @DisplayName("존재하지 않는 쪽지방에 쪽지를 전송하려고 하는 경우 NotFoundException 발생")
      void invokeMessageRoomNotFoundException() {
        doReturn(Optional.of(user1)).when(userRepository).findById(any());
        doThrow(new NotFoundException(ErrorCode.MESSAGE_ROOM_NOT_FOUND))
            .when(messageRoomRepository).findById(any());

        assertThrows(NotFoundException.class, () -> {
          messageService.sendMessage(user1.getId(), messageRoom.getId(), request);
        });
      }

      @Test
      @DisplayName("쪽지를 보내려는 쪽지방의 대화 참여자가 아닌 유저가 쪽지 전송을 하려는 경우 PermissionDeniedException 발생")
      void invokePermissionDeniedException() {
        when(messageRoom.getInitialReceiver()).thenReturn(user1);
        when(messageRoom.getInitialSender()).thenReturn(user2);
        when(!(messageRoom.getInitialSender().getId() == user1.getId()) &&
            !(messageRoom.getInitialReceiver().getId() == user1.getId()))
            .thenThrow(new PermissionDeniedException(ErrorCode.NO_PERMISSION_TO_SEND_MESSAGE));

        assertThrows(PermissionDeniedException.class, () -> {
          messageService.sendMessage(user1.getId(), messageRoom.getId(), request);
        });
      }

      @Test
      @DisplayName("차단된 쪽지방에 쪽지 전송을 하려는 경우 CannotSendMessageException 발생")
      void invokeCannotSendMessageException() {
        doReturn(Optional.of(user1)).when(userRepository).findById(any());
        doReturn(Optional.of(messageRoom)).when(messageRoomRepository).findById(any());
        when(messageRoom.getInitialReceiver()).thenReturn(user1);
        when(messageRoom.getInitialSender()).thenReturn(user2);
        when(messageRoom.getIsBlocked()).thenReturn(true);

        when(messageRoom.getIsBlocked())
            .thenThrow(new CannotSendMessageException(ErrorCode.UNABLE_TO_SEND_MESSAGE));

        assertThrows(CannotSendMessageException.class, () -> {
          messageService.sendMessage(user1.getId(), messageRoom.getId(), request);
        });
      }
    }

  }

  @Nested
  @DisplayName("쪽지 다건 조회")
  class GetAllMessagesTest {

    Pageable pageable = PageRequest.of(2, 20, Sort.by("createdAt").descending());

    @Test
    @DisplayName("성공")
    void success() {
      doReturn(Optional.of(user1)).when(userRepository).findById(any());
      doReturn(Optional.of(messageRoom)).when(messageRoomRepository).findById(any());
      when(messageRoom.getVisibilityTo()).thenReturn(VisibilityState.BOTH);
      when(messageRoom.getInitialReceiver()).thenReturn(user1);
      when(messageRoom.getInitialSender()).thenReturn(user2);
      when(user1.getId()).thenReturn(1L);
      when(messageRoom.getInitialSender().getId()).thenReturn(1L);
      doReturn(messages).when(messageRoomRepository).findMessagesByMessageRoomId(any(), any());

      messageService.getAllMessages(1L, 1L, pageable);

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
          messageService.getAllMessages(1L, 1L, pageable);
        });
      }

      @Test
      @DisplayName("존재하지 않는 쪽지방의 쪽지를 조회하려고 하는 경우 NotFoundException 발생")
      void invokeMessageRoomNotFoundException() {
        doReturn(Optional.of(user1)).when(userRepository).findById(any());
        doThrow(new NotFoundException(ErrorCode.MESSAGE_ROOM_NOT_FOUND))
            .when(messageRoomRepository).findById(any());

        assertThrows(NotFoundException.class, () -> {
          messageService.getAllMessages(1L, 1L, pageable);
        });
      }

      @Test
      @DisplayName("삭제한 쪽지방의 쪽지를 조회하려고 하는 경우 PermissionDeniedException 발생")
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
          messageService.getAllMessages(1L, 1L, pageable);
        });
      }

    }

  }

}