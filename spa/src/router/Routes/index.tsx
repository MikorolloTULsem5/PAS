import {Route, Routes} from "react-router-dom";
import {AdminRoutes, ClientRoutes, PublicRoutes} from "../routes";
import PublicLayout from "../../components/layouts/PublicLayout";
import {useAccount} from "../../hooks/useAccount";
import {AccountTypeEnum} from "../../types/Users";
import {useEffect} from "react";
import AdminLayout from "../../components/layouts/AdminLayout";
import ClientLayout from "../../components/layouts/ClientLayout";
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
                    <AdminLayout>
                        <Component/>
                    </AdminLayout>}/>
            ))}

            //TODO Zamienić logike
            {isAuthenticated && false && ClientRoutes.map(({path, Component}) => (
                <Route key={path} path={path} element={
                    <ClientLayout>
                        <Component/>
                    </ClientLayout>}/>
            ))}

            {isAuthenticated && false && AdminRoutes.map(({path, Component}) => (
                <Route key={path} path={path} element={
                    <PublicLayout>
                        <Component/>
                    </PublicLayout>}/>
            ))}
        </Routes>
    )
}