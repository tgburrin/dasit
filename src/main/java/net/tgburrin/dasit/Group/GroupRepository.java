package net.tgburrin.dasit.Group;

import java.util.List;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface GroupRepository extends CrudRepository<Group, Long> {
	@Query("select id, name, email, status from dasit.groups where email=:email")
	List<Group> findByEmailAddress(@Param("email") String email);

	@Query("select id, name, email, status from dasit.groups where name=:name")
	Group findByName(@Param("name") String email);

	@Query("select * from dasit.groups where status='ACTIVE'")
	List<Group> findAllActive();
}
