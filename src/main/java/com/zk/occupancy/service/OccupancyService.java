package com.zk.occupancy.service;

import com.zk.occupancy.model.OccupancyRequest;
import com.zk.occupancy.model.OccupancyResponse;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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

		// book premium rooms
		var availablePremiumRooms = request.getPremiumRooms();
		for (int i = 0; i < premiumCandidates.size() && 0 < availablePremiumRooms; i++) {
			premiumRevenue = premiumRevenue.add(premiumCandidates.get(i));
			availablePremiumRooms--;
		}

		// upgrade economy customers if possible and necessary
		int upgrades = Math.min(availablePremiumRooms, economyCandidates.size());
		var availableEconomyRooms = request.getEconomyRooms();
		if (economyCandidates.size() > availableEconomyRooms) {
			for (int i = 0; i < upgrades; i++) {
				premiumRevenue = premiumRevenue.add(economyCandidates.remove(0));
				availablePremiumRooms--;
			}
		}

		// fill economy rooms
		for (int i = 0; i < economyCandidates.size() && 0 < availableEconomyRooms; i++) {
			economyRevenue = economyRevenue.add(economyCandidates.get(i));
			availableEconomyRooms--;
		}

		return buildResponse(request, availablePremiumRooms, premiumRevenue, availableEconomyRooms,
			economyRevenue);
	}

	private void sortBudgets(OccupancyRequest request, List<BigDecimal> premiumCandidates,
		List<BigDecimal> economyCandidates) {
		var guestBudgets = request.getPotentialGuests();
		for (var budget : guestBudgets) {
			if (budget.compareTo(PREMIUM_THRESHOLD) >= 0) {
				premiumCandidates.add(budget);
			} else {
				economyCandidates.add(budget);
			}
		}

		premiumCandidates.sort(Collections.reverseOrder());
		economyCandidates.sort(Collections.reverseOrder());
	}

	private OccupancyResponse buildResponse(OccupancyRequest request, int availablePremiumRooms,
		BigDecimal premiumRevenue,
		int availableEconomyRooms, BigDecimal economyRevenue) {
		return OccupancyResponse.builder()
			.usagePremium(request.getPremiumRooms() - availablePremiumRooms)

			.premiumRevenue(premiumRevenue.setScale(2,
				RoundingMode.HALF_UP).stripTrailingZeros())

			.usageEconomy(request.getEconomyRooms() - availableEconomyRooms)

			.economyRevenue(economyRevenue.setScale(2,
				RoundingMode.HALF_UP).stripTrailingZeros())
			
			.build();
	}

}