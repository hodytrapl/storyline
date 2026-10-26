package org.hodytrapl.storyline.Client_side.skin;

public class SkinNames {

//    верный формат файлов имени скина lol_2-\34.png
    static boolean isValidName(String name) {
        return name.matches("[a-z0-9_\\-]{1,64}");
    }
}
