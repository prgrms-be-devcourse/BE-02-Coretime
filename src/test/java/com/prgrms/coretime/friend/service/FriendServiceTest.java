package com.prgrms.coretime.friend.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.prgrms.coretime.friend.domain.Friend;
import com.prgrms.coretime.friend.domain.FriendRepository;
import com.prgrms.coretime.friend.dto.request.FriendRequestSendRequest;
import com.prgrms.coretime.user.domain.TestUser;
import com.prgrms.coretime.user.domain.UserRepository;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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

}