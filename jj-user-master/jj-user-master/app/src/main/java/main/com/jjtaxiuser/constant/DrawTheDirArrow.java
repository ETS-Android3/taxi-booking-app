package main.com.jjtaxiuser.constant;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.StrictMode;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.net.URL;

import main.com.jjtaxiuser.R;


/**
 * Created by technorizen on 22/5/18.
 */

public class DrawTheDirArrow {
    public static double degreesPerRadian = 180.0 / Math.PI;
    public static Context context_va;
    public static void DrawArrowHead(Activity collectiveRideDetail, GoogleMap mMap, LatLng from, LatLng to) {
        context_va = collectiveRideDetail;
        Log.e("COME DRAW ARROW", "TRUE");
        double bearing = GetBearing(from, to);
        double adjBearing = Math.round(bearing / 3) * 3;
        while (adjBearing >= 120) {
            adjBearing -= 120;
        }

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        // Get the corresponding triangle marker from Google
        URL url;
        Bitmap image = null;
        image = BitmapFactory.decodeResource(context_va.getResources(), R.drawable.carfromabove);

/*
        try {
            url = new URL("http://www.google.com/intl/en_ALL/mapfiles/dir_" + String.valueOf((int) adjBearing) + ".png");
            try {
                image = BitmapFactory.decodeStream(url.openConnection().getInputStream());
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
*/

        if (image != null) {

            // Anchor is ratio in range [0..1] so value of 0.5 on x and y will center the marker image on the lat/long
            float anchorX = 0.5f;
            float anchorY = 0.5f;

            int offsetX = 0;
            int offsetY = 0;

            // images are 24px x 24px
            // so transformed image will be 48px x 48px

            //315 range -- 22.5 either side of 315
            if (bearing >= 292.5 && bearing < 335.5) {
                offsetX = 24;
                offsetY = 24;
            }
            //270 range
            else if (bearing >= 247.5 && bearing < 292.5) {
                offsetX = 24;
                offsetY = 12;
            }
            //225 range
            else if (bearing >= 202.5 && bearing < 247.5) {
                offsetX = 24;
                offsetY = 0;
            }
            //180 range
            else if (bearing >= 157.5 && bearing < 202.5) {
                offsetX = 12;
                offsetY = 0;
            }
            //135 range
            else if (bearing >= 112.5 && bearing < 157.5) {
                offsetX = 0;
                offsetY = 0;
            }
            //90 range
            else if (bearing >= 67.5 && bearing < 112.5) {
                offsetX = 0;
                offsetY = 12;
            }
            //45 range
            else if (bearing >= 22.5 && bearing < 67.5) {
                offsetX = 0;
                offsetY = 24;
            }
            //0 range - 335.5 - 22.5
            else {
                offsetX = 12;
                offsetY = 24;
            }
            Bitmap wideBmp;
            Canvas wideBmpCanvas;
            Rect src, dest;
            wideBmp = Bitmap.createBitmap(image.getWidth() * 2, image.getHeight() * 2, image.getConfig());
            wideBmpCanvas = new Canvas(wideBmp);
            src = new Rect(0, 0, image.getWidth(), image.getHeight());
            dest = new Rect(src);
            dest.offset(offsetX, offsetY);
            wideBmpCanvas.drawBitmap(image, src, dest, null);
            mMap.addMarker(new MarkerOptions()
                    .position(to)
                    .icon(BitmapDescriptorFactory.fromBitmap(wideBmp))
                    .anchor(anchorX, anchorY));
        }
    }

    public static double GetBearing(LatLng from, LatLng to) {
        double lat1 = from.latitude * Math.PI / 180.0;
        double lon1 = from.longitude * Math.PI / 180.0;
        double lat2 = to.latitude * Math.PI / 180.0;
        double lon2 = to.longitude * Math.PI / 180.0;
        double angle = -Math.atan2(Math.sin(lon1 - lon2) * Math.cos(lat2), Math.cos(lat1) * Math.sin(lat2) - Math.sin(lat1) * Math.cos(lat2) * Math.cos(lon1 - lon2));
        if (angle < 0.0)
            angle += Math.PI * 2.0;
        angle = angle * degreesPerRadian;
        return angle;
    }
    public double bearingBetweenLocations(LatLng latLng1, LatLng latLng2) {

        double PI = 3.14159;
        double lat1 = latLng1.latitude * PI / 180;
        double long1 = latLng1.longitude * PI / 180;
        double lat2 = latLng2.latitude * PI / 180;
        double long2 = latLng2.longitude * PI / 180;
        double dLon = (long2 - long1);

        double y = Math.sin(dLon) * Math.cos(lat2);
        double x = Math.cos(lat1) * Math.sin(lat2) - Math.sin(lat1)
                * Math.cos(lat2) * Math.cos(dLon);

        double brng = Math.atan2(y, x);

        brng = Math.toDegrees(brng);
        brng = (brng + 360) % 360;

        return brng;
    }

}
