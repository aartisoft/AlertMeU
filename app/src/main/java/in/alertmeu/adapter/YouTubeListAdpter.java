package in.alertmeu.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;
import java.util.Locale;

import in.alertmeu.R;
import in.alertmeu.models.YouTubeDAO;


public class YouTubeListAdpter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    SharedPreferences preferences;
    SharedPreferences.Editor prefEditor;
    private Context context;
    private LayoutInflater inflater;
    List<YouTubeDAO> data;
    YouTubeDAO current;
    int number = 1, clickflag = 1;
    Resources res;
    private static final String FILE_NAME = "file_lang";
    private static final String KEY_LANG = "key_lang";
    // create constructor to innitilize context and data sent from MainActivity
    public YouTubeListAdpter(Context context, List<YouTubeDAO> data) {
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
        View view = inflater.inflate(R.layout.layout_youtube_details, parent, false);
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
            myHolder.title.setText(current.getVideo_description());
            myHolder.title.setTag(position);
        } else if (preferences.getString("ulang", "").equals("hi")) {
            myHolder.title.setText(current.getVideo_description_hindi());
            myHolder.title.setTag(position);
        }
        myHolder.hideShow.setTag(position);
        myHolder.playVideo.setTag(position);
        myHolder.shareVideo.setTag(position);

        myHolder.hideShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        myHolder.playVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int ID = (Integer) v.getTag();
                Log.e("", "list Id" + ID);
                current = data.get(ID);
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(current.getVideo_link()));
                context.startActivity(intent);
            }
        });
        myHolder.shareVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int ID = (Integer) v.getTag();
                Log.e("", "list Id" + ID);
                current = data.get(ID);
                Intent shareIntent = new Intent();
                shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                shareIntent.putExtra(Intent.EXTRA_TEXT, current.getVideo_description() + "\n" + current.getVideo_link());
                // shareIntent.putExtra(Intent.EXTRA_STREAM, bmpUri);
                shareIntent.setType("text/plain");
                context.startActivity(Intent.createChooser(shareIntent, "Share Video with"));

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
        ImageView playVideo, shareVideo;

        // create constructor to get widget reference
        public MyHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.title);
            desc = (TextView) itemView.findViewById(R.id.desc);
            hideShow = (LinearLayout) itemView.findViewById(R.id.hideShow);
            playVideo = (ImageView) itemView.findViewById(R.id.playVideo);
            shareVideo = (ImageView) itemView.findViewById(R.id.shareVideo);
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
