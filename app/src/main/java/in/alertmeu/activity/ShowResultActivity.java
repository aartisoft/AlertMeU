package in.alertmeu.activity;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import in.alertmeu.R;

public class ShowResultActivity extends AppCompatActivity {
    TextView results;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_result);
        results=(TextView)findViewById(R.id.results);
        Intent intent=getIntent();
        results.setText(intent.getStringExtra("value"));
        if(intent.getStringExtra("value").contains("https://play.google.com/store/")) {
            Intent i = new Intent(android.content.Intent.ACTION_VIEW);
            i.setData(Uri.parse(intent.getStringExtra("value")));
            startActivity(i);
        }
    }
}

