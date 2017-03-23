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
package com.wafitz.pixelspacebase.items;

import com.wafitz.pixelspacebase.Assets;
import com.wafitz.pixelspacebase.Badges;
import com.wafitz.pixelspacebase.Dungeon;
import com.wafitz.pixelspacebase.PixelSpacebase;
import com.wafitz.pixelspacebase.Statistics;
import com.wafitz.pixelspacebase.actors.buffs.Buff;
import com.wafitz.pixelspacebase.actors.buffs.Burning;
import com.wafitz.pixelspacebase.actors.buffs.Frost;
import com.wafitz.pixelspacebase.actors.hero.Hero;
import com.wafitz.pixelspacebase.actors.mobs.ConfusedShapeshifter;
import com.wafitz.pixelspacebase.actors.mobs.Turret;
import com.wafitz.pixelspacebase.effects.CellEmitter;
import com.wafitz.pixelspacebase.effects.Speck;
import com.wafitz.pixelspacebase.effects.Splash;
import com.wafitz.pixelspacebase.effects.particles.ElmoParticle;
import com.wafitz.pixelspacebase.effects.particles.FlameParticle;
import com.wafitz.pixelspacebase.effects.particles.ShadowParticle;
import com.wafitz.pixelspacebase.items.ExperimentalTech.ExperienceBooster;
import com.wafitz.pixelspacebase.items.ExperimentalTech.ExperimentalTech;
import com.wafitz.pixelspacebase.items.ExperimentalTech.HealingTech;
import com.wafitz.pixelspacebase.items.ExperimentalTech.PowerUpgrade;
import com.wafitz.pixelspacebase.items.ExperimentalTech.StrengthUpgrade;
import com.wafitz.pixelspacebase.items.artifacts.Artifact;
import com.wafitz.pixelspacebase.items.artifacts.TechToolkit;
import com.wafitz.pixelspacebase.items.blasters.Blaster;
import com.wafitz.pixelspacebase.items.food.AlienPod;
import com.wafitz.pixelspacebase.items.food.ChargrilledMeat;
import com.wafitz.pixelspacebase.items.food.FrozenCarpaccio;
import com.wafitz.pixelspacebase.items.food.MysteryMeat;
import com.wafitz.pixelspacebase.items.scripts.EnhancementScript;
import com.wafitz.pixelspacebase.items.scripts.Script;
import com.wafitz.pixelspacebase.items.scripts.UpgradeScript;
import com.wafitz.pixelspacebase.messages.Messages;
import com.wafitz.pixelspacebase.mines.Mine;
import com.wafitz.pixelspacebase.mines.Mine.Device;
import com.wafitz.pixelspacebase.sprites.ItemSprite;
import com.wafitz.pixelspacebase.sprites.ItemSpriteSheet;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundlable;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;

public class Heap implements Bundlable {

    private static final int DEVICES_TO_TECH = 3;

    public enum Type {
        HEAP,
        TO_MAKE,
        CHEST,
        LOCKED_CHEST,
        CRYSTAL_CHEST,
        TOMB,
        SKELETON,
        REMAINS,
        CONFUSEDSHAPESHIFTER
    }

    public Type type = Type.HEAP;

    public int pos = 0;

    public ItemSprite sprite;
    public boolean seen = false;

    public LinkedList<Item> items = new LinkedList<>();

    public int image() {
        switch (type) {
            case HEAP:
            case TO_MAKE:
                return size() > 0 ? items.peek().image() : 0;
            case CHEST:
            case CONFUSEDSHAPESHIFTER:
                return ItemSpriteSheet.CHEST;
            case LOCKED_CHEST:
                return ItemSpriteSheet.LOCKED_CHEST;
            case CRYSTAL_CHEST:
                return ItemSpriteSheet.CRYSTAL_CHEST;
            case TOMB:
                return ItemSpriteSheet.REDTERMINAL;
            case SKELETON:
                return ItemSpriteSheet.DISCARDEDSUIT;
            case REMAINS:
                return ItemSpriteSheet.REMAINS;
            default:
                return 0;
        }
    }

