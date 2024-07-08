package io.ylab.managment.enums;

public enum ResponseEnum {
    TEXT(""),
    BOOKING_SUCCESS_ADD("Аудитория успешно забронирована"),
    BOOKING_SUCCESS_DELETE("Резервация успешно удалена"),
    BOOKING_FAILURE_DELETE("Ошибка отмены резервации"),
    NO_BOOKED_ROOMS("История резерваций пуста"),
    CHAMBER_SUCCESS_ADD("Аудитория успешно добавлена"),
    CHAMBER_DELETE_SUCCESS("Аудитория успешно удалена"),
    CHAMBER_DELETE_FAILURE("Данной аудитории не существует"),
    CHAMBER_OCCUPIED("Бронь невозвозможна из-за пересечения с другими резервациями"),
    USER_EXISTS("Пользователь с данной электронной почтой уже существует"),
    USER_ADD_SUCCESS("Пользователь успешно зарегистрирован"),
    USER_AUTH_SUCCESS("Пользователь успешно авторизован"),
    USER_AUTH_WRONG_DATA("Неверные почта или пароль"),
    AUTH_SUCCESS("Вы успешно вошли в систему"),
    INVALID_DATA("Невалидные данные"),
    NO_ACCESS("Недостаточно прав"),


    //Ошибки
    TOKEN_INVALID("Invalid token"),
    TOKEN_INVALID_SIGNATURE("Invalid signature"),
    TOKEN_MALFORMED_JWT("Malformed jwt"),
    TOKEN_UNSUPPORTED_JWT("Unsupported jwt"),
    TOKEN_EXPIRED("Token expired"),
    SQL_ERROR("Ошибка запроса. Невалидные данные");
    private final String text;

    ResponseEnum(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }
}
