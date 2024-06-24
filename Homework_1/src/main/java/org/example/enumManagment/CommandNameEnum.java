package org.example.enumManagment;

public enum CommandNameEnum {
    Create("Просмотр доступных аудиторий и их резервация (для авторизованных пользователей)"),
    Read("Просмотр резерваций (для авторизованных пользователей)"),
    Help("Список команд"),
    Delete("Удаление брони (для авторизованных пользователей)"),
    Registration("Регистрация новой учетной записи"),
    Authorization("Вход в учетную запись"),
    Logout("Выход из учетной записи (для авторизованных пользователей)"),
    Back("Отмена (только в активированных меню команд)");

    private String text;
    CommandNameEnum(String text){
        this.text=text;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
