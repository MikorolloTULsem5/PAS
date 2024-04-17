import React, {useEffect, useState} from 'react';
import {Button, Form} from "react-bootstrap";
import * as formik from 'formik';
import * as yup from 'yup';
import {capitalize} from "lodash";
import ModalBasic from "../Modal/ModalBasic";
import {useAccount} from "../../hooks/useAccount";
import {api} from "../../api/api";
import {FormikHelpers} from "formik";
import {useNavigate} from "react-router-dom";
import {PublicRoutes} from "../../router/routes";
import {Pathnames} from "../../router/pathnames";
function RegisterForm() {
    const [showErrorModal,setShowErrorModal] = useState(false);
    const [errorModalContent,setErrorModalContent] = useState<string>("");
    const navigate = useNavigate();
    const {Formik} = formik;
    const schemaUser = yup.object().shape({
        login: yup.string().required(),
        password: yup.string().required().matches(new RegExp("^(?=.*[a-z])(?=.*[A-Z])(?=.*\d).{5,}$")),
        confirmPassword: yup.string().required().oneOf([yup.ref('password')], 'Passwords must match'),
        firstName: yup.string().required(),
        lastName: yup.string().required(),
    });

    interface formResult{
        login:string,
        password:string,
        confirmPassword: string,
        firstName: string,
        lastName: string
    }
    const handleSubmit = (values:formResult, formikHelpers : FormikHelpers<formResult>) =>{
        api.register(values.login, values.password, values.firstName, values.lastName).
        then(()=>{navigate(Pathnames.public.login)})
            .catch((error) =>{
            setErrorModalContent(JSON.stringify(error.response.data));
            setShowErrorModal(true)
        })
            .finally(()=>{formikHelpers.setSubmitting(false);});
    }

    return (
        <div>
            <ModalBasic show={showErrorModal} setShow={setShowErrorModal} title={"Register Error"} body={errorModalContent} footer={""}/>
            <Formik
                validationSchema={schemaUser}
                initialValues={{
                    login: '',
                    password: '',
                    confirmPassword: '',
                    firstName: '',
                    lastName: ''
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
                    <Form.Group controlId="addUserFormConfirmPassword">
                        <Form.Label>Confirm Password:</Form.Label>
                        <Form.Control onChange={props.handleChange} onBlur={props.handleBlur}
                                      value={props.values.confirmPassword}
                                      isInvalid={props.touched.confirmPassword && !!props.errors.confirmPassword}
                                      isValid={props.touched.confirmPassword && !props.errors.confirmPassword} name="confirmPassword"
                                      type="password"/>
                        <Form.Control.Feedback
                            type="invalid">{capitalize(props.errors.confirmPassword?.toString())}</Form.Control.Feedback>
                    </Form.Group>
                    <Form.Group controlId="addUserFormFirstName">
                        <Form.Label>First name:</Form.Label>
                        <Form.Control onChange={props.handleChange} onBlur={props.handleBlur}
                                      value={props.values.firstName}
                                      isInvalid={props.touched.firstName && !!props.errors.firstName}
                                      isValid={props.touched.firstName && !props.errors.firstName} name="firstName"
                                      type="text"/>
                        <Form.Control.Feedback
                            type="invalid">{capitalize(props.errors.firstName?.toString())}</Form.Control.Feedback>
                    </Form.Group>
                    <Form.Group controlId="addUserFormLastName">
                        <Form.Label>Last name:</Form.Label>
                        <Form.Control onChange={props.handleChange} onBlur={props.handleBlur}
                                      value={props.values.lastName}
                                      isInvalid={props.touched.lastName && !!props.errors.lastName}
                                      isValid={props.touched.lastName && !props.errors.lastName} name="lastName"
                                      type="text"/>
                        <Form.Control.Feedback
                            type="invalid">{capitalize(props.errors.confirmPassword?.toString())}</Form.Control.Feedback>
                    </Form.Group>
                    <Button variant="success" type="submit" className="mt-3">Register</Button>
                </Form>
            )}</Formik>
        </div>
    );
}

export default RegisterForm;
