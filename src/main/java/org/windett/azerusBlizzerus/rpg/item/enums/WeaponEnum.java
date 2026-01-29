package org.windett.azerusBlizzerus.rpg.item.enums;

public class WeaponEnum {

    public enum WeaponType{
        MELEE("melee"),
        BOW("bow"),
        WAND("wand");

        private final String pdc;

        WeaponType(String pdc) {
            this.pdc = pdc;
        }
    }

    public enum WeaponSubType{
        SWORD(WeaponType.MELEE, "sword"),
        AXE(WeaponType.MELEE, "axe"),
        DAGGER(WeaponType.MELEE, "dagger"),
        BOW(WeaponType.BOW, "bow"),
        MAGIC_STICK(WeaponType.WAND, "magic_stick"),
        STAFF(WeaponType.WAND, "staff");

        private final WeaponType weaponType;
        private final String pdc;

        WeaponSubType(WeaponType weaponType, String pdc) {
            this.weaponType = weaponType;
            this.pdc = pdc;
        }
    }
}
