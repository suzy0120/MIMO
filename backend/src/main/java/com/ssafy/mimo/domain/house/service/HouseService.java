package com.ssafy.mimo.domain.house.service;

import com.ssafy.mimo.domain.house.dto.HouseResponseDto;
import com.ssafy.mimo.domain.house.dto.HouseUpdateRequestDto;
import com.ssafy.mimo.domain.house.entity.House;
import com.ssafy.mimo.domain.house.entity.UserHouse;
import com.ssafy.mimo.domain.house.repository.HouseRepository;
import com.ssafy.mimo.domain.house.repository.UserHouseRepository;
import com.ssafy.mimo.user.entity.User;
import com.ssafy.mimo.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class HouseService {

	private final HouseRepository houseRepository;
	private final UserHouseRepository userHouseRepository;
	private final UserRepository userRepository;

	public List<HouseResponseDto> getHouses(Long userId) {
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new IllegalArgumentException("해당하는 회원이 없습니다."));

		List<UserHouse> userHouses = userHouseRepository.findAllByUser_Id(userId);
		List<HouseResponseDto> houseList = new ArrayList<>();

		// UserHouse 목록을 순회하며 HouseResponseDto 생성
		for (UserHouse userHouse : userHouses) {
			House house = userHouse.getHouse();
			HouseResponseDto houseResponseDto = HouseResponseDto.builder()
					.id(userHouse.getId())
					.nickname(userHouse.getNickname())
					.address(house.getAddress())
					.isHome(true)
					.devices(new ArrayList<>())
					.build();
			houseList.add(houseResponseDto);
		}
		return houseList;
	}

	public void updateInfo(Long userId, Long userHouseId, HouseUpdateRequestDto houseUpdateRequestDto) {
		UserHouse userHouse = userHouseRepository.findById(userHouseId)
				.orElseThrow(() -> new IllegalArgumentException("해당 ID의 UserHouse가 존재하지 않습니다: " + userHouseId));

		if (!userHouse.getUser().getId().equals(userId)) {
			throw new IllegalArgumentException("집을 수정할 수 있는 권한이 없습니다.");
		}

		userHouse.updateNickname(houseUpdateRequestDto.getNickname());
	}

	public House findHouseById(Long houseId) {
		return houseRepository.findById(houseId)
				.orElseThrow(() -> new IllegalArgumentException("해당 ID를 가진 집이 존재하지 않습니다."));
	}
}
