package com.prgrms.coretime.friend.domain;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.samePropertyValuesAs;

import com.prgrms.coretime.TestConfig;
import com.prgrms.coretime.school.domain.School;
import com.prgrms.coretime.school.domain.respository.SchoolRepository;
import com.prgrms.coretime.user.domain.LocalUser;
import com.prgrms.coretime.user.domain.User;
import com.prgrms.coretime.user.domain.repository.UserRepository;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@Import(TestConfig.class)
@DataJpaTest
class FriendRepositoryTest {

  @Autowired
  private FriendRepository friendRepository;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private SchoolRepository schoolRepository;

  private School school = new School("schoolName", "school1@example.com");

  private User user1 = LocalUser.builder()
      .nickname("userOne")
      .profileImage("profileImage1")
      .email("example1@example.co.kr")
      .name("userOne")
      .school(school)
      .password("pw123$%^")
      .build();

  private User user2 = LocalUser.builder()
      .nickname("userTwo")
      .profileImage("profileImage2")
      .email("example2@example.co.kr")
      .name("userTwo")
      .school(school)
      .password("pw123$%^")
      .build();

  private User user3 = LocalUser.builder()
      .nickname("userThree")
      .profileImage("profileImage3")
      .email("example3@example.co.kr")
      .name("userThree")
      .school(school)
      .password("pw123$%^")
      .build();

  @BeforeEach
  void setUp() {
    schoolRepository.save(school);

    userRepository.save(user1);
    userRepository.save(user2);
    userRepository.save(user3);
  }

  @AfterEach
  void tearDown() {
    friendRepository.deleteAllInBatch();
    userRepository.deleteAllInBatch();
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
    userRepository.save(user3);

    friendRepository.save(new Friend(user1, user2));
    friendRepository.save(new Friend(user2, user1));
    friendRepository.save(new Friend(user3, user1));

    Pageable pageable = PageRequest.of(0, 20, Sort.by("created_at").descending());
    Page<Friend> friendPage = friendRepository.findByFolloweeUser_Id(user1.getId(), pageable);

    assertThat(friendPage.getNumberOfElements(), is(1));
  }

  @Test
  @DisplayName("친구 목록 조회")
  void findAllByFolloweeUser_IdWithPagingTest() {
    userRepository.save(user3);

    friendRepository.save(new Friend(user1, user2));
    friendRepository.save(new Friend(user2, user1));
    friendRepository.save(new Friend(user1, user3));
    friendRepository.save(new Friend(user3, user1));
    friendRepository.save(new Friend(user2, user3));

    Pageable pageable = PageRequest.of(0, 20, Sort.by("created_at").ascending());

    Page<Friend> friendPage1 = friendRepository.findAllFriendWithPaging(user1.getId(), pageable);
    Page<Friend> friendPage2 = friendRepository.findAllFriendWithPaging(user2.getId(), pageable);

    assertThat(friendPage1.getNumberOfElements(), is(2));
    assertThat(friendPage2.getNumberOfElements(), is(1));
  }
  
  @Test
  @DisplayName("친구 관계 여부 확인")
  void existsFriendRelationshipTest() {
    userRepository.save(user3);
    
    friendRepository.save(new Friend(user1, user2));
    friendRepository.save(new Friend(user2, user1));
    friendRepository.save(new Friend(user1, user3));

    boolean result1 = friendRepository.existsFriendRelationship(user1.getId(), user2.getId());
    boolean result2 = friendRepository.existsFriendRelationship(user1.getId(), user3.getId());

    assertThat(result1, is(true));
    assertThat(result2, is(false));
  }

}