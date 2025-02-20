import React, { useState } from "react";
import { NavLink } from "react-router-dom";
import { FaCogs, FaUser, FaCalendar, FaClock, FaFile } from "react-icons/fa";

const EmpSidebar = () => {
  const [settingsOpen, setSettingsOpen] = useState(false);

  const toggleSettings = () => {
    setSettingsOpen(!settingsOpen);
  };

  return (
    <div className="bg-gray-800 text-white h-screen fixed left-0 top-0 bottom-0 space-y-2 w-64">
      <div className="bg-teal-700 h-14 flex items-center justify-center">
        <h3 className="text-2xl text-center font-serif">HRMS</h3>
      </div>
      <div className="px-4">
        <NavLink
          to="/employee/dashboard"
          className={({ isActive }) => `${isActive ? "bg-teal-500" : " "} flex items-center space-x-4 py-2.5 px-4 rounded`}
          end
        >
          <FaUser />
          <span>Profile</span>
        </NavLink>
        <NavLink
          to="/employee/dashboard/leaves"
          className={({ isActive }) => `${isActive ? "bg-teal-500" : " "} flex items-center space-x-4 py-2.5 px-4 rounded`}
          end
        >
          <FaCalendar />
          <span>Leaves</span>
        </NavLink>
        <NavLink
          to="/employee/dashboard/attendance"
          className={({ isActive }) => `${isActive ? "bg-teal-500" : " "} flex items-center space-x-4 py-2.5 px-4 rounded`}
          end
        >
          <FaClock />
          <span>Attendance</span>
        </NavLink>
        <NavLink
          to="/employee/dashboard/documents"
          className={({ isActive }) => `${isActive ? "bg-teal-500" : " "} flex items-center space-x-4 py-2.5 px-4 rounded`}
          end
        >
          <FaFile />
          <span>Documents</span>
        </NavLink>

          <NavLink
            to="/employee/dashboard/change-password"
            className={({ isActive }) => `${isActive ? "bg-teal-500" : " "} flex items-center space-x-4 py-2.5 px-4 rounded`}
            onClick={toggleSettings} // Toggle the settings dropdown on click
            end
          >
            <FaCogs />
            <span>Change Password</span>
          </NavLink>

      </div>
    </div>
  );
};

export default EmpSidebar;
