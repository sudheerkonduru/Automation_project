import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { AuthService } from '../service/authService'; 
import { postRequest } from './api';

const Login = () => {
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [error, setError] = useState(null);
    const navigate = useNavigate();

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError(null);

        if (!email || !password) {
            setError("Please enter both email and password.");
            return;
        }

        try {
            const response = await postRequest('/auth/login', { email, password });
            console.log(response);

            if (response && response.accessToken) {
                const { accessToken, role, employeeId } = response;

                // Using AuthService to handle user data
                AuthService.setUser({ token: accessToken, role, employeeId });

                if (role === 'HR') {
                    navigate('/hr/dashboard');
                } else if (role === 'EMPLOYEE') {
                    navigate('/employee/dashboard');
                } else if (role === 'ADMIN') {
                    navigate('/admin/dashboard');
                } else if (role === 'TEMPORARY_Admin') {
                    navigate('/temporary/admin/dashboard');
                }else {
                    setError('Unknown role. Please contact support.');
                }
            } else {
                setError(response?.message || 'Login failed. Please check your credentials.');
            }
        } catch (error) {
            console.error("Login request error:", error);
            setError('Invalid login credentials.');
        }
    };

    return (
        <div className="flex flex-col items-center h-screen justify-center bg-gradient-to-b from-teal-600 from-50% to-gray-100 to-50% space-y-6">
            <h2 className="font-serif text-3xl text-white">
                Human Resource Management System
            </h2>
            <div className="border shadow p-6 w-80 bg-white">
                <h2 className="text-2xl font-bold mb-4">Login</h2>
                <form onSubmit={handleSubmit}>
                    <div className="mb-4">
                        <label htmlFor="email" className="block text-gray-700">Email:</label>
                        <input
                            type="email"
                            className="w-full px-3 py-2 border"
                            placeholder="Enter Your Email"
                            value={email}
                            onChange={(e) => setEmail(e.target.value)}
                            autoFocus
                            required
                        />
                    </div>
                    <div className="mb-4">
                        <label htmlFor="password" className="block text-gray-700">Password:</label>
                        <input
                            type="password"
                            className="w-full px-3 py-2 border"
                            placeholder="*******"
                            value={password}
                            onChange={(e) => setPassword(e.target.value)}
                            required
                        />
                    </div>
                    {error && <div className="mb-4 text-red-600 text-sm">{error}</div>}
                    <div className="mb-4 flex items-center justify-between">
                        <label className="inline-flex items-center">
                            <input type="checkbox" className="form-checkbox" />
                            <span className="ml-2 text-gray-700">Remember Me</span>
                        </label>
                        <a href="#" className="text-teal-600">Forgot Password?</a>
                    </div>
                    <div className="mb-4">
                        <button type="submit" className="w-full bg-teal-600 text-white py-2">Login</button>
                    </div>
                </form>
            </div>
        </div>
    );
};

export default Login; 
