package com.tomaszkyc.app.ui.model.contextmenu;

import javafx.scene.control.MenuItem;

public interface CustomMenuItemBuilder {

    MenuItem build();

    void prepareActionEventHandler();

}
