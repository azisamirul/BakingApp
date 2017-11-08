
package com.azisamirul.bakingapp.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
///This class serialized by : http://www.jsonschema2pojo.org/
public class Step implements Serializable, Parcelable
{

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("shortDescription")
    @Expose
    private String shortDescription;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("videoURL")
    @Expose
    private String videoURL;
    @SerializedName("thumbnailURL")
    @Expose
    private String thumbnailURL;
    public final static Creator<Step> CREATOR = new Creator<Step>() {


        @SuppressWarnings({
            "unchecked"
        })
        public Step createFromParcel(Parcel in) {
            Step instance = new Step();
            instance.id = ((Integer) in.readValue((Integer.class.getClassLoader())));
            instance.shortDescription = ((String) in.readValue((String.class.getClassLoader())));
            instance.description = ((String) in.readValue((String.class.getClassLoader())));
            instance.videoURL = ((String) in.readValue((String.class.getClassLoader())));
            instance.thumbnailURL = ((String) in.readValue((String.class.getClassLoader())));
            return instance;
        }

        public Step[] newArray(int size) {
            return (new Step[size]);
        }

    }
    ;
    private final static long serialVersionUID = -4015607819172208896L;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getVideoURL() {
        return videoURL;
    }

    public void setVideoURL(String videoURL) {
        this.videoURL = videoURL;
    }

    public String getThumbnailURL() {
        return thumbnailURL;
    }

    public void setThumbnailURL(String thumbnailURL) {
        this.thumbnailURL = thumbnailURL;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(id);
        dest.writeValue(shortDescription);
        dest.writeValue(description);
        dest.writeValue(videoURL);
        dest.writeValue(thumbnailURL);
    }

    public int describeContents() {
        return  0;
    }

}
