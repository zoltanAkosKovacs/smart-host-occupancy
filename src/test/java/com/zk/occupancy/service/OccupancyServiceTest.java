package com.zk.occupancy.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zk.occupancy.model.OccupancyRequest;
import com.zk.occupancy.model.OccupancyResponse;
import java.io.File;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class OccupancyServiceTest {

	private static List<BigDecimal> potentialGuests;

	static {
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			potentialGuests = objectMapper.readValue(
				new File("src/test/java/resources/potentialGuests.json"),
				objectMapper.getTypeFactory().constructCollectionType(List.class, BigDecimal.class)
			);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Autowired
	ObjectMapper objectMapper;
	@Autowired
	OccupancyService occupancyService;

	private static Stream<Arguments> provideRoomConfigurations() {
		return Stream.of(
			Arguments.of(3, 3, 3, 3, new BigDecimal(738), new BigDecimal("167.99")),
			Arguments.of(7, 5, 6, 4, new BigDecimal(1054), new BigDecimal("189.99")),
			Arguments.of(2, 7, 2, 4, new BigDecimal(583), new BigDecimal("189.99")),
			Arguments.of(0, 0, 0, 0, BigDecimal.ZERO, BigDecimal.ZERO)
		);
	}

	@ParameterizedTest
	@MethodSource("provideRoomConfigurations")
	void testOptimization(int premiumRooms, int economyRooms,
		int expectedPremiumUsage, int expectedEconomyUsage,
		BigDecimal expectedPremiumRevenue, BigDecimal exceptedEconomyRevenue) {

		var response = occupancyService.optimizeBooking(
			OccupancyRequest.builder()
				.potentialGuests(potentialGuests)
				.premiumRooms(premiumRooms)
				.economyRooms(economyRooms)
				.build()
		);

		assertThat(response)
			.isNotNull()
			.isInstanceOf(OccupancyResponse.class);

		assertThat(response.getRevenuePremium())
			.isNotNull()
			.isNotNegative();

		assertThat(response.getRevenueEconomy())
			.isNotNull()
			.isNotNegative();

		assertThat(response.getRevenuePremium()).isEqualByComparingTo(expectedPremiumRevenue);
		assertThat(response.getRevenueEconomy()).isEqualByComparingTo(exceptedEconomyRevenue);

		assertThat(response.getUsageEconomy())
			.isNotNegative();

		assertThat(response.getUsageEconomy())
			.isNotNegative();

		assertThat(response.getUsagePremium()).isEqualByComparingTo(expectedPremiumUsage);
		assertThat(response.getUsageEconomy()).isEqualByComparingTo(expectedEconomyUsage);
	}


	@Test
	void testOptimizaton_nullBudgets() {
		var response = occupancyService.optimizeBooking(
			OccupancyRequest.builder()
				.potentialGuests(List.of(
					BigDecimal.valueOf(0), BigDecimal.valueOf(0)
				))
				.premiumRooms(1)
				.economyRooms(1)
				.build()
		);

		assertThat(response)
			.isNotNull()
			.isInstanceOf(OccupancyResponse.class);

		assertThat(response.getRevenuePremium())
			.isNotNull()
			.isNotNegative();

		assertThat(response.getRevenueEconomy())
			.isNotNull()
			.isNotNegative();

		assertThat(response.getRevenuePremium()).isEqualByComparingTo(BigDecimal.ZERO);
		assertThat(response.getRevenueEconomy()).isEqualByComparingTo(BigDecimal.ZERO);

		assertThat(response.getUsageEconomy())
			.isNotNegative();

		assertThat(response.getUsageEconomy())
			.isNotNegative();

		assertThat(response.getUsagePremium()).isEqualByComparingTo(0);
		assertThat(response.getUsageEconomy()).isEqualByComparingTo(0);
	}

	@Test
	void testOptimizaton_negativeBudgets() {
		var response = occupancyService.optimizeBooking(
			OccupancyRequest.builder()
				.potentialGuests(List.of(
					BigDecimal.valueOf(-10), BigDecimal.valueOf(-1)
				))
				.premiumRooms(1)
				.economyRooms(1)
				.build()
		);

		assertThat(response)
			.isNotNull()
			.isInstanceOf(OccupancyResponse.class);

		assertThat(response.getRevenuePremium())
			.isNotNull()
			.isNotNegative();

		assertThat(response.getRevenueEconomy())
			.isNotNull()
			.isNotNegative();

		assertThat(response.getRevenuePremium()).isEqualByComparingTo(BigDecimal.ZERO);
		assertThat(response.getRevenueEconomy()).isEqualByComparingTo(BigDecimal.ZERO);

		assertThat(response.getUsageEconomy())
			.isNotNegative();

		assertThat(response.getUsageEconomy())
			.isNotNegative();

		assertThat(response.getUsagePremium()).isEqualByComparingTo(0);
		assertThat(response.getUsageEconomy()).isEqualByComparingTo(0);
	}
}