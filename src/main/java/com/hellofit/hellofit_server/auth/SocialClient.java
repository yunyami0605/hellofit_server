package com.hellofit.hellofit_server.auth;

import com.hellofit.hellofit_server.auth.dto.AuthResponseDto;
import com.hellofit.hellofit_server.auth.exception.SocialAuthException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Component
@RequiredArgsConstructor
public class SocialClient {
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${kakao.client-id}")
    private String kakaoClientId;

    @Value("${kakao.redirect-uri}")
    private String kakaoRedirectUri;

    /**
     * 인가 코드로 토큰 요청 api 호출 로직
     *
     * @param code 인가 코드
     */
    public AuthResponseDto.KakaoToken getKakaoAccessToken(String code) {
        try {
            String url = "https://kauth.kakao.com/oauth/token";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            params.add("grant_type", "authorization_code");
            params.add("client_id", kakaoClientId);
            params.add("redirect_uri", kakaoRedirectUri);
            params.add("code", code);

            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

            ResponseEntity<AuthResponseDto.KakaoToken> response =
                restTemplate.postForEntity(url, request, AuthResponseDto.KakaoToken.class);

            return response.getBody();
        } catch (HttpClientErrorException | HttpServerErrorException ex) {
            throw new SocialAuthException.UserInfoRequestFail("SocialClient > getKakaoAccessToken", "카카오 응답 코드: " + ex.getStatusCode(), ex);
        } catch (ResourceAccessException ex) {
            throw new SocialAuthException.ApiUnavailable("SocialClient > getKakaoAccessToken", "카카오 서버와 통신 불가: " + ex.getMessage(), ex);
        }
    }

    public AuthResponseDto.KakaoUser getKakaoUser(String accessToken) {
        String url = "https://kapi.kakao.com/v2/user/me";

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        HttpEntity<Void> request = new HttpEntity<>(headers);

        try {
            ResponseEntity<AuthResponseDto.KakaoUser> response =
                restTemplate.exchange(url, HttpMethod.GET, request, AuthResponseDto.KakaoUser.class);

            return response.getBody();

        } catch (HttpClientErrorException ex) {
            throw new SocialAuthException.AuthFail("SocialClient > getKakaoUser", "카카오 사용자 인증 실패: " + ex.getMessage(), ex);
        } catch (HttpServerErrorException ex) {
            throw new SocialAuthException.ServerError("SocialClient > getKakaoUser", "카카오 서버 오류: " + ex.getMessage(), ex);
        } catch (ResourceAccessException ex) {
            throw new SocialAuthException.ApiUnavailable("SocialClient > getKakaoUser", "카카오 API 네트워크 오류: " + ex.getMessage(), ex);
        }
    }
}
