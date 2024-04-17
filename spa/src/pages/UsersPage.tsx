import {Form, Table} from "react-bootstrap";
import {ChangeEvent, useEffect, useState} from "react";
import {UserType} from "../types/Users";
import User from "../components/User";
import {usersApi} from "../api/userApi";
import {filter, includes} from "lodash";

function UsersPage() {
    const [users, setUsers] = useState<UserType[]>([]);
    const [filterId, setFilterId] = useState("")

    useEffect(() => {
        usersApi.getUsers().then((getUsers) => setUsers(getUsers));
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
                    <th>Account type</th>
                    <th>Status</th>
                    <th>Actions</th>
                </tr>
                </thead>
                <tbody>
                {filterId!=="" && filter(users,(user:UserType)=>{return includes(user.id, filterId)}).map((user)=> (
                    <User key={user.id} user={user} users={users} setUsers={setUsers}/>
                ))}
                {filterId==="" && users.map((user)=> (
                    <User key={user.id} user={user} users={users} setUsers={setUsers}/>
                ))}
                </tbody>
            </Table>
        </div>
    )
}

export default UsersPage