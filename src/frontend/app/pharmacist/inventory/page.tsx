"use client"

import { useState, useEffect } from "react"
import { Card } from "@/components/ui/card"
import api from "@/lib/api"

export default function PharmacistInventoryPage() {
  const [medications, setMedications] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState("")

  useEffect(() => {
    const fetchMedications = async () => {
      try {
        const response = await api.get("/medications")
        setMedications(response.data)
      } catch (err) {
        setError("Failed to load medications")
      } finally {
        setLoading(false)
      }
    }

    fetchMedications()
  }, [])

  if (loading) return <div className="container mx-auto p-6">Loading...</div>

  return (
    <div className="container mx-auto p-6">
      <h1 className="text-3xl font-bold mb-6">Medication Inventory</h1>

      {error && <div className="mb-4 p-3 bg-red-100 text-red-700 rounded">{error}</div>}

      {medications.length === 0 ? (
        <Card className="p-8 text-center">
          <p className="text-gray-600">No medications found</p>
        </Card>
      ) : (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          {medications.map((med: any) => (
            <Card key={med.medicationId} className="p-6">
              <h3 className="text-lg font-semibold mb-2">{med.name}</h3>
              <p className="text-sm text-gray-600 mb-4">{med.genericName}</p>

              <div className="space-y-2 text-sm mb-4">
                <div>
                  <p className="text-gray-600">Manufacturer</p>
                  <p>{med.manufacturer}</p>
                </div>
                <div>
                  <p className="text-gray-600">Unit Price</p>
                  <p className="font-semibold">₹{med.unitPrice}</p>
                </div>
                <div>
                  <p className="text-gray-600">Description</p>
                  <p>{med.description}</p>
                </div>
              </div>
            </Card>
          ))}
        </div>
      )}
    </div>
  )
}
