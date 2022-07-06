package com.prgrms.coretime.message.service;

import com.prgrms.coretime.common.ErrorCode;
import com.prgrms.coretime.common.error.exception.NotFoundException;
import com.prgrms.coretime.message.domain.MessageRoomRepository;
import com.prgrms.coretime.post.domain.PostRepository;
import com.prgrms.coretime.user.domain.TestUserRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MessageRoomService {

  private final MessageRoomRepository messageRoomRepository;
  private final TestUserRepository testUserRepository;
  private final PostRepository postRepository;

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

}
