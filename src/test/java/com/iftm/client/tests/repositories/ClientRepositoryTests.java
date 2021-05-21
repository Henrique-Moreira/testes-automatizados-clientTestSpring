package com.iftm.client.tests.repositories;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.persistence.EntityNotFoundException;

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
	private Calendar birthdayDate;
	private String newName;
	private Double newIncome;

	@BeforeEach
	void setUp() throws Exception {
		existingId = 1L;
		noneExistingId = Long.MAX_VALUE;
		countTotalClients = 12L;
		countClientByIncome = 5L;
		existingName = "Carolina";
		existingNameCaseSensitive = "cARoLiNa";
		emptyName = "";
		birthdayDate = Calendar.getInstance();
		birthdayDate.set(1996, Calendar.DECEMBER, 23);
		newName = "Homem Aranha";
		newIncome = 10000.0;
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
		Assertions.assertEquals(countTotalClients + 1, client.getId());
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

		if (listName.isEmpty()) {
			result = false;
		} else {
			result = true;
		}

		Assertions.assertTrue(result);
	}

	// Testar find para nome vazio (Neste caso teria que retornar todos os
	// clientes);
	@Test
	public void findByNameShouldReturnAllNamesWithoudPuttingTheName() {
		List<Client> listName = repository.findByNameContainingIgnoreCase(emptyName);
		List<Client> allNames = repository.findAll();

		Assertions.assertEquals(allNames, listName);
	}

	// Testar find para data de nascimento maior que determinado data de referência
	@Test
	public void findByBirthDateShouldReturnAllNamesThatHaveBirthdayOnTheInformedDate() {
		// Data de referência = 1996-12-23
		Date newDate = birthdayDate.getTime();
		List<Client> allNames = repository.findByBirthDateOrYear(newDate.toInstant());

		Assertions.assertFalse(allNames.isEmpty());
		
		// Data de referencia + 1 ano = 1997-12-23
		birthdayDate.add(Calendar.YEAR, 1);
		newDate = birthdayDate.getTime();

		allNames = repository.findByBirthDateOrYear(newDate.toInstant());

		Assertions.assertTrue(allNames.isEmpty());

	}

	// Na classe ClientRepositoryTests, criar teste para testar o update de um
	// cliente. Teste pelo menos dois cenários diferentes.
	@Test
	public void clientNameShouldUpdateWhenPassAnExistingId() {
		Client entity = repository.getOne(existingId);
		entity.setName(newName);

		Assertions.assertEquals(newName, entity.getName());
	}

	@Test
	public void updateShouldReturnADefaultMessageWhenUnableToFindTheInformedId() {
		try {	
			Client entity = repository.getOne(noneExistingId);
			entity.setName(newName);
			entity = repository.save(entity);
		} catch (EntityNotFoundException e) {
			String actual = e.getMessage();
			Assertions.assertEquals("Unable to find com.iftm.client.entities.Client with id " + Long.MAX_VALUE, actual);
		}
	}
	
	@Test
	public void clientIncomeShouldUpdateWhenPassAnExistingId() {
		Client entity = repository.getOne(existingId);
		entity.setIncome(newIncome);

		Assertions.assertEquals(newIncome, entity.getIncome());
	}
}
