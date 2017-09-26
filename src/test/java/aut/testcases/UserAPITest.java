package aut.testcases;

import java.io.File;

import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.jayway.restassured.response.Response;
import com.relevantcodes.extentreports.ExtentReports;
import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;

import aut.constants.ExcelCellsConstant;
import framework.commonutils.ExcelUtils;
import framework.commonutils.PropertyFileRead;
import framework.commonutils.RestWebServiceUtil;
import framework.main.AutomationBase;

public class UserAPITest extends AutomationBase {
	String prjPath = PropertyFileRead.getProjectPath();
	String path = propRead.readPropertyFile("project.properties", "apiTestData");
	String sheetName = "users";
	String baseURL = propRead.readPropertyFile("project.properties", "uri");
	int excelRowNum = 1;
	
	Response response;
	
	ExtentReports report;
	ExtentTest testLog;

	@BeforeTest(groups = { "", "" })
	public void setUp() {
		try {
			logger.info("Starting setup to launch GitHub USER API testing");
			super.setUp(path, sheetName, baseURL, "api");
			logger.info("@BeforeTest: Setup done");
		} catch (Exception e) {
			e.printStackTrace();
			logger.info("@BeforeTest: Setup failed");
		}
	}

	@Test(groups = { "", "" })
	public void userAPIResponseTest() throws Exception {
		
		report = new ExtentReports(prjPath+"//src//test//java//resources//reports//report.html");
		report.loadConfig(new File(prjPath+"//src//test//java//framework//project-config//config.xml"));
		testLog = report.startTest("API Test Case-1", "Objective is to test the report");
		
		String restURL="";
		String execute, resourceURL, expectedStatusCode;
		int totalExcelRowNum;
		ExcelUtils.setExcelFile(path, sheetName);
		totalExcelRowNum = ExcelUtils.getNumberOfRows();
		logger.info("Total No. of Rows: " + totalExcelRowNum);
		try {
			for (int i = excelRowNum; i < totalExcelRowNum; i++) {

				execute = ExcelUtils.getCellData(i,	ExcelCellsConstant.UserAPISheet.COL_EXECUTE);

				if (execute.equalsIgnoreCase("Y")) {
					testLog.log(LogStatus.INFO, "<h6>SCENARIO - "+i+"</h6> "+ExcelUtils.getCellData(i,	ExcelCellsConstant.UserAPISheet.COL_SCENARIO));
					restURL = "";
					resourceURL = ExcelUtils.getCellData(i, ExcelCellsConstant.UserAPISheet.COL_URI);
					expectedStatusCode = ExcelUtils.getCellData(i, ExcelCellsConstant.UserAPISheet.COL_STATUS_CODE);
					restURL = baseURL + resourceURL;
					logger.info("Rest WebService API: "+restURL);
					testLog.log(LogStatus.UNKNOWN, "Rest WebService API: "+restURL);
					
					response = RestWebServiceUtil.GET(restURL);
					logger.info("API Response: "+response.asString());
					
					int actualStatusCode = RestWebServiceUtil.getStatusCode(response);
					
					Assert.assertEquals(actualStatusCode, Integer.parseInt(expectedStatusCode), "[FAILED] Status code mismatch. API not working: "+restURL);
					testLog.log(LogStatus.PASS,"Verify the Status Code", "Status Code: " +actualStatusCode);
					logger.info("[PASSED] Status Code: " +actualStatusCode);
					
					String jsonAsString = response.asString();
					System.out.println("\n"+jsonAsString);
					
					// if status code 200 i.e Success reponse validate json
					if(200 == actualStatusCode)	{
						
						// validate login key
						String exLogin = ExcelUtils.getCellData(i, ExcelCellsConstant.UserAPISheet.COL_LOGIN);
						String acLogin = response.path("login");
						Assert.assertEquals(acLogin, exLogin,"[FAILED] 'login' key value mismatched");
						testLog.log(LogStatus.PASS,"Verify the key:login", "login: " +acLogin);
						logger.info("[PASSED] 'login' key value matched "+acLogin);
						
						// validate id key
						String exID = ExcelUtils.getCellData(i, ExcelCellsConstant.UserAPISheet.COL_ID);
						int acID = response.path("id");
						Assert.assertEquals(acID, Integer.parseInt(exID),"[FAILED] 'id' key value mismatched");
						testLog.log(LogStatus.PASS,"Verify the key:id", "id: " +acID);
						logger.info("[PASSED] 'id' key value matched "+acID);				
						
						//LinkedHashMap<String, LinkedHashMap<String, Object>> jsonResponseFormater = new LinkedHashMap<String, LinkedHashMap<String, Object>>();
						//jsonResponseFormater = RestWebServiceUtil.formatJSONToMapString(response, "login");
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@AfterMethod
	public void tearDown(ITestResult result){
		if (result.getStatus()==ITestResult.FAILURE){
				System.out.println("Failed");
				testLog.log(LogStatus.FAIL, "SampleTestFailed","Verification failed: "+result.getAttributeNames());
		}
		
		report.endTest(testLog);
		report.flush();
	}
}
