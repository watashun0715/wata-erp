package com.example.wataerp.shared.filter;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class CorrelationIdFilterTest {
  @Autowired MockMvc mvc;

  @Test
  void addsCorrelationIdWhenMissing() throws Exception {
    mvc.perform(get("/api/v1/customers/all"))
        .andExpect(status().isOk())
        .andExpect(header().exists("X-Correlation-Id"));
  }

  @Test
  void reusesCorrelationIdWhenPresent() throws Exception {
    mvc.perform(get("/api/v1/customers/all").header("X-Correlation-Id", "test-123"))
        .andExpect(status().isOk())
        .andExpect(header().string("X-Correlation-Id", "test-123"));
  }
}
