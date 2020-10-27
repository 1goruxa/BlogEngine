package main.Repo;

import main.model.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;


@Repository
public interface TagRepository extends JpaRepository<Tag, Integer> {

    @Query(value = "SELECT * FROM tags WHERE text = :query", nativeQuery = true)
    List<Tag> findAllByQuery(String query);

    @Query(value = "SELECT * FROM tags", nativeQuery = true)
    List<Tag> findAll();

}