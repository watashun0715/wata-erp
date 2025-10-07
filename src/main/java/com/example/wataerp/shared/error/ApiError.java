package com.example.wataerp.shared.error;

import java.time.OffsetDateTime;
import java.util.List;

public class ApiError {
    public int status; // 404 など
    public String error; // "Not Found"
    public String message; // エラー詳細
    public String path; // 例: /api/v1/customers/C999
    public String timestamp; // ISO8601
    public List<Violation> violations; // バリデーション詳細（任意）

    public ApiError(int status, String error, String message, String path, String timestamp,
            List<Violation> violations) {
        this.status = status;
        this.error = error;
        this.message = message;
        this.path = path;
        this.timestamp = timestamp;
        this.violations = violations;
    }

    public static class Violation {
        public String field; // 例: name
        public String reason; // 例: must not be blank
        public Object rejectedValue;// 例: ""

        public Violation(String field, String reason, Object rejectedValue) {
            this.field = field;
            this.reason = reason;
            this.rejectedValue = rejectedValue;
        }
    }
}