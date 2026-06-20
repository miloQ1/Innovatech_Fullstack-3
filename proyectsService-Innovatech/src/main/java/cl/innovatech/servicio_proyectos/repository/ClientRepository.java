package cl.innovatech.servicio_proyectos.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import cl.innovatech.servicio_proyectos.model.Client;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long>{



}
