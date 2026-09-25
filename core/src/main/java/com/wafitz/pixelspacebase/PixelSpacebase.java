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
package com.wafitz.pixelspacebase;

import android.content.pm.ActivityInfo;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;

import com.wafitz.pixelspacebase.actors.mobs.HoodedRaiderCommander;
import com.wafitz.pixelspacebase.actors.mobs.ToughXeno;
import com.wafitz.pixelspacebase.items.armor.enhancements.Lockdown;
import com.wafitz.pixelspacebase.items.equippablemodules.PortableMaker;
import com.wafitz.pixelspacebase.items.blasters.DominationBlaster;
import com.wafitz.pixelspacebase.items.blasters.FlameThrower;
import com.wafitz.pixelspacebase.items.blasters.FreezeThrower;
import com.wafitz.pixelspacebase.items.blasters.MissileBlaster;
import com.wafitz.pixelspacebase.items.blasters.VenomBlaster;
import com.wafitz.pixelspacebase.items.blasters.WaveBlaster;
import com.wafitz.pixelspacebase.items.upgrades.EnhancementUpgrade;
import com.wafitz.pixelspacebase.items.weapon.enhancements.Buggy;
import com.wafitz.pixelspacebase.items.weapon.melee.Spanner;
import com.wafitz.pixelspacebase.messages.Languages;
import com.wafitz.pixelspacebase.mines.HunterTrapper;
import com.wafitz.pixelspacebase.scenes.GameScene;
import com.wafitz.pixelspacebase.scenes.PixelScene;
import com.wafitz.pixelspacebase.scenes.WelcomeScene;
import com.watabou.noosa.Game;
import com.watabou.noosa.RenderedText;
import com.watabou.noosa.audio.Music;
import com.watabou.noosa.audio.Sample;

import java.util.Locale;

import javax.microedition.khronos.opengles.GL10;

public class PixelSpacebase extends Game {

