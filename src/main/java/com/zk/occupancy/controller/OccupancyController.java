package com.zk.occupancy.controller;

import com.zk.occupancy.model.OccupancyRequest;
import com.zk.occupancy.model.OccupancyResponse;
import com.zk.occupancy.service.OccupancyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@EnableCaching
@Slf4j
public class OccupancyController {

	private final OccupancyService occupancyService;

	@Autowired
	public OccupancyController(OccupancyService occupancyService) {
		this.occupancyService = occupancyService;
	}

	@PostMapping(value = "/occupancy")
	@Cacheable(value = "occupancyCache", key = "#request.toString()")
	public ResponseEntity<OccupancyResponse> occupancy(
		@RequestBody @Validated OccupancyRequest request) {
		
		log.info("occupancy endpoint called with request: {}", request);
		return new ResponseEntity<>(occupancyService.optimizeBooking(request), HttpStatus.OK);
	}
}