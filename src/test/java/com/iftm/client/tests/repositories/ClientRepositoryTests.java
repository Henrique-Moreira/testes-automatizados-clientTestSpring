package com.iftm.client.tests.repositories;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import com.iftm.client.entities.Client;
import com.iftm.client.repositories.ClientRepository;
import com.iftm.client.tests.factory.ClientFactory;

@DataJpaTest
public class ClientRepositoryTests {
		
	@Autowired
	private ClientRepository repository;
	
	private long existingId;
	private long noneExistingId;
	private long countTotalClients;
	private long countClientByIncome;
	private String existingName;
	private String existingNameCaseSensitive;
	private String emptyName;
	
	@BeforeEach
	void setUp() throws Exception {
		existingId = 1L;
		noneExistingId = Long.MAX_VALUE;
		countTotalClients = 12L;
		countClientByIncome = 5L;
		existingName = "Carolina";
		existingNameCaseSensitive = "cARoLiNa";
		emptyName = "";
	}
	
	@Test
	public void deleteShouldDeleteObjectWhenIdExists() {
		repository.deleteById(existingId);
		
		Optional<Client> result = repository.findById(existingId);
		result.isPresent();
		
		Assertions.assertFalse(result.isPresent());
	}
	
	@Test
	public void deleteShouldThrowExceptionWhenIdDoesNotExists() {
		
		Assertions.assertThrows(EmptyResultDataAccessException.class, () -> {
			repository.deleteById(noneExistingId);
		});
	}
	
	@Test
	public void saveShouldPersistWithAutoIncrementWhenIdIsNull() {
		
		Client client = ClientFactory.createClient();
		client.setId(null);
		
		client = repository.save(client);
		Optional<Client> result = repository.findById(client.getId());
		
		Assertions.assertNotNull(client.getId());
		Assertions.assertEquals(countTotalClients +1, client.getId());
		Assertions.assertTrue(result.isPresent());
		Assertions.assertSame(result.get(), client);
	}
	
	@Test
	public void findByIncomeShouldReturnClientsWhenClientIncomeIsGreaterThanOrEqualsToValue() {
		
		Double income = 4000.0;
		PageRequest pageResquest = PageRequest.of(0, 10);
		
		Page<Client> result = repository.findByIncome(income, pageResquest);
		
		Assertions.assertFalse(result.isEmpty());
		Assertions.assertEquals(countClientByIncome, result.getTotalElements());
	}
	
	
	// Atividade 3 - Testes JPA Repository
	
	// Testar o find para nome existente;
	@Test
	public void findByNameShouldReturnExistingName() {
		List<Client> listName = repository.findByNameContainingIgnoreCase(existingName);
		Assertions.assertFalse(listName.isEmpty());
	}
	
	// Testar o find para nome existente ignorando case
	@Test
	public void findByNameShouldReturnExistingNameIgnoringCase() {
		boolean result;
		List<Client> listName = repository.findByNameContainingIgnoreCase(existingNameCaseSensitive);
		
		
		if(listName.isEmpty()) {
			result = false;
		} else {
			result = true;
		}
		
		Assertions.assertTrue(result);
	}
	
	// Testar find para nome vazio (Neste caso teria que retornar todos os clientes);
	@Test
	public void findByNameShouldReturnAllNamesWithoudPuttingTheName() {
		List<Client> listName = repository.findByNameContainingIgnoreCase(emptyName);
		List<Client> allNames = repository.findAll();
		
		Assertions.assertEquals(allNames, listName);
	}
	
	// Testar find para data de nascimento maior que determinado data de referência
}














