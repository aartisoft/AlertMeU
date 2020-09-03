package in.alertmeu.jsonparser;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import in.alertmeu.models.AdvertisementDAO;
import in.alertmeu.models.FAQDAO;
import in.alertmeu.models.ImageModel;
import in.alertmeu.models.LocationDAO;
import in.alertmeu.models.MainCatModeDAO;
import in.alertmeu.models.MyPlaceModeDAO;
import in.alertmeu.models.SubCatModeDAO;
import in.alertmeu.models.YouTubeDAO;


public class JsonHelper {

    private ArrayList<LocationDAO> locationDAOArrayList = new ArrayList<LocationDAO>();
    private LocationDAO locationDAO;

    private ArrayList<AdvertisementDAO> advertisementDAOArrayList = new ArrayList<AdvertisementDAO>();
    private AdvertisementDAO advertisementDAO;

    private ArrayList<ImageModel> imageModelArrayList = new ArrayList<ImageModel>();
    private ImageModel imageModel;

    private ArrayList<MyPlaceModeDAO> myPlaceModeDAOArrayList = new ArrayList<MyPlaceModeDAO>();
    private MyPlaceModeDAO myPlaceModeDAO;

    private ArrayList<MainCatModeDAO> mainCatModeDAOArrayList = new ArrayList<MainCatModeDAO>();
    private MainCatModeDAO mainCatModeDAO;

    private ArrayList<SubCatModeDAO> subCatModeDAOArrayList = new ArrayList<SubCatModeDAO>();
    private SubCatModeDAO subCatModeDAO;

    private ArrayList<FAQDAO> faqdaoArrayList = new ArrayList<FAQDAO>();
    private FAQDAO faqdao;

    private ArrayList<YouTubeDAO> youTubeDAOArrayList = new ArrayList<YouTubeDAO>();
    private YouTubeDAO youTubeDAO;

