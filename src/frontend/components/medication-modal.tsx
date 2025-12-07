"use client"

import type React from "react"

import { useState, useEffect } from "react"
import { Button } from "@/components/ui/button"
import { Card } from "@/components/ui/card"
import { X } from "lucide-react"
import api from "@/lib/api"
interface MedicationFormData {
  name: string
  genericName: string
  manufacturer: string
  description: string
  unitPrice: string
  pharmacyId?: string
  pharmacyLocation?: string
}

interface MedicationModalProps {
  isOpen: boolean
  onClose: () => void
  onSave: (data: any) => Promise<void>
  initialData?: {
    medicationId: string
    name: string
    genericName: string
    manufacturer: string
    description: string
    unitPrice: number
    pharmacyId?: string
    pharmacyLocation?: string
  }
}

export function MedicationModal({ isOpen, onClose, onSave, initialData }: MedicationModalProps) {
  const [formData, setFormData] = useState<MedicationFormData>({
    name: "",
    genericName: "",
    manufacturer: "",
    description: "",
    unitPrice: "",
    pharmacyId: "",
    pharmacyLocation: "",
  })
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState("")

  useEffect(() => {
    if (initialData) {
      setFormData({
        name: initialData.name,
        genericName: initialData.genericName,
        manufacturer: initialData.manufacturer,
        description: initialData.description,
        unitPrice: initialData.unitPrice.toString(),
        pharmacyId: initialData.pharmacyId || "",
        pharmacyLocation: initialData.pharmacyLocation || "",
      })
    } else {
      setFormData({
        name: "",
        genericName: "",
        manufacturer: "",
        description: "",
        unitPrice: "",
        pharmacyId: "",
        pharmacyLocation: "",
      })
    }
    setError("")
  }, [initialData, isOpen])

  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) => {
    const { name, value } = e.target
    setFormData((prev) => ({ ...prev, [name]: value }))
  }

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    setError("")

    // Validation
    if (!formData.name.trim()) {
      setError("Medication name is required")
      return
    }
    if (!formData.genericName.trim()) {
      setError("Generic name is required")
      return
    }
    if (!formData.manufacturer.trim()) {
      setError("Manufacturer is required")
      return
    }
    if (!formData.unitPrice || Number.parseFloat(formData.unitPrice) <= 0) {
      setError("Unit price must be a positive number")
      return
    }

    try {
      setLoading(true)
      await onSave({
        name: formData.name.trim(),
        genericName: formData.genericName.trim(),
        manufacturer: formData.manufacturer.trim(),
        description: formData.description.trim(),
        unitPrice: Number.parseFloat(formData.unitPrice),
        pharmacyId: formData.pharmacyId?.trim() || "",
        pharmacyLocation: formData.pharmacyLocation?.trim() || "",
      })
    } catch (err: any) {
      setError(err.message || "Failed to save medication")
    } finally {
      setLoading(false)
    }
  }
  // Add state for pharmacies
const [pharmacies, setPharmacies] = useState<{ pharmacyId: string; location: string }[]>([])

// Load pharmacies from API
useEffect(() => {
  async function fetchPharmacies() {
    try {
      const res = await api.get("/pharmacies")// update URL as needed
      const data = res.data;
      setPharmacies(data)
    } catch (err) {
      console.error("Failed to load pharmacies", err)
    }
  }
  fetchPharmacies()
}, [])

  if (!isOpen) return null

  return (
    <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center p-4 z-50">
      <Card className="w-full max-w-2xl max-h-[90vh] overflow-y-auto">
        <div className="p-6">
          {/* Header */}
          <div className="flex justify-between items-center mb-6">
            <h2 className="text-2xl font-bold">{initialData ? "Edit Medication" : "Add New Medication"}</h2>
            <button onClick={onClose} disabled={loading} className="p-1 hover:bg-gray-100 rounded transition-colors">
              <X size={24} />
            </button>
          </div>

          {/* Error Alert */}
          {error && <div className="mb-4 p-3 bg-red-100 text-red-700 rounded text-sm">{error}</div>}

          {/* Form */}
          <form onSubmit={handleSubmit} className="space-y-4">
            {/* Name */}
            <div>
              <label className="block text-sm font-semibold mb-1">Medication Name *</label>
              <input
                type="text"
                name="name"
                value={formData.name}
                onChange={handleChange}
                disabled={loading}
                placeholder="e.g., Aspirin"
                className="w-full px-3 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 disabled:bg-gray-100"
              />
            </div>

            {/* Generic Name */}
            <div>
              <label className="block text-sm font-semibold mb-1">Generic Name *</label>
              <input
                type="text"
                name="genericName"
                value={formData.genericName}
                onChange={handleChange}
                disabled={loading}
                placeholder="e.g., Acetylsalicylic acid"
                className="w-full px-3 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 disabled:bg-gray-100"
              />
            </div>

            {/* Manufacturer */}
            <div>
              <label className="block text-sm font-semibold mb-1">Manufacturer *</label>
              <input
                type="text"
                name="manufacturer"
                value={formData.manufacturer}
                onChange={handleChange}
                disabled={loading}
                placeholder="e.g., Bayer"
                className="w-full px-3 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 disabled:bg-gray-100"
              />
            </div>

            {/* Description */}
            <div>
              <label className="block text-sm font-semibold mb-1">Description</label>
              <textarea
                name="description"
                value={formData.description}
                onChange={handleChange}
                disabled={loading}
                rows={3}
                placeholder="Medication description and usage"
                className="w-full px-3 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 disabled:bg-gray-100"
              />
            </div>

            {/* Unit Price */}
            <div>
              <label className="block text-sm font-semibold mb-1">Unit Price (₹) *</label>
              <input
                type="number"
                name="unitPrice"
                value={formData.unitPrice}
                onChange={handleChange}
                disabled={loading}
                step="0.01"
                min="0"
                placeholder="0.00"
                className="w-full px-3 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 disabled:bg-gray-100"
              />
            </div>

            {/* Pharmacy Select */}
            <div>
            <label className="block text-sm font-semibold mb-1">Pharmacy</label>

            <select
                name="pharmacyId"
                value={formData.pharmacyId}
                onChange={(e) => setFormData(prev => ({ ...prev, pharmacyId: e.target.value }))}
                disabled={loading}
                className="w-full px-3 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 disabled:bg-gray-100"
            >
                <option value="">Select a pharmacy</option>

                {pharmacies.map((p) => (
                <option key={p.pharmacyId} value={p.pharmacyId}>
                    {p.location}
                </option>
                ))}
            </select>
            </div>


            {/* Action Buttons */}
            <div className="flex gap-3 pt-4 border-t">
              <Button
                type="button"
                onClick={onClose}
                disabled={loading}
                className="flex-1 bg-gray-400 hover:bg-gray-500 text-white"
              >
                Cancel
              </Button>
              <Button type="submit" disabled={loading} className="flex-1 bg-blue-600 hover:bg-blue-700 text-white">
                {loading ? "Saving..." : initialData ? "Update Medication" : "Add Medication"}
              </Button>
            </div>
          </form>
        </div>
      </Card>
    </div>
  )
}
