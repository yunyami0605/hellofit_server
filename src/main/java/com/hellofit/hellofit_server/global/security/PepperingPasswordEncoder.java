package com.hellofit.hellofit_server.global.security;

import org.springframework.security.crypto.password.PasswordEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/** raw password에 서버 비밀키(pepper)로 HMAC → 그 결과를 BCrypt에 태움 */
public class PepperingPasswordEncoder implements PasswordEncoder {

    private final PasswordEncoder delegate; // BCrypt 등
    private final byte[] pepperKey;

    public PepperingPasswordEncoder(PasswordEncoder delegate, String pepper) {
        this.delegate = delegate;
        this.pepperKey = pepper.getBytes(StandardCharsets.UTF_8);
    }

    // 비밀번호에 pepper를 섞어서 바이너리화
    private String hmacSha256(String raw) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(pepperKey, "HmacSHA256"));
            byte[] out = mac.doFinal(raw.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(out);
        } catch (Exception e) {
            throw new IllegalStateException("Pepper HMAC failed", e);
        }
    }

    @Override
    public String encode(CharSequence rawPassword) {
        String transformed = hmacSha256(rawPassword.toString());

        // 인코딩
        return delegate.encode(transformed);
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        String transformed = hmacSha256(rawPassword.toString());
        return delegate.matches(transformed, encodedPassword);
    }
}
