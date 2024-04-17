import React, {useEffect, useState} from 'react';
import {Button, Form} from "react-bootstrap";
import * as formik from 'formik';
import * as yup from 'yup';
import {capitalize} from "lodash";
import ModalBasic from "../Modal/ModalBasic";
import {useAccount} from "../../hooks/useAccount";
import {FormikHelpers} from "formik";

function LoginForm() {
    const [showErrorModal,setShowErrorModal] = useState(false);
    const [errorModalContent,setErrorModalContent] = useState<string>("");
    const {Formik} = formik;
    const schemaUser = yup.object().shape({
        login: yup.string().required(),
        password: yup.string().required()
    });

    const { isAuthenticated, logIn, getCurrentAccount } = useAccount();
    useEffect(() => {
        getCurrentAccount();
    }, []);

    interface formResult{
        login:string,
        password:string
    }

    const handleSubmit = (values:formResult, formikHelpers : FormikHelpers<formResult>) =>{
        logIn(values.login, values.password).catch((error) =>{
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
                    login: '',
                    password: '',
                }}
                onSubmit={handleSubmit}
            >{props => (
                <Form onSubmit={props.handleSubmit} noValidate className="m-3 p-3">
                    <Form.Group controlId="addUserFormLogin" >
                        <Form.Label>Login:</Form.Label>
                        <Form.Control onChange={props.handleChange} onBlur={props.handleBlur}
                                      value={props.values.login}
                                      isInvalid={props.touched.login && !!props.errors.login}
                                      isValid={props.touched.login && !props.errors.login} name="login"
                                      type="text"/>
                        <Form.Control.Feedback
                            type="invalid">{capitalize(props.errors.login?.toString())}</Form.Control.Feedback>
                    </Form.Group>
                    <Form.Group controlId="addUserFormPassword">
                        <Form.Label>Password:</Form.Label>
                        <Form.Control onChange={props.handleChange} onBlur={props.handleBlur}
                                      value={props.values.password}
                                      isInvalid={props.touched.password && !!props.errors.password}
                                      isValid={props.touched.password && !props.errors.password} name="password"
                                      type="password"/>
                        <Form.Control.Feedback
                            type="invalid">{capitalize(props.errors.password?.toString())}</Form.Control.Feedback>
                    </Form.Group>
                    <Button variant="success" type="submit" className="mt-3">Log in</Button>
                </Form>
            )}</Formik>
        </div>
    );
}

export default LoginForm;
