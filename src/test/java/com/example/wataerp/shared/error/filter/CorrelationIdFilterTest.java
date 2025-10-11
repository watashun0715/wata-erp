import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.example.wataerp.shared.filter.CorrelationIdFilter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.*;

class CorrelationIdFilterStandaloneTest {

  MockMvc mvc;

  @RestController
  @RequestMapping("/test")
  static class PingController {
    @GetMapping(value = "/ping", produces = MediaType.TEXT_PLAIN_VALUE)
    String ping() {
      return "ok";
    }
  }

  @BeforeEach
  void setUp() {
    mvc =
        MockMvcBuilders.standaloneSetup(new PingController())
            .addFilters(new CorrelationIdFilter()) // ← フィルタを積む
            .build();
  }

  @Test
  void addsCorrelationIdWhenMissing() throws Exception {
    mvc.perform(get("/test/ping"))
        .andExpect(status().isOk())
        .andExpect(header().exists(CorrelationIdFilter.HEADER_NAME));
  }

  @Test
  void reusesCorrelationIdWhenPresent() throws Exception {
    mvc.perform(get("/test/ping").header(CorrelationIdFilter.HEADER_NAME, "cid-123"))
        .andExpect(status().isOk())
        .andExpect(header().string(CorrelationIdFilter.HEADER_NAME, "cid-123"));
  }
}
