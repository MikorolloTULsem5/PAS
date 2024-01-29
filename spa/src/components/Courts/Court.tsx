import React, {useRef, useState} from "react";
import {CourtType} from "../../types/ReservationsTypes";
import ModalBasic from "../Modal/ModalBasic";
import {Button, Form} from "react-bootstrap";
import {capitalize} from "lodash";
import {Formik, FormikProps, FormikValues} from "formik";
import * as yup from "yup";
import ConfirmModal from "../Modal/ConfirmModal";
import {reservationsApi} from "../../api/reservationsApi";
import {courtsApi} from "../../api/courtsApi";

interface CourtProps {
    court: CourtType
}

function Court({court}: CourtProps) {
    const [courtCopy,setCourtCopy] = useState(court);
    const [showReservationModal,setShowReservationModal] = useState(false);
    const formRef = useRef<FormikProps<FormikValues>>(null);
    const [showErrorModal,setShowErrorModal] = useState(false);
    const [errorModalContent,setErrorModalContent] = useState<string>("");

    const schema = yup.object().shape({
        clientId: yup.string().required()
    });

    const handleSubmit = () => {
        if (formRef.current) {
            formRef.current.handleSubmit()
        }
    }

    const rentCourt = (form:FormikValues) => {
        reservationsApi.create({courtId:court.id, clientId:form.clientId}).then(() =>{
            courtsApi.getCourtById(court.id).then((response)=>setCourtCopy(response.data)).catch(console.log);
        }).catch((error)=>{setErrorModalContent(JSON.stringify(error.response.data)); setShowErrorModal(true)})
    }

    return (
        <tr>
            <ModalBasic show={showErrorModal} setShow={setShowErrorModal} title={"Modification Error"} body={errorModalContent} footer={""}/>
            <td>{court.id}</td>
            <td>{court.courtNumber}</td>
            <td>{court.area}</td>
            <td>{court.baseCost}</td>
            {courtCopy.rented && <td>Yes</td>}
            {!courtCopy.rented && <td>No</td>}
            {!court.archive && <td>Active</td>}
            {court.archive && <td>Archived</td>}
            {court.archive || courtCopy.rented && <td/>}
            {!court.archive && !courtCopy.rented && <td>
                <Button variant="primary" onClick={()=>setShowReservationModal(true)}>Reserve</Button>
                <ModalBasic show={showReservationModal}
                            setShow={setShowReservationModal}
                            title="Reserve a court" body={
                    <Formik innerRef={formRef}
                            validationSchema={schema}
                            initialValues={{clientId: ''}}
                            onSubmit={rentCourt}>{props => (
                    <Form noValidate onSubmit={props.handleSubmit}>
                        <Form.Group controlId="reserveFormClientId">
                            <Form.Label>Client id:</Form.Label>
                            <Form.Control onChange={props.handleChange} onBlur={props.handleBlur}
                                          value={props.values.clientId}
                                          isInvalid={props.touched.clientId && !!props.errors.clientId}
                                          isValid={props.touched.clientId && !props.errors.clientId} name="clientId"
                                          type="text"/>
                            <Form.Control.Feedback
                                type="invalid">{capitalize(props.errors.clientId?.toString())}</Form.Control.Feedback>
                        </Form.Group>
                    </Form>
                )}</Formik>}
                            footer={<ConfirmModal variant={"primary"} onConfirm={handleSubmit} title="Reserve a court" body={<h2>Do you want to reserve a court: {court.id}</h2>}>Reserve</ConfirmModal>}/>
            </td>}
        </tr>
    )
}

export default Court;