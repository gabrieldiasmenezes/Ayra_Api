package br.com.fiap.Ayra.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.fiap.Ayra.model.Coordinates;
import br.com.fiap.Ayra.model.User;
import br.com.fiap.Ayra.model.dto.UserResponse;
import br.com.fiap.Ayra.repository.CoordinatesRepository;
import br.com.fiap.Ayra.repository.UserRepository;
import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/users")
@Tag(name = "Usuários", description = "Endpoints para gerenciamento de usuários")
public class UserController {
    private final Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    private UserRepository repository;

    @Autowired
    private CoordinatesRepository coordinatesRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // CREATE: Criar um novo usuário
    @PostMapping
    @Operation(
        summary = "Cria um novo usuário",
        description = "Registra um novo usuário no sistema. Todos os campos obrigatórios devem ser fornecidos.",
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Dados do usuário a ser criado",
            required = true,
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = User.class))
        ),
        responses = {
            @ApiResponse(responseCode = "201", description = "Usuário criado com sucesso",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponse.class))),
            @ApiResponse(responseCode = "400", description = "Erro de validação nos dados fornecidos",
                content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor",
                content = @Content(mediaType = "application/json"))
        }
    )
    public ResponseEntity<UserResponse> createUser(@RequestBody User user) {
        // Verifica e trata as coordenadas
        Coordinates coordinates = user.getCoordinates();
        if (coordinates != null) {
            boolean exists = false;
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

            if (!exists) {
                coordinates = coordinatesRepository.save(coordinates);
            }

            user.setCoordinates(coordinates);
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User savedUser = repository.save(user);
        return ResponseEntity.status(201).body(new UserResponse(savedUser.getId(), savedUser.getName(), savedUser.getEmail()));
    }

    // READ: Recuperar o próprio usuário autenticado
    @GetMapping("/me")
    @Operation(
        summary = "Recupera o perfil do usuário autenticado",
        description = "Retorna os dados do usuário atualmente autenticado no sistema.",
        responses = {
            @ApiResponse(responseCode = "200", description = "Perfil do usuário recuperado com sucesso",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = User.class))),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado",
                content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor",
                content = @Content(mediaType = "application/json"))
        }
    )
    public ResponseEntity<User> getMe() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = repository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado"));
        return ResponseEntity.ok(user);
    }

    // DELETE: Excluir apenas o próprio usuário autenticado
    @DeleteMapping("/{email}")
    @CacheEvict(value = "categories", allEntries = true)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
        summary = "Exclui o próprio usuário",
        description = "Remove permanentemente o usuário autenticado. Não é permitido excluir outro usuário.",
        parameters = {
            @Parameter(name = "email", description = "Email do usuário a ser excluído (deve ser o mesmo do usuário autenticado)",
                required = true, schema = @Schema(type = "string", example = "usuario.logado@example.com"))
        },
        responses = {
            @ApiResponse(responseCode = "204", description = "Usuário excluído com sucesso"),
            @ApiResponse(responseCode = "403", description = "Acesso negado: não é possível excluir outro usuário",
                content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado",
                content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor",
                content = @Content(mediaType = "application/json"))
        }
    )
    public void destroy(@PathVariable String email) {
        String authenticatedEmail = SecurityContextHolder.getContext().getAuthentication().getName();

        if (!authenticatedEmail.equals(email)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Você só pode excluir sua própria conta.");
        }

        log.info("Apagando usuário com email: " + email);
        User user = getUser(email);
        repository.delete(user);
    }

    // UPDATE: Atualizar apenas o próprio usuário autenticado
    @PutMapping("/{email}")
    @CacheEvict(value = "categories", allEntries = true)
    @Operation(
        summary = "Atualiza o próprio usuário",
        description = "Atualiza os dados do usuário autenticado. Não é permitido atualizar outro usuário.",
        parameters = {
            @Parameter(name = "email", description = "Email do usuário a ser atualizado (deve ser o mesmo do usuário autenticado)",
                required = true, schema = @Schema(type = "string", example = "usuario.logado@example.com"))
        },
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Dados atualizados do usuário",
            required = true,
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = User.class))
        ),
        responses = {
            @ApiResponse(responseCode = "200", description = "Usuário atualizado com sucesso",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = User.class))),
            @ApiResponse(responseCode = "400", description = "Erro de validação nos dados fornecidos",
                content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "403", description = "Acesso negado: não é possível editar outro usuário",
                content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado",
                content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor",
                content = @Content(mediaType = "application/json"))
        }
    )
    public ResponseEntity<User> update(@Valid @PathVariable String email, @RequestBody User updatedUser) {
        String authenticatedEmail = SecurityContextHolder.getContext().getAuthentication().getName();

        if (!authenticatedEmail.equals(email)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Você só pode editar sua própria conta.");
        }

        log.info("Atualizando usuário com email: " + email);
        User currentUser = getUser(email);

        if (updatedUser.getName() != null) {
            currentUser.setName(updatedUser.getName());
        }
        if (updatedUser.getEmail() != null) {
            currentUser.setEmail(updatedUser.getEmail());
        }
        if (updatedUser.getPhone() != null) {
            currentUser.setPhone(updatedUser.getPhone());
        }
        if (updatedUser.getPassword() != null) {
            currentUser.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
        }

        User savedUser = repository.save(currentUser);
        return ResponseEntity.ok(savedUser);
    }

    // Método auxiliar para buscar um usuário pelo email
    private User getUser(String email) {
        return repository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado"));
    }
}