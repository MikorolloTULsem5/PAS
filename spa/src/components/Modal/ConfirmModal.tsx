import {ReactNode,useState} from 'react';
import ModalBasic from "./ModalBasic";
import {ButtonVariant} from "react-bootstrap/types";
import {Button} from "react-bootstrap";

interface ButtonModalProps {
    variant: ButtonVariant
    children: ReactNode
    title: string
    body: ReactNode
    onConfirm?: () => void
    onCancel?: () => void
}

function ConfirmModal({variant,children,title,body,onConfirm,onCancel}:ButtonModalProps) {
    const [showModal,setShowModal] = useState(false);

    return (
        <div>
            <Button variant={variant} onClick={()=>setShowModal(true)}>{children}</Button>
            <ModalBasic show={showModal} setShow={setShowModal} title={title} body={body} footer={
                <div>
                    <Button variant='success' onClick={() => {
                        onConfirm?.();
                        setShowModal(false);
                    }}>Confirm</Button>
                    <Button variant='danger' onClick={()=>{
                        onCancel?.();
                        setShowModal(false);
                    }}>Cancel</Button>
                </div>
            }></ModalBasic>
        </div>
    );
}

export default ConfirmModal;