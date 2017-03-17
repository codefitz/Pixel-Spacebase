/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015  Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2016 Evan Debenham
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */
package com.wafitz.pixelspacebase.sprites;

public class ItemSpriteSheet {

    private static final int WIDTH = 16;

    private static int xy(int x, int y) {
        x -= 1;
        y -= 1;
        return x + WIDTH * y;
    }

    private static final int PLACEHOLDERS = xy(1, 1);   //8 slots
    //null warning occupies space 0, should only show up if there's a bug.
    public static final int NULLWARN = PLACEHOLDERS;
    public static final int WEAPON_HOLDER = PLACEHOLDERS + 1;
    public static final int ARMOR_HOLDER = PLACEHOLDERS + 2;
    public static final int MODULE_HOLDER = PLACEHOLDERS + 3;
    public static final int SOMETHING = PLACEHOLDERS + 4;

    private static final int UNCOLLECTIBLE = xy(9, 1);   //8 slots
    public static final int PARTS = UNCOLLECTIBLE;
    public static final int DEWDROP = UNCOLLECTIBLE + 1;
    public static final int HOLOBATTERY = UNCOLLECTIBLE + 2;
    public static final int SANDBAG = UNCOLLECTIBLE + 3;
    public static final int DBL_BOMB = UNCOLLECTIBLE + 4;

    private static final int CONTAINERS = xy(1, 2);   //16 slots
    public static final int BONES = CONTAINERS;
    public static final int REMAINS = CONTAINERS + 1;
    public static final int TOMB = CONTAINERS + 2;
    public static final int GRAVE = CONTAINERS + 3;
    public static final int CHEST = CONTAINERS + 4;
    public static final int LOCKED_CHEST = CONTAINERS + 5;
    public static final int CRYSTAL_CHEST = CONTAINERS + 6;

    private static final int SINGLE_USE = xy(1, 3);   //32 slots
    public static final int CLONE = SINGLE_USE;
    public static final int STYLUS = SINGLE_USE + 1;
    public static final int WEIGHT = SINGLE_USE + 2;
    public static final int FORCEFIELD = SINGLE_USE + 3;
    public static final int TORCH = SINGLE_USE + 4;
    public static final int BEACON = SINGLE_USE + 5;
    public static final int BOMB = SINGLE_USE + 6;
    public static final int HONEYPOT = SINGLE_USE + 7;
    public static final int SHATTPOT = SINGLE_USE + 8;
    public static final int IRON_KEY = SINGLE_USE + 9;
    public static final int GOLDEN_KEY = SINGLE_USE + 10;
    public static final int SKELETON_KEY = SINGLE_USE + 11;
    public static final int MASTERY = SINGLE_USE + 12;
    public static final int KIT = SINGLE_USE + 13;
    public static final int AMULET = SINGLE_USE + 14;

    //32 free slots

    private static final int WEP_TIER1 = xy(1, 7);   //8 slots
    public static final int WORN_SHORTSWORD = WEP_TIER1;
    public static final int CUDGEL = WEP_TIER1 + 1;
    public static final int KNUCKLEDUSTER = WEP_TIER1 + 2;
    public static final int RAPIER = WEP_TIER1 + 3;
    public static final int DAGGER = WEP_TIER1 + 4;
    public static final int DM3000_STAFF = WEP_TIER1 + 5;

    private static final int WEP_TIER2 = xy(9, 7);   //8 slots
    public static final int SHORTSWORD = WEP_TIER2;
    public static final int HAND_AXE = WEP_TIER2 + 1;
    public static final int SPEAR = WEP_TIER2 + 2;
    public static final int QUARTERSTAFF = WEP_TIER2 + 3;
    public static final int DIRK = WEP_TIER2 + 4;

    private static final int WEP_TIER3 = xy(1, 8);   //8 slots
    public static final int SWORD = WEP_TIER3;
    public static final int MACE = WEP_TIER3 + 1;
    public static final int SCIMITAR = WEP_TIER3 + 2;
    public static final int ROUND_SHIELD = WEP_TIER3 + 3;
    public static final int SAI = WEP_TIER3 + 4;
    public static final int WHIP = WEP_TIER3 + 5;

    private static final int WEP_TIER4 = xy(9, 8);   //8 slots
    public static final int LONGSWORD = WEP_TIER4;
    public static final int BATTLE_AXE = WEP_TIER4 + 1;
    public static final int FLAIL = WEP_TIER4 + 2;
    public static final int RUNIC_BLADE = WEP_TIER4 + 3;
    public static final int ASSASSINS_BLADE = WEP_TIER4 + 4;

    private static final int WEP_TIER5 = xy(1, 9);   //8 slots
    public static final int GREATSWORD = WEP_TIER5;
    public static final int WAR_HAMMER = WEP_TIER5 + 1;
    public static final int GLAIVE = WEP_TIER5 + 2;
    public static final int GREATAXE = WEP_TIER5 + 3;
    public static final int GREATSHIELD = WEP_TIER5 + 4;

