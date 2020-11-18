package br.com.tadeu.microservice.loja.service;

import java.time.LocalDate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;

import br.com.tadeu.microservice.loja.client.FornecedorClient;
import br.com.tadeu.microservice.loja.client.TransportadorClient;
import br.com.tadeu.microservice.loja.dto.CompraDTO;
import br.com.tadeu.microservice.loja.dto.InfoEntregaDTO;
import br.com.tadeu.microservice.loja.dto.InfoFornecedorDTO;
import br.com.tadeu.microservice.loja.dto.InfoPedidoDTO;
import br.com.tadeu.microservice.loja.dto.VoucherDTO;
import br.com.tadeu.microservice.loja.model.Compra;
import br.com.tadeu.microservice.loja.repository.CompraRepository;
import br.com.tadeu.microservice.model.enums.CompraStatus;

@Service
public class CompraService {

	private static final Logger LOG = LoggerFactory.getLogger(CompraService.class);
	
	@Autowired
	private FornecedorClient fornecedorClient;
	
	@Autowired
	private CompraRepository compraRepository;
	
	@Autowired
	private TransportadorClient transportadorClient;
	
	@HystrixCommand( threadPoolKey = "findByIdThreadPool")
	public Compra findById(Long id) {
		return compraRepository.findById(id).orElse(new Compra());
	}
	
	@HystrixCommand(fallbackMethod = "realizaCompraFallback", threadPoolKey = "realizaCompraThreadPool")
	public Compra realizaCompra(CompraDTO compra) {
		Compra compraSalva = new Compra();
		compraSalva.setStatus(CompraStatus.RECEBIDO);
		compraSalva.setEnderecoDestino(compra.getEndereco().toString());
		compraRepository.save(compraSalva);
		
		final String estado = compra.getEndereco().getEstado();
		LOG.info("Buscando informações do fornecedor de {}.", estado);
		InfoFornecedorDTO infoFornecedor = fornecedorClient.getInfoPorEstado(estado);
		LOG.info("Realizando um pedido.");
		InfoPedidoDTO pedido = fornecedorClient.realizaPedido(compra.getItens());
		compraSalva.setStatus(CompraStatus.PEDIDO_REALIZADO);
		compra.setCompraId(compra.getCompraId());
		
		compraSalva.setPedidoId(pedido.getId());
		compraSalva.setTempoDePreparo(pedido.getTempoDePreparo());
		compraRepository.save(compraSalva);
		
		InfoEntregaDTO entregaDto = new InfoEntregaDTO();
		entregaDto.setPedidoId(pedido.getId());
		entregaDto.setDataParaEntrega(LocalDate.now().plusDays(pedido.getTempoDePreparo()));
		entregaDto.setEnderecoOrigem(infoFornecedor.getEndereco());
		entregaDto.setEnderecoDestino(compra.getEndereco().toString());
		VoucherDTO voucher = transportadorClient.reservaEntrega(entregaDto);
		compraSalva.setDataParaEntrega(voucher.getPrevisaoParaEntrega());
		compraSalva.setVoucher(voucher.getNumero());
		compraSalva.setStatus(CompraStatus.RESERVA_ENTRADA_REALIZADA);
		compraRepository.save(compraSalva);
		
		return compraSalva;

	}
	
	public Compra reprocessaCompra(Long id) {
		return null;
	}
	
	public Compra cancelaCompra(Long id) {
		return null;
	}
	
	public Compra realizaCompraFallback(CompraDTO compra) {
		if(compra.getCompraId() != null) {
			return compraRepository.findById(compra.getCompraId()).get();
		}
		Compra compraFallback = new Compra();
		compraFallback.setEnderecoDestino(compra.getEndereco().toString());
		return compraFallback;

	}

}
