package com.prgrms.coretime.friend.controller;

import com.prgrms.coretime.common.ApiResponse;
import com.prgrms.coretime.common.jwt.JwtPrincipal;
import com.prgrms.coretime.friend.dto.request.FriendDeleteRequest;
import com.prgrms.coretime.friend.dto.request.FriendRequestAcceptRequest;
import com.prgrms.coretime.friend.dto.request.FriendRequestRefuseRequest;
import com.prgrms.coretime.friend.dto.request.FriendRequestRevokeRequest;
import com.prgrms.coretime.friend.dto.request.FriendRequestSendRequest;
import com.prgrms.coretime.friend.dto.response.FriendInfoResponse;
import com.prgrms.coretime.friend.dto.response.FriendRequestInfoResponse;
import com.prgrms.coretime.friend.service.FriendService;
import com.prgrms.coretime.timetable.domain.Semester;
import com.prgrms.coretime.timetable.dto.response.FriendDefaultTimetableInfo;
import com.prgrms.coretime.timetable.dto.response.LectureInfo;
import com.prgrms.coretime.timetable.service.TimetableService;
import io.swagger.annotations.ApiOperation;
import java.util.List;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/friends")
@RequiredArgsConstructor
public class FriendController {

  private final FriendService friendService;
  private final TimetableService timetableService;

  @ApiOperation(value = "친구 요청 보내기", notes = "친구 요청을 보내는 요청입니다.")
  @PostMapping("/requests")
  public ResponseEntity<ApiResponse> sendFriendRequest(@AuthenticationPrincipal JwtPrincipal principal,
      @Valid @RequestBody final FriendRequestSendRequest request) {

    friendService.sendFriendRequest(principal.userId, request);
    return ResponseEntity.ok().body(new ApiResponse<>("친구 요청 보내기가 완료되었습니다."));
  }

  @ApiOperation(value = "친구 요청 취소하기", notes = "보낸 친구 요청을 취소하는 요청입니다.")
  @DeleteMapping("/requests")
  public ResponseEntity<ApiResponse> revokeFriendRequest(@AuthenticationPrincipal JwtPrincipal principal,
      @Valid @RequestBody final FriendRequestRevokeRequest request) {

    friendService.revokeFriendRequest(principal.userId, request);
    return ResponseEntity.ok().body(new ApiResponse<>("친구 요청 취소가 완료되었습니다."));
  }

  @ApiOperation(value = "친구 요청 수락하기", notes = "받은 친구 요청을 수락하는 요청입니다.")
  @PostMapping("/requests/accept")
  public ResponseEntity<ApiResponse> acceptFriendRequest(@AuthenticationPrincipal JwtPrincipal principal,
      @Valid @RequestBody final FriendRequestAcceptRequest request) {

    friendService.acceptFriendRequest(principal.userId, request);
    return ResponseEntity.ok().body(new ApiResponse<>("친구 요청 수락이 완료되었습니다."));
  }

  @ApiOperation(value = "친구 요청 거절하기", notes = "받은 친구 요청을 거절하는 요청입니다.")
  @DeleteMapping("/requests/refuse")
  public ResponseEntity<ApiResponse> refuseFriendRequest(@AuthenticationPrincipal JwtPrincipal principal,
      @Valid @RequestBody final FriendRequestRefuseRequest request) {

    friendService.refuseFriendRequest(principal.userId, request);
    return ResponseEntity.ok().body(new ApiResponse<>("친구 요청 거절이 완료되었습니다."));
  }

  @ApiOperation(value = "친구 요청 받은 목록 조회하기", notes = "친구 요청 받은 목록을 조회하는 요청입니다.")
  @GetMapping("/requests")
  public ResponseEntity<ApiResponse> getAllFriendRequests(@AuthenticationPrincipal JwtPrincipal principal,
      @PageableDefault(size = 20, sort = "created_at", direction = Sort.Direction.DESC) final Pageable pageable) {
    Page<FriendRequestInfoResponse> allFriendRequests = friendService.getAllFriendRequests(principal.userId,
        pageable);
    return ResponseEntity.ok()
        .body(new ApiResponse<>("친구 요청받은 목록 조회가 완료되었습니다.", allFriendRequests));
  }

  @ApiOperation(value = "친구 목록 조회하기", notes = "친구 목록을 조회하는 요청입니다.")
  @GetMapping
  public ResponseEntity<ApiResponse> getAllFriends(@AuthenticationPrincipal JwtPrincipal principal,
      @PageableDefault(size = 20, sort = "created_at", direction = Sort.Direction.DESC) final Pageable pageable) {

    Page<FriendInfoResponse> allFriends = friendService.getAllFriends(principal.userId, pageable);
    return ResponseEntity.ok().body(new ApiResponse<>("친구 목록 조회가 완료되었습니다.", allFriends));
  }

  @ApiOperation(value = "친구 삭제하기", notes = "친구를 삭제하는 요청입니다.")
  @DeleteMapping
  public ResponseEntity<ApiResponse> deleteFriend(@AuthenticationPrincipal JwtPrincipal principal,
      @Valid @RequestBody final FriendDeleteRequest request) {

    friendService.deleteFriend(principal.userId, request);
    return ResponseEntity.ok().body(new ApiResponse<>("친구 삭제가 완료되었습니다."));
  }

  @ApiOperation(value = "친구 시간표 목록 조회하기", notes = "친구의 기본 시간표 목록을 조회하는 요청입니다.")
  @GetMapping("/{friendId}")
  public ResponseEntity<ApiResponse> getFriendTimetableInfos(
      @AuthenticationPrincipal JwtPrincipal principal,
      @PathVariable("friendId") final Long friendId) {

    List<FriendDefaultTimetableInfo> response = timetableService.getFriendDefaultTimetableInfos(
        principal.userId, friendId);
    return ResponseEntity.ok().body(new ApiResponse<>("친구의 기본 시간표 목록 조회가 완료되었습니다.", response));
  }

  @ApiOperation(value = "친구 시간표 단건 조회하기", notes = "친구의 시간표를 조회하는 요청입니다.")
  @GetMapping("/{friendId}/timetables")
  public ResponseEntity<ApiResponse> getFriendTimetable(
      @AuthenticationPrincipal JwtPrincipal principal,
      @PathVariable("friendId") final Long friendId,
      @RequestParam final Integer year, @RequestParam final String semester) {

    List<LectureInfo> response = timetableService.getDefaultTimetableOfFriend(
        principal.userId, friendId, year, Semester.valueOf(semester));
    return ResponseEntity.ok().body(new ApiResponse<>("친구의 시간표 조회가 완료되었습니다.", response));
  }
}
