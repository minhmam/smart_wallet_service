package com.minhpt.smart_wallet_service.repository;

import com.minhpt.smart_wallet_service.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long>, CategoryRepositoryCustom {
    List<Category> findAllByCreatedBy(String username);

    Optional<Category> findByIdAndStatus(Long id, int status);

    List<Category> findTop10ByStatusOrderByIdAsc(int status);

}
