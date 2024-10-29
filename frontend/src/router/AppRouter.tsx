import React from 'react'
import { Route, Routes } from 'react-router-dom'
import Calendar from '../components/Calendar'

const AppRouter:React.FC = () => {
  return (
      <Routes>
          <Route path="/calendar" element={<Calendar />} />
    </Routes>
  )
}

export default AppRouter