import Container from 'react-bootstrap/Container';
import Nav from 'react-bootstrap/Nav';
import Navbar from 'react-bootstrap/Navbar';
import {Link} from "react-router-dom";
import {capitalize} from "lodash";
import {useAccount} from "../../hooks/useAccount";
import {useEffect} from "react";

interface NavBarProps {
    pathnames: any
}

function NavbarBasic({pathnames}:NavBarProps) {
    const {isAuthenticated, account, getCurrentAccount, logOut} = useAccount();

    useEffect(() => {
        getCurrentAccount()
    }, []);

    const keys = Object.keys(pathnames);
    return (
        <Navbar expand="lg" className="bg-body-tertiary container-fluid">
            <Container>
                <Navbar.Brand>App</Navbar.Brand>
                <Navbar.Toggle aria-controls="basic-navbar-nav" />
                <Navbar.Collapse id="basic-navbar-nav">
                    <Nav className="me-auto">
                        {keys.map((key)=>(<Nav.Link key={key} as={Link} to={pathnames[key as keyof typeof pathnames]}>{capitalize(key)}</Nav.Link>))}
                        {isAuthenticated && <Nav.Link onClick={logOut} as={Link} to='/'>Log out</Nav.Link>}
                    </Nav>
                </Navbar.Collapse>
                <Navbar.Collapse className="justify-content-end">
                    {isAuthenticated &&
                        <Navbar.Text>
                        Logged in as: <a>{account?.login}</a> - {account?.userType}
                    </Navbar.Text>}
                    {!isAuthenticated &&
                        <Navbar.Text>
                            Logged in as: <a>Anonymus</a>
                        </Navbar.Text>}
                </Navbar.Collapse>
            </Container>
        </Navbar>
    );
}

export default NavbarBasic;