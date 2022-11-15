package com.ccat.catbot.model.repositories;

import com.ccat.catbot.model.entities.UserTime;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserTimeDao extends JpaRepository<UserTime, Long> {
}
