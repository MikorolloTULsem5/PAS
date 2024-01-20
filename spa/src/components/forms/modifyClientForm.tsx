import React from 'react';
import {Form} from "react-bootstrap";
import * as formik from 'formik';
import * as yup from 'yup';
import {string} from "yup";

function modifyClientForm() {
    const {Formik} = formik;
    const schema = yup.object().shape({
        login: yup.string().required()
    });

    return (
        <Formik
            validationSchema={schema}
            initialValues={{
                login: ''
            }}
            onSubmit={(values) => {
                console.log(string);
            }}
        >{props => (
            <Form noValidate onSubmit={props.handleSubmit} className="m-3 p-3">
                <Form.Group controlId="modifyFormLogin">
                    <Form.Label>Login:</Form.Label>
                    <Form.Control onChange={props.handleChange} onBlur={props.handleBlur}
                                  value={props.values.login}
                                  isInvalid={props.touched.login && !!props.errors.login}
                                  isValid={props.touched.login && !props.errors.login} name="login"
                                  type="text"/>
                    <Form.Control.Feedback
                        type="invalid">Error</Form.Control.Feedback>
                </Form.Group>
            </Form>
        )}
        </Formik>
    );
}

export default modifyClientForm;
