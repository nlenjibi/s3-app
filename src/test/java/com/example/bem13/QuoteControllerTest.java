package com.example.bem13;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class QuoteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void getAllQuotesReturnsList() throws Exception {
        mockMvc.perform(get("/api/quotes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(10));
    }

    @Test
    void randomQuoteReturnsQuote() throws Exception {
        mockMvc.perform(get("/api/quotes/random"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text").isNotEmpty())
                .andExpect(jsonPath("$.author").isNotEmpty());
    }

    @Test
    void getQuoteByIdReturnsCorrectQuote() throws Exception {
        mockMvc.perform(get("/api/quotes/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void getQuoteByUnknownIdReturns404() throws Exception {
        mockMvc.perform(get("/api/quotes/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void healthEndpointReturnsOk() throws Exception {
        mockMvc.perform(get("/health"))
                .andExpect(status().isOk())
                .andExpect(content().string("OK"));
    }
}
