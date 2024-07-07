package vn.hoidanit.jobhunter.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import vn.hoidanit.jobhunter.domain.User;
import vn.hoidanit.jobhunter.domain.dto.request.ReqLoginDTO;
import vn.hoidanit.jobhunter.domain.dto.response.ResLoginDTO;
import vn.hoidanit.jobhunter.service.UserService;
import vn.hoidanit.jobhunter.util.SecurityUtil;
import vn.hoidanit.jobhunter.util.annotation.ApiMessage;
import vn.hoidanit.jobhunter.util.error.IdInvalidException;

@RestController
@RequestMapping("/api/v1/auth")
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class AuthController {

    final AuthenticationManagerBuilder authenticationManagerBuilder;

    final SecurityUtil securityUtil;

    final UserService userService;

    @Value("${hoidanit.jwt.refresh-token-validity-in-seconds}")
    public Long refreshTokenExpiration;
    
    @PostMapping("/login")
    public ResponseEntity<ResLoginDTO> login(@Valid @RequestBody ReqLoginDTO loginDTO){
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(loginDTO.getUsername(), loginDTO.getPassword());

        //xác thực người dùng, viết hàm loadUserByUsername
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        //set thông tin người dùng đăng nhập vào contexxt(có thẻ sử dụng sau này)
        SecurityContextHolder.getContext().setAuthentication(authentication);

        ResLoginDTO res = new ResLoginDTO();
        User userDB = userService.handleFetchUserByEmail(loginDTO.getUsername());
        ResLoginDTO.UserLogin userLogin = new ResLoginDTO.UserLogin(userDB.getId(),userDB.getEmail(),userDB.getName());

        String access_Token =  securityUtil.createAccessToken(loginDTO.getUsername(), userLogin);
        res.setUser(userLogin);
        res.setAccessToken(access_Token);

        //create refresh token
        String refreshToken = securityUtil.createRefreshToken(loginDTO.getUsername(), userLogin);

        //update token user
        userService.updateTokenUser(refreshToken, loginDTO.getUsername());

        //set cookies
        ResponseCookie resCookies = ResponseCookie
            .from("refresh_token",refreshToken)
            .httpOnly(true)
            .secure(true)
            .path("/")
            .maxAge(refreshTokenExpiration)
            // .domain("example.com")
            .build();
        return ResponseEntity.ok()
            .header(HttpHeaders.SET_COOKIE, resCookies.toString())
            .body(res);
    }

    @GetMapping("/account")
    @ApiMessage("Fetch account")
    public ResponseEntity<ResLoginDTO.UserGetAccount> getAccount(){
        String email = SecurityUtil.getCurrentUserLogin().isPresent() ?
        SecurityUtil.getCurrentUserLogin().get() : "";
        User userDB = userService.handleFetchUserByEmail(email);

        ResLoginDTO.UserLogin userLogin = new ResLoginDTO.UserLogin();
        ResLoginDTO.UserGetAccount userGetAccount = new ResLoginDTO.UserGetAccount();
        if(userDB!=null){
            userLogin.setEmail(userDB.getEmail());
            userLogin.setId(userDB.getId());;
            userLogin.setName(userDB.getName());;
            userGetAccount.setUser(userLogin);
        }

        return ResponseEntity.ok(userGetAccount);
    }

    @GetMapping("/refresh")
    @ApiMessage("Get user by refresh token")
    public ResponseEntity<ResLoginDTO> getRefreshToken(
        @CookieValue(name = "refresh_token",defaultValue = "") String refresh_token
    ) throws IdInvalidException{
        if(refresh_token.equals("")){
            throw new IdInvalidException("Cookies không có giá trị");
        }
       Jwt decodedToken = securityUtil.checkValidRefreshToken(refresh_token);
       String email = decodedToken.getSubject();

        //check user by email and token 
        User cUser = userService.getUserByEmailAndRefreshToken(email, refresh_token);
        if(cUser == null){
            throw new IdInvalidException("RefreshToken không hợp lệ");
        }
        //issue new token/set refresh token as cookies
        ResLoginDTO res = new ResLoginDTO();
        ResLoginDTO.UserLogin userLogin = new ResLoginDTO.UserLogin(cUser.getId(),cUser.getEmail(),cUser.getName());

        String access_Token =  securityUtil.createAccessToken(email, userLogin);
        res.setUser(userLogin);
        res.setAccessToken(access_Token);

        //create refresh token
        String new_refresh_token = securityUtil.createRefreshToken(email, userLogin);

        //update token user
        userService.updateTokenUser(new_refresh_token, email);

        //set cookies
        ResponseCookie resCookies = ResponseCookie
            .from("refresh_token",new_refresh_token)
            .httpOnly(true)
            .secure(true)
            .path("/")
            .maxAge(refreshTokenExpiration)
            // .domain("example.com")
            .build();
        return ResponseEntity.ok()
            .header(HttpHeaders.SET_COOKIE, resCookies.toString())
            .body(res);
    }

    @PostMapping("/logout")
    @ApiMessage("Logout user")
    public ResponseEntity<Void> logout() throws IdInvalidException{
        String email = securityUtil.getCurrentUserLogin().isPresent() ? securityUtil.getCurrentUserLogin().get() : "";

        if(email.equals("")){
            throw new IdInvalidException("Access token không hợp lệ");
        }

        //update refresh token in db
        userService.updateTokenUser("", email);

        //remove refresh token cookies
        ResponseCookie deleteSpringCookie = ResponseCookie
                .from("refresh_token","")
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(0)
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, deleteSpringCookie.toString())
                .body(null);

    }
}
