package br.com.trama.service;

import br.com.trama.error.BadRequestException;
import br.com.trama.error.ResourceNotFoundException;
import br.com.trama.model.Categoria;
import br.com.trama.model.Confeccao;
import br.com.trama.model.Importacao;
import br.com.trama.model.LancamentoMensal;
import br.com.trama.model.Usuario;
import br.com.trama.model.enums.OrigemLancamento;
import br.com.trama.model.enums.StatusImportacao;
import br.com.trama.model.enums.StatusLancamento;
import br.com.trama.repository.CategoriaRepository;
import br.com.trama.repository.ConfeccaoRepository;
import br.com.trama.repository.ImportacaoRepository;
import br.com.trama.repository.LancamentoMensalRepository;
import br.com.trama.repository.UsuarioRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.HashSet;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ImportacaoTransactionService {
    private final ImportacaoRepository importacaoRepository;
    private final LancamentoMensalRepository lancamentoRepository;
    private final ConfeccaoRepository confeccaoRepository;
    private final CategoriaRepository categoriaRepository;
    private final UsuarioRepository usuarioRepository;

    public ImportacaoTransactionService(ImportacaoRepository importacaoRepository,
                                        LancamentoMensalRepository lancamentoRepository,
                                        ConfeccaoRepository confeccaoRepository,
                                        CategoriaRepository categoriaRepository,
                                        UsuarioRepository usuarioRepository) {
        this.importacaoRepository = importacaoRepository;
        this.lancamentoRepository = lancamentoRepository;
        this.confeccaoRepository = confeccaoRepository;
        this.categoriaRepository = categoriaRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional
    public Importacao importar(String nomeArquivo, Usuario usuarioRecebido, List<ImportacaoCsvLinha> linhas) {
        Usuario usuario = usuarioRepository.findById(usuarioRecebido.getIdUsuario())
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado."));
        Importacao importacao = new Importacao();
        importacao.setNomeArquivo(nomeArquivo);
        importacao.setDataImportacao(java.time.LocalDateTime.now());
        importacao.setQtdLinhas(linhas.size());
        importacao.setStatus(StatusImportacao.CONCLUIDA);
        importacao.setUsuario(usuario);
        importacaoRepository.saveAndFlush(importacao);

        Set<String> chaves = new HashSet<>();
        for (ImportacaoCsvLinha linha : linhas) {
            Confeccao confeccao = confeccaoRepository.findById(linha.idConfeccao())
                    .orElseThrow(() -> new ResourceNotFoundException("Confecção " + linha.idConfeccao() + " não encontrada."));
            Categoria categoria = categoriaRepository.findById(linha.idCategoria())
                    .orElseThrow(() -> new ResourceNotFoundException("Categoria " + linha.idCategoria() + " não encontrada."));
            LocalDate mes = linha.mesAno().atDay(1);
            String chave = confeccao.getIdConfeccao() + ":" + categoria.getIdCategoria() + ":" + mes;
            if (!chaves.add(chave) || lancamentoRepository
                    .existsByConfeccao_IdConfeccaoAndCategoria_IdCategoriaAndMesReferencia(
                            confeccao.getIdConfeccao(), categoria.getIdCategoria(), mes)) {
                throw new BadRequestException("O CSV contém um lançamento duplicado ou já cadastrado: " + chave);
            }
            if (usuario.getPerfil().name().equals("EMPRESARIO")
                    && (usuario.getConfeccao() == null
                    || !usuario.getConfeccao().getIdConfeccao().equals(confeccao.getIdConfeccao()))) {
                throw new BadRequestException("O usuário empresário só pode importar vendas da própria confecção.");
            }
            LancamentoMensal lancamento = new LancamentoMensal();
            lancamento.setMesReferencia(mes);
            lancamento.setQuantidade(linha.quantidade());
            lancamento.setFaturamento(linha.faturamento());
            lancamento.setOrigem(OrigemLancamento.IMPORTACAO);
            lancamento.setStatus(StatusLancamento.ATIVO);
            lancamento.setConfeccao(confeccao);
            lancamento.setCategoria(categoria);
            lancamento.setIdImportacao(importacao.getIdImportacao());
            lancamento.setOrigemDado(linha.origemDado());
            lancamento.setNomeFonte(linha.nomeFonte());
            lancamentoRepository.save(lancamento);
        }
        lancamentoRepository.flush();
        return importacao;
    }
}
