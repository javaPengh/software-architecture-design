package org.zzu.utils;

import lombok.Getter;

/**
 * @className ResultCodeEnum
 * @description 统一返回结果状态信息类
 */
@Getter
public enum ResultCodeEnum {

    SUCCESS(200,"success"),
    FAIL(201,"fail"),
    USERNAME_ERROR(501,"usernameError"),
    PASSWORD_ERROR(503,"passwordError"),
    NOTLOGIN(504,"notLogin"),
    USERNAME_USED(505,"userNameUsed"),
    SYSTEM_ERROR(512, "systemError"), // <-- 在这里添加缺失的系统异常枚举
    TICKET_BOOKED_FAILED(506,"ticketBookedFailed"),
    TICKET_CANCEL_FAILED(507, "ticketCancelFailed"),
    UPDATE_FAILED(508, "updateFailed"),
    DELETE_FAILED(509, "deleteFailed"),
    CAPTCHA_ERROR(510, "captchaError"),
    SCREENING_CONFLICT(511, "screeningConflict"),
    SEAT_OCCUPIED(513, "seatOccupied");

    private final Integer code;
    private final String message;
    private ResultCodeEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
    public Integer getCode() {
        return code;
    }
    public String getMessage() {
        return message;
    }
}