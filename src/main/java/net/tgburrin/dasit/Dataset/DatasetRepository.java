package net.tgburrin.dasit.Dataset;

import java.sql.Timestamp;
import java.util.List;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface DatasetRepository extends CrudRepository<Dataset, Long> {
	/* Dataset object calls */
	@Query("select * from dasit.datasets where status='ACTIVE'")
	List<Dataset> findAllActive();

	@Query("select id, name, owner_group, status from dasit.datasets where name=:name")
	Dataset findByName(@Param("name") String name);

	@Query("select id, name, owner_group, status from dasit.datasets where owner_group=:ownerId and status='A'")
	List<Dataset> findActiveByOwnerId(@Param("ownerId") Long ownerId);

	@Query("select id, name, owner_group, status from dasit.datasets where owner_group=:ownerId")
	List<Dataset> findByOwnerId(@Param("ownerId") Long ownerId);

	/* Dataset window calls */
	@Query("select p.dataset_id, p.publish_start_dt, publish_end_dt from dasit.datasets d join dasit.datasets_published p on d.id = p.dataset_id where d.name=:name order by p.publish_start_dt")
	List<DatasetWindow> findWindowsByName(@Param("name") String name);

	@Query("select p.dataset_id, p.publish_start_dt, publish_end_dt from dasit.check_published_window(:datasetId, :startDt, :endDt) p")
	DatasetWindow checkWindowExists(@Param("datasetId") Long datasetId, @Param("startDt") Timestamp startDt, @Param("endDt") Timestamp endDt);

	@Query("select p.dataset_id, p.publish_start_dt, publish_end_dt from dasit.add_published_window(:datasetId, :startDt, :endDt) p")
	DatasetWindow addPublishedWindow(@Param("datasetId") Long datasetId, @Param("startDt") Timestamp startDt, @Param("endDt") Timestamp endDt);

	@Query("select p.dataset_id, p.publish_start_dt, publish_end_dt from dasit.remove_published_window(:datasetId, :startDt, :endDt) p")
	List<DatasetWindow> removePublishedWindow(@Param("datasetId") Long datasetId, @Param("startDt") Timestamp startDt, @Param("endDt") Timestamp endDt);
}
