package com.ssafy.mimo.domain.curtain.entity;

import org.jetbrains.annotations.NotNull;

import com.ssafy.mimo.common.BaseDeviceEntity;

import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
public class Curtain extends BaseDeviceEntity {
	@NotNull
	private Integer openDegree;
}
