package main.model;

import javax.persistence.*;
import java.util.List;


@Entity(name = "tags")
public class Tag {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @OneToMany(targetEntity = Tag2Post.class, mappedBy = "tag")
    private List<Tag2Post> tag2PostList;

    private String text;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<Tag2Post> getTag2PostList() {
        return tag2PostList;
    }

    public void setTag2PostList(List<Tag2Post> tag2PostList) {
        this.tag2PostList = tag2PostList;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
