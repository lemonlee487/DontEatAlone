package cyruslee487.donteatalone;

import android.support.design.widget.NavigationView;
import android.view.Menu;
import android.view.MenuItem;

public class UtilFunction {

    public static void changeNavMenuItemName(NavigationView navigationView){
        //Change Navigation Menu item name
        Menu menu = navigationView.getMenu();
        MenuItem nav_camara = menu.findItem(R.id.nav_camera);
        MenuItem nav_gallery = menu.findItem(R.id.nav_gallery);
        MenuItem nav_slideshow = menu.findItem(R.id.nav_slideshow);
        MenuItem nav_manage = menu.findItem(R.id.nav_manage);
        MenuItem nav_share = menu.findItem(R.id.nav_share);
        MenuItem nav_send = menu.findItem(R.id.nav_send);

        nav_camara.setTitle("Restaurant");
        nav_gallery.setTitle("Find Event");
        nav_slideshow.setTitle("My Event");
        nav_manage.setTitle("Discount??");
        nav_share.setTitle("Profile");
        nav_send.setTitle("Sign out");
    }
}
