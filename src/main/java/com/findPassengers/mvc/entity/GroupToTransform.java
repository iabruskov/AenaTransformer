package com.findPassengers.mvc.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "groups_to_transforme")
public class GroupToTransform {

    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "flight_icao_code", length = 8)
    private String flight_icao_code;

    @Column(name = "flight_number", length = 8)
    private String flight_number;

    @Column(name = "schd_dep_only_date_lt", length = 32)
    private String schd_dep_only_date_lt;



    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFlight_icao_code() {
        return flight_icao_code;
    }

    public void setFlight_icao_code(String flight_icao_code) {
        this.flight_icao_code = flight_icao_code;
    }

    public String getFlight_number() {
        return flight_number;
    }

    public void setFlight_number(String flight_number) {
        this.flight_number = flight_number;
    }

    public String getSchd_dep_only_date_lt() {
        return schd_dep_only_date_lt;
    }

    public void setSchd_dep_only_date_lt(String schd_dep_only_date_lt) {
        this.schd_dep_only_date_lt = schd_dep_only_date_lt;
    }
}