    public PixelSpacebase() {
        super(WelcomeScene.class);

        // 0.2.4
        com.watabou.utils.Bundle.addAlias(
                com.wafitz.pixelspacebase.items.weapon.enhancements.Shocking.class,
                "com.wafitz.pixelspacebase.items.weapon.enhancements.Piercing");
        com.watabou.utils.Bundle.addAlias(
                com.wafitz.pixelspacebase.items.weapon.enhancements.Shocking.class,
                "com.wafitz.pixelspacebase.items.weapon.enhancements.Swing");

        com.watabou.utils.Bundle.addAlias(
                EnhancementUpgrade.class,
                "com.wafitz.pixelspacebase.items.upgrades.WeaponUpgradePatch");

        // 0.2.4d
        com.watabou.utils.Bundle.addAlias(
                PortableMaker.class,
                "com.wafitz.pixelspacebase.items.PortableMaker");

        // 0.3.0, lots of blasters
        com.watabou.utils.Bundle.addAlias(
                VenomBlaster.class,
                "com.wafitz.pixelspacebase.items.blasters.VenomBlaster");
        com.watabou.utils.Bundle.addAlias(
                FreezeThrower.class,
                "com.wafitz.pixelspacebase.items.blasters.FreezeThrower");
        com.watabou.utils.Bundle.addAlias(
                FlameThrower.class,
                "com.wafitz.pixelspacebase.items.blasters.FlameThrower");
        com.watabou.utils.Bundle.addAlias(
                DominationBlaster.class,
                "com.wafitz.pixelspacebase.items.blasters.DominationBlaster");
        com.watabou.utils.Bundle.addAlias(
                WaveBlaster.class,
                "com.wafitz.pixelspacebase.items.blasters.WaveBlaster");
        com.watabou.utils.Bundle.addAlias(
                MissileBlaster.class,
                "com.wafitz.pixelspacebase.items.blasters.FlockBlaster");
        com.watabou.utils.Bundle.addAlias(
                MissileBlaster.class,
                "com.wafitz.pixelspacebase.items.blasters.TremorBlaster");
        com.watabou.utils.Bundle.addAlias(
                MissileBlaster.class,
                "com.wafitz.pixelspacebase.items.blasters.SpaceFolder");
        com.watabou.utils.Bundle.addAlias(
                MissileBlaster.class,
                "com.wafitz.pixelspacebase.items.blasters.Teleporter");

        //0.3.3
        com.watabou.utils.Bundle.addAlias(
                ToughXeno.class,
                "com.wafitz.pixelspacebase.actors.mobs.npcs.Hologram$ToughXeno");
        com.watabou.utils.Bundle.addAlias(
                HoodedRaiderCommander.class,
                "com.wafitz.pixelspacebase.actors.mobs.npcs.Hologram$HoodedRaiderCommander");
        com.watabou.utils.Bundle.addAlias(
                com.wafitz.pixelspacebase.actors.mobs.ArmoredCrawler.class,
                "com.wafitz.pixelspacebase.actors.mobs.npcs.Hologram$ArmoredCrawler");
        com.watabou.utils.Bundle.addAlias(
                HunterTrapper.class,
                "com.wafitz.pixelspacebase.actors.mobs.npcs.Quartermaster$HunterTrapper");
        com.watabou.utils.Bundle.addAlias(
                HunterTrapper.Device.class,
                "com.wafitz.pixelspacebase.actors.mobs.npcs.Quartermaster$HunterTrapper$Device");

        //0.4.0
        //equipment
        com.watabou.utils.Bundle.addAlias(
                Spanner.class,
                "com.wafitz.pixelspacebase.items.weapon.melee.ShortSword");
        //enhancements
        com.watabou.utils.Bundle.addAlias(
                com.wafitz.pixelspacebase.items.weapon.enhancements.Grim.class,
                "com.wafitz.pixelspacebase.items.weapon.enhancements.Death");
        com.watabou.utils.Bundle.addAlias(
                com.wafitz.pixelspacebase.items.weapon.enhancements.Blazing.class,
                "com.wafitz.pixelspacebase.items.weapon.enhancements.FireMine");
        com.watabou.utils.Bundle.addAlias(
                com.wafitz.pixelspacebase.items.weapon.enhancements.Eldritch.class,
                "com.wafitz.pixelspacebase.items.weapon.enhancements.Horror");
        com.watabou.utils.Bundle.addAlias(
                Buggy.class,
                "com.wafitz.pixelspacebase.items.weapon.enhancements.Instability");
        com.watabou.utils.Bundle.addAlias(
                com.wafitz.pixelspacebase.items.weapon.enhancements.Vampiric.class,
                "com.wafitz.pixelspacebase.items.weapon.enhancements.Leech");
        com.watabou.utils.Bundle.addAlias(
                com.wafitz.pixelspacebase.items.weapon.enhancements.Lucky.class,
                "com.wafitz.pixelspacebase.items.weapon.enhancements.Luck");
        com.watabou.utils.Bundle.addAlias(
                com.wafitz.pixelspacebase.items.weapon.enhancements.Stunning.class,
                "com.wafitz.pixelspacebase.items.weapon.enhancements.Paralysis");
        com.watabou.utils.Bundle.addAlias(
                com.wafitz.pixelspacebase.items.weapon.enhancements.Venomous.class,
                "com.wafitz.pixelspacebase.items.weapon.enhancements.Poison");
        com.watabou.utils.Bundle.addAlias(
                com.wafitz.pixelspacebase.items.weapon.enhancements.Shocking.class,
                "com.wafitz.pixelspacebase.items.weapon.enhancements.Shock");
        com.watabou.utils.Bundle.addAlias(
                com.wafitz.pixelspacebase.items.weapon.enhancements.Chilling.class,
                "com.wafitz.pixelspacebase.items.weapon.enhancements.TimeSink");

        com.watabou.utils.Bundle.addAlias(
                com.wafitz.pixelspacebase.items.armor.enhancements.Repulsion.class,
                "com.wafitz.pixelspacebase.items.armor.enhancements.Bounce");
        com.watabou.utils.Bundle.addAlias(
                com.wafitz.pixelspacebase.items.armor.enhancements.Repulsion.class,
                "com.wafitz.pixelspacebase.items.armor.enhancements.Displacement");
        com.watabou.utils.Bundle.addAlias(
                com.wafitz.pixelspacebase.items.armor.enhancements.Potential.class,
                "com.wafitz.pixelspacebase.items.armor.enhancements.AntiEntropy");
        com.watabou.utils.Bundle.addAlias(
                Lockdown.class,
                "com.wafitz.pixelspacebase.items.armor.enhancements.Metabolism");
        com.watabou.utils.Bundle.addAlias(
                Lockdown.class,
                "com.wafitz.pixelspacebase.items.armor.enhancements.Multiplicity");
        com.watabou.utils.Bundle.addAlias(
                com.wafitz.pixelspacebase.items.armor.enhancements.Repulsion.class,
                "com.wafitz.pixelspacebase.items.armor.enhancements.Gas");

        // v1.0.3 entity and level terminology cleanup. Keep existing saves loadable.
        com.watabou.utils.Bundle.addAlias(
                com.wafitz.pixelspacebase.actors.mobs.MaintenanceCrawler.class,
                "com.wafitz.pixelspacebase.actors.mobs.Crab");
        com.watabou.utils.Bundle.addAlias(
                com.wafitz.pixelspacebase.actors.mobs.ArmoredCrawler.class,
                "com.wafitz.pixelspacebase.actors.mobs.GreatCrab");
        com.watabou.utils.Bundle.addAlias(
                com.wafitz.pixelspacebase.actors.mobs.OuterColonyShockTrooper.class,
                "com.wafitz.pixelspacebase.actors.mobs.Brute");
        com.watabou.utils.Bundle.addAlias(
                com.wafitz.pixelspacebase.actors.mobs.ShieldedShockTrooper.class,
                "com.wafitz.pixelspacebase.actors.mobs.Shielded");
        com.watabou.utils.Bundle.addAlias(
                com.wafitz.pixelspacebase.actors.mobs.BithAcolyte.class,
                "com.wafitz.pixelspacebase.actors.mobs.Guard");
        com.watabou.utils.Bundle.addAlias(
                com.wafitz.pixelspacebase.actors.mobs.Jawar.class,
                "com.wafitz.pixelspacebase.actors.mobs.Thief");
        com.watabou.utils.Bundle.addAlias(
                com.wafitz.pixelspacebase.actors.mobs.JawarScavenger.class,
                "com.wafitz.pixelspacebase.actors.mobs.Bandit");
        com.watabou.utils.Bundle.addAlias(
                com.wafitz.pixelspacebase.actors.mobs.npcs.Quartermaster.class,
                "com.wafitz.pixelspacebase.actors.mobs.npcs.Gunsmith");
        com.watabou.utils.Bundle.addAlias(
                com.wafitz.pixelspacebase.actors.mobs.npcs.Y.class,
                "com.wafitz.pixelspacebase.actors.mobs.npcs.Arp");
        com.watabou.utils.Bundle.addAlias(
                com.wafitz.pixelspacebase.actors.mobs.npcs.YTrader.class,
                "com.wafitz.pixelspacebase.actors.mobs.npcs.ArpTrader");
        com.watabou.utils.Bundle.addAlias(
                com.wafitz.pixelspacebase.levels.MaintenanceLevel.class,
                "com.wafitz.pixelspacebase.levels.OperationsLevel");
        com.watabou.utils.Bundle.addAlias(
                com.wafitz.pixelspacebase.levels.MaintenanceBossLevel.class,
                "com.wafitz.pixelspacebase.levels.OperationsBossLevel");

        // v1.0.3 renamed persisted classes. These names are embedded in saves.
        registerCompatibilityAliases(new String[][]{
                {"actors.mobs.ArmoredCrawler", "actors.mobs.GreatCrab"},
                {"actors.mobs.BithAcolyte", "actors.mobs.Guard"},
                {"actors.mobs.ContainmentMass", "actors.mobs.Yog"},
                {"actors.mobs.Facehugger", "actors.mobs.Spinner"},
                {"actors.mobs.HolodeckMonarch", "actors.mobs.King"},
                {"actors.mobs.HoodedRaiderCommander", "actors.mobs.GnollTrickster"},
                {"actors.mobs.Jawar", "actors.mobs.Thief"},
                {"actors.mobs.JawarScavenger", "actors.mobs.Bandit"},
                {"actors.mobs.JedaKnight", "actors.mobs.Monk"},
                {"actors.mobs.MaintenanceCrawler", "actors.mobs.Crab"},
                {"actors.mobs.MaskedPrisoner", "actors.mobs.Tengu"},
                {"actors.mobs.OuterColonyPsion", "actors.mobs.Shaman"},
                {"actors.mobs.OuterColonyScout", "actors.mobs.Gnoll"},
                {"actors.mobs.OuterColonyShockTrooper", "actors.mobs.Brute"},
                {"actors.mobs.ReplicatorSwarm", "actors.mobs.Swarm"},
                {"actors.mobs.RupturedCrewSuit", "actors.mobs.Skeleton"},
                {"actors.mobs.ShieldedShockTrooper", "actors.mobs.Shielded"},
                {"actors.mobs.SignalLeech", "actors.mobs.Warlock"},
                {"actors.mobs.SignalSiren", "actors.mobs.Succubus"},
                {"actors.mobs.SiphonDrone", "actors.mobs.Bat"},
                {"actors.mobs.Turret", "actors.mobs.Wraith"},
                {"actors.mobs.WarMachine", "actors.mobs.Golem"},
                {"actors.mobs.npcs.ContainmentEcho", "actors.mobs.npcs.Sheep"},
                {"actors.mobs.npcs.Leonard", "actors.mobs.npcs.Blacksmith"},
                {"actors.mobs.npcs.Quartermaster", "actors.mobs.npcs.Gunsmith"},
                {"actors.mobs.npcs.QueenXeno", "actors.mobs.npcs.RatKing"},
                {"actors.mobs.npcs.WeakClone", "actors.mobs.npcs.MirrorImage"},
                {"actors.mobs.npcs.Y", "actors.mobs.npcs.Imp"},
                {"actors.mobs.npcs.YTrader", "actors.mobs.npcs.ImpMakerBot"},
                {"actors.buffs.CombatFocus", "actors.buffs.Upgrade"},
                {"items.EscapePodOverride", "items.Amulet"},
                {"items.MedigelContainer", "items.AirTank"},
                {"items.MedigelDroplet", "items.Dewdrop"},
                {"items.TrainingManual", "items.TomeOfMastery"},
                {"items.WeaponTuner", "items.Weightstone"},
                {"items.armor.enhancements.Fire", "items.armor.enhancements.Brimstone"},
                {"items.blasters.Disintegrator", "items.blasters.Disintergrator"},
                {"items.blasters.DominationBlaster", "items.blasters.MindBlaster"},
                {"items.blasters.FlameThrower", "items.blasters.FireBlaster"},
                {"items.blasters.FreezeThrower", "items.blasters.FreezeBlaster"},
                {"items.blasters.LazerGun", "items.blasters.LightBlaster"},
                {"items.blasters.ShockBlaster", "items.blasters.LightningBlaster"},
                {"items.blasters.VampiricBlaster", "items.blasters.TransfusionBlaster"},
                {"items.containers.OrdnanceKit", "items.containers.GadgetCase"},
                {"items.containers.PlasmidKit", "items.containers.ScriptLibrary"},
                {"items.containers.UtilityKit", "items.containers.XPort"},
                {"items.equippablemodules.AlienDNA", "items.artifacts.AlienDNA"},
                {"items.equippablemodules.BuggyCompiler", "items.artifacts.BuggyCompiler"},
                {"items.equippablemodules.EquippableModule", "items.artifacts.Artifact"},
                {"items.equippablemodules.FrontierTechShield", "items.artifacts.GnollTechShield"},
                {"items.equippablemodules.GravityGun", "items.artifacts.GravityGun"},
                {"items.equippablemodules.HoloPad", "items.artifacts.HoloPad"},
                {"items.equippablemodules.McGyvrModule", "items.artifacts.McGyvrModule"},
                {"items.equippablemodules.PortableMaker", "items.artifacts.LloydsBeacon"},
                {"items.equippablemodules.StealthModule", "items.artifacts.StealthModule"},
                {"items.equippablemodules.StrongForcefield", "items.artifacts.StrongForcefield"},
                {"items.equippablemodules.SurveyorModule", "items.artifacts.SurveyorModule"},
                {"items.equippablemodules.SurvivalModule", "items.artifacts.SurvivalModule"},
                {"items.equippablemodules.TechToolkit", "items.artifacts.MakersToolkit"},
                {"items.equippablemodules.TimeFolder", "items.artifacts.TimeFolder"},
                {"items.food.AlienPod", "items.ExperimentalTech.AlienTech"},
                {"items.keys.MasterKeycard", "items.keys.SkeletonKey"},
                {"items.plasmids.CloakPlasmid", "items.ExperimentalTech.InvisibilityTech"},
                {"items.plasmids.CryoGrenade", "items.ExperimentalTech.FrostTech"},
                {"items.plasmids.ExperiencePlasmid", "items.ExperimentalTech.ExperienceTech"},
                {"items.plasmids.FireGrenade", "items.ExperimentalTech.FireTech"},
                {"items.plasmids.GravLiftPlasmid", "items.ExperimentalTech.RocketTech"},
                {"items.plasmids.HealingPlasmid", "items.ExperimentalTech.HealingTech"},
                {"items.plasmids.MyoFiberPlasmid", "items.ExperimentalTech.StrengthTech"},
                {"items.plasmids.ParalysisGrenade", "items.ExperimentalTech.ParalyzingTech"},
                {"items.plasmids.Plasmid", "items.ExperimentalTech.ExperimentalTech"},
                {"items.plasmids.PolymerPlasmid", "items.ExperimentalTech.PolymerMembrane"},
                {"items.plasmids.SecurityPlasmid", "items.ExperimentalTech.SecurityTech"},
                {"items.plasmids.TitanPlasmid", "items.ExperimentalTech.PowerTech"},
                {"items.plasmids.ToxicGrenade", "items.ExperimentalTech.ToxicGasTech"},
                {"items.quest.HardLightEmitter", "items.quest.DwarfToken"},
                {"items.quest.Lazer", "items.quest.CorpseDust"},
                {"items.quest.ScrewDriver", "items.quest.Pickaxe"},
                {"items.quest.SpareBaseParts", "items.quest.DarkMetals"},
                {"items.upgrades.DiagnosticScanUpgrade", "items.scripts.IdentifyScript"},
                {"items.upgrades.EchoLocationUpgrade", "items.scripts.EchoLocationScript"},
                {"items.upgrades.EnhancementUpgrade", "items.scripts.EnhancementScript"},
                {"items.upgrades.InventoryUpgrade", "items.scripts.InventoryScript"},
                {"items.upgrades.KnockoutUpgrade", "items.scripts.KnockoutScript"},
                {"items.upgrades.MappingUpgrade", "items.scripts.MappingScript"},
                {"items.upgrades.PanicUpgrade", "items.scripts.TerrorScript"},
                {"items.upgrades.PhaseShiftUpgrade", "items.scripts.TeleportationScript"},
                {"items.upgrades.PsionicBlastUpgrade", "items.scripts.PsionicBlastScript"},
                {"items.upgrades.RechargeUpgrade", "items.scripts.RechargingScript"},
                {"items.upgrades.RepairUpgrade", "items.scripts.FixScript"},
                {"items.upgrades.Upgrade", "items.scripts.Script"},
                {"items.upgrades.UpgradePatch", "items.scripts.UpgradeScript"},
                {"items.upgrades.WeakCloneUpgrade", "items.scripts.WeakCloneScript"},
                {"items.weapon.melee.BrightHammer", "items.weapon.melee.WarHammer"},
                {"items.weapon.melee.BrightSaber", "items.weapon.melee.RunicBlade"},
                {"items.weapon.melee.DM3000Launcher", "items.weapon.melee.DM3000Staff"},
                {"items.weapon.melee.DarkSaber", "items.weapon.melee.AssassinsBlade"},
                {"items.weapon.melee.Drill", "items.weapon.melee.Quarterstaff"},
                {"items.weapon.melee.DualBlade", "items.weapon.melee.Greatsword"},
                {"items.weapon.melee.HoloAxe", "items.weapon.melee.BattleAxe"},
                {"items.weapon.melee.HoloScimitar", "items.weapon.melee.Scimitar"},
                {"items.weapon.melee.LazerSword", "items.weapon.melee.Sword"},
                {"items.weapon.melee.LazerWhip", "items.weapon.melee.Whip"},
                {"items.weapon.melee.MCPickAxe", "items.weapon.melee.HandAxe"},
                {"items.weapon.melee.OrbMelder", "items.weapon.melee.Glaive"},
                {"items.weapon.melee.RaiderBlade", "items.weapon.melee.Longsword"},
                {"items.weapon.melee.Spade", "items.weapon.melee.Mace"},
                {"items.weapon.melee.Spanner", "items.weapon.melee.WornShortsword"},
                {"items.weapon.melee.UltonAxe", "items.weapon.melee.Greataxe"},
                {"items.weapon.melee.UltonShield", "items.weapon.melee.RoundShield"},
                {"items.weapon.melee.Wrench", "items.weapon.melee.NewShortsword"},
                {"items.weapon.missiles.HunterDisc", "items.weapon.missiles.Boomerang"},
                {"items.weapon.missiles.HunterJavelin", "items.weapon.missiles.Javelin"},
                {"items.weapon.missiles.Nanobots", "items.weapon.missiles.Tamahawk"},
                {"levels.DeepContainmentCoreLevel", "levels.HallsBossLevel"},
                {"levels.DeepContainmentDeckLevel", "levels.HallsLevel"},
                {"levels.EngineeringBossLevel", "levels.CavesBossLevel"},
                {"levels.EngineeringLevel", "levels.CavesLevel"},
                {"levels.HabitationRingLevel", "levels.CityLevel"},
                {"levels.HolodeckBossLevel", "levels.CityBossLevel"},
                {"levels.MaintenanceBossLevel", "levels.OperationsBossLevel"},
                {"levels.MaintenanceLevel", "levels.OperationsLevel"},
                {"levels.SecurityBlockLevel", "levels.PrisonLevel"},
                {"levels.SecurityBossLevel", "levels.PrisonBossLevel"},
                {"mines.AdrenalBoost", "triggers.Boost"},
                {"mines.AlienEgg", "triggers.AlienPlant"},
                {"mines.DisorientationMine", "triggers.Disorient"},
                {"mines.FireMine", "triggers.FireTrigger"},
                {"mines.FlashMine", "triggers.Blinding"},
                {"mines.HunterTrapper", "triggers.AlienTrap"},
                {"mines.IceMine", "triggers.IceTrigger"},
                {"mines.KnockoutMine", "triggers.Knockout"},
                {"mines.KoltoPod", "triggers.Healing"},
                {"mines.Mine", "triggers.Trigger"},
                {"mines.TeleportationPod", "triggers.Teleportation"},
                {"mines.VenomMine", "triggers.Venom"},
                {"mines.WeakForcefield", "triggers.WeakForcefield"}
        });

        com.watabou.utils.Bundle.exceptionReporter =
                new com.watabou.utils.Bundle.BundleExceptionCallback() {
                    @Override
                    public void call(Throwable t) {
                        PixelSpacebase.reportException(t);
                    }
                };

    }

