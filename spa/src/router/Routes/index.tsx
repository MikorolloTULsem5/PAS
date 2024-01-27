import {Route, Routes} from "react-router-dom";
import {publicRoutes} from "../routes";
import PublicLayout from "../../components/layouts/PublicLayout";

export const RoutesComponents = () => (
    <Routes>
        {publicRoutes.map(({path, Component}) => (
            <Route key={path} path={path} element={
                <PublicLayout>
                    <Component/>
                </PublicLayout>}/>
        ))}
    </Routes>
)