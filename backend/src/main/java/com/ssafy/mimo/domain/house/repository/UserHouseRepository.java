package com.ssafy.mimo.domain.house.repository;

import com.ssafy.mimo.domain.house.entity.House;
import com.ssafy.mimo.domain.house.entity.UserHouse;
import com.ssafy.mimo.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserHouseRepository extends JpaRepository<UserHouse, Long> {
    List<UserHouse> findAllByUserId(Long userId);

    List<UserHouse> findByUserAndIsHome(User user, boolean b);

    Optional<UserHouse> findByHouseAndIsActive(House house, boolean b);

    UserHouse findHomeByUserIdAndIsHome(Long userId, boolean b);

    Optional<UserHouse> findByIdAndUserId(Long userHouseId, Long userId);

    Optional<Object> findByUserIdAndHouseId(Long userId, Long houseId);
}
