package com.ssafy.mimo.domain.light.entity;

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
public class Light extends BaseDeviceEntity {
	@NotNull
	private String wakeupColor;
	@NotNull
	private String curColor;
}
