package br.com.trama.service;

import br.com.trama.dto.AvaliacaoRequest;
import br.com.trama.dto.AvaliacaoResponse;
import br.com.trama.error.ConflictException;
import br.com.trama.error.ForbiddenException;
import br.com.trama.error.ResourceNotFoundException;
import br.com.trama.model.AvaliacaoConfeccao;
import br.com.trama.model.Pedido;
import br.com.trama.model.enums.PerfilUsuario;
import br.com.trama.model.enums.StatusPedido;
import br.com.trama.repository.AvaliacaoConfeccaoRepository;
import br.com.trama.repository.PedidoRepository;
import br.com.trama.security.UsuarioPrincipal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AvaliacaoService {
    private final AvaliacaoConfeccaoRepository avaliacaoConfeccaoRepository;
    private final PedidoRepository pedidoRepository;

    public AvaliacaoService(AvaliacaoConfeccaoRepository avaliacaoConfeccaoRepository, PedidoRepository pedidoRepository) {
        this.avaliacaoConfeccaoRepository = avaliacaoConfeccaoRepository;
        this.pedidoRepository = pedidoRepository;
    }

    @Transactional
    public AvaliacaoResponse criar(AvaliacaoRequest request, UsuarioPrincipal principal) {
        Pedido pedido = pedidoRepository.findById(request.pedidoId())
                .orElseThrow(() -> new ResourceNotFoundException("Pedido não encontrado."));
        validarAutor(pedido, principal);
        if (pedido.getStatus() != StatusPedido.ENTREGUE) {
            throw new ConflictException("Somente pedidos entregues podem ser avaliados.");
        }
        if (avaliacaoConfeccaoRepository.existsByPedidoId(pedido.getIdPedido())) {
            throw new ConflictException("Este pedido já foi avaliado.");
        }

        AvaliacaoConfeccao avaliacao = new AvaliacaoConfeccao();
        avaliacao.setPedidoId(pedido.getIdPedido());
        avaliacao.setConfeccaoId(pedido.getConfeccao().getIdConfeccao());
        avaliacao.setNota(request.nota());
        String comentario = request.comentario() == null ? null : request.comentario().trim();
        avaliacao.setComentario(comentario == null || comentario.isEmpty() ? null : comentario);
        return toResponse(avaliacaoConfeccaoRepository.saveAndFlush(avaliacao));
    }

    /** Só quem fez o pedido (ou um ADMIN) pode avaliá-lo. */
    private void validarAutor(Pedido pedido, UsuarioPrincipal principal) {
        if (principal == null) {
            throw new ForbiddenException("Usuário não autenticado.");
        }
        boolean admin = principal.getPerfil() == PerfilUsuario.ADMIN;
        boolean autor = pedido.getUsuario().getIdUsuario().equals(principal.getIdUsuario());
        if (!admin && !autor) {
            throw new ForbiddenException("Somente quem fez o pedido pode avaliá-lo.");
        }
    }

    private AvaliacaoResponse toResponse(AvaliacaoConfeccao a) {
        return new AvaliacaoResponse(a.getId(), a.getPedidoId(), a.getConfeccaoId(), a.getNota(),
                a.getComentario(), a.getDataAvaliacao());
    }
}
