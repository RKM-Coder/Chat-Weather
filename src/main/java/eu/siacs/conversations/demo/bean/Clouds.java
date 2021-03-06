package eu.siacs.conversations.demo.bean;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Murugeshwaran M on 05-03-2021.
 */
public class Clouds {
    @SerializedName("all")
    @Expose
    private Double all;

    public Double getAll() {
        return all;
    }

    public void setAll(Double all) {
        this.all = all;
    }
}
