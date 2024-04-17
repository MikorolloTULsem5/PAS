import {AccountTypeEnum} from "../types/Users";
import {useAccountState} from "../context/AccountContext";
import {useNavigate} from "react-router-dom";
import {api} from "../api/api";
import {Pathnames} from "../router/pathnames";
import {adminsApi} from "../api/adminsApi";
import {usersApi} from "../api/userApi";
import {jwtDecode} from "jwt-decode";
import {TokenPayload} from "../types/TokenPayload";

export const useAccount = () => {
    const navigate = useNavigate()
    const { account, setAccount} =
        useAccountState()
    const isAuthenticated = !!account?.login
    const accountType:AccountTypeEnum | null = !!account?.userType ? account.userType : null;
    const logOut = async () => {
        try {
            await api.logOut()
        } catch {
            alert('Logout failure!')
        } finally {
            localStorage.removeItem('token')
            setAccount(null)
        }
    }
    const logIn = async (login: string, password: string) => {
        try {
            const token = (await api.logIn(login, password)).data.accessToken;
            localStorage.setItem('token',token);
            const {data} = await usersApi.getMe(jwtDecode<TokenPayload>(token).authorities[0].authority);
            setAccount(data);
            navigate(Pathnames.public.home)
        } catch(e) {
            console.log(e);
            alert('Logging in error!')
            if(isAuthenticated)logOut();
        } finally {
        }
    }
    const getCurrentAccount = async () => {
        try {
            const tokenRaw = localStorage.getItem("token");
            if (tokenRaw && tokenRaw !=='null') {
                const token = jwtDecode<TokenPayload>(tokenRaw);
                const { data } = await usersApi.getMe(token.authorities[0].authority);
                setAccount(data)
            } else throw new Error("Token is null");
        } catch {
            if(account!==null){
                alert('Unable to get current account!');
                logOut();
            }
        } finally {
        }
    }
    return {
        account,
        isAuthenticated,
        accountType,
        logIn,
        getCurrentAccount,
        logOut,
    }
}