    //8 free slots

    private static final int MISSILE_WEP = xy(1, 10);  //16 slots
    public static final int DART = MISSILE_WEP;
    public static final int BOOMERANG = MISSILE_WEP + 1;
    public static final int INCENDIARY_DART = MISSILE_WEP + 2;
    public static final int SHURIKEN = MISSILE_WEP + 3;
    public static final int CURARE_DART = MISSILE_WEP + 4;
    public static final int JAVELIN = MISSILE_WEP + 5;
    public static final int TOMAHAWK = MISSILE_WEP + 6;

    private static final int ARMOR = xy(1, 11);  //16 slots
    public static final int ARMOR_UNIFORM = ARMOR;
    public static final int ARMOR_SPACESUIT = ARMOR + 1;
    public static final int ARMOR_HUNTER = ARMOR + 2;
    public static final int HOVERPOD = ARMOR + 3;
    public static final int LOADER = ARMOR + 4;
    public static final int ARMOR_COMMANDER = ARMOR + 5;
    public static final int ARMOR_DM3000 = ARMOR + 6;
    public static final int ARMOR_SHAPESHIFTER = ARMOR + 7;
    public static final int ARMOR_CAPTAIN = ARMOR + 8;

    //32 free slots

    private static final int BLASTERS = xy(1, 14);  //16 slots
    public static final int MISSILEBLASTER = BLASTERS;
    public static final int FIREBLASTER = BLASTERS + 1;
    public static final int FREEZEBLASTER = BLASTERS + 2;
    public static final int LIGHTNINGBLASTER = BLASTERS + 3;
    public static final int DISINTEGRATOR = BLASTERS + 4;
    public static final int LIGHTBLASTER = BLASTERS + 5;
    public static final int VENOMBLASTER = BLASTERS + 6;
    public static final int EARTHBLASTER = BLASTERS + 7;
    public static final int WAVEBLASTER = BLASTERS + 8;
    public static final int MINDBLASTER = BLASTERS + 9;
    public static final int WARDINGBLASTER = BLASTERS + 10;
    public static final int EMP = BLASTERS + 11;
    public static final int TRANSFUSIONBLASTER = BLASTERS + 12;

    private static final int MODULES = xy(1, 15);  //16 slots
    public static final int MODULE_GARNET = MODULES;
    public static final int MODULE_RUBY = MODULES + 1;
    public static final int MODULE_TOPAZ = MODULES + 2;
    public static final int MODULE_EMERALD = MODULES + 3;
    public static final int MODULE_ONYX = MODULES + 4;
    public static final int MODULE_OPAL = MODULES + 5;
    public static final int MODULE_TOURMALINE = MODULES + 6;
    public static final int MODULE_SAPPHIRE = MODULES + 7;
    public static final int MODULE_AMETHYST = MODULES + 8;
    public static final int MODULE_QUARTZ = MODULES + 9;
    public static final int MODULE_AGATE = MODULES + 10;
    public static final int MODULE_DIAMOND = MODULES + 11;

    private static final int ARTIFACTS = xy(1, 16);  //32 slots
    public static final int ARTIFACT_CLOAK = ARTIFACTS;
    public static final int ARTIFACT_ARMBAND = ARTIFACTS + 1;
    public static final int ARTIFACT_CAPE = ARTIFACTS + 2;
    public static final int ARTIFACT_TALISMAN = ARTIFACTS + 3;
    public static final int ARTIFACT_HOURGLASS = ARTIFACTS + 4;
    public static final int ARTIFACT_TOOLKIT = ARTIFACTS + 5;
    public static final int ARTIFACT_COMPILER = ARTIFACTS + 6;
    public static final int ARTIFACT_BEACON = ARTIFACTS + 7;
    public static final int ARTIFACT_CHAINS = ARTIFACTS + 8;
    public static final int ARTIFACT_HORN1 = ARTIFACTS + 9;
    public static final int ARTIFACT_HORN2 = ARTIFACTS + 10;
    public static final int ARTIFACT_HORN3 = ARTIFACTS + 11;
    public static final int ARTIFACT_HORN4 = ARTIFACTS + 12;
    public static final int ARTIFACT_CHALICE1 = ARTIFACTS + 13;
    public static final int ARTIFACT_CHALICE2 = ARTIFACTS + 14;
    public static final int ARTIFACT_CHALICE3 = ARTIFACTS + 15;
    public static final int ARTIFACT_SANDALS = ARTIFACTS + 16;
    public static final int ARTIFACT_SHOES = ARTIFACTS + 17;
    public static final int ARTIFACT_BOOTS = ARTIFACTS + 18;
    public static final int ARTIFACT_GREAVES = ARTIFACTS + 19;
    public static final int ARTIFACT_HOLOPAD1 = ARTIFACTS + 20;
    public static final int ARTIFACT_HOLOPAD2 = ARTIFACTS + 21;
    public static final int ARTIFACT_HOLOPAD3 = ARTIFACTS + 22;

