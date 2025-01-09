package io.ndk.cordis_backend.handler;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
public enum BusinessErrorCodes {

    NO_CODE(0,"No code", HttpStatus.NOT_IMPLEMENTED),
    INCORRECT_CURRENT_PASSWORD(300, "Current password is incorrect", HttpStatus.BAD_REQUEST),
    NEW_PASSWORD_DOES_NOT_MATCH(301, "New password does not match", HttpStatus.BAD_REQUEST),
    ACCOUNT_LOCKED(302, "User account locked", HttpStatus.FORBIDDEN),
    ACCOUNT_DISABLED(303, "User account is disable", HttpStatus.FORBIDDEN),
    BAD_CREDENTIALS(304, "Login and / or password is incorrect", HttpStatus.FORBIDDEN),
    NO_SUCH_EMAIL(305, "There is no user of such email", HttpStatus.BAD_REQUEST),
    BAD_JWT_TOKEN(306, "Invalid JWT token", HttpStatus.BAD_REQUEST),
    EMAIL_IS_USED(307, "Email is used", HttpStatus.BAD_REQUEST),
    NICKNAME_IS_USED(308, "Nick name is used", HttpStatus.BAD_REQUEST),
    NO_SUCH_ID(309, "There is no user with that ID", HttpStatus.BAD_REQUEST),
    BAD_COOKIE(310, "Provided cookie is incorrect", HttpStatus.BAD_REQUEST),
    IMAGE_FETCH_FAILED(311, "Operation of fetching image has failed", HttpStatus.BAD_REQUEST),
    IMAGE_NOT_FOUND(312, "This file doesn't exist", HttpStatus.BAD_REQUEST),
    IMAGE_UPDATE_FAILED(313, "Failed to update image", HttpStatus.BAD_REQUEST),
    NO_USERNAME(312, "Username of such nickname doesn't exist", HttpStatus.BAD_REQUEST),
    NO_SUCH_SERVER(313, "Such server doesn't exist", HttpStatus.BAD_REQUEST);

    @Getter
    private final int code;
    @Getter
    private final String description;
    @Getter
    private final HttpStatus httpStatus;

}