    private static void registerCompatibilityAliases(String[][] aliases) {
        final String packageName = "com.wafitz.pixelspacebase.";
        for (String[] alias : aliases) {
            try {
                com.watabou.utils.Bundle.addAlias(
                        Class.forName(packageName + alias[0]), packageName + alias[1]);
            } catch (ClassNotFoundException e) {
                throw new IllegalStateException("Missing compatibility alias target: " + alias[0], e);
            }
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        updateImmersiveMode();

        if (Preferences.INSTANCE.contains(Preferences.KEY_LANDSCAPE)) {
            landscape(Preferences.INSTANCE.getBoolean(Preferences.KEY_LANDSCAPE, false));

        } else {
            DisplayMetrics metrics = new DisplayMetrics();
            if (immersed() && Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR1)
                getWindowManager().getDefaultDisplay().getRealMetrics(metrics);
            else
                getWindowManager().getDefaultDisplay().getMetrics(metrics);
            boolean landscape = metrics.widthPixels > metrics.heightPixels;

            landscape(landscape);
        }

        Music.INSTANCE.enable(music());
        Sample.INSTANCE.enable(soundFx());
        Sample.INSTANCE.volume(SFXVol() / 10f);

        Sample.INSTANCE.load(
                Assets.SND_CLICK,
                Assets.SND_BADGE,
                Assets.SND_GOLD,

                Assets.SND_STEP,
                Assets.SND_WATER,
                Assets.SND_OPEN,
                Assets.SND_UNLOCK,
                Assets.SND_ITEM,
                Assets.SND_MEDIGEL,
                Assets.SND_HIT,
                Assets.SND_MISS,

                Assets.SND_DESCEND,
                Assets.SND_EAT,
                Assets.SND_READ,
                Assets.SND_LULLABY,
                Assets.SND_DRINK,
                Assets.SND_SHATTER,
                Assets.SND_ZAP,
                Assets.SND_LIGHTNING,
                Assets.SND_LEVELUP,
                Assets.SND_DEATH,
                Assets.SND_CHALLENGE,
                Assets.SND_CURSED,
                Assets.SND_EVOKE,
                Assets.SND_TRAP,
                Assets.SND_TOMB,
                Assets.SND_ALERT,
                Assets.SND_MELD,
                Assets.SND_BOSS,
                Assets.SND_BLAST,
                Assets.SND_PLANT,
                Assets.SND_RAY,
                Assets.SND_BEACON,
                Assets.SND_TELEPORT,
                Assets.SND_CHARMS,
                Assets.SND_MASTERY,
                Assets.SND_PUFF,
                Assets.SND_ROCKS,
                Assets.SND_BURNING,
                Assets.SND_FALLING,
                Assets.SND_HOLOGRAM,
                Assets.SND_SECRET,
                Assets.SND_BONES,
                Assets.SND_BEE,
                Assets.SND_DEGRADE,
                Assets.SND_MIMIC);

        if (classicFont()) {
            RenderedText.setFont("pixelfont.ttf");
        } else {
            RenderedText.setFontFamily("sans-serif-condensed", Typeface.BOLD);
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {

        super.onWindowFocusChanged(hasFocus);

        if (hasFocus) {
            updateImmersiveMode();
        }
    }

    public static void switchNoFade(Class<? extends PixelScene> c) {
        switchNoFade(c, null);
    }

    public static void switchNoFade(Class<? extends PixelScene> c, SceneChangeCallback callback) {
        PixelScene.noFade = true;
        switchScene(c, callback);
    }

	/*
     * ---> Prefernces
	 */

    public static void landscape(boolean value) {
        Game.instance.setRequestedOrientation(value ?
                ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE :
                ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
        Preferences.INSTANCE.put(Preferences.KEY_LANDSCAPE, value);
        ((PixelSpacebase) instance).updateDisplaySize();
    }

    public static boolean landscape() {
        return width > height;
    }

    public static void scale(int value) {
        Preferences.INSTANCE.put(Preferences.KEY_SCALE, value);
    }

    private static boolean immersiveModeChanged = false;

    public static void immerse(boolean value) {
        Preferences.INSTANCE.put(Preferences.KEY_IMMERSIVE, value);

        instance.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                updateImmersiveMode();
                immersiveModeChanged = true;
                //ensures surfacechanged is called if the view was previously set to be fixed.
                ((PixelSpacebase) instance).view.getHolder().setSizeFromLayout();
            }
        });
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {

        super.onSurfaceChanged(gl, width, height);

        updateDisplaySize();

        if (immersiveModeChanged) {
            requestedReset = true;
            immersiveModeChanged = false;
        }
    }

    private void updateDisplaySize() {
        DisplayMetrics m = new DisplayMetrics();
        if (immersed() && Build.VERSION.SDK_INT >= 19)
            getWindowManager().getDefaultDisplay().getRealMetrics(m);
        else
            getWindowManager().getDefaultDisplay().getMetrics(m);
        dispHeight = m.heightPixels;
        dispWidth = m.widthPixels;

        float dispRatio = dispWidth / (float) dispHeight;

        float renderWidth = dispRatio > 1 ? PixelScene.MIN_WIDTH_L : PixelScene.MIN_WIDTH_P;
        float renderHeight = dispRatio > 1 ? PixelScene.MIN_HEIGHT_L : PixelScene.MIN_HEIGHT_P;

        //force power saver in this case as all devices must run at at least 2x scale.
        if (dispWidth < renderWidth * 2 || dispHeight < renderHeight * 2)
            Preferences.INSTANCE.put(Preferences.KEY_POWER_SAVER, true);

        if (powerSaver()) {

            int maxZoom = (int) Math.min(dispWidth / renderWidth, dispHeight / renderHeight);

            renderWidth *= Math.max(2, Math.round(1f + maxZoom * 0.4f));
            renderHeight *= Math.max(2, Math.round(1f + maxZoom * 0.4f));

            if (dispRatio > renderWidth / renderHeight) {
                renderWidth = renderHeight * dispRatio;
            } else {
                renderHeight = renderWidth / dispRatio;
            }

            final int finalW = Math.round(renderWidth);
            final int finalH = Math.round(renderHeight);
            if (finalW != width || finalH != height) {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        view.getHolder().setFixedSize(finalW, finalH);
                    }
                });

            }
        } else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    view.getHolder().setSizeFromLayout();
                }
            });
        }
    }

    public static void updateImmersiveMode() {
        if (android.os.Build.VERSION.SDK_INT >= 19) {
            try {
                // Sometime NullPointerException happens here
                instance.getWindow().getDecorView().setSystemUiVisibility(
                        immersed() ?
                                View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                                        View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
                                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                                        View.SYSTEM_UI_FLAG_FULLSCREEN |
                                        View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                                :
                                0);
            } catch (Exception e) {
                reportException(e);
            }
        }
    }

    public static boolean immersed() {
        return Preferences.INSTANCE.getBoolean(Preferences.KEY_IMMERSIVE, false);
    }

    public static boolean powerSaver() {
        return Preferences.INSTANCE.getBoolean(Preferences.KEY_POWER_SAVER, false);
    }

    public static void powerSaver(boolean value) {
        Preferences.INSTANCE.put(Preferences.KEY_POWER_SAVER, value);
        ((PixelSpacebase) instance).updateDisplaySize();
    }

    public static int scale() {
        return Preferences.INSTANCE.getInt(Preferences.KEY_SCALE, 0);
    }

    public static void zoom(int value) {
        Preferences.INSTANCE.put(Preferences.KEY_ZOOM, value);
    }

    public static int zoom() {
        return Preferences.INSTANCE.getInt(Preferences.KEY_ZOOM, 0);
    }

    public static void music(boolean value) {
        Music.INSTANCE.enable(value);
        Music.INSTANCE.volume(musicVol() / 10f);
        Preferences.INSTANCE.put(Preferences.KEY_MUSIC, value);
    }

    public static boolean music() {
        return Preferences.INSTANCE.getBoolean(Preferences.KEY_MUSIC, true);
    }

    public static void nowPlaying(boolean value) {
        Preferences.INSTANCE.put(Preferences.KEY_NOW_PLAYING, value);
    }

    public static boolean nowPlaying() {
        return Preferences.INSTANCE.getBoolean(Preferences.KEY_NOW_PLAYING, true);
    }

    public static void musicVol(int value) {
        Preferences.INSTANCE.put(Preferences.KEY_MUSIC_VOL, value);
    }

    public static int musicVol() {
        return Preferences.INSTANCE.getInt(Preferences.KEY_MUSIC_VOL, 10, 0, 10);
    }

    public static void soundFx(boolean value) {
        Sample.INSTANCE.enable(value);
        Preferences.INSTANCE.put(Preferences.KEY_SOUND_FX, value);
    }

    public static boolean soundFx() {
        return Preferences.INSTANCE.getBoolean(Preferences.KEY_SOUND_FX, true);
    }

    public static void SFXVol(int value) {
        Preferences.INSTANCE.put(Preferences.KEY_SFX_VOL, value);
    }

    public static int SFXVol() {
        return Preferences.INSTANCE.getInt(Preferences.KEY_SFX_VOL, 10, 0, 10);
    }

    public static void brightness(int value) {
        Preferences.INSTANCE.put(Preferences.KEY_BRIGHTNESS, value);
        GameScene.updateFog();
    }

    public static int brightness() {
        return Preferences.INSTANCE.getInt(Preferences.KEY_BRIGHTNESS, 0, -2, 2);
    }

    public static void language(Languages lang) {
        Preferences.INSTANCE.put(Preferences.KEY_LANG, lang.code());
    }

    public static Languages language() {
        String code = Preferences.INSTANCE.getString(Preferences.KEY_LANG, null);
        if (code == null) {
            Languages lang = Languages.matchLocale(Locale.getDefault());
            if (lang.status() == Languages.Status.REVIEWED)
                return lang;
            else
                return Languages.ENGLISH;
        } else return Languages.matchCode(code);
    }

    public static void classicFont(boolean classic) {
        Preferences.INSTANCE.put(Preferences.KEY_CLASSICFONT, classic);
        if (classic) {
            RenderedText.setFont("pixelfont.ttf");
        } else {
            RenderedText.setFontFamily("sans-serif-condensed", Typeface.BOLD);
        }
    }

    public static boolean classicFont() {
        return Preferences.INSTANCE.getBoolean(Preferences.KEY_CLASSICFONT, false);
    }

    public static void lastClass(int value) {
        Preferences.INSTANCE.put(Preferences.KEY_LAST_CLASS, value);
    }

    public static int lastClass() {
        return Preferences.INSTANCE.getInt(Preferences.KEY_LAST_CLASS, 0, 0, 3);
    }

    public static void challenges(int value) {
        Preferences.INSTANCE.put(Preferences.KEY_CHALLENGES, value);
    }

    public static int challenges() {
        return Preferences.INSTANCE.getInt(Preferences.KEY_CHALLENGES, 0, 0, Challenges.MAX_VALUE);
    }

    public static void quickSlots(int value) {
        Preferences.INSTANCE.put(Preferences.KEY_QUICKSLOTS, value);
    }

    public static int quickSlots() {
        return Preferences.INSTANCE.getInt(Preferences.KEY_QUICKSLOTS, 4, 0, 4);
    }

    public static void flipToolbar(boolean value) {
        Preferences.INSTANCE.put(Preferences.KEY_FLIPTOOLBAR, value);
    }

    public static boolean flipToolbar() {
        return Preferences.INSTANCE.getBoolean(Preferences.KEY_FLIPTOOLBAR, false);
    }

    public static void flipTags(boolean value) {
        Preferences.INSTANCE.put(Preferences.KEY_FLIPTAGS, value);
    }

    public static boolean flipTags() {
        return Preferences.INSTANCE.getBoolean(Preferences.KEY_FLIPTAGS, false);
    }

    public static void toolbarMode(String value) {
        Preferences.INSTANCE.put(Preferences.KEY_BARMODE, value);
    }

    public static String toolbarMode() {
        return Preferences.INSTANCE.getString(Preferences.KEY_BARMODE, !landscape() ? "SPLIT" : "GROUP");
    }

    public static void intro(boolean value) {
        Preferences.INSTANCE.put(Preferences.KEY_INTRO, value);
    }

    public static boolean intro() {
        return Preferences.INSTANCE.getBoolean(Preferences.KEY_INTRO, true);
    }

    public static void version(int value) {
        Preferences.INSTANCE.put(Preferences.KEY_VERSION, value);
    }

    public static int version() {
        return Preferences.INSTANCE.getInt(Preferences.KEY_VERSION, 0);
    }

	/*
	 * <--- Preferences
	 */

    public static void reportException(Throwable tr) {
        Log.e("PD", Log.getStackTraceString(tr));
    }
}
