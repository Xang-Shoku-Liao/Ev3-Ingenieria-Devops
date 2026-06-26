package com.rentacar.rentacar;

import com.rentacar.rentacar.Model.Auto;
import com.rentacar.rentacar.Repository.AutoRepository;
import com.rentacar.rentacar.Service.AutoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * IE5/IE6: Tests unitarios — JaCoCo mide cobertura.
 * Si cobertura < 70%, el pipeline se detiene (IE6).
 */
@SpringBootTest
class AutoServiceTest {

    @Autowired
    private AutoService service;

    @Test
    @DisplayName("listarTodos() debe retornar los autos seed")
    void listarTodos_debeRetornarAutos() {
        List<Auto> autos = service.listarTodos();
        assertNotNull(autos);
        assertFalse(autos.isEmpty());
    }

    @Test
    @DisplayName("listarDisponibles() retorna solo autos con disponible=true")
    void listarDisponibles_soloRetornaDisponibles() {
        List<Auto> disponibles = service.listarDisponibles();
        assertNotNull(disponibles);
        disponibles.forEach(a ->
            assertTrue(a.isDisponible(), "Todos deben estar disponibles")
        );
    }

    @Test
    @DisplayName("guardar() acepta patente nueva")
    void guardar_aceptaPatenteNueva() {
        Auto nuevo = new Auto(99L, "Kia", "Rio", "ZZ-99-99", true, 18000);
        assertTrue(service.guardar(nuevo));
    }

    @Test
@DisplayName("guardar() rechaza patente duplicada")
void guardar_rechazaPatenteDuplicada() {
    // Primero guardamos un auto con patente conocida
    Auto original = new Auto(101L, "Ford", "Focus", "TEST-DUP-01", true, 20000);
    service.guardar(original);
    // Intentamos guardar otro con la misma patente → debe fallar
    Auto duplicado = new Auto(102L, "Honda", "Civic", "TEST-DUP-01", true, 35000);
    assertFalse(service.guardar(duplicado));
}

    @Test
    @DisplayName("arrendarVehiculo() retorna 0 si ID no existe")
    void arrendarVehiculo_retornaCeroSiNoExiste() {
        assertEquals(0, service.arrendarVehiculo(999L));
    }

    @Test
    @DisplayName("arrendarVehiculo() retorna 1 si ya está arrendado")
    void arrendarVehiculo_retornaUnoSiYaArrendado() {
        // ID 2 (Hyundai Accent) tiene disponible=false en seed
        assertEquals(1, service.arrendarVehiculo(2L));
    }

    @Test
    @DisplayName("arrendarVehiculo() retorna 2 y cambia estado")
    void arrendarVehiculo_retornaDosYCambiaEstado() {
        // ID 3 (Suzuki Swift) disponible=true en seed
        assertEquals(2, service.arrendarVehiculo(3L));
    }

    @Test
    @DisplayName("eliminar() retorna true si ID existe")
    void eliminar_retornaTrueSiExiste() {
        assertTrue(service.eliminar(1L));
    }

    @Test
    @DisplayName("eliminar() retorna false si ID no existe")
    void eliminar_retornaFalseSiNoExiste() {
        assertFalse(service.eliminar(999L));
    }
}
