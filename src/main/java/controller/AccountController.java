package controller;

import dto.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/accounts")
@Tag(name = "Account Management", description = "APIs for managing accounts")
public class AccountController {
    
    @Operation(summary = "Get account info", description = "Returns basic account information")
    @GetMapping("/info")
    public ResponseEntity<ApiResponse<String>> getAccountInfo() {
        return ResponseEntity.ok(ApiResponse.of(HttpStatus.OK.value(), "Account information retrieved", "Account information endpoint"));
    }


}

