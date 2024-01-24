import {Dispatch, ReactNode, SetStateAction} from 'react';
import Modal from 'react-bootstrap/Modal';

interface ModalProps {
    show: boolean
    setShow: Dispatch<SetStateAction<boolean>>
    title: string
    body: ReactNode
    footer: ReactNode
}

function ModalBasic({show,setShow,title,body,footer}:ModalProps) {
    const handleClose = () => setShow(false);

    return (
        <Modal size="lg" show={show} onHide={handleClose}>
            <Modal.Header closeButton>
                <Modal.Title>{title}</Modal.Title>
            </Modal.Header>
            <Modal.Body>
                {body}
            </Modal.Body>
            <Modal.Footer>
                {footer}
            </Modal.Footer>
        </Modal>
    );
}

export default ModalBasic;