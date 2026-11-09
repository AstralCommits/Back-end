package br.com.trama.service;

import br.com.trama.dto.ImportacaoResponse;
import br.com.trama.error.BadRequestException;
import br.com.trama.error.ResourceNotFoundException;
import br.com.trama.model.Importacao;
import br.com.trama.model.Usuario;
import br.com.trama.model.enums.OrigemDado;
import br.com.trama.model.enums.StatusImportacao;
import br.com.trama.repository.ImportacaoRepository;
import br.com.trama.repository.UsuarioRepository;
import br.com.trama.security.UsuarioPrincipal;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.text.Normalizer;
import java.time.YearMonth;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ImportacaoService {
    private final UsuarioRepository usuarioRepository;
    private final ImportacaoRepository importacaoRepository;
    private final ImportacaoTransactionService transactionService;

    public ImportacaoService(UsuarioRepository usuarioRepository, ImportacaoRepository importacaoRepository,
                             ImportacaoTransactionService transactionService) {
        this.usuarioRepository = usuarioRepository;
        this.importacaoRepository = importacaoRepository;
        this.transactionService = transactionService;
    }

    public ImportacaoResponse importar(MultipartFile arquivo, UsuarioPrincipal principal) {
        Usuario usuario = usuarioRepository.findById(principal.getIdUsuario())
                .orElseThrow(() -> new ResourceNotFoundException("Usuário autenticado não encontrado."));
        String nome = nomeSeguro(arquivo == null ? null : arquivo.getOriginalFilename());
        List<ImportacaoCsvLinha> linhas = List.of();
        try {
            validarArquivo(arquivo);
            linhas = lerLinhas(arquivo);
            Importacao criada = transactionService.importar(nome, usuario, linhas);
            return toResponse(criada);
        } catch (RuntimeException | IOException ex) {
            registrarErro(nome, usuario, linhas.size());
            if (ex instanceof RuntimeException runtime) throw runtime;
            throw new BadRequestException("Não foi possível ler o arquivo CSV.");
        }
    }

    @Transactional(readOnly = true)
    public List<ImportacaoResponse> listar(UsuarioPrincipal principal) {
        List<Importacao> registros = principal.getPerfil().name().equals("ADMIN")
                ? importacaoRepository.findAll()
                : importacaoRepository.findAllByUsuario_IdUsuarioOrderByDataImportacaoDesc(principal.getIdUsuario());
        return registros.stream().map(this::toResponse).toList();
    }

    private void validarArquivo(MultipartFile arquivo) {
        if (arquivo == null || arquivo.isEmpty()) throw new BadRequestException("Selecione um arquivo CSV.");
        String nome = arquivo.getOriginalFilename();
        if (nome == null || !nome.toLowerCase(Locale.ROOT).endsWith(".csv")) {
            throw new BadRequestException("O arquivo enviado deve possuir extensão .csv.");
        }
    }

    private List<ImportacaoCsvLinha> lerLinhas(MultipartFile arquivo) throws IOException {
        String conteudo = new String(arquivo.getBytes(), StandardCharsets.UTF_8).replace("﻿", "");
        if (conteudo.isBlank()) throw new BadRequestException("O arquivo CSV está vazio.");
        String primeiraLinha = conteudo.lines().findFirst().orElse("");
        char delimitador = contar(primeiraLinha, ';') >= contar(primeiraLinha, ',') ? ';' : ',';
        CSVFormat formato = CSVFormat.DEFAULT.builder()
                .setDelimiter(delimitador)
                .setHeader()
                .setSkipHeaderRecord(true)
                .setIgnoreEmptyLines(true)
                .setTrim(true)
                .build();
        List<ImportacaoCsvLinha> linhas = new ArrayList<>();
        Set<String> colunas = new HashSet<>();
        try (CSVParser parser = CSVParser.parse(conteudo, formato)) {
            parser.getHeaderMap().keySet().forEach(h -> colunas.add(normalizarColuna(h)));
            exigirColuna(colunas, "mesano", "mesreferencia");
            exigirColuna(colunas, "quantidade");
            exigirColuna(colunas, "faturamento", "valor");
            exigirColuna(colunas, "idconfeccao");
            exigirColuna(colunas, "idcategoria");
            exigirColuna(colunas, "origemdado");
            exigirColuna(colunas, "nomefonte");
            for (CSVRecord registro : parser) {
                try {
                    String mesTexto = valor(registro, parser, "mesano", "mesreferencia");
                    YearMonth mes = YearMonth.parse(mesTexto);
                    int quantidade = Integer.parseInt(valor(registro, parser, "quantidade"));
                    BigDecimal faturamento = new BigDecimal(valor(registro, parser, "faturamento", "valor"));
                    Long idConfeccao = Long.valueOf(valor(registro, parser, "idconfeccao"));
                    Long idCategoria = Long.valueOf(valor(registro, parser, "idcategoria"));
                    OrigemDado origem = OrigemDado.valueOf(valor(registro, parser, "origemdado").toUpperCase(Locale.ROOT));
                    String fonte = valor(registro, parser, "nomefonte");
                    if (quantidade < 0 || faturamento.compareTo(BigDecimal.ZERO) < 0) {
                        throw new IllegalArgumentException("Quantidade e faturamento devem ser maiores ou iguais a zero.");
                    }
                    if (fonte.isBlank() || fonte.length() > 200) {
                        throw new IllegalArgumentException("Nome da fonte é obrigatório e deve ter até 200 caracteres.");
                    }
                    linhas.add(new ImportacaoCsvLinha(mes, quantidade, faturamento, idConfeccao,
                            idCategoria, origem, fonte));
                } catch (IllegalArgumentException | DateTimeParseException ex) {
                    throw new BadRequestException("Linha " + registro.getRecordNumber()
                            + " inválida: " + ex.getMessage());
                }
            }
        }
        if (linhas.isEmpty()) throw new BadRequestException("O CSV precisa conter pelo menos uma linha de venda.");
        return linhas;
    }

    private String valor(CSVRecord registro, CSVParser parser, String... aliases) {
        for (String header : parser.getHeaderMap().keySet()) {
            String normalizado = normalizarColuna(header);
            for (String alias : aliases) {
                if (normalizado.equals(alias)) return registro.get(header).trim();
            }
        }
        return "";
    }

    private void exigirColuna(Set<String> headers, String... aliases) {
        if (java.util.Arrays.stream(aliases).noneMatch(headers::contains)) {
            throw new BadRequestException("Coluna obrigatória ausente no CSV: " + aliases[0]);
        }
    }

    private String normalizarColuna(String valor) {
        return Normalizer.normalize(valor, Normalizer.Form.NFD)
                .replaceAll("\\p{M}+", "")
                .toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9]", "");
    }

    private long contar(String texto, char caractere) {
        return texto.chars().filter(c -> c == caractere).count();
    }

    private String nomeSeguro(String nome) {
        if (nome == null || nome.isBlank()) return "arquivo-invalido.csv";
        String limpo = nome.replace('\\', '/');
        limpo = limpo.substring(limpo.lastIndexOf('/') + 1);
        return limpo.length() > 150 ? limpo.substring(limpo.length() - 150) : limpo;
    }

    private void registrarErro(String nome, Usuario usuario, int qtdLinhas) {
        Importacao erro = new Importacao();
        erro.setNomeArquivo(nome);
        erro.setDataImportacao(LocalDateTime.now());
        erro.setQtdLinhas(qtdLinhas);
        erro.setStatus(StatusImportacao.ERRO);
        erro.setUsuario(usuario);
        importacaoRepository.save(erro);
    }

    private ImportacaoResponse toResponse(Importacao i) {
        return new ImportacaoResponse(i.getIdImportacao(), i.getNomeArquivo(), i.getDataImportacao(),
                i.getQtdLinhas(), i.getStatus(), i.getUsuario().getIdUsuario());
    }
}
