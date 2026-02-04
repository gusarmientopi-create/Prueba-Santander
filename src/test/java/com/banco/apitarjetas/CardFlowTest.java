package com.banco.apitarjetas;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CardFlowTest {

    @Autowired
    private MockMvc mockMvc;

    // Guardaremos el ID de la tarjeta creada para usarlo en los siguientes tests
    private static String cardIdGenerado;

    @Test
    @Order(1)
    public void testEmitirTarjeta() throws Exception {
        String jsonRequest = "{ \"productid\": \"123456\", \"holderName\": \"Juan Perez\" }";

        MvcResult result = mockMvc.perform(post("/cards")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
                .andExpect(status().isCreated()) // Esperamos 201 Created
                .andExpect(jsonPath("$.status").value("ISSUED")) // Estado inicial ISSUED
                .andExpect(jsonPath("$.balance").value(0.0)) // Saldo 0
                .andReturn();

        // Extraer el cardId de la respuesta para usarlo después
        String response = result.getResponse().getContentAsString();
        // Un hack rápido para sacar el ID sin usar librerías extra de JSON
        cardIdGenerado = response.split("\"cardId\":\"")[1].split("\"")[0];
        System.out.println(">>> TARJETA CREADA CON ID: " + cardIdGenerado);
    }

    @Test
    @Order(2)
    public void testActivarTarjeta() throws Exception {
        mockMvc.perform(post("/cards/" + cardIdGenerado + "/activate"))
                .andExpect(status().isOk()) // Esperamos 200 OK
                .andExpect(jsonPath("$.status").value("ACTIVE")); // Ahora debe ser ACTIVE
    }
    
    @Test
    @Order(3)
    public void testConsultarTarjeta() throws Exception {
        mockMvc.perform(get("/cards/" + cardIdGenerado))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.holderName").value("Juan Perez"))
                .andExpect(jsonPath("$.status").value("ACTIVE"));
    }

    @Test
    @Order(4)
    public void testBloquearTarjeta() throws Exception {
        mockMvc.perform(post("/cards/" + cardIdGenerado + "/block"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("BLOCKED"));
    }
}