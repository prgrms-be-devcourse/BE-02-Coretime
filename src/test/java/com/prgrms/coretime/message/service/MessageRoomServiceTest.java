package com.prgrms.coretime.message.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import com.prgrms.coretime.message.domain.MessageRoomRepository;
import com.prgrms.coretime.post.domain.Post;
import com.prgrms.coretime.post.domain.PostRepository;
import com.prgrms.coretime.user.domain.TestUser;
import com.prgrms.coretime.user.domain.TestUserRepository;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MessageRoomServiceTest {

  @InjectMocks
  private MessageRoomService messageRoomService;

  @Mock
  private MessageRoomRepository messageRoomRepository;

  @Mock
  private TestUserRepository testUserRepository;

  @Mock
  private PostRepository postRepository;

  private TestUser user1 = mock(TestUser.class);
  private TestUser user2 = mock(TestUser.class);

  private Post post = mock(Post.class);

  @Test
  @DisplayName("쪽지방 id 조회하기: 성공")
  void getMessageRoomIdSuccessTest() {
    doReturn(Optional.of(user1)).doReturn(Optional.of(user2)).when(testUserRepository)
        .findById(anyLong());
    doReturn(Optional.of(post)).when(postRepository).findById(anyLong());

    messageRoomService.getMessageRoomId(user1.getId(), post.getId(), user2.getId(),
        post.getIsAnonymous());

    verify(messageRoomRepository).findIdByInfo(any(), any(), any(), any());
  }

}