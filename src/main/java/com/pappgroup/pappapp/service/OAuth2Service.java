package com.pappgroup.pappapp.service;

import com.pappgroup.pappapp.dto.request.OAuthCompleteRequest;
import com.pappgroup.pappapp.dto.response.AuthResponse;
import com.pappgroup.pappapp.dto.response.OAuthResponse;
import com.pappgroup.pappapp.dto.response.UserResponse;
import com.pappgroup.pappapp.entity.User;
import com.pappgroup.pappapp.enums.Role;
import com.pappgroup.pappapp.repository.UserRepository;
import com.pappgroup.pappapp.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OAuth2Service {

    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public OAuthResponse processOAuthUser(OAuth2User oauth2User, String provider) {
        String email = oauth2User.getAttribute("email");
        String oauthId = oauth2User.getAttribute("sub") != null ?
            oauth2User.getAttribute("sub").toString() :
            oauth2User.getAttribute("id").toString();

        // Mevcut kullanıcıyı kontrol et
        Optional<User> existingUser = userRepository.findByEmailOrOauthId(email, oauthId);

        if (existingUser.isPresent()) {
            // Kullanıcı zaten var, direkt giriş yap
            User user = existingUser.get();
            return createCompleteAuthResponse(user);
        }

        // Yeni OAuth kullanıcısı, geçici token oluştur
        String temporaryToken = UUID.randomUUID().toString();

        OAuthResponse response = new OAuthResponse();
        response.setNeedsCompletion(true);
        response.setTemporaryToken(temporaryToken);
        response.setEmail(email);
        response.setMessage("Kayıt işlemini tamamlamak için ad ve soyad bilgilerinizi girin");

        // Geçici kullanıcı bilgilerini cache'de saklayabiliriz (Redis vs.)
        // Şimdilik basit bir yaklaşım kullanacağız

        return response;
    }

    @Transactional
    public AuthResponse completeOAuthRegistration(OAuthCompleteRequest request, String provider, OAuth2User oauth2User) {
        String email = oauth2User.getAttribute("email");
        String oauthId = oauth2User.getAttribute("sub") != null ?
            oauth2User.getAttribute("sub").toString() :
            oauth2User.getAttribute("id").toString();

        // Kullanıcı oluştur
        User user = new User();
        user.setEmail(email);
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setOauthProvider(provider);
        user.setOauthId(oauthId);
        user.setIsOauthUser(true);
        user.setIsVerified(true); // OAuth kullanıcıları doğrulanmış kabul edilir
        user.setRole(Role.USER);
        user.setEnabled(true);

        user = userRepository.save(user);

        // JWT token oluştur
        String token = jwtTokenProvider.generateToken(user.getEmail(), user.getRole().name(), user.getId());
        String refreshToken = jwtTokenProvider.generateRefreshToken(user.getEmail());

        // UserResponse oluştur
        UserResponse userResponse = new UserResponse();
        BeanUtils.copyProperties(user, userResponse);

        return new AuthResponse(token, refreshToken, userResponse, "OAuth kayıt başarılı");
    }

    private OAuthResponse createCompleteAuthResponse(User user) {
        String token = jwtTokenProvider.generateToken(user.getEmail(), user.getRole().name(), user.getId());
        String refreshToken = jwtTokenProvider.generateRefreshToken(user.getEmail());

        UserResponse userResponse = new UserResponse();
        BeanUtils.copyProperties(user, userResponse);

        OAuthResponse response = new OAuthResponse();
        response.setNeedsCompletion(false);
        response.setToken(token);
        response.setRefreshToken(refreshToken);
        response.setUser(userResponse);
        response.setMessage("Giriş başarılı");

        return response;
    }
}