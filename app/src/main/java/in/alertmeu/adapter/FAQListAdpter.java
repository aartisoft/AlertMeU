package in.alertmeu.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import in.alertmeu.R;
import in.alertmeu.models.FAQDAO;
import in.alertmeu.models.MyPlaceModeDAO;
import in.alertmeu.utils.Config;
import in.alertmeu.utils.Listener;
import in.alertmeu.utils.WebClient;


public class FAQListAdpter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    SharedPreferences preferences;
    SharedPreferences.Editor prefEditor;
    private Context context;
    private LayoutInflater inflater;
    List<FAQDAO> data;
    FAQDAO current;
    int number = 1, clickflag = 1;
    Resources res;
    private static final String FILE_NAME = "file_lang";
    private static final String KEY_LANG = "key_lang";
    // create constructor to innitilize context and data sent from MainActivity
    public FAQListAdpter(Context context, List<FAQDAO> data) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.data = data;
        res = context.getResources();
        loadLanguage(context);
        preferences = context.getSharedPreferences("Prefrence", Context.MODE_PRIVATE);
        prefEditor = preferences.edit();
    }

    // Inflate the layout when viewholder created
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.layout_faq_details, parent, false);
        MyHolder holder = new MyHolder(view);

        return holder;
    }

    // Bind data
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final int pos = position;
        // Get current position of item in recyclerview to bind data and assign values from list
        final MyHolder myHolder = (MyHolder) holder;
        current = data.get(position);
        if (preferences.getString("ulang", "").equals("en")) {
            myHolder.title.setText(current.getTitle());
            myHolder.title.setTag(position);
            myHolder.desc.setText(current.getDescription());
            myHolder.desc.setTag(position);
        } else if (preferences.getString("ulang", "").equals("hi")) {
            myHolder.title.setText(current.getTitle_hindi());
            myHolder.title.setTag(position);
            myHolder.desc.setText(current.getDescription_hindi());
            myHolder.desc.setTag(position);
        }
        myHolder.hideShow.setTag(position);
        myHolder.hideShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clickflag == 1) {
                    clickflag = 2;
                    myHolder.desc.setVisibility(View.VISIBLE);
                } else {
                    clickflag = 1;
                    myHolder.desc.setVisibility(View.GONE);
                }
            }
        });

    }


    // return total item from List
    @Override
    public int getItemCount() {
        return data.size();
    }


    class MyHolder extends RecyclerView.ViewHolder {

        TextView title, desc;
        LinearLayout hideShow;


        // create constructor to get widget reference
        public MyHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.title);
            desc = (TextView) itemView.findViewById(R.id.desc);
            hideShow = (LinearLayout) itemView.findViewById(R.id.hideShow);
        }

    }

    private String getLangCode(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(FILE_NAME, context.MODE_PRIVATE);
        String langCode = preferences.getString(KEY_LANG, "");
        return langCode;
    }

    private void loadLanguage(Context context) {
        Locale locale = new Locale(getLangCode(context));
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        context.getResources().updateConfiguration(config, context.getResources().getDisplayMetrics());
    }
}
