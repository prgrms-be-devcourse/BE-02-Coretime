package com.prgrms.coretime;

import com.prgrms.coretime.common.util.JwtService;
import com.prgrms.coretime.user.domain.User;
import com.prgrms.coretime.user.domain.repository.UserRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase(replace = Replace.NONE)
public class AcceptanceTest {

  @LocalServerPort
  int port;


  @Autowired
  protected UserRepository userRepository;

  @Autowired
  protected JwtService jwtService;

  protected String getAccessToken(User user) {
    User currentUser = userRepository.save(user);
    List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("USER"));

    return jwtService.createAccessToken(
        currentUser.getId(),
        currentUser.getSchool().getId(),
        currentUser.getNickname(),
        currentUser.getEmail(),
        authorities
    );
  }

}
