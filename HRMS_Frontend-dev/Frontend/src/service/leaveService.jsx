import axios from "axios";
import { AuthService } from "./authService";

const leaveManagementLink = import.meta.env.VITE_ATTENDANCE_LEAVE_MANAGEMENT;
const token = (sessionStorage.getItem("token"));

export const LeaveService = {
  // Employee-specific methods
  employee: {
    async getLeaves() {
      const { user } = AuthService.getUserData();  // Retrieve user data
      const employeeId = user.employeeId; // Use employeeId
      const response = await axios.get(`${leaveManagementLink}/api/employee/leaves/${employeeId}`   
      );
    
      return response.data;
    },

    async getLeaveBalance(year = new Date().getFullYear()) {
      const { user } = AuthService.getUserData();  // Retrieve user data
      const employeeId = user.employeeId; // Use employeeId
      const response = await axios.get(`${leaveManagementLink}/api/employee/leave-balances/${employeeId}?year=${year}`,   
      );
      return response.data;
    },

    async applyLeave(leaveData) {
      const response = await axios.post(`${leaveManagementLink}/api/employee/leave/apply`, leaveData,
        
      );
      return response.data;
    },
  },

  // HR-specific methods
  hr: {
    async getAllLeaves() {
      const response = await axios.get(`${leaveManagementLink}/api/hr/leaves`);
      return response.data;
    },

    async processLeaveRequest(leaveId,{status, remarks}) {
      const response = await axios.put(`${leaveManagementLink}/api/hr/leave/${leaveId}?status=${status}&remarks=${encodeURIComponent(remarks)}`);
      return response.data;
    },

    async getLeaveBalances(year = new Date().getFullYear()) {
      const response = await axios.get(`${leaveManagementLink}/api/hr/leave-balances?year=${year}`);
      return response.data;
    },
  },
};
