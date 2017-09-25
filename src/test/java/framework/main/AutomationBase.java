package framework.main;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import framework.commonutils.PropertyFileRead;

public class AutomationBase {

	//static SelectBrowser selectBrowser = new SelectBrowser();
	public static PropertyFileRead propRead = new PropertyFileRead();
	//public static final Logger logger = Logger.getLogger(Logger.class.getName());

	public final Logger logger = Logger.getLogger(this.getClass().getSimpleName());
	
	String baseURL = "";

	public void setUp(String path, String sheetName, String url, String browser) {
		try {
			/***** START: Setup Excel file *****/
			if (!path.equalsIgnoreCase("") & !sheetName.equalsIgnoreCase("")) {
				framework.commonutils.ExcelUtils.setExcelFile(path, sheetName);
			}
			/* --> END: Setup Excel file */

			/***** START: Setup and config log4j property file *****/
			PropertyConfigurator.configure(aut.constants.ProjectConstant.LOG4J_PROPERTY_PATH);
			/* --> END: Setup and config log4j property file */

			/***** START: Browser Settings *****/
			if(browser.equalsIgnoreCase("") | browser.equals(null))	{
				browser = propRead.readPropertyFile("project.properties", "browser");
			}
			
			switch (browser) {
			case "api":
				logger.info("WebService API testing URI - "+url);
				break;
				
			default:
				
				break;
			}
			/***** --> END: Browser Settings *****/

			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @Function Description: This function will setup Application URL from setup.properties file
	 */

}