package main.repo;

import main.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    int countAllByEmail(String email);

    Optional<User> findOneByEmail(String email);

    Optional<User> findOneByName(String name);

    Optional<User> findOneByCode(String code);

}