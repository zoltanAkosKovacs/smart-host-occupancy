package com.zk.occupancy.service;

import com.zk.occupancy.model.OccupancyRequest;
import com.zk.occupancy.model.OccupancyResponse;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class OccupancyService {

	public static final BigDecimal PREMIUM_THRESHOLD = new BigDecimal("100");

	public OccupancyResponse optimizeBooking(OccupancyRequest request) {
		List<BigDecimal> premiumBudgets = new ArrayList<>();
		List<BigDecimal> economyBudgets = new ArrayList<>();

		sortBudgets(request, premiumBudgets, economyBudgets);

		var premiumRevenue = BigDecimal.ZERO;
		var economyRevenue = BigDecimal.ZERO;

		// fill premium rooms
		var remainingPremiumRooms = request.getPremiumRooms();
		var assignedPremiumRooms = premiumBudgets.stream()
			.limit(remainingPremiumRooms)
			.toList();

		for (var price : assignedPremiumRooms) {
			premiumRevenue = premiumRevenue.add(price);
		}

		remainingPremiumRooms -= assignedPremiumRooms.size();

		// upgrade economy to premium if possible and necessary
		var remainingEconomyRooms = request.getEconomyRooms();
		var upgrades = Math.min(remainingPremiumRooms,
			economyBudgets.size() - remainingEconomyRooms);
		var upgradedPremiumRooms = economyBudgets.stream()
			.limit(Math.max(upgrades, 0))
			.toList();

		for (var price : upgradedPremiumRooms) {
			log.info("Room upgrade guest paying: {}", price);
			premiumRevenue = premiumRevenue.add(price);
		}

		economyBudgets.removeAll(upgradedPremiumRooms);
		remainingPremiumRooms -= upgradedPremiumRooms.size();

		// fill economy rooms
		var assignedEconomyRooms = economyBudgets.stream()
			.limit(remainingEconomyRooms)
			.toList();

		for (var price : assignedEconomyRooms) {
			economyRevenue = economyRevenue.add(price);
		}

		remainingEconomyRooms -= assignedEconomyRooms.size();

		return buildResponse(request, remainingPremiumRooms, premiumRevenue, remainingEconomyRooms,
			economyRevenue);
	}

	private void sortBudgets(OccupancyRequest request, List<BigDecimal> premiumBudgets,
		List<BigDecimal> economyBudgets) {
		var guestBudgets = request.getPotentialGuests();

		var partitionedBudgets = guestBudgets.stream()
			.filter(budget -> budget.compareTo(BigDecimal.ZERO) > 0)
			.collect(Collectors.partitioningBy(budget -> budget.compareTo(PREMIUM_THRESHOLD) >= 0));

		premiumBudgets.addAll(partitionedBudgets.get(true));
		economyBudgets.addAll(partitionedBudgets.get(false));

		premiumBudgets.sort(Collections.reverseOrder());
		economyBudgets.sort(Collections.reverseOrder());

		log.info("premium budgets: {}", premiumBudgets);
		log.info("economy budgets: {}", economyBudgets);
	}

	private OccupancyResponse buildResponse(OccupancyRequest request,
		int remainingPremiumRooms,
		BigDecimal premiumRevenue,
		int remainingEconomyRooms,
		BigDecimal economyRevenue) {

		var response = OccupancyResponse.builder()
			.usagePremium(request.getPremiumRooms() - remainingPremiumRooms)
			.revenuePremium(premiumRevenue.stripTrailingZeros())
			.usageEconomy(request.getEconomyRooms() - remainingEconomyRooms)
			.revenueEconomy(economyRevenue.stripTrailingZeros())
			.build();

		log.info("returning occupancy response: {}", response);
		return response;
	}

}