package basicClassesTests;

import nbd.gV.clients.clienttype.Athlete;
import nbd.gV.clients.clienttype.ClientType;
import nbd.gV.clients.clienttype.Coach;
import nbd.gV.clients.clienttype.Normal;
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
        assertEquals("Maksymalna liczba godzin rezerwacji dla typu Normal wynosi 3%n".formatted(), type.getTypeInfo());
    }

    @Test
    void testAthleteType() {
        ClientType type = new Athlete();
        assertNotNull(type);
        assertEquals(10, type.applyDiscount(100));
        assertEquals(6, type.getMaxHours());
        assertEquals("Athlete", type.getClientTypeName());
        assertEquals("Maksymalna liczba godzin rezerwacji dla typu Athlete wynosi 6%n".formatted(), type.getTypeInfo());
    }

    @Test
    void testCoachType() {
        ClientType type = new Coach();
        assertNotNull(type);
        assertEquals(15, type.applyDiscount(100));
        assertEquals(12, type.getMaxHours());
        assertEquals("Coach", type.getClientTypeName());
        assertEquals("Maksymalna liczba godzin rezerwacji dla typu Coach wynosi 12%n".formatted(), type.getTypeInfo());
    }
}
