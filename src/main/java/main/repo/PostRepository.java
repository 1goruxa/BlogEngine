package main.repo;
import main.model.Post;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Integer>{

    //Этот запрос вернет только посты с лайками, если лайков нет, то и постов нет
    //@Query(value = "SELECT  *, COUNT(post_id) AS count FROM posts JOIN post_votes ON post_votes.post_id = posts.id  WHERE posts.time < :date AND is_active=\"1\" AND moderation_status=\"ACCEPTED\" AND posts.time < NOW() AND value = \"1\" GROUP BY (post_id) ORDER BY count DESC", nativeQuery = true)
    @Query(value = "SELECT *, (select IFNULL(SUM(post_votes.value),0) FROM post_votes WHERE post_id = posts.id) AS sum FROM posts WHERE posts.time < NOW() AND posts.is_active=\"1\" AND posts.moderation_status=\"ACCEPTED\" order by (sum) desc", nativeQuery = true)
    List<Post> getBestPosts(Pageable pageable, Date date);

    //Этот запрос выводит только посты с комментами
    //@Query(value = "SELECT  *, COUNT(post_id) AS count FROM posts JOIN post_comments ON post_comments.post_id = posts.id  WHERE posts.time < :date AND is_active=\"1\" AND moderation_status=\"ACCEPTED\" AND posts.time < NOW() GROUP BY (post_id) ORDER BY count DESC", nativeQuery = true)
    @Query(value="SELECT *, (select COUNT(post_comments.post_id) FROM post_comments WHERE post_id = posts.id) AS count FROM posts WHERE posts.time < NOW() AND is_active=\"1\" AND moderation_status=\"ACCEPTED\" order by (count) desc", nativeQuery = true)
    List<Post> getDiscussedPosts(Pageable pageable, Date date);

    @Query(value="SELECT COUNT(DISTINCT post_id) AS count FROM posts JOIN post_votes ON post_votes.post_id = posts.id  WHERE is_active=\"1\" AND moderation_status=\"ACCEPTED\" AND posts.time < NOW() AND value = \"1\"", nativeQuery = true)
    Integer counter4Best();

    @Query(value="SELECT  COUNT(DISTINCT post_id) AS count FROM posts JOIN post_comments ON post_comments.post_id = posts.id  WHERE is_active=\"1\" AND moderation_status=\"ACCEPTED\" AND posts.time < NOW()", nativeQuery = true)
    Integer counter4Discussed();

    @Query(value= "SELECT * FROM posts WHERE time < :date AND is_active=\"1\" AND moderation_status=\"ACCEPTED\" ORDER BY time DESC ", nativeQuery = true)
    List<Post> getRecentPosts(Pageable pageable, Date date);

    @Query(value= "SELECT * FROM posts WHERE time < :date AND is_active=\"1\" AND moderation_status=\"ACCEPTED\" ORDER BY time", nativeQuery = true)
    List<Post> getEarlyPosts(Pageable pageable, Date date);

    @Query(value = "SELECT * FROM posts JOIN tag2post ON posts.id=tag2post.post_id JOIN tags ON tag2post.tagid=tags.id WHERE tags.text = :tag", nativeQuery = true)
    List<Post> getPostsByTag(String tag);

    @Query(value = "SELECT DISTINCT year(time) FROM posts ", nativeQuery = true)
    List<Integer> getUniqueYearsOfPosts();

    @Query(value = "select * from posts where year(time) = :year AND is_active=\"1\" AND moderation_status=\"ACCEPTED\"", nativeQuery = true)
    List<Post> getPostForYear(int year);

    @Query(value="select * from posts where DATE(time) = :date AND is_active=\"1\" AND moderation_status=\"ACCEPTED\"", nativeQuery = true)
    List<Post> getPostsByDate(String date);

    @Query(value = "select COUNT(id) from posts WHERE user_id = :id", nativeQuery = true)
    int getPostsCount4MyStat(int id);

    @Query(value = "select sum(view_count) from posts WHERE user_id = :id", nativeQuery = true)
    int getViewsCounter4MyStat(int id);

    @Query(value = "select min(time) from posts WHERE user_id = :id", nativeQuery = true)
    Date getFirstPublication4MylStat(int id);

    @Query(value="select COUNT(id) from posts WHERE is_active = 1 AND moderation_status = 'ACCEPTED'", nativeQuery = true)
    int getPostsCount4AllStat();

    @Query(value = "select sum(view_count) from posts WHERE is_active = 1 AND moderation_status = 'ACCEPTED'", nativeQuery = true)
    int getViewsCounter4AllStat();

    @Query(value = "select min(time) from posts WHERE is_active = 1 AND moderation_status = 'ACCEPTED'", nativeQuery = true)
    Date getFirstPublication4AllStat();

    @Query(value="SELECT * FROM posts WHERE posts.text LIKE %:query% OR title LIKE %:query%", nativeQuery = true)
    List<Post> getPostsByQuery(String query);

    @Query(value = "select COUNT(*) from tag2post JOIN posts ON tag2post.tagid = posts.id WHERE posts.is_active = :isActive AND posts.moderation_status = :moderationStatus AND posts.time < :time", nativeQuery = true)
    int countAllByIsActiveAndModerationStatusAndTimeLessThan(int isActive, String moderationStatus,Date time);

    int countAllByModerationStatus(String moderationStatus);

    @Query(value="SELECT * FROM posts WHERE user_id = :user AND is_active = 0", nativeQuery = true)
    List<Post> getMyInactivePosts(Pageable pageable, int user);

    @Query(value = "select COUNT(*) from posts WHERE user_id = :user AND is_active = 0", nativeQuery = true)
    int countMyInactivePosts(int user);

    @Query(value="SELECT * FROM posts WHERE user_id = :user AND is_active = 1 AND moderation_status = 'NEW'", nativeQuery = true)
    List<Post> getMyPendingPosts(Pageable pageable, int user);

    @Query(value="SELECT COUNT(*) FROM posts WHERE user_id = :user AND is_active = 1 AND moderation_status = 'NEW'", nativeQuery = true)
    int countMyPendingPosts(int user);

    @Query(value="SELECT * FROM posts WHERE user_id = :user AND is_active = 1 AND moderation_status = 'DECLINED'", nativeQuery = true)
    List<Post> getMyDeclinedPosts(Pageable pageable, int user);

    @Query(value="SELECT COUNT(*) FROM posts WHERE user_id = :user AND is_active = 1 AND moderation_status = 'DECLINED'", nativeQuery = true)
    int countMyDeclinedPosts(int user);

    @Query(value="SELECT * FROM posts WHERE user_id = :user AND is_active = 1 AND moderation_status = 'ACCEPTED' ORDER BY time DESC", nativeQuery = true)
    List<Post> getMyPublishedPosts(Pageable pageable, int user);

    @Query(value="SELECT COUNT(*) FROM posts WHERE user_id = :user AND is_active = 1 AND moderation_status = 'ACCEPTED'", nativeQuery = true)
    int countMyPublishedPosts(int user);

    @Query(value="SELECT * FROM posts WHERE moderation_status = 'NEW' ", nativeQuery = true)
    List<Post> getNewPostsForModeration(Pageable pageable);

    @Query(value="SELECT * FROM posts WHERE moderation_status = 'DECLINED'", nativeQuery = true)
    List<Post> getDeclinedPostsForModeration(Pageable pageable);

    @Query(value="SELECT * FROM posts WHERE moderation_status = 'ACCEPTED'", nativeQuery = true)
    List<Post> getAcceptedPostsForModeration(Pageable pageable);

    @Query(value="SELECT COUNT(*) FROM posts WHERE moderation_status = 'NEW'", nativeQuery = true)
    int countNewPostsForModeration();

    @Query(value="SELECT COUNT(*) FROM posts WHERE moderation_status = 'DECLINED'", nativeQuery = true)
    int countDeclinedPostsForModeration();

    @Query(value="SELECT COUNT(*) FROM posts WHERE moderation_status = 'ACCEPTED'", nativeQuery = true)
    int countAcceptedPostsForModeration();

    @Modifying
    @Query(value = "UPDATE posts SET moderation_status = 'ACCEPTED', moderator_id = :userId WHERE id = :postId",nativeQuery = true)
    void acceptPost(int postId, int userId);

    @Modifying
    @Query(value = "UPDATE posts SET moderation_status = 'DECLINED', moderator_id = :userId WHERE id = :postId", nativeQuery = true)
    void declinePost(int postId, int userId);

}