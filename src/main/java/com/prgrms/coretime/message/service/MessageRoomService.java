package com.prgrms.coretime.message.service;

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
import com.prgrms.coretime.message.dto.response.MessageRoomIdResponse;
import com.prgrms.coretime.message.dto.response.MessageRoomListResponse;
import com.prgrms.coretime.message.dto.response.MessageRoomResponse;
import com.prgrms.coretime.post.domain.Post;
import com.prgrms.coretime.post.domain.repository.PostRepository;
import com.prgrms.coretime.user.domain.User;
import com.prgrms.coretime.user.domain.repository.UserRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MessageRoomService {

  private final MessageRoomRepository messageRoomRepository;
  private final MessageRepository messageRepository;
  private final UserRepository userRepository;
  private final PostRepository postRepository;

  /**
   * 쪽지방 생성
   */
  @Transactional
  public MessageRoomIdResponse saveMessageRoom(Long userId, MessageRoomCreateRequest request) {
    if (userId == request.getReceiverId()) {
      throw new InvalidRequestException(ErrorCode.INVALID_MESSAGE_TARGET);
    }
    User currentUser = userRepository.findById(userId)
        .orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND));
    User receiver = userRepository.findById(request.getReceiverId())
        .orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND));
    Post post = postRepository.findById(request.getCreatedFrom())
        .orElseThrow(() -> new NotFoundException(ErrorCode.POST_NOT_FOUND));

    MessageRoom messageRoom = MessageRoom.builder()
        .initialSender(currentUser)
        .initialReceiver(receiver)
        .createdFrom(post)
        .isAnonymous(request.getIsAnonymous())
        .build();

    MessageRoom savedMessageRoom = messageRoomRepository.save(messageRoom);
    Message message = Message.builder()
        .messageRoom(savedMessageRoom)
        .writer(currentUser)
        .content(request.getFirstMessage())
        .build();
    messageRepository.save(message);
    return new MessageRoomIdResponse(savedMessageRoom);
  }

  /**
   * 쪽지방 Id 조회
   */
  @Transactional(readOnly = true)
  public Optional<Long> getMessageRoomId(Long userId, Long createdFrom, Long receiverId,
      Boolean isAnonymous) {
    userRepository.findById(userId)
        .orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND));
    userRepository.findById(receiverId)
        .orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND));
    postRepository.findById(createdFrom)
        .orElseThrow(() -> new NotFoundException(ErrorCode.POST_NOT_FOUND));

    return messageRoomRepository.findIdByInfo(createdFrom, isAnonymous, userId, receiverId);
  }

  /**
   * 쪽지방 조회
   */
  @Transactional(readOnly = true)
  public MessageRoomResponse getMessageRoom(Long userId, MessageRoomGetRequest request) {
    User currentUser = userRepository.findById(userId)
        .orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND));
    MessageRoom messageRoom = messageRoomRepository.findById(request.getMessageRoomId())
        .orElseThrow(() -> new NotFoundException(ErrorCode.MESSAGE_ROOM_NOT_FOUND));
    checkMessageRoomIsDeleted(messageRoom, userId);

    Pageable pageable = PageRequest.of(0, 20, Sort.by("createdAt").descending());
    Page<Message> messages = messageRoomRepository.findMessagesByMessageRoomId(
        messageRoom.getId(), pageable);
    User interlocutor = currentUser.getId() == messageRoom.getInitialSender().getId()
        ? messageRoom.getInitialReceiver() : messageRoom.getInitialSender();
    return MessageRoomResponse.builder()
        .messages(messages)
        .messageRoom(messageRoom)
        .interlocutor(interlocutor)
        .build();
  }

  /**
   * 쪽지방 리스트 조회
   */
  @Transactional(readOnly = true)
  public Page<MessageRoomListResponse> getMessageRooms(Long userId, Pageable pageable) {
    User currentUser = userRepository.findById(userId)
        .orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND));

    Page<MessageRoomsWithLastMessages> messageRooms = messageRoomRepository.findMessageRoomsAndLastMessagesByUserId(
        currentUser.getId(), pageable);

    Page<MessageRoomListResponse> responses = messageRooms.map(messageRoom -> {
      Long interlocutorId = userId == messageRoom.getInitialReceiverId().longValue() ?
          messageRoom.getInitialSenderId().longValue()
          : messageRoom.getInitialReceiverId().longValue();
      User interlocutor = userRepository.findById(interlocutorId)
          .orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND));
      return MessageRoomListResponse.builder()
          .messageRoomId(messageRoom.getMessageRoomId().longValue())
          .isAnonymous(messageRoom.getIsAnonymous())
          .interlocutorNickname(interlocutor.getNickname())
          .lastMessageSentTime(messageRoom.getCreatedAt().toLocalDateTime())
          .lastMessageContent(messageRoom.getContent())
          .build();
    });

    return responses;
  }

  /**
   * 쪽지방 삭제
   */
  @Transactional
  public void deleteMessageRoom(Long userId, Long messageRoomId) {
    User currentUser = userRepository.findById(userId)
        .orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND));
    MessageRoom messageRoom = messageRoomRepository.findById(messageRoomId)
        .orElseThrow(() -> new NotFoundException(ErrorCode.MESSAGE_ROOM_NOT_FOUND));
    checkUserAuthority(currentUser, messageRoom);

    VisibilityState visibilityState = isInitialSender(currentUser, messageRoom) ?
        VisibilityState.ONLY_INITIAL_RECEIVER : VisibilityState.ONLY_INITIAL_SENDER;
    messageRoom.changeVisibilityTo(visibilityState);
  }

  /**
   * 쪽지방 차단
   */
  @Transactional
  public void blockMessageRoom(Long userId, Long messageRoomId) {
    User currentUser = userRepository.findById(userId)
        .orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND));
    MessageRoom messageRoom = messageRoomRepository.findById(messageRoomId)
        .orElseThrow(() -> new NotFoundException(ErrorCode.MESSAGE_ROOM_NOT_FOUND));
    checkUserAuthority(currentUser, messageRoom);

    messageRoom.changeIsBlocked(true);
  }

  /**
   * 쪽지방 수정(삭제, 차단) 권한 확인
   */
  private void checkUserAuthority(User user, MessageRoom messageRoom) {
    if (!(messageRoom.getInitialSender().getId() == user.getId()) &&
        !(messageRoom.getInitialReceiver().getId() == user.getId())) {
      throw new PermissionDeniedException(ErrorCode.NO_PERMISSION_TO_MODIFY_MESSAGE_ROOM);
    }
  }

  /**
   * 현재 user가 최초 발신자인지 확인
   */
  private boolean isInitialSender(User user, MessageRoom messageRoom) {
    if (messageRoom.getInitialSender().getId() == user.getId()) {
      return true;
    }
    return false;
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
