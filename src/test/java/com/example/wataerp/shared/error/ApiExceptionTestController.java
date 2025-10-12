package com.example.wataerp.shared.error;

import com.example.wataerp.shared.error.ApiExceptionHandlerTest.AmountReq;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/test")
@Validated
public class ApiExceptionTestController {
  @GetMapping("/ping")
  String ping() {
    return "ok";
  }

  // JSONパース/型不一致 → HttpMessageNotReadableException
  @PostMapping(value = "/amount", consumes = MediaType.APPLICATION_JSON_VALUE)
  void amount(@Valid @RequestBody AmountReq req) {}

  // @RequestParam の Bean Validation → ConstraintViolationException
  @GetMapping("/min")
  void min(@RequestParam @Min(1) int n) {}

  // 必須パラメータ不足 → MissingServletRequestParameterException
  @GetMapping("/need")
  void need(@RequestParam String q) {}

  // POST のみ → GET すると MethodNotSupported
  @PostMapping("/only-post")
  void onlyPost() {}

  // DataIntegrityViolationException を投げるエンドポイント
  @GetMapping("/dup")
  void dup() {
    throw new DataIntegrityViolationException("duplicate");
  }

  // ResponseStatusException を投げるエンドポイント
  @GetMapping("/rsp")
  void rsp() {
    throw new ResponseStatusException(HttpStatus.CONFLICT, "conflict!");
  }

  // 想定外エラー
  @GetMapping("/boom")
  void boom() {
    throw new RuntimeException("boom");
  }

  @GetMapping("/rsp-no-reason")
  void rspNoReason() {
    throw new ResponseStatusException(HttpStatus.CONFLICT); // reason無し
  }
}
