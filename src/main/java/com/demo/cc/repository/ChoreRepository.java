package com.demo.cc.repository;

import com.demo.cc.domain.Chore;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ChoreRepository extends JpaRepository<Chore, Long> {

    List<Chore> findByUserIdAndDate(Long userId, LocalDate date);

    List<Chore> findByDate(LocalDate date);
}
