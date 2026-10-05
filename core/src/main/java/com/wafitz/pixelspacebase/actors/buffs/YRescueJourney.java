package com.wafitz.pixelspacebase.actors.buffs;

import com.wafitz.pixelspacebase.Statistics;
import com.wafitz.pixelspacebase.SpacebaseRun;
import com.wafitz.pixelspacebase.actors.mobs.npcs.YRescuer;
import com.wafitz.pixelspacebase.scenes.InterlevelScene;
import com.wafitz.pixelspacebase.levels.Level;
import com.watabou.noosa.Game;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;
import java.util.ArrayList;

/** Saved journey and transition intent; an active journey keeps its original fall origin. */
public class YRescueJourney extends Buff {
    public enum Destination { STATION, ALIEN_PLANET, DARK_MAZE, PIXEL_DUNGEON, BOSS }
    public enum Phase { STRANDED, DEPARTING, VISITING, RETURNING, RESOLVING, COMPLETE }
    public enum Outcome { NONE, VICTORY, DEFEAT }

    public int sourceDepth = -1;
    public int sourcePos = -1;
    public int rescueDepth = -1;
    public int journeyId;
    public Destination destination = Destination.STATION;
    public Phase phase = Phase.STRANDED;
    public Outcome outcome = Outcome.NONE;
    public String destinationIdentity = "";
    public boolean transferPrepared;
    public Level arrivalLevel;
    public int landingPos = -1;
    public Bundle bossBaseline;
    public boolean bossCommitted;
    private boolean extractionShown;
    public transient volatile boolean resolutionReady;
    private java.util.HashMap<Integer, Integer> acquiredArenaLoot = new java.util.HashMap<>();

    public void recordFall(int depth) { recordFall(depth, -1); }

    public void recordFall(int depth, int pos) {
        if (phase != Phase.STRANDED) return;
        sourceDepth = depth;
        sourcePos = pos;
        rescueDepth = -1;
    }

    public boolean beginDeparture(int strandedDepth) { return beginDeparture(strandedDepth, null); }

    /** Development callers can select an enabled destination after the first rescue. */
    public boolean beginDeparture(int strandedDepth, Destination preferred) {
        if (phase != Phase.STRANDED) return true;
        ArrayList<Integer> choices = YRescuer.rescueDestinations(strandedDepth);
        choices.remove(Integer.valueOf(sourceDepth));
        if (choices.isEmpty()) return false;
        boolean first = Statistics.yRescueDepartures == 0;
        destination = !first && (preferred == Destination.STATION || preferred == Destination.ALIEN_PLANET || preferred == Destination.BOSS)
                ? preferred : chooseDestination(first);
        int boss = destination == Destination.BOSS ? SpacebaseRun.nextRescueBoss(sourceDepth) : -1;
        if (destination == Destination.BOSS && boss < 0) destination = Destination.STATION;
        rescueDepth = destination == Destination.BOSS ? boss
                : destination == Destination.ALIEN_PLANET ? sourceDepth : Random.element(choices);
        journeyId = ++Statistics.yRescueDepartures;
        destinationIdentity = destination == Destination.ALIEN_PLANET ? "alien_" + journeyId
                : destination == Destination.BOSS ? "boss_" + journeyId : "";
        phase = Phase.DEPARTING;
        transferPrepared = false;
        return true;
    }

    private static Destination chooseDestination(boolean firstRescue) {
        if (firstRescue) return Destination.STATION;
        int roll = Random.Int(100);
        return roll < 15 ? Destination.ALIEN_PLANET : roll < 20 ? Destination.BOSS : Destination.STATION;
    }

    public boolean pendingTravel() {
        return phase == Phase.DEPARTING || phase == Phase.RETURNING;
    }

    public void requestReturn() {
        prepareReturn();
        travel();
    }

    public void prepareReturn() {
        if (destination == Destination.BOSS && outcome == Outcome.NONE) outcome = Outcome.DEFEAT;
        Buff.detach(SpacebaseRun.hero, LockedFloor.class);
        phase = Phase.RETURNING;
        transferPrepared = false;
        arrivalLevel = null;
        landingPos = -1;
    }

    public void configureTravel() {
        InterlevelScene.returnDepth = phase == Phase.RETURNING ? sourceDepth : rescueDepth;
        InterlevelScene.returnPos = -1;
        InterlevelScene.returnAtEntrance = phase == Phase.RETURNING;
        InterlevelScene.rescueScatter = phase == Phase.DEPARTING;
    }

