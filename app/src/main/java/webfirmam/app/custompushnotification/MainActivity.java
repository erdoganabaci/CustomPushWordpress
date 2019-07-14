package webfirmam.app.custompushnotification;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.onesignal.OneSignal;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    private WebView webView;
    private CustomWebViewClient webViewClient;
    private String Url = "http://webfirmam.net";
    ProgressDialog mProgressDialog;
    Handler handler = new Handler();
    FirebaseDatabase database;
    DatabaseReference databaseReference;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.add_user,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.login_menu){
            Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();
        OneSignal.startInit(this)
                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                .unsubscribeWhenNotificationsAreDisabled(true)
                .init();

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("Yükleniyor...");

        webViewClient = new CustomWebViewClient();
        webView = findViewById(R.id.webview);//webview mızı xml anasayfa.xml deki webview bağlıyoruz
        webView.getSettings().setBuiltInZoomControls(true); //zoom yapılmasına izin verir
        webView.getSettings().setSupportZoom(true);
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        webView.getSettings().setAllowFileAccess(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(webViewClient); //oluşturduğumuz webViewClient objesini webViewımıza set ediyoruz
        webView.loadUrl(Url);

        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference();



        OneSignal.idsAvailable(new OneSignal.IdsAvailableHandler() {
            @Override
            public void idsAvailable(final String userId, String registrationId) {
                System.out.println("userID: " + userId);

                UUID uuid = UUID.randomUUID();
                final String uuidString = uuid.toString();

                DatabaseReference newReference = database.getReference("PlayerIDs");
                newReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        ArrayList<String> playerIDsFromServer = new ArrayList<>();

                        for (DataSnapshot ds: dataSnapshot.getChildren()) {

                            HashMap<String, String> hashMap = (HashMap<String, String>) ds.getValue();
                            String currentPlayerID = hashMap.get("playerID");

                            playerIDsFromServer.add(currentPlayerID);
                        }
                        //Eğer o kullanıcı yoksa ekle aynı telefon idleri eklemek 2 defa göndermiş olucak.
                        if (!playerIDsFromServer.contains(userId)) {
                            databaseReference.child("PlayerIDs").child(uuidString).child("playerID").setValue(userId);

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }
        });

    }
    public void adminPanelFab(View view){
        Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
        startActivity(intent);
    }

    public void sendPush(View view){
/*
        DatabaseReference newReference = database.getReference("PlayerIDs");
        newReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot ds : dataSnapshot.getChildren()) {

                    HashMap<String, String> hashMap = (HashMap<String, String>) ds.getValue();

                    String playerID = hashMap.get("playerID");

                    System.out.println("playerID server:" + playerID);

                    try {
                        OneSignal.postNotification(new JSONObject("{'headings':{'en':'finalPageTitle'},'contents': {'en':'Everyone'}, 'include_player_ids': ['" + playerID + "']}"), null);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });*/

    }

    private class CustomWebViewClient extends WebViewClient {

        //Alttaki methodların hepsini kullanmak zorunda deilsiniz
        //Hangisi işinize yarıyorsa onu kullanabilirsiniz.
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) { //Sayfa yüklenirken çalışır
            super.onPageStarted(view, url, favicon);

            if(!mProgressDialog.isShowing())//mProgressDialog açık mı kontrol ediliyor
            {

                mProgressDialog.show();//mProgressDialog açık değilse açılıyor yani gösteriliyor ve yükleniyor yazısı çıkıyor
                handler.postDelayed(new Runnable() {
                    public void run() {
                        mProgressDialog.dismiss();
                    }
                }, 5000);

            }

        }

        @Override
        public void onPageFinished(WebView view, String url) {//sayfamız yüklendiğinde çalışıyor.
            super.onPageFinished(view, url);

            if(mProgressDialog.isShowing()){//mProgressDialog açık mı kontrol açıksa kapat ama sayfa yüklenmesi tamalanırsa
                mProgressDialog.dismiss();//mProgressDialog açıksa kapatılıyor
            }
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            // Bu method açılan sayfa içinden başka linklere tıklandığında açılmasına yarıyor.
            //Bu methodu override etmez yada edip içini boş bırakırsanız ilk url den açılan sayfa dışında başka sayfaya geçiş yapamaz

            view.loadUrl(url);//yeni tıklanan url i açıyor
            return true;
        }

        @Override
        public void onReceivedError(WebView view, int errorCode,String description, String failingUrl) {

            //	if(errorCode !=null){
            //		Timeout
            //	} şeklinde kullanabilirsiniz



        }
    }
    public void onBackPressed() //Android Back Buttonunu Handle ediyoruz. Back butonu bir önceki sayfaya geri dönecek
    {
        if(webView.canGoBack()){//eğer varsa bir önceki sayfaya gidecek
            webView.goBack();
        }else{//Sayfa yoksa uygulamadan çıkacak
            super.onBackPressed();
        }
    }


}
