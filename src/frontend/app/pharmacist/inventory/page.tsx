"use client"

import { useState, useEffect } from "react"
import { Button } from "@/components/ui/button"
import { Card } from "@/components/ui/card"
import api from "@/lib/api"
import { MedicationModal } from "@/components/medication-modal"
import { Trash2, Edit2, Plus } from "lucide-react"

interface Medication {
  medicationId: string
  name: string
  genericName: string
  manufacturer: string
  description: string
  unitPrice: number
  pharmacyId: string
  pharmacyLocation: string
  quantity: number
}

export default function PharmacistInventoryPage() {
  const [medications, setMedications] = useState<Medication[]>([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState("")
  const [success, setSuccess] = useState("")
  const [isModalOpen, setIsModalOpen] = useState(false)
  const [editingMedication, setEditingMedication] = useState<Medication | null>(null)
  const [searchTerm, setSearchTerm] = useState("")

  useEffect(() => {
    fetchMedications()
  }, [])

  const fetchMedications = async () => {
    try {
      setLoading(true)
      const response = await api.get("/medications")
      setMedications(response.data)
      setError("")
    } catch (err) {
      setError("Failed to load medications")
      console.error(err)
    } finally {
      setLoading(false)
    }
  }

  const handleSaveMedication = async (formData: Omit<Medication, "medicationId">) => {
    try {
      if (editingMedication) {
        // Update existing medication
        await api.put(`/medications/${editingMedication.medicationId}`, formData)
        setSuccess("Medication updated successfully")
      } else {
        // Add new medication
        await api.post("/medications", formData)
        setSuccess("Medication added successfully")
      }
      setIsModalOpen(false)
      setEditingMedication(null)
      fetchMedications()
    } catch (err: any) {
      setError(err.response?.data?.message || "Failed to save medication")
      console.error(err)
    }
  }

  const handleDeleteMedication = async (medicationId: string) => {
    if (!window.confirm("Are you sure you want to delete this medication?")) return

    try {
      await api.delete(`/medications/${medicationId}`)
      setSuccess("Medication deleted successfully")
      fetchMedications()
    } catch (err: any) {
      setError(err.response?.data?.message || "Failed to delete medication")
      console.error(err)
    }
  }

  const filteredMedications = medications.filter(
    (med) =>
      med.name.toLowerCase().includes(searchTerm.toLowerCase()) ||
      med.genericName.toLowerCase().includes(searchTerm.toLowerCase()) ||
      med.manufacturer.toLowerCase().includes(searchTerm.toLowerCase()),
  )

  if (loading) return <div className="container mx-auto p-6">Loading...</div>

  return (
    <div className="container mx-auto p-6">
      {/* Header Section */}
      <div className="flex justify-between items-center mb-8">
        <div>
          <h1 className="text-4xl font-bold mb-2">Medication Inventory</h1>
          <p className="text-gray-600">Manage medications in the pharmacy</p>
        </div>
        <Button
          onClick={() => {
            setEditingMedication(null)
            setIsModalOpen(true)
          }}
          className="bg-blue-600 hover:bg-blue-700 flex gap-2"
        >
          <Plus size={20} />
          Add Medication
        </Button>
      </div>

      {/* Alerts */}
      {error && (
        <div className="mb-4 p-4 bg-red-100 text-red-700 rounded-lg flex justify-between items-center">
          <span>{error}</span>
          <button onClick={() => setError("")} className="text-xl">
            ×
          </button>
        </div>
      )}
      {success && (
        <div className="mb-4 p-4 bg-green-100 text-green-700 rounded-lg flex justify-between items-center">
          <span>{success}</span>
          <button onClick={() => setSuccess("")} className="text-xl">
            ×
          </button>
        </div>
      )}

      {/* Search Bar */}
      <div className="mb-6">
        <input
          type="text"
          placeholder="Search by name, generic name, or manufacturer..."
          value={searchTerm}
          onChange={(e) => setSearchTerm(e.target.value)}
          className="w-full px-4 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
        />
      </div>

      {/* Medications Grid */}
      {filteredMedications.length === 0 ? (
        <Card className="p-12 text-center">
          <p className="text-gray-600 text-lg">
            {medications.length === 0 ? "No medications found" : "No medications match your search"}
          </p>
        </Card>
      ) : (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          {filteredMedications.map((med) => (
            <Card key={med.medicationId} className="p-6 hover:shadow-lg transition-shadow">
              <div className="mb-4">
                <h3 className="text-xl font-bold text-blue-600 mb-1">{med.name}</h3>
                <p className="text-sm text-gray-600 italic">{med.genericName}</p>
              </div>

              <div className="space-y-3 mb-4 text-sm">
                <div>
                  <p className="text-gray-600">Manufacturer</p>
                  <p className="font-semibold">{med.manufacturer}</p>
                </div>
                <div>
                  <p className="text-gray-600">Unit Price</p>
                  <p className="font-semibold text-green-600">
                    ₹{Number.parseFloat(med.unitPrice.toString()).toFixed(2)}
                  </p>
                </div>
                <div>
                  <p className="text-gray-600">Description</p>
                  <p className="font-semibold">{med.description}</p>
                </div>
                {med.pharmacyLocation && (
                  <div>
                    <p className="text-gray-600">Location</p>
                    <p className="font-semibold">{med.pharmacyLocation}</p>
                  </div>
                )}
                <div>
                  <p className="text-gray-600">Quantity</p>
                  <p className="font-semibold text-green-600">
                    {med.quantity}
                  </p>
                </div>
              </div>

              {/* Action Buttons */}
              <div className="flex gap-2 pt-4 border-t">
                <Button
                  onClick={() => {
                    setEditingMedication(med)
                    setIsModalOpen(true)
                  }}
                  className="flex-1 bg-blue-600 hover:bg-blue-700 flex gap-2 items-center justify-center"
                >
                  <Edit2 size={16} />
                  Edit
                </Button>
                <Button
                  onClick={() => handleDeleteMedication(med.medicationId)}
                  className="flex-1 bg-red-600 hover:bg-red-700 flex gap-2 items-center justify-center"
                >
                  <Trash2 size={16} />
                  Delete
                </Button>
              </div>
            </Card>
          ))}
        </div>
      )}

      {/* Medication Modal */}
      <MedicationModal
        isOpen={isModalOpen}
        onClose={() => {
          setIsModalOpen(false)
          setEditingMedication(null)
        }}
        onSave={handleSaveMedication}
        initialData={editingMedication || undefined}
      />
    </div>
  )
}
