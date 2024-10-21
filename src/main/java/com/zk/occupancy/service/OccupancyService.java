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
		List<BigDecimal> premiumCandidates = new ArrayList<>();
		List<BigDecimal> economyCandidates = new ArrayList<>();

		sortBudgets(request, premiumCandidates, economyCandidates);

		var premiumRevenue = BigDecimal.ZERO;
		var economyRevenue = BigDecimal.ZERO;

		// fill premium rooms
		var availablePremiumRooms = request.getPremiumRooms();
		var premiumAssigned = premiumCandidates.stream()
			.limit(availablePremiumRooms)
			.toList();

		premiumRevenue = premiumRevenue.add(
			premiumAssigned.stream().reduce(BigDecimal.ZERO, BigDecimal::add)
		);

		availablePremiumRooms -= premiumAssigned.size();

		// upgrade economy candidates if possible and necessary
		var availableEconomyRooms = request.getEconomyRooms();
		var upgrades = Math.min(availablePremiumRooms,
			Math.max(0, economyCandidates.size() - availableEconomyRooms));
		var upgradedCandidates = economyCandidates.stream()
			.limit(upgrades)
			.toList();

		upgradedCandidates.forEach(candidate ->
			log.info("Room upgrade for economy candidate with budget: {}", candidate)
		);

		premiumRevenue = premiumRevenue.add(
			upgradedCandidates.stream().reduce(BigDecimal.ZERO, BigDecimal::add)
		);

		economyCandidates.removeAll(upgradedCandidates);
		availablePremiumRooms -= upgradedCandidates.size();

		// fill economy rooms
		var economyAssigned = economyCandidates.stream()
			.limit(availableEconomyRooms)
			.toList();

		economyRevenue = economyRevenue.add(
			economyAssigned.stream().reduce(BigDecimal.ZERO, BigDecimal::add)
		);

		availableEconomyRooms -= economyAssigned.size();

		return buildResponse(request, availablePremiumRooms, premiumRevenue, availableEconomyRooms,
			economyRevenue);
	}

	private void sortBudgets(OccupancyRequest request, List<BigDecimal> premiumCandidates,
		List<BigDecimal> economyCandidates) {
		var guestBudgets = request.getPotentialGuests();

		var partitionedGuests = guestBudgets.stream()
			.filter(budget -> budget.compareTo(BigDecimal.ZERO) > 0)
			.collect(Collectors.partitioningBy(budget -> budget.compareTo(PREMIUM_THRESHOLD) >= 0));

		premiumCandidates.addAll(partitionedGuests.get(true));
		economyCandidates.addAll(partitionedGuests.get(false));

		premiumCandidates.sort(Collections.reverseOrder());
		economyCandidates.sort(Collections.reverseOrder());

		log.info("premium candidates: {}", premiumCandidates);
		log.info("economy candidates: {}", economyCandidates);
	}

	private OccupancyResponse buildResponse(OccupancyRequest request,
		int availablePremiumRooms,
		BigDecimal premiumRevenue,
		int availableEconomyRooms,
		BigDecimal economyRevenue) {

		var response = OccupancyResponse.builder()
			.usagePremium(request.getPremiumRooms() - availablePremiumRooms)
			.revenuePremium(premiumRevenue.stripTrailingZeros())
			.usageEconomy(request.getEconomyRooms() - availableEconomyRooms)
			.revenueEconomy(economyRevenue.stripTrailingZeros())
			.build();

		log.info("returning occupancy response: {}", response);
		return response;
	}

}