import { useState } from 'react'
import { createBrowserRouter, RouterProvider } from 'react-router-dom'
import reactLogo from './assets/react.svg'
import viteLogo from '/vite.svg'
import './App.css'
import HomePage from './pages/HomePage'

const router = createBrowserRouter([{ path: '/', element: <HomePage /> }])

function App() {
  return <RouterProvider router={router} />
}

export default App;