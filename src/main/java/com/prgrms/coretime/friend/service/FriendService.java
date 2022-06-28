package com.prgrms.coretime.friend.service;

import com.prgrms.coretime.common.error.DuplicateFriendRequestException;
import com.prgrms.coretime.common.error.ErrorCode;
import com.prgrms.coretime.common.error.FriendAlreadyExistsException;
import com.prgrms.coretime.common.error.InvalidRequestException;
import com.prgrms.coretime.common.error.NotFoundException;
import com.prgrms.coretime.friend.domain.Friend;
import com.prgrms.coretime.friend.domain.FriendId;
import com.prgrms.coretime.friend.domain.FriendRepository;
import com.prgrms.coretime.friend.dto.request.FriendRequestAcceptRequest;
import com.prgrms.coretime.friend.dto.request.FriendRequestRefuseRequest;
import com.prgrms.coretime.friend.dto.request.FriendRequestRevokeRequest;
import com.prgrms.coretime.friend.dto.request.FriendRequestSendRequest;
import com.prgrms.coretime.friend.dto.response.FriendInfoResponse;
import com.prgrms.coretime.friend.dto.response.FriendRequestInfoResponse;
import com.prgrms.coretime.user.domain.TestUser;
import com.prgrms.coretime.user.domain.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

  /**
   * 친구 요청 취소
   */
  @Transactional
  public void revokeFriendRequest(Long userId, FriendRequestRevokeRequest request) {
    TestUser currentUser = userRepository.findById(userId)
        .orElseThrow(() -> new NotFoundException());
    TestUser targetUser = userRepository.findById(request.getFolloweeId())
        .orElseThrow(() -> new NotFoundException());

    FriendId currentUserSideFriendId = new FriendId(currentUser.getId(), targetUser.getId());
    FriendId targetUserSideFriendId = new FriendId(targetUser.getId(), currentUser.getId());
    if (!friendRepository.existsById(currentUserSideFriendId)) {
      throw new NotFoundException();
    }
    if (friendRepository.existsById(currentUserSideFriendId)
        && friendRepository.existsById(targetUserSideFriendId)) {
      throw new FriendAlreadyExistsException(ErrorCode.FRIEND_ALREADY_EXISTS.getMessage());
    }

    friendRepository.deleteById(currentUserSideFriendId);
  }

  /**
   * 친구 요청 수락
   */
  @Transactional
  public void acceptFriendRequest(Long userId, FriendRequestAcceptRequest request) {
    TestUser currentUser = userRepository.findById(userId)
        .orElseThrow(() -> new NotFoundException());
    TestUser targetUser = userRepository.findById(request.getFollowerId())
        .orElseThrow(() -> new NotFoundException());

    FriendId currentUserSideFriendId = new FriendId(currentUser.getId(), targetUser.getId());
    FriendId targetUserSideFriendId = new FriendId(targetUser.getId(), currentUser.getId());
    if (!friendRepository.existsById(targetUserSideFriendId)) {
      throw new NotFoundException();
    }
    if (friendRepository.existsById(currentUserSideFriendId)
        && friendRepository.existsById(targetUserSideFriendId)) {
      throw new FriendAlreadyExistsException(ErrorCode.FRIEND_ALREADY_EXISTS.getMessage());
    }

    Friend friend = new Friend(currentUser, targetUser);
    friendRepository.save(friend);
  }

  /**
   * 친구 요청 거절
   */
  @Transactional
  public void refuseFriendRequest(Long userId, FriendRequestRefuseRequest request) {
    TestUser currentUser = userRepository.findById(userId)
        .orElseThrow(() -> new NotFoundException());
    TestUser targetUser = userRepository.findById(request.getFollowerId())
        .orElseThrow(() -> new NotFoundException());

    FriendId currentUserSideFriendId = new FriendId(currentUser.getId(), targetUser.getId());
    FriendId targetUserSideFriendId = new FriendId(targetUser.getId(), currentUser.getId());
    if (!friendRepository.existsById(targetUserSideFriendId)) {
      throw new NotFoundException();
    }
    if (friendRepository.existsById(currentUserSideFriendId)
        && friendRepository.existsById(targetUserSideFriendId)) {
      throw new FriendAlreadyExistsException(ErrorCode.FRIEND_ALREADY_EXISTS.getMessage());
    }

    friendRepository.deleteById(targetUserSideFriendId);
  }

  /**
   * 친구 요청 받은 목록 조회
   */
  @Transactional(readOnly = true)
  public Page<FriendRequestInfoResponse> getAllFriendRequests(Long userId, Pageable pageable) {
    if (!userRepository.existsById(userId)) {
      throw new NotFoundException();
    }

    Page<Friend> friendRequests = friendRepository.findByFolloweeUser_Id(userId, pageable);
    return friendRequests.map(FriendRequestInfoResponse::new);
  }

  /**
   * 친구 목록 조회
   */
  @Transactional(readOnly = true)
  public Page<FriendInfoResponse> getAllFriends(Long userId, Pageable pageable) {
    if (!userRepository.existsById(userId)) {
      throw new NotFoundException();
    }

    Page<Friend> friends = friendRepository.findAllFriendWithPaging(userId, pageable);
    return friends.map(FriendInfoResponse::new);
  }

}
