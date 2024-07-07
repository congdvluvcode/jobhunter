package vn.hoidanit.jobhunter.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import com.nimbusds.jose.util.Base64;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import vn.hoidanit.jobhunter.domain.dto.ResLoginDTO;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class SecurityUtil {

    @Value("${hoidanit.jwt.base64-secret}")
    String jwtKey;

    @Value("${hoidanit.jwt.access-token-validity-in-seconds}")
    Long accessTokenExpiration;

    @Value("${hoidanit.jwt.refresh-token-validity-in-seconds}")
    Long refreshTokenExpiration;

    public static final MacAlgorithm JWT_ALGORITHM = MacAlgorithm.HS512;

    final JwtEncoder jwtEncoder;
    
    public String createAccessToken(String email,ResLoginDTO.UserLogin user) {
        Instant now = Instant.now();
        Instant validity = now.plus(accessTokenExpiration, ChronoUnit.SECONDS);

        //hard code permission
        List<String> authority = new ArrayList<>();
        authority.add("ROLE_USER_CREATE");
        authority.add("ROLE_USER_UPDATE");

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuedAt(now)
                .expiresAt(validity)
                .subject(email)
                .claim("user", user)
                .claim("permission", authority)
                .build();

        JwsHeader jwsHeader = JwsHeader.with(JWT_ALGORITHM).build();
        return jwtEncoder.encode(JwtEncoderParameters.from(jwsHeader, claims)).getTokenValue();
    }

    //tạo refresh token
    public String createRefreshToken(String email, ResLoginDTO.UserLogin user) {
        Instant now = Instant.now();
        Instant validity = now.plus(refreshTokenExpiration, ChronoUnit.SECONDS);

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuedAt(now)
                .expiresAt(validity)
                .subject(email)
                .claim("user", user)
                .build();

        JwsHeader jwsHeader = JwsHeader.with(JWT_ALGORITHM).build();
        return jwtEncoder.encode(JwtEncoderParameters.from(jwsHeader, claims)).getTokenValue();
    }

    public Jwt checkValidRefreshToken(String token){
        NimbusJwtDecoder jwtDecoder = NimbusJwtDecoder.withSecretKey(getSecretKey()).macAlgorithm(SecurityUtil.JWT_ALGORITHM).build();
        try {
             return jwtDecoder.decode(token);
        } catch (Exception e) {
            System.out.println(">>> JWT refresh token error: " + e.getMessage());
            throw e;
        }
    }

    private SecretKey getSecretKey() {
        byte[] keyBytes = Base64.from(jwtKey).decode();
        return new SecretKeySpec(keyBytes, 0, keyBytes.length, SecurityUtil.JWT_ALGORITHM.getName());
    }



    //lấy thông tin user đang đăng nhập ra 
    public static Optional<String> getCurrentUserLogin() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        return Optional.ofNullable(extractPrincipal(securityContext.getAuthentication()));
    }

    // từ authentication trong contexthoder, lấy ra email của người dùng trong phần sub
    private static String extractPrincipal(Authentication authentication) {
        if (authentication == null) {
            return null;
        } else if (authentication.getPrincipal() instanceof UserDetails springSecurityUser) {
            return springSecurityUser.getUsername();
        } else if (authentication.getPrincipal() instanceof Jwt jwt) {
            return jwt.getSubject();
        } else if (authentication.getPrincipal() instanceof String s) {
            return s;
        }
        return null;
    }

    /**
     * Get the JWT of the current user.
     *
     * @return the JWT of the current user.
     */
    public static Optional<String> getCurrentUserJWT() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        return Optional.ofNullable(securityContext.getAuthentication())
            .filter(authentication -> authentication.getCredentials() instanceof String)
            .map(authentication -> (String) authentication.getCredentials());
    }

    /**
     * Check if a user is authenticated.
     *
     * @return true if the user is authenticated, false otherwise.
     */
    // public static boolean isAuthenticated() {
    //     Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    //     return authentication != null && getAuthorities(authentication).noneMatch(AuthoritiesConstants.ANONYMOUS::equals);
    // }

    // /**
    //  * Checks if the current user has any of the authorities.
    //  *
    //  * @param authorities the authorities to check.
    //  * @return true if the current user has any of the authorities, false otherwise.
    //  */
    // public static boolean hasCurrentUserAnyOfAuthorities(String... authorities) {
    //     Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    //     return (
    //         authentication != null && getAuthorities(authentication).anyMatch(authority -> Arrays.asList(authorities).contains(authority))
    //     );
    // }

    // /**
    //  * Checks if the current user has none of the authorities.
    //  *
    //  * @param authorities the authorities to check.
    //  * @return true if the current user has none of the authorities, false otherwise.
    //  */
    // public static boolean hasCurrentUserNoneOfAuthorities(String... authorities) {
    //     return !hasCurrentUserAnyOfAuthorities(authorities);
    // }

    // /**
    //  * Checks if the current user has a specific authority.
    //  *
    //  * @param authority the authority to check.
    //  * @return true if the current user has the authority, false otherwise.
    //  */
    // public static boolean hasCurrentUserThisAuthority(String authority) {
    //     return hasCurrentUserAnyOfAuthorities(authority);
    // }

    // private static Stream<String> getAuthorities(Authentication authentication) {
    //     return authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority);
    // }

}