    public ItemSprite.Glowing glowing() {
        return (type == Type.HEAP || type == Type.TO_MAKE) && items.size() > 0 ? items.peek().glowing() : null;
    }

    public void open(Hero hero) {
        switch (type) {
            case CONFUSEDSHAPESHIFTER:
                if (ConfusedShapeshifter.spawnAt(pos, items) != null) {
                    destroy();
                } else {
                    type = Type.CHEST;
                }
                break;
            case TOMB:
                Turret.spawnAround(hero.pos);
                break;
            case REMAINS:
            case SKELETON:
                CellEmitter.center(pos).start(Speck.factory(Speck.RATTLE), 0.1f, 3);
                for (Item item : items) {
                    if (item.malfunctioning) {
                        if (Turret.spawnAt(pos) == null) {
                            hero.sprite.emitter().burst(ShadowParticle.MALFUNCTION, 6);
                            hero.damage(hero.HP / 2, this);
                        }
                        Sample.INSTANCE.play(Assets.SND_CURSED);
                        break;
                    }
                }
                break;
            default:
        }

        if (type != Type.CONFUSEDSHAPESHIFTER) {
            type = Type.HEAP;
            sprite.link();
            sprite.drop();
        }
    }

    public int size() {
        return items.size();
    }

    public Item pickUp() {

        Item item = items.removeFirst();
        if (items.isEmpty()) {
            destroy();
        } else if (sprite != null) {
            sprite.view(image(), glowing());
        }

        return item;
    }

    public Item peek() {
        return items.peek();
    }

    public void drop(Item item) {

        if (item.stackable && type != Type.TO_MAKE) {

            for (Item i : items) {
                if (i.isSimilar(item)) {
                    i.quantity += item.quantity;
                    item = i;
                    break;
                }
            }
            items.remove(item);

        }

        if (item instanceof Dewdrop && type != Type.TO_MAKE) {
            items.add(item);
        } else {
            items.addFirst(item);
        }

        if (sprite != null) {
            if (type == Type.HEAP || type == Type.TO_MAKE)
                sprite.view(items.peek());
            else
                sprite.view(image(), glowing());
        }
    }

    public void replace(Item a, Item b) {
        int index = items.indexOf(a);
        if (index != -1) {
            items.remove(index);
            items.add(index, b);
        }
    }

    public void burn() {

        if (type == Type.CONFUSEDSHAPESHIFTER) {
            ConfusedShapeshifter m = ConfusedShapeshifter.spawnAt(pos, items);
            if (m != null) {
                Buff.affect(m, Burning.class).reignite(m);
                m.sprite.emitter().burst(FlameParticle.FACTORY, 5);
                destroy();
            }
        }

        if (type != Type.HEAP) {
            return;
        }

        boolean burnt = false;
        boolean evaporated = false;

        for (Item item : items.toArray(new Item[0])) {
            if (item instanceof Script
                    && !(item instanceof UpgradeScript || item instanceof EnhancementScript)) {
                items.remove(item);
                burnt = true;
            } else if (item instanceof Dewdrop) {
                items.remove(item);
                evaporated = true;
            } else if (item instanceof MysteryMeat) {
                replace(item, ChargrilledMeat.make((MysteryMeat) item));
                burnt = true;
            } else if (item instanceof Bomb) {
                items.remove(item);
                ((Bomb) item).explode(pos);
                //stop processing the burning, it will be replaced by the explosion.
                return;
            }
        }

        if (burnt || evaporated) {

            if (Dungeon.visible[pos]) {
                if (burnt) {
                    burnFX(pos);
                } else {
                    evaporateFX(pos);
                }
            }

            if (isEmpty()) {
                destroy();
            } else if (sprite != null) {
                sprite.view(items.peek());
            }

        }
    }

