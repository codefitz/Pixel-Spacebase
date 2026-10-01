package com.wafitz.pixelspacebase.ui;

import com.wafitz.pixelspacebase.Assets;
import com.wafitz.pixelspacebase.scenes.PixelScene;
import com.watabou.noosa.Camera;
import com.watabou.noosa.Image;
import com.watabou.noosa.ui.Component;

/**
 * The view beyond the station hull. It follows the level camera more slowly
 * than the terrain, giving the stars and nearby planet a restrained parallax.
 */
public class SpacebaseBackdrop extends Component {

    private static final float MARGIN = 32f;
    private static final float STAR_PARALLAX = 0.04f;
    private static final float PLANET_PARALLAX = 0.12f;
    private static final float PLANET_VIEW_SCALE = 1.65f;

    private Image starfield;
    private Image planet;
    private float cameraOriginX = Float.NaN;
    private float cameraOriginY = Float.NaN;

    @Override
    protected void createChildren() {
        //Keep the backdrop in screen space. Only the offsets below respond to
        //the world camera, so it cannot change the level's zoom or framing.
        camera = PixelScene.uiCamera;

        starfield = new Image(Assets.SPACEBASE_STARFIELD);
        starfield.alpha(0.86f);
        add(starfield);

        planet = new Image(Assets.SPACEBASE_PLANET);
        planet.alpha(0.72f);
        add(planet);
    }

    @Override
    public void update() {
        super.update();

        Camera worldCamera = Camera.main;
        Camera viewCamera = PixelScene.uiCamera;

        if (Float.isNaN(cameraOriginX)) {
            cameraOriginX = worldCamera.script.x;
            cameraOriginY = worldCamera.script.y;
        }

        float cameraDeltaX = worldCamera.script.x - cameraOriginX;
        float cameraDeltaY = worldCamera.script.y - cameraOriginY;

        float starScale = Math.max(
                (viewCamera.width + MARGIN * 2) / starfield.width,
                (viewCamera.height + MARGIN * 2) / starfield.height);
        starfield.scale.set(starScale, starScale);
        starfield.x = -MARGIN - cameraDeltaX * STAR_PARALLAX;
        starfield.y = -MARGIN - cameraDeltaY * STAR_PARALLAX;

        float planetScale = Math.max(viewCamera.width, viewCamera.height)
                * PLANET_VIEW_SCALE / planet.width;
        planet.scale.set(planetScale, planetScale);
        planet.x = viewCamera.width * 0.66f - planet.width() / 2f
                - cameraDeltaX * PLANET_PARALLAX;
        planet.y = viewCamera.height * 0.72f - planet.height() / 2f
                - cameraDeltaY * PLANET_PARALLAX;
    }
}
