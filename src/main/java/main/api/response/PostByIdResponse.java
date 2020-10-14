package main.api.response;

import java.util.ArrayList;

public class PostByIdResponse {
    private int id;
    private long timestamp;
    private boolean active;
    private UserPostResponse user;
    private String title;
    private String text;
    private int likeCount;
    private int dislikeCount;
    private int viewCount;
    private ArrayList<CommentPostResponse> comments;
    private ArrayList<String> tags;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public UserPostResponse getUser() {
        return user;
    }

    public void setUser(UserPostResponse user) {
        this.user = user;
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

    public int getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }

    public int getDislikeCount() {
        return dislikeCount;
    }

    public void setDislikeCount(int dislikeCount) {
        this.dislikeCount = dislikeCount;
    }

    public int getViewCount() {
        return viewCount;
    }

    public void setViewCount(int viewCount) {
        this.viewCount = viewCount;
    }

    public ArrayList<CommentPostResponse> getComments() {
        return comments;
    }

    public void setComments(ArrayList<CommentPostResponse> comments) {
        this.comments = comments;
    }

    public ArrayList<String> getTags() {
        return tags;
    }

    public void setTags(ArrayList<String> tags) {
        this.tags = tags;
    }
}


//{
// "id": 34,
// "timestamp": 1592338706,
// "active": true,
// "user":
//   {
//   	"id": 88,
//   	"name": "Дмитрий Петров"
//   },
// "title": "Заголовок поста",
// "text": "Текст поста в формате HTML",
// "likeCount": 36,
// "dislikeCount": 3,
// "viewCount": 55,
// "comments": [
//   {
//     "id": 776,
//     "timestamp": 1592338706,
//     "text": "Текст комментария в формате HTML",
//     "user":
//       {
//         "id": 88,
//         "name": "Дмитрий Петров",
//         "photo": "/avatars/ab/cd/ef/52461.jpg"
//       }
//   },
//   {...}
// ],
// "tags": ["Статьи", "Java"]
//}