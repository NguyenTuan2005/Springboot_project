import React from 'react';
import { BrowserRouter as Router, Route, Routes, Navigate, useLocation } from 'react-router-dom';
import { AuthProvider, useAuth } from "./components/AuthContext.tsx";
import Login from "./components/Login.tsx";
import Header from "./components/Header.tsx";
import TaskList from "./components/TaskList.tsx";
import TaskForm from "./components/TaskForm.tsx";

const ProtectedRoute: React.FC<{ element: React.ReactElement }> = ({ element }) => {
    const { token } = useAuth();
    return token ? element : <Navigate to="/login" />;
};

const AppLayout: React.FC = () => {
    const location = useLocation();
    const isLoginPage = location.pathname === '/login';

    return (
        <div className="App">
            {!isLoginPage && <Header />}
            <Routes>
                <Route path="/login" element={<Login />} />
                <Route path="/tasks" element={<ProtectedRoute element={<TaskList />} />} />
                <Route path="/add-task" element={<ProtectedRoute element={<TaskForm />} />} />
                <Route path="/" element={<Navigate to="/tasks" />} />
            </Routes>
        </div>
    );
};

const App: React.FC = () => {
    return (
        <AuthProvider>
            <Router>
                <AppLayout />
            </Router>
        </AuthProvider>
    );
};

export default App;