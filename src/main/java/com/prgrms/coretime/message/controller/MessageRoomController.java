package com.prgrms.coretime.message.controller;

import com.prgrms.coretime.common.ApiResponse;
import com.prgrms.coretime.message.dto.request.MessageRoomCreateRequest;
import com.prgrms.coretime.message.dto.request.MessageRoomGetRequest;
import com.prgrms.coretime.message.dto.response.MessageRoomIdResponse;
import com.prgrms.coretime.message.dto.response.MessageRoomResponse;
import com.prgrms.coretime.message.service.MessageRoomService;
import io.swagger.annotations.ApiOperation;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@RestController
@RequestMapping("/api/v1/message-rooms")
@RequiredArgsConstructor
public class MessageRoomController {

  private final MessageRoomService messageRoomService;

  @ApiOperation(value = "쪽지방 생성하기", notes = "쪽지방을 생성하는 요청입니다.")
  @PostMapping
  public ResponseEntity<ApiResponse> createMessageRoom(@RequestParam final Long userId,
      @Valid @RequestBody final MessageRoomCreateRequest request,
      RedirectAttributes redirectAttributes) throws URISyntaxException {

    Optional<Long> maybeMessageRoomId = messageRoomService.getMessageRoomId(userId,
        request.getCreatedFrom(), request.getReceiverId(), request.getIsAnonymous());
    if (maybeMessageRoomId.isPresent()) {
      URI redirectUri = new URI(
            new StringBuilder().append("/api/v1/message-rooms/").append(maybeMessageRoomId.get())
                .append("/redirect-message?userId=").append(userId).toString()
      );

      redirectAttributes.addFlashAttribute("message", request.getFirstMessage());
      HttpHeaders httpHeaders = new HttpHeaders();
      httpHeaders.setLocation(redirectUri);

      return ResponseEntity.status(HttpStatus.TEMPORARY_REDIRECT)
          .headers(httpHeaders)
          .body(new ApiResponse<>("이미 존재하는 쪽지방입니다. 쪽지방 생성 대신 쪽지 전송 요청으로 리디렉트되었습니다."));
    }

    MessageRoomIdResponse response = messageRoomService.saveMessageRoom(userId, request);

    URI redirectUri = new URI(
        new StringBuilder().append("/api/v1/message-rooms/").append(response.getMessageRoomId())
            .append("?userId=").append(userId).toString());
    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.setLocation(redirectUri);
    return ResponseEntity.status(HttpStatus.FOUND)
        .headers(httpHeaders)
        .body(new ApiResponse<>("쪽지방 생성이 완료되었습니다."));
  }

  @ApiOperation(value = "쪽지방 조회하기", notes = "쪽지방 정보와 최근 쪽지를 조회하는 요청입니다.")
  @GetMapping("/{messageRoomId}")
  public ResponseEntity<ApiResponse> getMessageRoom(@RequestParam final Long userId,
      @PathVariable("messageRoomId") Long messageRoomId) {

    MessageRoomGetRequest request = new MessageRoomGetRequest(messageRoomId);
    MessageRoomResponse response = messageRoomService.getMessageRoom(userId, request);
    return ResponseEntity.ok().body(new ApiResponse<>("쪽지방 조회가 완료되었습니다.", response));
  }

}
