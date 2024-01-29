import {Route, Routes} from "react-router-dom";
import {AdminRoutes, PublicRoutes} from "../routes";
import PublicLayout from "../../components/layouts/PublicLayout";
import {useAccount} from "../../hooks/useAccount";
import {AccountTypeEnum} from "../../types/Users";
export const RoutesComponents = () => {
    const { isAuthenticated, accountType } = useAccount();
    return (
        <Routes>
            {!isAuthenticated && PublicRoutes.map(({path, Component}) => (
                <Route key={path} path={path} element={
                    <PublicLayout>
                        <Component/>
                    </PublicLayout>}/>
            ))}

            {isAuthenticated && accountType===AccountTypeEnum.ADMIN && AdminRoutes.map(({path, Component}) => (
                <Route key={path} path={path} element={
                    <PublicLayout>
                        <Component/>
                    </PublicLayout>}/>
            ))}

            {isAuthenticated && accountType===AccountTypeEnum.CLIENT && AdminRoutes.map(({path, Component}) => (
                <Route key={path} path={path} element={
                    <PublicLayout>
                        <Component/>
                    </PublicLayout>}/>
            ))}

            {isAuthenticated && accountType===AccountTypeEnum.RESADMIN && AdminRoutes.map(({path, Component}) => (
                <Route key={path} path={path} element={
                    <PublicLayout>
                        <Component/>
                    </PublicLayout>}/>
            ))}
        </Routes>
    )
}