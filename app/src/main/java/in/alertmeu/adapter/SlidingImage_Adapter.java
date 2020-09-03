package in.alertmeu.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import in.alertmeu.R;
import in.alertmeu.imageUtils.ImageLoader;
import in.alertmeu.models.ImageModel;


public class SlidingImage_Adapter extends PagerAdapter {
    SharedPreferences preferences;
    SharedPreferences.Editor prefEditor;
    private List<ImageModel> data;
    private LayoutInflater inflater;
    private Context context;
    ImageModel current;
    Resources res;
    private static final String FILE_NAME = "file_lang";
    private static final String KEY_LANG = "key_lang";
    public SlidingImage_Adapter(Context context, List<ImageModel> data) {
        this.context = context;
        this.data = data;
        inflater = LayoutInflater.from(context);
        res = context.getResources();
        loadLanguage(context);
        preferences = context.getSharedPreferences("Prefrence", Context.MODE_PRIVATE);
        prefEditor = preferences.edit();
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object instantiateItem(ViewGroup view, int position) {
        View imageLayout = inflater.inflate(R.layout.slidingimages_layout, view, false);
        current = data.get(position);
        assert imageLayout != null;
        final ImageView imageView = (ImageView) imageLayout.findViewById(R.id.image);
        final TextView imageDescription = (TextView) imageLayout.findViewById(R.id.imageDescription);
        ImageLoader imageLoader = new ImageLoader(context);
        imageLoader.DisplayImage(current.getImage_path(), imageView);
        if (preferences.getString("ulang", "").equals("en")) {
            imageDescription.setText(current.getImage_description());
        } else if (preferences.getString("ulang", "").equals("hi")) {
            imageDescription.setText(current.getImage_description_hindi());
        }
        view.addView(imageLayout, 0);

        return imageLayout;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view.equals(object);
    }

    @Override
    public void restoreState(Parcelable state, ClassLoader loader) {
    }

    @Override
    public Parcelable saveState() {
        return null;
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