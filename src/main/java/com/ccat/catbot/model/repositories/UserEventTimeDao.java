package com.ccat.catbot.model.repositories;

import com.ccat.catbot.model.entities.UserEventTime;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserEventTimeDao extends JpaRepository<UserEventTime, Long> {
}
