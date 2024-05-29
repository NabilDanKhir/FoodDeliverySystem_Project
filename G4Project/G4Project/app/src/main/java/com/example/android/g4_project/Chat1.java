package com.example.android.g4_project;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.PaintDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

public class Chat1 extends AppCompatActivity{

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    protected static int count=1;
    protected static int userQuestionCount=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_chat1);

        createQuestions();
        createDefaultText();
        cantfind();
    }
    private void createQuestions()
    {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        RelativeLayout r = (RelativeLayout) findViewById(R.id.questions);
        RelativeLayout.LayoutParams params1 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        RelativeLayout.LayoutParams params2 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        RelativeLayout.LayoutParams params3 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);

        Typeface font = ResourcesCompat.getFont(this, R.font.poppins);

        Button btn1 = new Button(r.getContext());
        btn1.setId(count);
        params1.setMargins(20, 20, 20, 20);
        btn1.setPadding(20, 0, 20, 0);
        btn1.getBackground().setTint(ContextCompat.getColor(this, R.color.yellow));
        btn1.setTypeface(font);
        count++;

        Button btn2 = new Button(r.getContext());
        btn2.setId(count);
        params2.addRule(RelativeLayout.BELOW, count-1);
        params2.setMargins(20, 20, 20, 20);
        btn2.setPadding(20, 0, 20, 0);
        btn2.getBackground().setTint(ContextCompat.getColor(this, R.color.yellow));
        btn2.setTypeface(font);
        count++;

        Button btn3 = new Button(r.getContext());
        btn3.setId(count);
        params3.addRule(RelativeLayout.RIGHT_OF, count-2);
        params3.setMargins(20, 20, 20, 20);
        btn3.setPadding(20, 0, 20, 0);
        btn3.getBackground().setTint(ContextCompat.getColor(this, R.color.yellow));
        btn3.setTypeface(font);
        count++;

        DocumentReference doc1 = db.collection("Questions").document("HowDistanceWorks");
        doc1.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                btn1.setText(documentSnapshot.getString("Question"));
            }
        });
        DocumentReference doc2 = db.collection("Questions").document("ProfileChangeLimitations");
        doc2.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                btn2.setText(documentSnapshot.getString("Question"));
            }
        });
        DocumentReference doc3 = db.collection("Questions").document("DarkModeWhen");
        doc3.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                btn3.setText(documentSnapshot.getString("Question"));
            }
        });

        r.addView(btn1, params1);
        r.addView(btn2, params2);
        r.addView(btn3, params3);

        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String docId=doc1.getId();
                QnAButtonAction(docId);
            }
        });
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String docId=doc2.getId();
                QnAButtonAction(docId);
            }
        });
        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String docId=doc3.getId();
                QnAButtonAction(docId);
            }
        });

        /*db.collection("Questions").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>()
        {
           @Override
           public void onComplete(@NonNull Task<QuerySnapshot> task) {
               int counter=1;
               for(QueryDocumentSnapshot document : task.getResult()) {
                   Button btn = new Button(r.getContext());
                   btn.setId(count);
                   params.setMargins(20, 20, 20, 20);
                   btn.setPadding(10, 0, 10, 0);
                   btn.setTypeface(font);

                   if (counter > 1)
                   {
                       if ((count) % 2 == 0)
                       {
                           params.addRule(RelativeLayout.BELOW, count);
                           Log.i("In %2",document.getString("Question")+", count: "+count+", counter: "+counter);
                       } else
                       {
                           params.addRule(RelativeLayout.RIGHT_OF, count - 1);
                           Log.i("in else",document.getString("Question")+", count: "+count+", counter: "+counter);
                       }
                   }

                   Log.i("Question",document.getString("Question")+", count: "+count+", counter: "+counter);

                   btn.setText(document.getString("Question"));
                   r.addView(btn, params);
                   count++;
                   counter++;

                   btn.setOnClickListener(new View.OnClickListener() {
                       @Override
                       public void onClick(View view) {
                           String docId= document.getId();
                           QnAButtonAction(docId);
                       }
                   });
               }
           }
        });*/
    }
    private void createDefaultText()
    {
        RelativeLayout r = (RelativeLayout) findViewById(R.id.chat);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);

        Typeface font = ResourcesCompat.getFont(this, R.font.poppins);

        TextView tv = new TextView(r.getContext());
        tv.setId(count);
        params.setMargins(20, 20, 300, 20);
        tv.setBackground(getDrawable(R.drawable.edit_text_border));
        tv.setPadding(15,0,15,0);
        tv.setTypeface(font);
        count++;

        tv.setText(getResources().getString(R.string.welcome_msg));

        r.addView(tv, params);
    }
    private void QnAButtonAction(String docId)
    {
        RelativeLayout r = (RelativeLayout) findViewById(R.id.chat);
        RelativeLayout.LayoutParams params1 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        RelativeLayout.LayoutParams params2 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);

        ScrollView chatScroll = findViewById(R.id.chatScroll);
        Typeface font = ResourcesCompat.getFont(this, R.font.poppins);
        GradientDrawable bubbleShape = new GradientDrawable();
        bubbleShape.setColor(ContextCompat.getColor(this, R.color.yellow));
        bubbleShape.setCornerRadius(12);
        bubbleShape.setStroke(2, ContextCompat.getColor(this, R.color.black));

        TextView tv1 = new TextView(r.getContext());
        tv1.setId(count);
        params1.addRule(RelativeLayout.BELOW, count-1);
        params1.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, count-1);
        params1.setMargins(300, 5, 20, 5);
        tv1.setBackground(bubbleShape);
        tv1.setPadding(15,0,15,0);
        tv1.setTypeface(font);
        count++;

        TextView tv2 = new TextView(r.getContext());
        tv2.setId(count);
        params2.addRule(RelativeLayout.BELOW, count-1);
        params2.setMargins(20, 15, 300, 15);
        tv2.setBackground(getDrawable(R.drawable.edit_text_border));
        tv2.setPadding(15,0,15,0);
        tv2.setTypeface(font);
        count++;

        DocumentReference doc = db.collection("Questions").document(docId);
        doc.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                tv1.setText(documentSnapshot.getString("Question"));
                tv2.setText(documentSnapshot.getString("Answer"));
            }
        });

        r.addView(tv1, params1);
        r.addView(tv2, params2);
        chatScroll.fullScroll(View.FOCUS_DOWN);
    }
    private void cantfind()
    {
        Button cantfind = findViewById(R.id.cantFindButton);
        EditText questionbox = findViewById(R.id.userquestion);
        Button send = findViewById(R.id.send);

        cantfind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cantfind.setVisibility(View.GONE);
                questionbox.setVisibility(View.VISIBLE);
                send.setVisibility(View.VISIBLE);
            }
        });

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DocumentReference doc = db.collection("UserQuestions").document(user.getEmail());

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(questionbox.getText().toString().trim().isEmpty()==false)
                {
                    String randomFieldName = db.collection("UserQuestions").document().getId();
                    Map<String, Object> question = new HashMap<>();
                    question.put(randomFieldName, questionbox.getText().toString().trim());
                    doc.set(question, SetOptions.merge());
                    createBubble(questionbox.getText().toString().trim());
                    questionbox.getText().clear();
                }
            }
        });
    }
    private void createBubble(String question)
    {
        RelativeLayout r = (RelativeLayout) findViewById(R.id.chat);
        RelativeLayout.LayoutParams params1 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        RelativeLayout.LayoutParams params2 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);

        ScrollView chatScroll = findViewById(R.id.chatScroll);
        Typeface font = ResourcesCompat.getFont(this, R.font.poppins);
        GradientDrawable bubbleShape = new GradientDrawable();
        bubbleShape.setColor(ContextCompat.getColor(this, R.color.yellow));
        bubbleShape.setCornerRadius(12);
        bubbleShape.setStroke(2, ContextCompat.getColor(this, R.color.black));

        TextView tv1 = new TextView(r.getContext());
        tv1.setId(count);
        params1.addRule(RelativeLayout.BELOW, count-1);
        params1.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, count-1);
        params1.setMargins(300, 5, 20, 5);
        tv1.setBackground(bubbleShape);
        tv1.setPadding(15,0,15,0);
        tv1.setTypeface(font);
        tv1.setText(question);
        count++;

        TextView tv2 = new TextView(r.getContext());
        tv2.setId(count);
        params2.addRule(RelativeLayout.BELOW, count-1);
        params2.setMargins(20, 5, 300, 5);
        tv2.setBackground(getDrawable(R.drawable.edit_text_border));
        tv2.setPadding(15,0,15,0);
        tv2.setTypeface(font);
        tv2.setText(getResources().getString(R.string.automated_message));
        count++;

        r.addView(tv1, params1);
        r.addView(tv2, params2);
        chatScroll.fullScroll(View.FOCUS_DOWN);
    }
}