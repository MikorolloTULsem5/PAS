import {Table} from "react-bootstrap";
import {useEffect, useState} from "react";
import {ReservationType} from "../types/ReservationsTypes";
import Reservation from "../components/Reservation/Reservation";
import {reservationsApi} from "../api/reservationsApi";

function ReservationsPage() {
    const [reservations, setReservations] = useState<ReservationType[]>([]);

    useEffect(() => {
        reservationsApi.getAllReservations().then((result)=>setReservations(result))
    }, []);

    return (
        <Table striped hover>
            <thead>
            <tr>
                <th>ID</th>
                <th>Court number</th>
                <th>User login</th>
                <th>Begin time</th>
                <th>End time</th>
                <th>Cost</th>
                <th>Total time</th>
                <th>Actions</th>
            </tr>
            </thead>
            <tbody>
            {reservations.length!==0 && reservations.map((reservation) => (
                    <Reservation key={reservation.id} reservation={reservation}></Reservation>
                )
            )}
            </tbody>
        </Table>
    )
}

export default ReservationsPage