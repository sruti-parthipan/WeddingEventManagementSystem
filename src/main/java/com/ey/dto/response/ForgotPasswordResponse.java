

package com.ey.dto.response;

public class ForgotPasswordResponse {
    private String token; // reset token

    public ForgotPasswordResponse() {}
    public ForgotPasswordResponse(String token) { this.token = token; }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
}


