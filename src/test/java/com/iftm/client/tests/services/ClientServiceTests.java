package com.iftm.client.tests.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.iftm.client.dto.ClientDTO;
import com.iftm.client.entities.Client;
import com.iftm.client.repositories.ClientRepository;
import com.iftm.client.services.ClientService;
import com.iftm.client.services.exceptions.DatabaseException;
import com.iftm.client.services.exceptions.ResourceNotFoundException;
import com.iftm.client.tests.factory.ClientFactory;

@ExtendWith(SpringExtension.class)
public class ClientServiceTests {

	@InjectMocks
	private ClientService service;

	@Mock
	private ClientRepository repository;

	private long existingId;
	private long nonExistingId;
	private long nonExistingId2;
	private long dependentId;
	private long dependentId2;
	private Client client;
	private ClientDTO dtoClient;
	private PageRequest pageRequest;
	private List<Client> fakeList;
	private Page<Client> pageMock;
	private Double income;
	
	@BeforeEach
	void setUp() throws Exception {
		existingId = 1L;
		nonExistingId = 1000L;
		nonExistingId2 = 1001L;
		dependentId = 4L;
		dependentId2 = 5L;
		client = ClientFactory.createClient();
		dtoClient = ClientFactory.createClientDTO();
		pageRequest = PageRequest.of(0, 12, Direction.valueOf("ASC"), "name");
		fakeList = new ArrayList<>();
		fakeList.add(client);
		pageMock = new PageImpl<Client>(fakeList);
		income = 1500.0;
		
		// Configurando comportamento para o meu mock
		Mockito.doNothing().when(repository).deleteById(existingId);
		Mockito.doThrow(EmptyResultDataAccessException.class).when(repository).deleteById(nonExistingId);
		Mockito.doThrow(DataIntegrityViolationException.class).when(repository).deleteById(dependentId);

		Mockito.when(repository.findById(existingId)).thenReturn(Optional.of(client));
		Mockito.when(repository.findById(nonExistingId)).thenReturn(Optional.empty());

		// Atividade: testes de service com Mockito
		Mockito.doThrow(EmptyResultDataAccessException.class).when(repository).deleteById(nonExistingId2);
		Mockito.doThrow(ResourceNotFoundException.class).when(repository).findById(nonExistingId);
		Mockito.doThrow(DataIntegrityViolationException.class).when(repository).deleteById(dependentId2);
	
		Mockito.when(repository.findAll(pageRequest)).thenReturn(pageMock);
		Mockito.when(repository.findByIncome(income, pageRequest)).thenReturn(pageMock);
		Mockito.when(repository.getOne(existingId)).thenReturn(client);
		Mockito.when(repository.save(client)).thenReturn(client);

		Mockito.doThrow(ResourceNotFoundException.class).when(repository).getOne(nonExistingId);
	}
	
	@Test
	public void deleteShouldDoNothingWhenIdExists() {
		// service.delete(existingId);

		Assertions.assertDoesNotThrow(() -> {
			service.delete(existingId);
		});

		Mockito.verify(repository, Mockito.times(1)).deleteById(existingId);
	}

	@Test
	public void deleteShouldThrowEmptyResourceNotFoundExceptionWhenIdDoesNotExists() {

		Assertions.assertThrows(ResourceNotFoundException.class, () -> {
			service.delete(nonExistingId);
		});

		Mockito.verify(repository, Mockito.times(1)).deleteById(nonExistingId);
	}

	@Test
	public void deleteShouldThrowDatabaseExceptionWhenIdHasDependecyIntegrity() {

		Assertions.assertThrows(DatabaseException.class, () -> {
			service.delete(dependentId);
		});

		Mockito.verify(repository, Mockito.times(1)).deleteById(dependentId);
	}

	/*
	 * delete deveria retornar vazio quando o id existir
	 */
	@Test
	public void deleteShouldReturnEmptyWhenIdExists() {
		Assertions.assertDoesNotThrow(() -> {
			service.delete(existingId);
		});
		Mockito.verify(repository, Mockito.times(1)).deleteById(existingId);
	}

	// lançar uma EmptyResultDataAccessException quando o id não existir
	@Test
	public void deleteShouldThrowEmptyResultDataAccessExceptionWhenIdDoesNotExists() {
		
		Assertions.assertThrows(ResourceNotFoundException.class, () -> {
			service.delete(nonExistingId2);
		});

		Mockito.verify(repository, Mockito.times(1)).deleteById(nonExistingId2);
	}
	
	/* lançar DataIntegrityViolationException quando a deleção implicar em uma restrição de integridade.*/
	@Test
	public void deleteShouldThrowDataIntegrityViolationExceptionWhenIdHasDependecyIntegrity() {

		Assertions.assertThrows(DatabaseException.class, () -> {
			service.delete(dependentId2);
		});

		Mockito.verify(repository, Mockito.times(1)).deleteById(dependentId2);
	}
	
	
	// findAllPaged deveria retornar uma página (e chamar o método findAll do repository)

		@Test
		public void findAllPagedShouldReturnAPageAndCallFindAllMethodFromRepository() {
			Assertions.assertNotNull(service.findAllPaged(pageRequest));
			
			Mockito.verify(repository, Mockito.times(1)).findAll(pageRequest);	
		}

	// findByIncome deveria retornar uma página (e chamar o método findByIncome do
	// repository)
		@Test
		public void findByIncomeShouldReturnAPageAndCallfindByIncomeMethod() {
			Assertions.assertNotNull(service.findByIncome(income, pageRequest));
			
			Mockito.verify(repository, Mockito.times(1)).findByIncome(income, pageRequest);	
		}

	// findById deveria
	// retornar um ClientDTO quando o id existir
	@Test
	public void findByIdShouldReturnClientDTOWhenIdExists() {
		Boolean result;
		if (service.findById(existingId) == null) {
			result = false;
		} else {
			result = true;
		}

		Assertions.assertTrue(result);

		Mockito.verify(repository, Mockito.times(1)).findById(existingId);
	}

	// ○ lançar ResourceNotFoundException quando o id não existir
	@Test
	public void findByIdShouldThrowsResourceNotFoundExceptionWhenIdNotExists() {

		Assertions.assertThrows(ResourceNotFoundException.class, () -> {
			service.findById(nonExistingId);
		});

		Mockito.verify(repository, Mockito.times(1)).findById(nonExistingId);
	}

	// update deveria
	// retornar um ClientDTO quando o id existir
	@Test
	public void updateShouldReturnClientDTOWhenIdExists() {
		Assertions.assertDoesNotThrow(() -> {
			service.update(existingId, dtoClient);
		});

		Mockito.verify(repository, Mockito.times(1)).getOne(existingId);
		Mockito.verify(repository, Mockito.times(1)).save(client);
	}

	// lançar uma ResourceNotFoundException quando o id não existir
	@Test
	public void updateShouldThrowsResourceNotFoundExceptionWhenIdNotExists() {

		Assertions.assertThrows(ResourceNotFoundException.class, () -> {
			service.update(nonExistingId, dtoClient);
		});

		Mockito.verify(repository, Mockito.times(1)).getOne(nonExistingId);

	}

	// insert deveria retornar um ClientDTO ao inserir um novo cliente
		@Test
		public void insertShouldReturnClientDTOWhenCreateANewClient() {
//			usa o mock do save
			ClientDTO clientDTO = service.insert(dtoClient);
			Assertions.assertNotNull(clientDTO);
			Mockito.verify(repository, Mockito.times(1)).save(clientDTO.toEntity());
		}
}