    //locationPaser
    public ArrayList<LocationDAO> parseLocationList(String rawLeadListResponse) {
        // TODO Auto-generated method stub
        Log.d("scheduleListResponse", rawLeadListResponse);
        try {
            JSONArray leadJsonObj = new JSONArray(rawLeadListResponse);

            for (int i = 0; i < leadJsonObj.length(); i++) {
                String sequence = String.format("%03d", i + 1);
                locationDAO = new LocationDAO();
                JSONObject json_data = leadJsonObj.getJSONObject(i);
                locationDAO.setId(json_data.getString("id"));
                locationDAO.setUser_id(json_data.getString("business_user_id"));
                locationDAO.setLatitude(json_data.getString("latitude"));
                locationDAO.setLongitude(json_data.getString("longitude"));
                locationDAO.setFlag_map(json_data.getString("title"));
                locationDAO.setPath(json_data.getString("original_image_path"));
                locationDAO.setNumbers("" + sequence);
                locationDAO.setDescription(json_data.getString("description"));
                locationDAO.setRq_code(json_data.getString("rq_code"));
                locationDAO.setTitle(json_data.getString("title"));
                locationDAO.setBusiness_name(json_data.getString("business_name"));
                locationDAO.setAddress(json_data.getString("address"));
                locationDAO.setLikecnt(json_data.getString("likecnt"));
                locationDAO.setDislikecnt(json_data.getString("dislikecnt"));
                locationDAO.setS_time(json_data.getString("s_time"));
                locationDAO.setE_date(json_data.getString("e_date"));
                locationDAO.setE_time(json_data.getString("e_time"));
                locationDAO.setS_date(json_data.getString("s_date"));
                locationDAO.setMobile_no(json_data.getString("mobile_no"));
                locationDAO.setDescribe_limitations(json_data.getString("describe_limitations"));
                locationDAO.setBusiness_main_category(json_data.getString("business_main_category"));
                locationDAO.setBusiness_subcategory(json_data.getString("business_subcategory"));
                if (json_data.getString("business_email").equals("")) {
                    locationDAO.setBusiness_email(" ");
                } else {
                    locationDAO.setBusiness_email(json_data.getString("business_email"));
                }
                if (json_data.getString("business_number").equals("")) {
                    locationDAO.setBusiness_number(" ");
                } else {
                    locationDAO.setBusiness_number(json_data.getString("business_number"));
                }
                locationDAOArrayList.add(locationDAO);
            }

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return locationDAOArrayList;
    }


    //advertisementPaser
    public ArrayList<AdvertisementDAO> parseAdvertisementList(String rawLeadListResponse) {
        // TODO Auto-generated method stub
        Log.d("scheduleListResponse", rawLeadListResponse);
        try {
            JSONArray leadJsonObj = new JSONArray(rawLeadListResponse);

            for (int i = 0; i < leadJsonObj.length(); i++) {
                advertisementDAO = new AdvertisementDAO();
                String sequence = String.format("%03d", i + 1);
                JSONObject json_data = leadJsonObj.getJSONObject(i);
                advertisementDAO.setId(json_data.getString("id"));
                advertisementDAO.setBusiness_user_id(json_data.getString("business_user_id"));
                advertisementDAO.setMobile_no(json_data.getString("mobile_no"));
                advertisementDAO.setBusiness_email(json_data.getString("business_email"));
                advertisementDAO.setLatitude(json_data.getString("latitude"));
                advertisementDAO.setLongitude(json_data.getString("longitude"));
                advertisementDAO.setAddress(json_data.getString("address"));
                advertisementDAO.setCompany_logo(json_data.getString("company_logo"));
                advertisementDAO.setTitle(json_data.getString("title"));
                advertisementDAO.setDescription(json_data.getString("description"));
                advertisementDAO.setBusiness_name(json_data.getString("business_name"));
                advertisementDAO.setBusiness_number(json_data.getString("business_number"));
                advertisementDAO.setBusiness_main_category_id(json_data.getString("business_main_category_id"));
                // advertisementDAO.setBusiness_subcategory_id(json_data.getString("business_subcategory_id"));
                advertisementDAO.setDescribe_limitations(json_data.getString("describe_limitations"));
                advertisementDAO.setRq_code(json_data.getString("rq_code"));
                advertisementDAO.setOriginal_image_path(json_data.getString("original_image_path"));
                advertisementDAO.setModify_image_path(json_data.getString("modify_image_path"));
                advertisementDAO.setLocation_name(json_data.getString("location_name"));
                advertisementDAO.setLikecnt(json_data.getString("likecnt"));
                advertisementDAO.setDislikecnt(json_data.getString("dislikecnt"));
                advertisementDAO.setS_time(json_data.getString("s_time"));
                advertisementDAO.setE_time(json_data.getString("e_time"));
                advertisementDAO.setS_date(json_data.getString("s_date"));
                advertisementDAO.setE_date(json_data.getString("e_date"));
                advertisementDAO.setMain_cat_name(json_data.getString("main_cat_name"));
                advertisementDAO.setSubcategory_name(json_data.getString("subcategory_name"));
                advertisementDAO.setNumbers("" + sequence);
                advertisementDAOArrayList.add(advertisementDAO);
            }

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return advertisementDAOArrayList;
    }

    //advertisementPaser
    public ArrayList<ImageModel> parseImagePathList(String ListResponse) {
        // TODO Auto-generated method stub
        Log.d("scheduleListResponse", ListResponse);
        try {
            JSONObject jsonObject = new JSONObject(ListResponse);

            if (!jsonObject.isNull("dataList")) {
                JSONArray jsonArray = jsonObject.getJSONArray("dataList");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject object = jsonArray.getJSONObject(i);
                    imageModel = new ImageModel();
                    imageModel.setImage_id(object.getString("id"));
                    imageModel.setImage_path(object.getString("image_path"));
                    imageModel.setImage_description(object.getString("image_description"));
                    imageModel.setImage_description_hindi(object.getString("image_description_hindi"));
                    imageModelArrayList.add(imageModel);
                }
            }

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return imageModelArrayList;
    }

    //advertisementPaser
    public ArrayList<MyPlaceModeDAO> parseMyPlaceList(String ListResponse) {
        // TODO Auto-generated method stub
        Log.d("scheduleListResponse", ListResponse);
        try {


            JSONArray leadJsonObj = new JSONArray(ListResponse);
            for (int i = 0; i < leadJsonObj.length(); i++) {
                JSONObject object = leadJsonObj.getJSONObject(i);
                myPlaceModeDAO = new MyPlaceModeDAO();
                myPlaceModeDAO.setId(object.getString("id"));
                myPlaceModeDAO.setUser_id(object.getString("user_id"));
                myPlaceModeDAO.setFull_address(object.getString("full_address"));
                myPlaceModeDAO.setLatitude(object.getString("latitude"));
                myPlaceModeDAO.setLongitude(object.getString("longitude"));
                myPlaceModeDAO.setTime_stamp(object.getString("time_stamp"));
                myPlaceModeDAOArrayList.add(myPlaceModeDAO);
            }


        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return myPlaceModeDAOArrayList;
    }

    //Main cat
    public ArrayList<MainCatModeDAO> parseMainCatList(String ListResponse) {
        // TODO Auto-generated method stub
        Log.d("scheduleListResponse", ListResponse);
        try {


            JSONArray leadJsonObj = new JSONArray(ListResponse);
            for (int i = 0; i < leadJsonObj.length(); i++) {
                JSONObject object = leadJsonObj.getJSONObject(i);
                mainCatModeDAO = new MainCatModeDAO();
                mainCatModeDAO.setId(object.getString("id"));
                mainCatModeDAO.setCategory_name(object.getString("category_name"));
                mainCatModeDAO.setCategory_name_hindi(object.getString("category_name_hindi"));
                mainCatModeDAO.setChecked_status(object.getString("checked_status"));
                mainCatModeDAOArrayList.add(mainCatModeDAO);
            }


        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return mainCatModeDAOArrayList;
    }

    //Sub cat
    public ArrayList<SubCatModeDAO> parseSubCatList(String ListResponse) {
        // TODO Auto-generated method stub
        Log.d("scheduleListResponse", ListResponse);
        try {


            JSONArray leadJsonObj = new JSONArray(ListResponse);
            for (int i = 0; i < leadJsonObj.length(); i++) {
                JSONObject object = leadJsonObj.getJSONObject(i);
                subCatModeDAO = new SubCatModeDAO();
                subCatModeDAO.setId(object.getString("id"));
                subCatModeDAO.setBc_id(object.getString("bc_id"));
                subCatModeDAO.setSubcategory_name(object.getString("subcategory_name"));
                subCatModeDAO.setSubcategory_name_hindi(object.getString("subcategory_name_hindi"));
                subCatModeDAO.setChecked_status(object.getString("checked_status"));
                subCatModeDAOArrayList.add(subCatModeDAO);
            }


        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return subCatModeDAOArrayList;
    }

    public ArrayList<FAQDAO> parseFAQList(String ListResponse) {
        // TODO Auto-generated method stub
        Log.d("scheduleListResponse", ListResponse);
        try {


            JSONArray leadJsonObj = new JSONArray(ListResponse);
            for (int i = 0; i < leadJsonObj.length(); i++) {
                JSONObject object = leadJsonObj.getJSONObject(i);
                faqdao = new FAQDAO();
                faqdao.setId(object.getString("id"));
                faqdao.setTitle(object.getString("title"));
                faqdao.setDescription(object.getString("description"));
                faqdao.setTitle_hindi(object.getString("title_hindi"));
                faqdao.setDescription_hindi(object.getString("description_hindi"));
                faqdaoArrayList.add(faqdao);
            }


        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return faqdaoArrayList;
    }

    //Main cat
    public ArrayList<MainCatModeDAO> parseNewMainCatList(String ListResponse) {
        // TODO Auto-generated method stub
        Log.d("scheduleListResponse", ListResponse);
        try {


            JSONArray leadJsonObj = new JSONArray(ListResponse);
            for (int i = 0; i < leadJsonObj.length(); i++) {
                JSONObject object = leadJsonObj.getJSONObject(i);
                mainCatModeDAO = new MainCatModeDAO();
                mainCatModeDAO.setId(object.getString("id"));
                mainCatModeDAO.setCategory_name(object.getString("category_name"));
                mainCatModeDAO.setCategory_name_hindi(object.getString("category_name_hindi"));
                mainCatModeDAOArrayList.add(mainCatModeDAO);
            }


        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return mainCatModeDAOArrayList;
    }

    public ArrayList<YouTubeDAO> parseYouTubeList(String ListResponse) {
        // TODO Auto-generated method stub
        Log.d("scheduleListResponse", ListResponse);
        try {


            JSONArray leadJsonObj = new JSONArray(ListResponse);
            for (int i = 0; i < leadJsonObj.length(); i++) {
                JSONObject object = leadJsonObj.getJSONObject(i);
                youTubeDAO = new YouTubeDAO();
                youTubeDAO.setId(object.getString("id"));
                youTubeDAO.setVideo_description(object.getString("video_description"));
                youTubeDAO.setVideo_description_hindi(object.getString("video_description_hindi"));
                youTubeDAO.setVideo_link(object.getString("video_link"));
                youTubeDAOArrayList.add(youTubeDAO);
            }


        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return youTubeDAOArrayList;
    }
}
