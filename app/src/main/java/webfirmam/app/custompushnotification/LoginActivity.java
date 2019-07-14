package webfirmam.app.custompushnotification;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
    EditText emailText ;
    EditText passwordText;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        emailText = findViewById(R.id.emailText);
        passwordText = findViewById(R.id.passwordText);
        mAuth = FirebaseAuth.getInstance();
        //eğer giriş yapmış kullanıcı varsa direk bildirim yerine götür
        FirebaseUser user = mAuth.getCurrentUser();
        if (user !=null){
            String email = user.getEmail().toString();
            Intent intent = new Intent(getApplicationContext(),SendPushNotification.class);
            Toast.makeText(LoginActivity.this,"Hoşgeldiniz "+email.toString(),Toast.LENGTH_LONG).show();
            startActivity(intent);
        }
    }
    public void login(View view){
        if (!emailText.getText().toString().equals("") && !passwordText.getText().toString().equals("")){
            mAuth.signInWithEmailAndPassword(emailText.getText().toString(),passwordText.getText().toString())
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()){
                                FirebaseUser user = mAuth.getCurrentUser();
                                String email = user.getEmail().toString();
                                Intent intent = new Intent(getApplicationContext(),SendPushNotification.class);
                                Toast.makeText(LoginActivity.this,"Hoşgeldiniz "+email.toString(),Toast.LENGTH_LONG).show();
                                startActivity(intent);
                            }else {
                                Toast.makeText(LoginActivity.this,"Giriş Başarısız! Site Adminiyle Görüşün!",Toast.LENGTH_LONG).show();

                            }
                        }
                    });
        }else {
            Toast.makeText(LoginActivity.this,"Lütfen Her Alanı Doldurunuz!!",Toast.LENGTH_LONG).show();

        }


    }
}
