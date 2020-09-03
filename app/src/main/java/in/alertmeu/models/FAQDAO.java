package in.alertmeu.models;

public class FAQDAO {
    String id="";
    String title="";
    String description = "";
    String title_hindi="";
    String description_hindi = "";
    String numbers = "";
    private boolean isSelected;

    public FAQDAO() {

    }

    public FAQDAO(String id, String title, String description, String numbers, boolean isSelected) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.numbers = numbers;
        this.isSelected = isSelected;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getNumbers() {
        return numbers;
    }

    public void setNumbers(String numbers) {
        this.numbers = numbers;
    }

    public String getTitle_hindi() {
        return title_hindi;
    }

    public void setTitle_hindi(String title_hindi) {
        this.title_hindi = title_hindi;
    }

    public String getDescription_hindi() {
        return description_hindi;
    }

    public void setDescription_hindi(String description_hindi) {
        this.description_hindi = description_hindi;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
