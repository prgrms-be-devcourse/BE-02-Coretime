package com.prgrms.coretime.message.service;

import com.prgrms.coretime.common.ErrorCode;
import com.prgrms.coretime.common.error.exception.NotFoundException;
import com.prgrms.coretime.common.error.exception.PermissionDeniedException;
import com.prgrms.coretime.message.domain.Message;
import com.prgrms.coretime.message.domain.MessageRepository;
import com.prgrms.coretime.message.domain.MessageRoom;
import com.prgrms.coretime.message.domain.MessageRoomRepository;
import com.prgrms.coretime.message.dto.request.MessageSendRequest;
import com.prgrms.coretime.message.dto.response.MessageResponse;
import com.prgrms.coretime.user.domain.TestUser;
import com.prgrms.coretime.user.domain.TestUserRepository;
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
  private final TestUserRepository testUserRepository;

  /**
   * 쪽지 전송
   */
  @Transactional
  public void sendMessage(Long userId, Long messageRoomId, MessageSendRequest request) {
    TestUser currentUser = testUserRepository.findById(userId)
        .orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND));
    MessageRoom messageRoom = messageRoomRepository.findById(messageRoomId)
        .orElseThrow(() -> new NotFoundException(ErrorCode.MESSAGE_ROOM_NOT_FOUND));
    checkUserAuthority(currentUser, messageRoom);

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
    TestUser currentUser = testUserRepository.findById(userId)
        .orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND));
    MessageRoom messageRoom = messageRoomRepository.findById(messageRoomId)
        .orElseThrow(() -> new NotFoundException(ErrorCode.MESSAGE_ROOM_NOT_FOUND));

    Page<Message> messages = messageRoomRepository.findMessagesByMessageRoomId(
        messageRoomId, pageable);
    TestUser interlocutor = currentUser.getId() == messageRoom.getInitialSender().getId()
        ? messageRoom.getInitialReceiver() : messageRoom.getInitialSender();
    return messages.map(message -> new MessageResponse(message, interlocutor));
  }

  /**
   * 쪽지 전송 권한 확인
   */
  private void checkUserAuthority(TestUser user, MessageRoom messageRoom) {
    if (!(messageRoom.getInitialSender().getId() == user.getId()) &&
        !(messageRoom.getInitialReceiver().getId() == user.getId())) {
      throw new PermissionDeniedException(ErrorCode.NO_PERMISSION_TO_SEND_MESSAGE);
    }
  }

}
