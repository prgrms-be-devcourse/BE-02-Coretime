package com.prgrms.coretime.user.controller;

import static org.springframework.http.HttpStatus.*;

import com.prgrms.coretime.common.ApiResponse;
import com.prgrms.coretime.common.jwt.JwtAuthenticationToken;
import com.prgrms.coretime.common.jwt.JwtPrincipal;
import com.prgrms.coretime.common.jwt.claim.AccessClaim;
import com.prgrms.coretime.common.util.JwtService;
import com.prgrms.coretime.user.domain.LocalUser;
import com.prgrms.coretime.user.domain.User;
import com.prgrms.coretime.user.dto.request.UserLocalLoginRequest;
import com.prgrms.coretime.user.dto.request.UserPasswordChangeRequest;
import com.prgrms.coretime.user.dto.request.UserRegisterRequest;
import com.prgrms.coretime.user.dto.response.LoginResponse;
import com.prgrms.coretime.user.dto.response.RegisterResponse;
import com.prgrms.coretime.user.dto.response.ValidCheckResponse;
import com.prgrms.coretime.user.service.UserService;
import java.util.List;
import javax.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/users")
public class UserController {

  private final UserService userService;

  private final JwtService jwtService;

  private final AuthenticationManager authenticationManager;

  public UserController(UserService userService,
      JwtService jwtService,
      AuthenticationManager authenticationManager) {
    this.userService = userService;
    this.jwtService = jwtService;
    this.authenticationManager = authenticationManager;
  }

  @PostMapping("/local/login")
  public ResponseEntity<ApiResponse<LoginResponse>> localLogin(@RequestBody UserLocalLoginRequest request) {
    JwtAuthenticationToken authToken = new JwtAuthenticationToken(request.getEmail(),
        request.getPassword());
    Authentication authentication = authenticationManager.authenticate(authToken);
    String refreshToken = (String) authentication.getDetails();
    JwtPrincipal principal = (JwtPrincipal) authentication.getPrincipal();
    return ResponseEntity.ok(new ApiResponse<>("로그인 성공", new LoginResponse(principal.accessToken, refreshToken)));
  }

  @PostMapping("/oauth/login")
  public ResponseEntity<ApiResponse<LoginResponse>> oauthLogin(@RequestBody UserLocalLoginRequest request) {
    return null;
  }

  @GetMapping("/logout")
  public ResponseEntity<ApiResponse<Object>> logout(@RequestParam("accessToken") String accessToken) {
    jwtService.logout(accessToken);
    return ResponseEntity.ok(new ApiResponse<>("로그아웃이 완료되었습니다."));
  }

  /* TODO: 블랙아웃 처리 */
  @GetMapping("/reissue")
  public ResponseEntity<ApiResponse<LoginResponse>> reIssueAccessToken(@RequestParam("email") String email, @RequestParam("refreshToken") String refreshToken) {
    User user = userService.findByEmail(email);
    jwtService.checkRefreshToken(email, refreshToken);
    List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("USER"));
    String newAccessToken = jwtService.createAccessToken(user.getId(), user.getSchool().getId(), user.getNickname(), user.getEmail(), authorities);
    return ResponseEntity.ok(new ApiResponse<>("토큰이 재발급되었습니다.", new LoginResponse(newAccessToken, refreshToken)));
  }

  @PostMapping("/local/register")
  public ResponseEntity<ApiResponse<RegisterResponse>> register(@RequestBody @Valid
      UserRegisterRequest request) {
    User newUser = userService.register(request);
    return ResponseEntity.status(CREATED).body(new ApiResponse<>("회원가입 성공하였습니다.", RegisterResponse.from(newUser)));
  }

  @GetMapping("/check")
  public ResponseEntity<ApiResponse<ValidCheckResponse>> checkNicknameValid(@RequestParam("nickname") String nickname) {
    ValidCheckResponse response = userService.checkNicknameUnique(nickname) ? new ValidCheckResponse(false) : new ValidCheckResponse(true);
    return ResponseEntity.ok(new ApiResponse<>("중복검사가 완료되었습니다.", response));
  }

  /*TOOD: entity 밖으로 나와도 되는지 고민*/
  @PatchMapping("/password/change")
  public ResponseEntity<ApiResponse<Object>> changePassword(@AuthenticationPrincipal JwtPrincipal principal, @RequestBody @Valid UserPasswordChangeRequest request) {
    userService.changePassword(principal.userId, request);
    return ResponseEntity.ok(new ApiResponse<>("비밀번호 변경이 완료되었습니다."));
  }

  @PatchMapping("/quit")
  public ResponseEntity<ApiResponse<Object>> quit(@AuthenticationPrincipal JwtPrincipal principal) {
    userService.quit(principal.userId);
    return ResponseEntity.ok(new ApiResponse<>("회원 탈퇴가 완료되었습니다."));
  }
}
