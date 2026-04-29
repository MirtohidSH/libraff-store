package org.example.libraffstore.inventory.repository;

import org.example.libraffstore.inventory.entity.Store;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StoreRepository extends JpaRepository<Store, Long> {
}
