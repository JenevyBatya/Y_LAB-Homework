package org.example.enumManagment;

public enum HelperNameEnum {
    Book("Резервация аудитоии"),
    Rooms("Просмотр доступных аудиторий"),
    Table("Просмотр свободных временных слотов для выбранной аудитории"),
    All("Просмотр всех доступных дней для резервации"),
    Period("Просмотр всех доступных дней для резервации в определенный промежуток времени"),
    Number("Номер аудитории, которую вы хотите забронировать:"),
    NUMBER_READ("Номер аудитории, в которой вы хотите просмотреть бронь:"),
    Add("Добавление новых аудиторий"),
    Delete("Удаление существующих аудиторий");

    private String text;

    HelperNameEnum(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
