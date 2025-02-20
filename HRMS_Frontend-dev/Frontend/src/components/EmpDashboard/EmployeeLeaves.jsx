import React, { useState, useEffect } from "react";
import { LeaveService } from "../../service/leaveService";
import { AuthService } from "../../service/authService";
import { Calendar } from "lucide-react";

export default function EmployeeLeaves() {
  const { user, isAuthenticated } = AuthService.getUserData();
  const [leaves, setLeaves] = useState([]);
  const [leaveBalance, setLeaveBalance] = useState({});
  const [showForm, setShowForm] = useState(false);
  const [formData, setFormData] = useState({
    type: "",
    startDate: "",
    endDate: "",
    reason: "",
  });

  // Redirect unauthenticated users to login
  useEffect(() => {
    if (!isAuthenticated) {
      console.error("User is not authenticated. Redirecting to login...");
      window.location.href = "/login"; // Redirect to login page
    }
  }, [isAuthenticated]);

  // Fetch leaves and leave balance
  useEffect(() => {
    const fetchLeavesAndBalance = async () => {
      if (user) {
        try {
          const userLeaves = await LeaveService.employee.getLeaves();
          const currentLeaveBalance = await LeaveService.employee.getLeaveBalance();
          setLeaves(userLeaves);
          setLeaveBalance(currentLeaveBalance);
        } catch (error) {
          console.error("Error fetching leaves or leave balance:", error.response?.data || error.message);
        }
      }
    };
    fetchLeavesAndBalance();
  }, [user]);

  // Handle leave request submission
  const handleSubmit = async (e) => {
    e.preventDefault();

    const today = new Date().toISOString().split("T")[0];
    if (formData.startDate <= today || formData.endDate <= today) {
      console.error("Start and end dates must be in the future.");
      return;
    }

    try {
      const newLeave = {
        employeeId: user.employeeId, // Use employeeId from user data
        leaveType: formData.type,
        startDate: formData.startDate,
        endDate: formData.endDate,
        reason: formData.reason,
      };
      await LeaveService.employee.applyLeave(newLeave);
      console.log("leave req:", newLeave)

      // Fetch updated leaves after submission
      const updatedLeaves = await LeaveService.employee.getLeaves();
      setLeaves(updatedLeaves);

      setFormData({ type: "", startDate: "", endDate: "", reason: "" });
      setShowForm(false);
    } catch (error) {
      console.error("Error submitting leave request:", error.response?.data || error.message);
    }
  };

  return (
    <div className="p-6 bg-gray-100 min-h-screen">
      <div className="flex justify-between items-center mb-6">
        <h2 className="text-3xl font-bold text-gray-800">My Leaves</h2>
        <button
          onClick={() => setShowForm(true)}
          className="bg-gradient-to-r from-blue-500 to-blue-600 text-white px-6 py-2 rounded-lg shadow-lg hover:shadow-xl transition duration-300"
        >
          Request Leave
        </button>
      </div>

      {/* Leave Balance Section
      <div className="mb-6 bg-white p-6 rounded-lg shadow-xl">
        <h3 className="text-lg font-semibold text-gray-800 mb-4">Leave Balance</h3>
        <div className="grid grid-cols-2 gap-4">
          <div className="bg-blue-100 p-4 rounded-lg">
            <p className="text-sm text-blue-800">Sick Leave</p>
            <p className="text-2xl font-bold text-blue-600">{leaveBalance.sickLeave || 0} days</p>
          </div>
          <div className="bg-green-100 p-4 rounded-lg">
            <p className="text-sm text-green-800">Casual Leave</p>
            <p className="text-2xl font-bold text-green-600">{leaveBalance.casualLeave || 0} days</p>
          </div>
          {/* <div className="bg-yellow-100 p-4 rounded-lg">
            <p className="text-sm text-yellow-800">Floater Leave</p>
            <p className="text-2xl font-bold text-yellow-600">{leaveBalance.floaterLeave || 0} days</p>
          </div> */}
        {/* </div>
      </div> */}

      {/* Leave Request Form */}
      {showForm && (
        <div className="mb-6 bg-white p-6 rounded-lg shadow-xl">
          <form onSubmit={handleSubmit} className="space-y-4">
            <div>
              <label className="block text-sm font-medium text-gray-700">Leave Type</label>
              <select
                required
                className="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:ring-blue-500 focus:border-blue-500"
                value={formData.type}
                onChange={(e) => setFormData({ ...formData, type: e.target.value })}
              >
                <option value="">Select Type</option>
                <option value="SICK_LEAVE">Sick Leave</option>
                <option value="PAID_LEAVE">Casual Leave</option>
                {/* <option value="FLOATER_LEAVE">Floater Leave</option> */}
              </select>
            </div>
            <div className="grid grid-cols-2 gap-4">
              <div>
                <label className="block text-sm font-medium text-gray-700">Start Date</label>
                <input
                  type="date"
                  required
                  className="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:ring-blue-500 focus:border-blue-500"
                  value={formData.startDate}
                  onChange={(e) => setFormData({ ...formData, startDate: e.target.value })}
                />
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-700">End Date</label>
                <input
                  type="date"
                  required
                  className="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:ring-blue-500 focus:border-blue-500"
                  value={formData.endDate}
                  onChange={(e) => setFormData({ ...formData, endDate: e.target.value })}
                />
              </div>
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-700">Reason</label>
              <textarea
                required
                className="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:ring-blue-500 focus:border-blue-500"
                rows={3}
                value={formData.reason}
                onChange={(e) => setFormData({ ...formData, reason: e.target.value })}
              />
            </div>
            <div className="flex justify-end space-x-3">
              <button
                type="button"
                onClick={() => setShowForm(false)}
                className="px-4 py-2 border rounded-md text-gray-600 hover:bg-gray-50"
              >
                Cancel
              </button>
              <button
                type="submit"
                className="px-6 py-2 bg-gradient-to-r from-blue-500 to-blue-600 text-white rounded-lg shadow-lg hover:shadow-xl transition duration-300"
              >
                Submit Request
              </button>
            </div>
          </form>
        </div>
      )}

      {/* Leave History */}
      <div className="grid gap-6 grid-cols-1 sm:grid-cols-2">
        {leaves.length > 0 ? (
          leaves.map((leave) => (
            <div key={leave.id} className="bg-white rounded-lg shadow-lg p-6">
              <div className="flex justify-between">
                <div>
                  <div className="flex items-center space-x-2">
                    <Calendar className="h-5 w-5 text-gray-500" />
                    <span className="font-medium text-lg text-gray-800">{leave.type}</span>
                  </div>
                  <p className="text-sm mt-1">
                    <span className="font-semibold text-blue-600">{leave.startDate}</span>
                    {" to "}
                    <span className="font-semibold text-blue-600">{leave.endDate}</span>
                  </p>
                  <p className="mt-2 text-gray-600">{leave.reason}</p>
                </div>
                <span
                  className={`flex justify-center items-center px-4 py-1 rounded-full text-sm font-semibold tracking-wide shadow-md transition-transform transform hover:scale-105 ${
                    leave.status === "PENDING"
                      ? "bg-gradient-to-r from-yellow-400 to-yellow-500 text-yellow-900"
                      : leave.status === "APPROVED"
                      ? "bg-gradient-to-r from-green-400 to-green-500 text-green-900"
                      : "bg-gradient-to-r from-red-400 to-red-500 text-red-900"
                  }`}
                >
                  {leave.status}
                </span>
              </div>
            </div>
          ))
        ) : (
          <p className="text-gray-600">No leave requests found.</p>
        )}
      </div>
    </div>
  );
}