package com.prgrms.coretime.friend.service;

import com.prgrms.coretime.common.ErrorCode;
import com.prgrms.coretime.common.error.exception.DuplicateRequestException;
import com.prgrms.coretime.common.error.exception.AlreadyExistsException;
import com.prgrms.coretime.common.error.exception.InvalidRequestException;
import com.prgrms.coretime.common.error.exception.NotFoundException;
import com.prgrms.coretime.friend.domain.Friend;
import com.prgrms.coretime.friend.domain.FriendId;
import com.prgrms.coretime.friend.domain.FriendRepository;
import com.prgrms.coretime.friend.dto.request.FriendDeleteRequest;
import com.prgrms.coretime.friend.dto.request.FriendRequestAcceptRequest;
import com.prgrms.coretime.friend.dto.request.FriendRequestRefuseRequest;
import com.prgrms.coretime.friend.dto.request.FriendRequestRevokeRequest;
import com.prgrms.coretime.friend.dto.request.FriendRequestSendRequest;
import com.prgrms.coretime.friend.dto.response.FriendInfoResponse;
import com.prgrms.coretime.friend.dto.response.FriendRequestInfoResponse;
import com.prgrms.coretime.user.domain.TestUser;
import com.prgrms.coretime.user.domain.TestUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FriendService {

  private final FriendRepository friendRepository;
  private final TestUserRepository testUserRepository;

  /**
   * 친구 요청 보내기
   */
  @Transactional
  public void sendFriendRequest(Long userId, FriendRequestSendRequest request) {
    if (userId == request.getFolloweeId()) {
      throw new InvalidRequestException(ErrorCode.INVALID_FRIEND_REQUEST_TARGET);
    }

    TestUser currentUser = testUserRepository.findById(userId)
        .orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND));
    TestUser targetUser = testUserRepository.findById(request.getFolloweeId())
        .orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND));

    FriendId currentUserSideFriendId = new FriendId(currentUser.getId(), targetUser.getId());
    if (friendRepository.existsFriendRelationship(currentUser.getId(), targetUser.getId())) {
      throw new AlreadyExistsException(ErrorCode.FRIEND_ALREADY_EXISTS);
    }
    if (friendRepository.existsById(currentUserSideFriendId)) {
      throw new DuplicateRequestException(ErrorCode.DUPLICATE_FRIEND_REQUEST);
    }

    Friend friend = new Friend(currentUser, targetUser);
    friendRepository.save(friend);
  }

  /**
   * 친구 요청 취소
   */
  @Transactional
  public void revokeFriendRequest(Long userId, FriendRequestRevokeRequest request) {
    TestUser currentUser = testUserRepository.findById(userId)
        .orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND));
    TestUser targetUser = testUserRepository.findById(request.getFolloweeId())
        .orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND));

    FriendId currentUserSideFriendId = new FriendId(currentUser.getId(), targetUser.getId());
    if (!friendRepository.existsById(currentUserSideFriendId)) {
      throw new NotFoundException(ErrorCode.FRIEND_NOT_FOUND);
    }
    if (friendRepository.existsFriendRelationship(currentUser.getId(), targetUser.getId())) {
      throw new AlreadyExistsException(ErrorCode.FRIEND_ALREADY_EXISTS);
    }

    friendRepository.deleteById(currentUserSideFriendId);
  }

  /**
   * 친구 요청 수락
   */
  @Transactional
  public void acceptFriendRequest(Long userId, FriendRequestAcceptRequest request) {
    TestUser currentUser = testUserRepository.findById(userId)
        .orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND));
    TestUser targetUser = testUserRepository.findById(request.getFollowerId())
        .orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND));

    FriendId targetUserSideFriendId = new FriendId(targetUser.getId(), currentUser.getId());
    if (!friendRepository.existsById(targetUserSideFriendId)) {
      throw new NotFoundException(ErrorCode.FRIEND_NOT_FOUND);
    }
    if (friendRepository.existsFriendRelationship(currentUser.getId(), targetUser.getId())) {
      throw new AlreadyExistsException(ErrorCode.FRIEND_ALREADY_EXISTS);
    }

    Friend friend = new Friend(currentUser, targetUser);
    friendRepository.save(friend);
  }

  /**
   * 친구 요청 거절
   */
  @Transactional
  public void refuseFriendRequest(Long userId, FriendRequestRefuseRequest request) {
    TestUser currentUser = testUserRepository.findById(userId)
        .orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND));
    TestUser targetUser = testUserRepository.findById(request.getFollowerId())
        .orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND));

    FriendId targetUserSideFriendId = new FriendId(targetUser.getId(), currentUser.getId());
    if (!friendRepository.existsById(targetUserSideFriendId)) {
      throw new NotFoundException(ErrorCode.FRIEND_NOT_FOUND);
    }
    if (friendRepository.existsFriendRelationship(currentUser.getId(), targetUser.getId())) {
      throw new AlreadyExistsException(ErrorCode.FRIEND_ALREADY_EXISTS);
    }

    friendRepository.deleteById(targetUserSideFriendId);
  }

  /**
   * 친구 요청 받은 목록 조회
   */
  @Transactional(readOnly = true)
  public Page<FriendRequestInfoResponse> getAllFriendRequests(Long userId, Pageable pageable) {
    if (!testUserRepository.existsById(userId)) {
      throw new NotFoundException(ErrorCode.USER_NOT_FOUND);
    }

    Page<Friend> friendRequests = friendRepository.findByFolloweeUser_Id(userId, pageable);
    return friendRequests.map(FriendRequestInfoResponse::new);
  }

  /**
   * 친구 목록 조회
   */
  @Transactional(readOnly = true)
  public Page<FriendInfoResponse> getAllFriends(Long userId, Pageable pageable) {
    if (!testUserRepository.existsById(userId)) {
      throw new NotFoundException(ErrorCode.USER_NOT_FOUND);
    }

    Page<Friend> friends = friendRepository.findAllFriendWithPaging(userId, pageable);
    return friends.map(FriendInfoResponse::new);
  }

  /**
   * 친구 삭제
   */
  @Transactional
  public void deleteFriend(Long userId, FriendDeleteRequest request) {
    FriendId currentUserSideFriendId = new FriendId(userId, request.getFriendId());
    FriendId targetUserSideFriendId = new FriendId(request.getFriendId(), userId);
    if (friendRepository.existsFriendRelationship(userId, request.getFriendId())) {
      friendRepository.deleteById(currentUserSideFriendId);
      friendRepository.deleteById(targetUserSideFriendId);
    } else {
      throw new NotFoundException(ErrorCode.FRIEND_NOT_FOUND);
    }
  }
}