    public void travel() {
        configureTravel();
        InterlevelScene.mode = InterlevelScene.Mode.RETURN;
        Game.switchScene(InterlevelScene.class);
    }

    public boolean atDestination(int depth) {
        return destinationIdentity.isEmpty() ? !SpacebaseRun.isRescueSideLevel() && depth == rescueDepth
                : SpacebaseRun.matchesRescueLevel(destinationIdentity);
    }

    public void arrived(int depth) {
        if (!SpacebaseRun.isRescueSideLevel() && depth == sourceDepth && (phase == Phase.RETURNING || phase == Phase.VISITING)) {
            phase = Phase.COMPLETE;
        } else if (atDestination(depth) && phase == Phase.DEPARTING) {
            phase = Phase.VISITING;
            transferPrepared = false;
        }
        if (!pendingTravel()) arrivalLevel = null;
    }

    public boolean activeBossFight() {
        return destination == Destination.BOSS && phase == Phase.VISITING && atDestination(SpacebaseRun.depth);
    }

    public void captureBossBaseline(Level arena) {
        if (bossBaseline != null) return;
        int id = 1;
        for (com.wafitz.pixelspacebase.items.Heap heap : arena.heaps.values()) {
            for (com.wafitz.pixelspacebase.items.Item item : heap.items) {
                item.rescueLootJourney = journeyId;
                item.rescueLootId = id++;
            }
        }
        bossBaseline = new Bundle();
        bossBaseline.put("level", arena);
    }

    public void recordArenaLoot(com.wafitz.pixelspacebase.items.Item item) {
        if (activeBossFight() && item.rescueLootJourney == journeyId && item.rescueLootId > 0) {
            int id = item.rescueLootId;
            acquiredArenaLoot.put(id, acquiredArenaLoot.getOrDefault(id, 0) + item.quantity());
            item.rescueLootId = 0; // Dropping and collecting it again must not consume baseline loot twice.
        }
    }

    public void commitBossArena() throws java.io.IOException {
        if (destination != Destination.BOSS || bossCommitted) return;
        if (bossBaseline == null) throw new java.io.IOException("Missing boss rescue baseline");
        Level arena = SpacebaseRun.level;
        if (outcome != Outcome.VICTORY) {
            arena = (Level) bossBaseline.get("level");
            for (com.wafitz.pixelspacebase.items.Heap heap : arena.heaps.values().toArray(new com.wafitz.pixelspacebase.items.Heap[0])) {
                for (com.wafitz.pixelspacebase.items.Item item : heap.items.toArray(new com.wafitz.pixelspacebase.items.Item[0])) {
                    int collected = acquiredArenaLoot.getOrDefault(item.rescueLootId, 0);
                    if (collected >= item.quantity()) heap.items.remove(item);
                    else if (collected > 0) item.quantity(item.quantity() - collected);
                }
                if (heap.items.isEmpty()) arena.heaps.remove(heap.pos);
            }
        }
        SpacebaseRun.saveRescueBossArena(rescueDepth, arena);
        bossCommitted = true;
    }

    public static void bossVictory() {
        YRescueJourney journey = SpacebaseRun.hero.buff(YRescueJourney.class);
        if (journey != null && journey.activeBossFight()) journey.resolveBoss(Outcome.VICTORY);
    }

    public boolean rescueLethalDefeat() {
        if (!activeBossFight()) return false;
        for (Buff buff : SpacebaseRun.hero.buffs().toArray(new Buff[0])) {
            if (buff.type == buffType.NEGATIVE) buff.detach();
        }
        SpacebaseRun.hero.HP = Math.max(1, SpacebaseRun.hero.HT / 4);
        SpacebaseRun.hero.paralysed = 0;
        Buff.detach(SpacebaseRun.hero, LockedFloor.class);
        resolveBoss(Outcome.DEFEAT);
        return true;
    }

    private void resolveBoss(Outcome result) {
        outcome = result;
        phase = Phase.RESOLVING;
        SpacebaseRun.hero.interrupt();
        Buff.detach(SpacebaseRun.hero, LockedFloor.class);
        resolutionReady = false;
    }

