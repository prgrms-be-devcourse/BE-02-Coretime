package com.prgrms.coretime.friend.controller;

import com.prgrms.coretime.common.ApiResponse;
import com.prgrms.coretime.friend.dto.request.FriendRequestRevokeRequest;
import com.prgrms.coretime.friend.dto.request.FriendRequestSendRequest;
import com.prgrms.coretime.friend.service.FriendService;
import io.swagger.annotations.ApiOperation;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
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

}
