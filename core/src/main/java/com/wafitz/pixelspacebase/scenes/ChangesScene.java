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

import com.wafitz.pixelspacebase.Chrome;
import com.wafitz.pixelspacebase.PixelSpacebase;
import com.wafitz.pixelspacebase.messages.Messages;
import com.wafitz.pixelspacebase.ui.ExitButton;
import com.wafitz.pixelspacebase.ui.RenderedTextMultiline;
import com.wafitz.pixelspacebase.ui.ScriptPane;
import com.wafitz.pixelspacebase.ui.Starfield;
import com.wafitz.pixelspacebase.ui.Window;
import com.watabou.noosa.Camera;
import com.watabou.noosa.NinePatch;
import com.watabou.noosa.RenderedText;
import com.watabou.noosa.ui.Component;

public class ChangesScene extends PixelScene {

    private static final String TXT_Update =
            "_v1.0.2:_\n" +
                    "_-_ Added the alien egg blackout and recovery message sequence.\n" +
                    "_-_ Made facehugger infection consume the attacker and made alien emergence more dangerous.\n" +
                    "_-_ Made exposed bridge sections require spacesuit protection, with DM-3000 and Shapeshifter exceptions.\n" +
                    "_-_ Strengthened the Dark Lord of Yendor quest and victory text.\n" +
                    "_-_ Reworked chain-themed text and pull visuals toward force-themed effects.\n" +
                    "_-_ Renamed stimulants to Stims and refreshed related pickup behavior.\n" +
                    "_-_ Tightened dialogue box presentation and replaced missing item/action text.\n" +
                    "_-_ Fixed lingering target indicators and Guardian Floor Lighting crashes.\n" +
                    "\n" +
            "_v1.0.1:_\n" +
                    "_-_ Added rescue cradle mechanics tied to the escape pod override.\n" +
                    "_-_ Expanded workshop storage handling and delivery between levels.\n" +
                    "_-_ Refreshed deck text, NPC dialogue, item descriptions, and enemy naming for the spacebase theme.\n" +
                    "_-_ Updated tiles, sprites, audio, and atmosphere videos across the station.\n" +
                    "_-_ Fixed repair blaster behavior and WaveBlaster trajectory handling.\n" +
                    "\n" +
            "_v1.0.0:_\n" +
                    "_-_ Rebased visible versioning for Pixel Spacebase.\n" +
                    "_-_ Replaced the title screen soundtrack with the new intro theme.\n" +
                    "_-_ Updated title, HUD, popup, and badge presentation toward a spacebase visual identity.\n" +
                    "_-_ Updated About screen credits for Pixel Spacebase, Shattered Pixel Dungeon v0.4.3, and Pixel Dungeon.\n" +
                    "\n" +
                    "_Historic conversion work:_\n" +
                    "_-_ Converted the package and game identity from Shattered Pixel Dungeon to Pixel Spacebase.\n" +
                    "_-_ Added Commander, DM3000, Shapeshifter, and Captain player classes.\n" +
                    "_-_ Reworked items toward parts, modules, tech, blasters, spacesuits, and gene mods.\n" +
                    "_-_ Added spacebase enemies, NPCs, terminals, vents, mines, and level text.\n" +
                    "_-_ Modernized the Android Gradle build so the project builds on current SDK tooling.\n" +
                    "\n";

    @Override
    public void create() {
        super.create();

        int w = Camera.main.width;
        int h = Camera.main.height;

        RenderedText title = PixelScene.renderText(Messages.get(this, "title"), 9);
        title.hardlight(Window.TITLE_COLOR);
        title.x = (w - title.width()) / 2;
        title.y = 4;
        align(title);
        add(title);

        ExitButton btnExit = new ExitButton();
        btnExit.setPos(Camera.main.width - btnExit.width(), 0);
        add(btnExit);

        RenderedTextMultiline text = renderMultiline(TXT_Update, 6);

        NinePatch panel = Chrome.get(Chrome.Type.TOAST);

        int pw = 135 + panel.marginLeft() + panel.marginRight() - 2;
        int ph = h - 16;

        panel.size(pw, ph);
        panel.x = (w - pw) / 2f;
        panel.y = title.y + title.height();
        align(panel);
        add(panel);

        ScriptPane list = new ScriptPane(new Component());
        add(list);

        Component content = list.content();
        content.clear();

        text.maxWidth((int) panel.innerWidth());

        content.add(text);

        content.setSize(panel.innerWidth(), (int) Math.ceil(text.height()));

        list.setRect(
                panel.x + panel.marginLeft(),
                panel.y + panel.marginTop() - 1,
                panel.innerWidth(),
                panel.innerHeight() + 2);
        list.scriptTo(0, 0);

        Starfield starfield = new Starfield();
        starfield.setSize(Camera.main.width, Camera.main.height);
        addToBack(starfield);

        fadeIn();
    }

    @Override
    protected void onBackPressed() {
        PixelSpacebase.switchNoFade(TitleScene.class);
    }
}
