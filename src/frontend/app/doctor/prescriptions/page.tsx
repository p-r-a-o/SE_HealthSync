"use client"

import type React from "react"

import { useState, useEffect } from "react"
import { useAuth } from "@/lib/auth-context"
import { Card } from "@/components/ui/card"
import { Button } from "@/components/ui/button"
import api from "@/lib/api"

export default function WritePrescriptionPage() {
  const { user } = useAuth()
  const [patients, setPatients] = useState([])
  const [medications, setMedications] = useState([])
  const [selectedPatient, setSelectedPatient] = useState("")
  const [prescriptionItems, setPrescriptionItems] = useState<any[]>([])
  const [currentItem, setCurrentItem] = useState({ medicationId: "", quantity: 1 })
  const [formData, setFormData] = useState({
    instructions: "",
  })
  const [error, setError] = useState("")
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    const fetchData = async () => {
      try {
        const [patientsRes, medicationsRes] = await Promise.all([api.get("/patients"), api.get("/medications")])
        setPatients(patientsRes.data)
        setMedications(medicationsRes.data)
      } catch (err) {
        setError("Failed to load data")
      } finally {
        setLoading(false)
      }
    }

    fetchData()
  }, [])

  const handleAddMedication = () => {
    if (!currentItem.medicationId) {
      setError("Please select a medication")
      return
    }

    const medication = medications.find((m) => m.medicationId === currentItem.medicationId)
    if (!medication) return

    setPrescriptionItems([
      ...prescriptionItems,
      {
        medicationId: currentItem.medicationId,
        medicationName: medication.name,
        quantity: currentItem.quantity,
      },
    ])
    setCurrentItem({ medicationId: "", quantity: 1 })
  }

  const handleRemoveMedication = (index: number) => {
    setPrescriptionItems(prescriptionItems.filter((_, i) => i !== index))
  }

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    setError("")

    if (!selectedPatient || prescriptionItems.length === 0) {
      setError("Please select patient and add medications")
      return
    }

    try {
      const prescriptionData = {
        prescription: {
          doctorId: user?.userId,
          patientId: selectedPatient,
          instructions: formData.instructions,
          status: "PENDING",
        },
        items: prescriptionItems.map((item) => ({
          medicationId: item.medicationId,
          quantity: item.quantity,
        })),
      }

      await api.post("/prescriptions/with-items", prescriptionData)

      // Reset form
      setSelectedPatient("")
      setPrescriptionItems([])
      setFormData({ instructions: "" })
      alert("Prescription created successfully")
    } catch (err: any) {
      setError(err.response?.data?.message || "Failed to create prescription")
    }
  }

  if (loading) return <div className="container mx-auto p-6">Loading...</div>

  return (
    <div className="container mx-auto p-6">
      <h1 className="text-3xl font-bold mb-6">Write Prescription</h1>

      {error && <div className="mb-4 p-3 bg-red-100 text-red-700 rounded">{error}</div>}

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        {/* Form */}
        <Card className="lg:col-span-2 p-6">
          <form onSubmit={handleSubmit} className="space-y-6">
            <div>
              <label className="block text-sm font-medium mb-2">Select Patient *</label>
              <select
                required
                value={selectedPatient}
                onChange={(e) => setSelectedPatient(e.target.value)}
                className="w-full px-3 py-2 border border-gray-300 rounded"
              >
                <option value="">Choose a patient...</option>
                {patients.map((p: any) => (
                  <option key={p.personId} value={p.personId}>
                    {p.firstName} {p.lastName} - {p.email}
                  </option>
                ))}
              </select>
            </div>

            <div>
              <label className="block text-sm font-medium mb-2">Instructions</label>
              <textarea
                value={formData.instructions}
                onChange={(e) => setFormData({ ...formData, instructions: e.target.value })}
                className="w-full px-3 py-2 border border-gray-300 rounded"
                rows={3}
                placeholder="e.g., Take one tablet twice daily before meals..."
              />
            </div>

            <Button type="submit" className="w-full bg-green-600 hover:bg-green-700">
              Create Prescription
            </Button>
          </form>
        </Card>

        {/* Medications Sidebar */}
        <Card className="p-6">
          <h2 className="text-xl font-bold mb-4">Add Medications</h2>

          <div className="space-y-3 mb-4">
            <div>
              <label className="block text-sm font-medium mb-1">Medication</label>
              <select
                value={currentItem.medicationId}
                onChange={(e) => setCurrentItem({ ...currentItem, medicationId: e.target.value })}
                className="w-full px-3 py-2 border border-gray-300 rounded text-sm"
              >
                <option value="">Select...</option>
                {medications.map((m: any) => (
                  <option key={m.medicationId} value={m.medicationId}>
                    {m.name}
                  </option>
                ))}
              </select>
            </div>

            <div>
              <label className="block text-sm font-medium mb-1">Quantity</label>
              <input
                type="number"
                min="1"
                value={currentItem.quantity}
                onChange={(e) => setCurrentItem({ ...currentItem, quantity: Number.parseInt(e.target.value) })}
                className="w-full px-3 py-2 border border-gray-300 rounded text-sm"
              />
            </div>

            <Button type="button" onClick={handleAddMedication} className="w-full bg-blue-600 hover:bg-blue-700">
              Add to Prescription
            </Button>
          </div>

          <div className="border-t pt-4">
            <p className="font-semibold mb-3">Selected Medications ({prescriptionItems.length})</p>
            <div className="space-y-2">
              {prescriptionItems.map((item, idx) => (
                <div key={idx} className="flex justify-between items-center p-2 bg-gray-50 rounded text-sm">
                  <span>
                    {item.medicationName} (x{item.quantity})
                  </span>
                  <button
                    type="button"
                    onClick={() => handleRemoveMedication(idx)}
                    className="text-red-600 hover:text-red-700"
                  >
                    ✕
                  </button>
                </div>
              ))}
            </div>
          </div>
        </Card>
      </div>
    </div>
  )
}
