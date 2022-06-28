package com.prgrms.coretime.friend.service;

import com.prgrms.coretime.common.error.DuplicateFriendRequestException;
import com.prgrms.coretime.common.error.ErrorCode;
import com.prgrms.coretime.common.error.FriendAlreadyExistsException;
import com.prgrms.coretime.common.error.InvalidRequestException;
import com.prgrms.coretime.common.error.NotFoundException;
import com.prgrms.coretime.friend.domain.Friend;
import com.prgrms.coretime.friend.domain.FriendId;
import com.prgrms.coretime.friend.domain.FriendRepository;
import com.prgrms.coretime.friend.dto.request.FriendRequestSendRequest;
import com.prgrms.coretime.user.domain.TestUser;
import com.prgrms.coretime.user.domain.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FriendService {

  private final FriendRepository friendRepository;
  private final UserRepository userRepository;

  /**
   * 친구 요청 보내기
   */
  @Transactional
  public void sendFriendRequest(Long userId, FriendRequestSendRequest request) {
    if (userId == request.getFolloweeId()) {
      throw new InvalidRequestException(ErrorCode.INVALID_FRIEND_REQUEST_TARGET.getMessage());
    }

    TestUser currentUser = userRepository.findById(userId)
        .orElseThrow(() -> new NotFoundException());
    TestUser targetUser = userRepository.findById(request.getFolloweeId())
        .orElseThrow(() -> new NotFoundException());

    FriendId currentUserSideFriendId = new FriendId(currentUser.getId(), targetUser.getId());
    FriendId targetUserSideFriendId = new FriendId(targetUser.getId(), currentUser.getId());
    if (friendRepository.existsById(currentUserSideFriendId)
        && friendRepository.existsById(targetUserSideFriendId)) {
      throw new FriendAlreadyExistsException(ErrorCode.FRIEND_ALREADY_EXISTS.getMessage());
    }
    if (friendRepository.existsById(currentUserSideFriendId)) {
      throw new DuplicateFriendRequestException(ErrorCode.DUPLICATE_FRIEND_REQUEST.getMessage());
    }

    Friend friend = new Friend(currentUser, targetUser);
    friendRepository.save(friend);
  }

}
