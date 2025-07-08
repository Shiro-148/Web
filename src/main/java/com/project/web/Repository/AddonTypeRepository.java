package com.project.web.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.web.Entity.AddonTypeEntity;

@Repository
public interface AddonTypeRepository extends JpaRepository<AddonTypeEntity, Integer> {
}