package com.ssafy.mimo.common;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@MappedSuperclass
public class BaseDeletableEntity extends BaseEntity {

	private boolean isActive = true;

	@CreatedDate
	@Column(columnDefinition = "datetime(0) default now(0)", nullable = false, updatable = false)
	private LocalDateTime registeredDttm;

	private LocalDateTime unregisteredDttm;
}
