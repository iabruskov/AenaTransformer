package com.findPassengers.mvc.repository;

import com.findPassengers.mvc.entity.AenaflightDestination;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

/**
 * Created by ivan.bruskov on 19.09.2018.
 */
public interface AenaflightDestinationRepository extends JpaRepository<AenaflightDestination, Long> {
}
