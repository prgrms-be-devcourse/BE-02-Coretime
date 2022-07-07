package com.prgrms.coretime.message.controller;

import com.prgrms.coretime.common.ApiResponse;
import com.prgrms.coretime.common.jwt.JwtPrincipal;
import com.prgrms.coretime.message.dto.request.MessageSendRequest;
import com.prgrms.coretime.message.dto.response.MessageResponse;
import com.prgrms.coretime.message.service.MessageService;
import io.swagger.annotations.ApiOperation;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/message-rooms")
@RequiredArgsConstructor
public class MessageController {
  private final MessageService messageService;

  @ApiOperation(value = "쪽지 전송하기", notes = "쪽지를 전송하는 요청입니다.")
  @PostMapping("/{messageRoomId}/messages")
  public ResponseEntity<ApiResponse> sendMessage(@AuthenticationPrincipal JwtPrincipal principal, @PathVariable("messageRoomId") Long messageRoomId,
      @Valid @RequestBody final MessageSendRequest request) {

    messageService.sendMessage(principal.userId, messageRoomId, request);
    return ResponseEntity.ok().body(new ApiResponse<>("쪽지 전송이 완료되었습니다."));
  }

  @ApiOperation(value = "리디렉트된 쪽지 전송하기", notes = "쪽지방 생성에서 리디렉트되어 쪽지를 전송하는 요청입니다.")
  @PostMapping("/{messageRoomId}/redirect-message")
  public ResponseEntity<ApiResponse> sendRedirectedMessage(@AuthenticationPrincipal JwtPrincipal principal,
      @PathVariable("messageRoomId") Long messageRoomId,
      @ModelAttribute("message") String message) {

    MessageSendRequest request = new MessageSendRequest(message);
    messageService.sendMessage(principal.userId, messageRoomId, request);
    return ResponseEntity.ok().body(new ApiResponse<>("쪽지 전송이 완료되었습니다."));
  }

  @ApiOperation(value = "쪽지 다건 조회하기", notes = "쪽지를 다건 조회하는 요청입니다.")
  @GetMapping("/{messageRoomId}/messages")
  public ResponseEntity<ApiResponse> getAllMessages(@AuthenticationPrincipal JwtPrincipal principal, @PathVariable("messageRoomId") Long messageRoomId,
      @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) final Pageable pageable) {

    Page<MessageResponse> allMessages = messageService.getAllMessages(principal.userId, messageRoomId,
        pageable);
    return ResponseEntity.ok().body(new ApiResponse<>("쪽지 다건 조회가 완료되었습니다.", allMessages));
  }
}
