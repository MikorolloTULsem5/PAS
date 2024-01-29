import {AccountTypeEnum} from "../types/Users";
import {useAccountState} from "../context/AccountContext";
import {useNavigate} from "react-router-dom";
import {api} from "../api/api";
import {Pathnames} from "../router/pathnames";
import {adminsApi} from "../api/adminsApi";

export const useAccount = () => {
    const navigate = useNavigate()
    const { account, setAccount, token, setToken } =
        useAccountState()
    const isAuthenticated = !!account?.login
    const accountType:AccountTypeEnum | null = !!account ? AccountTypeEnum.ADMIN : null;
    const logOut = async () => {
        try {
            await api.logOut()
        } catch {
            alert('Logout failure!')
        } finally {
            localStorage.removeItem('token')
            setAccount(null)
            setToken(null)
            navigate(Pathnames.public.login)
        }
    }
    const logIn = async (login: string, password: string) => {
        try {
            const token = (await api.logIn(login, password)).data.accessToken;
            setToken(token);
            localStorage.setItem('token',token);
            console.log(localStorage);
            //TODO zrobić to uniwersalne
            const {data} = await adminsApi.getAdminByLogin(login);
            setAccount(data);
            navigate(Pathnames.public.home)
        } catch(e) {
            alert('Logging in error!')
            if(isAuthenticated)logOut();
        } finally {
        }
    }
    const getCurrentAccount = async () => {
        try {
            if (localStorage.getItem("token")) {
                const { data } = await api.getCurrentAccount()
                setAccount(data)
            }
        } catch {
            alert('Unable to get current account!')
            logOut()
        } finally {
        }
    }
    return {
        account,
        token,
        isAuthenticated,
        accountType,
        logIn,
        getCurrentAccount,
        logOut,
    }
}