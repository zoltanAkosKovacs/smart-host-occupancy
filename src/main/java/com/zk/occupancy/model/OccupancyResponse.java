package com.zk.occupancy.model;

import java.math.BigDecimal;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
public class OccupancyResponse {

	int usagePremium;
	BigDecimal revenuePremium;
	int usageEconomy;
	BigDecimal revenueEconomy;

}
