package com.prgrms.coretime.common.aop;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import javax.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.util.ContentCachingRequestWrapper;

@Slf4j
@Aspect
@Component
@Order(2)
@RequiredArgsConstructor
public class LoggingAspect {

  private final ObjectMapper objectMapper;

  private static final String LOG_FORMAT = "REQUEST : {}";
  private static final String ERROR_LOG_FORMAT = "API 로그를 생성하는 과정에서 문제가 발생하였습니다.";

  @Around("execution(* com.prgrms.coretime..controller.*Controller.*(..))")
  public Object printLog(ProceedingJoinPoint joinPoint) throws Throwable {
    HttpServletRequest request = (
        (ServletRequestAttributes) Objects.requireNonNull(
            RequestContextHolder.getRequestAttributes())
    ).getRequest();

    log.info(LOG_FORMAT, geMetaData(request));
    return joinPoint.proceed();
  }

  private String geMetaData(HttpServletRequest request) throws JsonProcessingException {
    Map<String, Object> metaDataMap = new LinkedHashMap<>();

    try {
      metaDataMap.put("ip", getClientIp(request));
      metaDataMap.put("method", request.getMethod());
      metaDataMap.put("uri", request.getRequestURI());
      metaDataMap.put("params", getParams(request));
      metaDataMap.put("body", getBody(request));
      metaDataMap.put("time", new Date());
    } catch (Exception e) {
      log.error(ERROR_LOG_FORMAT);
    }
    return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(metaDataMap);
  }

  private String getClientIp(HttpServletRequest request) throws JsonProcessingException {
    String ip = request.getHeader("X-Forwarded-For");

    if (!StringUtils.hasText(ip) || "unknown".equalsIgnoreCase(ip)) {
      ip = request.getHeader("Proxy-Client-IP");
    }

    if (!StringUtils.hasText(ip) || "unknown".equalsIgnoreCase(ip)) {
      ip = request.getHeader("WL-Proxy-Client-IP");
    }

    if (!StringUtils.hasText(ip) || "unknown".equalsIgnoreCase(ip)) {
      ip = request.getHeader("HTTP-Client-IP");
    }

    if (!StringUtils.hasText(ip) || "unknown".equalsIgnoreCase(ip)) {
      ip = request.getHeader("HTTP_X_FORWARDED_FOR");
    }

    if (!StringUtils.hasText(ip) || "unknown".equalsIgnoreCase(ip)) {
      ip = request.getRemoteAddr();
    }

    return ip;
  }

  private String getParams(HttpServletRequest request) throws JsonProcessingException {
    return objectMapper.writeValueAsString(request.getParameterMap());
  }

  private JsonNode getBody(HttpServletRequest request) throws IOException {
    ContentCachingRequestWrapper wrapper = (ContentCachingRequestWrapper) request;
    return objectMapper.readTree(wrapper.getContentAsByteArray());
  }
}
