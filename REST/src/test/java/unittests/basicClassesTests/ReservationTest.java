package unittests.basicClassesTests;

import pas.gV.model.logic.users.Client;
import pas.gV.model.logic.courts.Court;
import pas.gV.model.logic.reservations.Reservation;
import pas.gV.model.exceptions.ReservationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ReservationTest {
    Client testClient;
    String testClientType;
    Court testCourt;
    UUID testUUID;

    @BeforeEach
    void setUp() {
        testClientType = "normal";
        testClient = new Client(UUID.randomUUID(), "John", "Smith", "123456789", "123456789", testClientType);
        testCourt = new Court(null, 1, 100, 1);
        testUUID = UUID.randomUUID();
    }

    @Test
    void testCreatingReservation() {
        LocalDateTime now = LocalDateTime.of(2023, Month.JUNE, 3, 22, 15);
        assertNotNull(now);
        Reservation reservation = new Reservation(testUUID, testClient, testCourt, now);
        assertNotNull(reservation);

        assertEquals(testUUID, reservation.getId());
        assertEquals(testClient, reservation.getClient());
        assertEquals(testCourt, reservation.getCourt());
        assertEquals(now, reservation.getBeginTime());
        assertNull(reservation.getEndTime());
    }

    @Test
    void testCreatingReservationWithNullDate() {
        Reservation reservation = new Reservation(testUUID, testClient, testCourt, null);
        assertNotNull(reservation);
        LocalDateTime now = LocalDateTime.now();
        assertNotNull(now);

        assertEquals(testUUID, reservation.getId());
        assertEquals(testClient, reservation.getClient());
        assertEquals(testCourt, reservation.getCourt());
        assertEquals(0, Duration.between(reservation.getBeginTime(), now).getSeconds());
        assertNull(reservation.getEndTime());
    }

    @Test
    void testEndingReservation() {
        LocalDateTime now = LocalDateTime.of(2023, Month.JUNE, 3, 22, 15);
        LocalDateTime then = LocalDateTime.of(2023, Month.JUNE, 3, 20, 7);
        Reservation reservation = new Reservation(testUUID, testClient, testCourt, then);
        assertNotNull(reservation);

        assertEquals(0, reservation.getReservationHours());
        assertEquals(0, reservation.getReservationCost());
        assertNull(reservation.getEndTime());

        reservation.endReservation(now);

        assertEquals(3, reservation.getReservationHours());
        assertEquals(300, reservation.getReservationCost());
        assertNotNull(reservation.getEndTime());
        assertEquals(now, reservation.getEndTime());

        assertThrows(ReservationException.class, () ->
                reservation.endReservation(LocalDateTime.of(2023, Month.JUNE, 3, 20, 8)));
    }

    @Test
    void testEndingReservationWithNullDate() {
        LocalDateTime then = LocalDateTime.of(2023, Month.JUNE, 3, 20, 7);
        Reservation reservation = new Reservation(testUUID, testClient, testCourt, then);
        assertNotNull(reservation);

        assertNull(reservation.getEndTime());

        reservation.endReservation(null);
        LocalDateTime now = LocalDateTime.now();
        assertNotNull(reservation.getEndTime());

        assertNotNull(reservation.getEndTime());
        assertEquals(0, Duration.between(reservation.getEndTime(), now).getSeconds());

        assertThrows(ReservationException.class, () ->
                reservation.endReservation(LocalDateTime.of(2023, Month.JUNE, 3, 20, 8)));

        Court testCourt1 = new Court(null, 1, 100, 2);
        LocalDateTime earlier = LocalDateTime.of(2023, Month.JUNE, 2, 21, 10);
        Reservation reservation2 = new Reservation(UUID.randomUUID(), testClient, testCourt1, then);
        assertNotNull(reservation2);

        reservation2.endReservation(earlier);
        assertEquals(reservation2.getBeginTime(), reservation2.getEndTime());
    }

    @Test
    void testGettingReservationHours() {
        LocalDateTime beginTime = LocalDateTime.of(2023, Month.JUNE, 3, 20, 7);
        LocalDateTime endTimeSecs = LocalDateTime.of(2023, Month.JUNE, 3, 20, 7, 30);
        LocalDateTime endTimeOneMinute = LocalDateTime.of(2023, Month.JUNE, 3, 20, 8);
        LocalDateTime endTimeFullHours = LocalDateTime.of(2023, Month.JUNE, 3, 22, 7);
        LocalDateTime endTimeMixHoursMinutes = LocalDateTime.of(2023, Month.JUNE, 3, 22, 8);
        Court testCourt1 = new Court(null, 1, 100, 2);
        Court testCourt2 = new Court(null, 1, 100, 3);
        Court testCourt3 = new Court(null, 1, 100, 4);
        Court testCourt4 = new Court(null, 1, 100, 4);
        Reservation reservation1 = new Reservation(UUID.randomUUID(), testClient, testCourt, beginTime);
        assertNotNull(reservation1);
        Reservation reservation2 = new Reservation(UUID.randomUUID(), testClient, testCourt1, beginTime);
        assertNotNull(reservation2);
        Reservation reservation3 = new Reservation(UUID.randomUUID(), testClient, testCourt2, beginTime);
        assertNotNull(reservation3);
        Reservation reservation4 = new Reservation(UUID.randomUUID(), testClient, testCourt3, beginTime);
        assertNotNull(reservation4);
        Reservation reservation5 = new Reservation(UUID.randomUUID(), testClient, testCourt4, beginTime);
        assertNotNull(reservation5);

        assertEquals(0, reservation1.getReservationHours());
        assertEquals(0, reservation2.getReservationHours());
        assertEquals(0, reservation3.getReservationHours());
        assertEquals(0, reservation4.getReservationHours());

        reservation1.endReservation(beginTime);
        assertEquals(0, reservation1.getReservationHours());

        reservation2.endReservation(endTimeSecs);
        assertEquals(0, reservation2.getReservationHours());

        reservation3.endReservation(endTimeOneMinute);
        assertEquals(1, reservation3.getReservationHours());

        reservation4.endReservation(endTimeFullHours);
        assertEquals(2, reservation4.getReservationHours());

        reservation5.endReservation(endTimeMixHoursMinutes);
        assertEquals(3, reservation5.getReservationHours());
    }

    @Test
    void testExceedingReservationPermittedTime() {
        LocalDateTime now = LocalDateTime.of(2023, Month.JUNE, 3, 23, 15);
        LocalDateTime then = LocalDateTime.of(2023, Month.JUNE, 3, 19, 7);
        Reservation reservation = new Reservation(testUUID, testClient, testCourt, then);
        assertNotNull(reservation);

        reservation.endReservation(now);

        assertTrue(reservation.getReservationHours() > testClient.clientMaxHours());
        assertEquals(600, reservation.getReservationCost());
    }
}
