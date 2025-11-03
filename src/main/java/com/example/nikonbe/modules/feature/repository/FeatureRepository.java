package com.example.nikonbe.modules.feature.repository;

import com.example.nikonbe.modules.feature.entity.Feature;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface FeatureRepository extends JpaRepository<Feature, Integer> {

  boolean existsByName(String name);

  @Query(
      "SELECT f FROM Feature f WHERE "
          + "(?1 IS NULL OR LOWER(f.name) LIKE LOWER(CONCAT('%', ?1, '%'))) AND "
          + "(?2 IS NULL OR f.featureGroup = ?2)")
  List<Feature> findAllByFilters(String name, String featureGroup);

  @Query(
      "SELECT f FROM Feature f WHERE "
          + "(?1 IS NULL OR LOWER(f.name) LIKE LOWER(CONCAT('%', ?1, '%'))) AND "
          + "(?2 IS NULL OR f.featureGroup = ?2)")
  Page<Feature> findAllPaginated(String name, String featureGroup, Pageable pageable);
}