    //Note: should not be called to initiate an explosion, but rather by an explosion that is happening.
    public void explode() {

        //breaks open most standard containers, mimics die.
        if (type == Type.CONFUSEDSHAPESHIFTER || type == Type.CHEST || type == Type.SKELETON) {
            type = Type.HEAP;
            sprite.link();
            sprite.drop();
            return;
        }

        if (type != Type.HEAP) {

        } else {

            for (Item item : items.toArray(new Item[0])) {

                if (item instanceof ExperimentalTech) {
                    items.remove(item);
                    ((ExperimentalTech) item).shatter(pos);

                } else if (item instanceof Bomb) {
                    items.remove(item);
                    ((Bomb) item).explode(pos);
                    //stop processing current explosion, it will be replaced by the new one.
                    return;

                    //unique and upgraded items can endure the blast
                } else if (!(item.level() > 0 || item.unique))
                    items.remove(item);

            }

            if (isEmpty()) {
                destroy();
            } else if (sprite != null) {
                sprite.view(items.peek());
            }
        }
    }

    public void freeze() {

        if (type == Type.CONFUSEDSHAPESHIFTER) {
            ConfusedShapeshifter m = ConfusedShapeshifter.spawnAt(pos, items);
            if (m != null) {
                Buff.prolong(m, Frost.class, Frost.duration(m) * Random.Float(1.0f, 1.5f));
                destroy();
            }
        }

        if (type != Type.HEAP) {
            return;
        }

        boolean frozen = false;
        for (Item item : items.toArray(new Item[0])) {
            if (item instanceof MysteryMeat) {
                replace(item, FrozenCarpaccio.make((MysteryMeat) item));
                frozen = true;
            } else if (item instanceof ExperimentalTech
                    && !(item instanceof StrengthUpgrade || item instanceof PowerUpgrade)) {
                items.remove(item);
                ((ExperimentalTech) item).shatter(pos);
                frozen = true;
            } else if (item instanceof Bomb) {
                ((Bomb) item).fuse = null;
                frozen = true;
            }
        }

        if (frozen) {
            if (isEmpty()) {
                destroy();
            } else if (sprite != null) {
                sprite.view(items.peek());
            }
        }
    }

    public Item transmute() {

        CellEmitter.get(pos).burst(Speck.factory(Speck.BUBBLE), 3);
        Splash.at(pos, 0xFFFFFF, 3);

        float chances[] = new float[items.size()];
        int count = 0;


        if (items.size() == 2 && items.get(0) instanceof Mine.Device && items.get(1) instanceof AlienPod) {

            Sample.INSTANCE.play(Assets.SND_PUFF);
            CellEmitter.center(pos).burst(Speck.factory(Speck.EVOKE), 3);

            AlienPod result = new AlienPod();
            result.make((Mine.Device) items.get(0));

            destroy();

            return result;

        }

        int index = 0;
        for (Item item : items) {
            if (item instanceof Mine.Device) {
                count += item.quantity;
                chances[index++] = item.quantity;
            } else {
                count = 0;
                break;
            }
        }

        //makers toolkit gives a chance to make a potion in two or even one devices
        TechToolkit.crafting crafting = Dungeon.hero.buff(TechToolkit.crafting.class);
        int bonus = crafting != null ? crafting.itemLevel() : -1;

        if (bonus != -1 ? crafting.tryMake(count) : count >= DEVICES_TO_TECH) {

            CellEmitter.get(pos).burst(Speck.factory(Speck.WOOL), 6);
            Sample.INSTANCE.play(Assets.SND_PUFF);

            Item experimentaltech;

            if (Random.Int(count + bonus) == 0) {

                CellEmitter.center(pos).burst(Speck.factory(Speck.EVOKE), 3);

                destroy();

                Statistics.experimentalTechMade++;
                Badges.validateExperimentalTechMade();

                experimentaltech = Generator.random(Generator.Category.EXPERIMENTALTECH);

            } else {

                Mine.Device proto = (Device) items.get(Random.chances(chances));
                Class<? extends Item> itemClass = proto.craftingClass;

                destroy();

                Statistics.experimentalTechMade++;
                Badges.validateExperimentalTechMade();

                if (itemClass == null) {
                    experimentaltech = Generator.random(Generator.Category.EXPERIMENTALTECH);
                } else {
                    try {
                        experimentaltech = itemClass.newInstance();
                    } catch (Exception e) {
                        PixelSpacebase.reportException(e);
                        return null;
                    }
                }
            }

            //not a buff per-se, meant to cancel out higher experimentaltech accuracy when ppl are farming for ExperimentalTech of exp.
            if (bonus > 0)
                if (Random.Int(1000 / bonus) == 0)
                    return new ExperienceBooster();

            while (experimentaltech instanceof HealingTech && Random.Int(10) < Dungeon.limitedDrops.makingHP.count)
                experimentaltech = Generator.random(Generator.Category.EXPERIMENTALTECH);

            if (experimentaltech instanceof HealingTech)
                Dungeon.limitedDrops.makingHP.count++;

            return experimentaltech;

        } else {
            return null;
        }
    }

