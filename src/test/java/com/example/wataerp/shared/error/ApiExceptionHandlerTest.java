package com.example.wataerp.shared.error;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(
    controllers = ApiExceptionTestController.class,
    excludeAutoConfiguration = {
      SecurityAutoConfiguration.class,
      SecurityFilterAutoConfiguration.class
    })
@AutoConfigureMockMvc(/* addFilters = true */ )
@TestPropertySource(
    properties = {
      "spring.mvc.throw-exception-if-no-handler-found=true",
      "spring.web.resources.add-mappings=false"
    })
@Import({
  com.example.wataerp.shared.error.ApiExceptionHandler.class,
  com.example.wataerp.shared.filter.CorrelationIdFilter.class // ← これでFilterもDI
})
class ApiExceptionHandlerTest {
  @Autowired MockMvc mvc;

  /** 404エラー原因切り分け用最小テスト */
  @Test
  void minTest() throws Exception {
    mvc.perform(get("/test/ping")).andExpect(status().isOk());
  }

  /** リクエストBodyの型不一致 → 400 + violations */
  @Test
  void badRequestIncludesCorrelationId() throws Exception {
    String badJson = """
        {"amount":"aaa"}
        """;
    mvc.perform(post("/test/amount").contentType(MediaType.APPLICATION_JSON).content(badJson))
        .andExpect(status().isBadRequest())
        .andExpect(header().exists("X-Correlation-Id"))
        .andExpect(jsonPath("$.message", containsString("リクエストボディの形式が不正です")))
        .andExpect(jsonPath("$.violations[0].field", containsString("amount")))
        .andExpect(jsonPath("$.violations[0].reason", containsString("型")));
  }

  /**
   * @RequestParam の Bean Validation 失敗 → 400
   */
  @Test
  void handleConstraintViolation_returns400() throws Exception {
    mvc.perform(get("/test/min").param("n", "0"))
        .andExpect(status().isBadRequest())
        .andExpect(header().exists("X-Correlation-Id"))
        .andExpect(jsonPath("$.message", containsString("入力値が不正です")));
  }

  /** サポートされないメソッド → 405 */
  @Test
  void handleMethodNotSupported_returns405() throws Exception {
    mvc.perform(get("/test/only-post"))
        .andExpect(status().isMethodNotAllowed())
        .andExpect(header().exists("X-Correlation-Id"))
        .andExpect(jsonPath("$.message", containsString("許可されていないHTTPメソッド")));
  }

  /** 必須パラメータ不足 → 400 */
  @Test
  void handleMissingParam_returns400() throws Exception {
    mvc.perform(get("/test/need"))
        .andExpect(status().isBadRequest())
        .andExpect(header().exists("X-Correlation-Id"))
        .andExpect(jsonPath("$.message", containsString("必須パラメータが不足しています")));
  }

  /** DB制約違反（ユニーク等） → 409 */
  @Test
  void handleDataIntegrity_returns409() throws Exception {
    mvc.perform(get("/test/dup"))
        .andExpect(status().isConflict())
        .andExpect(header().exists("X-Correlation-Id"))
        .andExpect(jsonPath("$.message", containsString("データ整合性エラー")));
  }

  /** ResponseStatusException → 指定ステータス（ここでは409） */
  @Test
  void handleResponseStatus_returnsStatusFromException() throws Exception {
    mvc.perform(get("/test/rsp"))
        .andExpect(status().isConflict())
        .andExpect(header().exists("X-Correlation-Id"))
        .andExpect(jsonPath("$.message", containsString("conflict")));
  }

  /** 想定外例外 → 500 */
  @Test
  void handleOthers_returns500() throws Exception {
    mvc.perform(get("/test/boom"))
        .andExpect(status().isInternalServerError())
        .andExpect(header().exists("X-Correlation-Id"))
        .andExpect(jsonPath("$.message", containsString("サーバーエラー")));
  }

  /** 存在しないURL → NoHandlerFoundException → 404 */
  @Test
  void handleNoHandler_returns404() throws Exception {
    mvc.perform(get("/no-such-path"))
        .andExpect(status().isNotFound())
        .andExpect(header().exists("X-Correlation-Id"))
        .andExpect(jsonPath("$.message", containsString("エンドポイントが見つかりません")));
  }

  @Test
  void handleResponseStatus_noReason_stillUsesStatus() throws Exception {
    mvc.perform(get("/test/rsp-no-reason")) // controller で new ResponseStatusException(CONFLICT)
        .andExpect(status().isConflict())
        .andExpect(header().exists("X-Correlation-Id"));
  }

  // --- リクエスト model ---
  public static class AmountReq {
    @NotNull BigDecimal amount;

    public BigDecimal getAmount() {
      return amount;
    }

    public void setAmount(BigDecimal amount) {
      this.amount = amount;
    }
  }
}
