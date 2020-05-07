package com.callbackcats.codenames.repository;

import com.callbackcats.codenames.domain.Lobby;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LobbyRepository extends JpaRepository<Lobby, String> {
}
