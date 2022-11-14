package rip.alpha.libraries.tests;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import rip.alpha.libraries.model.DataManager;
import rip.alpha.libraries.model.DomainContext;
import rip.alpha.libraries.model.LocalDataDomain;

import java.util.concurrent.ThreadLocalRandom;

public class LocalDataDomainTest extends DatabaseBackedTest {

    private static LocalDataDomain<String, TestData> localDomain;

    @BeforeAll
    public static void setUpDomain() {
        System.out.println("Loading local domain");
        DomainContext<String, TestData> context = DomainContext.<String, TestData>builder()
                .mongoDatabase(mongoDatabase)
                .creator(TestData::new)
                .valueClass(TestData.class)
                .keyClass(String.class)
                .namespace("test-data")
                .redissonClient(redissonClient)
                .keyFunction(TestData::getId)
                .build();
        localDomain = DataManager.getInstance().getOrCreateLocalDomain(context);
        System.out.println("Finished loading local domain");
    }

    public void loadingTest() {
        String key = "SomeData";

        ThreadLocalRandom random = ThreadLocalRandom.current();
        localDomain.loadDataSync(key);

        Assertions.assertTrue(localDomain.isDataLoaded("SomeData"));

        TestData data = localDomain.getData(key);
        double setD = random.nextDouble(-1E6, 1E6);
        int setI = random.nextInt((int) -1E6, (int) 1E6);
        String setStr = TestUtils.generateRandomString(10);
        data.setDoubleData(setD);
        data.setIntData(setI);
        data.setStringData(setStr);
        localDomain.unloadDataSync("SomeData");

        Assertions.assertFalse(localDomain.isDataLoaded("SomeData"));

        localDomain.loadDataSync("SomeData");

        Assertions.assertTrue(localDomain.isDataLoaded("SomeData"));

        TestData loadedData = localDomain.getData("SomeData");
        Assertions.assertEquals(data, loadedData);
    }

}
