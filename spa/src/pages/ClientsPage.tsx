import {Form, Table} from "react-bootstrap";
import {ChangeEvent, useEffect, useState} from "react";
import {ClientType} from "../types/Users";
import {clientsApi} from "../api/clientsApi";
import Client from "../components/Client/Client";
import {filter, includes} from "lodash";
import {useAccount} from "../hooks/useAccount";

function ClientsPage() {
    const [clients, setClients] = useState<ClientType[]>([]);
    const [filterId, setFilterId] = useState("");
    const {accountType, getCurrentAccount } = useAccount();

    useEffect(() => {
        clientsApi.getClients().then(async (response) => {
            setClients((await response).data)
        })
    }, []);

    return (
        <div>
            <Form.Group className="m-3" controlId="formId">
                <Form.Label>
                    Id:
                </Form.Label>
                <Form.Control
                    onChange={(event:ChangeEvent<HTMLInputElement>) => {
                        let value = event.target.value;
                        setFilterId(value);
                    }}
                    name="idFilter"
                    defaultValue=""
                >
                </Form.Control>
            </Form.Group>
            <Table striped hover>
                <thead>
                <tr>
                    <th>ID</th>
                    <th>Login</th>
                    <th>First name</th>
                    <th>Last name</th>
                    <th>Client type</th>
                    <th>Status</th>
                    <th>Actions</th>
                </tr>
                </thead>
                <tbody>
                {filterId!=="" && filter(clients,(client:ClientType)=>{return includes(client.id, filterId)}).map((client)=> (
                    <Client key={client.id} client={client} clients={clients} setClients={setClients} accountType={accountType}/>
                ))}
                {filterId==="" && clients.map((client)=> (
                    <Client key={client.id} client={client} clients={clients} setClients={setClients} accountType={accountType}/>
                ))}
                </tbody>
            </Table>
        </div>
    )
}

export default ClientsPage