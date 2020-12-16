package main.repo;


import main.model.Vote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VoteRepository extends JpaRepository<Vote, Integer>{

    @Query(value="select COUNT(id) from post_votes where value= :vote", nativeQuery = true)
    int getVotesCount4AllStat(int vote);

    @Query(value="SELECT * FROM post_votes WHERE post_id = :postId AND user_id = :userId AND value = :type", nativeQuery = true)
    Optional<Vote> findOneByIdAndType(int userId, int postId, String type);

    @Query(value="select COUNT(id) from post_votes where value= :vote AND user_id = :id", nativeQuery = true)
    int getVotesCount4MyStat(int vote, int id);


}