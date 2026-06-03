package com.hsf302.final_project.repository;
import com.hsf302.final_project.entity.AppFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface AppFileRepository extends JpaRepository<AppFile, Long> {
}