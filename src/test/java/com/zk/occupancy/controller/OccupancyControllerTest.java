package com.zk.occupancy.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zk.occupancy.model.OccupancyRequest;
import com.zk.occupancy.model.OccupancyResponse;
import com.zk.occupancy.service.OccupancyService;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

@ExtendWith(SpringExtension.class)
@WebMvcTest
class OccupancyControllerTest {

	@MockBean
	OccupancyService occupancyService;

	@Autowired
	ObjectMapper objectMapper;

	@Autowired
	private MockMvc mvc;

	@Test
	void occupancy_OK() throws Exception {

		var response = OccupancyResponse.builder().build();

		when(occupancyService.optimizeBooking(any(OccupancyRequest.class)))
			.thenReturn(response);

		var request = OccupancyRequest.builder()
			.economyRooms(0)
			.premiumRooms(0)
			.potentialGuests(List.of(BigDecimal.ZERO))
			.build();

		mvc.perform(post("/occupancy")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isOk());
	}

	@Test
	void occupancy_responseMapped_OK() throws Exception {

		var response = OccupancyResponse.builder()
			.revenueEconomy(BigDecimal.ONE)
			.usageEconomy(1)
			.revenuePremium(BigDecimal.ONE)
			.usagePremium(1)
			.build();

		when(occupancyService.optimizeBooking(any(OccupancyRequest.class)))
			.thenReturn(response);

		var request = OccupancyRequest.builder()
			.economyRooms(0)
			.premiumRooms(0)
			.potentialGuests(List.of(BigDecimal.ZERO))
			.build();

		mvc.perform(post("/occupancy")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isOk())
			.andExpect((jsonPath("$.usagePremium").isNotEmpty()))
			.andExpect((jsonPath("$.revenuePremium").isNotEmpty()))
			.andExpect((jsonPath("$.usageEconomy").isNotEmpty()))
			.andExpect((jsonPath("$.revenueEconomy").isNotEmpty()));
	}

	@Test
	void occupancy_negativePremiumRooms_BAD_REQUEST() throws Exception {

		var response = OccupancyResponse.builder().build();

		when(occupancyService.optimizeBooking(any(OccupancyRequest.class)))
			.thenReturn(response);

		var request = OccupancyRequest.builder()
			.economyRooms(0)
			.premiumRooms(-1)
			.potentialGuests(List.of(BigDecimal.ZERO))
			.build();

		mvc.perform(post("/occupancy")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isBadRequest())
			.andExpect((jsonPath("$.premiumRooms").isNotEmpty()));
	}

	@Test
	void occupancy_negativeEconomyRooms_BAD_REQUEST() throws Exception {
		var response = OccupancyResponse.builder().build();

		when(occupancyService.optimizeBooking(any(OccupancyRequest.class)))
			.thenReturn(response);

		var request = OccupancyRequest.builder()
			.economyRooms(-1)
			.premiumRooms(0)
			.potentialGuests(List.of(BigDecimal.ZERO))
			.build();

		mvc.perform(post("/occupancy")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isBadRequest())
			.andExpect((jsonPath("$.economyRooms").isNotEmpty()));
	}

	@Test
	void occupancy_emptyPotentialGuests_BAD_REQUEST() throws Exception {
		var response = OccupancyResponse.builder().build();

		when(occupancyService.optimizeBooking(any(OccupancyRequest.class)))
			.thenReturn(response);

		var request = OccupancyRequest.builder()
			.economyRooms(0)
			.premiumRooms(0)
			.potentialGuests(List.of())
			.build();

		mvc.perform(post("/occupancy")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isBadRequest())
			.andExpect((jsonPath("$.potentialGuests").isNotEmpty()));
	}
}