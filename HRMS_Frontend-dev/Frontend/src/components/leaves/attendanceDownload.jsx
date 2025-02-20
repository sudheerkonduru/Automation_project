import { useState } from 'react';
import { AttendanceService } from '../../service/attendanceService';
import * as XLSX from 'xlsx';

const AttendanceDownload = () => {
  const [selectedDate, setSelectedDate] = useState('');

  const handleDateChange = async (e) => {
    const date = e.target.value;
    setSelectedDate(date);

    const [year, month] = date.split('-');
    try {
      const attendanceLeaves = await AttendanceService.getAttendanceLeaves(year, month);
      console.log(attendanceLeaves);
    } catch (error) {
      console.error(error.message);
    }
  };

  const handleDownload = async () => {
    const [year, month] = selectedDate.split('-');
    try {
      const attendanceLeaves = await AttendanceService.getAttendanceLeaves(year, month);
      if (attendanceLeaves instanceof Blob) {
        const url = window.URL.createObjectURL(attendanceLeaves);
        const a = document.createElement('a');
        a.style.display = 'none';
        a.href = url;
        a.download = `attendance_leaves_${year}_${month}.xlsx`;
        document.body.appendChild(a);
        a.click();
        window.URL.revokeObjectURL(url);
      } else {
        const worksheet = XLSX.utils.json_to_sheet(attendanceLeaves);
        const workbook = XLSX.utils.book_new();
        XLSX.utils.book_append_sheet(workbook, worksheet, 'Attendance Leaves');
        XLSX.writeFile(workbook, `attendance_leaves_${year}_${month}.xlsx`);
      }
    } catch (error) {
      console.error(error.message);
    }
  };

  return (
    <div className='flex gap-4 mb-6'>
      <input
        type="month"
        value={selectedDate}
        onChange={handleDateChange}
        className="p-2 border border-gray-300 rounded w-60 focus:outline-none focus:ring-2 focus:ring-blue-500"
      />
      <button
        onClick={handleDownload}
        className="bg-blue-600 hover:bg-blue-700 text-white px-4 py-2 rounded shadow"
      >
        Download
      </button>
    </div>
  );
};

export default AttendanceDownload;