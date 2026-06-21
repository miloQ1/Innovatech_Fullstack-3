package cl.innovatech.ms_clientes.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import cl.innovatech.ms_clientes.model.Client;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long>{



}
