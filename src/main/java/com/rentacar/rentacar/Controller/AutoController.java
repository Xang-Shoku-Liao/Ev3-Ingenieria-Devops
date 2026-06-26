package com.rentacar.rentacar.Controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.rentacar.rentacar.Model.Auto;
import com.rentacar.rentacar.Service.AutoService;

/**
 * CAPA DE CONTROLADOR (Entry Point)
 * Esta clase expone los Endpoints para que un cliente (Postman, Frontend) 
 * pueda comunicarse con nuestra aplicación. 
 * Gestiona las Solicitudes (Requests) y entrega las Respuestas (Responses).
 */
@RestController // Define que esta clase es un controlador REST (maneja datos JSON).
@RequestMapping("/api/autos") // Ruta base para todos los endpoints de este controlador.
public class AutoController {

    @Autowired // Inyección de dependencias para conectar con la capa de Servicio.
    private AutoService service;

    /**
     * MÉTODO GET: Obtener todos los vehículos.
     * URL: GET localhost:8080/api/autos
     * @return 200 OK con la lista completa.
     */
    @GetMapping
    public ResponseEntity<List<Auto>> getAll() {
        // El controlador NO decide, solo pide al servicio y entrega el resultado.
        return ResponseEntity.ok(service.listarTodos());
    }

    /**
     * MÉTODO GET: Filtrar solo vehículos disponibles.
     * URL: GET localhost:8080/api/autos/disponibles
     * @return 200 OK con la lista filtrada.
     */
    @GetMapping("/disponibles")
    public ResponseEntity<List<Auto>> getDisponibles() {
        return ResponseEntity.ok(service.listarDisponibles());
    }

    /**
     * MÉTODO POST: Registrar un nuevo vehículo.
     * URL: POST localhost:8080/api/autos
     * @param auto Objeto JSON enviado en el cuerpo de la petición.
     * @return 201 Created si tuvo éxito | 400 Bad Request si la patente ya existe.
     */
    @PostMapping
    public ResponseEntity<String> create(@RequestBody Auto auto) {
        if (service.guardar(auto)) {
            // El código 201 es el estándar para creación exitosa.
            return new ResponseEntity<>("Auto registrado con éxito", HttpStatus.CREATED);
        }
        // Si la lógica de negocio falla (patente duplicada), devolvemos un 400.
        return new ResponseEntity<>("Error: La patente ya existe", HttpStatus.BAD_REQUEST);
    }

    /**
     * MÉTODO PUT: Arrendar un vehículo (Actualizar estado).
     * URL: PUT localhost:8080/api/autos/arrendar/{id}
     * @param id ID del vehículo enviado en la URL.
     * @return 200 OK, 400 Bad Request o 404 Not Found según el caso.
     */
    @PutMapping("/arrendar/{id}")
    public ResponseEntity<String> rent(@PathVariable Long id) {
        int resultado = service.arrendarVehiculo(id);

        // Evaluamos la respuesta de la lógica de negocio para elegir el código HTTP.
        return switch (resultado) {
            case 2 -> ResponseEntity.ok("Vehículo arrendado correctamente"); // Éxito (200)
            case 1 -> ResponseEntity.status(HttpStatus.BAD_REQUEST).body("El vehículo ya está arrendado"); // Error lógico (400)
            default -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("Vehículo no encontrado"); // Error de existencia (404)
        };
    }

    /**
     * MÉTODO DELETE: Eliminar un registro.
     * URL: DELETE localhost:8080/api/autos/{id}
     * @return 204 No Content si se borró | 404 Not Found si no existía.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (service.eliminar(id)) {
            // 204 No Content es la respuesta ideal para una eliminación exitosa.
            return ResponseEntity.noContent().build();
        }
        // Si no se encontró nada que borrar, devolvemos 404.
        return ResponseEntity.notFound().build();
    }
}