package commons;

public enum EditableProperty {
    PHONE("telefono"),
    EMAIL("email"),
    IBAN("IBAN");
    private final String stringValue;

    EditableProperty(String stringValue) {
        this.stringValue = stringValue;
    }

    public String getStringValue() {
        return stringValue;
    }
}