    //32 free slots

    private static final int SCRIPTS = xy(1, 20);  //16 slots
    public static final int KAUNAN_SCRIPT = SCRIPTS;
    public static final int SOWILO_SCRIPT = SCRIPTS + 1;
    public static final int LAGUZ_SCRIPT = SCRIPTS + 2;
    public static final int YNGVI_SCRIPT = SCRIPTS + 3;
    public static final int GYFU_SCRIPT = SCRIPTS + 4;
    public static final int RAIDO_SCRIPT = SCRIPTS + 5;
    public static final int ISAZ_SCRIPT = SCRIPTS + 6;
    public static final int MANNAZ_SCRIPT = SCRIPTS + 7;
    public static final int NAUDIZ_SCRIPT = SCRIPTS + 8;
    public static final int BERKANAN_SCRIPT = SCRIPTS + 9;
    public static final int ODAL_SCRIPT = SCRIPTS + 10;
    public static final int TIWAZ_SCRIPT = SCRIPTS + 11;

    private static final int EXPERIMENTALTECH = xy(1, 21);  //16 slots
    public static final int CRIMSON_EXPERIMENTAL_TECH = EXPERIMENTALTECH;
    public static final int AMBER_EXPERIMENTAL_TECH = EXPERIMENTALTECH + 1;
    public static final int GOLDEN_EXPERIMENTAL_TECH = EXPERIMENTALTECH + 2;
    public static final int JADE_EXPERIMENTAL_TECH = EXPERIMENTALTECH + 3;
    public static final int TURQUOISE_EXPERIMENTAL_TECH = EXPERIMENTALTECH + 4;
    public static final int AZURE_EXPERIMENTAL_TECH = EXPERIMENTALTECH + 5;
    public static final int INDIGO_EXPERIMENTAL_TECH = EXPERIMENTALTECH + 6;
    public static final int MAGENTA_EXPERIMENTAL_TECH = EXPERIMENTALTECH + 7;
    public static final int BISTRE_EXPERIMENTAL_TECH = EXPERIMENTALTECH + 8;
    public static final int CHARCOAL_EXPERIMENTAL_TECH = EXPERIMENTALTECH + 9;
    public static final int SILVER_EXPERIMENTAL_TECH = EXPERIMENTALTECH + 10;
    public static final int IVORY_EXPERIMENTAL_TECH = EXPERIMENTALTECH + 11;

    private static final int GADGETS = xy(1, 22);  //16 slots
    public static final int ROTBERRY_GADGET = GADGETS;
    public static final int FIREBLOOM_GADGET = GADGETS + 1;
    public static final int STARFLOWER_GADGET = GADGETS + 2;
    public static final int BLINDWEED_GADGET = GADGETS + 3;
    public static final int SUNGRASS_GADGET = GADGETS + 4;
    public static final int ICECAP_GADGET = GADGETS + 5;
    public static final int STORMVINE_GADGET = GADGETS + 6;
    public static final int SORROWMOSS_GADGET = GADGETS + 7;
    public static final int DREAMFOIL_GADGET = GADGETS + 8;
    public static final int EARTHROOT_GADGET = GADGETS + 9;
    public static final int FADELEAF_GADGET = GADGETS + 10;
    public static final int BLANDFRUIT_GADGET = GADGETS + 11;

    //32 free slots

    private static final int FOOD = xy(1, 25);  //16 slots
    public static final int MEAT = FOOD;
    public static final int STEAK = FOOD + 1;
    public static final int OVERPRICED = FOOD + 2;
    public static final int CARPACCIO = FOOD + 3;
    public static final int BLANDFRUIT = FOOD + 4;
    public static final int RATION = FOOD + 5;
    public static final int PASTY = FOOD + 6;
    public static final int PUMPKIN_PIE = FOOD + 7;
    public static final int CANDY_CANE = FOOD + 8;

    private static final int QUEST = xy(1, 26);  //32 slots
    public static final int SKULL = QUEST;
    public static final int DUST = QUEST + 1;
    public static final int CANDLE = QUEST + 2;
    public static final int EMBER = QUEST + 3;
    public static final int PICKAXE = QUEST + 4;
    public static final int ORE = QUEST + 5;
    public static final int TOKEN = QUEST + 6;

    private static final int BAGS = xy(1, 28);  //16 slots
    public static final int VIAL = BAGS;
    public static final int POUCH = BAGS + 1;
    public static final int HOLDER = BAGS + 2;
    public static final int BANDOLIER = BAGS + 3;
    public static final int HOLSTER = BAGS + 4;

    //64 free slots

}
