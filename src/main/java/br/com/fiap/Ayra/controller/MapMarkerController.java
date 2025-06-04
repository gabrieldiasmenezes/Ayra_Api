package br.com.fiap.Ayra.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import br.com.fiap.Ayra.model.Coordinates;
import br.com.fiap.Ayra.model.MapMarker;
import br.com.fiap.Ayra.repository.CoordinatesRepository;
import br.com.fiap.Ayra.repository.MapMarkerRepository;
import br.com.fiap.Ayra.specification.MapMarkerSpecification;
import java.net.URI;


@RestController
@RequestMapping("/map-marker")
@Tag(name = "Marcadores no Mapa", description = "Endpoints para gerenciamento de marcadores geográficos")
public class MapMarkerController {
    public record MapMarkerFilter(String intensity) {}
    @Autowired
    private MapMarkerRepository repository;

    @Autowired
    private CoordinatesRepository coordinatesRepository;

    // POST /map-marker - Criar novo marcador
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @CacheEvict(value = "map-marker", allEntries = true)
    @Operation(
        summary = "Cria um novo marcador no mapa",
        description = "Registra um marcador geográfico associado a uma coordenada já existente.",
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Dados do marcador a ser criado",
            required = true,
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = MapMarker.class))
        ),
        responses = {
            @ApiResponse(responseCode = "201", description = "Marcador criado com sucesso",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = MapMarker.class))),
            @ApiResponse(responseCode = "400", description = "Erro de validação ou dados inválidos",
                content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "Coordenada não encontrada",
                content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor",
                content = @Content(mediaType = "application/json"))
        }
    )
    public ResponseEntity<MapMarker> create(@RequestBody MapMarker map) {
        // Obtém as coordenadas do JSON recebido
        Coordinates coordinates = map.getCoordinates();

        if (coordinates == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "As coordenadas são obrigatórias.");
        }

        boolean exists = false;

        // Verifica se já existe pelo ID ou por proximidade geográfica
        if (coordinates.getId() != null) {
            exists = coordinatesRepository.existsById(coordinates.getId());
        } else {
            exists = !coordinatesRepository
                .findByLatitudeBetweenAndLongitudeBetween(
                    coordinates.getLatitude() - 0.0001,
                    coordinates.getLatitude() + 0.0001,
                    coordinates.getLongitude() - 0.0001,
                    coordinates.getLongitude() + 0.0001)
                .isEmpty();
        }

        // Se não existir, salva as coordenadas
        if (!exists) {
            coordinates = coordinatesRepository.save(coordinates); // Salva e obtém a instância persistida
        } else {
            // Se existir, busca a instância persistida no banco
            coordinates = coordinatesRepository
                .findByLatitudeBetweenAndLongitudeBetween(
                    coordinates.getLatitude() - 0.0001,
                    coordinates.getLatitude() + 0.0001,
                    coordinates.getLongitude() - 0.0001,
                    coordinates.getLongitude() + 0.0001)
                .stream()
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Coordenadas não encontradas"));
        }

        // Garante que a instância persistida seja usada
        map.setCoordinates(coordinates);

        // Salva o marcador no banco de dados
        MapMarker saved = repository.save(map);

        // Retorna a URI do recurso criado
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(saved.getId())
                .toUri();

        return ResponseEntity.created(location).body(saved);
    }
    // GET /map-marker/{id} - Buscar marcador por ID
    @GetMapping("/{id}")
    @Operation(
        summary = "Recupera um marcador pelo ID",
        description = "Retorna os dados completos de um marcador geográfico cadastrado.",
        parameters = {
            @Parameter(name = "id", description = "ID do marcador", example = "1")
        },
        responses = {
            @ApiResponse(responseCode = "200", description = "Marcador recuperado com sucesso",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = MapMarker.class))),
            @ApiResponse(responseCode = "404", description = "Marcador não encontrado",
                content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor",
                content = @Content(mediaType = "application/json"))
        }
    )
    public ResponseEntity<MapMarker> getById(@PathVariable Long id) {
        return repository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // GET /map-marker - Listar todos os marcadores
    @GetMapping
    @Operation(
        summary = "Lista todos os marcadores",
        description = "Retorna uma lista com todos os marcadores geográficos cadastrados.",
        responses = {
            @ApiResponse(responseCode = "200", description = "Lista de marcadores retornada com sucesso",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = MapMarker.class))),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor",
                content = @Content(mediaType = "application/json"))
        }
    )
    public ResponseEntity<Page<MapMarker>> getAll(
            MapMarkerFilter filters,
            @PageableDefault(size = 10, sort = "id", direction = Direction.DESC) Pageable pageable) {

        return ResponseEntity.ok(repository.findAll(MapMarkerSpecification.withFilters(filters), pageable));
    }
}