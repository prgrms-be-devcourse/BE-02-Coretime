package com.prgrms.coretime.friend.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.prgrms.coretime.friend.domain.Friend;
import com.prgrms.coretime.friend.domain.FriendRepository;
import com.prgrms.coretime.friend.dto.request.FriendDeleteRequest;
import com.prgrms.coretime.friend.dto.request.FriendRequestAcceptRequest;
import com.prgrms.coretime.friend.dto.request.FriendRequestRefuseRequest;
import com.prgrms.coretime.friend.dto.request.FriendRequestRevokeRequest;
import com.prgrms.coretime.friend.dto.request.FriendRequestSendRequest;
import com.prgrms.coretime.user.domain.TestUser;
import com.prgrms.coretime.user.domain.TestUserRepository;
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
  private TestUserRepository testUserRepository;

  private TestUser user1 = mock(TestUser.class);
  private TestUser user2 = mock(TestUser.class);

  private Friend friend1 = mock(Friend.class);
  private Friend friend2 = mock(Friend.class);

  @Test
  @DisplayName("친구 요청 보내기: 성공")
  void sendFriendRequestSuccessTest() {
    FriendRequestSendRequest request = new FriendRequestSendRequest(1L);

    doReturn(Optional.of(user1), Optional.of(user2)).when(testUserRepository).findById(any());
    doReturn(false).when(friendRepository).existsFriendRelationship(any(), any());
    doReturn(false).when(friendRepository).existsById(any());

    when(friendRepository.save(any())).thenReturn(friend1);

    friendService.sendFriendRequest(user1.getId(), request);

    verify(friendRepository).save(any());
  }

  @Test
  @DisplayName("친구 요청 취소하기: 성공")
  void revokeFriendRequestSuccessTest() {
    FriendRequestRevokeRequest request = new FriendRequestRevokeRequest(1L);

    doReturn(Optional.of(user1), Optional.of(user2)).when(testUserRepository).findById(any());
    doReturn(true).when(friendRepository).existsById(any());
    doReturn(false).when(friendRepository).existsFriendRelationship(anyLong(), anyLong());

    friendService.revokeFriendRequest(user1.getId(), request);

    verify(friendRepository).deleteById(any());
  }

  @Test
  @DisplayName("친구 요청 수락하기: 성공")
  void acceptFriendRequestSuccessTest() {
    FriendRequestAcceptRequest request = new FriendRequestAcceptRequest(1L);

    doReturn(Optional.of(user1), Optional.of(user2)).when(testUserRepository).findById(any());
    doReturn(true).when(friendRepository).existsById(any());
    doReturn(false).when(friendRepository).existsFriendRelationship(anyLong(), anyLong());

    friendService.acceptFriendRequest(user1.getId(), request);

    verify(friendRepository).save(any());
  }

  @Test
  @DisplayName("친구 요청 거절하기: 성공")
  void refuseFriendRequestSuccessTest() {
    FriendRequestRefuseRequest request = new FriendRequestRefuseRequest(1L);

    doReturn(Optional.of(user1), Optional.of(user2)).when(testUserRepository).findById(any());
    doReturn(true).when(friendRepository).existsById(any());
    doReturn(false).when(friendRepository).existsFriendRelationship(anyLong(), anyLong());

    friendService.refuseFriendRequest(user1.getId(), request);

    verify(friendRepository).deleteById(any());
  }

  @Test
  @DisplayName("친구 요청 받은 목록 조회하기: 성공")
  void getAllFriendRequestsSuccessTest() {
    doReturn(true).when(testUserRepository).existsById(any());
    doReturn(new PageImpl<>(new ArrayList<>())).when(friendRepository)
        .findByFolloweeUser_Id(any(), any());

    friendService.getAllFriendRequests(user1.getId(), PageRequest.of(0, 20));

    verify(friendRepository).findByFolloweeUser_Id(any(), any());
  }

  @Test
  @DisplayName("친구 목록 조회하기: 성공")
  void getAllFriendsSuccessTest() {
    doReturn(true).when(testUserRepository).existsById(any());
    doReturn(new PageImpl<>(new ArrayList<>())).when(friendRepository)
        .findAllFriendWithPaging(any(), any());

    friendService.getAllFriends(user1.getId(), PageRequest.of(0, 20));

    verify(friendRepository).findAllFriendWithPaging(any(), any());
  }

  @Test
  @DisplayName("친구 삭제하기: 성공")
  void deleteFriendSuccessTest() {
    FriendDeleteRequest request = new FriendDeleteRequest(1L);

    doReturn(true).when(friendRepository).existsFriendRelationship(anyLong(), anyLong());

    friendService.deleteFriend(user1.getId(), request);

    verify(friendRepository, times(2)).deleteById(any());
  }
}