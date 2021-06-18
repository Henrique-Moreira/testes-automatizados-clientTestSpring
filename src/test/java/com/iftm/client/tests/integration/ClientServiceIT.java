package com.iftm.client.tests.integration;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

import com.iftm.client.dto.ClientDTO;
import com.iftm.client.entities.Client;
import com.iftm.client.services.ClientService;
import com.iftm.client.services.exceptions.ResourceNotFoundException;

@SpringBootTest //carrega o contexto da aplicacao
@Transactional 
public class ClientServiceIT {

	@Autowired // fazer inj de dependencia
	private ClientService service;

	private long existingId;
	private long nonExistingId;
	private long countTotalClients;
	private int countClientByIncome;
	private PageRequest pageResquest;
	
	@BeforeEach
	void setUp() throws Exception {
		existingId = 1L;
		nonExistingId = 1000L;
		countClientByIncome = 5;
		pageResquest = PageRequest.of(0, 10);
		countTotalClients = 12L;
	}
	
	@Test
	public void deleteShouldDoNothingWhenIdExists() {
		Assertions.assertDoesNotThrow(() -> {
			service.delete(existingId);
		});
	}
	
	@Test
	public void deleteShouldThrowResourceNotFoundExceptionWhenIdDoesNotExists() {
		Assertions.assertThrows(ResourceNotFoundException.class, () -> {
			service.delete(nonExistingId);
		});
	}
	
	@Test
	public void findByIncomeShouldReturnClientsWhenClientIncomeIsGreaterThanOrEqualsToValue() {
		Double income = 4000.0;
		
		Page<ClientDTO> result = service.findByIncome(income, pageResquest);

		Assertions.assertFalse(result.isEmpty());
		Assertions.assertEquals(countClientByIncome, result.getTotalElements());
	}
	
	@Test
	public void findAllShouldReturnAllClients() {
		List<ClientDTO> result = service.findAll();
		
		Assertions.assertEquals(countTotalClients, result.size());
	}
}
