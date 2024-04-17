import NavbarBasic from "../../NavBar/NavbarBasic";
import {Pathnames} from "../../../router/pathnames";
import {ReactNode} from "react";
import Container from "react-bootstrap/Container";

interface LayoutProps {
    children: ReactNode
}

function AdminLayout({children}:LayoutProps){
    return (
        <div>
            <NavbarBasic pathnames={Pathnames.admin}/>
            <Container>{children}</Container>
        </div>
    )
}

export default AdminLayout;