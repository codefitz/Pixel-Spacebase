# Hero Dev/Test Mode

These changes were used during pre-release testing to move quickly through encounters and survive crossfire-heavy feature checks. They are disabled for release, but this note records the exact behavior so it can be reintroduced deliberately in the next sprint.

## Release Values

File: `core/src/main/java/com/wafitz/pixelspacebase/actors/hero/Hero.java`

```java
private static final int STARTING_STR = 10;
private static final boolean DEV_TEST_INVULNERABLE = false;
```

## Dev/Test Values

```java
private static final int STARTING_STR = 100;
private static final boolean DEV_TEST_INVULNERABLE = true;
```

## Behavior When Enabled

- New heroes start with 100 strength instead of the normal 10.
- `Hero.defenseProc(...)` returns zero incoming damage after restoring health.
- `Hero.damage(...)` restores health and exits before applying incoming damage.
- `Hero.die(...)` restores health, shows the flare effect, and prevents death handling.
- `Hero.isAlive()` always reports alive and restores health if HP dropped to zero or below.
- `SpacebaseRun.fail(...)` restores health and skips ranking/death flow while the flag is enabled.

## Starter Chest

The release build does not place the old depth-1 entrance starter chest. It was previously injected in `core/src/main/java/com/wafitz/pixelspacebase/levels/OperationsLevel.java`, inside `decorate()`, after `placeSign()`.

The removed dev/test chest logic:

```java
if (SpacebaseRun.depth <= 1) {
    int pos = pointToCell(roomEntrance.random());
    if (pos != entrance && vents.get(pos) == null
            && findMob(pos) == null && pos != Terrain.SIGN) {
        drop(Generator.random(), pos).type = Heap.Type.CHEST;
        drop(new Uniform().identify(), pos);
        drop(new Food().identify(), pos);
        drop(new MappingUpgrade().identify(), pos);
        drop(new WeakForcefield().identify(), pos);
        drop(new SpaceSuit().identify(), pos);
        drop(new Wrench().identify(), pos);
        drop(new Wrench().identify(), pos);
    }
}
```

Required imports if this exact block is restored:

```java
import com.wafitz.pixelspacebase.items.Generator;
import com.wafitz.pixelspacebase.items.Heap;
import com.wafitz.pixelspacebase.items.WeakForcefield;
import com.wafitz.pixelspacebase.items.armor.SpaceSuit;
import com.wafitz.pixelspacebase.items.armor.Uniform;
import com.wafitz.pixelspacebase.items.food.Food;
import com.wafitz.pixelspacebase.items.upgrades.MappingUpgrade;
import com.wafitz.pixelspacebase.items.weapon.melee.Wrench;
```

## Reimplementation Notes

- Prefer keeping this behind one explicit build/debug flag before the next sprint instead of manually changing release constants.
- If enabled for field testing, add visible UI/build labeling so screenshots and APKs cannot be confused with release builds.
- Recheck cat escort and crossfire behavior with the mode disabled before shipping, because invulnerability can hide companion-risk issues.
- If the starter chest returns, gate it behind the same explicit dev/test mode instead of making it unconditional on depth 1.
