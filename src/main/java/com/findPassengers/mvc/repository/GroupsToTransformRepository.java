package com.findPassengers.mvc.repository;

import com.findPassengers.mvc.entity.GroupToTransform;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Created by ivan.bruskov on 19.09.2018.
 */
public interface GroupsToTransformRepository  extends JpaRepository<GroupToTransform, Long> {

    List<GroupToTransform> findTop1000By();
    List<GroupToTransform> findTop2000By();
    List<GroupToTransform> findTop10000By();
}
