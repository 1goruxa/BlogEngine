package main.Repo;

import main.model.Comment;
import main.model.Tag2Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface Tag2PostRepository extends JpaRepository<Tag2Post, Integer>, QuerydslPredicateExecutor<Tag2Post> {


}