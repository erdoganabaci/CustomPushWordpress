package webfirmam.app.custompushnotification;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.widget.Toast;

import com.onesignal.OneSignal;

import java.util.Timer;
import java.util.TimerTask;

public class MyService extends Service {
    Handler handler = new Handler();


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        handler = new Handler();
        Timer timer = new Timer();

        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        try {
                            final DownloadData downloadData = new DownloadData();

                            try {
                                //aşağıdaki asynctaskin stringine parametre geçirmeye yarıyor
                                final String url = "http://osmanozet.webfirmam.com.tr/appconfig/getpost.php";
                                //her saniye execute et

                                OneSignal.startInit(getApplicationContext())
                                        .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                                        .unsubscribeWhenNotificationsAreDisabled(true)
                                        .init();

                                // Create the Handler object (on the main thread by default)
                                // Define the code block to be executed

                                // Do something here on the main thread
                                downloadData.execute(url);

                                Toast.makeText(getApplicationContext(),"Calısıyor ..",Toast.LENGTH_LONG).show();
                                // Repeat this the same runnable code block again another 2 seconds



                            } catch (Exception e) {
                                System.out.println("Api not respond maybe site down");
                            }

                        } catch (Exception e) {
                            // error, do something
                        }
                    }
                });
            }
        };

        timer.schedule(task, 0, 30*1000);  // interval of one minute
        return START_STICKY;
    }

    @Override
    public void onDestroy() {

        //super.onDestroy();
    }
}