    public void showBossExtraction() {
        if (phase != Phase.RESOLVING || extractionShown || com.wafitz.pixelspacebase.actors.Actor.isActing()) return;
        if (!resolutionReady) {
            try { SpacebaseRun.saveAll(); }
            catch (java.io.IOException error) {
                com.wafitz.pixelspacebase.PixelSpacebase.reportException(error);
                return;
            }
            resolutionReady = true;
        }
        extractionShown = true;
        final com.wafitz.pixelspacebase.actors.mobs.npcs.YRescuer y = new com.wafitz.pixelspacebase.actors.mobs.npcs.YRescuer();
        y.pos = SpacebaseRun.hero.pos;
        for (int cell : new int[]{y.pos - 1, y.pos + 1, y.pos - SpacebaseRun.level.width(), y.pos + SpacebaseRun.level.width()}) {
            if (SpacebaseRun.level.insideMap(cell) && Level.passable[cell]
                    && com.wafitz.pixelspacebase.actors.Actor.findChar(cell) == null) { y.pos = cell; break; }
        }
        com.wafitz.pixelspacebase.scenes.GameScene.add(y);
        com.wafitz.pixelspacebase.scenes.GameScene.show(new com.wafitz.pixelspacebase.windows.WndQuest(y,
                com.wafitz.pixelspacebase.messages.Messages.get(com.wafitz.pixelspacebase.actors.mobs.npcs.Y.class,
                        outcome == Outcome.VICTORY ? "boss_victory" : "boss_defeat")) {
            @Override public void hide() {
                super.hide();
                y.destroy();
                if (y.sprite != null) y.sprite.killAndErase();
                requestReturn();
            }
        });
    }

    @Override public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put("journeyVersion", 1);
        bundle.put("sourceDepth", sourceDepth);
        bundle.put("sourcePos", sourcePos);
        bundle.put("rescueDepth", rescueDepth);
        bundle.put("journeyId", journeyId);
        bundle.put("destination", destination.name());
        bundle.put("destinationIdentity", destinationIdentity);
        bundle.put("phase", phase.name());
        bundle.put("outcome", outcome.name());
        bundle.put("transferPrepared", transferPrepared);
        if (arrivalLevel != null) bundle.put("arrivalLevel", arrivalLevel);
        bundle.put("landingPos", landingPos);
        if (bossBaseline != null) bundle.put("bossBaseline", bossBaseline);
        bundle.put("bossCommitted", bossCommitted);
        int[] ids = new int[acquiredArenaLoot.size()], amounts = new int[ids.length];
        int i = 0;
        for (java.util.Map.Entry<Integer, Integer> entry : acquiredArenaLoot.entrySet()) {
            ids[i] = entry.getKey(); amounts[i++] = entry.getValue();
        }
        bundle.put("arenaLootIds", ids); bundle.put("arenaLootAmounts", amounts);
    }

    @Override public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        sourceDepth = bundle.contains("sourceDepth") ? bundle.getInt("sourceDepth") : -1;
        sourcePos = bundle.contains("sourcePos") ? bundle.getInt("sourcePos") : -1;
        rescueDepth = bundle.contains("rescueDepth") ? bundle.getInt("rescueDepth") : -1;
        journeyId = bundle.getInt("journeyId");
        bossBaseline = bundle.contains("bossBaseline") ? bundle.getBundle("bossBaseline") : null;
        bossCommitted = bundle.getBoolean("bossCommitted");
        if (bundle.contains("arenaLootIds")) {
            int[] ids = bundle.getIntArray("arenaLootIds"), amounts = bundle.getIntArray("arenaLootAmounts");
            for (int i = 0; i < Math.min(ids.length, amounts.length); i++) acquiredArenaLoot.put(ids[i], amounts[i]);
        }
        destinationIdentity = bundle.contains("destinationIdentity") ? bundle.getString("destinationIdentity") : "";
        if (bundle.contains("journeyVersion")) {
            destination = parse(Destination.class, bundle.getString("destination"), Destination.STATION);
            phase = parse(Phase.class, bundle.getString("phase"), rescueDepth > 0 ? Phase.VISITING : Phase.STRANDED);
            outcome = parse(Outcome.class, bundle.getString("outcome"), Outcome.NONE);
            resolutionReady = false;
            transferPrepared = bundle.getBoolean("transferPrepared");
            arrivalLevel = bundle.contains("arrivalLevel") ? (Level) bundle.get("arrivalLevel") : null;
            landingPos = bundle.contains("landingPos") ? bundle.getInt("landingPos") : -1;
        } else {
            phase = rescueDepth > 0 ? Phase.VISITING : Phase.STRANDED;
        }
    }

    private static <T extends Enum<T>> T parse(Class<T> type, String value, T fallback) {
        try { return Enum.valueOf(type, value); }
        catch (IllegalArgumentException | NullPointerException ignored) { return fallback; }
    }
}
