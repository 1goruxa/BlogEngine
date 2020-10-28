package main.Repo;
import main.model.Post;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Integer>{

    @Query(value = "SELECT  *, COUNT(post_id) AS count FROM posts JOIN post_votes ON post_votes.post_id = posts.id  WHERE is_active=\"1\" AND moderation_status=\"ACCEPTED\" AND posts.time < NOW() AND value = \"1\" GROUP BY (post_id) ORDER BY count DESC", nativeQuery = true)
    List<Post> getBestPosts(Pageable pageable);

    @Query(value = "SELECT  *, COUNT(post_id) AS count FROM posts JOIN post_comments ON post_comments.post_id = posts.id  WHERE is_active=\"1\" AND moderation_status=\"ACCEPTED\" AND posts.time < NOW() GROUP BY (post_id) ORDER BY count DESC", nativeQuery = true)
    List<Post> getDiscussedPosts(Pageable pageable);

    @Query(value= "SELECT * FROM posts WHERE time < now() AND is_active=\"1\" AND moderation_status=\"ACCEPTED\" ORDER BY time DESC ", nativeQuery = true)
    List<Post> getRecentPosts(Pageable pageable);

    @Query(value= "SELECT * FROM posts WHERE time < now() AND is_active=\"1\" AND moderation_status=\"ACCEPTED\" ORDER BY time", nativeQuery = true)
    List<Post> getEarlyPosts(Pageable pageable);

    @Query(value = "SELECT * FROM posts JOIN tag2post ON posts.id=tag2post.post_id JOIN tags ON tag2post.tagid=tags.id WHERE tags.text = :tag", nativeQuery = true)
    List<Post> getPostsByTag(String tag);

    @Query(value = "SELECT DISTINCT year(time) FROM posts ", nativeQuery = true)
    List<Integer> getUniqueYearsOfPosts();

    @Query(value = "select * from posts where year(time) = :year", nativeQuery = true)
    List<Post> getPostForYear(int year);

    @Query(value="select * from posts where time = :date", nativeQuery = true)
    List<Post> getPostsByDate(String date);

    @Query(value="select COUNT(id) from posts", nativeQuery = true)
    int getPostsCount4AllStat();

    @Query(value = "select sum(view_count) from posts", nativeQuery = true)
    int getViewsCounter4AllStat();

    @Query(value = "select min(time) from posts", nativeQuery = true)
    Date getFirstPublication4AllStat();

    @Query(value="SELECT COUNT(DISTINCT post_id) AS count FROM posts JOIN post_votes ON post_votes.post_id = posts.id  WHERE is_active=\"1\" AND moderation_status=\"ACCEPTED\" AND posts.time < NOW() AND value = \"1\"", nativeQuery = true)
    Integer counter4Best();

    @Query(value="SELECT  COUNT(DISTINCT post_id) AS count FROM posts JOIN post_comments ON post_comments.post_id = posts.id  WHERE is_active=\"1\" AND moderation_status=\"ACCEPTED\" AND posts.time < NOW()", nativeQuery = true)
    Integer counter4Discussd();

    @Query(value="SELECT * FROM posts WHERE posts.text LIKE %:query% OR title LIKE %:query%", nativeQuery = true)
    List<Post> getPostsByQuery(String query);

    int countAllByIsActiveAndModerationStatusAndTimeLessThan(int isActive, String moderationStatus,Date time);

    int countAllByModerationStatus(String moderationStatus);

}