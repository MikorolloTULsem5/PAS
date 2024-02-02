import React, {useRef, useState} from 'react';
import {Form} from "react-bootstrap";
import * as formik from 'formik';
import * as yup from 'yup';
import {string} from "yup";
import ConfirmModal from "../Modal/ConfirmModal";
import {FormikProps, FormikValues} from "formik";
import {NewClientType, NewUserType} from "../../types/Users";
import {capitalize, isEmpty} from "lodash";
import {usersApi} from "../../api/userApi";
import ModalBasic from "../Modal/ModalBasic";

function AddUserForm() {
    const [userType,setUserType] = useState<string>('Admin');
    const [showErrorModal,setShowErrorModal] = useState(false);
    const [errorModalContent,setErrorModalContent] = useState<string>("");
    const [createdNewUser, setCreatedNewUser] = useState(false);
    const userFormRef = useRef<FormikProps<FormikValues>>(null)
    const clientFormRef = useRef<FormikProps<FormikValues>>(null)
    const {Formik} = formik;
    const schemaUser = yup.object().shape({
        login: yup.string().required(),
        password: yup.string().required().matches(new RegExp("^(?=.*[a-z])(?=.*[A-Z])(?=.*\d).{5,}$"))
    });
    const schemaClient = yup.object().shape({
        firstName: yup.string().required(),
        lastName: yup.string().required()
    });

    const handleSubmit = () => {
        setCreatedNewUser(false);
        if (!userFormRef.current) {return;}
        if (!isEmpty(userFormRef.current.errors) || isEmpty(userFormRef.current.touched)){return;}

        let newUser:NewUserType= {login:'',password:'',userType:null};
        newUser.login = userFormRef.current.values.login;
        newUser.password = userFormRef.current.values.password;
        newUser.userType = userFormRef.current.values.userType;

        if(clientFormRef.current){
            if(!isEmpty(clientFormRef.current.errors) || isEmpty(clientFormRef.current.touched)){return;}
            let newClient:NewClientType = {...newUser,firstName:'',lastName:'',clientTypeName:'normal'}
            newClient.firstName = clientFormRef.current.values.firstName;
            newClient.lastName = clientFormRef.current.values.lastName;
            newClient.clientTypeName = clientFormRef.current.values.clientTypeName;

            usersApi.create(newClient)?.then(()=>setCreatedNewUser(true))
                .catch((error) => {setErrorModalContent(JSON.stringify(error.response.data)); setShowErrorModal(true)})
        } else {
            usersApi.create(newUser)?.then(()=>setCreatedNewUser(true))
                .catch((error) => {setErrorModalContent(JSON.stringify(error.response.data)); setShowErrorModal(true)})
        }
    }

    return (
        <div>
            <ModalBasic show={showErrorModal} setShow={setShowErrorModal} title={"Modification Error"} body={errorModalContent} footer={""}/>
            <Formik innerRef={userFormRef}
                validationSchema={schemaUser}
                initialValues={{
                    login: '',
                    password: '',
                    userType:'Admin'
                }}
                onSubmit={(values) => {}}
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
                    <Form.Group controlId="modifyFormUserType">
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
                }}
            >{props => (
                <Form noValidate onSubmit={props.handleSubmit} className="m-3 p-3">
                    <Form.Group controlId="addClientFormFirstName">
                        <Form.Label>First name:</Form.Label>
                        <Form.Control onChange={props.handleChange} onBlur={props.handleBlur}
                                      value={props.values.firstName}
                                      isInvalid={props.touched.firstName && !!props.errors.firstName}
                                      isValid={props.touched.firstName && !props.errors.firstName} name="firstName"
                                      type="text"/>
                        <Form.Control.Feedback
                            type="invalid">{capitalize(props.errors.firstName?.toString())}</Form.Control.Feedback>
                    </Form.Group>
                    <Form.Group controlId="addClientFormLastName">
                        <Form.Label>Last name:</Form.Label>
                        <Form.Control onChange={props.handleChange} onBlur={props.handleBlur}
                                      value={props.values.lastName}
                                      isInvalid={props.touched.lastName && !!props.errors.lastName}
                                      isValid={props.touched.lastName && !props.errors.lastName} name="lastName"
                                      type="text"/>
                        <Form.Control.Feedback
                            type="invalid">{capitalize(props.errors.lastName?.toString())}</Form.Control.Feedback>
                    </Form.Group>
                    <Form.Group controlId="addClientClientTypeName">
                        <Form.Label>Client type:</Form.Label>
                        <Form.Select onChange={props.handleChange} onBlur={props.handleBlur}
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
            )}
            </Formik>}
            <ConfirmModal variant={"primary"} title={'Create user'}
                          body={<h2>Are you sure you want to create new user?</h2>}
                          onConfirm={handleSubmit}>Save</ConfirmModal>
            {createdNewUser && <a>Created new user!</a>}
        </div>
    );
}

export default AddUserForm;
