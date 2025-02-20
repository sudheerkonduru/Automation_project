import {React} from 'react';
import { EmployeeService } from '../../service/employeeService';
import { FaUsers, FaBuilding, FaFileAlt, FaCheckCircle, FaHourglassHalf, FaTimesCircle } from 'react-icons/fa';



const AdminSummary = () => {   
   


  return (
    <div className='p-6 bg-gray-100 min-h-screen'>
        <h3 className='text-2xl font-bold text-gray-800'>Dashboard</h3>
        <div className='grid grid-cols-1 md:grid-cols-4 gap-6 mt-6'>
            {/* Total Employees Card */}
            <div className='bg-gray-200 p-6 rounded-lg shadow-md text-center transition-transform transform hover:scale-105 hover:shadow-xl hover:bg-gray-300'
            >
                <p className='text-lg font-semibold text-gray-700'>Total Employees</p>
                <div className='flex justify-between items-center mt-4'>
                    <FaUsers className='text-6xl text-gray-700'/>
                    <p className='text-4xl font-bold text-gray-800'>{EmployeeService.getTotalEmployees()}</p>
                </div>
            </div>

            {/* Total Departments Card */}
            <div className='bg-gray-200 p-6 rounded-lg shadow-md text-center transition-transform transform hover:scale-105 hover:shadow-xl hover:bg-gray-300'>
                <p className='text-lg font-semibold text-gray-700'>Total Departments</p>
                <div className='flex justify-between items-center mt-4'>
                    <FaBuilding className='text-6xl text-blue-500'/>
                    <p className='text-4xl font-bold text-gray-800'>3</p>
                </div>
            
            </div>
            </div>
            
            <h3 className='text-2xl font-bold text-gray-800 mt-3'>Leave Requests</h3>
            <div className='grid grid-cols-1 md:grid-cols-4 gap-6 mt-6'>
            {/* Total Leaves Applied Card */}
            <div className='bg-gray-200 p-6 rounded-lg shadow-md text-center transition-transform transform hover:scale-105 hover:shadow-xl hover:bg-gray-300'>
                <p className='text-lg font-semibold text-gray-700'>Total Leaves Applied</p>
                <div className='flex justify-between items-center mt-4'>
                    <FaFileAlt className='text-6xl text-purple-500'/>
                    <p className='text-4xl font-bold text-gray-800'>5</p>
                </div>
            </div>

            {/* Leaves Approved Card */}
            <div className='bg-gray-200 p-6 rounded-lg shadow-md text-center transition-transform transform hover:scale-105 hover:shadow-xl hover:bg-gray-300'>
                <p className='text-lg font-semibold text-gray-700'>Leaves Approved</p>
                <div className='flex justify-between items-center mt-4'>
                    <FaCheckCircle className='text-6xl text-green-500'/>
                    <p className='text-4xl font-bold text-gray-800'>2</p>
                </div>
            </div>

            {/* Leaves Pending Card */}
            <div className='bg-gray-200 p-6 rounded-lg shadow-md text-center transition-transform transform hover:scale-105 hover:shadow-xl hover:bg-gray-300'>
                <p className='text-lg font-semibold text-gray-700'>Leaves Pending</p>
                <div className='flex justify-between items-center mt-4'>
                    <FaHourglassHalf className='text-6xl text-yellow-500'/>
                    <p className='text-4xl font-bold text-gray-800'>2</p>
                </div>
            </div>

            {/* Leaves Rejected Card */}
            <div className='bg-gray-200 p-6 rounded-lg shadow-md text-center transition-transform transform hover:scale-105 hover:shadow-xl hover:bg-gray-300'>
                <p className='text-lg font-semibold text-gray-700'>Leaves Rejected</p>
                <div className='flex justify-between items-center mt-4'>
                    <FaTimesCircle className='text-6xl text-red-500'/>
                    <p className='text-4xl font-bold text-gray-800'>1</p>
                </div>
            </div>
        </div>
    </div>
  );
};

export default AdminSummary;



// import React from 'react'
// import SummaryCard from './SummaryCard'
// import { EmployeeService } from '../../service/employeeService'
// import { FaUsers, FaBuilding, FaFileAlt, FaCheckCircle, FaHourglassHalf, FaTimesCircle } from 'react-icons/fa'

// const AdminSummary = () => {
//   return (
//     <div className='p-6'>
//         <h3 className='text-2xl font-bold'>Dashboard Overview</h3>
//         <div className='grid grid-cols-1 md:grid-cols-2 gap-6 mt-6'>
//             <SummaryCard icon={<FaUsers/>} text="Total Employees" number={EmployeeService.getTotalEmployees()} color="bg-teal-600"/>
//             <SummaryCard icon={<FaBuilding/>} text="Total Departments" number={3} color="bg-yellow-600"/>
//         </div>

//         <div className='mt-12'>
//             <h4 className='text-2xl font-bold'>Leave Details</h4>
//             <div className='grid grid-cols-1 md:grid-cols-2 gap-6 mt-6'>
//             <SummaryCard icon={<FaFileAlt/>} text="Leave Applied" number={5} color="bg-teal-600"/>
//             <SummaryCard icon={<FaCheckCircle/>} text="Leave Approved" number={2} color="bg-green-600"/>
//             <SummaryCard icon={<FaHourglassHalf/>} text="Leave Pending" number={2} color="bg-yellow-600"/>
//             <SummaryCard icon={<FaTimesCircle/>} text="Leave Rejected" number={1} color="bg-red-600"/>
//             </div>
//         </div>
//     </div>
//   )
// }

// export default AdminSummary