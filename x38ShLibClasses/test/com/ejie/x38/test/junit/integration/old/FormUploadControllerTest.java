package com.ejie.x38.test.junit.integration.old;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.ejie.x38.IframeXHREmulationFilter;
import com.ejie.x38.UdaFilter;
import com.ejie.x38.serialization.UdaMappingJackson2HttpMessageConverter;
import com.ejie.x38.test.common.model.Alumno;
import com.ejie.x38.test.common.model.Comarca;
import com.ejie.x38.test.common.model.Departamento;
import com.ejie.x38.test.common.utils.TestMessages;
import com.ejie.x38.test.junit.integration.config.X38TestingApplicationContext;
import com.ejie.x38.test.junit.integration.config.X38TestingContextLoader;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

//@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = X38TestingContextLoader.class, classes = {X38TestingApplicationContext.class})
//@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class,
//    DirtiesContextTestExecutionListener.class,
//    TransactionalTestExecutionListener.class})
public class FormUploadControllerTest {
	
	@Resource
    private WebApplicationContext webApplicationContext;
	
	private MockMvc mockMvc;
	
	
	@Autowired
    private IframeXHREmulationFilter iframeXHREmulationFilter;
    
    @Autowired
    private UdaFilter udaFilter;
	
    @Autowired
    private UdaMappingJackson2HttpMessageConverter udaMappingJackson2HttpMessageConverter;
    
	private ObjectMapper objectMapper;
	
//	@Before
    public void setUp() {
		
		this.objectMapper = udaMappingJackson2HttpMessageConverter.getObjectMapper();
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
        		.addFilters(udaFilter, iframeXHREmulationFilter)
//    			.addInterceptors(interceptors)
        		
//    			.addFilter(iframeXHREmulationFilter, "/*")
    			
                .build();
    }
	
	
	
//	@Test
    public void test() throws Exception {
    	
    	
           
        mockMvc.perform(get("/form/test")
        		
        		
        		)
                    .andExpect(status().is(200))
                    .andExpect(content().string("{\"respuesta\":\"ok\"}"))
                    ;
    }
	
//	@Test
    public void simple() throws Exception {
    	
    	
          
        
        String json = this.getAsJsonString(this.getAlumnoTestObject());
        
        mockMvc.perform(post("/form/simple")
        		
        		
        		.contentType(MediaType.APPLICATION_JSON)
        		.accept(MediaType.ALL)
        		.content(json)
        		)
                    .andExpect(status().is(200))
                    .andExpect(content().string(json))
                    ;
    }
	
//	@Test
    public void multientidad() throws Exception {
    	
        String json = this.getAsJsonString(this.getMultientidadTestObject());
        
        mockMvc.perform(post("/form/multientidad")
        		
        		
        		.contentType(MediaType.APPLICATION_JSON)
        		.accept(MediaType.ALL)
        		.content(json)
        		)
                    .andExpect(status().is(200))
                    .andExpect(content().string(json))
                    ;
    }
	
	
//	@Test
    public void multientidadesMismoTipo() throws Exception {
    	
		 
		String json = this.getAsJsonString(this.getMultientidadMismoTipo());
		
		mockMvc.perform(post("/form/multientidadesMismoTipo")
        		
        		
        		.contentType(MediaType.APPLICATION_JSON)
        		.accept(MediaType.ALL)
        		.header("X-Requested-With", "true")
        		.content(json)
        		)
                    .andExpect(status().is(200))
                    .andExpect(content().string(json))
                    ;
    	
  
    }
	
//	@Test
    public void testSubidaFicheros() throws Exception {
    	
		 
		byte[] bytes = new byte[1024 * 1024 * 10];
		MockMultipartFile mockFile = new MockMultipartFile("fotoPadre", "file1.txt", "text/plain", bytes); 
//    	MockMultipartFile mockFile = new MockMultipartFile("fotoPadre", ITUploadControllerTest.class.getResourceAsStream("image.jpg"));
            	   	
    	
    	mockMvc.perform(MockMvcRequestBuilders.fileUpload("/form/subidaArchivos")
                .file(mockFile)            
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .header("X-Requested-With", "true")
                .param("apellido1", "Apellido 1")
                .param("apellido2", "Apellido 2"))
            .andExpect(status().is(200))
            .andExpect(content().string(TestMessages.REQUEST_TEST_OK));
    	
  
    }
    
//	@Test
    public void testSubidaFicherosValidated() throws Exception {
    	
		 
		byte[] bytes = new byte[1024 * 1024 * 10];
		MockMultipartFile mockFile = new MockMultipartFile("fotoPadre", "file1.txt", "text/plain", bytes); 
//    	MockMultipartFile mockFile = new MockMultipartFile("fotoPadre", ITUploadControllerTest.class.getResourceAsStream("image.jpg"));
            	   	
    	
    	mockMvc.perform(MockMvcRequestBuilders.fileUpload("/form/subidaArchivos?_emulate_iframe_http_status=true")
                .file(mockFile)            
                .contentType(MediaType.MULTIPART_FORM_DATA)
                
    			.param("apellido2", "Apellido 2"))
            .andExpect(status().is(200))
            .andExpect(content().string(TestMessages.REQUEST_IFRAME_TEST_OK));
    }
	
//	@Test
    public void testSubidaFicherosIframe() throws Exception {
    	
		 
		byte[] bytes = new byte[1024 * 1024 * 10];
		MockMultipartFile mockFile = new MockMultipartFile("fotoPadre", "file1.txt", "text/plain", bytes); 
//    	MockMultipartFile mockFile = new MockMultipartFile("fotoPadre", ITUploadControllerTest.class.getResourceAsStream("image.jpg"));
            	   	
    	
    	mockMvc.perform(MockMvcRequestBuilders.fileUpload("/form/subidaArchivos?_emulate_iframe_http_status=true")
                .file(mockFile)            
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .param("apellido1", "Apellido 1")
    			.param("apellido2", "Apellido 2"))
            .andExpect(status().is(200))
            .andExpect(content().string(TestMessages.REQUEST_IFRAME_TEST_OK));
    }
	
	
	
    
    private String getAsJsonString(Object object) throws JsonProcessingException{
    	
        
        return this.objectMapper.writeValueAsString(object);
    	
    }
    
