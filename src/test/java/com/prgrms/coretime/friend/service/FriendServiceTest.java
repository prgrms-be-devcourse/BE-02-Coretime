package com.prgrms.coretime.friend.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.prgrms.coretime.common.ErrorCode;
import com.prgrms.coretime.common.error.exception.AlreadyExistsException;
import com.prgrms.coretime.common.error.exception.DuplicateRequestException;
import com.prgrms.coretime.common.error.exception.InvalidRequestException;
import com.prgrms.coretime.common.error.exception.NotFoundException;
import com.prgrms.coretime.friend.domain.Friend;
import com.prgrms.coretime.friend.domain.FriendRepository;
import com.prgrms.coretime.friend.dto.request.FriendDeleteRequest;
import com.prgrms.coretime.friend.dto.request.FriendRequestAcceptRequest;
import com.prgrms.coretime.friend.dto.request.FriendRequestRefuseRequest;
import com.prgrms.coretime.friend.dto.request.FriendRequestRevokeRequest;
import com.prgrms.coretime.friend.dto.request.FriendRequestSendRequest;
import com.prgrms.coretime.user.domain.User;
import com.prgrms.coretime.user.domain.repository.UserRepository;
import java.util.ArrayList;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class FriendServiceTest {

  @InjectMocks
  private FriendService friendService;

  @Mock
  private FriendRepository friendRepository;

  @Mock
  private UserRepository userRepository;

  private User user1 = mock(User.class);
  private User user2 = mock(User.class);

  private Friend friend1 = mock(Friend.class);
  private Friend friend2 = mock(Friend.class);

  @Nested
  @DisplayName("친구 요청 보내기")
  class SendFriendRequestTest {

    FriendRequestSendRequest request = new FriendRequestSendRequest(1L);

    @Test
    @DisplayName("성공")
    void success() {
      doReturn(Optional.of(user1), Optional.of(user2)).when(userRepository).findById(any());
      doReturn(false).when(friendRepository).existsFriendRelationship(any(), any());
      doReturn(false).when(friendRepository).existsById(any());

      when(friendRepository.save(any())).thenReturn(friend1);

      friendService.sendFriendRequest(user1.getId(), request);

      verify(friendRepository).save(any());
    }

    @Nested
    @DisplayName("실패")
    class Fail {

      @Test
      @DisplayName("자기 자신에게 친구 요청을 보내는 경우 InvalidRequestException 발생")
      void invokeInvalidRequestException() {
        when(user1.getId() != request.getFolloweeId())
            .thenThrow(new InvalidRequestException(ErrorCode.INVALID_FRIEND_REQUEST_TARGET));

        assertThrows(InvalidRequestException.class, () -> {
          friendService.sendFriendRequest(user1.getId(), request);
        });
      }

      @Test
      @DisplayName("user가 존재하지 않은 경우 NotFoundException 발생")
      void invokeUserNotFoundException() {
        doThrow(new NotFoundException(ErrorCode.USER_NOT_FOUND))
            .when(userRepository).findById(any());

        assertThrows(NotFoundException.class, () -> {
          friendService.sendFriendRequest(user1.getId(), request);
        });
      }

      @Test
      @DisplayName("이미 등록된 친구인 유저에게 친구 요청을 보내는 경우 AlreadyExistsException 발생")
      void invokeAlreadyExistsException() {
        doReturn(Optional.of(user1), Optional.of(user2)).when(userRepository).findById(any());
        doThrow(new AlreadyExistsException(ErrorCode.FRIEND_ALREADY_EXISTS))
            .when(friendRepository).existsFriendRelationship(any(), any());

        assertThrows(AlreadyExistsException.class, () -> {
          friendService.sendFriendRequest(user1.getId(), request);
        });
      }

      @Test
      @DisplayName("이미 친구요청을 보낸 유저에게 친구 요청을 보내는 경우 DuplicateRequestException 발생")
      void invokeDuplicateRequestException() {
        doReturn(Optional.of(user1), Optional.of(user2)).when(userRepository).findById(any());
        doReturn(false).when(friendRepository).existsFriendRelationship(any(), any());
        doThrow(new DuplicateRequestException(ErrorCode.DUPLICATE_FRIEND_REQUEST))
            .when(friendRepository).existsById(any());

        assertThrows(DuplicateRequestException.class, () -> {
          friendService.sendFriendRequest(user1.getId(), request);
        });
      }

    }

  }

  @Nested
  @DisplayName("친구 요청 취소하기")
  class FriendRequestTest {

    FriendRequestRevokeRequest request = new FriendRequestRevokeRequest(1L);

    @Test
    @DisplayName("성공")
    void success() {
      doReturn(Optional.of(user1), Optional.of(user2)).when(userRepository).findById(any());
      doReturn(true).when(friendRepository).existsById(any());
      doReturn(false).when(friendRepository).existsFriendRelationship(anyLong(), anyLong());

      friendService.revokeFriendRequest(user1.getId(), request);

      verify(friendRepository).deleteById(any());
    }

    @Nested
    @DisplayName("실패")
    class Fail {

      @Test
      @DisplayName("user가 존재하지 않는 경우 NotFoundException 발생")
      void invokeUserNotFoundException() {
        doThrow(new NotFoundException(ErrorCode.USER_NOT_FOUND))
            .when(userRepository).findById(any());

        assertThrows(NotFoundException.class, () -> {
          friendService.revokeFriendRequest(user1.getId(), request);
        });
      }

      @Test
      @DisplayName("취소하려는 친구 요청이 존재하지 않는 경우 NotFoundException 발생")
      void invokeFriendRequestNotFoundException() {
        doReturn(Optional.of(user1), Optional.of(user2)).when(userRepository).findById(any());
        doThrow(new NotFoundException(ErrorCode.FRIEND_NOT_FOUND))
            .when(friendRepository).existsById(any());

        assertThrows(NotFoundException.class, () -> {
          friendService.revokeFriendRequest(user1.getId(), request);
        });
      }

      @Test
      @DisplayName("이미 친구 관계인 경우 NotFoundException 발생")
      void invokeFriendNotFoundException() {
        doReturn(Optional.of(user1), Optional.of(user2)).when(userRepository).findById(any());
        doReturn(true).when(friendRepository).existsById(any());
        doThrow(new AlreadyExistsException(ErrorCode.FRIEND_ALREADY_EXISTS))
            .when(friendRepository).existsFriendRelationship(any(), any());

        assertThrows(AlreadyExistsException.class, () -> {
          friendService.revokeFriendRequest(user1.getId(), request);
        });
      }

    }

  }

  @Nested
  @DisplayName("친구 요청 수락하기")
  class AcceptFriendRequestTest {

    FriendRequestAcceptRequest request = new FriendRequestAcceptRequest(1L);

    @Test
    @DisplayName("성공")
    void success() {
      doReturn(Optional.of(user1), Optional.of(user2)).when(userRepository).findById(any());
      doReturn(true).when(friendRepository).existsById(any());
      doReturn(false).when(friendRepository).existsFriendRelationship(anyLong(), anyLong());

      friendService.acceptFriendRequest(user1.getId(), request);

      verify(friendRepository).save(any());
    }

    @Nested
    @DisplayName("실패")
    class Fail {

      @Test
      @DisplayName("user가 존재하지 않는 경우 NotFoundException 발생")
      void invokeUserNotFoundException() {
        doThrow(new NotFoundException(ErrorCode.USER_NOT_FOUND))
            .when(userRepository).findById(any());

        assertThrows(NotFoundException.class, () -> {
          friendService.acceptFriendRequest(user1.getId(), request);
        });
      }

      @Test
      @DisplayName("수락하려는 친구 요청이 존재하지 않는 경우 NotFoundException 발생")
      void invokeFriendRequestNotFoundException() {
        doReturn(Optional.of(user1), Optional.of(user2)).when(userRepository).findById(any());
        doThrow(new NotFoundException(ErrorCode.FRIEND_NOT_FOUND))
            .when(friendRepository).existsById(any());

        assertThrows(NotFoundException.class, () -> {
          friendService.acceptFriendRequest(user1.getId(), request);
        });
      }

      @Test
      @DisplayName("이미 친구 관계인 경우 NotFoundException 발생")
      void invokeFriendNotFoundException() {
        doReturn(Optional.of(user1), Optional.of(user2)).when(userRepository).findById(any());
        doReturn(true).when(friendRepository).existsById(any());
        doThrow(new AlreadyExistsException(ErrorCode.FRIEND_ALREADY_EXISTS))
            .when(friendRepository).existsFriendRelationship(any(), any());

        assertThrows(AlreadyExistsException.class, () -> {
          friendService.acceptFriendRequest(user1.getId(), request);
        });
      }
    }

  }

  @Nested
  @DisplayName("친구 요청 거절하기")
  class RefuseFriendRequestTest {

    FriendRequestRefuseRequest request = new FriendRequestRefuseRequest(1L);

    @Test
    @DisplayName("성공")
    void success() {
      doReturn(Optional.of(user1), Optional.of(user2)).when(userRepository).findById(any());
      doReturn(true).when(friendRepository).existsById(any());
      doReturn(false).when(friendRepository).existsFriendRelationship(anyLong(), anyLong());

      friendService.refuseFriendRequest(user1.getId(), request);

      verify(friendRepository).deleteById(any());
    }

    @Nested
    @DisplayName("실패")
    class Fail {

      @Test
      @DisplayName("user가 존재하지 않는 경우 NotFoundException 발생")
      void invokeUserNotFoundException() {
        doThrow(new NotFoundException(ErrorCode.USER_NOT_FOUND))
            .when(userRepository).findById(any());

        assertThrows(NotFoundException.class, () -> {
          friendService.refuseFriendRequest(user1.getId(), request);
        });
      }

      @Test
      @DisplayName("수락하려는 친구 요청이 존재하지 않는 경우 NotFoundException 발생")
      void invokeFriendRequestNotFoundException() {
        doReturn(Optional.of(user1), Optional.of(user2)).when(userRepository).findById(any());
        doThrow(new NotFoundException(ErrorCode.FRIEND_NOT_FOUND))
            .when(friendRepository).existsById(any());

        assertThrows(NotFoundException.class, () -> {
          friendService.refuseFriendRequest(user1.getId(), request);
        });
      }

      @Test
      @DisplayName("이미 친구 관계인 경우 NotFoundException 발생")
      void invokeFriendNotFoundException() {
        doReturn(Optional.of(user1), Optional.of(user2)).when(userRepository).findById(any());
        doReturn(true).when(friendRepository).existsById(any());
        doThrow(new AlreadyExistsException(ErrorCode.FRIEND_ALREADY_EXISTS))
            .when(friendRepository).existsFriendRelationship(any(), any());

        assertThrows(AlreadyExistsException.class, () -> {
          friendService.refuseFriendRequest(user1.getId(), request);
        });
      }
    }

  }

  @Nested
  @DisplayName("친구 요청 받은 목록 조회하기")
  class GetAllFriendRequestsTest {

    Pageable pageable = PageRequest.of(0, 20);

    @Test
    @DisplayName("성공")
    void success() {
      doReturn(true).when(userRepository).existsById(any());
      doReturn(new PageImpl<>(new ArrayList<>())).when(friendRepository)
          .findByFolloweeUser_Id(any(), any());

      friendService.getAllFriendRequests(user1.getId(), pageable);

      verify(friendRepository).findByFolloweeUser_Id(any(), any());
    }

    @Nested
    @DisplayName("실패")
    class Fail {

      @Test
      @DisplayName("user가 존재하지 않는 경우 NotFoundException 발생")
      void invokeUserNotFoundException() {
        doThrow(new NotFoundException(ErrorCode.USER_NOT_FOUND))
            .when(userRepository).existsById(any());

        assertThrows(NotFoundException.class, () -> {
          friendService.getAllFriendRequests(user1.getId(), pageable);
        });
      }
    }
  }

  @Nested
  @DisplayName("친구 목록 조회하기")
  class GetAllFriendsTest {

    Pageable pageable = PageRequest.of(0, 20);

    @Test
    @DisplayName("성공")
    void success() {
      doReturn(true).when(userRepository).existsById(any());
      doReturn(new PageImpl<>(new ArrayList<>())).when(friendRepository)
          .findAllFriendWithPaging(any(), any());

      friendService.getAllFriends(user1.getId(), pageable);

      verify(friendRepository).findAllFriendWithPaging(any(), any());
    }

    @Nested
    @DisplayName("실패")
    class Fail {

      @Test
      @DisplayName("user가 존재하지 않는 경우 NotFoundException 발생")
      void invokeUserNotFoundException() {
        doThrow(new NotFoundException(ErrorCode.USER_NOT_FOUND))
            .when(userRepository).existsById(any());

        assertThrows(NotFoundException.class, () -> {
          friendService.getAllFriends(user1.getId(), pageable);
        });
      }
    }
  }

  @Nested
  @DisplayName("친구 삭제하기")
  class DeleteFriendTest {

    FriendDeleteRequest request = new FriendDeleteRequest(1L);

    @Test
    @DisplayName("성공")
    void success() {
      doReturn(true).when(friendRepository).existsFriendRelationship(anyLong(), anyLong());

      friendService.deleteFriend(user1.getId(), request);

      verify(friendRepository, times(2)).deleteById(any());
    }

    @Nested
    @DisplayName("실패")
    class Fail {

      @Test
      @DisplayName("친구관계가 존재하지 않는 경우 NotFoundException 발생")
      void invokeUserNotFoundException() {
        doThrow(new NotFoundException(ErrorCode.FRIEND_NOT_FOUND))
            .when(friendRepository).existsFriendRelationship(any(), any());

        assertThrows(NotFoundException.class, () -> {
          friendService.deleteFriend(user1.getId(), request);
        });
      }
    }

  }
}