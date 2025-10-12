package com.example.wataerp.interfaces.api.v1;

import com.example.wataerp.domain.customer.dto.CustomerRequest;
import com.example.wataerp.domain.customer.dto.CustomerResponse;
import com.example.wataerp.domain.customer.service.CustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/api/v1/customers")
public class CustomerApiController {

  private final CustomerService service;

  public CustomerApiController(CustomerService service) {
    this.service = service;
  }

  @Operation(summary = "顧客の登録", description = "新しい顧客を登録します。")
  @ApiResponses({
    @ApiResponse(responseCode = "201", description = "登録に成功しました。"),
    @ApiResponse(responseCode = "400", description = "入力値が不正です。"),
    @ApiResponse(responseCode = "409", description = "顧客コードが重複しています。"),
    @ApiResponse(responseCode = "500", description = "サーバーエラーが発生しました。")
  })
  @PostMapping
  public ResponseEntity<CustomerResponse> create(@Valid @RequestBody CustomerRequest request) {

    CustomerResponse response = service.create(request);
    URI location =
        ServletUriComponentsBuilder.fromCurrentRequest()
            .path("/{code}")
            .buildAndExpand(response.getCode())
            .toUri();
    return ResponseEntity.created(location).body(response);
  }

  @Operation(summary = "全顧客一覧の取得", description = "全顧客の一覧を取得します。")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "正常に取得できました。"),
    @ApiResponse(responseCode = "500", description = "サーバーエラーが発生しました。")
  })
  @GetMapping("/all")
  public ResponseEntity<List<CustomerResponse>> getAllCustomers() {
    return ResponseEntity.ok(service.getCustomers(null));
  }

  @Operation(summary = "顧客コードによる単一顧客の取得", description = "指定した顧客コードに一致する単一顧客を取得します。")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "正常に取得できました。"),
    @ApiResponse(responseCode = "400", description = "入力値が不正です。"),
    @ApiResponse(responseCode = "500", description = "サーバーエラーが発生しました。")
  })
  @GetMapping("/{code}")
  public ResponseEntity<CustomerResponse> getCustomerByCode(@PathVariable String code) {
    return ResponseEntity.ok(service.getCustomer(code));
  }

  @Operation(summary = "顧客コードによる複数顧客の取得", description = "指定した顧客コードに一致する複数顧客を取得します。")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "正常に取得できました。"),
    @ApiResponse(responseCode = "400", description = "入力値が不正です。"),
    @ApiResponse(responseCode = "500", description = "サーバーエラーが発生しました。")
  })
  @GetMapping("/by-codes")
  public ResponseEntity<List<CustomerResponse>> getCustomersByCode(
      @Valid @RequestParam List<String> codes) {
    return ResponseEntity.ok(service.getCustomers(codes));
  }
}
