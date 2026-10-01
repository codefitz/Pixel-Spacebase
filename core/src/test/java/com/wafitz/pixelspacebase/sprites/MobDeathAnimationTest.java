package com.wafitz.pixelspacebase.sprites;

import com.wafitz.pixelspacebase.actors.mobs.Mob;
import com.watabou.noosa.MovieClip;
import org.junit.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

import static org.junit.Assert.*;

public class MobDeathAnimationTest {
    @Test
    public void sleepingMobKeepsDeathAnimationAfterUpdate() throws Exception {
        Mob mob = deadSleepingMob();
        TestSprite sprite = new TestSprite();
        sprite.ch = mob;
        sprite.die();
        sprite.update();
        assertFalse(sprite.sleeping);
        assertSame(sprite.die, sprite.animation());
    }

    @Test
    public void deathClearsPauseAndOldAnimationCallback() {
        TestSprite sprite = new TestSprite();
        sprite.paused = true;
        sprite.animCallback = () -> fail("Old attack callback must not run after death");
        sprite.die();
        assertFalse(sprite.paused);
        assertNull(sprite.animCallback);
    }

    @Test
    public void lateIdleRequestCannotRestartDeadMobAnimation() throws Exception {
        Mob mob = deadSleepingMob();
        TestSprite sprite = new TestSprite();
        sprite.ch = mob;
        sprite.die();
        sprite.idle();
        assertSame(sprite.die, sprite.animation());
    }

    private static Mob deadSleepingMob() throws Exception {
        // Avoid the localized-name constructor, which needs Android preferences.
        Class<?> allocator = Class.forName("sun.misc.Unsafe");
        Field field = allocator.getDeclaredField("theUnsafe");
        field.setAccessible(true);
        Mob mob = (Mob) allocator.getMethod("allocateInstance", Class.class)
                .invoke(field.get(null), TestMob.class);
        Constructor<?> sleeping = Class.forName(Mob.class.getName() + "$Sleeping")
                .getDeclaredConstructor(Mob.class);
        sleeping.setAccessible(true);
        mob.SLEEPING = (Mob.AiState) sleeping.newInstance(mob);
        mob.state = mob.SLEEPING;
        mob.HP = 0;
        return mob;
    }

    private static class TestMob extends Mob { }

    private static class TestSprite extends MobSprite {
        TestSprite() {
            // Empty animations avoid loading texture assets in local tests.
            idle = new Animation(0, true);
            die = new Animation(0, false);
        }

        @Override public void play(Animation animation, boolean force) {
            curAnim = animation;
        }

        MovieClip.Animation animation() { return curAnim; }
    }
}
