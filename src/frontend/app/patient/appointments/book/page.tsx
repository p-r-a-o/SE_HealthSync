"use client"

import type React from "react"

import { useState, useEffect } from "react"
import { useAuth } from "@/lib/auth-context"
import { Card } from "@/components/ui/card"
import { Button } from "@/components/ui/button"
import { useRouter } from "next/navigation"
import api from "@/lib/api"

export default function BookAppointmentPage() {
  const { user } = useAuth()
  const router = useRouter()
  const [doctors, setDoctors] = useState([])
  const [availableSlots, setAvailableSlots] = useState([])
  const [formData, setFormData] = useState({
    doctorId: "",
    appointmentDate: "",
    startTime: "",
    type: "CONSULTATION",
    notes: "",
  })
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState("")
  const [loadingDoctors, setLoadingDoctors] = useState(true)

  // Fetch doctors
  useEffect(() => {
    const fetchDoctors = async () => {
      try {
        const response = await api.get("/doctors")
        setDoctors(response.data)
      } catch (err) {
        console.error("Error fetching doctors:", err)
      } finally {
        setLoadingDoctors(false)
      }
    }

    fetchDoctors()
  }, [])

  // Fetch available slots when doctor and date are selected
  useEffect(() => {
    if (formData.doctorId && formData.appointmentDate) {
      const fetchSlots = async () => {
        try {
          const response = await api.get("/appointments/available-slots", {
            params: {
              doctorId: formData.doctorId,
              date: formData.appointmentDate,
            },
          })
          setAvailableSlots(response.data)
        } catch (err) {
          console.error("Error fetching slots:", err)
          setAvailableSlots([])
        }
      }

      fetchSlots()
    }
  }, [formData.doctorId, formData.appointmentDate])

  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement | HTMLTextAreaElement>) => {
    const { name, value } = e.target
    setFormData((prev) => ({ ...prev, [name]: value }))
  }

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    setError("")
    setLoading(true)

    try {
      const appointmentData = {
        patientId: user?.userId,
        doctorId: formData.doctorId,
        appointmentDate: formData.appointmentDate,
        startTime: formData.startTime,
        endTime: formData.startTime, // Backend calculates endTime
        type: formData.type,
        notes: formData.notes,
        status: "SCHEDULED",
      }

      await api.post("/appointments", appointmentData)
      router.push("/patient/appointments")
    } catch (err: any) {
      setError(err.response?.data?.message || "Failed to book appointment")
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="container mx-auto p-6">
      <Card className="max-w-2xl mx-auto">
        <div className="p-8">
          <h1 className="text-3xl font-bold mb-6">Book an Appointment</h1>

          {error && <div className="mb-4 p-3 bg-red-100 text-red-700 rounded border border-red-300">{error}</div>}

          <form onSubmit={handleSubmit} className="space-y-6">
            <div>
              <label className="block text-sm font-medium mb-2">Select Doctor *</label>
              <select
                required
                name="doctorId"
                value={formData.doctorId}
                onChange={handleChange}
                className="w-full px-3 py-2 border border-gray-300 rounded"
                disabled={loadingDoctors}
              >
                <option value="">{loadingDoctors ? "Loading doctors..." : "Choose a doctor..."}</option>
                {doctors.map((doctor: any) => (
                  <option key={doctor.personId} value={doctor.personId}>
                    Dr. {doctor.firstName} {doctor.lastName} - {doctor.specialization} (Fee: ₹{doctor.consultationFee})
                  </option>
                ))}
              </select>
            </div>

            <div>
              <label className="block text-sm font-medium mb-2">Appointment Date *</label>
              <input
                type="date"
                required
                name="appointmentDate"
                value={formData.appointmentDate}
                onChange={handleChange}
                min={new Date().toISOString().split("T")[0]}
                className="w-full px-3 py-2 border border-gray-300 rounded"
              />
            </div>

            {availableSlots.length > 0 && (
              <div>
                <label className="block text-sm font-medium mb-2">Select Time Slot *</label>
                <select
                  required
                  name="startTime"
                  value={formData.startTime}
                  onChange={handleChange}
                  className="w-full px-3 py-2 border border-gray-300 rounded"
                >
                  <option value="">Choose a time slot...</option>
                  {availableSlots.map((slot: any) => (
                    <option key={`${slot.startTime}`} value={slot.startTime}>
                      {slot.startTime} - {slot.endTime}
                    </option>
                  ))}
                </select>
              </div>
            )}

            <div>
              <label className="block text-sm font-medium mb-2">Appointment Type</label>
              <select
                name="type"
                value={formData.type}
                onChange={handleChange}
                className="w-full px-3 py-2 border border-gray-300 rounded"
              >
                <option value="CONSULTATION">Consultation</option>
                <option value="FOLLOW_UP">Follow-up</option>
                <option value="CHECKUP">Checkup</option>
              </select>
            </div>

            <div>
              <label className="block text-sm font-medium mb-2">Additional Notes</label>
              <textarea
                name="notes"
                value={formData.notes}
                onChange={handleChange}
                className="w-full px-3 py-2 border border-gray-300 rounded"
                rows={3}
                placeholder="Any additional information for the doctor..."
              />
            </div>

            <Button
              type="submit"
              disabled={loading || !formData.doctorId || !formData.appointmentDate || !formData.startTime}
              className="w-full bg-blue-600 hover:bg-blue-700"
            >
              {loading ? "Booking..." : "Book Appointment"}
            </Button>
          </form>
        </div>
      </Card>
    </div>
  )
}
