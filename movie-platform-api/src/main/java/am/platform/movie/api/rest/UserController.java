package am.platform.movie.api.rest;

import am.platform.movie.api.rest.dto.UserVO;
import am.platform.movie.api.rest.response.ResponseInfo;
import am.platform.movie.api.rest.response.ResponseMessage;
import am.platform.movie.api.security.JwtTokenUtil;
import am.platform.movie.api.service.MailService;
import am.platform.movie.api.service.UserService;
import am.platform.movie.api.util.Validator;
import am.platform.movie.common.model.User;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.Authorization;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotBlank;

import static am.platform.movie.api.rest.response.ResponseMessage.*;

/**
 * @author mher13.02.94@gmail.com
 */

@RestController
@CrossOrigin
@RequestMapping("/api/private/v1/user/")
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;
    private final MailService mailService;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenUtil jwtTokenUtil;

    @Autowired
    public UserController(
            UserService userService,
            MailService mailService,
            PasswordEncoder passwordEncoder,
            JwtTokenUtil jwtTokenUtil
    ) {
        this.userService = userService;
        this.mailService = mailService;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenUtil = jwtTokenUtil;
    }

    @AllArgsConstructor
    public static class ChangeEmailResponse {
        public String newEmail;
        public String accessToken;
        public String tokenType;
    }

    private ChangeEmailResponse generateResponse(User user) {
        return new ChangeEmailResponse(user.getEmail(), jwtTokenUtil.generateToken(user.getEmail()), "Bearer");
    }

    public static class UpdateUserRequest {
        public String name;
    }

    public static class DeleteRequest {
        @NotBlank
        public String password;
    }

    public static class ChangeEmailRequest {
        @NotBlank
        public String newEmail;
    }

    public static class ChangePasswordRequest {
        @NotBlank
        public String newPassword;
        @NotBlank
        public String confirmNewPassword;
    }


    @ApiResponses({
            @ApiResponse(code = 200, message = "", response = UserVO.class),
    })
    @ApiOperation(value = "", authorizations = {@Authorization(value = "Bearer")})
    @GetMapping("/me")
    public HttpEntity<UserVO> getMe() {
        User currentUser = userService.getCurrentUser();
        return ResponseEntity.ok(new UserVO(currentUser));
    }

    @ApiResponses({
            @ApiResponse(code = 200, message = "", response = UserVO.class)
    })
    @ApiOperation(value = "", authorizations = {@Authorization(value = "Bearer")})
    @PutMapping("/me")
    public HttpEntity<?> updateMe(@RequestBody UpdateUserRequest request) {

        User user = userService.updateUser(userService.getCurrentUser(), request.name);

        return ResponseEntity.ok(new UserVO(user));
    }

    @ApiResponses({
            @ApiResponse(code = 400, message = "PASSWORD_IS_INVALID, PASSWORDS_DO_NOT_MATCH", response = ResponseInfo.class),
            @ApiResponse(code = 200, message = "ACCOUNT_DELETED", response = ResponseInfo.class)})
    @ApiOperation(value = "", authorizations = {@Authorization(value = "Bearer")})
    @DeleteMapping("/me")
    public HttpEntity<ResponseInfo> deleteMe(@RequestBody DeleteRequest request) {

        if (!Validator.isValidPassword(request.password)) {
            return ResponseEntity.badRequest().body(ResponseInfo.createResponse(ResponseMessage.PASSWORD_IS_INVALID));
        }

        User currentUser = userService.getCurrentUser();
        if (!passwordEncoder.matches(request.password, currentUser.getPassword())) {
            return ResponseEntity.badRequest().body(ResponseInfo.createResponse(ResponseMessage.PASSWORDS_DO_NOT_MATCH));
        }

        userService.deleteUser(currentUser);
        log.debug("user is deleted,email: [{}] ", currentUser.getEmail());

        return ResponseEntity.ok().body(ResponseInfo.createResponse(ResponseMessage.ACCOUNT_DELETED));
    }


    @ApiResponses({
            @ApiResponse(code = 400, message = "EMAIL_IS_NOT_VALID, EMAIL_ALREADY_EXISTS", response = ResponseInfo.class),
            @ApiResponse(code = 200, message = "CONFIRMATION_CODE_SENT", response = ResponseInfo.class)
    })
    @ApiOperation(value = "API to get code to change email", authorizations = {@Authorization(value = "Bearer")})
    @PutMapping("/change-email")
    public HttpEntity<?> changeEmail(@RequestBody ChangeEmailRequest request) {

        if (!Validator.isValidEmail(request.newEmail)) {
            return ResponseEntity.badRequest().body(ResponseInfo.createResponse(ResponseMessage.EMAIL_IS_NOT_VALID));
        }
        if (userService.userExists(request.newEmail)) {
            return ResponseEntity.badRequest().body(ResponseInfo.createResponse(ResponseMessage.EMAIL_ALREADY_EXISTS));
        }

        mailService.createEmailVerify(request.newEmail);
        return ResponseEntity.status(200).body(ResponseInfo.createResponse(ResponseMessage.CONFIRMATION_CODE_SENT));
    }

    @ApiResponses({
            @ApiResponse(code = 400, message = "CONFIRMATION_CODE_EVENT_IS_NULL, EMAIL_IS_NOT_VALID, CODE_DOES_NOT_MATCH",
                    response = ResponseInfo.class),
            @ApiResponse(code = 409, message = "CODE_EXPIRED", response = ResponseInfo.class),
            @ApiResponse(code = 200, message = "", response = ChangeEmailResponse.class)
    })
    @ApiOperation(value = "API for confirm email code and change email", authorizations = {@Authorization(value = "Bearer")})
    @PostMapping("/verify-change-email")
    public HttpEntity<?> verifyEmail(
            @RequestParam(value = "code") String code,
            @RequestBody ChangeEmailRequest request
    ) {

        if (!Validator.isValidEmail(request.newEmail)) {
            return ResponseEntity.badRequest().body(ResponseInfo.createResponse(ResponseMessage.EMAIL_IS_NOT_VALID));
        }

        ResponseMessage checkEmailCode = mailService.checkEmailCode(code, request.newEmail);
        if (checkEmailCode == null) {
            User user = userService.updateEmail(userService.getCurrentUser(), request.newEmail);
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
            @ApiResponse(code = 400, message = " PASSWORD_IS_INVALID, PASSWORDS_DOES_NOT_MATCH,CURRENT_AND_NEW_PASSWORDS_ARE_MATCHING", response = ResponseInfo.class),
            @ApiResponse(code = 200, message = "")
    })
    @ApiOperation(value = "API for change password", authorizations = {@Authorization(value = "Bearer")})
    @PutMapping("/change-password")
    public HttpEntity<?> changePassword(@RequestBody ChangePasswordRequest request) {

        if (!Validator.isValidPassword(request.newPassword)) {
            return ResponseEntity.badRequest().body(ResponseInfo.createResponse(PASSWORD_IS_INVALID));
        }
        if (!request.newPassword.equals(request.confirmNewPassword)) {
            return ResponseEntity.badRequest().body(ResponseInfo.createResponse(PASSWORDS_DOES_NOT_MATCH));
        }

        User currentUser = userService.getCurrentUser();

        if (passwordEncoder.matches(request.newPassword, currentUser.getPassword())) {
            return ResponseEntity.badRequest().body(ResponseInfo.createResponse(CURRENT_AND_NEW_PASSWORDS_ARE_MATCHING));
        }
        userService.changePassword(request.newPassword, currentUser);

        return ResponseEntity.ok().build();
    }


}

