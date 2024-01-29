import {Table} from "react-bootstrap";
import {useEffect, useState} from "react";
import {ClientType, UserType} from "../types/Users";
import User from "../components/User";
import {usersApi} from "../api/userApi";
import AddUserForm from "../components/forms/AddUserForm";
import {clientsApi} from "../api/clientsApi";
import Client from "../components/Client/Client";
import {courtsApi} from "../api/courtsApi";
import {CourtType} from "../types/ReservationsTypes";
import Court from "../components/Courts/Court";

function CourtsPage() {
    const [courts, setCourts] = useState<CourtType[]>([])

    useEffect(() => {
        courtsApi.getCourts().then((response) => setCourts(response.data))
    }, []);

    return (
        <div>
            <Table striped hover>
                <thead>
                <tr>
                    <th>ID</th>
                    <th>Court number</th>
                    <th>Area</th>
                    <th>Base cost</th>
                    <th>Rented</th>
                    <th>Status</th>
                    <th>Action</th>
                </tr>
                </thead>
                <tbody>
                {courts.map((court)=> (
                    <Court key={court.id} court={court}/>
                ))}
                </tbody>
            </Table>
        </div>
    )
}

export default CourtsPage