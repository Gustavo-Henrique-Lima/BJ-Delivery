package com.bj_food.ms_usuario.repository;

import com.bj_food.ms_usuario.domain.model.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AddressRepository extends JpaRepository<Address, UUID> {

    List<Address> findByUserId(UUID userId);

    boolean existsByUserIdAndMainTrue(UUID userId);
}