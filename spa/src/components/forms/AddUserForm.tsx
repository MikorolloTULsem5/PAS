import React, {useEffect, useRef, useState} from 'react';
import {Form} from "react-bootstrap";
import * as formik from 'formik';
import * as yup from 'yup';
import {string} from "yup";
import ConfirmModal from "../Modal/ConfirmModal";
import {FormikProps, FormikValues} from "formik";

function AddUserForm() {
    const [userType,setUserType] = useState<string>('Admin');
    const userFormRef = useRef<FormikProps<FormikValues>>(null)
    const clientFormRef = useRef<FormikProps<FormikValues>>(null)
    const {Formik} = formik;
    const schemaClient = yup.object().shape({
        login: yup.string().required(),
        password: yup.string().required()
    });
    const schemaUser = yup.object().shape({
        clientTypeName: yup.string().required(),
        firstName: yup.string().required(),
        lastName: yup.string().required()
    });

    const handleSubmit = () => {
        if (userFormRef.current) {
            console.log(userFormRef);
            console.log(clientFormRef.current?.values);
            userFormRef.current.handleSubmit()
        }
    }

    return (
        <div>
            <Formik innerRef={userFormRef}
                validationSchema={schemaUser}
                initialValues={{
                    login: '',
                    password: '',
                    userType:''
                }}
                onSubmit={(values) => {
                    console.log(string);
                }}
            >{props => (
                <Form noValidate onSubmit={props.handleSubmit} className="m-3 p-3">
                    <Form.Group controlId="addUserFormLogin">
                        <Form.Label>Login:</Form.Label>
                        <Form.Control onChange={props.handleChange} onBlur={props.handleBlur}
                                      value={props.values.login}
                                      isInvalid={props.touched.login && !!props.errors.login}
                                      isValid={props.touched.login && !props.errors.login} name="login"
                                      type="text"/>
                        <Form.Control.Feedback
                            type="invalid">Error</Form.Control.Feedback>
                    </Form.Group>
                    <Form.Group controlId="addUserFormPassword">
                        <Form.Label>Password:</Form.Label>
                        <Form.Control onChange={props.handleChange} onBlur={props.handleBlur}
                                      value={props.values.password}
                                      isInvalid={props.touched.password && !!props.errors.password}
                                      isValid={props.touched.password && !props.errors.password} name="password"
                                      type="password"/>
                        <Form.Control.Feedback
                            type="invalid">Error</Form.Control.Feedback>
                    </Form.Group>
                    <Form.Group controlId="modifyFormLogin">
                        <Form.Label>User type:</Form.Label>
                        <Form.Select onChange={(event)=>{props.handleChange(event);setUserType(event.target.value)}}
                                      value={props.values.userType}
                                      name="userType">
                            <option>Admin</option>
                            <option>Resource admin</option>
                            <option>Client</option>
                        </Form.Select>
                        <Form.Control.Feedback
                            type="invalid">Error</Form.Control.Feedback>
                    </Form.Group>
                </Form>
            )}
            </Formik>
            {userType==='Client' && <Formik innerRef={clientFormRef}
                validationSchema={schemaClient}
                initialValues={{
                    clientTypeName: 'Normal',
                    firstName: '',
                    lastName:''
                }}
                onSubmit={(values) => {
                    console.log(string);
                }}
            >{props => (
                <Form noValidate onSubmit={props.handleSubmit} className="m-3 p-3">
                    <Form.Group controlId="addUserFormLogin">
                        <Form.Label>First name:</Form.Label>
                        <Form.Control onChange={props.handleChange} onBlur={props.handleBlur}
                                      value={props.values.firstName}
                                      isInvalid={props.touched.firstName && !!props.errors.firstName}
                                      isValid={props.touched.firstName && !props.errors.firstName} name="firstName"
                                      type="text"/>
                        <Form.Control.Feedback
                            type="invalid">Error</Form.Control.Feedback>
                    </Form.Group>
                    <Form.Group controlId="addUserFormPassword">
                        <Form.Label>Last name:</Form.Label>
                        <Form.Control onChange={props.handleChange} onBlur={props.handleBlur}
                                      value={props.values.lastName}
                                      isInvalid={props.touched.lastName && !!props.errors.lastName}
                                      isValid={props.touched.lastName && !props.errors.lastName} name="lastName"
                                      type="text"/>
                        <Form.Control.Feedback
                            type="invalid">Error</Form.Control.Feedback>
                    </Form.Group>
                    <Form.Group controlId="modifyFormLogin">
                        <Form.Label>Client type:</Form.Label>
                        <Form.Select onChange={props.handleChange} onBlur={props.handleBlur}
                                     value={props.values.clientTypeName}
                                     name="clientTypeName">
                            <option>Normal</option>
                            <option>Coach</option>
                            <option>Athlete</option>
                        </Form.Select>
                        <Form.Control.Feedback
                            type="invalid">Error</Form.Control.Feedback>
                    </Form.Group>
                </Form>
            )}
            </Formik>}
            <ConfirmModal variant={"primary"} title={'Create user'}
                          body={<h2>Are you sure you want to create new user?</h2>}
                          onConfirm={handleSubmit}>Save</ConfirmModal>
        </div>
    );
}

export default AddUserForm;
