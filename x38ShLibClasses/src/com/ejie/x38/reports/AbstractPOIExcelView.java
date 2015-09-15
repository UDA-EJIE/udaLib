package com.ejie.x38.reports;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.web.servlet.view.AbstractView;

/* https://jira.springsource.org/browse/SPR-6898 */
public abstract class AbstractPOIExcelView extends AbstractView {

    /** The content type for an Excel response */
    private static final String CONTENT_TYPE_XLSX = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
    private static final String CONTENT_TYPE_XLS = "application/vnd.ms-excel";

    /**
     * Default Constructor. Sets the content type of the view for excel files.
     */
    public AbstractPOIExcelView() {
    }

    @Override
    protected boolean generatesDownloadContent() {
        return true;
    }

    /**
     * Renders the Excel view, given the specified model.
     */
    @Override
    protected final void renderMergedOutputModel(Map<String, Object> model, HttpServletRequest request,
            HttpServletResponse response) throws Exception {

        Workbook workbook = createWorkbook();

        if (workbook instanceof HSSFWorkbook) {
        	setContentType(CONTENT_TYPE_XLS);
        } else {
        	setContentType(CONTENT_TYPE_XLSX);
        }

        buildExcelDocument(model, workbook, request, response);

        // Set the content type.
        response.setContentType(getContentType());

        // Flush byte array to servlet output stream.
        ServletOutputStream out = response.getOutputStream();
        out.flush();
        workbook.write(out);
        out.flush();

        // Don't close the stream - we didn't open it, so let the container
        // handle it.
        // http://stackoverflow.com/questions/1829784/should-i-close-the-servlet-outputstream
    }

    /**
     * Subclasses must implement this method to create an Excel Workbook.
     * HSSFWorkbook and XSSFWorkbook are both possible formats.
     */
    protected abstract Workbook createWorkbook();

    
    /**
     * Subclasses must implement this method to create an Excel Workbook.
     * HSSFWorkbook and XSSFWorkbook are both possible formats.
     */
    protected abstract String getFileExtension();
    
    /**
     * Subclasses must implement this method to create an Excel HSSFWorkbook
     * document, given the model.
     * 
     * @param model
     *            the model Map
     * @param workbook
     *            the Excel workbook to complete
     * @param request
     *            in case we need locale etc. Shouldn't look at attributes.
     * @param response
     *            in case we need to set cookies. Shouldn't write to it.
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
	protected void buildExcelDocument(Map<String, Object> model, Workbook workbook,
            HttpServletRequest request, HttpServletResponse response) throws Exception {
    	
    	//Nombre Fichero
    	response.setHeader("Content-Disposition", "attachment; filename=\"" + model.get("fileName") + getFileExtension() + "\"");
		
		//Procesar Hojas
		List<ReportData> reportData = (List<ReportData>) model.get("reportData");
		for (ReportData dataSheet : reportData) {
			
			//Hoja
			Sheet sheet = workbook.createSheet(dataSheet.getSheetName());
			
			//Cabeceras Hoja
			Row row = sheet.createRow(0);
			int cellNum = 0;
			int rowNum = 0;
			LinkedHashMap<String, String> dataHeader = (LinkedHashMap<String, String>) dataSheet.getHeaderNames();
			if (dataSheet.isShowHeaders()){
				for (Map.Entry<String, String> entry : dataHeader.entrySet()) {
					row.createCell(cellNum++).setCellValue(entry.getValue());
				}
				rowNum++;
			}
			
			//Datos Hoja
			List<Object> modelData = dataSheet.getModelData();
			for (Object object : modelData) {
				row = sheet.createRow(rowNum++);
				cellNum = 0;
				for (Map.Entry<String, String> entry : dataHeader.entrySet()) {
				    row.createCell(cellNum++).setCellValue(BeanUtils.getProperty(object, entry.getKey()));
				}
			}
			
			//Autoajustar tamaño columnas
			int columnNumber = dataHeader.size();
			for (int i = 0; i < columnNumber; i++) {
				sheet.autoSizeColumn(i); 
			}
		}
		
		//Gestión de cookie (determina el final de la generación del fichero)
		Cookie cookie = new Cookie("fileDownload", "true");
		cookie.setPath("/");
		response.addCookie(cookie); 
    }
}