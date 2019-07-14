package webfirmam.app.custompushnotification;

import android.os.AsyncTask;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.onesignal.OneSignal;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

class DownloadData extends AsyncTask<String, Void, String> {
    String finalDate;
    String finalPageTitle;
    String finalPageSubTitle;
    Handler handler = new Handler();
    Runnable runnableCode ;
    String CurrentDate;
    FirebaseDatabase database;
    DatabaseReference databaseReference;

    /*
    @Override
    protected String doInBackground(String... strings) {

        String result = "";
        final URL url;
        final HttpURLConnection[] httpURLConnection = new HttpURLConnection[1];
        final InputStreamReader[] inputStreamReader = new InputStreamReader[1];

        try {

            url = new URL(strings[0]);
             handler = new Handler();
             runnableCode= new Runnable() {

                public void run() {

                    try
                    {
                        httpURLConnection[0] = (HttpURLConnection) url.openConnection();
                        InputStream inputStream = httpURLConnection[0].getInputStream();
                         inputStreamReader[0] = new InputStreamReader(inputStream);

                    } catch (Exception e) {

                    }

                    handler.postDelayed(this, 2000);

                }
            };


            int data = inputStreamReader[0].read();

            while (data > 0) {

                char character = (char) data;
                result += character;

                data = inputStreamReader[0].read();

            }




        } catch (Exception e) {
            //hata varsa null yap
            return null;
        }
        return result;
    } */

    @Override
    protected String doInBackground(String... strings) {

        String result = "";
        URL url;
        HttpURLConnection httpURLConnection;

        try {

            url = new URL(strings[0]);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            InputStream inputStream = httpURLConnection.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);

            int data = inputStreamReader.read();

            while (data > 0) {

                char character = (char) data;
                result += character;

                data = inputStreamReader.read();

            }


            return result;

        } catch (Exception e) {
            //hata varsa null yap
            return null;
        }

    }
    /*
    @Override
    protected void onPostExecute(final String s) {
        super.onPostExecute(s);
        System.out.println("alınan data:" + s);



        //System.out.println("compare :"+CompareString(CurrentDate,finalDate));

         runnableCode = new Runnable() {
            @Override
            public void run() {

                DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");

                CurrentDate = df.format(Calendar.getInstance().getTime());
                // Do something here on the main thread

                try {

                    JSONObject parentObject  = new JSONObject(s);
                    JSONArray parentArray = parentObject.getJSONArray("postdates");
                    JSONObject finalObject = parentArray.getJSONObject(0);
                    finalDate = finalObject.getString("post_date");
                    System.out.println("dates :"+finalDate);

                } catch (Exception e) {
                    System.out.println("Something Wrong Post Execute");
                }

                final Boolean compareDates = CompareString(CurrentDate,finalDate);

                System.out.println("calısıyor"+CurrentDate);
                // Do something here on the main thread
                if (compareDates){
                    System.out.println("compare"+compareDates);
                    OneSignal.idsAvailable(new OneSignal.IdsAvailableHandler() {
                        @Override
                        public void idsAvailable(String userId, String registrationId) {
                            try {
                                OneSignal.postNotification(new JSONObject("{'contents': {'en':'İçerik Güncellendi'}, 'include_player_ids': ['" + userId + "']}"), null);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });


                }
                // Repeat this the same runnable code block again another 2 seconds
                // 'this' is referencing the Runnable object
                handler.postDelayed(this, 6000);
            }
        };
        // Start the initial runnable task by posting through the handler
        handler.post(runnableCode);




    }   */
    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference();
        System.out.println("alınan data:" + s);
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String CurrentDate = df.format(Calendar.getInstance().getTime());
        try {

            JSONObject parentObject  = new JSONObject(s);
            JSONArray parentArray = parentObject.getJSONArray("postdates");
            JSONObject finalObject = parentArray.getJSONObject(0);


            finalDate = finalObject.getString("post_date");
            finalPageTitle = finalObject.getString("post_title");
            finalPageTitle = finalPageTitle + " Güncellendi";
            finalPageSubTitle = "Web sitenizde içerik güncellenmiştir.";
            System.out.println("final database :"+finalDate);

        } catch (Exception e) {
            System.out.println("Something Wrong Post Execute");
        }
        System.out.println("current date is :"+CurrentDate);
        Boolean compareDates = CompareString(CurrentDate,finalDate);
        //System.out.println("compare :"+CompareString(CurrentDate,finalDate));
        if (compareDates){
            System.out.println("compare"+compareDates);
            DatabaseReference newReference = database.getReference("PlayerIDs");
            newReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {

                        HashMap<String, String> hashMap = (HashMap<String, String>) ds.getValue();

                        String playerID = hashMap.get("playerID");

                        System.out.println("playerID server:" + playerID);

                        try {
                            //OneSignal.postNotification(new JSONObject("{'headings':{'en':'finalPageTitle'},'contents': {'en':'Everyone'}, 'include_player_ids': ['" + playerID + "']}"), null);
                            OneSignal.postNotification(new JSONObject("{'headings':{'en':'"+finalPageTitle+"'},'contents': {'en':'"+finalPageSubTitle+"'}, 'include_player_ids': ['" + playerID + "']}"), null);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            /*
            OneSignal.idsAvailable(new OneSignal.IdsAvailableHandler() {
                @Override
                public void idsAvailable(String userId, String registrationId) {
                    try {

                        //OneSignal.postNotification(new JSONObject(String.valueOf("{'headings':{'en':'"+finalPageTitle+"'},'contents': {'en':'Test Message'}, 'include_player_ids': ['" + userId + "']}")), null);
                        OneSignal.postNotification(new JSONObject(String.valueOf("{'headings':{'en':'"+finalPageTitle+"'},'contents': {'en':'"+finalPageSubTitle+"'}, 'include_player_ids': ['" + userId + "']}")), null);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
            */
        }





    }




    public Boolean CompareString(String currentDate , String databaseDate){
        String[] splitDatabaseArray =databaseDate.split(" ");
        //we dont use it tarihler aynı saatin saniye kısmını atcam
        String dateDatabaseIndex = splitDatabaseArray[0];
        String clockDatabaseIndex = splitDatabaseArray[1];
        String[] clockDatabaseHourIndex = clockDatabaseIndex.split(":");
        String clockDatabaseHour = clockDatabaseHourIndex[0];
        String clockDatabaseMin = clockDatabaseHourIndex[1];
        String clockCombineTime = clockDatabaseHour+":"+clockDatabaseMin;
        String dateClockCombineDate = dateDatabaseIndex+" "+clockCombineTime;
        Boolean check = dateClockCombineDate.equals(currentDate);
        //System.out.println("eşit mi :"+check);
        System.out.println("senin saatin :"+currentDate);
        return check;
    }


    /*
    Handler handler = new Handler();
// Define the code block to be executed
private Runnable runnableCode = new Runnable() {
    @Override
    public void run() {
      // Do something here on the main thread
      Log.d("Handlers", "Called on main thread");
      // Repeat this the same runnable code block again another 2 seconds
      // 'this' is referencing the Runnable object
      handler.postDelayed(this, 2000);
    }
};
// Start the initial runnable task by posting through the handler
handler.post(runnableCode);
     */
}