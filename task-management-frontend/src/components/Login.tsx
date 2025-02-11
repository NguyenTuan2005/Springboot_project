import React, { useState } from 'react';
import type { AxiosError } from 'axios';
import axios from 'axios';
import { useAuth } from './AuthContext.tsx';
import { useNavigate } from 'react-router-dom';
import 'bootstrap/dist/css/bootstrap.min.css';

const Login: React.FC = () => {
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [error, setError] = useState('');
    const [isLoading, setIsLoading] = useState(false);
    const { login } = useAuth();
    const navigate = useNavigate();

    const isAxiosError = (error: any): error is AxiosError => {
        return error?.isAxiosError === true;
    };

    // Error handler that logs the error for debugging or test purposes.
    const handleLoginError = (error: Error) => {
        console.error("Login failed:", error);
    };

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        setError('');
        setIsLoading(true);

        try {
            const response = await axios.post('http://localhost:8080/auth/login', {
                username,
                password,
            });

            const token = response.data.token || response.data;
            login(token);
            navigate('/tasks');
        } catch (error: any) {
            // Call the error handler so that tests can detect the error event.
            handleLoginError(error);

            if (isAxiosError(error)) {
                if (error.response?.status === 401) {
                    setError('Invalid username or password');
                } else if (error.response?.status === 429) {
                    setError('Too many attempts. Please try again later');
                } else if (!error.response) {
                    setError('Network error. Please check your connection');
                } else {
                    setError('An unexpected error occurred. Please try again');
                }
            } else {
                setError('An unexpected error occurred. Please try again');
            }
        } finally {
            setIsLoading(false);
        }
    };

    return (
        <div className="container">
            <div className="row justify-content-center mt-5">
                <div className="col-md-6 col-lg-4">
                    <div className="card shadow">
                        <div className="card-body p-4">
                            <h2 className="text-center mb-4">Login</h2>

                            {error && (
                                <div className="alert alert-danger" role="alert">
                                    {error}
                                </div>
                            )}

                            <form onSubmit={handleSubmit}>
                                <div className="mb-3">
                                    <label htmlFor="username" className="form-label">
                                        Username
                                    </label>
                                    <input
                                        type="text"
                                        className="form-control"
                                        id="username"
                                        value={username}
                                        onChange={(e) => setUsername(e.target.value)}
                                        required
                                        disabled={isLoading}
                                    />
                                </div>

                                <div className="mb-3">
                                    <label htmlFor="password" className="form-label">
                                        Password
                                    </label>
                                    <input
                                        type="password"
                                        className="form-control"
                                        id="password"
                                        value={password}
                                        onChange={(e) => setPassword(e.target.value)}
                                        required
                                        disabled={isLoading}
                                    />
                                </div>

                                <div className="d-grid gap-2">
                                    <button
                                        type="submit"
                                        className="btn btn-primary"
                                        disabled={isLoading}
                                    >
                                        {isLoading ? (
                                            <>
                                                <span
                                                    className="spinner-border spinner-border-sm me-2"
                                                    role="status"
                                                    aria-hidden="true"
                                                ></span>
                                                Logging in...
                                            </>
                                        ) : (
                                            'Login'
                                        )}
                                    </button>
                                </div>
                            </form>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default Login;
