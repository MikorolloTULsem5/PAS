import {Table} from "react-bootstrap";
import {useEffect, useState} from "react";
import {courtsApi} from "../api/courtsApi";
import {CourtType} from "../types/ReservationsTypes";
import Court from "../components/Courts/Court";
import {useAccount} from "../hooks/useAccount";
import {AccountTypeEnum} from "../types/Users";
import client from "../components/Client/Client";

function CourtsPage() {
    const [courts, setCourts] = useState<CourtType[]>([])
    const [trigger, setTrigger] = useState(false);
    const {accountType, account, getCurrentAccount} = useAccount()

    useEffect(() => {
        courtsApi.getCourts().then((response) => setCourts(response.data))
    }, [trigger]);

    useEffect(() => {
        getCurrentAccount();
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
                    <Court key={court.id} court={court} accountType={accountType}
                           clientId={(accountType===AccountTypeEnum.CLIENT ? account?.id : undefined)}
                           trigger={trigger} setTrigger={setTrigger} />
                ))}
                </tbody>
            </Table>
        </div>
    )
}

export default CourtsPage