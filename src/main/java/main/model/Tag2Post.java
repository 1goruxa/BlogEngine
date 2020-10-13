package main.model;

import javax.persistence.*;

@Entity
public class Tag2Post {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @OneToOne
    @JoinColumn(name = "post_id", referencedColumnName = "id")
    private Post post;

    @OneToOne
    @JoinColumn(name="tag_id", referencedColumnName = "id")
    private Tag tag;


}
