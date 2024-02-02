import {AccountTypeEnum, ClientType} from "../../types/Users";
import React, {Dispatch, SetStateAction, useEffect, useRef, useState} from "react";
import ConfirmModal from "../Modal/ConfirmModal";
import {Formik, FormikProps, FormikValues} from "formik";
import * as yup from 'yup';
import {Button, Col, Form, Row, Table} from "react-bootstrap";
import {capitalize, indexOf, isEmpty} from "lodash";
import ModalBasic from "../Modal/ModalBasic";
import {clientsApi} from "../../api/clientsApi";
import {ReservationType} from "../../types/ReservationsTypes";
import {reservationsApi} from "../../api/reservationsApi";

interface ClientProps {
    client: ClientType,
    clients: ClientType[],
    setClients: Dispatch<SetStateAction<ClientType[]>>,
    accountType: AccountTypeEnum | null
}

function Client({client, clients, setClients,accountType}: ClientProps) {
    const [isModified, setIsModified] = useState(false);
    const [clientCopy, setClientCopy] = useState(client);
    const [showErrorModal,setShowErrorModal] = useState(false);
    const [errorModalContent,setErrorModalContent] = useState<string>("");
    const [reservations,setReservations] = useState<ReservationType[]>([]);


    const loginFormRef = useRef<FormikProps<FormikValues>>(null)
    const firstNameFormRef = useRef<FormikProps<FormikValues>>(null)
    const lastNameFormRef = useRef<FormikProps<FormikValues>>(null)
    const clientTypeNameFormRef = useRef<FormikProps<FormikValues>>(null)

    const loginSchema = yup.object().shape({
        login: yup.string().required(),
    });

    const firstNameSchema = yup.object().shape({
        firstName: yup.string().required()
    });

    const lastNameSchema = yup.object().shape({
        lastName: yup.string().required()
    });

    const clientTypeNameSchema = yup.object().shape({
        clientTypeName: yup.string().required()
    });

    const handleSubmit = async () => {
        //war never changes
        if (!loginFormRef.current || !firstNameFormRef.current || !lastNameFormRef.current || !clientTypeNameFormRef.current) return;
        await loginFormRef.current.validateForm();
        await firstNameFormRef.current.validateForm();
        await lastNameFormRef.current.validateForm();
        await clientTypeNameFormRef.current.validateForm();
        if (!isEmpty(loginFormRef.current.errors) || !isEmpty(firstNameFormRef.current.errors) ||
            !isEmpty(lastNameFormRef.current.errors) || !isEmpty(clientTypeNameFormRef.current.errors)) return;
        let clientPacket = {...clientCopy,login:loginFormRef.current.values.login,
            firstName:firstNameFormRef.current.values.firstName,
            lastName: lastNameFormRef.current.values.lastName,
            clientTypeName: clientTypeNameFormRef.current.values.clientTypeName}
        clientsApi.modify(clientPacket).then(()=>{
            setClientCopy(clientPacket);
            setIsModified(false);
        }).catch( (error) => {setErrorModalContent(JSON.stringify(error.response.data)); setShowErrorModal(true)});
    }

    useEffect(() => {
        let clientsCopy = [...clients];
        clientsCopy[indexOf(clientsCopy,client)] = clientCopy;
        setClients(clientsCopy);
    }, [clientCopy]);

    useEffect(() => {
        reservationsApi.getReservationsByClientId(client.id).then((response)=>{
            if(response.status === 200){
                setReservations(response.data);
            }
        }).catch(console.log)
    }, []);

    return (
        <tr>
            <ModalBasic show={showErrorModal} setShow={setShowErrorModal} title={"Modification Error"} body={errorModalContent} footer={""}/>
            <td>{clientCopy.id}</td>

            {!isModified && <td>{clientCopy.login}</td>}
            {isModified && <td>
                <Formik innerRef={loginFormRef}
                        validationSchema={loginSchema}
                        initialValues={{login: clientCopy.login}}
                        onSubmit={()=>{}}>{props => (
                    <Form noValidate onSubmit={props.handleSubmit}>
                        <Form.Group controlId="modifyClientFormLogin">
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

            {!isModified && <td>{clientCopy.firstName}</td>}
            {isModified && <td>
                <Formik innerRef={firstNameFormRef}
                        validationSchema={firstNameSchema}
                        initialValues={{firstName: clientCopy.firstName}}
                        onSubmit={()=>{}}>{props => (
                    <Form noValidate onSubmit={props.handleSubmit}>
                        <Form.Group controlId="searchFormUsername">
                            <Form.Control onChange={props.handleChange} onBlur={props.handleBlur}
                                          value={props.values.firstName}
                                          isInvalid={props.touched.firstName && !!props.errors.firstName}
                                          isValid={props.touched.firstName && !props.errors.firstName} name="firstName"
                                          type="text"/>
                            <Form.Control.Feedback
                                type="invalid">{capitalize(props.errors.firstName?.toString())}</Form.Control.Feedback>
                        </Form.Group>
                    </Form>
                )}</Formik>
            </td>}

            {!isModified && <td>{clientCopy.lastName}</td>}
            {isModified && <td>
                <Formik innerRef={lastNameFormRef}
                        validationSchema={lastNameSchema}
                        initialValues={{lastName: clientCopy.lastName}}
                        onSubmit={()=>{}}>{props => (
                    <Form noValidate onSubmit={props.handleSubmit}>
                        <Form.Group controlId="searchFormUsername">
                            <Form.Control onChange={props.handleChange} onBlur={props.handleBlur}
                                          value={props.values.lastName}
                                          isInvalid={props.touched.lastName && !!props.errors.lastName}
                                          isValid={props.touched.lastName && !props.errors.lastName} name="lastName"
                                          type="text"/>
                            <Form.Control.Feedback
                                type="invalid">{capitalize(props.errors.lastName?.toString())}</Form.Control.Feedback>
                        </Form.Group>
                    </Form>
                )}</Formik>
            </td>}

            {!isModified && <td>{capitalize(clientCopy.clientTypeName)}</td>}
            {isModified && <td>
            <Formik innerRef={clientTypeNameFormRef}
                    validationSchema={clientTypeNameSchema}
                    initialValues={{clientTypeName: clientCopy.clientTypeName}}
                    onSubmit={()=>{}}>{props => (
                <Form noValidate onSubmit={props.handleSubmit}>
                    <Form.Group controlId="modifyFormClientTypeName">
                        <Form.Select onChange={props.handleChange}
                                     value={props.values.clientTypeName}
                                     name="clientTypeName">
                            <option value="normal">Normal</option>
                            <option value="coach">Coach</option>
                            <option value="athlete">Athlete</option>
                        </Form.Select>
                        <Form.Control.Feedback
                            type="invalid">Error</Form.Control.Feedback>
                    </Form.Group>
                </Form>
            )}</Formik>
        </td>}

            {!clientCopy.archive && <td>Active</td>}
            {clientCopy.archive && <td>Archived</td>}
            <td>
                <Row>
                    <Col>
                        <ConfirmModal variant={"primary"} title={'Modify user'}
                                      body={
                                          <Table striped hover responsive>
                                              <thead>
                                              <tr>
                                                  <th>ID</th>
                                                  <th>Court number</th>
                                                  <th>Begin time</th>
                                                  <th>End time</th>
                                                  <th>Cost</th>
                                                  <th>Total time</th>
                                              </tr>
                                              </thead>
                                              <tbody>
                                              {reservations?.map((reservation)=>(
                                                  <tr key={reservation.id}>
                                                      <td className="text-nowrap">{reservation.id}</td>
                                                      <td>{reservation.court.courtNumber}</td>
                                                      <td>{reservation.beginTime}</td>
                                                      <td>{reservation.endTime}</td>
                                                      <td>{reservation.reservationCost}</td>
                                                      <td>{reservation.reservationHours}</td>
                                                  </tr>
                                                  ))}
                                              </tbody>
                                          </Table>
                                      }>Reservations</ConfirmModal>
                    </Col>

                    {!isModified && accountType===AccountTypeEnum.ADMIN &&
                        <Col><Button variant="primary" onClick={() => setIsModified(true)}>Edit</Button></Col>
                    }

                    {isModified &&
                        <Col>
                            <ConfirmModal variant={"success"} title={'Modify user'}
                                          body={<h2>Are you sure you want to modify user: {client.id} ?</h2>}
                                          onConfirm={handleSubmit}>Save</ConfirmModal>
                        </Col>
                    }

                    {isModified &&
                        <Col><Button variant="danger" onClick={() => setIsModified(false)}>Cancel</Button></Col>
                    }
                </Row>
            </td>
        </tr>
    )
}

export default Client;