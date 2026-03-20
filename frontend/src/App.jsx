import { createBrowserRouter, RouterProvider } from 'react-router-dom'
import { AuthProvider } from './features/auth/AuthContext'
import { ProtectedRoute, AdminRoute } from './shared/components/ProtectedRoute'
import { PublicOnlyRoute } from './shared/components/ProtectedRoute'
import HomePage from './pages/HomePage'
import Login from './pages/Login'
import Signup from './pages/Signup'
import Dashboard from './pages/Dashboard'
import AdminPage from './pages/AdminPage'
import Leaderboard from './pages/Leaderboard'

import './App.css'

const router = createBrowserRouter([
  {
    path: '/',
    element: (
      <PublicOnlyRoute>
        <HomePage />
      </PublicOnlyRoute>
    ),
  },
  {
    path: '/login',
    element: (
      <PublicOnlyRoute>
        <Login />
      </PublicOnlyRoute>
    ),
  },
  {
    path: '/signup',
    element: (
      <PublicOnlyRoute>
        <Signup />
      </PublicOnlyRoute>
    ),
  },
  {
    path: '/dashboard',
    element: (
      <ProtectedRoute>
        <Dashboard />
      </ProtectedRoute>
    ),
  },
  {
    path: '/leaderboard',
    element: (
      <ProtectedRoute>
        <Leaderboard />
      </ProtectedRoute>
    ),
  },
  {
    path: '/admin',
    element: (
      <AdminRoute>
        <AdminPage />
      </AdminRoute>
    ),
  },
])

function App() {
  return (
    <AuthProvider>
      <RouterProvider router={router} />
    </AuthProvider>
  )
}

export default App
