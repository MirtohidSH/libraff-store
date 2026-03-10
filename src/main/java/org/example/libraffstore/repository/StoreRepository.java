package org.example.libraffstore.repository;

import org.example.libraffstore.entity.Store;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StoreRepository extends JpaRepository<Store, Long> {
}