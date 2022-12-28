package commons;

public enum EditableProperty {
    PHONE("numero di telefono", "+39"),
    EMAIL("indirizzo email", "user@email.com"),
    IBAN("IBAN", "IT01234567890123456789");
    private final String stringValue;
    private final String promptText;

    EditableProperty(String stringValue, String promptText) {
        this.stringValue = stringValue;
        this.promptText = promptText;
    }

    public String getStringValue() {
        return stringValue;
    }

    public String getPromptText() {
        return promptText;
    }
}
