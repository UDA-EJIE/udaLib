package com.ejie.x38.test.integration.old;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.ejie.x38.IframeXHREmulationFilter;
import com.ejie.x38.UdaFilter;
import com.ejie.x38.test.integration.config.X38TestingApplicationContext;
import com.ejie.x38.test.integration.config.X38TestingContextLoader;

//@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = X38TestingContextLoader.class, classes = {X38TestingApplicationContext.class})
//@ContextConfiguration(loader = WebContextLoader.class)
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class,
    DirtiesContextTestExecutionListener.class,
    TransactionalTestExecutionListener.class})
public class ITUploadControllerTest {
	
	@Resource
    private WebApplicationContext webApplicationContext;
	
	private MockMvc mockMvc;
	
	
	@Autowired
    private IframeXHREmulationFilter iframeXHREmulationFilter;
    
    @Autowired
    private UdaFilter udaFilter;
	
//	@Autowired
//  private UploadController uploadController;
	
//	@Before
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
        		.addFilter(udaFilter, "/*")
//    			.addInterceptors(interceptors)
    			.addFilter(iframeXHREmulationFilter, "/*")
                .build();
    }
	
//	@Test
    public void test() throws Exception {
    	
    	
           
        mockMvc.perform(get("/upload/test")
        		
        		
        		)
                    .andExpect(status().is(200))
                    .andExpect(content().string("{\"respuesta\":\"ok\"}"))
                    ;
    }
	
	
//	@Test
    public void testSubidaArchivos() throws Exception {
    	
		 
		byte[] bytes = new byte[1024 * 1024 * 10];
		MockMultipartFile mockFile = new MockMultipartFile("fotoPadre", "file1.txt", "text/plain", bytes); 
//    	MockMultipartFile mockFile = new MockMultipartFile("fotoPadre", ITUploadControllerTest.class.getResourceAsStream("image.jpg"));
            	   	
    	
    	mockMvc.perform(MockMvcRequestBuilders.fileUpload("/patrones/form/subidaArchivos")
                .file(mockFile)            
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .param("apellido1", "Apellido 1"))
            .andExpect(status().is(200))
            .andExpect(content().string("[\"Las entidades se han enviado correctamente\"]"));
    	
  
    }
	
//	@Test
    public void testSubidaArchivosIframe() throws Exception {
    	
		 
		byte[] bytes = new byte[1024 * 1024 * 10];
		MockMultipartFile mockFile = new MockMultipartFile("fotoPadre", "file1.txt", "text/plain", bytes); 
//    	MockMultipartFile mockFile = new MockMultipartFile("fotoPadre", ITUploadControllerTest.class.getResourceAsStream("image.jpg"));
            	   	
    	
    	mockMvc.perform(MockMvcRequestBuilders.fileUpload("/patrones/form/subidaArchivos?_emulate_iframe_http_status=true")
                .file(mockFile)            
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .param("apellido1", "Apellido 1"))
            .andExpect(status().is(200))
            .andExpect(content().string("<textarea status=\"200\" statusText=\"OK\">[\"Las entidades se han enviado correctamente\"]</textarea>"));
    	
  
    }

}
