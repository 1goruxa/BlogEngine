package main.model;

import javax.persistence.*;
import java.util.Set;

@Entity(name = "tags")
public class Tag {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @OneToMany(targetEntity = Tag2Post.class, mappedBy = "tag")
    private Set<Tag2Post> tag2PostSet;

    private String text;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Set<Tag2Post> getTag2PostSet() {
        return tag2PostSet;
    }

    public void setTag2PostSet(Set<Tag2Post> tag2PostSet) {
        this.tag2PostSet = tag2PostSet;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
