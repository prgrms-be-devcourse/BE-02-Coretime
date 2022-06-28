package com.prgrms.coretime.friend.controller;

import com.prgrms.coretime.common.ApiResponse;
import com.prgrms.coretime.friend.dto.request.FriendDeleteRequest;
import com.prgrms.coretime.friend.dto.request.FriendRequestAcceptRequest;
import com.prgrms.coretime.friend.dto.request.FriendRequestRefuseRequest;
import com.prgrms.coretime.friend.dto.request.FriendRequestRevokeRequest;
import com.prgrms.coretime.friend.dto.request.FriendRequestSendRequest;
import com.prgrms.coretime.friend.dto.response.FriendInfoResponse;
import com.prgrms.coretime.friend.dto.response.FriendRequestInfoResponse;
import com.prgrms.coretime.friend.service.FriendService;
import io.swagger.annotations.ApiOperation;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
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

  @ApiOperation(value = "친구 요청 보내기", notes = "친구 요청을 보내는 요청입니다.")
  @PostMapping("/requests")
  public ResponseEntity<ApiResponse> sendFriendRequest(@RequestParam final Long userId,
      @Valid @RequestBody final FriendRequestSendRequest request) {

    friendService.sendFriendRequest(userId, request);
    return ResponseEntity.ok().body(new ApiResponse<>("친구 요청 보내기가 완료되었습니다."));
  }

  @ApiOperation(value = "친구 요청 취소하기", notes = "보낸 친구 요청을 취소하는 요청입니다.")
  @DeleteMapping("/requests")
  public ResponseEntity<ApiResponse> revokeFriendRequest(@RequestParam final Long userId,
      @Valid @RequestBody final FriendRequestRevokeRequest request) {

    friendService.revokeFriendRequest(userId, request);
    return ResponseEntity.ok().body(new ApiResponse<>("친구 요청 취소가 완료되었습니다."));
  }

  @ApiOperation(value = "친구 요청 수락하기", notes = "받은 친구 요청을 수락하는 요청입니다.")
  @PostMapping("/requests/accept")
  public ResponseEntity<ApiResponse> acceptFriendRequest(@RequestParam final Long userId,
      @Valid @RequestBody final FriendRequestAcceptRequest request) {

    friendService.acceptFriendRequest(userId, request);
    return ResponseEntity.ok().body(new ApiResponse<>("친구 요청 수락이 완료되었습니다."));
  }

  @ApiOperation(value = "친구 요청 거절하기", notes = "받은 친구 요청을 거절하는 요청입니다.")
  @DeleteMapping("/requests/refuse")
  public ResponseEntity<ApiResponse> refuseFriendRequest(@RequestParam final Long userId,
      @Valid @RequestBody final FriendRequestRefuseRequest request) {

    friendService.refuseFriendRequest(userId, request);
    return ResponseEntity.ok().body(new ApiResponse<>("친구 요청 거절이 완료되었습니다."));
  }

  @ApiOperation(value = "친구 요청 받은 목록 조회하기", notes = "친구 요청 받은 목록을 조회하는 요청입니다.")
  @GetMapping("/requests")
  public ResponseEntity<ApiResponse> getAllFriendRequests(@RequestParam final Long userId,
      @RequestParam(required = false) @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) final Pageable pageable) {
    Page<FriendRequestInfoResponse> allFriendRequests = friendService.getAllFriendRequests(userId,
        pageable);
    return ResponseEntity.ok()
        .body(new ApiResponse<>("친구 요청받은 목록 조회가 완료되었습니다.", allFriendRequests));
  }

  @ApiOperation(value = "친구 목록 조회하기", notes = "친구 목록을 조회하는 요청입니다.")
  @GetMapping
  public ResponseEntity<ApiResponse> getAllFriends(@RequestParam final Long userId,
      @RequestParam(required = false) @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) final Pageable pageable) {

    Page<FriendInfoResponse> allFriends = friendService.getAllFriends(userId, pageable);
    return ResponseEntity.ok().body(new ApiResponse<>("친구 목록 조회가 완료되었습니다.", allFriends));
  }

  @ApiOperation(value = "친구 삭제하기", notes = "친구를 삭제하는 요청입니다.")
  @DeleteMapping
  public ResponseEntity<ApiResponse> deleteFriend(@RequestParam final Long userId,
      @Valid @RequestBody final FriendDeleteRequest request) {

    friendService.deleteFriend(userId, request);
    return ResponseEntity.ok().body(new ApiResponse<>("친구 삭제가 완료되었습니다."));
  }

}
