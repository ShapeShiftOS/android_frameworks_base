/*
 * Copyright (C) 2020 Yet Another AOSP Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.android.systemui.qs.tiles;

import android.content.ContentResolver;
import android.content.ComponentName;
import android.content.Intent;
import android.provider.Settings;
import android.provider.Settings.Secure;
import android.service.quicksettings.Tile;

import com.android.systemui.qs.QSHost;
import com.android.systemui.qs.tileimpl.QSTileImpl;
import com.android.systemui.plugins.qs.QSTile.BooleanState;
import com.android.systemui.R;

import com.android.internal.logging.nano.MetricsProto.MetricsEvent;

import javax.inject.Inject;


/** Quick settings tile: Reading mode tile **/
public class ReadingModeTile extends QSTileImpl<BooleanState> {

    private final Icon mIcon = ResourceIcon.get(R.drawable.ic_qs_reading_mode);

    // Settings keys
    private static final String DALTONIZER_KEY = Secure.ACCESSIBILITY_DISPLAY_DALTONIZER_ENABLED;
    private static final String DALTONIZER_MODE_KEY = Secure.ACCESSIBILITY_DISPLAY_DALTONIZER;

    private static final int DALTONIZER_MODE_OFF = -1;
    private static final int DALTONIZER_MODE_MONO = 0;

    @Inject
    public ReadingModeTile(QSHost host) {
        super(host);
    }

    @Override
    public Intent getLongClickIntent() {
        return null;
    }

    @Override
    protected void handleDestroy() {
        super.handleDestroy();
    }

    @Override
    public BooleanState newTileState() {
        BooleanState state = new BooleanState();
        state.handlesLongClick = false;
        return state;
    }

    @Override
    protected void handleUserSwitch(int newUserId) { }

    @Override
    public boolean isAvailable() {
        return true;
    }

    @Override
    protected void handleClick() {
        boolean newState = !isMonoEnabled();
        final ContentResolver cr = mContext.getContentResolver();
        Secure.putInt(cr, DALTONIZER_KEY, newState ? 1 : 0);
        Secure.putInt(cr, DALTONIZER_MODE_KEY,
                newState ? DALTONIZER_MODE_MONO : DALTONIZER_MODE_OFF);
        refreshState(newState);
    }

    @Override
    public CharSequence getTileLabel() {
        return mContext.getString(R.string.quick_settings_reading_mode);
    }

    @Override
    protected void handleLongClick() {
        handleClick();
    }

    @Override
    protected void handleUpdateState(BooleanState state, Object arg) {
        if (state.slash == null) {
            state.slash = new SlashState();
        }
        state.label = mHost.getContext().getString(R.string.quick_settings_reading_mode);
        state.secondaryLabel = "";
        state.stateDescription = "";
        if (arg instanceof Boolean) {
            boolean value = (Boolean) arg;
            if (value == state.value) {
                return;
            }
            state.value = value;
        } else {
            state.value = isMonoEnabled();
        }
        state.icon = mIcon;
        state.slash.isSlashed = !state.value;
        state.contentDescription = mContext.getString(R.string.quick_settings_reading_mode);
        state.state = state.value ? Tile.STATE_ACTIVE : Tile.STATE_INACTIVE;
    }

    @Override
    public int getMetricsCategory() {
        return MetricsEvent.CUSTOM_QS;
    }

    private boolean isMonoEnabled() {
        final ContentResolver cr = mContext.getContentResolver();
        final boolean enabled = Secure.getInt(cr, DALTONIZER_KEY, 0) != 0;
        if (enabled) {
            return Secure.getInt(
                    cr, DALTONIZER_MODE_KEY, 0) == DALTONIZER_MODE_MONO;
        }
        return false;
    }

}
