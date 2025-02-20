import React, { useEffect, useState } from "react";
import { LeaveService } from "../../service/leaveService";
import { EmployeeService } from "../../service/employeeService";
import { Check, X } from "lucide-react";

// Modal Component to show Leave Balance details
function LeaveBalanceModal({ isOpen, leaveBalance, onClose }) {
  if (!isOpen || !leaveBalance) return null;

  return (
    <div className="fixed inset-0 bg-black bg-opacity-50 flex justify-center items-center">
      <div className="bg-white p-6 rounded-lg shadow-lg w-1/3">
        <h3 className="text-2xl font-semibold mb-4 text-gray-700">
          Leave Balance for {leaveBalance.leaveType}
        </h3>
        <div className="mb-4">
          <p><strong className="text-gray-800">Employee ID:</strong> {leaveBalance.employeeId}</p>
          <p><strong className="text-gray-800">Total Leaves:</strong> {leaveBalance.totalLeaves}</p>
          <p><strong className="text-gray-800">Used Leaves:</strong> {leaveBalance.usedLeaves}</p>
          <p><strong className="text-gray-800">Remaining Leaves:</strong> {leaveBalance.remainingLeaves}</p>
          <p><strong className="text-gray-800">Year:</strong> {leaveBalance.year}</p>
        </div>
        <button
          onClick={onClose}
          className="mt-4 p-2 bg-red-500 text-white rounded-lg hover:bg-red-600 transition-all"
        >
          Close
        </button>
      </div>
    </div>
  );
}

