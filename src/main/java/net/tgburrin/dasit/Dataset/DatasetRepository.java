package net.tgburrin.dasit.Dataset;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface DatasetRepository extends CrudRepository<Dataset, Long> {
	@Query("select id, name, owner_group, status from dasit.datasets where name=:name")
	Dataset findByName(@Param("name") String name);
}
