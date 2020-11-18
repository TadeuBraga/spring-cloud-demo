package br.com.tadeu.microservice.fornecedor.repository;

import org.springframework.data.repository.CrudRepository;

import br.com.tadeu.microservice.fornecedor.model.Pedido;

public interface PedidoRepository extends CrudRepository<Pedido, Long>{

}
