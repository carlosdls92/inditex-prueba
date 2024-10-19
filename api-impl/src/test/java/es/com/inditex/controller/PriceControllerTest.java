package es.com.inditex.controller;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
class PriceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    String productId = "35455";
    String brandId = "1";

    @Test
    void test1() throws Exception {
        findPrice(LocalDateTime.of(2020, 6, 14, 0,0))
                .andExpect(jsonPath("$.id").value("1"))
        ;
    }

    @Test
    void test2() throws Exception {
        findPrice(LocalDateTime.of(2020, 6, 14, 16,0))
                .andExpect(jsonPath("$.id").value("2"))
        ;
    }

    @Test
    void test3() throws Exception {
        findPrice(LocalDateTime.of(2020, 6, 14, 21,0))
                .andExpect(jsonPath("$.id").value("1"))
        ;
    }

    @Test
    void test4() throws Exception {
        findPrice(LocalDateTime.of(2020, 6, 15, 10,0))
                .andExpect(jsonPath("$.id").value("3"))
        ;
    }

    @Test
    void test5() throws Exception {
        findPrice(LocalDateTime.of(2020, 6, 16, 21,0))
                .andExpect(jsonPath("$.id").value("4"))
        ;
    }

    public ResultActions findPrice(LocalDateTime applyDate) throws Exception {
        return mockMvc.perform(get("/prices/find-price")
                        .param("apply-date", applyDate.toString())
                        .param("product-id",productId)
                        .param("brand-id",brandId)
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andDo(print());
    }

}