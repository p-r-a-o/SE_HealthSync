"use client"

import { useState, useEffect } from "react"
import { useAuth } from "@/lib/auth-context"
import { Card } from "@/components/ui/card"
import api from "@/lib/api"

export default function MedicalHistoryPage() {
  const { user } = useAuth()
  const [history, setHistory] = useState<any>(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState("")

  useEffect(() => {
    const fetchHistory = async () => {
      try {
        const response = await api.get(`/patients/${user?.userId}/medical-history`)
        setHistory(response.data)
      } catch (err) {
        setError("Failed to load medical history")
      } finally {
        setLoading(false)
      }
    }

    if (user?.userId) {
      fetchHistory()
    }
  }, [user?.userId])

  if (loading) return <div className="container mx-auto p-6">Loading...</div>

  return (
    <div className="container mx-auto p-6">
      <h1 className="text-3xl font-bold mb-6">Medical History</h1>

      {error && <div className="mb-4 p-3 bg-red-100 text-red-700 rounded">{error}</div>}

      {history && (
        <div className="space-y-6">
          <Card className="p-6">
            <h2 className="text-2xl font-bold mb-4">Past Appointments</h2>
            {history.pastAppointments && history.pastAppointments.length > 0 ? (
              <div className="space-y-3">
                {history.pastAppointments.map((apt: any) => (
                  <div key={apt.appointmentId} className="border-l-4 border-blue-600 pl-4 py-2">
                    <p className="font-semibold">
                      Dr. {apt.doctorName} - {apt.appointmentDate}
                    </p>
                    {apt.diagnosis && <p className="text-sm text-gray-700">Diagnosis: {apt.diagnosis}</p>}
                    {apt.treatmentPlan && <p className="text-sm text-gray-700">Treatment: {apt.treatmentPlan}</p>}
                  </div>
                ))}
              </div>
            ) : (
              <p className="text-gray-600">No past appointments</p>
            )}
          </Card>

          <Card className="p-6">
            <h2 className="text-2xl font-bold mb-4">Prescriptions</h2>
            {history.prescriptions && history.prescriptions.length > 0 ? (
              <div className="space-y-3">
                {history.prescriptions.map((rx: any) => (
                  <div key={rx.prescriptionId} className="border-l-4 border-green-600 pl-4 py-2">
                    <p className="font-semibold">
                      Dr. {rx.doctorName} - {rx.dateIssued}
                    </p>
                    <p className="text-sm text-gray-700">Medications: {rx.prescriptionItems?.length || 0}</p>
                    <p className="text-sm text-gray-700">Status: {rx.status}</p>
                  </div>
                ))}
              </div>
            ) : (
              <p className="text-gray-600">No prescriptions</p>
            )}
          </Card>
        </div>
      )}
    </div>
  )
}
