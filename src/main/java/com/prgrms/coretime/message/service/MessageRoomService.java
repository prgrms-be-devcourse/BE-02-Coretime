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
import com.prgrms.coretime.post.domain.PostRepository;
import com.prgrms.coretime.user.domain.TestUser;
import com.prgrms.coretime.user.domain.TestUserRepository;
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
  private final TestUserRepository testUserRepository;
  private final PostRepository postRepository;

  /**
   * 쪽지방 생성
   */
  @Transactional
  public MessageRoomIdResponse saveMessageRoom(Long userId, MessageRoomCreateRequest request) {
    if (userId == request.getReceiverId()) {
      throw new InvalidRequestException(ErrorCode.INVALID_MESSAGE_TARGET);
    }
    TestUser currentUser = testUserRepository.findById(userId)
        .orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND));
    TestUser receiver = testUserRepository.findById(request.getReceiverId())
        .orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND));
    Post post = postRepository.findById(request.getCreatedFrom())
        .orElseThrow(() -> new NotFoundException(ErrorCode.NOT_FOUND));// TODO: post(상혁님)부분과 통일시키기

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
    testUserRepository.findById(userId)
        .orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND));
    testUserRepository.findById(receiverId)
        .orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND));
    postRepository.findById(createdFrom)
        .orElseThrow(() -> new NotFoundException(ErrorCode.NOT_FOUND));// TODO: post(상혁님)부분과 통일시키기

    return messageRoomRepository.findIdByInfo(createdFrom, isAnonymous, userId, receiverId);
  }

  /**
   * 쪽지방 조회
   */
  @Transactional(readOnly = true)
  public MessageRoomResponse getMessageRoom(Long userId, MessageRoomGetRequest request) {
    TestUser currentUser = testUserRepository.findById(userId)
        .orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND));
    MessageRoom messageRoom = messageRoomRepository.findById(request.getMessageRoomId())
        .orElseThrow(() -> new NotFoundException(ErrorCode.MESSAGE_ROOM_NOT_FOUND));

    Pageable pageable = PageRequest.of(0, 20, Sort.by("createdAt").descending());
    Page<Message> messages = messageRoomRepository.findMessagesByMessageRoomId(
        messageRoom.getId(), pageable);
    TestUser interlocutor = currentUser.getId() == messageRoom.getInitialSender().getId()
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
    TestUser currentUser = testUserRepository.findById(userId)
        .orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND));

    Page<MessageRoomsWithLastMessages> messageRooms = messageRoomRepository.findMessageRoomsAndLastMessagesByUserId(
        currentUser.getId(), pageable);

    Page<MessageRoomListResponse> responses = messageRooms.map(messageRoom -> {
      Long interlocutorId = userId == messageRoom.getInitialReceiverId().longValue() ?
          messageRoom.getInitialSenderId().longValue()
          : messageRoom.getInitialReceiverId().longValue();
      TestUser interlocutor = testUserRepository.findById(interlocutorId)
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

}
