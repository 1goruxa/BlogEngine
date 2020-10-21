package main.Repo;
import main.model.Post;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Integer>, QuerydslPredicateExecutor<Post>, PagingAndSortingRepository<Post, Integer> {

    @Query(value = "SELECT  *, COUNT(post_id) AS count FROM posts JOIN post_votes ON post_votes.post_id = posts.id  WHERE is_active=\"1\" AND moderation_status=\"ACCEPTED\" AND posts.time < NOW() AND value = \"1\" GROUP BY (post_id)", nativeQuery = true)
    List<Post> getBestPosts(PageRequest pageRequest);

    @Query(value = "SELECT  *, COUNT(post_id) AS count FROM posts JOIN post_comments ON post_comments.post_id = posts.id  WHERE is_active=\"1\" AND moderation_status=\"ACCEPTED\" AND posts.time < NOW() GROUP BY (post_id)", nativeQuery = true)
    List<Post> getDiscussedPosts(PageRequest pageRequest);

    @Query(value = "SELECT * FROM posts JOIN tag2post ON posts.id=tag2post.post_id JOIN tags ON tag2post.tagid=tags.id WHERE tags.text = :tag #pageRequest", nativeQuery = true)
    List<Post> getPostsByTag(String tag, PageRequest pageRequest);

    @Query(value = "SELECT DISTINCT year(time) FROM posts ", nativeQuery = true)
    List<Integer> getUniqueYearsOfPosts();

    @Query(value = "select * from posts where year(time) = :year", nativeQuery = true)
    List<Post> getPostForYear(int year);
}