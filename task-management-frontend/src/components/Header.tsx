import React from 'react';
import { useAuth } from './AuthContext.tsx';
import { LogIn, LogOut, ClipboardList } from 'lucide-react';

const Header: React.FC = () => {
    const { token, logout } = useAuth();

    const handleLogout = () => {
        logout();
        // You might want to redirect to login page here
        window.location.href = '/login';
    };

    const handleLogin = () => {
        // Redirect to login page
        window.location.href = '/login';
    };

    return (
        <nav className="navbar navbar-expand-lg navbar-dark bg-primary">
            <div className="container">
                <a className="navbar-brand d-flex align-items-center" href="/">
                    <ClipboardList size={24} className="me-2" />
                    Task Management
                </a>

                <div className="ms-auto">
                    {token ? (
                        <button
                            className="btn btn-outline-light d-flex align-items-center"
                            onClick={handleLogout}
                        >
                            <LogOut size={18} className="me-2" />
                            Logout
                        </button>
                    ) : (
                        <button
                            className="btn btn-outline-light d-flex align-items-center"
                            onClick={handleLogin}
                        >
                            <LogIn size={18} className="me-2" />
                            Login
                        </button>
                    )}
                </div>
            </div>
        </nav>
    );
};

export default Header;