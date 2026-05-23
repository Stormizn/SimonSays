package me.stormizn.simonsays.game;

public enum SimonTask {

    JUMP("Jump"),
    SNEAK("Sneak"),
    BREAK_BLOCK("Break a block"),
    DROP_ITEM("Drop an item"),
    INTERACT("Right click");

    private final String display;

    SimonTask(String display) {
        this.display = display;
    }

    public String getDisplay() {
        return display;
    }

}
