package com.prgrms.coretime.common.jwt.claim;


import com.auth0.jwt.JWTCreator.Builder;

public interface Claims {

  void applyToBuilder(Builder builder);

}
