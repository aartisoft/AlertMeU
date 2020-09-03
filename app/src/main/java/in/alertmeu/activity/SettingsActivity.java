package in.alertmeu.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.ToneGenerator;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import in.alertmeu.R;
import in.alertmeu.adapter.MyPlcaesListAdpter;
import in.alertmeu.jsonparser.JsonHelper;
import in.alertmeu.models.MyPlaceModeDAO;
import in.alertmeu.utils.AppStatus;
import in.alertmeu.utils.Config;
import in.alertmeu.utils.Constant;
import in.alertmeu.utils.Listener;
import in.alertmeu.utils.WebClient;

public class SettingsActivity extends AppCompatActivity {
    SharedPreferences preferences;
    SharedPreferences.Editor prefEditor;
    ImageView addNewLocation, notifyOn, notifyOff;
    RecyclerView myPlaceList;
    JSONObject jsonLeadObj;
    JSONArray jsonArray;
    String myPlaceListResponse = "";
    List<MyPlaceModeDAO> data;
    MyPlcaesListAdpter myPlcaesListAdpter;
    RadioButton rb;
    LinearLayout showhide;
    ProgressDialog mProgressDialog;
    CheckBox chktoday, chktomorrow, chkoneweek, chktwoweek;
    ToggleButton onoffTongleButton;
    TextView ntext;
    Resources res;
    private static final String FILE_NAME = "file_lang";
    private static final String KEY_LANG = "key_lang";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadLanguage();
        res = getResources();
        setContentView(R.layout.activity_settings);
        preferences = getSharedPreferences("Prefrence", MODE_PRIVATE);
        prefEditor = preferences.edit();
        addNewLocation = (ImageView) findViewById(R.id.addNewLocation);
        notifyOn = (ImageView) findViewById(R.id.notifyOn);
        notifyOff = (ImageView) findViewById(R.id.notifyOff);
        myPlaceList = (RecyclerView) findViewById(R.id.myPlaceList);
        showhide = (LinearLayout) findViewById(R.id.showhide);
        rb = (RadioButton) findViewById(R.id.rbutton);
        chktoday = (CheckBox) findViewById(R.id.chktoday);
        chktomorrow = (CheckBox) findViewById(R.id.chktomorrow);
        chkoneweek = (CheckBox) findViewById(R.id.chkoneweek);
        chktwoweek = (CheckBox) findViewById(R.id.chktwoweek);
        onoffTongleButton = (ToggleButton) findViewById(R.id.onoffTongleButton);
        ntext = (TextView) findViewById(R.id.ntext);
        if (preferences.getString("notifyonoff", "").equals("1")) {
            ntext.setText(res.getString(R.string.jnotify));
            onoffTongleButton.setChecked(true);
            onoffTongleButton.setText(res.getString(R.string.jon));
            notifyOff.setVisibility(View.GONE);
            notifyOn.setVisibility(View.VISIBLE);

        } else {
            ntext.setText(res.getString(R.string.jnotify));
            onoffTongleButton.setChecked(false);
            onoffTongleButton.setText(res.getString(R.string.joff));
            notifyOn.setVisibility(View.GONE);
            notifyOff.setVisibility(View.VISIBLE);
        }
        onoffTongleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onoffTongleButton.isChecked()) {
                    prefEditor.putString("notifyonoff", "1");
                    prefEditor.commit();
                    ntext.setText(res.getString(R.string.jnotify));
                    notifyOff.setVisibility(View.GONE);
                    notifyOn.setVisibility(View.VISIBLE);
                    onoffTongleButton.setTextOn(res.getString(R.string.jon));

                    //  Toast.makeText(getApplicationContext(),""+onoffTongleButton.isChecked(),Toast.LENGTH_SHORT).show();
                } else {
                    prefEditor.putString("notifyonoff", "0");
                    prefEditor.commit();
                    ntext.setText(res.getString(R.string.jnotify));
                    notifyOn.setVisibility(View.GONE);
                    notifyOff.setVisibility(View.VISIBLE);
                    onoffTongleButton.setTextOff(res.getString(R.string.joff));
                    //  Toast.makeText(getApplicationContext(),""+onoffTongleButton.isChecked(),Toast.LENGTH_SHORT).show();

                }
            }
        });
        if (preferences.getString("today", "").equals("true")) {
            chktoday.setChecked(true);
        }
        if (preferences.getString("tomorrow", "").equals("true")) {
            chktomorrow.setChecked(true);
        }
        if (preferences.getString("oneweek", "").equals("true")) {
            chkoneweek.setChecked(true);
        }
        if (preferences.getString("twoweeks", "").equals("true")) {
            chktwoweek.setChecked(true);
        }
        //First CheckBox
        chktoday.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (chktoday.isChecked()) {
                    prefEditor.putString("today", "true");
                    prefEditor.commit();

                } else {
                    prefEditor.remove("today");
                    prefEditor.commit();

                }

            }
        });

        //Second CheckBox
        chktomorrow.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (chktomorrow.isChecked()) {
                    prefEditor.putString("tomorrow", "true");
                    prefEditor.commit();

                } else {
                    prefEditor.remove("tomorrow");
                    prefEditor.commit();

                }

            }
        });

        //Third CheckBox

        chkoneweek.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (chkoneweek.isChecked()) {
                    prefEditor.putString("oneweek", "true");
                    prefEditor.commit();

                } else {
                    prefEditor.remove("oneweek");
                    prefEditor.commit();

                }

            }
        });

        //Fourth CheckBox

        chktwoweek.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (chktwoweek.isChecked()) {
                    prefEditor.putString("twoweeks", "true");
                    prefEditor.commit();

                } else {
                    prefEditor.remove("twoweeks");
                    prefEditor.commit();

                }

            }
        });


        data = new ArrayList<>();
        if (preferences.getString("favloc", "").equals("0")) {
            rb.setChecked(true);
        } else {

        }
        rb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean checked = ((RadioButton) v).isChecked();
                // Check which radiobutton was pressed
                if (checked) {
                    // Do your coding
                    prefEditor.putString("favloc", "0");
                    prefEditor.commit();
                    // Toast.makeText(getApplicationContext(), "true", Toast.LENGTH_SHORT).show();
                    if (data.size() > 0) {
                        ((MyPlcaesListAdpter) myPlaceList.getAdapter()).notifyDataSetChanged();
                    }
                    Intent intent = new Intent(getApplicationContext(), HomePageActivity.class);
                    startActivity(intent);
                }
            }
        });
        addNewLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingsActivity.this, SerachAddressOnMapActivity.class);
                startActivity(intent);
            }
        });

        if (AppStatus.getInstance(getApplicationContext()).isOnline()) {
            new getMyPlaceList().execute();
        } else {

            Toast.makeText(getApplicationContext(), res.getString(R.string.jpcnc), Toast.LENGTH_SHORT).show();
        }
        MyPlcaesListAdpter.bindListener(new Listener() {
            @Override
            public void messageReceived(String messageText) {
                if (preferences.getString("favloc", "").equals("0")) {
                    rb.setChecked(true);

                } else {
                    rb.setChecked(false);
                }
                Intent intent = new Intent(getApplicationContext(), HomePageActivity.class);
                startActivity(intent);
            }
        });
    }

    private class getMyPlaceList extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Create a progressdialog
            mProgressDialog = new ProgressDialog(SettingsActivity.this);
            // Set progressdialog title
            mProgressDialog.setTitle(res.getString(R.string.jpw));
            // Set progressdialog message
            mProgressDialog.setMessage(res.getString(R.string.jsql));
            //mProgressDialog.setIndeterminate(false);
            // Show progressdialog
            mProgressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {

            jsonLeadObj = new JSONObject() {
                {
                    try {
                        put("user_id", preferences.getString("user_id", ""));
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e("json exception", "json exception" + e);
                    }
                }
            };
            WebClient serviceAccess = new WebClient();

            Log.i("json", "json" + jsonLeadObj);
            myPlaceListResponse = serviceAccess.SendHttpPost(Config.URL_GETALLMYPLACES, jsonLeadObj);
            Log.i("resp", "myPlaceListResponse" + myPlaceListResponse);
            if (myPlaceListResponse.compareTo("") != 0) {
                if (isJSONValid(myPlaceListResponse)) {

                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {

                            try {


                                JsonHelper jsonHelper = new JsonHelper();
                                data = jsonHelper.parseMyPlaceList(myPlaceListResponse);
                                jsonArray = new JSONArray(myPlaceListResponse);

                            } catch (JSONException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                        }
                    });

                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplication(), res.getString(R.string.jpcnc), Toast.LENGTH_LONG).show();
                        }
                    });

                    return null;
                }
            } else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplication(), res.getString(R.string.jpcnc), Toast.LENGTH_LONG).show();
                    }
                });

                return null;
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void args) {
            if (data.size() > 0) {
                showhide.setVisibility(View.VISIBLE);
                myPlcaesListAdpter = new MyPlcaesListAdpter(getApplication(), data);
                myPlaceList.setAdapter(myPlcaesListAdpter);
                myPlaceList.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                myPlcaesListAdpter.notifyDataSetChanged();
                myPlcaesListAdpter.notifyDataSetChanged();
                myPlaceList.setHasFixedSize(true);
                setUpItemTouchHelper();
                setUpAnimationDecoratorHelper();
                mProgressDialog.dismiss();
            } else {
                showhide.setVisibility(View.VISIBLE);
                // Close the progressdialog
                // Toast.makeText(getApplication(), "No Favourite location.", Toast.LENGTH_LONG).show();
                mProgressDialog.dismiss();

            }
        }
    }

    protected boolean isJSONValid(String callReoprtResponse2) {
        // TODO Auto-generated method stub
        try {
            new JSONObject(callReoprtResponse2);
        } catch (JSONException ex) {
            // edited, to include @Arthur's comment
            // e.g. in case JSONArray is valid as well...
            try {
                new JSONArray(callReoprtResponse2);
            } catch (JSONException ex1) {
                return false;
            }
        }
        return true;
    }

    private void setUpItemTouchHelper() {

        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {

            // we want to cache these and not allocate anything repeatedly in the onChildDraw method
            Drawable background;
            Drawable xMark;
            int xMarkMargin;
            boolean initiated;

            private void init() {
                background = new ColorDrawable(Color.parseColor("#E6E6DC"));
                xMark = ContextCompat.getDrawable(SettingsActivity.this, R.drawable.ic_delete_black_24dp);
                xMark.setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_ATOP);
                xMarkMargin = (int) SettingsActivity.this.getResources().getDimension(R.dimen.ic_clear_margin);
                initiated = true;
            }

            // not important, we don't want drag & drop
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public int getSwipeDirs(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                int position = viewHolder.getAdapterPosition();
                MyPlcaesListAdpter testAdapter = (MyPlcaesListAdpter) recyclerView.getAdapter();
                if (testAdapter.isUndoOn() && testAdapter.isPendingRemoval(position)) {
                    return 0;
                }
                return super.getSwipeDirs(recyclerView, viewHolder);
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                int swipedPosition = viewHolder.getAdapterPosition();
                MyPlcaesListAdpter adapter = (MyPlcaesListAdpter) myPlaceList.getAdapter();
                boolean undoOn = adapter.isUndoOn();
                if (undoOn) {
                    adapter.pendingRemoval(swipedPosition);
                } else {
                    adapter.remove(swipedPosition);
                }
            }

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                View itemView = viewHolder.itemView;

                // not sure why, but this method get's called for viewholder that are already swiped away
                if (viewHolder.getAdapterPosition() == -1) {
                    // not interested in those
                    return;
                }

                if (!initiated) {
                    init();
                }

                // draw red background
                background.setBounds(itemView.getRight() + (int) dX, itemView.getTop(), itemView.getRight(), itemView.getBottom());
                background.draw(c);

                // draw x mark
                int itemHeight = itemView.getBottom() - itemView.getTop();
                int intrinsicWidth = xMark.getIntrinsicWidth();
                int intrinsicHeight = xMark.getIntrinsicWidth();

                int xMarkLeft = itemView.getRight() - xMarkMargin - intrinsicWidth;
                int xMarkRight = itemView.getRight() - xMarkMargin;
                int xMarkTop = itemView.getTop() + (itemHeight - intrinsicHeight) / 2;
                int xMarkBottom = xMarkTop + intrinsicHeight;
                xMark.setBounds(xMarkLeft, xMarkTop, xMarkRight, xMarkBottom);

                xMark.draw(c);

                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }

        };
        ItemTouchHelper mItemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        mItemTouchHelper.attachToRecyclerView(myPlaceList);
    }

    private void setUpAnimationDecoratorHelper() {
        myPlaceList.addItemDecoration(new RecyclerView.ItemDecoration() {

            // we want to cache this and not allocate anything repeatedly in the onDraw method
            Drawable background;
            boolean initiated;

            private void init() {
                background = new ColorDrawable(Color.RED);
                initiated = true;
            }

            @Override
            public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {

                if (!initiated) {
                    init();
                }

                // only if animation is in progress
                if (parent.getItemAnimator().isRunning()) {

                    // some items might be animating down and some items might be animating up to close the gap left by the removed item
                    // this is not exclusive, both movement can be happening at the same time
                    // to reproduce this leave just enough items so the first one and the last one would be just a little off screen
                    // then remove one from the middle

                    // find first child with translationY > 0
                    // and last one with translationY < 0
                    // we're after a rect that is not covered in recycler-view views at this point in time
                    View lastViewComingDown = null;
                    View firstViewComingUp = null;

                    // this is fixed
                    int left = 0;
                    int right = parent.getWidth();

                    // this we need to find out
                    int top = 0;
                    int bottom = 0;

                    // find relevant translating views
                    int childCount = parent.getLayoutManager().getChildCount();
                    for (int i = 0; i < childCount; i++) {
                        View child = parent.getLayoutManager().getChildAt(i);
                        if (child.getTranslationY() < 0) {
                            // view is coming down
                            lastViewComingDown = child;
                        } else if (child.getTranslationY() > 0) {
                            // view is coming up
                            if (firstViewComingUp == null) {
                                firstViewComingUp = child;
                            }
                        }
                    }

                    if (lastViewComingDown != null && firstViewComingUp != null) {
                        // views are coming down AND going up to fill the void
                        top = lastViewComingDown.getBottom() + (int) lastViewComingDown.getTranslationY();
                        bottom = firstViewComingUp.getTop() + (int) firstViewComingUp.getTranslationY();
                    } else if (lastViewComingDown != null) {
                        // views are going down to fill the void
                        top = lastViewComingDown.getBottom() + (int) lastViewComingDown.getTranslationY();
                        bottom = lastViewComingDown.getBottom();
                    } else if (firstViewComingUp != null) {
                        // views are coming up to fill the void
                        top = firstViewComingUp.getTop();
                        bottom = firstViewComingUp.getTop() + (int) firstViewComingUp.getTranslationY();
                    }

                    background.setBounds(left, top, right, bottom);
                    background.draw(c);

                }
                super.onDraw(c, parent, state);
            }

        });
    }
    private void loadLanguage() {
        Locale locale = new Locale(getLangCode());
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getResources().updateConfiguration(config, getResources().getDisplayMetrics());
    }

    private String getLangCode() {
        SharedPreferences preferences = getSharedPreferences(FILE_NAME, MODE_PRIVATE);
        String langCode = preferences.getString(KEY_LANG, "en");
        return langCode;
    }
    //
    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        super.onBackPressed();
        finish();
        Intent setIntent = new Intent(SettingsActivity.this, HomePageActivity.class);
        startActivity(setIntent);
    }
}
