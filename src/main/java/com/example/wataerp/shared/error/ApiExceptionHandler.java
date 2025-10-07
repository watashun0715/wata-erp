package com.example.wataerp.shared.error;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.*;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.NoHandlerFoundException;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@RestControllerAdvice
public class ApiExceptionHandler {

    // 共通のエラーボディ作成
    private ResponseEntity<ApiError> build(HttpStatus status, String message, HttpServletRequest req,
            List<ApiError.Violation> violations) {
        ApiError body = new ApiError(
                status.value(),
                status.getReasonPhrase(),
                message,
                req.getRequestURI(),
                OffsetDateTime.now().toString(),
                violations);
        return ResponseEntity.status(status).body(body);
    }

    // 1) 明示的に投げた ResponseStatusException
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ApiError> handleResponseStatus(ResponseStatusException ex, HttpServletRequest req) {
        HttpStatus status = HttpStatus.resolve(ex.getStatusCode().value());
        return build(status != null ? status : HttpStatus.INTERNAL_SERVER_ERROR, ex.getReason(), req, null);
    }

    // 2) @RequestBody の Bean Validation エラー
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
            HttpServletRequest req) {
        List<ApiError.Violation> violations = ex.getBindingResult().getFieldErrors().stream()
                .map(fe -> new ApiError.Violation(fe.getField(), fe.getDefaultMessage(), fe.getRejectedValue()))
                .collect(toList());
        return build(HttpStatus.BAD_REQUEST, "入力値が不正です。", req, violations);
    }

    // 3) @RequestParam / @PathVariable の検証エラー
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiError> handleConstraintViolation(ConstraintViolationException ex, HttpServletRequest req) {
        List<ApiError.Violation> violations = ex.getConstraintViolations().stream()
                .map(v -> new ApiError.Violation(v.getPropertyPath().toString(), v.getMessage(), v.getInvalidValue()))
                .collect(toList());
        return build(HttpStatus.BAD_REQUEST, "入力値が不正です。", req, violations);
    }

    // 4) JSONのパース失敗・型不一致など
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiError> handleNotReadable(HttpMessageNotReadableException ex, HttpServletRequest req) {
        List<ApiError.Violation> violations = null;

        Throwable cause = ex.getMostSpecificCause();
        if (cause instanceof InvalidFormatException ife) {
            // 例: creditLimit に "aaa" が来たケース
            String fieldPath = ife.getPath().stream()
                    .map(JsonMappingException.Reference::getFieldName)
                    .filter(Objects::nonNull)
                    .collect(Collectors.joining("."));
            String reason = "値の型が不正です。期待: " + ife.getTargetType().getSimpleName();
            Object rejected = ife.getValue();
            violations = List.of(new ApiError.Violation(fieldPath, reason, rejected));
        }
        // 他のJSON構文エラー等は共通メッセージのみ
        return build(HttpStatus.BAD_REQUEST, "リクエストボディの形式が不正です。", req, violations);
    }

    // 5) 必須クエリパラメータ不足
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiError> handleMissingParam(MissingServletRequestParameterException ex,
            HttpServletRequest req) {
        return build(HttpStatus.BAD_REQUEST, "必須パラメータが不足しています: " + ex.getParameterName(), req, null);
    }

    // 6) メソッド不一致（POSTにGETした等）
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiError> handleMethodNotSupported(HttpRequestMethodNotSupportedException ex,
            HttpServletRequest req) {
        return build(HttpStatus.METHOD_NOT_ALLOWED, "許可されていないHTTPメソッドです。", req, null);
    }

    // 7) DB制約違反（ユニーク制約など）
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiError> handleDataIntegrity(DataIntegrityViolationException ex, HttpServletRequest req) {
        return build(HttpStatus.CONFLICT, "データ整合性エラーが発生しました。", req, null);
    }

    // 8) ルート未定義（404）— 有効化設定が必要（下記参照）
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ApiError> handleNoHandler(NoHandlerFoundException ex, HttpServletRequest req) {
        return build(HttpStatus.NOT_FOUND, "エンドポイントが見つかりません。", req, null);
    }

    // 9) 最後の砦（想定外 500）
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleOthers(Exception ex, HttpServletRequest req) {
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "サーバーエラーが発生しました。", req, null);
    }
}