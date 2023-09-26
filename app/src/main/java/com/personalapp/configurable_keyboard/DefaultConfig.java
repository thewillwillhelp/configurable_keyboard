package com.personalapp.configurable_keyboard;

public class DefaultConfig {
    public KeyboardConfig.KeyboardLayout defaultLayout = new KeyboardConfig.KeyboardLayout(5);

    // 1 - label, 2 - charsequence
    // 3 - shifted label, 4 - shifted charsequence
    // 5 - action name
    // 6 - width, 7 - height
    private String[][][] defaultLayoutKeys = {
        { {"ESC", "\u001B", "", "", "", "30"}, {"`", "`", "~", "~"}, {"1", "1", "!", "!"}, {"2", "2", "@", "@"}, {"3", "3", "#", "#"},
                {"4", "4", "$", "$"}, {"5", "5", "%", "%"}, {"6", "6", "^", "^"}, {"7", "7", "&", "&"},
                {"8", "8", "*", "*"}, {"9", "9", "(", "("}, {"0", "0", ")", ")"}, {"-", "-", "_", "_"},
                {"=", "=", "+", "+"} },
        { {"TAB", "\t"}, {"q", "q", "Q", "Q"}, {"w", "w", "W", "W"}, {"e", "e", "E", "E"}, {"r", "r", "R", "R"},
                {"t", "t", "T", "T"}, {"y", "y", "Y", "Y"}, {"u", "u", "U", "U"}, {"i", "i", "I", "I"},
                {"o", "o", "O", "O"}, {"p", "p", "P", "P"}, {"[", "[", "{", "{"}, {"]", "]", "}", "}"} },
        { {"\u2699", "", "", "", "CHOOSE_KEYBOARD"}, {"a", "a", "A", "A"}, {"s", "s", "S", "S"}, {"d", "d", "D", "D"},
                {"f", "f", "F", "F"}, {"g", "g", "G", "G"}, {"h", "h", "H", "H"}, {"j", "j", "J", "J"},
                {"k", "k", "K", "K"}, {"l", "l", "L", "L"}, {";", ";", ":", ":"}, {"'", "'", "\"", "\""} },
        { {"SHFT", "", "", "", "LShift", "60"}, {"z", "z", "Z", "Z"}, {"x", "x", "X", "X"}, {"c", "c", "C", "C"},
                {"v", "v", "V", "V"}, {"b", "b", "B", "B"}, {"n", "n", "N", "N"}, {"m", "m", "M", "M"},
                {",", ",", "<", "<"}, {".", ".", ">", ">"}, {"/", "/", "?", "?"}, {"\\", "\\", "|", "|"} },
        { {"CTRL", "", "", "", "LCtrl", "45"}, {"SPACE", "\u0020", "", "", "", "100"}, {"CTX", "", "", "", "DISPLAY_CONFIG"},
                {"\u2190" /* arrow left */, "\u001B[D"}, {"\u2191" /* arrow up */, "\u001B[A"},
                {"\u2193"  /* arrow down */, "\u001B[A"}, {"\u2192"  /* arrow right */, "\u001B[C"},
                {"\u21A4" /* backspace */, "\u0008", "", "", "", "30"}, {"ENTER", "\n", "", "", "", "50"} }
    };

    public DefaultConfig() {
        defaultLayout.name = "Original default";
        for (int rowIndex = 0; rowIndex < defaultLayoutKeys.length; rowIndex++) {
            for (int keyIndex = 0; keyIndex < defaultLayoutKeys[rowIndex].length; keyIndex++) {
                KeyboardConfig.KeyConfig kc = new KeyboardConfig.KeyConfig();
                String[] keyConfigList = defaultLayoutKeys[rowIndex][keyIndex];
                kc.label = keyConfigList[0];

                // height
                if (keyConfigList.length > 6) {
                    kc.height = Integer.parseInt(keyConfigList[6]);
                }

                // width
                if (keyConfigList.length > 5) {
                    kc.width = Integer.parseInt(keyConfigList[5]);
                }

                // action
                if (keyConfigList.length > 4 && keyConfigList[4].length() > 0) {
                    kc.action = keyConfigList[4];
                }

                // shifted char sequence
                if (keyConfigList.length > 3 && keyConfigList[3].length() > 0) {
                    kc.shiftedSequence = keyConfigList[3];
                }

                // shifted label
                if (keyConfigList.length > 2 && keyConfigList[2].length() > 0) {
                    kc.shiftedLabel = keyConfigList[2];
                }

                // char sequence
                if (keyConfigList.length > 1) {
                    kc.charSequence = keyConfigList[1];
                }

                // label
                if (keyConfigList.length > 0) {
                    kc.label = keyConfigList[0];
                }

                defaultLayout.addKey(rowIndex, kc);
            }
        }
    }
}
