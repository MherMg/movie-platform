package am.platform.movie.api.service;

import am.platform.movie.api.rest.response.ResponseMessage;
import am.platform.movie.common.model.EmailVerify;
import am.platform.movie.common.repository.EmailVerifyRepository;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;

import static am.platform.movie.api.rest.response.ResponseMessage.*;
import static am.platform.movie.common.model.EmailVerify.State.PROCESSED;

/**
 * @author mher13.02.94@gmail.com
 */

@Service
public class MailService {

    @Value("${code.expire.timeout.sec}")
    private int codeExpireTimeoutSec;

    private static final Logger log = LoggerFactory.getLogger(MailService.class);
    private static final Integer EMAIL_CODE_LENGTH = 6;


    private final UserService userService;
    private final EmailVerifyRepository emailVerifyRepository;


    @Autowired
    public MailService(
            UserService userService,
            EmailVerifyRepository emailVerifyRepository) {
        this.userService = userService;
        this.emailVerifyRepository = emailVerifyRepository;
    }

    public void createEmailVerify(String email) {
        EmailVerify emailVerify = new EmailVerify();
        emailVerify.setEmail(email);
        emailVerify.setCode(RandomStringUtils.randomNumeric(EMAIL_CODE_LENGTH));
        emailVerifyRepository.save(emailVerify);
        //send email

    }

    public ResponseMessage checkSmsCode(String code, String email) {
        EmailVerify emailVerify = getEmailVerify(email);
        if (emailVerify == null) {
            return CONFIRMATION_CODE_EVENT_IS_NULL;
        }
        if ((System.currentTimeMillis() - emailVerify.getCreatedAt().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()) > (codeExpireTimeoutSec * 1000)) {
            codeProcessed(emailVerify);
            return CODE_EXPIRED;
        }
        if (emailVerify.getState() == PROCESSED) {
            return CODE_EXPIRED;
        }
        if (code.equals(emailVerify.getCode())) {
            codeProcessed(emailVerify);
            return CODE_DOES_NOT_MATCH;
        }
        codeProcessed(emailVerify);
        return null;
    }

    public void sendEmailCode(String email) {
        EmailVerify emailVerify = emailVerifyRepository.findByEmail(userService.getCurrentUser().getEmail());
        emailVerify.setState(PROCESSED);
        emailVerify.setCode(RandomStringUtils.randomNumeric(EMAIL_CODE_LENGTH));
        emailVerify.setUpdatedAt(LocalDateTime.now());
        emailVerifyRepository.save(emailVerify);

        //send email
    }

    public EmailVerify getEmailVerify(String email) {
        return emailVerifyRepository.findByEmail(email);
    }

    public void codeProcessed(EmailVerify emailVerify) {
        emailVerify.setState(PROCESSED);
        emailVerify.setUpdatedAt(LocalDateTime.now());
        emailVerifyRepository.save(emailVerify);
    }

}
