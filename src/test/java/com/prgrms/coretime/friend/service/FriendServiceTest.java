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
  @DisplayName("?????? ?????? ?????????")
  class SendFriendRequestTest {

    FriendRequestSendRequest request = new FriendRequestSendRequest(1L);

    @Test
    @DisplayName("??????")
    void success() {
      doReturn(Optional.of(user1), Optional.of(user2)).when(userRepository).findById(any());
      doReturn(false).when(friendRepository).existsFriendRelationship(any(), any());
      doReturn(false).when(friendRepository).existsById(any());

      when(friendRepository.save(any())).thenReturn(friend1);

      friendService.sendFriendRequest(user1.getId(), request);

      verify(friendRepository).save(any());
    }

    @Nested
    @DisplayName("??????")
    class Fail {

      @Test
      @DisplayName("?????? ???????????? ?????? ????????? ????????? ?????? InvalidRequestException ??????")
      void invokeInvalidRequestException() {
        when(user1.getId() != request.getFolloweeId())
            .thenThrow(new InvalidRequestException(ErrorCode.INVALID_FRIEND_REQUEST_TARGET));

        assertThrows(InvalidRequestException.class, () -> {
          friendService.sendFriendRequest(user1.getId(), request);
        });
      }

      @Test
      @DisplayName("user??? ???????????? ?????? ?????? NotFoundException ??????")
      void invokeUserNotFoundException() {
        doThrow(new NotFoundException(ErrorCode.USER_NOT_FOUND))
            .when(userRepository).findById(any());

        assertThrows(NotFoundException.class, () -> {
          friendService.sendFriendRequest(user1.getId(), request);
        });
      }

      @Test
      @DisplayName("?????? ????????? ????????? ???????????? ?????? ????????? ????????? ?????? AlreadyExistsException ??????")
      void invokeAlreadyExistsException() {
        doReturn(Optional.of(user1), Optional.of(user2)).when(userRepository).findById(any());
        doThrow(new AlreadyExistsException(ErrorCode.FRIEND_ALREADY_EXISTS))
            .when(friendRepository).existsFriendRelationship(any(), any());

        assertThrows(AlreadyExistsException.class, () -> {
          friendService.sendFriendRequest(user1.getId(), request);
        });
      }

      @Test
      @DisplayName("?????? ??????????????? ?????? ???????????? ?????? ????????? ????????? ?????? DuplicateRequestException ??????")
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
  @DisplayName("?????? ?????? ????????????")
  class FriendRequestTest {

    FriendRequestRevokeRequest request = new FriendRequestRevokeRequest(1L);

    @Test
    @DisplayName("??????")
    void success() {
      doReturn(Optional.of(user1), Optional.of(user2)).when(userRepository).findById(any());
      doReturn(true).when(friendRepository).existsById(any());
      doReturn(false).when(friendRepository).existsFriendRelationship(anyLong(), anyLong());

      friendService.revokeFriendRequest(user1.getId(), request);

      verify(friendRepository).deleteById(any());
    }

    @Nested
    @DisplayName("??????")
    class Fail {

      @Test
      @DisplayName("user??? ???????????? ?????? ?????? NotFoundException ??????")
      void invokeUserNotFoundException() {
        doThrow(new NotFoundException(ErrorCode.USER_NOT_FOUND))
            .when(userRepository).findById(any());

        assertThrows(NotFoundException.class, () -> {
          friendService.revokeFriendRequest(user1.getId(), request);
        });
      }

      @Test
      @DisplayName("??????????????? ?????? ????????? ???????????? ?????? ?????? NotFoundException ??????")
      void invokeFriendRequestNotFoundException() {
        doReturn(Optional.of(user1), Optional.of(user2)).when(userRepository).findById(any());
        doThrow(new NotFoundException(ErrorCode.FRIEND_NOT_FOUND))
            .when(friendRepository).existsById(any());

        assertThrows(NotFoundException.class, () -> {
          friendService.revokeFriendRequest(user1.getId(), request);
        });
      }

      @Test
      @DisplayName("?????? ?????? ????????? ?????? NotFoundException ??????")
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
  @DisplayName("?????? ?????? ????????????")
  class AcceptFriendRequestTest {

    FriendRequestAcceptRequest request = new FriendRequestAcceptRequest(1L);

    @Test
    @DisplayName("??????")
    void success() {
      doReturn(Optional.of(user1), Optional.of(user2)).when(userRepository).findById(any());
      doReturn(true).when(friendRepository).existsById(any());
      doReturn(false).when(friendRepository).existsFriendRelationship(anyLong(), anyLong());

      friendService.acceptFriendRequest(user1.getId(), request);

      verify(friendRepository).save(any());
    }

    @Nested
    @DisplayName("??????")
    class Fail {

      @Test
      @DisplayName("user??? ???????????? ?????? ?????? NotFoundException ??????")
      void invokeUserNotFoundException() {
        doThrow(new NotFoundException(ErrorCode.USER_NOT_FOUND))
            .when(userRepository).findById(any());

        assertThrows(NotFoundException.class, () -> {
          friendService.acceptFriendRequest(user1.getId(), request);
        });
      }

      @Test
      @DisplayName("??????????????? ?????? ????????? ???????????? ?????? ?????? NotFoundException ??????")
      void invokeFriendRequestNotFoundException() {
        doReturn(Optional.of(user1), Optional.of(user2)).when(userRepository).findById(any());
        doThrow(new NotFoundException(ErrorCode.FRIEND_NOT_FOUND))
            .when(friendRepository).existsById(any());

        assertThrows(NotFoundException.class, () -> {
          friendService.acceptFriendRequest(user1.getId(), request);
        });
      }

      @Test
      @DisplayName("?????? ?????? ????????? ?????? NotFoundException ??????")
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
  @DisplayName("?????? ?????? ????????????")
  class RefuseFriendRequestTest {

    FriendRequestRefuseRequest request = new FriendRequestRefuseRequest(1L);

    @Test
    @DisplayName("??????")
    void success() {
      doReturn(Optional.of(user1), Optional.of(user2)).when(userRepository).findById(any());
      doReturn(true).when(friendRepository).existsById(any());
      doReturn(false).when(friendRepository).existsFriendRelationship(anyLong(), anyLong());

      friendService.refuseFriendRequest(user1.getId(), request);

      verify(friendRepository).deleteById(any());
    }

    @Nested
    @DisplayName("??????")
    class Fail {

      @Test
      @DisplayName("user??? ???????????? ?????? ?????? NotFoundException ??????")
      void invokeUserNotFoundException() {
        doThrow(new NotFoundException(ErrorCode.USER_NOT_FOUND))
            .when(userRepository).findById(any());

        assertThrows(NotFoundException.class, () -> {
          friendService.refuseFriendRequest(user1.getId(), request);
        });
      }

      @Test
      @DisplayName("??????????????? ?????? ????????? ???????????? ?????? ?????? NotFoundException ??????")
      void invokeFriendRequestNotFoundException() {
        doReturn(Optional.of(user1), Optional.of(user2)).when(userRepository).findById(any());
        doThrow(new NotFoundException(ErrorCode.FRIEND_NOT_FOUND))
            .when(friendRepository).existsById(any());

        assertThrows(NotFoundException.class, () -> {
          friendService.refuseFriendRequest(user1.getId(), request);
        });
      }

      @Test
      @DisplayName("?????? ?????? ????????? ?????? NotFoundException ??????")
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
  @DisplayName("?????? ?????? ?????? ?????? ????????????")
  class GetAllFriendRequestsTest {

    Pageable pageable = PageRequest.of(0, 20);

    @Test
    @DisplayName("??????")
    void success() {
      doReturn(true).when(userRepository).existsById(any());
      doReturn(new PageImpl<>(new ArrayList<>())).when(friendRepository)
          .findByFolloweeUser_Id(any(), any());

      friendService.getAllFriendRequests(user1.getId(), pageable);

      verify(friendRepository).findByFolloweeUser_Id(any(), any());
    }

    @Nested
    @DisplayName("??????")
    class Fail {

      @Test
      @DisplayName("user??? ???????????? ?????? ?????? NotFoundException ??????")
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
  @DisplayName("?????? ?????? ????????????")
  class GetAllFriendsTest {

    Pageable pageable = PageRequest.of(0, 20);

    @Test
    @DisplayName("??????")
    void success() {
      doReturn(true).when(userRepository).existsById(any());
      doReturn(new PageImpl<>(new ArrayList<>())).when(friendRepository)
          .findAllFriendWithPaging(any(), any());

      friendService.getAllFriends(user1.getId(), pageable);

      verify(friendRepository).findAllFriendWithPaging(any(), any());
    }

    @Nested
    @DisplayName("??????")
    class Fail {

      @Test
      @DisplayName("user??? ???????????? ?????? ?????? NotFoundException ??????")
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
  @DisplayName("?????? ????????????")
  class DeleteFriendTest {

    FriendDeleteRequest request = new FriendDeleteRequest(1L);

    @Test
    @DisplayName("??????")
    void success() {
      doReturn(true).when(friendRepository).existsFriendRelationship(anyLong(), anyLong());

      friendService.deleteFriend(user1.getId(), request);

      verify(friendRepository, times(2)).deleteById(any());
    }

    @Nested
    @DisplayName("??????")
    class Fail {

      @Test
      @DisplayName("??????????????? ???????????? ?????? ?????? NotFoundException ??????")
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