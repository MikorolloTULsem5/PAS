import Container from 'react-bootstrap/Container';
import Nav from 'react-bootstrap/Nav';
import Navbar from 'react-bootstrap/Navbar';
import {Link} from "react-router-dom";
import {capitalize} from "lodash";

interface NavBarProps {
    pathnames: any
}

function NavbarBasic({pathnames}:NavBarProps) {
    const keys = Object.keys(pathnames);
    return (
        <Navbar expand="lg" className="bg-body-tertiary">
            <Container>
                <Navbar.Brand>App</Navbar.Brand>
                <Navbar.Toggle aria-controls="basic-navbar-nav" />
                <Navbar.Collapse id="basic-navbar-nav">
                    <Nav className="me-auto">
                        {keys.map((key)=>(<Nav.Link key={key} as={Link} to={pathnames[key as keyof typeof pathnames]}>{capitalize(key)}</Nav.Link>))}
                    </Nav>
                </Navbar.Collapse>
            </Container>
        </Navbar>
    );
}

export default NavbarBasic;