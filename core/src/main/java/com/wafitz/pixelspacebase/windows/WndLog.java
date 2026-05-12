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
package com.wafitz.pixelspacebase.windows;

import com.wafitz.pixelspacebase.messages.Messages;
import com.wafitz.pixelspacebase.scenes.PixelScene;
import com.wafitz.pixelspacebase.ui.GameLog;
import com.wafitz.pixelspacebase.ui.RedButton;
import com.wafitz.pixelspacebase.ui.RenderedTextMultiline;
import com.wafitz.pixelspacebase.ui.ScriptPane;
import com.wafitz.pixelspacebase.ui.Window;
import com.watabou.noosa.ui.Component;

public class WndLog extends Window {

    private static final int WIDTH = 112;
    private static final int HEIGHT = 160;
    private static final int GAP = 2;

    public WndLog() {
        super();
        resize(WIDTH, HEIGHT);

        RedButton title = new RedButton(Messages.get(this, "title"), 9);
        title.textColor(Window.TITLE_COLOR);
        title.setRect(0, 0, WIDTH, title.reqHeight());
        PixelScene.align(title);
        add(title);

        Component content = new Component();
        float pos = 0;

        for (GameLog.Entry entry : GameLog.history()) {
            RenderedTextMultiline line = PixelScene.renderMultiline(entry.text, 6);
            line.hardlight(entry.color);
            line.maxWidth(WIDTH);
            line.setPos(0, pos);
            content.add(line);
            pos += line.height() + GAP;
        }

        if (pos == 0) {
            RenderedTextMultiline empty = PixelScene.renderMultiline(Messages.get(this, "empty"), 6);
            empty.maxWidth(WIDTH);
            empty.setPos(0, 0);
            content.add(empty);
            pos = empty.height();
        }

        content.setSize(WIDTH, pos);

        ScriptPane list = new ScriptPane(content);
        add(list);

        list.setRect(0, title.height() + GAP, WIDTH, height - title.height() - GAP);
        list.scriptTo(0, Math.max(0, content.height() - list.height()));
    }
}
