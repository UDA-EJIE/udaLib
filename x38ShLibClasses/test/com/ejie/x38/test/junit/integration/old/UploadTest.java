package com.ejie.x38.test.junit.integration.old;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.mock.web.MockServletContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.ejie.x38.IframeXHREmulationFilter;
import com.ejie.x38.UdaFilter;
import com.ejie.x38.test.control.UploadController;
import com.ejie.x38.test.junit.integration.TestConfig;


@WebAppConfiguration
@ContextConfiguration(classes={TestConfig.class})

@RunWith(SpringJUnit4ClassRunner.class)
public class UploadTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;
    
    @Autowired
    private UploadController uploadController;
    
    
    @Autowired
    private IframeXHREmulationFilter iframeXHREmulationFilter;
    
    @Autowired
    private UdaFilter udaFilter;
    
    @Before
    public void setUp() {
    	
    	((MockServletContext)webApplicationContext.getServletContext()).setInitParameter("webAppName", "x21a");
    	((MockServletContext)webApplicationContext.getServletContext()).setInitParameter("contextConfigLocation", "classpath:app-config.xml");
    	((MockServletContext)webApplicationContext.getServletContext()).setContextPath("x21aAppWar");
//        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    	mockMvc = MockMvcBuilders.standaloneSetup(uploadController)
    			.addFilter(udaFilter, "/*")
//    			.addInterceptors(interceptors)
    			.addFilter(iframeXHREmulationFilter, "/*")
    			.build();
    }
    
    @Test
    public void test() throws Exception {
    	
    	
           
        mockMvc.perform(get("/patrones/test")
        		
        		
        		)
                    .andExpect(status().is(200))
                    .andExpect(content().string("{\"respuesta\":\"ok\"}"))
                    ;
    }
    
    @Test
    public void testSubidaArchivos() throws Exception {
    	
    	MockMultipartFile file = new MockMultipartFile("fotoPadre", "filename.txt", null, "bar".getBytes());
           
    	
    	mockMvc.perform(MockMvcRequestBuilders.fileUpload("/patrones/form/subidaArchivos")
                .file(file)                
                .param("apellido1", "Apellido 1"))
            .andExpect(status().is(200))
            .andExpect(content().string("[\"Las entidades se han enviado correctamente\"]"));
    	
  
    }
    
//    @Test
//    public void test() throws Exception {
//    	
//
//        MockMultipartFile firstFile = new MockMultipartFile("data", "filename.txt", "text/plain", "some xml".getBytes());
//        MockMultipartFile secondFile = new MockMultipartFile("data", "other-file-name.data", "text/plain", "some other type".getBytes());
//        MockMultipartFile jsonFile = new MockMultipartFile("json", "", "application/json", "{\"json\": \"someValue\"}".getBytes());
//
//        
//        mockMvc.perform(MockMvcRequestBuilders.fileUpload("/patrones/form/subidaArchivos")
//        				
//                        .file(firstFile)
//                        .file(secondFile).file(jsonFile)
//                        .param("some-random", "4"))
//                    .andExpect(status().is(200))
//                    .andExpect(content().string("success"));
//    }
}
