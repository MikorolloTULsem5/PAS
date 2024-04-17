import NavbarBasic from "../../NavBar/NavbarBasic";
import {Pathnames} from "../../../router/pathnames";
import {ReactNode} from "react";
import Container from "react-bootstrap/Container";

interface LayoutProps {
    children: ReactNode
}

function PublicLayout({children}:LayoutProps){
    return (
        <div>
            <NavbarBasic pathnames={Pathnames.public}/>
            <Container>{children}</Container>
        </div>
    )
}

export default PublicLayout;