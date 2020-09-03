package in.alertmeu.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import in.alertmeu.R;
import in.alertmeu.activity.UserProfileSettingActivity;
import in.alertmeu.activity.ViewImageDescriptionActivity;
import in.alertmeu.imageUtils.ImageLoader;
import in.alertmeu.imageUtils.ImageloaderNew;
import in.alertmeu.models.ExAdvertisementDAO;


public class AdvertisementsAdapter extends RecyclerView.Adapter<AdvertisementsAdapter.ViewHolder> {

    private Context context;
    private List<ExAdvertisementDAO> advertisements = new ArrayList<ExAdvertisementDAO>();
    ExAdvertisementDAO current;
    SharedPreferences preferences;
    SharedPreferences.Editor prefEditor;

    public AdvertisementsAdapter(Context context, List<ExAdvertisementDAO> advertisements) {
        this.context = context;
        this.advertisements = advertisements;
        preferences = context.getSharedPreferences("Prefrence", Context.MODE_PRIVATE);
        prefEditor = preferences.edit();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View cardView = inflater.inflate(R.layout.item_child, null, false);
        ViewHolder viewHolder = new ViewHolder(cardView);
        viewHolder.mobileImage = (ImageView) cardView.findViewById(R.id.image_mobile);
        viewHolder.modelName = (TextView) cardView.findViewById(R.id.text_mobile_model);
        // viewHolder.price = (TextView) cardView.findViewById(R.id.text_mobile_price);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final ViewHolder myHolder = (ViewHolder) holder;
        current = advertisements.get(position);
        ImageView mobileImageView = (ImageView) holder.mobileImage;
        // mobileImageView.setImageResource(advertisements.get(position).id);
        /*ImageLoader imageLoader = new ImageLoader(context);
        imageLoader.DisplayImage(advertisements.get(position).getOriginal_image_path(),mobileImageView);
        mobileImageView.setTag(position);*/
        ImageloaderNew imageLoader = new ImageloaderNew(context);
        mobileImageView.setTag(advertisements.get(position).getOriginal_image_path());
        imageLoader.DisplayImage(advertisements.get(position).getOriginal_image_path(), context, mobileImageView);
        //   mobileImageView.setTag(position);
        TextView modelTextView = (TextView) holder.modelName;
        modelTextView.setText(advertisements.get(position).title);
        LinearLayout clicki = (LinearLayout) holder.clickitem;
        clicki.setTag(position);
        // TextView priceTextView = (TextView) holder.price;
        // priceTextView.setText(advertisements.get(position).title);

        clicki.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int ID = (Integer) v.getTag();
                Log.e("", "list Id" + ID);
                current = advertisements.get(ID);
                Intent intent = new Intent(context, ViewImageDescriptionActivity.class);
                intent.putExtra("id", current.getId());
                intent.putExtra("qrCode", current.getRq_code());
                intent.putExtra("lat", "" + current.getLatitude());
                intent.putExtra("long", "" + current.getLongitude());
                intent.putExtra("imagePath", current.getOriginal_image_path());
                intent.putExtra("title", current.getTitle());
                intent.putExtra("description", current.getDescription());
                intent.putExtra("business", current.getBusiness_name() + "\n" + current.getAddress());
                intent.putExtra("stime", "" + current.getS_time());
                intent.putExtra("etime", "" + current.getE_time());
                intent.putExtra("sdate", current.getS_date());
                intent.putExtra("edate", current.getE_date());
                intent.putExtra("likecnt", current.getLikecnt());
                intent.putExtra("dislikecnt", current.getDislikecnt());
                intent.putExtra("mobile_no", current.getBusiness_number());
                intent.putExtra("email", current.getBusiness_email());
                intent.putExtra("describe_limitations", current.getDescribe_limitations());
                intent.putExtra("main_cat_name", current.getMain_cat_name());
                intent.putExtra("subcategory_name", current.getSubcategory_name());
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return advertisements.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView mobileImage;
        TextView modelName;
        TextView price;
        LinearLayout clickitem;

        public ViewHolder(View itemView) {
            super(itemView);
            mobileImage = (ImageView) itemView.findViewById(R.id.image_mobile);
            modelName = (TextView) itemView.findViewById(R.id.text_mobile_model);
            clickitem = (LinearLayout) itemView.findViewById(R.id.clickitem);
            //  price = (TextView) itemView.findViewById(R.id.text_mobile_price);
        }
    }

}
