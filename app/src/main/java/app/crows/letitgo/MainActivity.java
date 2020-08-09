package app.crows.letitgo;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 100;
    private FirebaseAuth mAuth;
    private ProgressBar progressBar;
    private FrameLayout frameLayout;
    private String lang = "none";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        progressBar = findViewById(R.id.progressBar);
        frameLayout = findViewById(R.id.frameLayout);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.top_menu, menu);
        return true;
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        updateUI();
        changeFragment(new EmotionsFragment(), "");
    }


    private void updateUI() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            // The user's ID, unique to the Firebase project. Do NOT use this value to
            // authenticate with your backend server, if you have one. Use
            // FirebaseUser.getIdToken() instead.
            String uID = currentUser.getUid();
            final DocumentReference docRef = FirebaseFirestore.getInstance().document("users/" + uID);
            docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if (documentSnapshot.exists()) {
                        // Existing user
                        lang = Objects.requireNonNull(documentSnapshot.get("language")).toString();
                        progressBar.setVisibility(View.GONE);
                        frameLayout.setVisibility(View.VISIBLE);
                    } else {
                        // New user
                        newUser(null);
                    }
                }
            });
        } else {
            signIn();
        }
    }

    public void newUser(MenuItem item) {
        Intent intent = new Intent(getApplicationContext(), NewUserActivity.class);
        startActivity(intent);
        finish();
    }

    public void signOut(MenuItem view) {
        mAuth.signOut();
        updateUI();
    }

    private void signIn() {
        // Choose authentication providers
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build());

        // Create and launch sign-in intent
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setIsSmartLockEnabled(false)
                        .setAvailableProviders(providers)
                        .setTheme(R.style.AppTheme)
                        .build(),
                RC_SIGN_IN);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }

    protected void onUserAction(int code) {
        String sEmotion = "";
        switch (code) {
            case 1:
                sEmotion = "Joy";
                break;
            case 2:
                sEmotion = "Sadness";
                break;
            case 3:
                sEmotion = "Trust";
                break;
            case 4:
                sEmotion = "Disgust";
                break;
            case 5:
                sEmotion = "Fear";
                break;
            case 6:
                sEmotion = "Anger";
                break;
            case 7:
                sEmotion = "Surprise";
                break;
            case 8:
                sEmotion = "Anticipation";
        }
        changeFragment(new ForumFragment(), sEmotion);
    }

    private void changeFragment(Fragment fragment, String emotion) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (!emotion.equals("")) {
            Bundle args = new Bundle();
            args.putString(ForumFragment.Lang, lang);
            args.putString(ForumFragment.Emotion, emotion);
            fragment.setArguments(args);
            transaction.addToBackStack(null);
        }
        transaction.replace(R.id.frameLayout, fragment);
        transaction.commit();
    }
}
