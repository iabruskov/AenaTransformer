package com.findPassengers.mvc.repository;

import com.findPassengers.mvc.entity.Aenaflight2017;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface AenaflightSourceRepository extends JpaRepository<Aenaflight2017, Long> {

    List<Aenaflight2017> findByFlightIcaoCodeAndFlightNumberAndSchdDepOnlyDateLtOrderByIdAsc(String flight_icao_code, String flight_number, String schd_dep_only_date_lt);

}
