import { BrowserRouter, Routes, Route, Navigate } from "react-router-dom";
import Login from "./pages/Login";
import AdminDashboard from "./pages/AdminDashboard";
import EmployeeDashboard from "./pages/EmployeeDashboard";
import HrDashboard from "./pages/HrDashboard";
import AdminSummary from "./components/Dashboard/AdminSummary";
import DepartmentList from "./components/Departments/DepartmentList";
import LeaveList from "./components/leaves/LeaveList";
import EmployeeProfile from "./components/EmpDashboard/EmployeeProfile";
import EmployeeLeaves from "./components/EmpDashboard/EmployeeLeaves";
import EmployeeAttendance from "./components/EmpDashboard/EmployeeAttendance";
import EmployeeList  from "./components/Employee/EmployeeList";
import Attendance from "./components/leaves/Attendance";
import TemporarayAdminDashboard from "./pages/TemporarayAdminDashboard";
import AccessManagement from "./components/Dashboard/AccessManagement";
import ChangePassword from "./components/EmpDashboard/ChangePassword";
import AssetTracking from "./components/Dashboard/AssetTracking";
import DocumentManagement from "./components/EmpDashboard/EmployeeDocument";
import Documents from "./components/leaves/Documents.";


function App() {
  return (
    <BrowserRouter>
    <Routes>
        <Route path="/" element={<Navigate to="./login" />}></Route>
        <Route path="/login" element={<Login />}></Route>
        <Route path="/admin/dashboard" element={<AdminDashboard />}>
          <Route index element={<AdminSummary />}></Route>
          <Route path="/admin/dashboard/profile" element={<EmployeeProfile />}></Route>
          <Route path="/admin/dashboard/departments" element={<DepartmentList />}></Route>
          <Route path="/admin/dashboard/employees" element={<EmployeeList />}></Route>
          <Route path="/admin/dashboard/leaves" element={<LeaveList />}></Route>
          <Route path="/admin/dashboard/attendance" element={<Attendance />}></Route>
          <Route path="/admin/dashboard/access" element={<AccessManagement />}></Route>
          <Route path="/admin/dashboard/assets" element={<AssetTracking />}></Route>
          <Route path="/admin/dashboard/documents" element={<Documents />}></Route>
        </Route>
        <Route path="/hr/dashboard" element={<HrDashboard />}>
          <Route index element={<AdminSummary />}></Route>
          <Route path="/hr/dashboard/profile" element={<EmployeeProfile />}></Route>
          <Route path="/hr/dashboard/departments" element={<DepartmentList />}></Route>
          <Route path="/hr/dashboard/employees" element={<EmployeeList />}></Route>
          <Route path="/hr/dashboard/leaves" element={<LeaveList />}></Route>
          <Route path="/hr/dashboard/attendance" element={<Attendance />}></Route>
          <Route path="/hr/dashboard/assets" element={<AssetTracking />}></Route>
          <Route path="/hr/dashboard/documents" element={<Documents />}></Route>

        </Route>
        <Route path="/temporary/admin/dashboard" element={<TemporarayAdminDashboard/>}>
          <Route index element={<AdminSummary />}></Route>
          <Route path="/temporary/admin/dashboard/profile" element={<EmployeeProfile />}></Route>
          <Route path="/temporary/admin/dashboard/departments" element={<DepartmentList />}></Route>
          <Route path="/temporary/admin/dashboard/employees" element={<EmployeeList />}></Route>
          <Route path="/temporary/admin/dashboard/leaves" element={<LeaveList />}></Route>
          <Route path="/temporary/admin/dashboard/attendance" element={<Attendance />}></Route>
        </Route>
        <Route path="/employee/dashboard" element={<EmployeeDashboard />}>
          <Route index element={<EmployeeProfile />}></Route>
          <Route path="/employee/dashboard/leaves" element={<EmployeeLeaves />}></Route>
          <Route path="/employee/dashboard/attendance" element={<EmployeeAttendance />}></Route>
          <Route path="/employee/dashboard/documents" element={<DocumentManagement />}></Route>
          <Route path="/employee/dashboard/Change-password" element={<ChangePassword />}></Route>
        </Route>
    </Routes>
    </BrowserRouter>
  );
}

export default App;
