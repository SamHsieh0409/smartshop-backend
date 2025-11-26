package com.smartshop.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.smartshop.model.entity.AiLog;

public interface AiLogRepository extends JpaRepository<AiLog, Long> {

    List<AiLog> findByUserId(Long userId);

}
