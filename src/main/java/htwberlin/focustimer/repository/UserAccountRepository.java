package htwberlin.focustimer.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import htwberlin.focustimer.entity.UserAccount;
import java.util.Optional;


@Repository
public interface UserAccountRepository extends CrudRepository<UserAccount, Long> {

    Optional<UserAccount> findByEmail(String email);
    
}
