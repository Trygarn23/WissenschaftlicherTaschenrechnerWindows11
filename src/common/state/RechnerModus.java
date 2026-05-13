package common.state;

public enum RechnerModus {
    STANDARD("Standard"),
    WISSENSCHAFTLICH("Wissenschaftlich"),
    PROGRAMMIERER("PRG"),
    GRAPH("Graph"),
    KOMPLEX("Komplex"),
    MATRIX("Matrix");

    private final String label;

    RechnerModus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
