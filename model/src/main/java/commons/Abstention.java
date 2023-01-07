package commons;

public enum Abstention {
    ILLNESS("Malattia"),
    HOLIDAY("Ferie"),
    PARENTAL_LEAVE("Congedo Parentale");

    private final String stringValue;

    Abstention(String stringValue) {
        this.stringValue = stringValue;
    }

    public String getStringValue() {
        return stringValue;
    }
}
