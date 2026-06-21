package dev.darkblade.mod.input_engine.server.utils;

public class KeyMapper {
    public static String getReadableName(int keyCode) {
        // Letters A-Z
        if (keyCode >= 65 && keyCode <= 90) {
            return String.valueOf((char) keyCode);
        }
        // Numbers 0-9
        if (keyCode >= 48 && keyCode <= 57) {
            return String.valueOf((char) keyCode);
        }
        
        switch (keyCode) {
            case 32: return "Space";
            case 256: return "Escape";
            case 257: return "Enter";
            case 258: return "Tab";
            case 259: return "Backspace";
            case 260: return "Insert";
            case 261: return "Delete";
            case 262: return "Right Arrow";
            case 263: return "Left Arrow";
            case 264: return "Down Arrow";
            case 265: return "Up Arrow";
            case 266: return "Page Up";
            case 267: return "Page Down";
            case 268: return "Home";
            case 269: return "End";
            case 280: return "Caps Lock";
            case 281: return "Scroll Lock";
            case 282: return "Num Lock";
            case 283: return "Print Screen";
            case 284: return "Pause";
            case 290: return "F1";
            case 291: return "F2";
            case 292: return "F3";
            case 293: return "F4";
            case 294: return "F5";
            case 295: return "F6";
            case 296: return "F7";
            case 297: return "F8";
            case 298: return "F9";
            case 299: return "F10";
            case 300: return "F11";
            case 301: return "F12";
            case 340: return "Left Shift";
            case 341: return "Left Control";
            case 342: return "Left Alt";
            case 344: return "Right Shift";
            case 345: return "Right Control";
            case 346: return "Right Alt";
            case 0: return "Left Click";
            case 1: return "Right Click";
            case 2: return "Middle Click";
            case 3: return "Mouse 4";
            case 4: return "Mouse 5";
            default: return "Unknown (" + keyCode + ")";
        }
    }
}
