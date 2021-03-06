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
import com.iftm.client.repositories.ClientRepository;
import com.iftm.client.services.ClientService;
import com.iftm.client.services.exceptions.ResourceNotFoundException;
import com.iftm.client.tests.factory.ClientFactory;

@SpringBootTest //carrega o contexto da aplicacao
@Transactional 
public class ClientServiceIT {

	@Autowired // fazer inj de dependencia
	private ClientService service;
	
	@Autowired
	private ClientRepository repository;

	private long existingId;
	private long nonExistingId;
	private long countTotalClients;
	private int countClientByIncome;
	private PageRequest pageResquest;
	private long existingId2;
	private String existingName, existingCpf;
	private Client client;
	private ClientDTO clientDTO;
	
	@BeforeEach
	void setUp() throws Exception {
		existingId = 1L;
		nonExistingId = 1000L;
		countClientByIncome = 5;
		pageResquest = PageRequest.of(0, 10);
		countTotalClients = 12L;
		existingId2 = 11L;
		existingName = "Silvio Almeida";
		existingCpf = "10164334861";
		client = ClientFactory.createClient();
		clientDTO = ClientFactory.createClientDTO(13L);
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
	
	// Atividade: testes de integra????o
	
	/* Implementar um teste que ao receber um id existente deve excluir o cliente com o
	 c??digo do id e verificar se realmente decrementou o n??mero de clientes inclu??dos na
	 base de dados.*/
	@Test
	public void deleteClientByIdShouldDecreaseTotalClients() {
		service.delete(existingId);
		
		long totalAfterDelete = repository.count();
		
		Assertions.assertEquals(countTotalClients-1, totalAfterDelete);
	}
	
	/*Implementar um teste que dever?? testar o findById. Para o teste, voc?? ter?? que
	entrar com o c??digo de um cliente existente e verificar se o nome e CPF do cliente
	s??o correspondentes.*/
	@Test
	public void findByIdShouldReturnCorrectCPFAndName() {
		ClientDTO entity = service.findById(existingId2);
	
		Assertions.assertTrue(existingName.equals(entity.getName()));
		Assertions.assertEquals(existingCpf, entity.getCpf());
	}
	
	/*Implementar um teste que dever?? testar o insert. Para o teste, voc?? dever?? criar um
	novo cliente usando o padr??o f??brica e inserir o cliente. Em seguida voc?? dever??
	verificar o findAll e verificar se o n??mero de clientes ser?? incrementado na base de
	dados.*/
	@Test
	public void insertShouldIncreaseCountTotalClients() {
		// antes de inserir
		Assertions.assertEquals(countTotalClients, repository.findAll().size());
		
		service.insert(clientDTO);
		
		// depois de inserir
		Assertions.assertEquals(countTotalClients+1, repository.findAll().size());
	}
	
	/*Implementar um teste que dever?? testar o update. Para o teste, voc?? dever??
	atualizar os dados de um cliente existente e em seguida voc?? dever?? verificar se os
	dados do cliente foram atualizados. Lembrando que o service update retorna os
	dados do cliente atrav??s do ClientDTO.*/
	@Test
	public void updateShouldUpdateTheClient() {
		ClientDTO client = service.findById(existingId);
		ClientDTO clientUpdated = service.update(existingId, clientDTO);
		 
		// Provar que o cliente foi atualizado testando se o id ?? o mesmo
		Assertions.assertEquals(client.getId(), clientUpdated.getId());
		
		
		// Verificar se os atributos atualizaram
		Assertions.assertNotEquals(client.getName(), clientUpdated.getName());
		Assertions.assertFalse(client.getCpf().equals(clientUpdated.getCpf()));
		Assertions.assertFalse(client.getChildren() == clientUpdated.getChildren());
		Assertions.assertTrue(client.getIncome() != clientUpdated.getIncome());
		Assertions.assertNotEquals(client.getBirthDate(), clientUpdated.getBirthDate());
							
	}
}
