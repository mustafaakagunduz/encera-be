package com.pappgroup.pappapp.controller;

import com.pappgroup.pappapp.dto.request.OAuthCompleteRequest;
import com.pappgroup.pappapp.dto.response.AuthResponse;
import com.pappgroup.pappapp.dto.response.OAuthResponse;
import com.pappgroup.pappapp.service.OAuth2Service;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/oauth2")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Slf4j
public class OAuth2Controller {

    private final OAuth2Service oAuth2Service;

    @Value("${app.frontend.url}")
    private String frontendUrl;

    @GetMapping({"/callback/{provider}", "/login/oauth2/code/{provider}"})
    public void oauthCallback(
            @PathVariable String provider,
            @AuthenticationPrincipal OAuth2User oauth2User,
            HttpServletRequest request,
            HttpServletResponse response,
            HttpSession session) throws IOException {

        log.info("OAuth callback received for provider: {}", provider);
        log.info("OAuth2User: {}", oauth2User != null ? oauth2User.getAttributes() : "null");

        if (oauth2User == null) {
            response.sendRedirect(frontendUrl + "/authentication?error=oauth_failed");
            return;
        }

        try {
            // OAuth kullanıcısını işle
            OAuthResponse oauthResponse = oAuth2Service.processOAuthUser(oauth2User, provider);

            if (oauthResponse.isNeedsCompletion()) {
                // Kullanıcı bilgileri eksik, form sayfasına yönlendir
                session.setAttribute("oauth2User", oauth2User);
                session.setAttribute("provider", provider);
                session.setAttribute("temporaryToken", oauthResponse.getTemporaryToken());

                response.sendRedirect(frontendUrl + "/authentication?mode=oauth-complete&token=" +
                    oauthResponse.getTemporaryToken() + "&email=" + oauthResponse.getEmail());
            } else {
                // Giriş tamamlandı, token ile anasayfaya yönlendir
                response.sendRedirect(frontendUrl + "?token=" + oauthResponse.getToken() +
                    "&refreshToken=" + oauthResponse.getRefreshToken());
            }

        } catch (Exception e) {
            log.error("OAuth callback error: ", e);
            response.sendRedirect(frontendUrl + "/authentication?error=oauth_failed");
        }
    }

    @PostMapping("/complete")
    public ResponseEntity<?> completeOAuthRegistration(
            @Valid @RequestBody OAuthCompleteRequest request,
            HttpSession session) {

        try {
            OAuth2User oauth2User = (OAuth2User) session.getAttribute("oauth2User");
            String provider = (String) session.getAttribute("provider");
            String storedToken = (String) session.getAttribute("temporaryToken");

            if (oauth2User == null || provider == null ||
                !request.getOauthToken().equals(storedToken)) {
                return ResponseEntity.badRequest().body("Geçersiz session veya token");
            }

            AuthResponse authResponse = oAuth2Service.completeOAuthRegistration(request, provider, oauth2User);

            // Session'ı temizle
            session.removeAttribute("oauth2User");
            session.removeAttribute("provider");
            session.removeAttribute("temporaryToken");

            return ResponseEntity.ok(authResponse);

        } catch (Exception e) {
            log.error("OAuth complete registration error: ", e);
            return ResponseEntity.badRequest().body("Kayıt tamamlanamadı: " + e.getMessage());
        }
    }
}