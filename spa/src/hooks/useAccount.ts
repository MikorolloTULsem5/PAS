
import {AccountTypeEnum} from "../types/Users";
import {useAccountState} from "../context/AccountContext";
import {useNavigate} from "react-router-dom";

export const useAccount = () => {
    const navigate = useNavigate()
    const { account, setAccount, isLoggingIn, setIsLoggingIn, isFetching, setIsFetching } =
        useAccountState()
    const isAuthenticated = !!account?.login
    const isAdmin:AccountTypeEnum | null = account ? account.userType : null;
    const logOut = async () => {
        try {
            setIsFetching(true)
            await api.logOut()
        } catch {
            alert('Logout failure!')
        } finally {
            localStorage.removeItem('token')
            setAccount(null)
            navigate(Pathnames.public.login)
            setIsFetching(false)
        }
    }
    const logIn = async (login: string, password: string) => {
        try {
            setIsLoggingIn(true)
            const { data } = await api.logIn(login, password)
            setAccount(data)
        } catch {
            alert('Logging in error!')
            logOut()
        } finally {
            setIsLoggingIn(false)
        }
    }
    const getCurrentAccount = async () => {
        try {
            setIsFetching(true)
            if (localStorage.getItem(TOKEN)) {
                const { data } = await api.getCurrentAccount()
                setAccount(data)
            }
        } catch {
            alert('Unable to get current account!')
            logOut()
        } finally {
            setIsFetching(false)
        }
    }
    return {
        account,
        isLoggingIn,
        isFetching,
        isAuthenticated,
        isAdmin,
        Documentation13logIn,
        getCurrentAccount,
        logOut,
    }
}