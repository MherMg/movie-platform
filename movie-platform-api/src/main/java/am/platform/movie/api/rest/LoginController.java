package am.platform.movie.api.rest;

import am.platform.movie.api.rest.response.ResponseInfo;
import am.platform.movie.api.rest.response.ResponseMessage;
import am.platform.movie.api.security.JwtAuthenticationRequest;
import am.platform.movie.api.security.JwtTokenUtil;
import am.platform.movie.api.service.MailService;
import am.platform.movie.api.service.UserService;
import am.platform.movie.api.util.Validator;
import am.platform.movie.common.model.User;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

import static am.platform.movie.api.rest.response.ResponseMessage.*;

/**
 * @author mher13.02.94@gmail.com
 */

@RestController
@CrossOrigin
@RequestMapping("/api/public/v1")
public class LoginController {

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);

    private final UserService userService;
    private final MailService mailService;
    private final JwtTokenUtil jwtTokenUtil;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public LoginController(
            UserService userService,
            MailService mailService,
            JwtTokenUtil jwtTokenUtil,
            PasswordEncoder passwordEncoder
    ) {
        this.userService = userService;
        this.mailService = mailService;
        this.jwtTokenUtil = jwtTokenUtil;
        this.passwordEncoder = passwordEncoder;
    }

    public static class RegisterRequest {
        public String email;
        public String password;
        public String confirmPassword;
    }

    public static class VerifiedRequest {
        public String email;
        public String password;
    }

    public static class VerifyCodeRequest {
        public String email;
    }

    @AllArgsConstructor
    public static class LoginResponse {
        public String accessToken;
        public String tokenType;
    }

    private LoginResponse generateResponse(User user) {
        return new LoginResponse(jwtTokenUtil.generateToken(user.getEmail()), "Bearer");
    }

    @ApiResponses({
            @ApiResponse(code = 400, message = "EMAIL_IS_NOT_VALID,REGISTER_USER_ALREADY_EXISTS,PASSWORD_IS_INVALID,PASSWORDS_DO_NOT_MATCH", response = ResponseInfo.class),
            @ApiResponse(code = 201, message = "ACCOUNT_WAS_CREATED", response = ResponseInfo.class)

    })
    @ApiOperation(value = "API user registration")
    @PostMapping("/register")
    public HttpEntity<ResponseInfo> registration(@RequestBody RegisterRequest request) {

        if (!Validator.isValidEmail(request.email)) {
            return ResponseEntity.badRequest().body(ResponseInfo.createResponse(ResponseMessage.EMAIL_IS_NOT_VALID));
        }
        if (userService.userExists(request.email)) {
            return ResponseEntity.badRequest().body(ResponseInfo.createResponse(ResponseMessage.REGISTER_USER_ALREADY_EXISTS));
        }
        if (!Validator.isValidPassword(request.password)) {
            return ResponseEntity.badRequest().body(ResponseInfo.createResponse(ResponseMessage.PASSWORD_IS_INVALID));
        }
        if (!request.password.equals(request.confirmPassword)) {
            return ResponseEntity.badRequest().body(ResponseInfo.createResponse(ResponseMessage.PASSWORDS_DO_NOT_MATCH));
        }

        mailService.createEmailVerify(request.email);

        userService.createUser(request.email, request.password);

        return ResponseEntity.status(201).body(ResponseInfo.createResponse(ResponseMessage.ACCOUNT_WAS_CREATED));


    }

    @ApiResponses({
            @ApiResponse(code = 400, message = "ACCOUNT_VERIFIED,ACCOUNT_DELETED,EMAIL_IS_NOT_VALID", response = ResponseInfo.class),
            @ApiResponse(code = 404, message = "USER_NOT_FOUND", response = ResponseInfo.class),
            @ApiResponse(code = 200, message = "CONFIRMATION_CODE_SENT", response = ResponseInfo.class)

    })
    @ApiOperation(value = "API to get code for account verify")
    @PostMapping("/request-verify-code")
    public HttpEntity<ResponseInfo> requestVerifyCode(@RequestBody VerifyCodeRequest request) {

        if (!Validator.isValidEmail(request.email)) {
            return ResponseEntity.badRequest().body(ResponseInfo.createResponse(ResponseMessage.EMAIL_IS_NOT_VALID));
        }
        User user = userService.loadUserByEmail(request.email);
        if (user == null) {
            return ResponseEntity.status(404).body(ResponseInfo.createResponse(ResponseMessage.USER_NOT_FOUND));
        }
        if (user.getState() == User.UserState.ACTIVE) {
            return ResponseEntity.badRequest().body(ResponseInfo.createResponse(ResponseMessage.ACCOUNT_VERIFIED));
        }

        if (user.getState() == User.UserState.DELETED) {
            return ResponseEntity.badRequest().body(ResponseInfo.createResponse(ResponseMessage.ACCOUNT_DELETED));
        }

        mailService.sendEmailCode(request.email);
        return ResponseEntity.status(200).body(ResponseInfo.createResponse(ResponseMessage.CONFIRMATION_CODE_SENT));
    }


    @ApiResponses({
            @ApiResponse(code = 409, message = " CODE_EXPIRED", response = ResponseInfo.class),
            @ApiResponse(code = 400, message = "EMAIL_IS_NOT_VALID,PASSWORD_IS_INVALID,CODE_DOES_NOT_MATCH", response = ResponseInfo.class),
            @ApiResponse(code = 404, message = "CONFIRMATION_CODE_EVENT_IS_NULL, USER_DOES_NOT_EXIST", response = ResponseInfo.class),
            @ApiResponse(code = 200, message = "", response = LoginResponse.class)

    })
    @ApiOperation(value = "API for verify account")
    @PostMapping("/verify")
    public HttpEntity<?> verifyAccount(@RequestParam(value = "code") String code, @RequestBody VerifiedRequest request) {

        log.debug("Verify user account: [code:{};email:{}]", code, request.email);

        if (!Validator.isValidEmail(request.email)) {
            return ResponseEntity.badRequest().body(ResponseInfo.createResponse(ResponseMessage.EMAIL_IS_NOT_VALID));
        }
        if (!Validator.isValidPassword(request.password)) {
            return ResponseEntity.badRequest().body(ResponseInfo.createResponse(ResponseMessage.PASSWORD_IS_INVALID));
        }
        User user = userService.loadUserByEmail(request.email);
        if (user == null) {
            return ResponseEntity.status(404).body(ResponseInfo.createResponse(ResponseMessage.USER_DOES_NOT_EXIST));
        }
        if (!passwordEncoder.matches(request.password, user.getPassword())) {
            return ResponseEntity.badRequest().body(ResponseInfo.createResponse(ResponseMessage.PASSWORD_IS_INVALID));
        }
        ResponseMessage checkEmailCode = mailService.checkEmailCode(code, request.email);
        if (checkEmailCode == null) {
            userService.userVerified(user);
            log.debug("user account verified: [userId:{};email:{}]", user.getId(), user.getEmail());
            return ResponseEntity.ok(generateResponse(user));
        }
        return switch (checkEmailCode) {
            case CONFIRMATION_CODE_EVENT_IS_NULL -> ResponseEntity.status(404).body(ResponseInfo.createResponse(CONFIRMATION_CODE_EVENT_IS_NULL));
            case CODE_DOES_NOT_MATCH -> ResponseEntity.badRequest().body(ResponseInfo.createResponse(CODE_DOES_NOT_MATCH));
            case CODE_EXPIRED -> ResponseEntity.status(409).body(ResponseInfo.createResponse(CODE_EXPIRED));
            default -> ResponseEntity.badRequest().build();
        };
    }

    @ApiResponses({
            @ApiResponse(code = 400, message = "EMAIL_IS_NOT_VALID,PASSWORD_IS_INVALID, ACCOUNT_DELETED", response = ResponseInfo.class),
            @ApiResponse(code = 404, message = "USER_DOES_NOT_EXIST", response = ResponseInfo.class),
            @ApiResponse(code = 418, message = "ACCOUNT_DOES_NOT_VERIFIED_CONFIRMATION_CODE_SENT", response = ResponseInfo.class),
            @ApiResponse(code = 200, message = "", response = LoginResponse.class)

    })
    @ApiOperation(value = "API for login")
    @PostMapping(value = "/login")
    public HttpEntity<?> login(@RequestBody JwtAuthenticationRequest request) {

        log.debug("User is trying to login: [email:{}]", request.getEmail());

        if (!Validator.isValidEmail(request.getEmail())) {
            return ResponseEntity.badRequest().body(ResponseInfo.createResponse(ResponseMessage.EMAIL_IS_NOT_VALID));
        }

        Optional<User> userOptional = Optional.ofNullable(userService.loadUserByEmail(request.getEmail()));

        if (userOptional.isEmpty()) {
            return ResponseEntity.status(404).body(ResponseInfo.createResponse(ResponseMessage.USER_DOES_NOT_EXIST));
        }

        if (!passwordEncoder.matches(request.getPassword(), userOptional.get().getPassword())) {
            return ResponseEntity.badRequest().body(ResponseInfo.createResponse(ResponseMessage.PASSWORD_IS_INVALID));
        }

        if (userOptional.get().getState() != User.UserState.ACTIVE) {

            mailService.getEmailVerify(request.getEmail());

            return ResponseEntity.status(418).body(ResponseInfo.createResponse(ResponseMessage.ACCOUNT_DOES_NOT_VERIFIED_CONFIRMATION_CODE_SENT));
        }

        if (userOptional.get().getState() == User.UserState.DELETED) {
            return ResponseEntity.badRequest().body(ResponseInfo.createResponse(ResponseMessage.ACCOUNT_DELETED));

        }

        return ResponseEntity.ok(generateResponse(userOptional.get()));


    }

}

