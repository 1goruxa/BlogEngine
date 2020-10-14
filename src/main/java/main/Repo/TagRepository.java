package main.Repo;

import main.model.Post;
import main.model.Tag;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TagRepository extends CrudRepository<Tag, Integer>, QuerydslPredicateExecutor<Tag> {


}