"use client"

import { useState, useEffect } from "react"
import { Card } from "@/components/ui/card"
import { Button } from "@/components/ui/button"
import Link from "next/link"
import api from "@/lib/api"

export default function ReceptionistAppointmentsPage() {
  const [appointments, setAppointments] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState("")
  const [filter, setFilter] = useState("ALL")

  useEffect(() => {
    const fetchAppointments = async () => {
      try {
        let response
        if (filter === "ALL") {
          response = await api.get("/appointments")
        } else {
          response = await api.get(`/appointments/status/${filter}`)
        }
        setAppointments(response.data)
      } catch (err) {
        setError("Failed to load appointments")
      } finally {
        setLoading(false)
      }
    }

    fetchAppointments()
  }, [filter])

  const handleCancel = async (appointmentId: string) => {
    if (!confirm("Are you sure you want to cancel this appointment?")) return

    try {
      await api.put(`/appointments/${appointmentId}/cancel`)
      setAppointments(appointments.filter((apt) => apt.appointmentId !== appointmentId))
    } catch (err) {
      setError("Failed to cancel appointment")
    }
  }

  if (loading) return <div className="container mx-auto p-6">Loading...</div>

  return (
    <div className="container mx-auto p-6">
      <div className="flex justify-between items-center mb-6">
        <h1 className="text-3xl font-bold">All Appointments</h1>
        <Link href="/receptionist/appointments/book">
          <Button className="bg-blue-600 hover:bg-blue-700">Book New Appointment</Button>
        </Link>
      </div>

      {error && <div className="mb-4 p-3 bg-red-100 text-red-700 rounded">{error}</div>}

      <div className="mb-6 flex gap-2">
        {["ALL", "SCHEDULED", "COMPLETED", "CANCELLED"].map((status) => (
          <Button
            key={status}
            onClick={() => setFilter(status)}
            className={`${filter === status ? "bg-blue-600" : "bg-gray-400"} hover:bg-blue-700`}
          >
            {status}
          </Button>
        ))}
      </div>

      {appointments.length === 0 ? (
        <Card className="p-8 text-center">
          <p className="text-gray-600">No appointments found</p>
        </Card>
      ) : (
        <div className="grid grid-cols-1 gap-4">
          {appointments.map((apt: any) => (
            <Card key={apt.appointmentId} className="p-6">
              <div className="grid grid-cols-1 md:grid-cols-5 gap-4">
                <div>
                  <p className="text-sm text-gray-600">Patient</p>
                  <p className="font-semibold">{apt.patientName}</p>
                </div>
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
                  <p className="text-sm text-gray-600">Status</p>
                  <p className={`font-semibold ${apt.status === "CANCELLED" ? "text-red-600" : "text-green-600"}`}>
                    {apt.status}
                  </p>
                </div>
                <div className="flex gap-2">
                  {apt.status !== "CANCELLED" && (
                    <Button
                      onClick={() => handleCancel(apt.appointmentId)}
                      className="bg-red-600 hover:bg-red-700 text-sm px-3 py-1"
                    >
                      Cancel
                    </Button>
                  )}
                </div>
              </div>
            </Card>
          ))}
        </div>
      )}
    </div>
  )
}
