import React, { useEffect, useState, useCallback } from "react";
import { AuthService } from "../../service/authService";
import { EmployeeService } from "../../service/employeeService";
import { User, Mail, Building2, Shield, Phone, Calendar, MapPin, Tag, IdCardIcon, UserCircle } from "lucide-react";

export default function EmployeeProfile() {
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [employee, setEmployee] = useState(null);
  const [newPhoneNumber, setNewPhoneNumber] = useState(""); // For storing new phone number
  const [isEditing, setIsEditing] = useState(false); // Track if the profile is in edit mode
  const [isUpdating, setIsUpdating] = useState(false); // For managing update state
  const { user, isAuthenticated } = AuthService.getUserData();

  const employeeManagementLink = import.meta.env.VITE_EMPLOYEE_MANAGEMENT;

  const fetchEmployeeDetails = useCallback(async () => {
    if (!user?.employeeId) {
      setError("Employee ID is missing.");
      setLoading(false);
      return;
    }

    try {
      setLoading(true);
      const token = AuthService.getToken();

      const response = await fetch(
        `${employeeManagementLink}/api/employees/employee/${user.employeeId}`,
        {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        }
      );

      if (!response.ok) {
        const errorData = await response.json();
        throw new Error(errorData.message || "Failed to fetch employee details.");
      }

      const data = await response.json();
      setEmployee(data);
      setNewPhoneNumber(data.phoneNumber); // Set the initial phone number
      EmployeeService.updateEmployee(user.employeeId, data);
    } catch (err) {
      console.error("Error in fetching employee details:", err);
      setError(err.message || "An error occurred while fetching employee details.");
    } finally {
      setLoading(false);
    }
  }, [user?.employeeId, employeeManagementLink]);

  useEffect(() => {
    if (isAuthenticated && user?.employeeId) {
      fetchEmployeeDetails();
    } else {
      setError("User is not authenticated or Employee ID is missing.");
      setLoading(false);
    }
  }, [isAuthenticated, user?.employeeId, fetchEmployeeDetails]);

  const handleUpdatePhoneNumber = async () => {
    if (!newPhoneNumber) {
      setError("Phone number cannot be empty.");
      return;
    }

    setIsUpdating(true);

    try {
      const token = AuthService.getToken();
      const response = await fetch(
        `${employeeManagementLink}/api/employees/employee/${user.employeeId}`,
        {
          method: "PUT",
          headers: {
            Authorization: `Bearer ${token}`,
            "Content-Type": "application/json",
          },
          body: JSON.stringify({
            ...employee,
            phoneNumber: newPhoneNumber, // Update only the phone number
          }),
        }
      );

      if (!response.ok) {
        const errorData = await response.json();
        throw new Error(errorData.message || "Failed to update phone number.");
      }

      const updatedEmployee = await response.json();
      setEmployee(updatedEmployee); // Update the employee state
      setError(null); // Clear any previous errors
      setIsEditing(false); // Exit edit mode after update
    } catch (err) {
      console.error("Error in updating phone number:", err);
      setError(err.message || "An error occurred while updating phone number.");
    } finally {
      setIsUpdating(false);
    }
  };

  return (
    <div className="p-6">
      <div className="flex justify-between p-6">
      <h2 className="text-2xl font-bold mb-6">My Profile</h2>

      {/* Edit Button */}
      <button
        onClick={() => setIsEditing(true)}
        className="bg-blue-500 text-white py-2 px-6 rounded-md mb-4"
      >
        Edit Profile
      </button>
      </div>
      <div className="bg-white rounded-lg shadow-md p-6">
        {loading ? (
          <p>Loading employee details...</p>
        ) : error ? (
          <p className="text-red-500">{error}</p>
        ) : employee ? (
          <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
            <DetailField icon={User} label="Full Name" value={employee.fullName} />
            <DetailField icon={Mail} label="Email" value={employee.email} />
            <DetailField icon={IdCardIcon} label="Employee Id" value={employee.employeeId} />
            <DetailField icon={UserCircle} label="Gender" value={employee.gender} />
            
            {/* Editable Phone Number */}
            <div className="flex items-center space-x-3 p-3 bg-gray-50 rounded-lg">
              <Phone className="h-5 w-5 text-gray-500" />
              <div>
                <p className="text-sm text-gray-500">Phone Number</p>
                {isEditing ? (
                  <div className="flex items-center space-x-3">
                    <input
                      type="text"
                      className="p-2 border border-gray-300 rounded-md"
                      placeholder="Enter new mobile number"
                      value={newPhoneNumber}
                      onChange={(e) => setNewPhoneNumber(e.target.value)}
                    />
                    <button
                      onClick={handleUpdatePhoneNumber}
                      className="bg-blue-500 text-white py-2 px-6 rounded-md"
                      disabled={isUpdating}
                    >
                      {isUpdating ? "Updating..." : "Save"}
                    </button>
                  </div>
                ) : (
                  <p className="font-medium">{employee.phoneNumber || "N/A"}</p>
                )}
              </div>
            </div>

            <DetailField icon={Building2} label="Department" value={employee.department} />
            <DetailField icon={Shield} label="Role" value={employee.role} />
            <DetailField icon={Calendar} label="Date of Birth" value={employee.dateOfBirth} />
            <DetailField icon={MapPin} label="Address" value={employee.address} />
            <DetailField icon={Tag} label="Date of Joining" value={employee.dateOfJoining} />
          </div>
        ) : (
          <p>No employee data available.</p>
        )}
      </div>
    </div>
  );
}

