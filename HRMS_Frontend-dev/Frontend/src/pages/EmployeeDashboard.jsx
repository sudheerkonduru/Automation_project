import React from 'react'
import EmpSidebar from '../components/EmpDashboard/EmpSidebar'
import Navbar from '../components/Dashboard/Navbar'
import { Outlet } from 'react-router-dom'


const EmployeeDashboard = () => {
  return (
    <div className='flex'>
      <EmpSidebar/>
      <div className='flex-1 ml-64 bg-gray-100 h-screen'>
        <Navbar />
        <Outlet />
      </div>

    </div>
  )

}

export default EmployeeDashboard