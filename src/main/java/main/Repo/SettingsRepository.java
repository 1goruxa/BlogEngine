package main.Repo;

import main.api.response.SettingsResponse;
import main.model.GlobalSettings;
import main.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface SettingsRepository extends JpaRepository<GlobalSettings, Integer> {

    @Modifying
    @Query(value="TRUNCATE TABLE global_settings", nativeQuery = true)
    void dropSettings();

    @Modifying
    @Query(value="INSERT INTO global_settings (code, name, value) values (\"1\", \"MULTIUSER_MODE\", :multiUser)", nativeQuery = true)
    void setMultiUser(boolean multiUser);

    GlobalSettings findOneByName(String settings);

    @Modifying
    @Query(value="INSERT INTO global_settings (code, name, value) values (\"2\", \"POST_PREMODERATION\", :postModer)", nativeQuery = true)
    void setPostModer(boolean postModer);

    @Modifying
    @Query(value="INSERT INTO global_settings (code, name, value) values (\"3\", \"STATISTICS_IS_PUBLIC\", :stats)", nativeQuery = true)
    void setStats(boolean stats);

    @Query(value="SELECT value FROM global_settings WHERE name='MULTIUSER_MODE' ", nativeQuery = true)
    boolean getMultiUser();

    @Query(value="SELECT value FROM global_settings WHERE name='STATISTICS_IS_PUBLIC' ", nativeQuery = true)
    boolean getStats();

    @Query(value="SELECT value FROM global_settings WHERE name='POST_PREMODERATION' ", nativeQuery = true)
    boolean getPostModer();
}
