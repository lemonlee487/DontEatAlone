package cyruslee487.donteatalone;

import android.support.design.widget.NavigationView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.util.Calendar;

import cyruslee487.donteatalone.DiscountRoomDatabase.Discount;
import cyruslee487.donteatalone.EventRoomDatabase.Event;

public class UtilFunction {

    private static final String TAG = "DB";

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
        nav_slideshow.setTitle("Get Discount");
        nav_manage.setTitle("My Event");
        nav_share.setTitle("Profile");
        nav_send.setTitle("Sign out");
    }

    public static boolean checkExpiredEvent(Event event){
        String[] sdate = event.getDate().split("/");
        String[] stime = event.getTime().split(":");
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH)+1;
        int day = cal.get(Calendar.DAY_OF_MONTH);
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int minute = cal.get(Calendar.MINUTE);
        int event_concat_date = Integer.parseInt(sdate[0]+sdate[1]+sdate[2]);
        int event_concat_time = Integer.parseInt(stime[0]+stime[1]);
        int current_concat_date = Integer.parseInt(""+year+month+day);
        int current_concat_time = Integer.parseInt(""+hour+minute);

        if(event_concat_date > current_concat_date){
            //Log.d(TAG, "checkExpiredEvent: Event date > current date");
            return true;
        }else if(event_concat_date == current_concat_date){
            //Log.d(TAG, "checkExpiredEvent: Event date == current date");
            //Log.d(TAG, "checkExpiredEvent: " + event_concat_time + " " + current_concat_time);
            if(event_concat_time >= current_concat_time){
                //Log.d(TAG, "checkExpiredEvent: Event time >= current time");
                return true;
            }else{
                //Log.d(TAG, "checkExpiredEvent: Event time < current time");
                return false;
            }
        }else {
            //Log.d(TAG, "checkExpiredEvent: Event date < current date");
            return false;
        }
    }

    public static boolean checkExpiredDiscount(Discount discount){
        String[] sdate = discount.getEndDate().split("/");
        String[] stime = discount.getEndTime().split(":");
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH)+1;
        int day = cal.get(Calendar.DAY_OF_MONTH);
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int minute = cal.get(Calendar.MINUTE);
        int discount_concat_date = Integer.parseInt(sdate[0]+sdate[1]+sdate[2]);
        int discount_concat_time = Integer.parseInt(stime[0]+stime[1]);
        int current_concat_date = Integer.parseInt(""+year+month+day);
        int current_concat_time = Integer.parseInt(""+hour+minute);


        if(discount_concat_date > current_concat_date){
            Log.d(TAG, "checkExpiredEvent: Discount date > current date");
            return true;
        }else if(discount_concat_date == current_concat_date){
            Log.d(TAG, "checkExpiredEvent: Discount date == current date");
            //Log.d(TAG, "checkExpiredEvent: " + event_concat_time + " " + current_concat_time);
            if(discount_concat_time >= current_concat_time){
                Log.d(TAG, "checkExpiredEvent: Discount time >= current time");
                return true;
            }else{
                Log.d(TAG, "checkExpiredEvent: Discount time < current time");
                return false;
            }
        }else {
            Log.d(TAG, "checkExpiredEvent: Discount date < current date");
            return false;
        }
    }
}
