package com.zk.occupancy.model;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class OccupancyRequest {

	@Min(value = 0, message = "premiumRooms must be non-negative")
	private int premiumRooms;

	@Min(value = 0, message = "economyRooms must be non-negative")
	private int economyRooms;

	@NotNull(message = "potentialGuests cannot be null")
	@Size(min = 1, message = "potentialGuests must have at least one element")
	private BigDecimal[] potentialGuests;

}
