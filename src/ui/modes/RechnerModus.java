package ui.modes;

public enum RechnerModus {
    STANDARD("Standard"),
    WISSENSCHAFTLICH("Wissenschaftlich"),
    PROGRAMMIERER("PRG"),
    GRAPH("Graph"),
    KOMPLEX("Komplex");

    private final String label;

    RechnerModus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}