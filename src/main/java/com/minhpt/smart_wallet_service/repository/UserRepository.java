package com.minhpt.smart_wallet_service.repository;

import com.minhpt.smart_wallet_service.model.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByUsernameIgnoreCase(String username);

    boolean existsByEmailIgnoreCase(String email);

    @EntityGraph(attributePaths = {"userRoles", "userRoles.role"})
    Optional<User> findById(Long id);

    @EntityGraph(attributePaths = {"userRoles", "userRoles.role"})
    Optional<User> findByUsername(String username);

    @EntityGraph(attributePaths = {"userRoles", "userRoles.role"})
    Optional<User> findByIdAndStatus(Long id, int status);

    @Override
    @EntityGraph(attributePaths = {"userRoles", "userRoles.role"})
    List<User> findAll();
}
