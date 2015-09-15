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

import com.ejie.x38.dto.JerarquiaDto;

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
			
			//Metadatos
			boolean isJerarquia = dataSheet.isJerarquia();
			boolean isGrouping = dataSheet.isGrouping();
			JerarquiaMetadata jmd = dataSheet.getJerarquiaMetadada();
			
			//Hoja
			Sheet sheet = workbook.createSheet(dataSheet.getSheetName());
			
			//Cabeceras Hoja
			Row row = sheet.createRow(0);
			int cellNum = 0;
			int rowNum = 0;
			LinkedHashMap<String, String> dataHeader = (LinkedHashMap<String, String>) dataSheet.getHeaderNames();
			if (dataSheet.isShowHeaders()){
				//Columna para la agrupacion
				if (isGrouping){
					row.createCell(cellNum++).setCellValue("");
				}
				//Columna para marcar los elementos que cumplen filtro en Jerarquia
				if (isJerarquia && jmd.isShowFiltered()){ 
					if (!"".equals(jmd.getFilterHeaderName())){
						row.createCell(cellNum++).setCellValue(jmd.getFilterHeaderName());
					} else {
						row.createCell(cellNum++).setCellValue("");
					}
				}
				for (Map.Entry<String, String> entry : dataHeader.entrySet()) {
					//Si tiene agrupacion, no debe mostrarse la columna del grupo y es la columna del grupo comprobar => pasar al siguiente
					if (isGrouping && !dataSheet.isShowGroupColumng() && entry.getKey().equals(dataSheet.getGroupColumnName())){
						continue;
					}
					row.createCell(cellNum++).setCellValue(entry.getValue());
				}
				rowNum++;
			}
			
			//Datos Hoja
			List<Object> modelData;
			if (!isJerarquia){
				modelData = dataSheet.getModelData();
			} else {
				modelData = dataSheet.getModelDataJerarquia();
			}
			String prevGroupValue="", groupValue = "";
			for (Object object : modelData) {
				//Gestión filas-columnas
				row = sheet.createRow(rowNum++);
				cellNum = 0;
				
				//jerarquia
				int level = 0;
				boolean hasChildren = false;
				
				//Si tiene agrupación => comprobar el cambio de grupo
				if (isGrouping){
					groupValue = BeanUtils.getProperty(((JerarquiaDto)object).getModel(), dataSheet.getGroupColumnName());
					if (!groupValue.equals(prevGroupValue)){
						prevGroupValue = groupValue;
						row.createCell(cellNum++).setCellValue(groupValue);
						row = sheet.createRow(rowNum++);
						cellNum = 0;
					}
					row.createCell(cellNum++).setCellValue(""); //Espacio para evitar columna agrupación
				}
				//Si es jerarquia => comprobar si debe mostrar filtro y obtener atributo + parsear objeto de iteracion
				if (isJerarquia){
					if (jmd.isShowFiltered()){//Mostrar si cumple filtro
						if (((JerarquiaDto) object).isFilter()){ //Cumple filtro
							row.createCell(cellNum++).setCellValue(jmd.getFilterToken());
						} else {
							row.createCell(cellNum++).setCellValue("");
						}
					}
					level = ((JerarquiaDto) object).getLevel();
					hasChildren = ((JerarquiaDto) object).isHasChildren();
					object = ((JerarquiaDto)object).getModel(); //Obtener objeto de negocio
				}
				for (Map.Entry<String, String> entry : dataHeader.entrySet()) {
					//Si tiene agrupacion, no debe mostrarse la columna del grupo y es la columna del grupo comprobar => pasar al siguiente
					if (isGrouping && !dataSheet.isShowGroupColumng() && entry.getKey().equals(dataSheet.getGroupColumnName())){
						continue;
					}
					String columnValue = BeanUtils.getProperty(object, entry.getKey()); 
					//Si es Jerarquia...
					if (isJerarquia){
						//Si está activados los iconos y la columna es la indicada => aplicar iconos
						if (jmd.isShowIcon() && entry.getKey().equals(jmd.getIconColumnName())){  
							if (jmd.getIconCollapsedList().contains(BeanUtils.getProperty(object, jmd.getIconBeanAtribute()))){
								columnValue = jmd.getIconUnexpanded() + columnValue;
							} else {
								if (hasChildren){
									columnValue = jmd.getIconExpanded() + columnValue;
								} else {
									columnValue = jmd.getIconNoChild() + columnValue;
								}
							}
						}
						//Si está activada la tabulación y la columna es la indicada => aplicar tabulación
						if (jmd.isShowTabbed() && entry.getKey().equals(jmd.getTabColumnName())){  
							columnValue = jmd.getTab(level) + columnValue;
						}
					}
					row.createCell(cellNum++).setCellValue(columnValue);
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