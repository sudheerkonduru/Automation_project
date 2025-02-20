import React from 'react'
import HrSidebar from '../components/Dashboard/HrSideBar'
import Navbar from '../components/Dashboard/Navbar'
import { Outlet } from 'react-router-dom'

const HrDashboard = () => {
    return (
        <div className='flex'>
          <HrSidebar/>
          <div className='flex-1 ml-64 bg-gray-100 h-screen'>
            <Navbar />
            <Outlet/>
          </div>
        </div>
      )
}

export default HrDashboard

