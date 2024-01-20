import {Route, Routes} from "react-router-dom";
import {publicRoutes} from "../routes";

export const RoutesComponents = () => (
    <Routes>
        {publicRoutes.map(({ path, Component }) => (
            <Route key={path} path={path} element={<Component />} />
        ))}
    </Routes>
)