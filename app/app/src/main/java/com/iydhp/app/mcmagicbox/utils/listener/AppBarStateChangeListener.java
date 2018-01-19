package com.iydhp.app.mcmagicbox.utils.listener;

import android.support.design.widget.AppBarLayout;

public abstract class AppBarStateChangeListener implements AppBarLayout.OnOffsetChangedListener {

        public enum State {
            EXPANDED,
            COLLAPSED,
            IDLE
        }

        private State currentState = State.IDLE;
        private State lastState;

        @Override
        public final void onOffsetChanged(AppBarLayout appBarLayout, int i) {
            if (i == 0) {
                if (currentState != State.EXPANDED) {
                    onStateChanged(appBarLayout, State.EXPANDED);
                }
                currentState = State.EXPANDED;
            } else if (Math.abs(i) >= appBarLayout.getTotalScrollRange()) {
                if (currentState != State.COLLAPSED) {
                    onStateChanged(appBarLayout, State.COLLAPSED);
                }
                currentState = State.COLLAPSED;
            } else {
                if (currentState != State.IDLE) {
                    onStateChanged(appBarLayout, State.IDLE);
                }
                currentState = State.IDLE;
            }
        }

    public State getCurrentState() {
        return currentState;
    }

    public abstract void onStateChanged(AppBarLayout appBarLayout, State state);
}