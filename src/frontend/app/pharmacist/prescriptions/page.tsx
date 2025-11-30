"use client"

import { useState, useEffect } from "react"
import { Card } from "@/components/ui/card"
import api from "@/lib/api"

export default function PharmacistPrescriptionsPage() {
  const [prescriptions, setPrescriptions] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState("")
  const [filter, setFilter] = useState("PENDING")

  useEffect(() => {
    const fetchPrescriptions = async () => {
      try {
        const response = await api.get(`/prescriptions/status/${filter}`)
        setPrescriptions(response.data)
      } catch (err) {
        setError("Failed to load prescriptions")
      } finally {
        setLoading(false)
      }
    }

    fetchPrescriptions()
  }, [filter])

  if (loading) return <div className="container mx-auto p-6">Loading...</div>

  return (
    <div className="container mx-auto p-6">
      <h1 className="text-3xl font-bold mb-6">Prescriptions</h1>

      {error && <div className="mb-4 p-3 bg-red-100 text-red-700 rounded">{error}</div>}

      <div className="mb-6 flex gap-2">
        {["PENDING", "DISPENSED"].map((status) => (
          <button
            key={status}
            onClick={() => setFilter(status)}
            className={`px-4 py-2 rounded ${filter === status ? "bg-blue-600 text-white" : "bg-gray-400 text-white"}`}
          >
            {status}
          </button>
        ))}
      </div>

      {prescriptions.length === 0 ? (
        <Card className="p-8 text-center">
          <p className="text-gray-600">No prescriptions found</p>
        </Card>
      ) : (
        <div className="grid grid-cols-1 gap-6">
          {prescriptions.map((rx: any) => (
            <Card key={rx.prescriptionId} className="p-6">
              <div className="grid grid-cols-1 md:grid-cols-4 gap-4 mb-4">
                <div>
                  <p className="text-sm text-gray-600">Patient</p>
                  <p className="font-semibold">{rx.patientName}</p>
                </div>
                <div>
                  <p className="text-sm text-gray-600">Doctor</p>
                  <p className="font-semibold">{rx.doctorName}</p>
                </div>
                <div>
                  <p className="text-sm text-gray-600">Date Issued</p>
                  <p className="font-semibold">{rx.dateIssued}</p>
                </div>
                <div>
                  <p className="text-sm text-gray-600">Status</p>
                  <p className={`font-semibold ${rx.status === "DISPENSED" ? "text-green-600" : "text-blue-600"}`}>
                    {rx.status}
                  </p>
                </div>
              </div>

              {rx.prescriptionItems && rx.prescriptionItems.length > 0 && (
                <div className="pt-4 border-t">
                  <p className="font-semibold mb-3">Medications:</p>
                  <div className="space-y-2">
                    {rx.prescriptionItems.map((item: any) => (
                      <div key={item.prescriptionItemId} className="p-2 bg-gray-50 rounded text-sm">
                        <p className="font-semibold">{item.medicationName}</p>
                        <p>Quantity: {item.quantity}</p>
                      </div>
                    ))}
                  </div>
                </div>
              )}
            </Card>
          ))}
        </div>
      )}
    </div>
  )
}
