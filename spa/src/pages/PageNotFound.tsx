import {Button} from "react-bootstrap";
import {useNavigate} from "react-router-dom";
import {Pathnames} from "../router/pathnames";

function PageNotFound(){
    const navigate = useNavigate();
    return (
        <div>
            <h1>Page not found.</h1>
            <Button variant="primary" onClick={()=>{navigate(Pathnames.public.home)}}>Go Home</Button>
        </div>
    )
}

export default PageNotFound;