import React, { useState, useEffect } from "react";
import { AttendanceService } from "../../service/attendanceService";
import { AuthService } from "../../service/authService";

const formatTime = (timeString) => {
  if (!timeString) return "--:--";
  try {
    return timeString.split("T")[1]?.split(".")[0]?.slice(0, 5) || "--:--";
  } catch (error) {
    console.error("Error formatting time:", error.message);
    return "--:--";
  }
};

const EmployeeAttendance = () => {
  const { user, isAuthenticated } = AuthService.getUserData();
  const [attendanceRecords, setAttendanceRecords] = useState([]);
  const [isCheckedIn, setIsCheckedIn] = useState(false);
  const [workDescription, setWorkDescription] = useState("");
  const [timer, setTimer] = useState(0);
  const [showPopup, setShowPopup] = useState(false);
  const [allowCheckout, setAllowCheckout] = useState(false);
  const [checkInTimestamp, setCheckInTimestamp] = useState(null);

  const employeeId = user?.employeeId;
  const today = new Date().toISOString().split("T")[0];

  useEffect(() => {
    if (!isAuthenticated) {
      console.error("User is not authenticated. Redirecting to login...");
      window.location.href = "/login";
    }
  }, [isAuthenticated]);

  useEffect(() => {
    if (employeeId) {
      fetchAttendance();
    }
  }, [employeeId]);

  useEffect(() => {
    const storedCheckInTimestamp = sessionStorage.getItem("checkInTimestamp");
    if (storedCheckInTimestamp) {
      setCheckInTimestamp(Number(storedCheckInTimestamp));
      setIsCheckedIn(true);
    }
  }, []);

  useEffect(() => {
    let interval;
    if (isCheckedIn && checkInTimestamp) {
      interval = setInterval(() => {
        setTimer(Math.floor((Date.now() - checkInTimestamp) / 1000));
      }, 1000);
    } else {
      clearInterval(interval);
    }
    return () => clearInterval(interval);
  }, [isCheckedIn, checkInTimestamp]);

  const fetchAttendance = async () => {
    try {
      const data = await AttendanceService.getEmployeeById(employeeId);
      const records = Array.isArray(data) ? data : [data];
      setAttendanceRecords(records);
      const todayRecord = records.find((rec) => rec.date.split("T")[0] === today);
      if (todayRecord?.checkInTime && !todayRecord?.checkOutTime) {
        setIsCheckedIn(true);
        const checkInTime = new Date(todayRecord.checkInTime).getTime();
        setCheckInTimestamp(checkInTime);
        sessionStorage.setItem("checkInTimestamp", checkInTime);
      }
    } catch (error) {
      console.error("Error fetching attendance:", error.message);
    }
  };

  const handleCheckIn = async () => {
    if (isCheckedIn) return;
    try {
      await AttendanceService.checkIn(employeeId);
      const checkInTime = Date.now();
      setCheckInTimestamp(checkInTime);
      localStorage.setItem("checkInTimestamp", checkInTime);
      await fetchAttendance();
      setIsCheckedIn(true);
    } catch (error) {
      console.error(error.message);
    }
  };

  const handleCheckOut = async () => {
    if (!isCheckedIn || !allowCheckout) return;
    try {
      await AttendanceService.checkOut(employeeId, workDescription);
      setWorkDescription("");
      await fetchAttendance();
      setIsCheckedIn(false);
      setAllowCheckout(false);
      setShowPopup(false);
      localStorage.removeItem("checkInTimestamp");
    } catch (error) {
      console.error(error.message);
    }
  };

  const handleHover = () => {
    if (timer < 32400 && !allowCheckout) setShowPopup(true);
  };

  return (
    <div className="p-6">
      <div className="flex justify-between items-center mb-6">
        <h2 className="text-2xl font-bold text-gray-800">Attendance</h2>
        <div className="relative">
          <button
            onClick={isCheckedIn ? handleCheckOut : handleCheckIn}
            onMouseEnter={isCheckedIn ? handleHover : null}
            className={`px-5 py-2 text-white font-semibold rounded-lg shadow-md ${
              isCheckedIn ? "bg-blue-600 hover:bg-blue-700" : "bg-green-600 hover:bg-green-700"
            }`}
            disabled={!isCheckedIn && attendanceRecords.some((rec) => rec.date === today && rec.checkOutTime)}
          >
            {isCheckedIn ? `Check Out (${Math.floor(timer / 3600)}h ${Math.floor((timer % 3600) / 60)}m ${timer % 60}s)` : "Check In"}
          </button>
          {showPopup && (
            <div className="fixed inset-0 flex items-center justify-center bg-gray-800 bg-opacity-50">
              <div className="bg-white shadow-lg rounded-lg p-10 w-96 text-center">
                <p className="text-lg text-gray-700 font-semibold">You are logging out early. Do you wish to continue?</p>
                <div className="flex justify-center gap-6 mt-6">
                  <button
                    className="bg-red-500 text-white px-6 py-2 rounded-lg"
                    onClick={() => {
                      setAllowCheckout(true);
                      setShowPopup(false);
                    }}
                  >
                    Yes
                  </button>
                  <button
                    className="bg-gray-300 px-6 py-2 rounded-lg"
                    onClick={() => setShowPopup(false)}
                  >
                    No
                  </button>
                </div>
              </div>
            </div>
          )}
        </div>
      </div>
      {isCheckedIn && (
        <div className="mb-4">
          <textarea
            className="w-full p-2 border rounded"
            placeholder="Enter work description..."
            value={workDescription}
            onChange={(e) => setWorkDescription(e.target.value)}
          ></textarea>
        </div>
      )}
      <table className="min-w-full bg-white shadow-md rounded-lg">
        <thead className="bg-gray-100">
          <tr>
            <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Date</th>
            <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Check In</th>
            <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Check Out</th>
            <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Working Hours</th>
            <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Work Description</th>
            <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Status</th>
          </tr>
        </thead>
        <tbody>
          {attendanceRecords.map((record, index) => (
            <tr key={index} className="border-t">
              <td className="px-6 py-4">{record.date.split("T")[0]}</td>
              <td className="px-6 py-4">{formatTime(record.checkInTime)}</td>
              <td className="px-6 py-4">{formatTime(record.checkOutTime)}</td>
              <td className="px-6 py-4">{record.workingHours}</td>
              <td className="px-6 py-4">{record.workDescription}</td>
              <td className="px-6 py-4">{record.status}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
};

export default EmployeeAttendance;