    private Alumno getAlumnoTestObject(){
    	

        Alumno alumno = new Alumno();
        
        alumno.setApellido1("appelido 1");
        alumno.setApellido2("appelido 2");
        
        return alumno;
    	
    }
    
    private Map<String, Object> getMultientidadTestObject(){
    	

    	Map<String, Object> mapRetorno = new HashMap<String, Object>();
        Alumno alumno = new Alumno();
        
        alumno.setApellido1("appelido 1");
        alumno.setApellido2("appelido 2");
        
        Departamento departamento = new Departamento();
        
        departamento.setCode(new BigDecimal(100));
        departamento.setDescEs("Informatica");
        departamento.setDescEs("Informatika");
        
        mapRetorno.put("alumno", alumno);
        mapRetorno.put("departamento", departamento);
        
        return mapRetorno;
    	
    }
    
    
    private Map<String, Object> getMultientidadMismoTipo(){
    	

    	Map<String, Object> mapRetorno = new HashMap<String, Object>();
        Comarca comarca1 = new Comarca();
        Comarca comarca2 = new Comarca();
        Comarca comarca3 = new Comarca();
        
        comarca1.setCode(new BigDecimal(1));
        comarca1.setDescEs("Comarca 1");
        comarca1.setDescEu("Comarca 1");
        
        comarca2.setCode(new BigDecimal(1));
        comarca2.setDescEs("Comarca 2");
        comarca2.setDescEu("Comarca 2");
        
        comarca3.setCode(new BigDecimal(1));
        comarca3.setDescEs("Comarca 3");
        comarca3.setDescEu("Comarca 3");
        
        Departamento departamento = new Departamento();
        
        departamento.setCode(new BigDecimal(100));
        departamento.setDescEs("Informatica");
        departamento.setDescEs("Informatika");
        
        mapRetorno.put("comarca1", comarca1);
        mapRetorno.put("comarca2", comarca2);
        mapRetorno.put("comarca3", comarca3);
        
        return mapRetorno;
    	
    }

}
