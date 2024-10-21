package com.zk.occupancy.model;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class OccupancyRequest {

	@Min(value = 0, message = "premiumRooms must be non-negative")
	int premiumRooms;

	@Min(value = 0, message = "economyRooms must be non-negative")
	int economyRooms;

	@NotNull(message = "potentialGuests cannot be null")
	@Size(min = 1, message = "potentialGuests must have at least one element - what a bad day to own a hotel")
	List<BigDecimal> potentialGuests;

}
