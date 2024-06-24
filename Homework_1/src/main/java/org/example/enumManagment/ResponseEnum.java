package org.example.enumManagment;

public enum ResponseEnum {
    WRONG_FORMAT("Неверный формат"),
    WRONG_DATA("Неверные данные"),
    NONEXISTENT_HALL("Данная аудитория отстутсвует"),
    SUCCESS_BOOKING("АУДИТОРИЯ УСПЕШНО ЗАБРОНИРОВАНА"),
    OCCUPIED("Слот занят. Выберите другую аудиторию или время"),
    BACK_TO_MAIN("Возвращаемся назад"),
    TEXT(""),
    NONAVAILABLE_SLOT("Запись в данный слот недоступна"),
    NO_AUTHORIZATION_YET("Для доступа к данной команде вы должны зарегистрироваться, либо авторизоваться"),
    ALREADY_AUTHORIZED("Вы уже авторизованы"),
    ALREADY_REGISTRATED("Пользователь с данной электронной почтой уже зарегистрирован"),
    SUCCESS_AUTH("Вы успешно вошли в учетную запись"),
    SUCCESS_LOGOUT("Вы вышли из учетной записи"),
    SUCCESS_DELETE("Резервация отменена"),
    NO_BOOKED_ROOMS("Вы не зарезервировали ни одну комнату"),
    ONLY_3_MONTHS("Резервация доступна только на числа следующщих 3 месяцев"),
    SUCCESS(""),
    SUCCESS_ADD("Аудитория успешно добавлена"),
    SUCCESS_DELETE_CHAMBER("Комната успешно удалена"),
    UNKNOWN_COMMAND("Неизвестная команда"),
    NO_ROOMS_DETECTED("Нет зарегистрированных аудиторий"),
    NO_AVAILABLE_SLOTS_DETECTED("Отствутсвуют временные слоты для резервации"),
    ACCESS_DENIED("Доступ закрыт");

    private String text;

    ResponseEnum(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }
}
