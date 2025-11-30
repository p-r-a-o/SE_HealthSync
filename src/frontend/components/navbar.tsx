"use client"

import Link from "next/link"
import { useAuth } from "@/lib/auth-context"
import { Button } from "./ui/button"
import { useRouter } from "next/navigation"

export default function Navbar() {
  const { user, logout, loading } = useAuth()
  const router = useRouter()

  const handleLogout = () => {
    logout()
    router.push("/")
  }

  if (loading) return <nav className="bg-blue-600 text-white p-4">Loading...</nav>

  return (
    <nav className="bg-blue-600 text-white p-4">
      <div className="container mx-auto flex justify-between items-center">
        <Link href="/" className="text-2xl font-bold">
          HealthSync
        </Link>

        <div className="flex items-center gap-6">
          {user ? (
            <>
              <span className="text-sm">
                {user.firstName} ({user.userType})
              </span>
              <Button onClick={handleLogout} className="bg-red-600 hover:bg-red-700 text-white px-4 py-2">
                Logout
              </Button>
            </>
          ) : (
            <>
              <Link href="/auth/login">
                <Button className="bg-white text-blue-600 hover:bg-gray-200">Login</Button>
              </Link>
              <Link href="/auth/register">
                <Button className="bg-green-600 hover:bg-green-700">Register</Button>
              </Link>
            </>
          )}
        </div>
      </div>
    </nav>
  )
}
