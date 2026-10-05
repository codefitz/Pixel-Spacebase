package com.wafitz.pixelspacebase.actors.mobs.npcs;

import com.wafitz.pixelspacebase.SpacebaseRun;
import com.wafitz.pixelspacebase.actors.Char;
import com.wafitz.pixelspacebase.actors.buffs.Buff;
import com.wafitz.pixelspacebase.messages.Messages;
import com.wafitz.pixelspacebase.scenes.GameScene;
import com.wafitz.pixelspacebase.sprites.AlienSprite;
import com.wafitz.pixelspacebase.windows.WndQuest;

public class AlienResident extends NPC {
    { spriteClass = AlienSprite.class; properties.add(Property.IMMOVABLE); }
    @Override protected boolean act() { throwItem(); spend(TICK); return true; }
    @Override public int defenseSkill(Char enemy) { return 1000; }
    @Override public void damage(int damage, Object source) { }
    @Override public void add(Buff buff) { }
    @Override public boolean reset() { return true; }
    @Override public boolean interact() {
        sprite.turnTo(pos, SpacebaseRun.hero.pos);
        GameScene.show(new WndQuest(this, Messages.get(this, (pos / SpacebaseRun.level.width()) % 2 == 0 ? "greeting" : "greeting_2")));
        return false;
    }
}
