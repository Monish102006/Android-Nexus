package com.androidnexus.model;

/**
 * Represents an Android notification posted on the device.
 *
 * Placed under the model layer to carry structured data parsed from
 * "dumpsys notification" command.
 */
public class Notification {

    private String key;
    private String packageName;
    private String title;
    private String text;
    private String timestamp;
    private String tag;
    private int id;
    private boolean ongoing;
    private boolean clearable;

    public Notification() {
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isOngoing() {
        return ongoing;
    }

    public void setOngoing(boolean ongoing) {
        this.ongoing = ongoing;
    }

    public boolean isClearable() {
        return clearable;
    }

    public void setClearable(boolean clearable) {
        this.clearable = clearable;
    }

    @Override
    public String toString() {
        return "Notification{" +
                "key='" + key + '\'' +
                ", packageName='" + packageName + '\'' +
                ", title='" + title + '\'' +
                ", text='" + text + '\'' +
                ", timestamp='" + timestamp + '\'' +
                ", tag='" + tag + '\'' +
                ", id=" + id +
                ", ongoing=" + ongoing +
                ", clearable=" + clearable +
                '}';
    }
}
