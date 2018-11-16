package uqac.inf872.projet.imok.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Stats {

    @SerializedName("appreciations")
    @Expose
    private Integer appreciations;

    @SerializedName("views")
    @Expose
    private Integer views;

    @SerializedName("comments")
    @Expose
    private Integer comments;

    // --- GETTERS ---

    public Integer getAppreciations() {
        return appreciations;
    }

    public void setAppreciations(Integer appreciations) {
        this.appreciations = appreciations;
    }

    public Integer getViews() {
        return views;
    }

    // --- SETTERS ---

    public void setViews(Integer views) {
        this.views = views;
    }

    public Integer getComments() {
        return comments;
    }

    public void setComments(Integer comments) {
        this.comments = comments;
    }
}
