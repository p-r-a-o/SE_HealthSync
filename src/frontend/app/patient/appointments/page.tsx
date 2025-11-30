"use client"

import { useState, useEffect } from "react"
import { useAuth } from "@/lib/auth-context"
import { Card } from "@/components/ui/card"
import { Button } from "@/components/ui/button"
import Link from "next/link"
import api from "@/lib/api"

export default function PatientAppointmentsPage() {
  const { user } = useAuth()
  const [appointments, setAppointments] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState("")

  useEffect(() => {
    const fetchAppointments = async () => {
      try {
        const response = await api.get(`/appointments/patient/${user?.userId}`)
        setAppointments(response.data)
      } catch (err) {
        setError("Failed to load appointments")
        console.error(err)
      } finally {
        setLoading(false)
      }
    }

    if (user?.userId) {
      fetchAppointments()
    }
  }, [user?.userId])

  if (loading) return <div className="container mx-auto p-6">Loading...</div>

  return (
    <div className="container mx-auto p-6">
      <div className="flex justify-between items-center mb-6">
        <h1 className="text-3xl font-bold">My Appointments</h1>
        <Link href="/patient/appointments/book">
          <Button className="bg-blue-600 hover:bg-blue-700">Book New Appointment</Button>
        </Link>
      </div>

      {error && <div className="mb-4 p-3 bg-red-100 text-red-700 rounded">{error}</div>}

      {appointments.length === 0 ? (
        <Card className="p-8 text-center">
          <p className="text-gray-600">No appointments found</p>
        </Card>
      ) : (
        <div className="grid grid-cols-1 gap-4">
          {appointments.map((apt: any) => (
            <Card key={apt.appointmentId} className="p-6">
              <div className="grid grid-cols-1 md:grid-cols-4 gap-4">
                <div>
                  <p className="text-sm text-gray-600">Doctor</p>
                  <p className="font-semibold">{apt.doctorName}</p>
                </div>
                <div>
                  <p className="text-sm text-gray-600">Date & Time</p>
                  <p className="font-semibold">
                    {apt.appointmentDate} {apt.startTime}
                  </p>
                </div>
                <div>
                  <p className="text-sm text-gray-600">Type</p>
                  <p className="font-semibold">{apt.type}</p>
                </div>
                <div>
                  <p className="text-sm text-gray-600">Status</p>
                  <p className={`font-semibold ${apt.status === "CANCELLED" ? "text-red-600" : "text-green-600"}`}>
                    {apt.status}
                  </p>
                </div>
              </div>
              {apt.notes && <p className="mt-4 text-gray-700 text-sm">Notes: {apt.notes}</p>}
            </Card>
          ))}
        </div>
      )}
    </div>
  )
}
