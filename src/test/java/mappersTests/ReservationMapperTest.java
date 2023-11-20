package mappersTests;

import nbd.gV.clients.Client;
import nbd.gV.clients.clienttype.Normal;
import nbd.gV.courts.Court;
import nbd.gV.mappers.ClientMapper;
import nbd.gV.mappers.CourtMapper;
import nbd.gV.mappers.ReservationMapper;
import nbd.gV.reservations.Reservation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

public class ReservationMapperTest {
    UUID uuid = UUID.randomUUID();
    Client testClient;
    ClientMapper testClientMapper;
    Court testCourt;
    CourtMapper testCourtMapper;

    LocalDateTime testTimeStart;
    LocalDateTime testTimeEnd;

    @BeforeEach
    void setUp() {
        testClient = new Client("John", "Smith", "12345678901", new Normal());
        testClientMapper = ClientMapper.toMongoClient(testClient);

        testCourt = new Court(1000, 100, 1);
        testCourtMapper = CourtMapper.toMongoCourt(testCourt);

        testTimeStart = LocalDateTime.of(2023, Month.JUNE, 4, 12, 0);
        testTimeEnd = LocalDateTime.of(2023, Month.JUNE, 4, 15, 0);
    }

    @Test
    void testCreatingMapper() {
        ReservationMapper reservationMapper = new ReservationMapper(uuid.toString(),
                testClient.getClientId().toString(), testCourt.getCourtId().toString(), testTimeStart, testTimeEnd,
                200);
        assertNotNull(reservationMapper);

        assertEquals(uuid, UUID.fromString(reservationMapper.getId()));
        assertEquals(testClient.getClientId(), UUID.fromString(reservationMapper.getClientId()));
        assertEquals(testCourt.getCourtId(), UUID.fromString(reservationMapper.getCourtId()));
        assertEquals(testTimeStart, reservationMapper.getBeginTime());
        assertEquals(testTimeEnd, reservationMapper.getEndTime());
        assertEquals(200, reservationMapper.getReservationCost());
    }

    @Test
    void testToMongoClientMethod() {
        Reservation reservation = new Reservation(testClient, testCourt, testTimeStart);
        assertNotNull(reservation);

        ReservationMapper reservationMapper = ReservationMapper.toMongoReservation(reservation);
        assertNotNull(reservationMapper);

        assertEquals(reservation.getId(), UUID.fromString(reservationMapper.getId()));
        assertEquals(testClient.getClientId(), UUID.fromString(reservationMapper.getClientId()));
        assertEquals(testCourt.getCourtId(), UUID.fromString(reservationMapper.getCourtId()));
        assertEquals(testTimeStart, reservationMapper.getBeginTime());
        assertNull(reservationMapper.getEndTime());
        assertEquals(0, reservationMapper.getReservationCost());

        Reservation reservationEnded = new Reservation(testClient, testCourt, testTimeStart);
        assertNotNull(reservationEnded);
        reservationEnded.endReservation(testTimeEnd);

        ReservationMapper reservationMapperEnded = ReservationMapper.toMongoReservation(reservationEnded);
        assertNotNull(reservationMapperEnded);

        assertEquals(reservationEnded.getId(), UUID.fromString(reservationMapperEnded.getId()));
        assertEquals(testClient.getClientId(), UUID.fromString(reservationMapperEnded.getClientId()));
        assertEquals(testCourt.getCourtId(), UUID.fromString(reservationMapperEnded.getCourtId()));
        assertEquals(testTimeStart, reservationMapperEnded.getBeginTime());
        assertEquals(testTimeEnd, reservationMapperEnded.getEndTime());
        assertEquals(300, reservationMapperEnded.getReservationCost());
    }

    @Test
    void testFromMongoClientMethod() {
        ReservationMapper reservationMapper = new ReservationMapper(uuid.toString(),
                testClient.getClientId().toString(), testCourt.getCourtId().toString(), testTimeStart, null,
                0);
        assertNotNull(reservationMapper);

        Reservation reservation = ReservationMapper.fromMongoReservation(reservationMapper,
                ClientMapper.toMongoClient(testClient), CourtMapper.toMongoCourt(testCourt));
        assertNotNull(reservation);

        assertEquals(uuid, reservation.getId());
        assertEquals(testClient.getClientId(), reservation.getClient().getClientId());
        assertEquals(testCourt.getCourtId(), reservation.getCourt().getCourtId());
        assertEquals(testTimeStart, reservation.getBeginTime());
        assertNull(reservation.getEndTime());
        assertEquals(0, reservation.getReservationCost());


        ReservationMapper reservationMapperEnded = new ReservationMapper(uuid.toString(),
                testClient.getClientId().toString(), testCourt.getCourtId().toString(), testTimeStart, testTimeEnd,
                300);
        assertNotNull(reservationMapperEnded);

        Reservation reservationEnded = ReservationMapper.fromMongoReservation(reservationMapperEnded,
                ClientMapper.toMongoClient(testClient), CourtMapper.toMongoCourt(testCourt));
        assertNotNull(reservationEnded);

        assertEquals(UUID.fromString(reservationMapperEnded.getId()), reservationEnded.getId());
        assertEquals(testClient.getClientId(), reservationEnded.getClient().getClientId());
        assertEquals(testCourt.getCourtId(), reservationEnded.getCourt().getCourtId());
        assertEquals(testTimeStart, reservationEnded.getBeginTime());
        assertEquals(testTimeEnd, reservationEnded.getEndTime());
        assertEquals(300, reservationEnded.getReservationCost());
    }
}
