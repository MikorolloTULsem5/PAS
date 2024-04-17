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
import {usersApi} from "../../api/userApi";
function RegisterForm() {
    const [showErrorModal,setShowErrorModal] = useState(false);
    const [errorModalContent,setErrorModalContent] = useState<string>("");
    const [changedPassword, setChangedPassword] = useState(false);
    const {accountType} = useAccount();
    const {Formik} = formik;
    const schemaUser = yup.object().shape({
        oldPassword: yup.string().required(),
        newPassword: yup.string().required().matches(new RegExp("^(?=.*[a-z])(?=.*[A-Z])(?=.*\d).{5,}$")),
        newPasswordConfirm: yup.string().required().oneOf([yup.ref('newPassword')], 'Passwords must match'),
    });

    interface formResult{
        oldPassword: string,
        newPassword: string,
        newPasswordConfirm: string
    }
    const handleSubmit = (values:formResult, formikHelpers : FormikHelpers<formResult>) =>{
        setChangedPassword(false);
        if(accountType){
            usersApi.changePassword(accountType,values.oldPassword, values.newPassword, values.newPasswordConfirm).
            then( () => {setChangedPassword(true)})
                .catch((error) =>{
                    setErrorModalContent(JSON.stringify(error.response.data));
                    setShowErrorModal(true)
                })
                .finally(()=>{formikHelpers.setSubmitting(false);});
        }
    }

    return (
        <div>
            <ModalBasic show={showErrorModal} setShow={setShowErrorModal} title={"Change Password Error"} body={errorModalContent} footer={""}/>
            <Formik
                validationSchema={schemaUser}
                initialValues={{
                    oldPassword: '',
                    newPassword: '',
                    newPasswordConfirm: ''
                }}
                onSubmit={handleSubmit}
            >{props => (
                <Form onSubmit={props.handleSubmit} noValidate className="m-3 p-3">
                    <Form.Group controlId="changePasswordFormOldPassword" >
                        <Form.Label>Old password:</Form.Label>
                        <Form.Control onChange={props.handleChange} onBlur={props.handleBlur}
                                      value={props.values. oldPassword}
                                      isInvalid={props.touched. oldPassword && !!props.errors. oldPassword}
                                      isValid={props.touched. oldPassword && !props.errors. oldPassword} name="oldPassword"
                                      type="password"/>
                        <Form.Control.Feedback
                            type="invalid">{capitalize(props.errors. oldPassword?.toString())}</Form.Control.Feedback>
                    </Form.Group>
                    <Form.Group controlId="changePasswordFormNewPassword">
                        <Form.Label>New password:</Form.Label>
                        <Form.Control onChange={props.handleChange} onBlur={props.handleBlur}
                                      value={props.values.newPassword}
                                      isInvalid={props.touched.newPassword && !!props.errors.newPassword}
                                      isValid={props.touched.newPassword && !props.errors.newPassword} name="newPassword"
                                      type="password"/>
                        <Form.Control.Feedback
                            type="invalid">{capitalize(props.errors.newPassword?.toString())}</Form.Control.Feedback>
                    </Form.Group>
                    <Form.Group controlId="changePasswordFormNewPasswordConfirm">
                        <Form.Label>Confirm new password:</Form.Label>
                        <Form.Control onChange={props.handleChange} onBlur={props.handleBlur}
                                      value={props.values.newPasswordConfirm}
                                      isInvalid={props.touched.newPasswordConfirm && !!props.errors.newPasswordConfirm}
                                      isValid={props.touched.newPasswordConfirm && !props.errors.newPasswordConfirm} name="newPasswordConfirm"
                                      type="password"/>
                        <Form.Control.Feedback
                            type="invalid">{capitalize(props.errors.newPasswordConfirm?.toString())}</Form.Control.Feedback>
                    </Form.Group>
                    <Button variant="success" type="submit" className="mt-3">Change Password</Button>
                </Form>
            )}</Formik>
            {changedPassword && <a>Changed password!</a>}
        </div>
    );
}

export default RegisterForm;
