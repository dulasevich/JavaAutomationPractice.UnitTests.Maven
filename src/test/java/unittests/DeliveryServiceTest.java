package unittests;

import delivery.DeliveryService;
import delivery.Dimension;
import delivery.ServiceLoad;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static delivery.Dimension.*;
import static delivery.ServiceLoad.*;
import static org.testng.Assert.assertEquals;

public class DeliveryServiceTest {

    @Test(groups = "positive", description = "Delivery Calculation", dataProvider = "positiveTestData")
    void calculateDeliveryCostPositiveTest(double distance, Dimension dimension, boolean isFragile, ServiceLoad serviceLoad, int expectedCost) {
        assertEquals(expectedCost, DeliveryService.calculateDeliveryCost(distance, dimension, isFragile, serviceLoad));
    }

    @Test(groups = "negative", description = "Distance negative", expectedExceptions = IllegalArgumentException.class,
            expectedExceptionsMessageRegExp = "Distance should be positive")
    void calculateNegativeDistanceTest() {
        DeliveryService.calculateDeliveryCost(-40, Dimension.BIG, true, ServiceLoad.NORMAL);
    }

    @Test(groups = "negative", description = "Distance zero")
    void calculateZeroDistanceTest() {
        try {
            DeliveryService.calculateDeliveryCost(0, Dimension.BIG, true, ServiceLoad.HIGHEST);
        } catch (IllegalArgumentException e) {
            assertEquals(e.getMessage(), "Delivery possible for some distance");
        }
    }

    @Test(groups = "negative", description = "Fragile big distance", expectedExceptions = UnsupportedOperationException.class,
            expectedExceptionsMessageRegExp = "Fragile cargo cannot be delivered for the distance above 30 km")
    void calculateFragileBigDistanceTest() {
        DeliveryService.calculateDeliveryCost(40, Dimension.BIG, true, ServiceLoad.INCREASED);
    }

    @Test(groups = "negative", description = "Null dimension", expectedExceptions = NullPointerException.class,
            expectedExceptionsMessageRegExp = "Dimension is null")
    void calculateNullDimensionTest() {
        DeliveryService.calculateDeliveryCost(10, null, true, ServiceLoad.HIGH);
    }

    @Test(groups = "negative", description = "Null service load")
    void calculateNullServiceLoadTest() {
        try {
            DeliveryService.calculateDeliveryCost(10, Dimension.BIG, true, null);
        } catch (NullPointerException e) {
            assertEquals(e.getMessage(), "Service load is null");
        }
    }

    @DataProvider
    public Object[][] positiveTestData() {
        return new Object[][] {
                {1.999, BIG, true, HIGHEST, 880}, // border distance 1, highest load, big dimension;
                {2, BIG, false, NORMAL, 400}, //distance 1, normal load, big dimension, min calculation
                {3, SMALL, false, INCREASED, 400}, // border distance 1, increased load, small dimension, min calculation {9, SMALL, true, NORMAL, 500", // border distance 2, normal load, small dimension
                {10, SMALL, true, INCREASED, 600}, // distance 2, increased load, small dimension
                {11, SMALL, true, INCREASED, 720}, // border distance 2, increased load, small dimension
                {29, BIG, false, HIGH, 560}, // border distance 3, high load, big dimension
                {30, SMALL, true, HIGHEST, 960}, //distance 3, highest load, small dimension
                {31, BIG, false, NORMAL, 500}, //distance 4, border distance 3, normal load, big dimension
                {1000, SMALL, false, HIGH, 560} // big distance 4, high load, small dimension
        };
    }
}
