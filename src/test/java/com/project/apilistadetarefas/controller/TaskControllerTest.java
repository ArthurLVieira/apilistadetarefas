package com.project.apilistadetarefas.controller;

import com.project.apilistadetarefas.service.TaskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

class TaskControllerTest {

    private TaskService taskService;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        taskService = mock(TaskService.class);
        mockMvc = standaloneSetup(new TaskController(taskService)).build();
    }

    @Test
    void setCompleteReadsCompletionFromJsonBody() throws Exception {
        when(taskService.setCompletion(1L, true)).thenReturn(null);

        mockMvc.perform(patch("/api/tasks/1/completion")
                        .contentType(APPLICATION_JSON)
                        .content("{\"completed\":true}"))
                .andExpect(status().isOk());

        verify(taskService).setCompletion(1L, true);
    }

    @Test
    void setCompleteRejectsBodyWithoutCompletedField() throws Exception {
        mockMvc.perform(patch("/api/tasks/1/completion")
                        .contentType(APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }
}