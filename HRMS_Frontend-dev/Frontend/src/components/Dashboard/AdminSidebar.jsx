import React from "react";
import { NavLink } from "react-router-dom";
import { FaBuilding, FaTachometerAlt, FaUsers, FaUser, FaCalendarAlt, FaFile, FaCogs, FaAccessibleIcon, FaUniversalAccess, FaLockOpen, FaKey, FaShieldAlt, FaClock, FaLaptop } from "react-icons/fa";

const AdminSidebar = () => {
  return (
    <div className="bg-gray-800 text-white h-screen fixed left-0 top-0 bottom-0 space-y-2 w-64 z-50">
      <div className="bg-teal-700 h-14 flex items-center justify-center">
        <h3 className="text-2xl text-center font-serif">HRMS</h3>
      </div>
      <div className="px-4">
        {/* <NavLink
          to="/admin/dashboard/profile"
          className={({isActive}) => `${isActive ? "bg-teal-500" : " "} flex items-center space-x-4 py-2.5 px-4 rounded`}
          end         
        >
          <FaUser />
          <span>Profile</span>
        </NavLink> */}
        <NavLink
          to="/admin/dashboard"
          className={({isActive}) => `${isActive ? "bg-teal-500" : " "} flex items-center space-x-4 py-2.5 px-4 rounded`}
          end
        >
          <FaTachometerAlt />
          <span>Dashboard</span>
        </NavLink>
        <NavLink
          to="/admin/dashboard/employees"
          className={({isActive}) => `${isActive ? "bg-teal-500" : " "} flex items-center space-x-4 py-2.5 px-4 rounded`}
        >
          <FaUsers />
          <span>Employees</span>
        </NavLink>
        <NavLink
          to="/admin/dashboard/departments"
          className={({isActive}) => `${isActive ? "bg-teal-500" : " "} flex items-center space-x-4 py-2.5 px-4 rounded`}
        >
          <FaBuilding />
          <span>Departments</span>
        </NavLink>
        <NavLink
          to="/admin/dashboard/leaves"
          className={({isActive}) => `${isActive ? "bg-teal-500" : " "} flex items-center space-x-4 py-2.5 px-4 rounded`}
        >
          <FaCalendarAlt />
          <span>Leaves</span>
        </NavLink>
        <NavLink
          to="/admin/dashboard/attendance"
          className={({isActive}) => `${isActive ? "bg-teal-500" : " "} flex items-center space-x-4 py-2.5 px-4 rounded`}
        >
          <FaClock />
          <span>Attendance</span>
        </NavLink>
        <NavLink
          to="/admin/dashboard/access"
          className={({isActive}) => `${isActive ? "bg-teal-500" : " "} flex items-center space-x-4 py-2.5 px-4 rounded`}
        >
          <FaShieldAlt />
          <span>Access</span>
        </NavLink>
        <NavLink
          to="/admin/dashboard/assets"
          className={({isActive}) => `${isActive ? "bg-teal-500" : " "} flex items-center space-x-4 py-2.5 px-4 rounded`}
        >
          <FaLaptop/>
          <span>Assets</span>
        </NavLink>
        <NavLink
          to="/admin/dashboard/documents"
          className={({ isActive }) => `${isActive ? "bg-teal-500" : " "} flex items-center space-x-4 py-2.5 px-4 rounded`}
          end
        >
          <FaFile />
          <span>Documents</span>
        </NavLink>
      </div>
    </div>
  );
};

export default AdminSidebar;
