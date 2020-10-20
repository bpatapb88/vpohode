package com.simon.vpohode;

import android.view.Menu;
import android.view.MenuItem;

public class LayoutManager {

    public static void invisible(int item, Menu menu){
        MenuItem menuItem = menu.findItem(item);
        menuItem.setVisible(false);
    }
}
