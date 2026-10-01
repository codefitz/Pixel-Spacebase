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
package com.wafitz.pixelspacebase.ui;

import com.wafitz.pixelspacebase.scenes.PixelScene;

public class CheckBox extends RedButton {

    private static final float LABEL_MARGIN = 4;
    private static final float ICON_MARGIN = 3;
    private static final float LABEL_GAP = 3;

    private boolean checked = false;

    public CheckBox(String label) {
        super(label);

        icon(Icons.get(Icons.UNCHECKED));
    }

    @Override
    protected void layout() {
        super.layout();

        fitText(width - LABEL_MARGIN - ICON_MARGIN - icon.width() - LABEL_GAP);
        text.x = x + LABEL_MARGIN;
        text.y = y + (height - text.baseLine()) / 2;
        PixelScene.align(text);

        icon.x = x + width - ICON_MARGIN - icon.width();
        icon.y = y + (height - icon.height()) / 2;
        PixelScene.align(icon);
    }

    public boolean checked() {
        return checked;
    }

    public void checked(boolean value) {
        if (checked != value) {
            checked = value;
            icon.copy(Icons.get(checked ? Icons.CHECKED : Icons.UNCHECKED));
            layout();
        }
    }

    @Override
    protected void onClick() {
        super.onClick();
        checked(!checked);
    }
}
