package main.Repo;
import main.model.Tag2Post;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface Tag2PostRepository extends JpaRepository<Tag2Post, Integer> {

    @Query(value = "SELECT COUNT(post_id) FROM tag2post WHERE tagid = :id", nativeQuery = true)
    int countTagsById(int id);

    @Query(value ="SELECT * FROM tag2post WHERE post_id = :postId AND tagid = :tagId", nativeQuery = true)
    Optional<Tag2Post> findEqual(int postId, int tagId);

}