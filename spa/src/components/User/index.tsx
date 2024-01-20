import {UserType} from "../../types/Users";
import {Dispatch, SetStateAction, useState} from "react";
import ModalBasic from "../ModalBasic";
import {Button} from "react-bootstrap";
import {clientsApi} from "../../api/clientsApi";
import {adminsApi} from "../../api/adminsApi";
import {resAdminsApi} from "../../api/resAdminsApi";
import modifyClientForm from "../forms/modifyClientForm";
import ModifyClientForm from "../forms/modifyClientForm";

interface UserProps {
    user: UserType
    users: UserType[]
    setUsers: Dispatch<SetStateAction<UserType[]>>
}

function User({user, users, setUsers}: UserProps) {
    const [showModal, setShowModal] = useState(false);

    const activate = async () => {
        try {
            switch (user.userType) {
                case "Client":
                    clientsApi.activate(user.id);
                    break;
                case "Admin":
                    adminsApi.activate(user.id);
                    break;
                case "Resource admin":
                    resAdminsApi.activate(user.id);
                    break;
            }
            user.archive = false;
        } catch (error) {
            console.log(error)
        }
    }

    const deactivate = async () => {
        try {
            switch (user.userType) {
                case "Client":
                    clientsApi.deactivate(user.id);
                    break;
                case "Admin":
                    adminsApi.deactivate(user.id);
                    break;
                case "Resource admin":
                    resAdminsApi.deactivate(user.id);
                    break;
            }
            user.archive = true;
        } catch (error) {
            console.log(error)
        }
    }

    return (
        <tr>
            <td>{user.id}</td>
            <td>{user.login}</td>
            <td>{user.userType}</td>
            {!user.archive && <td>Active</td>}
            {!user.archive &&
                <td>
                    <Button onClick={() => setShowModal(true)} variant="primary">Archive</Button>
                    <ModalBasic show={showModal} setShow={setShowModal}
                                title='Archive user'
                                body={<h2>Are you sure you want to archive user: {user.id} ?</h2>}
                                footer={
                                    <div>
                                        <Button variant='success' onClick={() => {
                                            deactivate()
                                            setShowModal(false);
                                        }}>Yes</Button>
                                        <Button variant='danger' onClick={() => setShowModal(false)}>No</Button>
                                    </div>
                                }/>
                </td>
            }
            {user.archive && <td>Archived</td>}
            {user.archive &&
                <td>
                    <Button onClick={() => setShowModal(true)} variant="primary">Activate</Button>
                    <ModalBasic show={showModal} setShow={setShowModal}
                                title='Archive user'
                                body={<h2>Are you sure you want to activate user: {user.id} ?</h2>}
                                footer={
                                    <div>
                                        <Button variant='success' onClick={() => {
                                            activate();
                                            setShowModal(false);
                                        }}>Yes</Button>
                                        <Button variant='danger' onClick={() => setShowModal(false)}>No</Button>
                                    </div>
                                }/>
                </td>
            }
            <td>
                <Button onClick={() => setShowModal(true)} variant="primary">Modify</Button>
                <ModalBasic show={showModal} setShow={setShowModal}
                            title='Modify user'
                            body={<ModifyClientForm/>}
                            footer={
                                <div>

                                </div>
                            }/>
            </td>
        </tr>
    )
}

export default User;