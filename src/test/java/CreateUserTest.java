import io.qameta.allure.Description;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import java.util.*;

public class CreateUserTest {
    private static final Logger logger = LogManager.getLogger(CreateUserTest.class);

    @Parameters({"appUrl"})
    @Test(threadPoolSize = 3, invocationCount = 1)
    @Description("Test of the SUCCESSFUL creation of a new user")
    @Severity(SeverityLevel.NORMAL)
    public void testCreateUserSuccessfully(String appUrl) throws Exception {
        String editor = "supervisor";
        String url = appUrl + "/get/all";
        ArrayList<String> onlyIds = HttpClientFunctions.getIds(url);
        for (String id : onlyIds){
            if(HttpClientFunctions.checkRole(id, editor)){
                Assert.assertEquals(HttpClientFunctions.createUser(editor, 25, "male", "test5", "testtest4", "admin", "test5"), 200);
                logger.info("This test has successfully created a user");
                break;
            }
        }
    }

    @Parameters({"appUrl"})
    @Test(threadPoolSize = 3, invocationCount = 1)
    @Description("Test of the UNSUCCESSFUL creation of a new user")
    @Severity(SeverityLevel.NORMAL)
    public void testCreateUserUnsuccessfully(String appUrl) throws Exception {
        String editor = "supervisor";
        String url = appUrl + "/get/all";
        ArrayList<String> onlyIds = HttpClientFunctions.getIds(url);
        for (String id : onlyIds){
            if(HttpClientFunctions.checkRole(id, editor)){
                try{
                    HttpClientFunctions.createUser(editor, 2, "male", "test5", "testtest4", "admin", "test5");
                } catch (Exception e) {
                    Assert.assertTrue(e.getMessage().contains("User should be older than 16 and younger than 60 years old"));
                    logger.error("This test has successfully thrown an exception");
                    break;
                }
            }
        }
    }
}
