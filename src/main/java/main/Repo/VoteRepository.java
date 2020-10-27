package main.Repo;


import main.model.Tag2Post;
import main.model.Vote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VoteRepository extends JpaRepository<Vote, Integer>{

    @Query(value="select COUNT(id) from post_votes where value= :vote", nativeQuery = true)
    int getVotesCount4AllStat(int vote);

}