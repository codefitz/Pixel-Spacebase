package com.wafitz.pixelspacebase.actors.buffs;

import com.wafitz.pixelspacebase.Statistics;
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
    public enum Phase { STRANDED, DEPARTING, VISITING, RETURNING, COMPLETE }
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

    public void recordFall(int depth) { recordFall(depth, -1); }

    public void recordFall(int depth, int pos) {
        if (phase != Phase.STRANDED) return;
        sourceDepth = depth;
        sourcePos = pos;
        rescueDepth = -1;
    }

    public boolean beginDeparture(int strandedDepth) {
        if (phase != Phase.STRANDED) return true;
        ArrayList<Integer> choices = YRescuer.rescueDestinations(strandedDepth);
        choices.remove(Integer.valueOf(sourceDepth));
        if (choices.isEmpty()) return false;
        destination = chooseDestination(Statistics.yRescueDepartures == 0);
        rescueDepth = Random.element(choices);
        journeyId = ++Statistics.yRescueDepartures;
        destinationIdentity = "";
        phase = Phase.DEPARTING;
        transferPrepared = false;
        return true;
    }

    private static Destination chooseDestination(boolean firstRescue) {
        if (firstRescue) return Destination.STATION;
        // Special outcomes stay disabled until their implementation stages are complete.
        return Destination.STATION;
    }

    public boolean pendingTravel() {
        return phase == Phase.DEPARTING || phase == Phase.RETURNING;
    }

    public void requestReturn() {
        phase = Phase.RETURNING;
        transferPrepared = false;
        arrivalLevel = null;
        landingPos = -1;
        travel();
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

    public void arrived(int depth) {
        if (depth == sourceDepth && (phase == Phase.RETURNING || phase == Phase.VISITING)) {
            phase = Phase.COMPLETE;
        } else if (depth == rescueDepth && phase == Phase.DEPARTING) {
            phase = Phase.VISITING;
            transferPrepared = false;
        }
        if (!pendingTravel()) arrivalLevel = null;
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
    }

    @Override public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        sourceDepth = bundle.contains("sourceDepth") ? bundle.getInt("sourceDepth") : -1;
        sourcePos = bundle.contains("sourcePos") ? bundle.getInt("sourcePos") : -1;
        rescueDepth = bundle.contains("rescueDepth") ? bundle.getInt("rescueDepth") : -1;
        journeyId = bundle.getInt("journeyId");
        destinationIdentity = bundle.contains("destinationIdentity") ? bundle.getString("destinationIdentity") : "";
        if (bundle.contains("journeyVersion")) {
            destination = parse(Destination.class, bundle.getString("destination"), Destination.STATION);
            phase = parse(Phase.class, bundle.getString("phase"), rescueDepth > 0 ? Phase.VISITING : Phase.STRANDED);
            outcome = parse(Outcome.class, bundle.getString("outcome"), Outcome.NONE);
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
