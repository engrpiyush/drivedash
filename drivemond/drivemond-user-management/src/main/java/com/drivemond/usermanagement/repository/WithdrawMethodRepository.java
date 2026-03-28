package com.drivemond.usermanagement.repository;

import com.drivemond.usermanagement.entity.WithdrawMethod;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WithdrawMethodRepository extends JpaRepository<WithdrawMethod, Long> {

    List<WithdrawMethod> findAllByActiveTrueOrderByIsDefaultDescMethodNameAsc();

    boolean existsByMethodName(String methodName);

    boolean existsByMethodNameAndIdNot(String methodName, Long id);
}