const DetailField = ({ icon: Icon, label, value }) => (
  <div className="flex items-center space-x-3 p-3 bg-gray-50 rounded-lg">
    <Icon className="h-5 w-5 text-gray-500" />
    <div>
      <p className="text-sm text-gray-500">{label}</p>
      <p className="font-medium">{value || "N/A"}</p>
    </div>
  </div>
);



// import React, { useEffect, useState, useCallback } from "react";
// import { AuthService } from "../../service/authService";
// import { EmployeeService } from "../../service/employeeService";
// import {User, Mail, Building2, Shield, Phone, Calendar, MapPin, Briefcase, Tag, IdCardIcon, UserCircle,} from "lucide-react";

// export default function EmployeeProfile() {
//   const [loading, setLoading] = useState(true);
//   const [error, setError] = useState(null);
//   const [employee, setEmployee] = useState(null);
//   const { user, isAuthenticated } = AuthService.getUserData();

//   const employeeManagementLink = import.meta.env.VITE_EMPLOYEE_MANAGEMENT;


//   // Memoize the fetchEmployeeDetails function
//   const fetchEmployeeDetails = useCallback(async () => {
//     if (!user?.employeeId) {
//       setError("Employee ID is missing.");
//       setLoading(false);
//       return;
//     }

//     try {
//       setLoading(true);
//       const token = AuthService.getToken();

//       const response = await fetch(
//         `${employeeManagementLink}/api/employees/employee/${user.employeeId}`,
//         {
//           headers: {
//             Authorization: `Bearer ${token}`,
//           },
//         }
//       );

//       if (!response.ok) {
//         const errorData = await response.json();
//         throw new Error(errorData.message || "Failed to fetch employee details.");
//       }

//       const data = await response.json();
//       setEmployee(data);
//       EmployeeService.updateEmployee(user.employeeId, data);
//     } catch (err) {
//       console.error("Error in fetching employee details:", err);
//       setError(err.message || "An error occurred while fetching employee details.");
//     } finally {
//       setLoading(false);
//     }
//   }, [user?.employeeId, employeeManagementLink]);

//   // Use the useEffect to call the memoized function
//   useEffect(() => {
//     if (isAuthenticated && user?.employeeId) {
//       fetchEmployeeDetails();
//     } else {
//       setError("User is not authenticated or Employee ID is missing.");
//       setLoading(false);
//     }
//   }, [isAuthenticated, user?.employeeId, fetchEmployeeDetails]);

//   return (
//     <div className="p-6">
//       <h2 className="text-2xl font-bold mb-6">My Profile</h2>
//       <div className="bg-white rounded-lg shadow-md p-6">
//         {loading ? (
//           <p>Loading employee details...</p>
//         ) : error ? (
//           <p className="text-red-500">{error}</p>
//         ) : employee ? (
//           <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
//             <DetailField icon={User} label="Full Name" value={employee.fullName} />
//             <DetailField icon={Mail} label="Email" value={employee.email} />
//             <DetailField icon={IdCardIcon} label="Employee Id" value={employee.employeeId} />
//             <DetailField icon={UserCircle} label="Gender" value={employee.gender} />
//             <DetailField icon={Phone} label="Phone Number" value={employee.phoneNumber} />
//             <DetailField icon={Building2} label="Department" value={employee.department} />
//             <DetailField icon={Shield} label="Role" value={employee.role} />
//             <DetailField icon={Calendar} label="Date of Birth" value={employee.dateOfBirth} />
//             <DetailField icon={MapPin} label="Address" value={employee.address} />
//             <DetailField icon={Tag} label="Date of Joining" value={employee.dateOfJoining} />
//           </div>
//         ) : (
//           <p>No employee data available.</p>
//         )}
//       </div>
//     </div>
//   );
// }

// const DetailField = ({ icon: Icon, label, value }) => (
//   <div className="flex items-center space-x-3 p-3 bg-gray-50 rounded-lg">
//     <Icon className="h-5 w-5 text-gray-500" />
//     <div>
//       <p className="text-sm text-gray-500">{label}</p>
//       <p className="font-medium">{value || "N/A"}</p>
//     </div>
//   </div>
// );