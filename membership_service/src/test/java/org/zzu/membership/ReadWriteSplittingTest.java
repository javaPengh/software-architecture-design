package org.zzu.membership;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class ReadWriteSplittingTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void readShouldGoToReplica() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/membership/rw/read/2"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void writeShouldGoToMaster() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/membership/rw/write")
                        .param("note", "rw-verify"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }
}

