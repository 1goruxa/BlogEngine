package main.api.request;

import main.model.Tag;

import java.util.Date;
import java.util.List;

public class SavePostRequest {
    long timestamp;
    int active;
    String title;
    List<String> tags;
    String text;

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public int getActive() {
        return active;
    }

    public void setActive(int active) {
        this.active = active;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}


//{
//  "timestamp":1592338706,
//  "active":1,
//  "title":"заголовок",
//  "tags":["java","spring"],
//  "text":"Текст поста включащий <b>тэги форматирования</b>"
//}