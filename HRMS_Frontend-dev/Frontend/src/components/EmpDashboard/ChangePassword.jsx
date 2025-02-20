import React, { useState, useEffect } from 'react';
import { AuthService } from '../../service/authService';
import axios from 'axios';

const ChangePassword = () => {
  const [currentPassword, setCurrentPassword] = useState('');
  const [newPassword, setNewPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  const [error, setError] = useState('');
  const [successMessage, setSuccessMessage] = useState('');
  
  const user = AuthService.getUserData();
  const employeeId = user?.employeeId; // Check if employeeId is available
  const employeeManagementLink = import.meta.env.VITE_EMPLOYEE_MANAGEMENT;


  const handleSubmit = async (e) => {
    e.preventDefault();

    if (newPassword !== confirmPassword) {
      setError('New passwords do not match.');
      return;
    }

    if (!employeeId) {
      setError('Employee ID is missing. Please log in again.');
      return;
    }

    try {
      // Make PUT request to update the password
      const response = await axios.put(`${employeeManagementLink}/api/employees/employee/${employeeId}`, {
        currentPassword,
        newPassword,
      });

      // If the request is successful, clear the form and show success message
      setSuccessMessage('Password changed successfully');
      setCurrentPassword('');
      setNewPassword('');
      setConfirmPassword('');
      setError('');
    } catch (err) {
      setError('Failed to change password. Please check the current password and try again.');
    }
  };

  return (
    <div className="min-h-screen flex items-center justify-center bg-gray-100">
      <div className="bg-white p-6 rounded-lg shadow-lg w-96">
        <h2 className="text-2xl font-semibold text-center text-gray-800 mb-6">Change Password</h2>
        <form onSubmit={handleSubmit}>
          <div className="mb-4">
            <label htmlFor="currentPassword" className="block text-sm font-medium text-gray-600">Current Password</label>
            <input
              type="password"
              id="currentPassword"
              className="w-full mt-2 p-3 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
              value={currentPassword}
              onChange={(e) => setCurrentPassword(e.target.value)}
              required
            />
          </div>

          <div className="mb-4">
            <label htmlFor="newPassword" className="block text-sm font-medium text-gray-600">New Password</label>
            <input
              type="password"
              id="newPassword"
              className="w-full mt-2 p-3 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
              value={newPassword}
              onChange={(e) => setNewPassword(e.target.value)}
              required
            />
          </div>

          <div className="mb-4">
            <label htmlFor="confirmPassword" className="block text-sm font-medium text-gray-600">Confirm New Password</label>
            <input
              type="password"
              id="confirmPassword"
              className="w-full mt-2 p-3 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
              value={confirmPassword}
              onChange={(e) => setConfirmPassword(e.target.value)}
              required
            />
          </div>

          {error && <div className="text-sm text-red-600 mb-4">{error}</div>}
          {successMessage && <div className="text-sm text-green-600 mb-4">{successMessage}</div>}

          <button type="submit" className="w-full py-3 bg-blue-500 text-white font-semibold rounded-lg hover:bg-blue-600 focus:outline-none focus:ring-2 focus:ring-blue-500">
            Change Password
          </button>
        </form>
      </div>
    </div>
  );
};

export default ChangePassword;
