import React, { useEffect, useState } from "react";
import { AttendanceService } from "../../service/attendanceService";
import * as XLSX from "xlsx";

const Attendance = () => {
  const [attendanceData, setAttendanceData] = useState([]);
  const [startDate, setStartDate] = useState(getTodayDate());
  const [endDate, setEndDate] = useState(getTodayDate());
  const [error, setError] = useState(null);
  const [currentPage, setCurrentPage] = useState(1);
  const recordsPerPage = 5;

  useEffect(() => {
    fetchAttendance(startDate, endDate);
  }, []);

  const fetchAttendance = async (startDate, endDate) => {
    try {
      const data = await AttendanceService.getAllAttendanceSummary(startDate, endDate);
      setAttendanceData(data);
      setCurrentPage(1);
      setError(null);
    } catch (err) {
      setError("Failed to fetch attendance. Please check the dates and try again.");
    }
  };

  const handleDownload = async () => {
    try {
      const attendanceLeaves = await AttendanceService.getAttendanceLeaves(startDate, endDate);
      if (attendanceLeaves instanceof Blob) {
        const url = window.URL.createObjectURL(attendanceLeaves);
        const a = document.createElement("a");
        a.style.display = "none";
        a.href = url;
        a.download = `attendance_leaves_${startDate}_to_${endDate}.xlsx`;
        document.body.appendChild(a);
        a.click();
        window.URL.revokeObjectURL(url);
      } else {
        const worksheet = XLSX.utils.json_to_sheet(attendanceLeaves);
        const workbook = XLSX.utils.book_new();
        XLSX.utils.book_append_sheet(workbook, worksheet, "Attendance Leaves");
        XLSX.writeFile(workbook, `attendance_leaves_${startDate}_to_${endDate}.xlsx`);
      }
    } catch (error) {
      console.error("Error downloading attendance:", error.message);
    }
  };

  // Helper function to get today's date in YYYY-MM-DD format
  function getTodayDate() {
    return new Date().toISOString().split("T")[0];
  }

  // Pagination Logic
  const lastIndex = currentPage * recordsPerPage;
  const firstIndex = lastIndex - recordsPerPage;
  const currentRecords = attendanceData.slice(firstIndex, lastIndex);
  const totalPages = Math.ceil(attendanceData.length / recordsPerPage);

  return (
    <div className="p-6 bg-gray-100 min-h-screen">
      <h1 className="text-2xl font-bold mb-4 text-gray-700">Attendance Management</h1>

      {/* Date Picker, Fetch & Download Buttons */}
      <div className="flex justify-between mb-6">
        <div className="flex gap-2">
          <input
            type="date"
            value={startDate}
            onChange={(e) => setStartDate(e.target.value)}
            className="p-2 border border-gray-300 rounded w-40 focus:outline-none focus:ring-2 focus:ring-blue-500"
          />
          <input
            type="date"
            value={endDate}
            onChange={(e) => setEndDate(e.target.value)}
            className="p-2 border border-gray-300 rounded w-40 focus:outline-none focus:ring-2 focus:ring-blue-500"
          />
          <button
            onClick={() => fetchAttendance(startDate, endDate)}
            className="bg-blue-600 hover:bg-blue-700 text-white px-4 py-2 rounded shadow"
          >
            Fetch Attendance
          </button>
          <button
            onClick={handleDownload}
            className="bg-green-600 hover:bg-green-700 text-white px-4 py-2 rounded shadow"
          >
            Download
          </button>
        </div>
      </div>

      {/* Error Message */}
      {error && <p className="text-red-500 mb-4">{error}</p>}

      {/* Attendance Table */}
      <div className="overflow-x-auto">
        <table className="min-w-[1000px] w-full border-collapse border border-gray-300">
          <thead className="bg-gray-200">
            <tr className="text-left">
              <th className="p-3 border w-32">Employee ID</th>
              <th className="p-3 border w-40">Check-In Time</th>
              <th className="p-3 border w-40">Check-Out Time</th>
              <th className="p-3 border w-52">Work Description</th>
              <th className="p-3 border w-28">Working Hours</th>
              <th className="p-3 border w-28">Date</th>
              <th className="p-3 border w-28">Status</th>
              <th className="p-3 border w-32">Department</th>
              <th className="p-3 border w-32">Designation</th>
              <th className="p-3 border w-52">Remarks</th>
            </tr>
          </thead>
          <tbody>
            {currentRecords.length > 0 ? (
              currentRecords.map((record, index) => (
                <tr key={index} className="border hover:bg-gray-100">
                  <td className="p-3 border">{record.employeeId}</td>
                  <td className="p-3 border">{record.checkInTime ? new Date(record.checkInTime).toLocaleString() : "N/A"}</td>
                  <td className="p-3 border">{record.checkOutTime ? new Date(record.checkOutTime).toLocaleString() : "N/A"}</td>
                  <td className="p-3 border">{record.workDescription || "N/A"}</td>
                  <td className="p-3 border">{record.workingHours ? record.workingHours.toFixed(2) : "N/A"}</td>
                  <td className="p-3 border">{record.date ? new Date(record.date).toLocaleDateString() : "N/A"}</td>
                  <td className="p-3 border">{record.status || "N/A"}</td>
                  <td className="p-3 border">{record.department || "N/A"}</td>
                  <td className="p-3 border">{record.designation || "N/A"}</td>
                  <td className="p-3 border">{record.remarks || "N/A"}</td>
                </tr>
              ))
            ) : (
              <tr>
                <td colSpan="10" className="text-center p-4">No attendance records found.</td>
              </tr>
            )}
          </tbody>
        </table>
      </div>

      {/* Pagination Controls */}
      {attendanceData.length > recordsPerPage && (
        <div className="flex justify-between items-center mt-4">
          <button
            onClick={() => setCurrentPage((prev) => Math.max(prev - 1, 1))}
            disabled={currentPage === 1}
            className={`px-4 py-2 rounded ${
              currentPage === 1 ? "bg-gray-300 cursor-not-allowed" : "bg-blue-600 hover:bg-blue-700 text-white"
            }`}
          >
            Previous
          </button>

          <span className="text-gray-700">
            Page {currentPage} of {totalPages}
          </span>

          <button
            onClick={() => setCurrentPage((prev) => Math.min(prev + 1, totalPages))}
            disabled={currentPage === totalPages}
            className={`px-4 py-2 rounded ${
              currentPage === totalPages ? "bg-gray-300 cursor-not-allowed" : "bg-blue-600 hover:bg-blue-700 text-white"
            }`}
          >
            Next
          </button>
        </div>
      )}
    </div>
  );
};

export default Attendance;