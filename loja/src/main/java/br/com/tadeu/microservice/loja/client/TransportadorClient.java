package br.com.tadeu.microservice.loja.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

import br.com.tadeu.microservice.loja.dto.InfoEntregaDTO;
import br.com.tadeu.microservice.loja.dto.VoucherDTO;

@FeignClient("transportador")
public interface TransportadorClient {

	@PostMapping("/entrega")
	VoucherDTO reservaEntrega(InfoEntregaDTO entregaDto);
}
