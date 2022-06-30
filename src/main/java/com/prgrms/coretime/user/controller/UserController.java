package com.prgrms.coretime.user.controller;

import com.prgrms.coretime.common.ApiResponse;
import com.prgrms.coretime.common.jwt.JwtAuthenticationToken;
import com.prgrms.coretime.common.jwt.JwtPrincipal;
import com.prgrms.coretime.user.dto.request.UserLocalLoginRequest;
import com.prgrms.coretime.user.dto.response.LoginResponse;
import com.prgrms.coretime.user.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/users")
public class UserController {

  private final UserService userService;

  private final AuthenticationManager authenticationManager;

  public UserController(UserService userService,
      AuthenticationManager authenticationManager) {
    this.userService = userService;
    this.authenticationManager = authenticationManager;
  }

  @PostMapping("/local/login")
  public ResponseEntity<ApiResponse<LoginResponse>> localLogin(@RequestBody UserLocalLoginRequest request) {
    JwtAuthenticationToken authToken = new JwtAuthenticationToken(request.getEmail(),
        request.getPassword());
    Authentication authentication = authenticationManager.authenticate(authToken);
    String refreshToken = (String) authentication.getDetails();
    JwtPrincipal principal = (JwtPrincipal) authentication.getPrincipal();
    return ResponseEntity.ok(new ApiResponse<>("로그인 성공", new LoginResponse(principal.accessToken, refreshToken, true)));
  }

  @PostMapping("/oauth/login")
  public ResponseEntity<ApiResponse<LoginResponse>> oauthLogin(@RequestBody UserLocalLoginRequest request) {
    return null;
  }

  @GetMapping("/principal")
  public ResponseEntity<ApiResponse<JwtPrincipal>> getPrincipalInfo(@AuthenticationPrincipal JwtPrincipal principal) {
    /*
    * principal.email
    * principal.schoolId
    * principal.userId
    * principal.nickname
    * principal.token
    * */
    return ResponseEntity.ok(new ApiResponse<>("현재 로그인한 사용자입니다.", principal));
  }
}
