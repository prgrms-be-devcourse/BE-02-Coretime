package com.prgrms.coretime.message.service;

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
import com.prgrms.coretime.message.dto.response.MessageResponse;
import com.prgrms.coretime.user.domain.User;
import com.prgrms.coretime.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MessageService {

  private final MessageRepository messageRepository;
  private final MessageRoomRepository messageRoomRepository;
  private final UserRepository userRepository;

  /**
   * 쪽지 전송
   */
  @Transactional
  public void sendMessage(Long userId, Long messageRoomId, MessageSendRequest request) {
    User currentUser = userRepository.findById(userId)
        .orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND));
    MessageRoom messageRoom = messageRoomRepository.findById(messageRoomId)
        .orElseThrow(() -> new NotFoundException(ErrorCode.MESSAGE_ROOM_NOT_FOUND));
    checkUserAuthority(currentUser, messageRoom);
    checkMessageRoomIsBlocked(messageRoom);

    Message message = Message.builder()
        .messageRoom(messageRoom)
        .writer(currentUser)
        .content(request.getMessage())
        .build();
    messageRepository.save(message);
  }

  /**
   * 쪽지 다건 조회
   */
  @Transactional(readOnly = true)
  public Page<MessageResponse> getAllMessages(Long userId, Long messageRoomId, Pageable pageable) {
    User currentUser = userRepository.findById(userId)
        .orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND));
    MessageRoom messageRoom = messageRoomRepository.findById(messageRoomId)
        .orElseThrow(() -> new NotFoundException(ErrorCode.MESSAGE_ROOM_NOT_FOUND));
    checkMessageRoomIsDeleted(messageRoom, userId);

    Page<Message> messages = messageRoomRepository.findMessagesByMessageRoomId(
        messageRoomId, pageable);
    User interlocutor = currentUser.getId() == messageRoom.getInitialSender().getId()
        ? messageRoom.getInitialReceiver() : messageRoom.getInitialSender();
    return messages.map(message -> new MessageResponse(message, interlocutor));
  }

  /**
   * 쪽지 전송 권한 확인
   */
  private void checkUserAuthority(User user, MessageRoom messageRoom) {
    if (!(messageRoom.getInitialSender().getId() == user.getId()) &&
        !(messageRoom.getInitialReceiver().getId() == user.getId())) {
      throw new PermissionDeniedException(ErrorCode.NO_PERMISSION_TO_SEND_MESSAGE);
    }
  }

  /**
   * 차단된 쪽지방인지 확인
   */
  private void checkMessageRoomIsBlocked(MessageRoom messageRoom) {
    if (messageRoom.getIsBlocked()) {
      throw new CannotSendMessageException(ErrorCode.UNABLE_TO_SEND_MESSAGE);
    }
  }

  /**
   *  삭제한 쪽지방인지 확인
   */
  private void checkMessageRoomIsDeleted(MessageRoom messageRoom, Long userId) {
    VisibilityState visibility = messageRoom.getVisibilityTo();
    if (visibility.equals(VisibilityState.NO_ONE) ||
        (messageRoom.getInitialSender().getId() == userId &&
            visibility.equals(VisibilityState.ONLY_INITIAL_RECEIVER)) ||
        (messageRoom.getInitialReceiver().getId() == userId &&
            visibility.equals(VisibilityState.ONLY_INITIAL_SENDER))) {
      throw new PermissionDeniedException(ErrorCode.NO_PERMISSION_TO_READ_DATA);
    }
  }
}
