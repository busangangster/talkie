import React from 'react'
import Calendar from 'react-calendar'
import { Route, Routes } from 'react-router-dom'

const AppRouter:React.FC = () => {
  return (
      <Routes>
          <Route path="/calendar" element={<Calendar />} />
    </Routes>
  )
}

export default AppRouter