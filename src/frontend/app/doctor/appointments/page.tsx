"use client"

import { useState, useEffect } from "react"
import { useAuth } from "@/lib/auth-context"
import { Card } from "@/components/ui/card"
import { Button } from "@/components/ui/button"
import api from "@/lib/api"

export default function DoctorAppointmentsPage() {
  const { user } = useAuth()
  const [appointments, setAppointments] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState("")
  const [selectedAppointment, setSelectedAppointment] = useState<any>(null)
  const [consultationData, setConsultationData] = useState({
    diagnosis: "",
    treatmentPlan: "",
    notes: "",
  })

  useEffect(() => {
    const fetchAppointments = async () => {
      try {
        const response = await api.get(`/appointments/doctor/${user?.userId}`)
        setAppointments(response.data)
      } catch (err) {
        setError("Failed to load appointments")
      } finally {
        setLoading(false)
      }
    }

    if (user?.userId) {
      fetchAppointments()
    }
  }, [user?.userId])

  const handleConsultationSubmit = async (appointmentId: string) => {
    try {
      await api.put(`/appointments/${appointmentId}/consultation`, null, {
        params: {
          diagnosis: consultationData.diagnosis,
          treatmentPlan: consultationData.treatmentPlan,
          notes: consultationData.notes,
        },
      })

      // Refresh appointments
      const response = await api.get(`/appointments/doctor/${user?.userId}`)
      setAppointments(response.data)
      setSelectedAppointment(null)
      setConsultationData({ diagnosis: "", treatmentPlan: "", notes: "" })
    } catch (err: any) {
      setError(err.response?.data?.message || "Failed to save consultation")
    }
  }

  if (loading) return <div className="container mx-auto p-6">Loading...</div>

  return (
    <div className="container mx-auto p-6">
      <h1 className="text-3xl font-bold mb-6">My Appointments</h1>

      {error && <div className="mb-4 p-3 bg-red-100 text-red-700 rounded">{error}</div>}

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        {/* Appointments List */}
        <div className="lg:col-span-2">
          {appointments.length === 0 ? (
            <Card className="p-8 text-center">
              <p className="text-gray-600">No appointments found</p>
            </Card>
          ) : (
            <div className="space-y-4">
              {appointments.map((apt: any) => (
                <Card
                  key={apt.appointmentId}
                  className={`p-6 cursor-pointer transition-all ${
                    selectedAppointment?.appointmentId === apt.appointmentId
                      ? "ring-2 ring-blue-500"
                      : "hover:shadow-md"
                  }`}
                  onClick={() => setSelectedAppointment(apt)}
                >
                  <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
                    <div>
                      <p className="text-sm text-gray-600">Patient</p>
                      <p className="font-semibold">{apt.patientName}</p>
                    </div>
                    <div>
                      <p className="text-sm text-gray-600">Date & Time</p>
                      <p className="font-semibold">
                        {apt.appointmentDate} {apt.startTime}
                      </p>
                    </div>
                    <div>
                      <p className="text-sm text-gray-600">Status</p>
                      <p className={`font-semibold ${apt.status === "COMPLETED" ? "text-green-600" : "text-blue-600"}`}>
                        {apt.status}
                      </p>
                    </div>
                  </div>
                </Card>
              ))}
            </div>
          )}
        </div>

        {/* Consultation Form */}
        {selectedAppointment && (
          <Card className="p-6 h-fit">
            <h2 className="text-xl font-bold mb-4">Record Consultation</h2>
            <div className="space-y-3 mb-4 pb-4 border-b">
              <div>
                <p className="text-sm text-gray-600">Patient</p>
                <p className="font-semibold">{selectedAppointment.patientName}</p>
              </div>
              <div>
                <p className="text-sm text-gray-600">Date & Time</p>
                <p className="font-semibold">
                  {selectedAppointment.appointmentDate} {selectedAppointment.startTime}
                </p>
              </div>
            </div>

            <div className="space-y-4">
              <div>
                <label className="block text-sm font-medium mb-1">Diagnosis *</label>
                <textarea
                  value={consultationData.diagnosis}
                  onChange={(e) => setConsultationData((prev) => ({ ...prev, diagnosis: e.target.value }))}
                  className="w-full px-3 py-2 border border-gray-300 rounded"
                  rows={2}
                  placeholder="Enter diagnosis..."
                />
              </div>

              <div>
                <label className="block text-sm font-medium mb-1">Treatment Plan *</label>
                <textarea
                  value={consultationData.treatmentPlan}
                  onChange={(e) => setConsultationData((prev) => ({ ...prev, treatmentPlan: e.target.value }))}
                  className="w-full px-3 py-2 border border-gray-300 rounded"
                  rows={2}
                  placeholder="Enter treatment plan..."
                />
              </div>

              <div>
                <label className="block text-sm font-medium mb-1">Notes</label>
                <textarea
                  value={consultationData.notes}
                  onChange={(e) => setConsultationData((prev) => ({ ...prev, notes: e.target.value }))}
                  className="w-full px-3 py-2 border border-gray-300 rounded"
                  rows={2}
                  placeholder="Additional notes..."
                />
              </div>

              <Button
                onClick={() => handleConsultationSubmit(selectedAppointment.appointmentId)}
                disabled={!consultationData.diagnosis || !consultationData.treatmentPlan}
                className="w-full bg-green-600 hover:bg-green-700"
              >
                Save Consultation
              </Button>
            </div>
          </Card>
        )}
      </div>
    </div>
  )
}
