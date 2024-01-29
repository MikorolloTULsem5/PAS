import {Form, Table} from "react-bootstrap";
import {ChangeEvent, useEffect, useState} from "react";
import {ClientType, UserType} from "../types/Users";
import User from "../components/User";
import {usersApi} from "../api/userApi";
import AddUserForm from "../components/forms/AddUserForm";
import {clientsApi} from "../api/clientsApi";
import Client from "../components/Client/Client";
import {filter, includes, indexOf} from "lodash";
import {ReservationType} from "../types/ReservationsTypes";
import reservation from "../components/Reservation/Reservation";
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