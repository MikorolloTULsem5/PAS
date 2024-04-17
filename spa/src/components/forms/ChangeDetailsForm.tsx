import React, {useEffect, useState} from 'react';
import {Button, Form} from "react-bootstrap";
import * as formik from 'formik';
import * as yup from 'yup';
import {capitalize} from "lodash";
import ModalBasic from "../Modal/ModalBasic";
import {useAccount} from "../../hooks/useAccount";
import {FormikHelpers} from "formik";
import {clientsApi} from "../../api/clientsApi";

function ChangeDetailsForm() {
    const { account, getCurrentAccount } = useAccount();
    const [showErrorModal,setShowErrorModal] = useState(false);
    const [errorModalContent,setErrorModalContent] = useState<string>("");
    const [changedDetails, setChangedDetails] = useState(false);
    // @ts-ignore
    const [details, setDetails] = useState<{firstName:string, lastName: string}>({firstName:account.firstName,lastName:account.lastName})
    const {Formik} = formik;
    const schemaUser = yup.object().shape({
        firstName: yup.string().required(),
        lastName: yup.string().required()
    });

    useEffect(() => {
        // @ts-ignore
        setDetails({firstName:account?.firstName, lastName:account?.lastName})
    }, [account]);

    interface formResult{
        firstName:string,
        lastName:string
    }

    const handleSubmit = (values:formResult, formikHelpers : FormikHelpers<formResult>) =>{
        setChangedDetails(false);
        // @ts-ignore
        clientsApi.changeDetails({...account, firstName: values.firstName, lastName:values.lastName})
            .then(()=>{setChangedDetails(true)})
            .catch((error) =>{
            setErrorModalContent(JSON.stringify(error.response.data));
            setShowErrorModal(true)
        });
        formikHelpers.setSubmitting(false);
    }

    return (
        <div>
            <ModalBasic show={showErrorModal} setShow={setShowErrorModal} title={"Modification Error"} body={errorModalContent} footer={""}/>
            <Formik
                validationSchema={schemaUser}
                initialValues={{
                    firstName: details.firstName,
                    lastName: details.lastName
                }}
                onSubmit={handleSubmit}
            >{props => (
                <Form onSubmit={props.handleSubmit} noValidate className="m-3 p-3">
                    <Form.Group controlId="changeDetailsFormFirstName" >
                        <Form.Label>First name:</Form.Label>
                        <Form.Control onChange={props.handleChange} onBlur={props.handleBlur}
                                      value={props.values.firstName}
                                      isInvalid={props.touched.firstName && !!props.errors.firstName}
                                      isValid={props.touched.firstName && !props.errors.firstName} name="firstName"
                                      type="text"/>
                        <Form.Control.Feedback
                            type="invalid">{capitalize(props.errors.firstName?.toString())}</Form.Control.Feedback>
                    </Form.Group>
                    <Form.Group controlId="changeDetailsFormLastName">
                        <Form.Label>Last name:</Form.Label>
                        <Form.Control onChange={props.handleChange} onBlur={props.handleBlur}
                                      value={props.values.lastName}
                                      isInvalid={props.touched.lastName && !!props.errors.lastName}
                                      isValid={props.touched.lastName && !props.errors.lastName} name="lastName"
                                      type="text"/>
                        <Form.Control.Feedback
                            type="invalid">{capitalize(props.errors.lastName?.toString())}</Form.Control.Feedback>
                    </Form.Group>
                    <Button variant="success" type="submit" className="mt-3">Change</Button>
                </Form>
            )}</Formik>
            {changedDetails && <a>Changed details!</a>}
        </div>
    );
}

export default ChangeDetailsForm;
