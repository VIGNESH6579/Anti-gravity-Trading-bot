package com.antigravity.trading.dto;

public class LoginRequest {
    private String clientId;
    private String password;
    private String apiKey;
    private String totp; // raw 6-digit code from authenticator app

    public String getClientId() { return clientId; }
    public void setClientId(String clientId) { this.clientId = clientId; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getApiKey() { return apiKey; }
    public void setApiKey(String apiKey) { this.apiKey = apiKey; }

    public String getTotp() { return totp; }
    public void setTotp(String totp) { this.totp = totp; }
}
