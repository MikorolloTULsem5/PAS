package basicClassesTests;

import nbd.gV.exceptions.ClientException;
import nbd.gV.exceptions.CourtException;
import nbd.gV.exceptions.JakartaException;
import nbd.gV.exceptions.MainException;
import nbd.gV.exceptions.MyMongoException;
import nbd.gV.exceptions.RepositoryException;
import nbd.gV.exceptions.ReservationException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ExceptionsTest {

    @Test
    void testMainException() {
        RuntimeException mainException = new MainException("TEST");
        assertThrows(MainException.class, () -> {throw mainException;});
        assertEquals("TEST", mainException.getMessage());
    }

    @Test
    void testClientException() {
        RuntimeException clientException = new ClientException("TEST");
        assertThrows(ClientException.class, () -> {throw clientException;});
        assertEquals("TEST", clientException.getMessage());
    }

    @Test
    void testCourtException() {
        RuntimeException courtException = new CourtException("TEST");
        assertThrows(CourtException.class, () -> {throw courtException;});
        assertEquals("TEST", courtException.getMessage());
    }

    @Test
    void testRepositoryException() {
        RuntimeException repositoryException = new RepositoryException("TEST");
        assertThrows(RepositoryException.class, () -> {throw repositoryException;});
        assertEquals("TEST", repositoryException.getMessage());
    }

    @Test
    void testReservationException() {
        RuntimeException reservationException = new ReservationException("TEST");
        assertThrows(ReservationException.class, () -> {throw reservationException;});
        assertEquals("TEST", reservationException.getMessage());
    }

    @Test
    void testJakartaException() {
        RuntimeException jakartaException = new JakartaException("TEST");
        assertThrows(JakartaException.class, () -> {throw jakartaException;});
        assertEquals("TEST", jakartaException.getMessage());
    }

    @Test
    void testMyMongoException() {
        RuntimeException myMongoException = new MyMongoException("TEST");
        assertThrows(MyMongoException.class, () -> {throw myMongoException;});
        assertEquals("TEST", myMongoException.getMessage());
    }
}
