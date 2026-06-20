package cl.innovatech.servicio_proyectos.service;

import java.util.List;

import org.springframework.stereotype.Service;

import cl.innovatech.servicio_proyectos.model.Client;
import cl.innovatech.servicio_proyectos.model.enums.ClientStatus;
import cl.innovatech.servicio_proyectos.repository.ClientRepository;
import cl.innovatech.servicio_proyectos.util.UserContext;

@Service
public class ClientService {

    private final ClientRepository clientRepository;

    //inicializo
    public ClientService(ClientRepository clientRepository){
        this.clientRepository = clientRepository;
    }

    public Client createClient(Client client){
        if (client.getStatus() == null){
            client.setStatus(ClientStatus.ACTIVE);
        }
        client.setCreatedBy(UserContext.getCurrentUserId());
        return clientRepository.save(client);
    }

    public List<Client> getAllClients(){
        return clientRepository.findAll();
    }

    public Client getClientById (Long id){
        return clientRepository.findById(id)
        .orElseThrow(()-> new RuntimeException("No se encontró el id solicitado"));
    }

    public Client updateClient(Long id, Client client){
        Client existente = getClientById(id);
        existente.setName(client.getName());
        existente.setIndustry(client.getIndustry());
        existente.setContactName(client.getContactName());
        existente.setContactEmail(client.getContactEmail());
        existente.setStatus(client.getStatus());
        return clientRepository.save(existente);
    }

    public void deleteClient(Long id){
        Client existente = getClientById(id);
        existente.setStatus(ClientStatus.INACTIVE);
        clientRepository.save(existente);
    }


}
