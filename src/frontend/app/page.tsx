"use client"

import { useEffect, useState } from "react"
import { useRouter } from "next/navigation"
import { useAuth } from "@/lib/auth-context"
import Link from "next/link"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Button } from "@/components/ui/button"

export default function Home() {
  const { user, loading } = useAuth()
  const router = useRouter()
  const [mounted, setMounted] = useState(false)

  useEffect(() => {
    setMounted(true)
  }, [])

  if (!mounted || loading) {
    return <div className="flex items-center justify-center min-h-screen">Loading...</div>
  }

  if (!user) {
    return (
      <div className="min-h-screen bg-gradient-to-br from-blue-50 to-blue-100 flex items-center justify-center p-4">
        <Card className="w-full max-w-md">
          <CardHeader>
            <CardTitle className="text-center text-4xl font-bold text-blue-600">HealthSync</CardTitle>
          </CardHeader>
          <CardContent className="space-y-4">
            <p className="text-center text-gray-600">Hospital Management System</p>
            <Link href="/auth/login" className="block">
              <Button className="w-full bg-blue-600 hover:bg-blue-700">Login</Button>
            </Link>
            <Link href="/auth/register" className="block">
              <Button className="w-full bg-green-600 hover:bg-green-700">Register as Patient</Button>
            </Link>
            <p className="text-sm text-gray-500 text-center">For staff access, please contact administration</p>
          </CardContent>
        </Card>
      </div>
    )
  }

  const dashboardLinks = {
    PATIENT: [
      { label: "My Profile", href: "/patient/profile" },
      { label: "Book Appointment", href: "/patient/appointments/book" },
      { label: "My Appointments", href: "/patient/appointments" },
      { label: "Medical History", href: "/patient/medical-history" },
      { label: "My Bills", href: "/patient/bills" },
      { label: "My Prescriptions", href: "/patient/prescriptions" },
    ],
    DOCTOR: [
      { label: "My Profile", href: "/doctor/profile" },
      { label: "Set Availability", href: "/doctor/availability" },
      { label: "My Appointments", href: "/doctor/appointments" },
      { label: "Patients", href: "/doctor/patients" },
      { label: "Write Prescription", href: "/doctor/prescriptions" },
    ],
    RECEPTIONIST: [
      { label: "Book Appointment", href: "/receptionist/appointments/book" },
      { label: "All Appointments", href: "/receptionist/appointments" },
      { label: "Manage Beds", href: "/receptionist/beds" },
      { label: "Manage Bills", href: "/receptionist/bills" },
      { label: "Patients", href: "/receptionist/patients" },
    ],
    PHARMACIST: [
      { label: "My Inventory", href: "/pharmacist/inventory" },
      { label: "Prescriptions", href: "/pharmacist/prescriptions" },
    ],
  }

  const links = dashboardLinks[user.userType as keyof typeof dashboardLinks] || []

  return (
    <div className="container mx-auto p-8">
      <div className="mb-8">
        <h1 className="text-4xl font-bold mb-2">Welcome, {user.firstName}!</h1>
        <p className="text-gray-600">Role: {user.userType}</p>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
        {links.map((link) => (
          <Link key={link.href} href={link.href}>
            <Card className="p-6 cursor-pointer hover:shadow-lg transition-shadow">
              <h2 className="text-xl font-semibold text-blue-600">{link.label}</h2>
              <p className="text-gray-500 mt-2">→</p>
            </Card>
          </Link>
        ))}
      </div>
    </div>
  )
}
