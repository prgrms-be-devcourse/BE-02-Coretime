package com.prgrms.coretime.friend.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.prgrms.coretime.friend.domain.Friend;
import com.prgrms.coretime.friend.domain.FriendRepository;
import com.prgrms.coretime.friend.dto.request.FriendRequestAcceptRequest;
import com.prgrms.coretime.friend.dto.request.FriendRequestRefuseRequest;
import com.prgrms.coretime.friend.dto.request.FriendRequestRevokeRequest;
import com.prgrms.coretime.friend.dto.request.FriendRequestSendRequest;
import com.prgrms.coretime.user.domain.TestUser;
import com.prgrms.coretime.user.domain.UserRepository;
import java.util.ArrayList;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

@ExtendWith(MockitoExtension.class)
class FriendServiceTest {

  @InjectMocks
  private FriendService friendService;

  @Mock
  private FriendRepository friendRepository;

  @Mock
  private UserRepository userRepository;

  private TestUser user1 = mock(TestUser.class);
  private TestUser user2 = mock(TestUser.class);

  private Friend friend1 = mock(Friend.class);

  @Test
  @DisplayName("친구 요청 보내기: 성공")
  void sendFriendRequestSuccessTest() {
    FriendRequestSendRequest request = new FriendRequestSendRequest(1L);

    when(userRepository.findById(any())).thenReturn(Optional.of(user1));
    when(userRepository.findById(any())).thenReturn(Optional.of(user2));
    when(friendRepository.save(any())).thenReturn(friend1);

    friendService.sendFriendRequest(user1.getId(), request);

    verify(friendRepository).save(any());
  }

  @Test
  @DisplayName("친구 요청 취소하기: 성공")
  void revokeFriendRequestSuccessTest() {
    FriendRequestRevokeRequest request = new FriendRequestRevokeRequest(1L);

    doReturn(Optional.of(user1), Optional.of(user2)).when(userRepository).findById(any());

    doReturn(true)
        .doReturn(true)
        .doReturn(false)
        .when(friendRepository).existsById(any());

    friendService.revokeFriendRequest(user1.getId(), request);

    verify(friendRepository).deleteById(any());
  }

  @Test
  @DisplayName("친구 요청 수락하기: 성공")
  void acceptFriendRequestSuccessTest() {
    FriendRequestAcceptRequest request = new FriendRequestAcceptRequest(1L);

    doReturn(Optional.of(user1), Optional.of(user2)).when(userRepository).findById(any());

    doReturn(true)
        .doReturn(true)
        .doReturn(false)
        .when(friendRepository).existsById(any());

    friendService.acceptFriendRequest(user1.getId(), request);

    verify(friendRepository).save(any());
  }

  @Test
  @DisplayName("친구 요청 거절하기: 성공")
  void refuseFriendRequestSuccessTest() {
    FriendRequestRefuseRequest request = new FriendRequestRefuseRequest(1L);

    doReturn(Optional.of(user1), Optional.of(user2)).when(userRepository).findById(any());

    doReturn(true)
        .doReturn(true)
        .doReturn(false)
        .when(friendRepository).existsById(any());

    friendService.refuseFriendRequest(user1.getId(), request);

    verify(friendRepository).deleteById(any());
  }

  @Test
  @DisplayName("친구 요청 받은 목록 조회하기: 성공")
  void getAllFriendRequestsSuccessTest() {
    doReturn(true).when(userRepository).existsById(any());
    doReturn(new PageImpl<>(new ArrayList<>())).when(friendRepository)
        .findByFolloweeUser_Id(any(), any());

    friendService.getAllFriendRequests(user1.getId(), PageRequest.of(0, 20));

    verify(friendRepository).findByFolloweeUser_Id(any(), any());
  }

  @Test
  @DisplayName("친구 목록 조회하기: 성공")
  void getAllFriendsSuccessTest() {
    doReturn(true).when(userRepository).existsById(any());
    doReturn(new PageImpl<>(new ArrayList<>())).when(friendRepository)
        .findAllFriendWithPaging(any(), any());

    friendService.getAllFriends(user1.getId(), PageRequest.of(0, 20));

    verify(friendRepository).findAllFriendWithPaging(any(), any());
  }

}