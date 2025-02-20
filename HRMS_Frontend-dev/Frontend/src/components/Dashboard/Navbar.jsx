import React from 'react';
import { useNavigate } from 'react-router-dom';
import { LogOut } from 'lucide-react';
import { AuthService } from '../../service/authService'; 

export default function Navbar() {
  
  const navigate = useNavigate();

  const handleLogout = () => {
    AuthService.logout();
    navigate('/');
  };

  return (
    <nav className="bg-teal-600 shadow-md p-3.5">
      <div className="flex justify-between items-center">
        <h1 className="text-xl font-bold text-white">WELCOME</h1>
        <button
          onClick={handleLogout}
          className="flex items-center space-x-2 text-white hover:text-white-900"
        >
          <LogOut className="h-5 w-5 text-white" />
          <span>Logout</span>
        </button>
      </div>
    </nav>
  );
}
