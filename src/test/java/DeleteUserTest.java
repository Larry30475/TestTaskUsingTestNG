import io.qameta.allure.Description;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import java.util.*;

public class DeleteUserTest {
    private static final Logger logger = LogManager.getLogger(CreateUserTest.class);

    @Parameters({"appUrl"})
    @Test(threadPoolSize = 3, invocationCount = 1)
    @Description("Test of the SUCCESSFUL delete of an existing user")
    @Severity(SeverityLevel.NORMAL)
    public void testDeleteUserSuccessfully(String appUrl) throws Exception {
        String editor = "supervisor";
        String idToDelete = "1171852012";
        String url = appUrl + "/get/all";
        ArrayList<String> onlyIds = HttpClientFunctions.getIds(url);
        for (String id : onlyIds){
            if(HttpClientFunctions.checkIfSupervisor(id, editor)){
                Assert.assertEquals(HttpClientFunctions.deleteUser(idToDelete, editor), 204);
                logger.info("This test has successfully created a user");
                break;
            }
        }
    }

    @Parameters({"appUrl"})
    @Test(threadPoolSize = 3, invocationCount = 1)
    @Description("Test of the UNSUCCESSFUL delete of an existing user")
    @Severity(SeverityLevel.NORMAL)
    public void testDeleteUserUnsuccessfully(String appUrl) throws Exception {
        String editor = "supervisor";
        String url = appUrl + "/get/all";
        ArrayList<String> onlyIds = HttpClientFunctions.getIds(url);
        for (String id : onlyIds){
            if(HttpClientFunctions.checkIfSupervisor(id, editor)){
                try{
                    HttpClientFunctions.deleteUser("2", editor);
                } catch (Exception e) {
                    Assert.assertTrue(e.getMessage().contains("User with such ID does not exist"));
                    logger.error("This test has successfully thrown an exception");
                    break;
                }
            }
        }
    }
}