export default function LeaveList() {
  const [leaves, setLeaves] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [leaveBalances, setLeaveBalances] = useState([]);
  const [selectedLeaveBalance, setSelectedLeaveBalance] = useState(null);
  const [year, setYear] = useState(new Date().getFullYear());
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [statusFilter, setStatusFilter] = useState('');
  const [employeeIdFilter, setEmployeeIdFilter] = useState('');
  const [currentPage, setCurrentPage] = useState(1);
  const itemsPerPage = 4;

  useEffect(() => {
    const fetchLeavesAndEmployees = async () => {
      try {
        const leaveData = await LeaveService.hr.getAllLeaves();
        const employeeData = await EmployeeService.getEmployees();
        const employeeMap = employeeData.reduce((map, emp) => {
          map[emp.employeeId.toString()] = emp.fullName;
          return map;
        }, {});

        const enrichedLeaves = leaveData.map((leave) => {
          const employeeName = employeeMap[leave.employeeId.toString()] || "Unknown";
          return {
            ...leave,
            employeeName,
          };
        });

        setLeaves(enrichedLeaves);
        setLoading(false);
      } catch (err) {
        setError("Error fetching leave or employee data.");
        setLoading(false);
      }
    };

    const fetchLeaveBalances = async () => {
      try {
        const balances = await LeaveService.hr.getLeaveBalances(year);
        setLeaveBalances(balances);
      } catch (err) {
        setError("Error fetching leave balances.");
        setLoading(false);
      }
    };

    fetchLeavesAndEmployees();
    fetchLeaveBalances();
  }, [year]);

  const handleUpdateStatus = (leaveId, status) => {
    const requestBody = {
      status: status.toUpperCase(),
      remarks: status === "APPROVED" ? "Approved by HR" : "Rejected by HR",
    };

    LeaveService.hr.processLeaveRequest(leaveId, requestBody)
      .then((updatedLeave) => {
        setLeaves((prevLeaves) =>
          prevLeaves.map((leave) =>
            leave.id === leaveId ? { ...leave, status: updatedLeave.status } : leave
          )
        );
      })
      .catch(() => console.error("Error updating leave status."));
  };

  const handleShowLeaveBalance = (employeeId) => {
    const balance = leaveBalances.find(
      (balance) => balance.employeeId === employeeId
    );
    setSelectedLeaveBalance(balance || null);
    setIsModalOpen(true);
  };

  const handleCloseModal = () => {
    setIsModalOpen(false);
    setSelectedLeaveBalance(null);
  };

  // Filtered and paginated leaves
  const filteredLeaves = leaves.filter((leave) => {
    const statusMatch = statusFilter ? leave.status === statusFilter : true;
    const employeeMatch = employeeIdFilter ? leave.employeeId === employeeIdFilter : true;
    return statusMatch && employeeMatch;
  });

  // Pagination logic
  const indexOfLastLeave = currentPage * itemsPerPage;
  const indexOfFirstLeave = indexOfLastLeave - itemsPerPage;
  const currentLeaves = filteredLeaves.slice(indexOfFirstLeave, indexOfLastLeave);

  const totalPages = Math.ceil(filteredLeaves.length / itemsPerPage);

  const handlePageChange = (pageNumber) => {
    setCurrentPage(pageNumber);
  };

  if (loading) {
    return <div className="text-center">Loading...</div>;
  }

  if (error) {
    return <div className="text-center text-red-600">{error}</div>;
  }

  return (
    <div className="p-8 bg-gray-50 min-h-screen">
      {/* Header and Filters */}
      <div className="flex justify-between items-center mb-6">
      <h1 className="text-3xl font-bold mb-4 text-gray-700">Leave Requests</h1>
        <div className="flex items-center space-x-4 bg-gray-200 p-3 rounded-lg shadow-lg">
          <select
            value={statusFilter}
            onChange={(e) => setStatusFilter(e.target.value)}
            className="p-3 pr-1 border-2 border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
          >
            <option value="">All Status</option>
            <option value="PENDING">Pending</option>
            <option value="APPROVED">Approved</option>
            <option value="REJECTED">Rejected</option>
          </select>
          {/* <input
            type="text"
            placeholder="Employee ID"
            value={employeeIdFilter}
            onChange={(e) => setEmployeeIdFilter(e.target.value)}
            className="p-3 border-2 border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
          /> */}
        </div>
      </div>

      <div className="grid gap-6 grid-cols-1 sm:grid-cols-2">
        {currentLeaves.length > 0 ? (
          currentLeaves.map((leave) => (
            <div key={leave.id} className="bg-white rounded-lg shadow-lg p-6 hover:shadow-2xl transition-all">
              <div className="flex justify-between items-start">
                <div>
                  <h3 className="text-xl font-semibold text-gray-800">{leave.employeeName}</h3>
                  <h4 className="text-sm text-gray-600">Employee Id: {leave.employeeId}</h4>
                  <p className="text-gray-600 mt-2">Leave Type: {leave.leaveType}</p>
                  <p className="text-sm text-gray-500 mt-1">
                    {leave.startDate} to {leave.endDate}
                  </p>
                  <p className="mt-4 text-gray-700">{leave.reason}</p>
                </div>
                <div className="flex flex-col items-end">
                  <span
                    className={`px-3 py-1 rounded-full text-sm ${
                      leave.status === "PENDING"
                        ? "bg-yellow-200 text-yellow-700"
                        : leave.status === "APPROVED"
                        ? "bg-green-200 text-green-700"
                        : "bg-red-200 text-red-700"
                    }`}
                  >
                    {leave.status}
                  </span>
                  {leave.status === "PENDING" && (
                    <div className="mt-4 flex space-x-4">
                      <button
                        onClick={() => handleUpdateStatus(leave.id, "APPROVED")}
                        className="p-2 bg-green-100 text-green-600 rounded-full hover:bg-green-200"
                      >
                        <Check className="h-5 w-5" />
                      </button>
                      <button
                        onClick={() => handleUpdateStatus(leave.id, "REJECTED")}
                        className="p-2 bg-red-100 text-red-600 rounded-full hover:bg-red-200"
                      >
                        <X className="h-5 w-5" />
                      </button>
                    </div>
                  )}
                  <button
                    onClick={() => handleShowLeaveBalance(leave.employeeId)}
                    className="mt-4 p-2 bg-blue-100 text-blue-600 rounded-full hover:bg-blue-200"
                  >
                    Show Leave Balance
                  </button>
                </div>
              </div>
            </div>
          ))
        ) : (
          <p className="text-gray-600">No leave requests found.</p>
        )}
      </div>

      {/* Pagination */}
      <div className="mt-6 flex justify-center items-center space-x-4">
        <button
          onClick={() => handlePageChange(currentPage - 1)}
          disabled={currentPage === 1}
          className="px-4 py-2 bg-gray-300 rounded-lg hover:bg-gray-400 disabled:opacity-50"
        >
          Prev
        </button>
        <span className="text-xl font-semibold">{currentPage}</span>
        <button
          onClick={() => handlePageChange(currentPage + 1)}
          disabled={currentPage === totalPages}
          className="px-4 py-2 bg-gray-300 rounded-lg hover:bg-gray-400 disabled:opacity-50"
        >
          Next
        </button>
      </div>

      {/* Leave Balance Modal */}
      <LeaveBalanceModal
        isOpen={isModalOpen}
        leaveBalance={selectedLeaveBalance}
        onClose={handleCloseModal}
      />
    </div>
  );
}
