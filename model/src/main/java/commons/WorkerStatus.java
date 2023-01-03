package commons;

public enum WorkerStatus {
    WORKING("Al lavoro", "#00FF38"),
    ILL("In malattia", "#FF0F00"),
    STRIKING("In sciopero", "#ABABAB"),
    ON_HOLIDAY("In ferie", "#03B3FF"),
    PARENTAL_LEAVE("In congedo p.", "#FAFF00"),
    FREE("Libero", "#DF59F4");

    private final String stringValue;
    private final String colorString;

    WorkerStatus(String stringValue, String colorString) {
        this.stringValue = stringValue;
        this.colorString = colorString;
    }

    public String getStringValue() {
        return stringValue;
    }

    public String getColorString() {
        return colorString;
    }
}
