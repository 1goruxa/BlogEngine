package main.api.response;

public class CommentPostResponse {
    private int id;
    private long timestamp;
    private String text;
    private UserCommentResponse user;

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

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public UserCommentResponse getUser() {
        return user;
    }

    public void setUser(UserCommentResponse user) {
        this.user = user;
    }
}

//"id": 776,
//     "timestamp": 1592338706,
//     "text": "Текст комментария в формате HTML",
//     "user":
//       {
//         "id": 88,
//         "name": "Дмитрий Петров",
//         "photo": "/avatars/ab/cd/ef/52461.jpg"