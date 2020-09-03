package in.alertmeu.models;

public class YouTubeDAO {
    String id = "";
    String video_description = "";
    String video_description_hindi = "";
    String video_link = "";

    public YouTubeDAO() {
    }

    public YouTubeDAO(String id, String video_description, String video_link) {
        this.id = id;
        this.video_description = video_description;
        this.video_link = video_link;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getVideo_description() {
        return video_description;
    }

    public void setVideo_description(String video_description) {
        this.video_description = video_description;
    }

    public String getVideo_link() {
        return video_link;
    }

    public void setVideo_link(String video_link) {
        this.video_link = video_link;
    }

    public String getVideo_description_hindi() {
        return video_description_hindi;
    }

    public void setVideo_description_hindi(String video_description_hindi) {
        this.video_description_hindi = video_description_hindi;
    }
}
