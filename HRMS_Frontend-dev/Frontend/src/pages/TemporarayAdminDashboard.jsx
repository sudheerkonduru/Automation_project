import React from 'react'
import TemporaryAdminSidebar from '../components/Dashboard/TemporaryAdminSidebar'
import Navbar from '../components/Dashboard/Navbar'
import { Outlet } from 'react-router-dom'

const TemporarayAdminDashboard = () => {
  return (
    <div className='flex'>
      <TemporaryAdminSidebar/>
      <div className='flex-1 ml-64 bg-gray-100 h-screen'>
        <Navbar />
        <Outlet />
      </div>

    </div>
  )
}

export default TemporarayAdminDashboard
