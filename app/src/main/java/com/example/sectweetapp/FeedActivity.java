package com.example.sectweetapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.Map;

public class FeedActivity extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;

    ArrayList<String> userEmailFromFB;
    ArrayList<String> userTweetFromFB;
    TweetRecyclerAdapter tweetRecycerAdapter;



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.options_menu,menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.add_tweet) {
            Intent intentToTweet = new Intent(FeedActivity.this, TweetActivity.class);
            startActivity(intentToTweet);
        } else if (item.getItemId() == R.id.signout) {

            firebaseAuth.signOut();

            Intent intentToSignUp = new Intent(FeedActivity.this, SignUpActivity.class);
            intentToSignUp.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intentToSignUp);
        }



        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        userEmailFromFB = new ArrayList<>();
        userTweetFromFB = new ArrayList<>();

        getDataFromFirestore();

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        tweetRecycerAdapter = new TweetRecyclerAdapter(userEmailFromFB,userTweetFromFB);
        recyclerView.setAdapter(tweetRecycerAdapter);


    }

    @SuppressLint("NotifyDataSetChanged")
    public void getDataFromFirestore() {
        CollectionReference collectionReference = firebaseFirestore.collection("Tweets");

        collectionReference.orderBy("date", Query.Direction.DESCENDING).addSnapshotListener((queryDocumentSnapshots, e) -> {

            if (e != null) {
                Toast.makeText(FeedActivity.this, e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            }

            if (queryDocumentSnapshots != null) {

                for (DocumentSnapshot snapshot : queryDocumentSnapshots.getDocuments()) {

                    Map<String,Object> data = snapshot.getData();

                    //Casting
                    assert data != null;
                    String tweet = (String) data.get("tweet");
                    String userEmail = (String) data.get("useremail");

                    userTweetFromFB.add(tweet);
                    userEmailFromFB.add(userEmail);

                    tweetRecycerAdapter.notifyDataSetChanged();

                }


            }

        });


    }
}
