package com.zk.occupancy.model;

import java.math.BigDecimal;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
@Builder
public class OccupancyResponse {

	int usagePremium;
	BigDecimal premiumRevenue;
	int usageEconomy;
	BigDecimal economyRevenue;

}
