package br.com.lefranchi.hexagonal.demo.infrastructure.input.rest;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.lefranchi.hexagonal.demo.application.port.input.ProductManagementUseCase;
import br.com.lefranchi.hexagonal.demo.application.port.input.command.CreateProductCommand;
import br.com.lefranchi.hexagonal.demo.application.port.input.command.UpdateProductCommand;
import br.com.lefranchi.hexagonal.demo.application.port.input.response.ProductResponse;
import br.com.lefranchi.hexagonal.demo.domain.vo.ProductId;
import br.com.lefranchi.hexagonal.demo.infrastructure.input.rest.request.CreateProductRequest;
import br.com.lefranchi.hexagonal.demo.infrastructure.input.rest.request.UpdateProductRequest;

// Imports de Swagger (¡NUEVOS!)
import br.com.lefranchi.hexagonal.demo.infrastructure.input.rest.response.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@Tag(name = "Gestión de Productos", description = "API para operaciones CRUD de productos") // <-- NUEVO
public class ProductController {

    private final ProductManagementUseCase productManagement;

    @PostMapping
    @Operation(summary = "Crear un nuevo producto", description = "Crea un producto en la base de datos.") // <-- NUEVO
    @ApiResponses(value = { // <-- NUEVO
            @ApiResponse(responseCode = "201", description = "Producto creado exitosamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ProductResponse.class))),
            @ApiResponse(responseCode = "400", description = "Datos de producto inválidos (ej. nombre vacío, precio nulo)",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<ProductResponse> createProduct(@RequestBody CreateProductRequest request) {
        CreateProductCommand command = new CreateProductCommand(
                request.getName(),
                request.getPrice()
        );

        ProductResponse response = productManagement.createProduct(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar un producto por ID", description = "Obtiene los detalles de un producto específico.") // <-- NUEVO
    @ApiResponses(value = { // <-- NUEVO
            @ApiResponse(responseCode = "200", description = "Producto encontrado",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ProductResponse.class))),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<ProductResponse> getProduct(
            @Parameter(description = "ID (UUID) del producto a buscar") // <-- NUEVO
            @PathVariable String id) {
        ProductId productId = new ProductId(id);
        ProductResponse response = productManagement.findProduct(productId);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(summary = "Listar todos los productos", description = "Devuelve una lista de todos los productos.") // <-- NUEVO
    @ApiResponses(value = { // <-- NUEVO
            @ApiResponse(responseCode = "200", description = "Listado exitoso",
                    content = @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = ProductResponse.class)))), // <-- Se usa ArraySchema para listas
            @ApiResponse(responseCode = "500", description = "Error interno del servidor",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<List<ProductResponse>> getAllProducts() {
        List<ProductResponse> responses = productManagement.findAllProducts();
        return ResponseEntity.ok(responses);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar un producto existente", description = "Actualiza el nombre y/o el precio de un producto.") // <-- NUEVO
    @ApiResponses(value = { // <-- NUEVO
            @ApiResponse(responseCode = "200", description = "Producto actualizado",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ProductResponse.class))),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<ProductResponse> updateProduct(
            @Parameter(description = "ID (UUID) del producto a actualizar") // <-- NUEVO
            @PathVariable String id,
            @RequestBody UpdateProductRequest request) {

        ProductId productId = new ProductId(id);
        UpdateProductCommand command = new UpdateProductCommand(
                request.getName(),
                request.getPrice()
        );

        ProductResponse response = productManagement.updateProduct(productId, command);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar un producto", description = "Elimina un producto por su ID.") // <-- NUEVO
    @ApiResponses(value = { // <-- NUEVO
            @ApiResponse(responseCode = "204", description = "Producto eliminado (Sin contenido)",
                    content = @Content(mediaType = "application/json")), // <-- 204 No devuelve cuerpo
            @ApiResponse(responseCode = "404", description = "Producto no encontrado",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<Void> deleteProduct(
            @Parameter(description = "ID (UUID) del producto a eliminar") // <-- NUEVO
            @PathVariable String id) {
        ProductId productId = new ProductId(id);
        productManagement.deleteProduct(productId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/activate")
    @Operation(summary = "Activar un producto", description = "Cambia el estado de un producto a 'ACTIVE'.") // <-- NUEVO
    @ApiResponses(value = { // <-- NUEVO
            @ApiResponse(responseCode = "200", description = "Producto activado",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ProductResponse.class))),
            @ApiResponse(responseCode = "400", description = "No se puede activar (ej. precio negativo)",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<ProductResponse> activateProduct(
            @Parameter(description = "ID (UUID) del producto a activar") // <-- NUEVO
            @PathVariable String id) {
        ProductId productId = new ProductId(id);
        ProductResponse response = productManagement.activateProduct(productId);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/deactivate")
    @Operation(summary = "Desactivar un producto", description = "Cambia el estado de un producto a 'INACTIVE'.") // <-- NUEVO
    @ApiResponses(value = { // <-- NUEVO
            @ApiResponse(responseCode = "200", description = "Producto desactivado",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ProductResponse.class))),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<ProductResponse> deactivateProduct(
            @Parameter(description = "ID (UUID) del producto a desactivar") // <-- NUEVO
            @PathVariable String id) {
        ProductId productId = new ProductId(id);
        ProductResponse response = productManagement.deactivateProduct(productId);
        return ResponseEntity.ok(response);
    }
}