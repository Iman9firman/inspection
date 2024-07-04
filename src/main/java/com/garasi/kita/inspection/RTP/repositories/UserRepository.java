package com.garasi.kita.inspection.RTP.repositories;

import com.garasi.kita.inspection.RTP.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
}