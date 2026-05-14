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
package com.wafitz.pixelspacebase.scenes;

import android.content.Intent;
import android.net.Uri;

import com.wafitz.pixelspacebase.PixelSpacebase;
import com.wafitz.pixelspacebase.ui.ExitButton;
import com.wafitz.pixelspacebase.ui.Icons;
import com.wafitz.pixelspacebase.ui.RenderedTextMultiline;
import com.wafitz.pixelspacebase.ui.Starfield;
import com.wafitz.pixelspacebase.ui.Window;
import com.watabou.input.Touchscreen.Touch;
import com.watabou.noosa.Camera;
import com.watabou.noosa.Game;
import com.watabou.noosa.Image;
import com.watabou.noosa.RenderedText;
import com.watabou.noosa.TouchArea;

// Game.switchScene uses reflection, so this class must be public
public class AboutScene extends PixelScene {

    private static final String TTL_PS = "Pixel Spacebase";

    private static final String TXT_PS =
            "Spacebase conversion, design, code, and graphics: Wafitz\n" +
                    "Implementation assistance: Codex";

    private static final String LNK_PS = "github.com/codefitz/Pixel-Spacebase";

    private static final String TTL_SHPX = "Built from Shattered Pixel Dungeon v0.4.3";

    private static final String TXT_SHPX =
            "Design, Code, & Graphics: Evan";

    private static final String LNK_SHPX = "ShatteredPixel.com";

    private static final String TTL_WATA = "Pixel Dungeon";

    private static final String TXT_WATA = "Code & Graphics: Watabou";

    private static final String TXT_MUSI = "Music: Cube_Code";

    private static final String LNK_WATA = "pixeldungeon.watabou.ru";

    @Override
    public void create() {
        super.create();

        final float width = Camera.main.width;
        final float top = PixelSpacebase.landscape() ? 16 : 24;
        final int textWidth = (int) Math.min(width - 20, PixelSpacebase.landscape() ? 210 : 132);

        RenderedText title = renderText(TTL_PS, 12);
        title.hardlight(Window.TITLE_COLOR);
        title.x = (width - title.width()) / 2;
        title.y = top;
        align(title);
        add(title);

        RenderedText version = renderText("version " + Game.version, 7);
        version.hardlight(0x66E6FF);
        version.x = (width - version.width()) / 2;
        version.y = title.y + title.height() + 2;
        align(version);
        add(version);

        Image commander = Icons.COMMANDER.get();
        Image dm3000 = Icons.DM3000.get();
        Image shapeshifter = Icons.SHAPESHIFTER.get();
        Image captain = Icons.CAPTAIN.get();
        Image[] crew = {commander, dm3000, shapeshifter, captain};
        float crewWidth = 16 * crew.length + 4 * (crew.length - 1);
        float crewX = (width - crewWidth) / 2;
        float crewY = version.y + version.height() + 10;
        for (int i = 0; i < crew.length; i++) {
            crew[i].x = crewX + i * 20;
            crew[i].y = crewY;
            align(crew[i]);
            add(crew[i]);
        }

        float y = crewY + 25;
        RenderedTextMultiline psText = renderMultiline(TXT_PS, 7);
        psText.maxWidth(textWidth);
        psText.hardlight(0xEAFDFF);
        psText.setPos((width - psText.width()) / 2, y);
        align(psText);
        add(psText);

        y = psText.bottom() + 8;
        RenderedTextMultiline psLink = addLink(LNK_PS, textWidth, y, 0x66E6FF);
        addLinkHotArea(psLink, "https://" + LNK_PS);

        y = psLink.bottom() + 12;
        RenderedTextMultiline shpxTitle = renderMultiline(TTL_SHPX, 7);
        shpxTitle.maxWidth(textWidth);
        shpxTitle.hardlight(Window.SHPX_COLOR);
        shpxTitle.setPos((width - shpxTitle.width()) / 2, y);
        align(shpxTitle);
        add(shpxTitle);

        y = shpxTitle.bottom() + 4;
        RenderedTextMultiline shpxText = renderMultiline(TXT_SHPX, 7);
        shpxText.maxWidth(textWidth);
        shpxText.setPos((width - shpxText.width()) / 2, y);
        align(shpxText);
        add(shpxText);

        y = shpxText.bottom() + 4;
        RenderedTextMultiline shpxLink = addLink(LNK_SHPX, textWidth, y, Window.SHPX_COLOR);
        addLinkHotArea(shpxLink, "http://" + LNK_SHPX);

        y = shpxLink.bottom() + 10;
        RenderedTextMultiline wataTitle = renderMultiline(TTL_WATA, 7);
        wataTitle.maxWidth(textWidth);
        wataTitle.hardlight(Window.TITLE_COLOR);
        wataTitle.setPos((width - wataTitle.width()) / 2, y);
        align(wataTitle);
        add(wataTitle);

        y = wataTitle.bottom() + 4;
        RenderedTextMultiline wataText = renderMultiline(TXT_WATA + "\\n" + TXT_MUSI, 7);
        wataText.maxWidth(textWidth);
        wataText.setPos((width - wataText.width()) / 2, y);
        align(wataText);
        add(wataText);

        y = wataText.bottom() + 4;
        RenderedTextMultiline wataLink = addLink(LNK_WATA, textWidth, y, Window.TITLE_COLOR);
        addLinkHotArea(wataLink, "http://" + LNK_WATA);


        Starfield starfield = new Starfield();
        starfield.setSize(Camera.main.width, Camera.main.height);
        addToBack(starfield);

        ExitButton btnExit = new ExitButton();
        btnExit.setPos(Camera.main.width - btnExit.width(), 0);
        add(btnExit);

        fadeIn();
    }

    private RenderedTextMultiline addLink(String text, int width, float y, int color) {
        RenderedTextMultiline link = renderMultiline(text, 7);
        link.maxWidth(width);
        link.hardlight(color);
        link.setPos((Camera.main.width - link.width()) / 2, y);
        align(link);
        add(link);
        return link;
    }

    private void addLinkHotArea(final RenderedTextMultiline link, final String url) {
        TouchArea hotArea = new TouchArea(link.left(), link.top(), link.width(), link.height()) {
            @Override
            protected void onClick(Touch touch) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                Game.instance.startActivity(intent);
            }
        };
        add(hotArea);
    }

    @Override
    protected void onBackPressed() {
        PixelSpacebase.switchNoFade(TitleScene.class);
    }
}
