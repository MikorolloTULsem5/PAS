import ChangeDetailsForm from "../components/forms/ChangeDetailsForm";
import {useAccount} from "../hooks/useAccount";
import {useEffect} from "react";

function ChangeDetailsPage(){
const {getCurrentAccount } = useAccount()
    useEffect(() => {
        getCurrentAccount();
    }, []);
    return (
        <ChangeDetailsForm/>
    )
}

export default ChangeDetailsPage