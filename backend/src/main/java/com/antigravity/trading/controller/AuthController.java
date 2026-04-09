package com.antigravity.trading.controller;

import com.antigravity.trading.dto.LoginRequest;
import com.antigravity.trading.service.AngelAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AngelAuthService authService;

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody LoginRequest request) {
        boolean success = authService.login(request);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", success);
        
        if (success) {
            response.put("message", "Authenticated successfully with Angel One.");
            return ResponseEntity.ok(response);
        } else {
            response.put("message", "Authentication failed. Check credentials and TOTP.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }

    @GetMapping("/status")
    public ResponseEntity<Map<String, Boolean>> getStatus() {
        Map<String, Boolean> response = new HashMap<>();
        response.put("connected", authService.isConnected());
        return ResponseEntity.ok(response);
    }
}
