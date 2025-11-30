"use client"

import type React from "react"

import { useState, useEffect } from "react"
import { useAuth } from "@/lib/auth-context"
import { Card } from "@/components/ui/card"
import { Button } from "@/components/ui/button"
import api from "@/lib/api"

const DAYS_OF_WEEK = ["MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY", "SATURDAY", "SUNDAY"]

export default function DoctorAvailabilityPage() {
  const { user } = useAuth()
  const [availability, setAvailability] = useState([])
  const [loading, setLoading] = useState(true)
  const [formData, setFormData] = useState({
    dayOfWeek: "",
    startTime: "",
    endTime: "",
  })
  const [error, setError] = useState("")

  useEffect(() => {
    const fetchAvailability = async () => {
      try {
        const response = await api.get(`/doctors/${user?.userId}/availability`)
        setAvailability(response.data)
      } catch (err) {
        console.error("Error fetching availability:", err)
      } finally {
        setLoading(false)
      }
    }

    if (user?.userId) {
      fetchAvailability()
    }
  }, [user?.userId])

  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>) => {
    const { name, value } = e.target
    setFormData((prev) => ({ ...prev, [name]: value }))
  }

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    setError("")

    try {
      const availabilityData = {
        dayOfWeek: formData.dayOfWeek,
        startTime: formData.startTime,
        endTime: formData.endTime,
      }

      await api.post(`/doctors/${user?.userId}/availability`, availabilityData)
      setFormData({ dayOfWeek: "", startTime: "", endTime: "" })

      // Refresh availability list
      const response = await api.get(`/doctors/${user?.userId}/availability`)
      setAvailability(response.data)
    } catch (err: any) {
      setError(err.response?.data?.message || "Failed to add availability")
    }
  }

  const handleDelete = async (slotId: string) => {
    try {
      await api.delete(`/doctors/availability/${slotId}`)
      setAvailability(availability.filter((slot) => slot.slotId !== slotId))
    } catch (err) {
      setError("Failed to delete availability")
    }
  }

  if (loading) return <div className="container mx-auto p-6">Loading...</div>

  return (
    <div className="container mx-auto p-6">
      <h1 className="text-3xl font-bold mb-6">Manage Availability</h1>

      {error && <div className="mb-4 p-3 bg-red-100 text-red-700 rounded">{error}</div>}

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        {/* Add New Availability */}
        <Card className="p-6">
          <h2 className="text-xl font-bold mb-4">Add Availability</h2>
          <form onSubmit={handleSubmit} className="space-y-4">
            <div>
              <label className="block text-sm font-medium mb-1">Day of Week *</label>
              <select
                required
                name="dayOfWeek"
                value={formData.dayOfWeek}
                onChange={handleChange}
                className="w-full px-3 py-2 border border-gray-300 rounded"
              >
                <option value="">Select...</option>
                {DAYS_OF_WEEK.map((day) => (
                  <option key={day} value={day}>
                    {day}
                  </option>
                ))}
              </select>
            </div>

            <div>
              <label className="block text-sm font-medium mb-1">Start Time *</label>
              <input
                type="time"
                required
                name="startTime"
                value={formData.startTime}
                onChange={handleChange}
                className="w-full px-3 py-2 border border-gray-300 rounded"
              />
            </div>

            <div>
              <label className="block text-sm font-medium mb-1">End Time *</label>
              <input
                type="time"
                required
                name="endTime"
                value={formData.endTime}
                onChange={handleChange}
                className="w-full px-3 py-2 border border-gray-300 rounded"
              />
            </div>

            <Button type="submit" className="w-full bg-blue-600 hover:bg-blue-700">
              Add Slot
            </Button>
          </form>
        </Card>

        {/* Current Availability */}
        <div className="lg:col-span-2">
          <Card className="p-6">
            <h2 className="text-xl font-bold mb-4">Current Availability</h2>
            {availability.length === 0 ? (
              <p className="text-gray-600">No availability slots set</p>
            ) : (
              <div className="space-y-3">
                {availability.map((slot: any) => (
                  <div key={slot.slotId} className="flex justify-between items-center p-3 bg-gray-50 rounded">
                    <div>
                      <p className="font-semibold">{slot.dayOfWeek}</p>
                      <p className="text-sm text-gray-600">
                        {slot.startTime} - {slot.endTime}
                      </p>
                    </div>
                    <Button
                      onClick={() => handleDelete(slot.slotId)}
                      className="bg-red-600 hover:bg-red-700 px-3 py-1 text-sm"
                    >
                      Delete
                    </Button>
                  </div>
                ))}
              </div>
            )}
          </Card>
        </div>
      </div>
    </div>
  )
}
