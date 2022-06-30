package com.prgrms.coretime.friend.domain;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.samePropertyValuesAs;

import com.prgrms.coretime.user.domain.TestUser;
import com.prgrms.coretime.user.domain.TestUserRepository;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@DataJpaTest
class FriendRepositoryTest {

  @Autowired
  private FriendRepository friendRepository;

  @Autowired
  private TestUserRepository testUserRepository;

  TestUser user1 = new TestUser("1111");
  TestUser user2 = new TestUser("2222");

  @BeforeEach
  void setUp() {
    testUserRepository.save(user1);
    testUserRepository.save(user2);
  }

  @AfterEach
  void tearDown() {
    friendRepository.deleteAllInBatch();
    testUserRepository.deleteAllInBatch();
  }

  @Test
  @DisplayName("Friend 저장")
  void saveFriendTest() {
    Friend friend = new Friend(user1, user2);

    Friend savedFriend = friendRepository.save(friend);

    Optional<Friend> maybeFriend = friendRepository.findById(
        new FriendId(user1.getId(), user2.getId()));
    assertThat(maybeFriend.isPresent(), is(true));
    assertThat(maybeFriend.get(), samePropertyValuesAs(savedFriend));
  }

  @Test
  @DisplayName("Friend 삭제")
  void deleteFriendTest() {
    Friend friend = new Friend(user1, user2);
    Friend savedFriend = friendRepository.save(friend);

    friendRepository.delete(savedFriend);

    Optional<Friend> maybeFriend = friendRepository.findById(
        new FriendId(user1.getId(), user2.getId()));
    assertThat(maybeFriend.isPresent(), is(false));
  }

  @Test
  @DisplayName("친구 요청 조회")
  void findByFolloweeUserTest() {
    TestUser user3 = new TestUser("3333");
    testUserRepository.save(user3);

    friendRepository.save(new Friend(user1, user2));
    friendRepository.save(new Friend(user2, user1));
    friendRepository.save(new Friend(user3, user1));
    Pageable pageable = PageRequest.of(0, 20);

    Page<Friend> friendPage = friendRepository.findByFolloweeUser_Id(user1.getId(), pageable);

    assertThat(friendPage.getNumberOfElements(), is(1));
  }

  @Test
  @DisplayName("친구 목록 조회")
  void findAllByFolloweeUser_IdWithPagingTest() {
    TestUser user3 = new TestUser("3333");
    testUserRepository.save(user3);

    friendRepository.save(new Friend(user1, user2));
    friendRepository.save(new Friend(user2, user1));
    friendRepository.save(new Friend(user1, user3));
    friendRepository.save(new Friend(user3, user1));
    friendRepository.save(new Friend(user2, user3));
    Pageable pageable = PageRequest.of(0, 20);

    Page<Friend> friendPage1 = friendRepository.findAllFriendWithPaging(user1.getId(), pageable);
    Page<Friend> friendPage2 = friendRepository.findAllFriendWithPaging(user2.getId(), pageable);

    assertThat(friendPage1.getNumberOfElements(), is(2));
    assertThat(friendPage2.getNumberOfElements(), is(1));
  }
  
  @Test
  @DisplayName("친구 관계 여부 확인")
  void existsFriendRelationshipTest() {
    TestUser user3 = new TestUser("3333");
    testUserRepository.save(user3);
    
    friendRepository.save(new Friend(user1, user2));
    friendRepository.save(new Friend(user2, user1));
    friendRepository.save(new Friend(user1, user3));

    boolean result1 = friendRepository.existsFriendRelationship(user1.getId(), user2.getId());
    boolean result2 = friendRepository.existsFriendRelationship(user1.getId(), user3.getId());

    assertThat(result1, is(true));
    assertThat(result2, is(false));
  }

}