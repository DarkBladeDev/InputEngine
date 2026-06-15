package dev.darkblade.mod.input_engine.common;

public enum KeyAction {
    SKILL_1(1),
    SKILL_2(2),
    SKILL_3(3),
    SKILL_4(4),
    DODGE(5),
    ULTIMATE(6),
    OPEN_STATS(7),
    SPECIAL_INTERACT(8);

    private final int id;

    KeyAction(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public static KeyAction fromId(int id) {
        for (KeyAction action : values()) {
            if (action.getId() == id) {
                return action;
            }
        }
        return null;
    }
}
