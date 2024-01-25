import io.qameta.allure.Description;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import java.util.*;

public class GetUserByIdTest {
    private static final Logger logger = LogManager.getLogger(CreateUserTest.class);

    @Test(threadPoolSize = 3, invocationCount = 1)
    @Description("Test of the SUCCESSFUL finding of an existing user")
    @Severity(SeverityLevel.NORMAL)
    public void testGetUserByIdSuccessfully() throws Exception {
        String idToCheck = "1";
        Assert.assertEquals(HttpClientFunctions.checkId(idToCheck), 200);
        logger.info("This test has successfully created a user");
    }

    @Test(threadPoolSize = 3, invocationCount = 1)
    @Description("Test of the UNSUCCESSFUL finding of an existing user")
    @Severity(SeverityLevel.NORMAL)
    public void testGetUserByIdUnsuccessfully() throws Exception {
        String idToCheck = "2";
        try{
            HttpClientFunctions.checkId(idToCheck);
        } catch (Exception e) {
            Assert.assertTrue(e.getMessage().contains("User with such ID does not exist"));
            logger.error("This test has successfully thrown an exception");
        }
    }
}
