import {UserType} from "../../types/Users";
import React, {Dispatch, SetStateAction, useEffect, useRef, useState} from "react";
import ConfirmModal from "../Modal/ConfirmModal";
import {Formik, FormikProps, FormikValues} from "formik";
import * as yup from 'yup';
import {Button, Col, Form, Row} from "react-bootstrap";
import {capitalize, indexOf} from "lodash";
import {usersApi} from "../../api/userApi";
import ModalBasic from "../Modal/ModalBasic";
import {useAccount} from "../../hooks/useAccount";

interface UserProps {
    user: UserType
    users: UserType[]
    setUsers: Dispatch<SetStateAction<UserType[]>>
}

function User({user, users, setUsers}: UserProps) {
    const [isModified, setIsModified] = useState(false);
    const [userCopy, setUserCopy] = useState(user);
    const [showErrorModal,setShowErrorModal] = useState(false);
    const [errorModalContent,setErrorModalContent] = useState<string>("");
    const formRef = useRef<FormikProps<FormikValues>>(null);

    const schema = yup.object().shape({
        login: yup.string().required()
    });

    const activate = async () => {
        try {
            usersApi.activate(user);
            setUserCopy({...userCopy, archive: false})
        } catch (error) {
            console.log(error)
        }
    }

    const deactivate = async () => {
        try {
            usersApi.deactivate(user);
            setUserCopy({...userCopy, archive: true})
        } catch (error) {
            console.log(error)
        }
    }

    const handleSubmit = () => {
        if (formRef.current) {
            formRef.current.handleSubmit()
        }
    }

    const modifyUser = (changes:FormikValues) => {
        usersApi.modify({...user,login:changes.login})?.then(
            (response) =>{
                setUserCopy({...userCopy, login:changes.login})
                setIsModified(false);
            }
        ).catch( (error) => {setErrorModalContent(JSON.stringify(error.response.data)); setShowErrorModal(true)});
    }

    useEffect(() => {
        let usersCopy = [...users]
        usersCopy[indexOf(usersCopy,user)] = userCopy;
        setUsers(usersCopy);
    }, [userCopy]);



    return (
        <tr>
            <ModalBasic show={showErrorModal} setShow={setShowErrorModal} title={"Modification Error"} body={errorModalContent} footer={""}/>
            <td>{userCopy.id}</td>
            {!isModified && <td>{userCopy.login}</td>}
            {isModified && <td>
                <Formik innerRef={formRef}
                        validationSchema={schema}
                        initialValues={{login: userCopy.login}}
                        onSubmit={modifyUser}>{props => (
                    <Form noValidate onSubmit={props.handleSubmit}>
                        <Form.Group controlId="searchFormUsername">
                            <Form.Control onChange={props.handleChange} onBlur={props.handleBlur}
                                          value={props.values.login}
                                          isInvalid={props.touched.login && !!props.errors.login}
                                          isValid={props.touched.login && !props.errors.login} name="login"
                                          type="text"/>
                            <Form.Control.Feedback
                                type="invalid">{capitalize(props.errors.login?.toString())}</Form.Control.Feedback>
                        </Form.Group>
                    </Form>
                )}</Formik>
            </td>}
            <td>{userCopy.userType}</td>
            {!userCopy.archive && <td>Active</td>}
            {userCopy.archive && <td>Archived</td>}
            <td>
                <Row>
                    {!isModified &&
                        <Col><Button variant="primary" onClick={() => setIsModified(true)}>Edit</Button></Col>
                    }

                    {isModified &&
                        <Col>
                            <ConfirmModal variant={"success"} title={'Modify user'}
                                          body={<h2>Are you sure you want to modify user: {user.id} ?</h2>}
                                          onConfirm={handleSubmit}>Save</ConfirmModal>
                        </Col>
                    }

                    {isModified &&
                        <Col><Button variant="danger" onClick={() => setIsModified(false)}>Cancel</Button></Col>
                    }


                    {!userCopy.archive &&
                        <Col>
                            <ConfirmModal variant={"primary"} title={'Archive user'}
                                          body={<h2>Are you sure you want to archive user: {user.id} ?</h2>}
                                          onConfirm={deactivate}>Archive</ConfirmModal>
                        </Col>
                    }
                    {userCopy.archive &&
                        <Col>
                            <ConfirmModal variant={"primary"} title={'Activate user'}
                                          body={<h2>Are you sure you want to activate user: {user.id} ?</h2>}
                                          onConfirm={activate}>Activate</ConfirmModal>
                        </Col>
                    }
                </Row>
            </td>
        </tr>
    )
}

export default User;