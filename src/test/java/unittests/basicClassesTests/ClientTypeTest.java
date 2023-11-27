package unittests.basicClassesTests;

import nbd.gV.model.users.clienttype.Athlete;
import nbd.gV.model.users.clienttype.ClientType;
import nbd.gV.model.users.clienttype.Coach;
import nbd.gV.model.users.clienttype.Normal;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ClientTypeTest {

    @Test
    void testNormalType() {
        ClientType type = new Normal();
        assertNotNull(type);
        assertEquals(0, type.applyDiscount(100));
        assertEquals(3, type.getMaxHours());
        assertEquals("Normal", type.getClientTypeName());
       }

    @Test
    void testAthleteType() {
        ClientType type = new Athlete();
        assertNotNull(type);
        assertEquals(10, type.applyDiscount(100));
        assertEquals(6, type.getMaxHours());
        assertEquals("Athlete", type.getClientTypeName());
        }

    @Test
    void testCoachType() {
        ClientType type = new Coach();
        assertNotNull(type);
        assertEquals(15, type.applyDiscount(100));
        assertEquals(12, type.getMaxHours());
        assertEquals("Coach", type.getClientTypeName());
        }
}