    public static void burnFX(int pos) {
        CellEmitter.get(pos).burst(ElmoParticle.FACTORY, 6);
        Sample.INSTANCE.play(Assets.SND_BURNING);
    }

    private static void evaporateFX(int pos) {
        CellEmitter.get(pos).burst(Speck.factory(Speck.STEAM), 5);
    }

    public boolean isEmpty() {
        return items == null || items.size() == 0;
    }

    public void destroy() {
        Dungeon.level.heaps.remove(this.pos);
        if (sprite != null) {
            sprite.kill();
        }
        items.clear();
        items = null;
    }

    @Override
    public String toString() {
        switch (type) {
            case CHEST:
            case CONFUSEDSHAPESHIFTER:
                return Messages.get(this, "chest");
            case LOCKED_CHEST:
                return Messages.get(this, "locked_chest");
            case CRYSTAL_CHEST:
                return Messages.get(this, "crystal_chest");
            case TOMB:
                return Messages.get(this, "tomb");
            case SKELETON:
                return Messages.get(this, "skeleton");
            case REMAINS:
                return Messages.get(this, "remains");
            default:
                return peek().toString();
        }
    }

    public String info() {
        switch (type) {
            case CHEST:
            case CONFUSEDSHAPESHIFTER:
                return Messages.get(this, "chest_desc");
            case LOCKED_CHEST:
                return Messages.get(this, "locked_chest_desc");
            case CRYSTAL_CHEST:
                if (peek() instanceof Artifact)
                    return Messages.get(this, "crystal_chest_desc", Messages.get(this, "artifact"));
                else if (peek() instanceof Blaster)
                    return Messages.get(this, "crystal_chest_desc", Messages.get(this, "blaster"));
                else
                    return Messages.get(this, "crystal_chest_desc", Messages.get(this, "module"));
            case TOMB:
                return Messages.get(this, "tomb_desc");
            case SKELETON:
                return Messages.get(this, "skeleton_desc");
            case REMAINS:
                return Messages.get(this, "remains_desc");
            default:
                return peek().info();
        }
    }

    private static final String POS = "pos";
    private static final String SEEN = "seen";
    private static final String TYPE = "type";
    private static final String ITEMS = "items";

    @SuppressWarnings("unchecked")
    @Override
    public void restoreFromBundle(Bundle bundle) {
        pos = bundle.getInt(POS);
        seen = bundle.getBoolean(SEEN);
        type = Type.valueOf(bundle.getString(TYPE));
        items = new LinkedList<>((Collection<Item>) ((Collection<?>) bundle.getCollection(ITEMS)));
        items.removeAll(Collections.singleton(null));
    }

    @Override
    public void storeInBundle(Bundle bundle) {
        bundle.put(POS, pos);
        bundle.put(SEEN, seen);
        bundle.put(TYPE, type.toString());
        bundle.put(ITEMS, items);
    }

}
